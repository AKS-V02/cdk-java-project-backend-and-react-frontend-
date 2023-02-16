package com.myorg;

import java.util.ArrayList;
import java.util.List;

import software.amazon.awscdk.services.dynamodb.Attribute;
import software.amazon.awscdk.services.dynamodb.AttributeType;

public class TableProp{
    private String tableName;
    private String partitionKey;
    private AttributeType partitionKeyType;
    private String createLambdaPath = null; 
    private String readLambdaPath = null;
    private String updateLambdaPath = null;
    private String deleteLambdaPath = null;
    private String readAllLambdaPath = null;  
    private List<String> secondaryIndexes = new ArrayList<>();
    
    
    
    public TableProp(String tableName, String partitionKey, AttributeType partitionKeyType) {
        this.tableName = tableName;
        this.partitionKey = partitionKey;
        this.partitionKeyType = partitionKeyType;
    }


    public String getTableName() {
        return tableName;
    }
    public void setTableName(String tableName) {
        this.tableName = tableName;
    }
    public void setPartitionKey(String partitionKey) {
        this.partitionKey = partitionKey;
    }
    public Attribute getPartitionKey() {
        return new Attribute.Builder().name(this.partitionKey).type(this.partitionKeyType).build();
        
    }
    public AttributeType getPartitionKeyType() {
        return partitionKeyType;
    }
    public void setPartitionKeyType(AttributeType partitionKeyType) {
        this.partitionKeyType = partitionKeyType;
    }
    public String getCreateLambdaPath() {
        return createLambdaPath;
    }
    public void setCreateLambdaPath(String createLambdaPath) {
        this.createLambdaPath = createLambdaPath;
    }
    public String getReadLambdaPath() {
        return readLambdaPath;
    }
    public void setReadLambdaPath(String readLambdaPath) {
        this.readLambdaPath = readLambdaPath;
    }
    public String getUpdateLambdaPath() {
        return updateLambdaPath;
    }
    public void setUpdateLambdaPath(String updateLambdaPath) {
        this.updateLambdaPath = updateLambdaPath;
    }
    public String getDeleteLambdaPath() {
        return deleteLambdaPath;
    }
    public void setDeleteLambdaPath(String deleteLambdaPath) {
        this.deleteLambdaPath = deleteLambdaPath;
    }
    public List<String> getSecondaryIndexes() {
        return secondaryIndexes;
    }
    public void setSecondaryIndexes(List<String> secondaryIndexes) {
        this.secondaryIndexes = secondaryIndexes;
    }
    public String getReadAllLambdaPath() {
        return readAllLambdaPath;
    }
    public void setReadAllLambdaPath(String readAllLambdaPath) {
        this.readAllLambdaPath = readAllLambdaPath;
    }

}
