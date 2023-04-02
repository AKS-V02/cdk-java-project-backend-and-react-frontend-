package com.myorg;


import java.util.HashMap;
import java.util.Map;

import software.amazon.awscdk.Duration;
import software.amazon.awscdk.RemovalPolicy;
import software.amazon.awscdk.Stack;
import software.amazon.awscdk.services.apigateway.LambdaIntegration;
import software.amazon.awscdk.services.dynamodb.Attribute;
import software.amazon.awscdk.services.dynamodb.AttributeType;
import software.amazon.awscdk.services.dynamodb.GlobalSecondaryIndexProps;
import software.amazon.awscdk.services.dynamodb.Table;
import software.amazon.awscdk.services.dynamodb.TableProps;
import software.amazon.awscdk.services.lambda.Code;
import software.amazon.awscdk.services.lambda.Function;
import software.amazon.awscdk.services.lambda.FunctionProps;
import software.amazon.awscdk.services.lambda.Runtime;
import software.amazon.awscdk.services.logs.RetentionDays;



public class GenericTable {
    private Stack stack;
    private Table table;
    private TableProp tableProps;

    private Function createLambda = null;
    private Function updateLambda = null;
    private Function deleteLambda = null;
    private Function readLambda = null;
    private Function readAllLambda = null;


    private LambdaIntegration createLambdaIntegration;
    private LambdaIntegration readLambdaIntegration;
    private LambdaIntegration updateLambdaIntegration;
    private LambdaIntegration deleteLambdaIntegration;
    private LambdaIntegration readAllLambdaIntegration;


    private Map<String, String> lambdaEnvMap = new HashMap<>();
        


    public LambdaIntegration getReadAllLambdaIntegration() {
        return readAllLambdaIntegration;
    }

    public LambdaIntegration getCreateLambdaIntegration() {
        return createLambdaIntegration;
    }

    public LambdaIntegration getReadLambdaIntegration() {
        return readLambdaIntegration;
    }

    public LambdaIntegration getUpdateLambdaIntegration() {
        return updateLambdaIntegration;
    }

    public LambdaIntegration getDeleteLambdaIntegration() {
        return deleteLambdaIntegration;
    }

    public GenericTable(Stack stack, TableProp tableProps) {
        this.stack = stack;
        this.tableProps = tableProps;
        this.initialize();
    }

    private void initialize() {
        this.createTable();
        this.addSecondaryIndexes();
        this.createLambdas();
        this.grantTableRights();
    }

    private void grantTableRights() {
        if(this.createLambda!=null){
            this.table.grantWriteData(this.createLambda);
        } 
        if(this.updateLambda!=null){
            this.table.grantWriteData(this.updateLambda);
        }
        if(this.readLambda!=null){
            this.table.grantReadData(this.readLambda);
        }
        if(this.deleteLambda!=null){
            this.table.grantWriteData(this.deleteLambda);
        }
        if(this.readAllLambda!=null){
            this.table.grantReadData(this.readAllLambda);
        }

    }

    private void createLambdas() {
        if(this.tableProps.getCreateLambdaPath()!=null){

            this.createLambda = createSingleLambda(this.tableProps.getCreateLambdaPath());
            this.createLambdaIntegration = new LambdaIntegration(this.createLambda);

        } 
        if(this.tableProps.getReadLambdaPath()!=null){

            this.readLambda = createSingleLambda(this.tableProps.getReadLambdaPath());
            this.readLambdaIntegration = new LambdaIntegration(this.readLambda);

        } 
        if(this.tableProps.getUpdateLambdaPath()!=null){

            this.updateLambda = createSingleLambda(this.tableProps.getUpdateLambdaPath());
            this.updateLambdaIntegration = new LambdaIntegration(this.updateLambda);

        } 
        if(this.tableProps.getDeleteLambdaPath()!=null){

            this.deleteLambda = createSingleLambda(this.tableProps.getDeleteLambdaPath());
            this.deleteLambdaIntegration = new LambdaIntegration(this.deleteLambda);
        } 
        if(this.tableProps.getReadAllLambdaPath()!=null){

            this.readAllLambda = createSingleLambda(this.tableProps.getReadAllLambdaPath());
            this.readAllLambdaIntegration = new LambdaIntegration(this.readAllLambda);
        }
        
    }


    private Function createSingleLambda(String lambdaName) {
        lambdaEnvMap.put("TABLE_NAME", this.tableProps.getTableName());
        lambdaEnvMap.put("PRIMARY_KEY",this.tableProps.getPartitionKey().getName());
        
        // require docker or Rancher dextop
        // List<String> lambdaFunctionPackagingInstructions = Arrays.asList(
        //     "/bin/sh",
        //     "-c",
        //     "cd functions " +
        //         "&& mvn clean install " +
        //         "&& cp /asset-input/functions/target/bmicalculator.jar /asset-output/"
        // );

        // BundlingOptions.Builder builderOptions = BundlingOptions.builder()
        // .image(Runtime.JAVA_11.getBundlingImage())
        // .volumes(singletonList(
        //         DockerVolume.builder()
        //             .hostPath(System.getProperty("user.home") + "/.m2/")
        //             .containerPath("/root/.m2/")))  
        // .outputType(BundlingOutput.ARCHIVED);

       return new Function(this.stack, this.tableProps.getTableName()+"-"+lambdaName, FunctionProps.builder()
       .functionName(this.tableProps.getTableName()+"-"+lambdaName)
       .runtime(Runtime.JAVA_11)
       .code(Code.fromAsset("../lambda_Assates/lambdaFunctions.jar"))
    //    .code(Code.fromAsset("../", AssetOptions.builder().bundling( // require docker or Rancher dextop
    //             builderOptions.command(
    //                 lambdaFunctionPackagingInstructions
    //             ).build()
    //         ).build()
    //         ))
       .handler("com.functions."+this.tableProps.getTableName()+"."+lambdaName)
       .environment(lambdaEnvMap)
       .memorySize(512)
       .timeout(Duration.seconds(120))
       .logRetention(RetentionDays.ONE_DAY)
       .build());
    }

    private void addSecondaryIndexes() {
        if(this.tableProps.getSecondaryIndexes()!=null){
            for(String index : this.tableProps.getSecondaryIndexes()){
                this.table.addGlobalSecondaryIndex(GlobalSecondaryIndexProps.builder()
                .indexName(index)
                .partitionKey(new Attribute.Builder().name(index).type(AttributeType.STRING).build())
                .build()
                );
            }
        }
    }


    private void createTable() {
    
        this.table = new Table(this.stack, this.tableProps.getTableName(), TableProps.builder()
        .tableName(tableProps.getTableName())
        .partitionKey(tableProps.getPartitionKey())
        .removalPolicy(RemovalPolicy.DESTROY)
        .build());

    }

}
