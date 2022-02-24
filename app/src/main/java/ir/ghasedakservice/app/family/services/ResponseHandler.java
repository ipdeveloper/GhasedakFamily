package ir.ghasedakservice.app.family.services;

import org.json.JSONObject;

/**
 * Created by Iman on 3/6/2018.
 */

public interface ResponseHandler {
    void onJsonObjectResult(int requestCode, int responseStatusCode, JSONObject responseJson, Object requestObject);
    void onJsonErrorResponse(int requestCode, int responseStatusCode, String responseError, Object requestObject);
//    void onJsonArrayReslut(int requestCode, int responseStatusCode, JSONArray responseArray, Object requestObject);
//    void onStringReslut( int requestCode,int responseStatusCode, String responseString, Object requestDate);
}
