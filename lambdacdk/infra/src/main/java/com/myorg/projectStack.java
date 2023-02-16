package com.myorg;

import java.util.List;

import org.jetbrains.annotations.NotNull;

import com.myorg.Auth.AutheriserWrapper;
import com.myorg.Auth.Policies;

import software.amazon.awscdk.CfnOutput;
import software.amazon.awscdk.Fn;
import software.amazon.awscdk.RemovalPolicy;
import software.amazon.awscdk.Stack;
import software.amazon.awscdk.StackProps;
import software.amazon.awscdk.services.apigateway.AuthorizationType;
import software.amazon.awscdk.services.apigateway.Cors;
import software.amazon.awscdk.services.apigateway.CorsOptions;
import software.amazon.awscdk.services.apigateway.IAuthorizer;
import software.amazon.awscdk.services.apigateway.IResource;
import software.amazon.awscdk.services.apigateway.MethodOptions;
import software.amazon.awscdk.services.apigateway.ResourceOptions;
// import software.amazon.awscdk.Duration;
// import software.amazon.awscdk.services.sqs.Queue;
import software.amazon.awscdk.services.apigateway.RestApi;
import software.amazon.awscdk.services.dynamodb.AttributeType;
import software.amazon.awscdk.services.s3.Bucket;
import software.amazon.awscdk.services.s3.BucketProps;
import software.amazon.awscdk.services.s3.CorsRule;
import software.amazon.awscdk.services.s3.HttpMethods;
import software.constructs.Construct;



public class projectStack extends Stack {
    public projectStack(final Construct scope, final String id) {
        this(scope, id, null);
    }


    private RestApi api = new RestApi(this, "NewApi");
    private TableProp postTableProp;
    private GenericTable postTable;

    private AutheriserWrapper authorizer;
    private String suffix;
    private Bucket postPhotosBucket;
    private Bucket profilePhotosBucket;
    private Policies policies;


    public projectStack(final Construct scope, final String id, final StackProps props) {
        super(scope, id, props);

        this.postTableProp = new TableProp("PostTable", 
                        "id", 
                        AttributeType.STRING);
       
        postTableProp.setCreateLambdaPath("Create");
        postTableProp.setDeleteLambdaPath("Delete");
        postTableProp.setReadLambdaPath("Read");
        postTableProp.setReadAllLambdaPath("ReadAll");
        postTableProp.setUpdateLambdaPath("Update");
        postTableProp.setSecondaryIndexes(List.of("username"));
        
        this.postTable = new GenericTable(this, postTableProp);

        // Function lambFunction = new Function(this, "testFunction", FunctionProps.builder()
        // 	.functionName("testFunction")
        //     .runtime(Runtime.JAVA_8)
        //     .code(Code.fromAsset("../lambda_Assates/lambdaFunctions.jar"))
        //     .handler("com.functions.hello")
        //     .memorySize(512)
        //     .timeout(Duration.seconds(60))
        //     .build());
    
        //     LambdaIntegration integration = new LambdaIntegration(lambFunction);


        //     this.api.getRoot().addResource("hello").addMethod("GET", integration);



        this.initializeSuffix();
        this.initializePostsPhotosBucket();
        this.initializeProfilePhotosBucket();
        this.policies = new Policies(this.postPhotosBucket, this.profilePhotosBucket);
        this.authorizer = new AutheriserWrapper(
            this,
            this.api,
            this.policies);
        
        new WebAppDeployment(this, this.suffix); 
        
        CfnOutput.Builder.create(this, "api name")
        .value(this.api.getRestApiName())
        .build();
        
        MethodOptions optionsWithAutheriser = new MethodOptions.Builder()
        .authorizationType(AuthorizationType.COGNITO)
        .authorizer(new IAuthorizer() {
            @Override
            public @NotNull String getAuthorizerId() {
                return authorizer.authorizer.getAttrAuthorizerId();
            }    
        })
        .build();      
        
        ResourceOptions optionsWithCors = new ResourceOptions.Builder()
        .defaultCorsPreflightOptions(CorsOptions.builder()
                                    .allowOrigins(Cors.ALL_ORIGINS)
                                    .allowMethods(Cors.ALL_METHODS)
                                    .build())   
        .build();
        

        //postTable Api Intigration
        IResource  postTableResource = this.api.getRoot().addResource("posts", optionsWithCors);
        postTableResource.addMethod("GET", postTable.getReadAllLambdaIntegration(), optionsWithAutheriser);
        postTableResource.addMethod("POST", postTable.getCreateLambdaIntegration(), optionsWithAutheriser);
        
        IResource singlemethodPostTable = postTableResource.addResource("{id}", optionsWithCors);
        singlemethodPostTable.addMethod("GET", postTable.getReadLambdaIntegration(), optionsWithAutheriser);
        singlemethodPostTable.addMethod("PUT", postTable.getUpdateLambdaIntegration(), optionsWithAutheriser);
        singlemethodPostTable.addMethod("DELETE", postTable.getDeleteLambdaIntegration(), optionsWithAutheriser);

    }


    private void initializeProfilePhotosBucket() {

        this.profilePhotosBucket = new Bucket(this, "profile-photos", BucketProps.builder()
        .bucketName("profile-photos-"+this.suffix)
        .cors(List.of(CorsRule.builder()
                    .allowedOrigins(List.of("*"))
                    .allowedHeaders(List.of("*"))
                    .allowedMethods(List.of(HttpMethods.GET,
                                            HttpMethods.PUT,
                                            HttpMethods.HEAD,
                                            HttpMethods.DELETE))  
                    .build()))  
        
        .removalPolicy(RemovalPolicy.DESTROY)
        .build());

        CfnOutput.Builder.create(this, "profile-photos-bucket-name")
        .value(this.profilePhotosBucket.getBucketName())
        .build();
    }


    private void initializePostsPhotosBucket() {

        this.postPhotosBucket = new Bucket(this, "posts-photos", BucketProps.builder()
        .bucketName("posts-photos-"+this.suffix)
        .cors(List.of(CorsRule.builder()
                    .allowedOrigins(List.of("*"))
                    .allowedHeaders(List.of("*"))
                    .allowedMethods(List.of(HttpMethods.GET,
                                            HttpMethods.PUT,
                                            HttpMethods.HEAD,
                                            HttpMethods.DELETE))  
                    .build()))  
        
        .removalPolicy(RemovalPolicy.DESTROY)
        .build());

        CfnOutput.Builder.create(this, "posts-photos-bucket-name")
        .value(this.postPhotosBucket.getBucketName())
        .build();

    }


    private void initializeSuffix() {

        String shortStackId = Fn.select(2, Fn.split("/", this.getStackId()));
        String Suffix = Fn.select(4, Fn.split("-", shortStackId));
        this.suffix = Suffix;
    }
}
