package com.myorg.Auth;

import java.util.ArrayList;
import java.util.List;

import software.amazon.awscdk.services.iam.Effect;
import software.amazon.awscdk.services.iam.PolicyStatement;
import software.amazon.awscdk.services.iam.PolicyStatementProps;
import software.amazon.awscdk.services.s3.Bucket;

public class Policies {
    private Bucket postPhotoBucket;
    private Bucket profilePhotoBucket;
    public PolicyStatement uploadPostPhotoPolicy;
    public PolicyStatement uploadProfilePhotoPolicy;
    public PolicyStatement deletePhotoPolicy;

    

    public Policies(Bucket postPhotoBucket, Bucket profilePhotoBucket) {
        this.postPhotoBucket = postPhotoBucket;
        this.profilePhotoBucket = profilePhotoBucket;
        this.initialize();
    }
    private void initialize() {
        List<String> acttions = new ArrayList<>();
        acttions.add("s3:PutObject");
        acttions.add("s3:PutObjectAcl");
        acttions.add("s3:DeleteObject");
        acttions.add("s3:DeleteObjectACL");
       

        List<String> resourcePostBucket = new ArrayList<>();
        resourcePostBucket.add(this.postPhotoBucket.getBucketArn()+"/*");

        this.uploadPostPhotoPolicy = new PolicyStatement(
          PolicyStatementProps.builder()
          .effect(Effect.ALLOW)
          .actions(acttions)
          .resources(resourcePostBucket)
          .build()  
        );

        List<String> resourceProfileBucket = new ArrayList<>();
        resourceProfileBucket.add(this.profilePhotoBucket.getBucketArn()+"/*");

        this.uploadProfilePhotoPolicy = new PolicyStatement(
            PolicyStatementProps.builder()
            .effect(Effect.ALLOW)
            .actions(acttions)
            .resources(resourceProfileBucket)
            .build()  
          );

          
    } 
    

    
}
