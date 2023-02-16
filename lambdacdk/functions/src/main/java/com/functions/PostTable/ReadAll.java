package com.functions.PostTable;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.LambdaLogger;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyResponseEvent;
import com.google.gson.Gson;

import software.amazon.awssdk.services.dynamodb.DynamoDbClient;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;
import software.amazon.awssdk.services.dynamodb.model.GetItemRequest;
import software.amazon.awssdk.services.dynamodb.model.QueryRequest;
import software.amazon.awssdk.services.dynamodb.model.ScanRequest;


public class ReadAll implements RequestHandler<APIGatewayProxyRequestEvent,APIGatewayProxyResponseEvent>{

    @Override
    public APIGatewayProxyResponseEvent handleRequest(APIGatewayProxyRequestEvent input, Context context) {
        
        APIGatewayProxyResponseEvent result = new APIGatewayProxyResponseEvent();
        LambdaLogger logger = context.getLogger();
        Map<String, String> headers = new HashMap<>();
        headers.put("Content-Type", "application/json");
        headers.put("Access-Control-Allow-Origin", "*");
        headers.put("Access-Control-Allow-Methods", "*");
        
        try {
            Map<String,String> queryParameter = input.getQueryStringParameters();
            Gson gson = new Gson();
            DynamoDbClient ddb = DynamoDbClient.create();
            String tableName= System.getenv("TABLE_NAME");
            String primaryKey = System.getenv("PRIMARY_KEY"); 
            String response = "";
            if(queryParameter !=null){
                if(queryParameter.keySet().contains(primaryKey)){
                    Map<String, AttributeValue> tableKey = new HashMap<>();
                    tableKey.put(primaryKey, AttributeValue.builder().s(queryParameter.get(primaryKey)).build());
                    GetItemRequest getItemRequest= GetItemRequest.builder()
                            .key(tableKey)
                            .tableName(tableName)
                            .build();
                    // response = gson.toJson(ddb.getItem(getItemRequest).item());
                    Map<String, AttributeValue> dbresponse = ddb.getItem(getItemRequest).item();
                    if(dbresponse.isEmpty()){
                        return result.withStatusCode(200).withBody("No Data Found").withHeaders(headers);
                    }
                    Post post = new Post(dbresponse.get("title").s(),
                    dbresponse.get("massage").s(), 
                    dbresponse.get("username").s(), 
                    dbresponse.get("imgUrl").s(),
                    dbresponse.get("imgKey").s());
                    post.setId(dbresponse.get("id").s());
                    logger.log("Body is:"+post.getId());
                    response = gson.toJson(post);
                } else {
                    String secondaryIndexkey = queryParameter.keySet().iterator().next();
                    Map<String, String> expressionAttributeName = new HashMap<>();
                    Map<String, AttributeValue> expressionAttributeValue = new HashMap<>();
                    String conditiontoQuery = "#aa = :vv";
                    expressionAttributeName.put("#aa", secondaryIndexkey);
                    expressionAttributeValue.put(":vv", AttributeValue.builder().s(queryParameter.get(secondaryIndexkey)).build());
                    QueryRequest queryRequest = QueryRequest.builder()
                            .indexName(secondaryIndexkey)
                            .tableName(tableName)
                            .keyConditionExpression(conditiontoQuery)
                            .expressionAttributeNames(expressionAttributeName)
                            .expressionAttributeValues(expressionAttributeValue)
                            .build();
                    // response =  gson.toJson(ddb.query(queryRequest).items());
                    List<Map<String, AttributeValue>> dbqueryresponse = ddb.query(queryRequest).items();
                    if(dbqueryresponse.isEmpty()){
                        return result.withStatusCode(200).withBody("No Data Found").withHeaders(headers);
                    }
                    List<Post> Posts = dbqueryresponse.stream().map(item->
                    new Post(item.get("id").s(),
                    item.get("title").s(),
                    item.get("massage").s(), 
                    item.get("username").s(), 
                    item.get("imgUrl").s(),
                    item.get("imgKey").s())).collect(Collectors.toList());
                    logger.log("Body is:"+Posts.get(0).getTitle());
                    response = gson.toJson(Posts);
                }

            } else {
                ScanRequest scanRequest= ScanRequest.builder()
                    .tableName(tableName)
                    .build();
                    // response =  gson.toJson(ddb.scan(scanRequest).items());
                    List<Map<String, AttributeValue>> ScanResponse = ddb.scan(scanRequest).items();
                    if(ScanResponse.isEmpty()){
                        return result.withStatusCode(200).withBody("No Data Found").withHeaders(headers);
                    }
                    List<Post> Posts = ScanResponse.stream().map(item->
                    new Post(item.get("id").s(),
                    item.get("title").s(),
                    item.get("massage").s(), 
                    item.get("username").s(), 
                    item.get("imgUrl").s(),
                    item.get("imgKey").s())).collect(Collectors.toList());
                    logger.log("Body is:"+Posts.get(0).getTitle());
                    response = gson.toJson(Posts);
            }
           
            return result.withStatusCode(200).withBody(response).withHeaders(headers);
        } catch (Exception e) {
            return result.withStatusCode(400).withBody(e.getMessage()).withHeaders(headers);
        }
    }
    
}
