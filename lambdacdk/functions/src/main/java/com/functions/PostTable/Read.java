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
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;
import software.amazon.awssdk.services.dynamodb.model.GetItemRequest;

public class Read implements RequestHandler<APIGatewayProxyRequestEvent,APIGatewayProxyResponseEvent>{

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
            DynamoDbClient ddb = DynamoDbClient.create();
            String tableName= System.getenv("TABLE_NAME");
            String primaryKey = System.getenv("PRIMARY_KEY");
            Map<String, AttributeValue> tableKey = new HashMap<>();
            tableKey.put(primaryKey, AttributeValue.builder().s(id).build());
            GetItemRequest getItemRequest= GetItemRequest.builder()
                    .key(tableKey)
                    .tableName(tableName)
                    .build();
            Gson gson = new Gson();
            Map<String, AttributeValue> dbResponse = ddb.getItem(getItemRequest).item();
            if(dbResponse.isEmpty()){
                return result.withStatusCode(200).withBody("No Data Found").withHeaders(headers);
            }
            Post post = new Post(dbResponse.get("title").s(),
            dbResponse.get("massage").s(), 
            dbResponse.get("username").s(), 
            dbResponse.get("imgUrl").s(),
            dbResponse.get("imgKey").s());
            post.setId(dbResponse.get("id").s());
            logger.log("Body is:"+post.getId());
            //response = gson.toJson(post);
           
            return result.withStatusCode(200).withBody(gson.toJson(post)).withHeaders(headers);
        } catch (Exception e) {
            return result.withStatusCode(400).withBody(e.getMessage()).withHeaders(headers);
        }

    }
    
}
