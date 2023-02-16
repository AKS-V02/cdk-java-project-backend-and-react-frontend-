package com.myorg;

import java.util.List;

import software.amazon.awscdk.CfnOutput;
import software.amazon.awscdk.CfnOutputProps;
import software.amazon.awscdk.RemovalPolicy;
import software.amazon.awscdk.Stack;
import software.amazon.awscdk.services.cloudfront.Behavior;
import software.amazon.awscdk.services.cloudfront.CloudFrontWebDistribution;
import software.amazon.awscdk.services.cloudfront.CloudFrontWebDistributionProps;
import software.amazon.awscdk.services.cloudfront.S3OriginConfig;
import software.amazon.awscdk.services.cloudfront.SourceConfiguration;
import software.amazon.awscdk.services.s3.Bucket;
import software.amazon.awscdk.services.s3.BucketProps;
import software.amazon.awscdk.services.s3.deployment.BucketDeployment;
import software.amazon.awscdk.services.s3.deployment.BucketDeploymentProps;
import software.amazon.awscdk.services.s3.deployment.Source;

public class WebAppDeployment {
    private Stack stack;
    private String bucketSuffix;
    private Bucket deploymenBucket;


    public WebAppDeployment(Stack stack, String bucketSuffix) {
        this.stack = stack;
        this.bucketSuffix = bucketSuffix;
        this.initialize();
    }


    private void initialize() {
        String bucketName = "project-web-app"+this.bucketSuffix;
        this.deploymenBucket = new Bucket(this.stack,"project-web-app-id",
         BucketProps.builder()
         .bucketName(bucketName)
         .publicReadAccess(true)
         .websiteIndexDocument("index.html")
         .removalPolicy(RemovalPolicy.DESTROY)
         .build());

         new BucketDeployment(this.stack, "project-web-app-id-deployment",
          BucketDeploymentProps.builder()
          .destinationBucket(this.deploymenBucket)
          .sources(List.of(Source.asset("../../app-front-end/build")))
          .build());

          new CfnOutput(this.stack, "projectWebAppS3Url",
           CfnOutputProps.builder()
           .value(this.deploymenBucket.getBucketWebsiteUrl())
           .build());


           CloudFrontWebDistribution cloudFront = new CloudFrontWebDistribution(this.stack,
           "project-web-app-distribution-id",
           CloudFrontWebDistributionProps.builder()
           .originConfigs(List.of(SourceConfiguration.builder()
                                .behaviors(List.of(Behavior.builder()
                                            .isDefaultBehavior(true)
                                            .build()))
                                .s3OriginSource(S3OriginConfig.builder()
                                            .s3BucketSource(this.deploymenBucket)
                                            .build())   
                                .build()))
            .build());


           new CfnOutput(this.stack, "projectWebAppCloudFrontUrl",
           CfnOutputProps.builder()
           .value(cloudFront.getDistributionDomainName())
           .build());

    }
    
    
}
