package com.hampay.mobile.android.webservice;

import com.google.gson.*;
import com.google.gson.reflect.TypeToken;
import com.hampay.common.common.request.RequestHeader;
import com.hampay.common.common.request.RequestMessage;
import com.hampay.common.common.response.ResponseMessage;
import com.hampay.common.core.model.request.BankListRequest;
import com.hampay.common.core.model.request.TransactionListRequest;
import com.hampay.common.core.model.request.UserProfileRequest;
import com.hampay.common.core.model.response.BankListResponse;
import com.hampay.common.core.model.response.TransactionListResponse;
import com.hampay.common.core.model.response.UserProfileResponse;
import com.hampay.mobile.android.util.Constant;

import org.apache.http.*;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.util.EntityUtils;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.lang.reflect.Type;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Date;

/**
 * Created by amir on 6/6/15.
 */
public class WebServices  {

    public WebServices(){}

    public void testBankList1() {

        HttpURLConnection connection = null;
        try {

            RequestHeader header = new RequestHeader();
            header.setAuthToken("008ewe");
            header.setVersion("1.0-PA");

            RequestMessage<BankListRequest> message = new RequestMessage<BankListRequest>();
            message.setRequestHeader(header);
            BankListRequest request = new BankListRequest();
            request.setRequestUUID("1234");
            message.setService(request);

            Type requestType = new com.google.gson.reflect.TypeToken<RequestMessage<BankListRequest>>() {}.getType();
            String jsonRequest = new Gson().toJson(message, requestType);

            URL url = new URL(Constant.SERVICE_URL + "/banks");

            connection = (HttpURLConnection) url.openConnection();
            connection.setDoOutput(true);
            connection.setRequestMethod("POST");
            connection.setRequestProperty("Content-Type", "application/json");
            OutputStream outputStream = connection.getOutputStream();
            outputStream.write(jsonRequest.getBytes());
            outputStream.flush();

            InputStreamReader reader = new InputStreamReader(connection.getInputStream());
            Gson gson = new Gson();
            ResponseMessage<BankListResponse> responseMessage = gson.fromJson(reader, new TypeToken<ResponseMessage<BankListResponse>>() {
            }.getType());

            if( responseMessage != null && responseMessage.getService() != null ) {
                System.out.println("Response requestUUID : " + responseMessage.getService().getRequestUUID());
                System.out.println("Response bank list size : " + responseMessage.getService().getBanks().size());
            } else {
                System.out.println("Response is null...");
            }

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (connection != null)
                connection.disconnect();
        }

    }

    public ResponseMessage<UserProfileResponse>  getUserProfile(){

        ResponseMessage<UserProfileResponse> userProfileResponse = null;

        HttpParams httpParameters = new BasicHttpParams();

        int timeoutConnection = 30000;
        HttpConnectionParams.setConnectionTimeout(httpParameters, timeoutConnection);
        int timeoutSocket = 30000;
        HttpConnectionParams.setSoTimeout(httpParameters, timeoutSocket);

        HttpClient httpClient = new DefaultHttpClient(httpParameters);

        HttpHost host = new HttpHost("176.58.104.158", 9093);
        HttpRequest request = new HttpPost("/users/profile");

        HttpResponse response;
        try {
            StringEntity entity = new StringEntity(createUserProfileJsonMessage(), "UTF-8");
            entity.setContentType("application/json");
            ((HttpPost) request).setEntity(entity);

            response = httpClient.execute(host, request);

            HttpEntity responseEntity = response.getEntity();
            StatusLine responseStatus = response.getStatusLine();
            int statusCode = responseStatus != null ? responseStatus.getStatusCode() : 0;
            String result = EntityUtils.toString(responseEntity);

            System.out.println("User profile response, status  : " + String.valueOf(statusCode) + " , payload : " + result);

            GsonBuilder builder = new GsonBuilder();
            builder.registerTypeAdapter(Date.class, new JsonDeserializer<Date>() {
                public Date deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
                    return new Date(json.getAsJsonPrimitive().getAsLong());
                }
            });
            Gson gson = builder.create();

            Type responseType = new com.google.gson.reflect.TypeToken<ResponseMessage<UserProfileResponse>>() {}.getType();
            userProfileResponse = gson.fromJson(result, responseType);


        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            httpClient.getConnectionManager().shutdown();
        }

        return userProfileResponse;

    }

    public ResponseMessage<TransactionListResponse>  getUserTransaction(){

        ResponseMessage<TransactionListResponse> transactionListResponse = null;

        HttpParams httpParameters = new BasicHttpParams();

        int timeoutConnection = 30000;
        HttpConnectionParams.setConnectionTimeout(httpParameters, timeoutConnection);
        int timeoutSocket = 30000;
        HttpConnectionParams.setSoTimeout(httpParameters, timeoutSocket);

        HttpClient httpClient = new DefaultHttpClient(httpParameters);

        HttpHost host = new HttpHost("176.58.104.158", 9093);
        HttpRequest request = new HttpPost("/transactions");

        HttpResponse response;
        try {
            StringEntity entity = new StringEntity(createUserTransaction(), "UTF-8");
            entity.setContentType("application/json");
            ((HttpPost) request).setEntity(entity);

            response = httpClient.execute(host, request);

            HttpEntity responseEntity = response.getEntity();
            StatusLine responseStatus = response.getStatusLine();
            int statusCode = responseStatus != null ? responseStatus.getStatusCode() : 0;
            String result = EntityUtils.toString(responseEntity);

            System.out.println("User profile response, status  : " + String.valueOf(statusCode) + " , payload : " + result);

            GsonBuilder builder = new GsonBuilder();
            builder.registerTypeAdapter(Date.class, new JsonDeserializer<Date>() {
                public Date deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
                    return new Date(json.getAsJsonPrimitive().getAsLong());
                }
            });
            Gson gson = builder.create();

            Type responseType = new com.google.gson.reflect.TypeToken<ResponseMessage<TransactionListResponse>>() {}.getType();
            transactionListResponse = gson.fromJson(result, responseType);


        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            httpClient.getConnectionManager().shutdown();
        }

        return transactionListResponse;

    }

    private String createUserProfileJsonMessage() {
        RequestHeader header = new RequestHeader();
        header.setAuthToken("008ewe");
        header.setVersion("1.0-PA");


        RequestMessage<UserProfileRequest> message = new RequestMessage<UserProfileRequest>();
        message.setRequestHeader(header);
        UserProfileRequest request = new UserProfileRequest();
        request.setRequestUUID("1234");
        message.setService(request);

        Type requestType = new com.google.gson.reflect.TypeToken<RequestMessage<UserProfileRequest>>() {}.getType();
        return new Gson().toJson(message, requestType);
    }

    private String createUserTransaction() {
        RequestHeader header = new RequestHeader();
        header.setAuthToken("008ewe");
        header.setVersion("1.0-PA");


        RequestMessage<TransactionListRequest> message = new RequestMessage<TransactionListRequest>();
        message.setRequestHeader(header);
        TransactionListRequest request = new TransactionListRequest();
        request.setRequestUUID("1234");
        message.setService(request);

        Type requestType = new com.google.gson.reflect.TypeToken<RequestMessage<UserProfileRequest>>() {}.getType();
        return new Gson().toJson(message, requestType);
    }

}
