package com.functions.PostTable;

import java.util.HashMap;
import java.util.Map;

import com.amazonaws.services.lambda.runtime.Context;
//import com.amazonaws.services.lambda.runtime.LambdaLogger;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyResponseEvent;
//import com.google.gson.Gson;

import software.amazon.awssdk.services.dynamodb.DynamoDbClient;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;
import software.amazon.awssdk.services.dynamodb.model.DeleteItemRequest;
import software.amazon.awssdk.services.dynamodb.model.ReturnValue;

public class Delete implements RequestHandler<APIGatewayProxyRequestEvent,APIGatewayProxyResponseEvent> {

    @Override
    public APIGatewayProxyResponseEvent handleRequest(APIGatewayProxyRequestEvent input, Context context) {
        //LambdaLogger logger = context.getLogger();
        APIGatewayProxyResponseEvent result = new APIGatewayProxyResponseEvent();
        Map<String, String> headers = new HashMap<>();
        headers.put("Content-Type", "application/json");
        headers.put("Access-Control-Allow-Origin", "*");
        headers.put("Access-Control-Allow-Methods", "*");

        try {
            //Gson gson = new Gson();
            Map<String, String> pathParametermap = input.getPathParameters();
            String id = pathParametermap.get("id");
            DynamoDbClient ddb = DynamoDbClient.create();
            String tableName= System.getenv("TABLE_NAME");
            String primaryKey = System.getenv("PRIMARY_KEY");
            Map<String, AttributeValue> tableKey = new HashMap<>();
            tableKey.put(primaryKey, AttributeValue.builder().s(id).build());
            DeleteItemRequest getItemRequest= DeleteItemRequest.builder()
                    .key(tableKey)
                    .returnValues(ReturnValue.ALL_OLD)
                    .tableName(tableName)
                    .build();
            //String response = gson.toJson(ddb.deleteItem(getItemRequest).attributes());
            String response = ddb.deleteItem(getItemRequest).attributes().get("title").s();
            
            return result.withStatusCode(200).withBody(response+" is deleated").withHeaders(headers);
        } catch (Exception e) {
            return result.withStatusCode(400).withBody(e.getMessage()).withHeaders(headers);
        }

    }
    
}
