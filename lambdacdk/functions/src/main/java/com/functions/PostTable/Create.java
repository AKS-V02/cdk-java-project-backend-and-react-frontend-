package com.functions.PostTable;




import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.LambdaLogger;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyResponseEvent;
import com.google.gson.Gson;

import software.amazon.awssdk.services.dynamodb.DynamoDbClient;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;
import software.amazon.awssdk.services.dynamodb.model.PutItemRequest;
import software.amazon.awssdk.services.dynamodb.model.PutItemResponse;
//import software.amazon.awssdk.services.dynamodb.model.PutRequest;
import software.amazon.awssdk.services.dynamodb.model.ReturnValue;


public class Create implements RequestHandler<APIGatewayProxyRequestEvent,APIGatewayProxyResponseEvent>{
   
    @Override
    public APIGatewayProxyResponseEvent handleRequest(APIGatewayProxyRequestEvent input, Context context) {
        APIGatewayProxyResponseEvent result = new APIGatewayProxyResponseEvent();
        Map<String, String> headers = new HashMap<>();
        headers.put("Content-Type", "application/json");
        headers.put("Access-Control-Allow-Origin", "*");
        headers.put("Access-Control-Allow-Methods", "*");
        LambdaLogger logger = context.getLogger();


       

        try {
            String body = input.getBody();
            logger.log("Body is:"+body);
            DynamoDbClient ddb = DynamoDbClient.create();
            String tableName= System.getenv("TABLE_NAME");
            String primaryKey = System.getenv("PRIMARY_KEY");
            Map<String, AttributeValue> item = new HashMap<>();
            String id = UUID.randomUUID().toString();
            item.put(primaryKey, AttributeValue.builder().s(id).build());

            Gson gson = new Gson();
            Post post = gson.fromJson(input.getBody(), Post.class);

            item.put("title", AttributeValue.builder().s(post.getTitle()).build());
            item.put("massage", AttributeValue.builder().s(post.getMassage()).build());
            item.put("username", AttributeValue.builder().s(post.getUsername()).build());
            item.put("imgUrl", AttributeValue.builder().s(post.getImgUrl()).build());
            item.put("imgKey", AttributeValue.builder().s(post.getImgKey()).build());
            PutItemRequest putItemRequest = PutItemRequest.builder()
                    .tableName(tableName)
                    .returnValues(ReturnValue.ALL_OLD)
                    .item(item)
                    .build();
            PutItemResponse response = ddb.putItem(putItemRequest);
            logger.log("Body is:"+response);
            return result.withStatusCode(200).withBody(gson.toJson(response.attributes().get(primaryKey))).withHeaders(headers);
        } catch (Exception e) {
            return result.withStatusCode(400).withBody("Error "+e.getMessage()).withHeaders(headers);
        }
    }   
}
