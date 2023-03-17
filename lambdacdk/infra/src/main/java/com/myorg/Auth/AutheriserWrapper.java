package com.myorg.Auth;

import java.util.List;

import software.amazon.awscdk.CfnOutput;
import software.amazon.awscdk.RemovalPolicy;
//import software.amazon.awscdk.services.apigateway.AuthorizationType;
//import software.amazon.awscdk.services.apigateway.Authorizer;
import software.amazon.awscdk.services.apigateway.CfnAuthorizer;
import software.amazon.awscdk.services.apigateway.CfnAuthorizerProps;
//import software.amazon.awscdk.services.apigateway.CognitoUserPoolsAuthorizer;
//import software.amazon.awscdk.services.apigateway.CognitoUserPoolsAuthorizerProps;
import software.amazon.awscdk.services.apigateway.RestApi;
import software.amazon.awscdk.services.cognito.AuthFlow;
import software.amazon.awscdk.services.cognito.CfnUserPoolGroup;
import software.amazon.awscdk.services.cognito.SignInAliases;
import software.amazon.awscdk.services.cognito.UserPool;
import software.amazon.awscdk.services.cognito.UserPoolClient;
import software.amazon.awscdk.services.cognito.UserPoolClientOptions;
import software.amazon.awscdk.services.cognito.UserPoolProps;
import software.amazon.awscdk.services.cognito.UserVerificationConfig;
import software.constructs.Construct;

public class AutheriserWrapper {
    
    private Construct scope;
    private RestApi api;
    private Policies policies;

    private UserPool userPool;
    private UserPoolClient userPoolClient;
    private IdentityPoolWrapper identityPoolWrapper;
    public CfnAuthorizer authorizer;


    public AutheriserWrapper(Construct scope, RestApi api, Policies policies) {
        this.scope = scope;
        this.api = api;
        this.policies = policies;
        this.initialize();
    }
    private void initialize() {
        this.createUserPool();
        this.addUserPoolClient();
        this.createAuthorizer();
        this.initializeIdentityPoolWrapper();
        this.createAdminsGroup(); 
    }


    private void createUserPool() {
        this.userPool = new UserPool(this.scope, "ProjectUserPool",
        UserPoolProps.builder()
        .userPoolName("ProjectUserPool")
        .selfSignUpEnabled(true)
        .userVerification(UserVerificationConfig.builder()
                        .emailBody("Hi, {####} is varification Code for your singn up. Thankyou")
                        .emailSubject("Project App varification code")
                        .build())
        .signInAliases(SignInAliases.builder()
                        .email(true)
                        .username(true)
                        .build())   
        .build());

        CfnOutput.Builder.create(this.scope, "UserPoolId")
        .value(this.userPool.getUserPoolId())
        .build();
    }


    private void addUserPoolClient() {
        this.userPoolClient = this.userPool.addClient("ProjectUserPool-client",
        UserPoolClientOptions.builder()
        .userPoolClientName("ProjectUserPool-client")
        .authFlows(AuthFlow.builder()
                    .adminUserPassword(true)
                    .custom(true)
                    .userPassword(true)
                    .userSrp(true)
                    .build())   
        .generateSecret(false)
        .build());

        CfnOutput.Builder.create(this.scope, "UserPoolClientId")
        .value(this.userPoolClient.getUserPoolClientId())
        .build();
    }
    private void createAuthorizer() {
        
        // this.authorizer = new CognitoUserPoolsAuthorizer(this.scope, "ProjectUserAuthorizer",
        //  CognitoUserPoolsAuthorizerProps.builder()
        //  .authorizerName("ProjectUserAuthorizer")
        //  .cognitoUserPools(List.of(this.userPool))
        //  .identitySource("method.request.header.Authorization")
        //  .build());
        //  this.authorizer._attachToApi(this.api);

        this.authorizer = new CfnAuthorizer(this.scope, "ProjectUserAuthorizer",
        CfnAuthorizerProps.builder()
        .name("ProjectUserAuthorizer")
        .type("COGNITO_USER_POOLS")
        .providerArns(List.of(this.userPool.getUserPoolArn()))
        .restApiId(this.api.getRestApiId())
        .identitySource("method.request.header.Authorization")
        .build());
        
        this.authorizer.applyRemovalPolicy(RemovalPolicy.DESTROY);
    }
    private void initializeIdentityPoolWrapper() {
        this.identityPoolWrapper = new IdentityPoolWrapper(this.scope,
        this.userPool, 
        this.userPoolClient, 
        this.policies);
    }
    private void createAdminsGroup() {
        CfnUserPoolGroup.Builder.create(this.scope, "admins")
        .groupName("admins")
        .userPoolId(this.userPool.getUserPoolId())
        .roleArn(this.identityPoolWrapper.getAdminRole().getRoleArn())
        .build();
    }
    

}
