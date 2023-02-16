package com.myorg.Auth;

import java.util.List;
import java.util.Map;


import software.amazon.awscdk.CfnOutput;
import software.amazon.awscdk.services.cognito.CfnIdentityPool;
import software.amazon.awscdk.services.cognito.CfnIdentityPoolProps;
import software.amazon.awscdk.services.cognito.CfnIdentityPoolRoleAttachment;
import software.amazon.awscdk.services.cognito.UserPool;
import software.amazon.awscdk.services.cognito.UserPoolClient;
import software.amazon.awscdk.services.cognito.CfnIdentityPool.CognitoIdentityProviderProperty;
import software.amazon.awscdk.services.cognito.CfnIdentityPoolRoleAttachment.RoleMappingProperty;
import software.amazon.awscdk.services.iam.FederatedPrincipal;
import software.amazon.awscdk.services.iam.Role;
import software.amazon.awscdk.services.iam.RoleProps;
import software.constructs.Construct;

public class IdentityPoolWrapper {
    private Construct scope;
    private UserPool userPool;
    private UserPoolClient userPoolClient;
    private Policies policies;

    private CfnIdentityPool identityPool;
    private Role authenticatedRole;
    private Role unAuthenticatedRole;
    private Role adminRole;

    public Role getAdminRole() {
        return adminRole;
    }

    public IdentityPoolWrapper(Construct scope, UserPool userPool, UserPoolClient userPoolClient, Policies policies) {
        this.scope = scope;
        this.userPool = userPool;
        this.userPoolClient = userPoolClient;
        this.policies = policies;
        this.initialize();
    }

    private void initialize() {
        this.initializeIdentityPool();
        this.initializeRoles();
        this.attachRoles();
    }



    private void initializeIdentityPool() {
        this.identityPool = new CfnIdentityPool(this.scope, "ProjectIdentityPool", 
        CfnIdentityPoolProps.builder()
        .allowUnauthenticatedIdentities(true)
        .cognitoIdentityProviders(
            List.of(CognitoIdentityProviderProperty.builder()
                .clientId(this.userPoolClient.getUserPoolClientId())
                .providerName(this.userPool.getUserPoolProviderName())
                .build()))
        .build());

        CfnOutput.Builder.create(this.scope, "IdentityPoolId")
        .value(this.identityPool.getRef())
        .build();
    }



    private void initializeRoles() {
        this.authenticatedRole = new Role(this.scope, "CognitoDefaultAuthenticatedRole",
         RoleProps.builder()
         .assumedBy(new FederatedPrincipal("cognito-identity.amazonaws.com",
          Map.of(
            "StringEquals",
                Map.of("cognito-identity.amazonaws.com:aud",this.identityPool.getRef()),
            "ForAnyValue:StringLike",
                Map.of("cognito-identity.amazonaws.com:amr","authenticated")
          )
          ,"sts:AssumeRoleWithWebIdentity"))
         .build());

         this.authenticatedRole.addToPolicy(this.policies.uploadProfilePhotoPolicy);
         this.authenticatedRole.addToPolicy(this.policies.uploadPostPhotoPolicy);

         this.unAuthenticatedRole = new Role(this.scope, "CognitoDefaultUnAuthenticatedRole",
         RoleProps.builder()
         .assumedBy(new FederatedPrincipal("cognito-identity.amazonaws.com",
          Map.of(
            "StringEquals",
                Map.of("cognito-identity.amazonaws.com:aud",this.identityPool.getRef()),
            "ForAnyValue:StringLike",
                Map.of("cognito-identity.amazonaws.com:amr","unauthenticated")
          )
          ,"sts:AssumeRoleWithWebIdentity"))
         .build());


         this.adminRole = new Role(this.scope, "CognitoAdminRole",
         RoleProps.builder()
         .assumedBy(new FederatedPrincipal("cognito-identity.amazonaws.com",
          Map.of(
            "StringEquals",
                Map.of("cognito-identity.amazonaws.com:aud",this.identityPool.getRef()),
            "ForAnyValue:StringLike",
                Map.of("cognito-identity.amazonaws.com:amr","authenticated")
          )
          ,"sts:AssumeRoleWithWebIdentity"))
         .build());

         this.adminRole.addToPolicy(this.policies.uploadPostPhotoPolicy);
         this.adminRole.addToPolicy(this.policies.uploadProfilePhotoPolicy);
    }



    private void attachRoles() {

        CfnIdentityPoolRoleAttachment.Builder.create(this.scope, "RolesAttachment")
        .identityPoolId(this.identityPool.getRef())
        .roles(Map.of("authenticated",this.authenticatedRole.getRoleArn(),
                        "unauthenticated",this.unAuthenticatedRole.getRoleArn()))
        .roleMappings(Map.of("adminsMapping", 
                            RoleMappingProperty.builder()
                                .type("Token")
                                .ambiguousRoleResolution("AuthenticatedRole")
                                .identityProvider(this.userPool.getUserPoolProviderName()+":"+this.userPoolClient.getUserPoolClientId())
                                .build()))
        .build();
    }
    
}
