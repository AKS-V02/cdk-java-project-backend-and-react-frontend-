package com.functions.PostTable;

import java.util.HashMap;
import java.util.Map;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.LambdaLogger;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyResponseEvent;
import com.google.gson.Gson;

import software.amazon.awssdk.services.dynamodb.DynamoDbClient;
import software.amazon.awssdk.services.dynamodb.model.AttributeAction;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;
import software.amazon.awssdk.services.dynamodb.model.AttributeValueUpdate;
import software.amazon.awssdk.services.dynamodb.model.ReturnValue;
import software.amazon.awssdk.services.dynamodb.model.UpdateItemRequest;

public class Update implements RequestHandler<APIGatewayProxyRequestEvent,APIGatewayProxyResponseEvent>  {

    @Override
    public APIGatewayProxyResponseEvent handleRequest(APIGatewayProxyRequestEvent input, Context context) {
        LambdaLogger logger = context.getLogger();
        APIGatewayProxyResponseEvent result = new APIGatewayProxyResponseEvent();
        Map<String, String> headers = new HashMap<>();
        headers.put("Content-Type", "application/json");
        headers.put("Access-Control-Allow-Origin", "*");
        headers.put("Access-Control-Allow-Methods", "*");

        try {
            Map<String, String> pathParametermap = input.getPathParameters();
            String id = pathParametermap.get("id");
            Gson gson = new Gson();
            Post updatedPost = gson.fromJson(input.getBody(), Post.class);

            DynamoDbClient ddb = DynamoDbClient.create();
            String tableName= System.getenv("TABLE_NAME");
            String primaryKey = System.getenv("PRIMARY_KEY");
            Map<String, AttributeValue> tableKey = new HashMap<>();
            tableKey.put(primaryKey, AttributeValue.builder().s(id).build());
    
            Map<String, AttributeValueUpdate> item = new HashMap<>();
    
            item.put("title", AttributeValueUpdate.builder()
                            .value(AttributeValue.builder().s(updatedPost.getTitle()).build())
                            .action(AttributeAction.PUT)
                            .build());
            item.put("massage", AttributeValueUpdate.builder()
                            .value(AttributeValue.builder().s(updatedPost.getMassage()).build())
                            .action(AttributeAction.PUT)
                            .build());
            item.put("username", AttributeValueUpdate.builder()
                            .value(AttributeValue.builder().s(updatedPost.getUsername()).build())
                            .action(AttributeAction.PUT)
                            .build()); 
            item.put("imgUrl", AttributeValueUpdate.builder()
                            .value(AttributeValue.builder().s(updatedPost.getImgUrl()).build())
                            .action(AttributeAction.PUT)
                            .build());   
            item.put("imgKey", AttributeValueUpdate.builder()
                            .value(AttributeValue.builder().s(updatedPost.getImgKey()).build())
                            .action(AttributeAction.PUT)
                            .build());         
            UpdateItemRequest updateItemRequest = UpdateItemRequest.builder()
                    .key(tableKey)
                    .tableName(tableName)
                    .attributeUpdates(item)
                    .returnValues(ReturnValue.ALL_NEW)
                    .build();
            // String response = gson.toJson(ddb.updateItem(updateItemRequest).attributes());
            Map<String, AttributeValue> response = ddb.updateItem(updateItemRequest).attributes();
            Post post = new Post(response.get("id").s(),
            response.get("title").s(),
            response.get("massage").s(), 
            response.get("username").s(), 
            response.get("imgUrl").s());
            logger.log("Body is:"+post.getId());

            return result.withStatusCode(200).withBody(gson.toJson(post)).withHeaders(headers);
        } catch (Exception e) {
            return result.withStatusCode(400).withBody(e.getMessage()).withHeaders(headers);
        }

    }
    
}
