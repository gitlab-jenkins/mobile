package com.hampay.mobile.android.webservice;

import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.provider.ContactsContract;

import com.google.gson.*;
import com.google.gson.reflect.TypeToken;
import com.hampay.common.common.request.RequestHeader;
import com.hampay.common.common.request.RequestMessage;
import com.hampay.common.common.response.ResponseMessage;
import com.hampay.common.core.model.dto.ContactDTO;
import com.hampay.common.core.model.request.BankListRequest;
import com.hampay.common.core.model.request.BusinessListRequest;
import com.hampay.common.core.model.request.BusinessPaymentConfirmRequest;
import com.hampay.common.core.model.request.BusinessPaymentRequest;
import com.hampay.common.core.model.request.BusinessSearchRequest;
import com.hampay.common.core.model.request.ChangeMemorableWordRequest;
import com.hampay.common.core.model.request.ChangePassCodeRequest;
import com.hampay.common.core.model.request.ContactUsRequest;
import com.hampay.common.core.model.request.ContactsHampayEnabledRequest;
import com.hampay.common.core.model.request.IndividualPaymentConfirmRequest;
import com.hampay.common.core.model.request.IndividualPaymentRequest;
import com.hampay.common.core.model.request.RegistrationConfirmUserDataRequest;
import com.hampay.common.core.model.request.RegistrationEntryRequest;
import com.hampay.common.core.model.request.RegistrationFetchUserDataRequest;
import com.hampay.common.core.model.request.RegistrationMemorableWordEntryRequest;
import com.hampay.common.core.model.request.RegistrationPassCodeEntryRequest;
import com.hampay.common.core.model.request.RegistrationSendSmsTokenRequest;
import com.hampay.common.core.model.request.RegistrationVerifyAccountRequest;
import com.hampay.common.core.model.request.RegistrationVerifyMobileRequest;
import com.hampay.common.core.model.request.RegistrationVerifyTransferMoneyRequest;
import com.hampay.common.core.model.request.TACAcceptRequest;
import com.hampay.common.core.model.request.TACRequest;
import com.hampay.common.core.model.request.TransactionListRequest;
import com.hampay.common.core.model.request.UserProfileRequest;
import com.hampay.common.core.model.request.VerifyAccountRequest;
import com.hampay.common.core.model.request.VerifyTransferMoneyRequest;
import com.hampay.common.core.model.response.BankListResponse;
import com.hampay.common.core.model.response.BusinessListResponse;
import com.hampay.common.core.model.response.BusinessPaymentConfirmResponse;
import com.hampay.common.core.model.response.BusinessPaymentResponse;
import com.hampay.common.core.model.response.ChangeMemorableWordResponse;
import com.hampay.common.core.model.response.ChangePassCodeResponse;
import com.hampay.common.core.model.response.ContactUsResponse;
import com.hampay.common.core.model.response.ContactsHampayEnabledResponse;
import com.hampay.common.core.model.response.IndividualPaymentConfirmResponse;
import com.hampay.common.core.model.response.IndividualPaymentResponse;
import com.hampay.common.core.model.response.RegistrationConfirmUserDataResponse;
import com.hampay.common.core.model.response.RegistrationEntryResponse;
import com.hampay.common.core.model.response.RegistrationFetchUserDataResponse;
import com.hampay.common.core.model.response.RegistrationMemorableWordEntryResponse;
import com.hampay.common.core.model.response.RegistrationPassCodeEntryResponse;
import com.hampay.common.core.model.response.RegistrationSendSmsTokenResponse;
import com.hampay.common.core.model.response.RegistrationVerifyAccountResponse;
import com.hampay.common.core.model.response.RegistrationVerifyMobileResponse;
import com.hampay.common.core.model.response.RegistrationVerifyTransferMoneyResponse;
import com.hampay.common.core.model.response.TACAcceptResponse;
import com.hampay.common.core.model.response.TACResponse;
import com.hampay.common.core.model.response.TransactionListResponse;
import com.hampay.common.core.model.response.UserProfileResponse;
import com.hampay.common.core.model.response.VerifyAccountResponse;
import com.hampay.common.core.model.response.VerifyTransferMoneyResponse;
import com.hampay.mobile.android.model.LoginData;
import com.hampay.mobile.android.model.SuccessLoginResponse;
import com.hampay.mobile.android.model.LogoutData;
import com.hampay.mobile.android.model.LogoutResponse;
import com.hampay.mobile.android.util.Constants;

import org.apache.http.*;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.util.EntityUtils;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.lang.reflect.Type;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by amir on 6/6/15.
 */
public class WebServices  {

    public WebServices(){}

    Context context;

    SharedPreferences prefs;

    public WebServices(Context context){

        this.context = context;

        prefs =  context.getSharedPreferences(Constants.APP_PREFERENCE_NAME, context.MODE_PRIVATE);

    }


//    public SuccessLoginResponse sendLoginRequest(LoginData loginData)throws Exception {
//
//
//
//
//        BufferedReader in = new BufferedReader(
//                new InputStreamReader(httpURLConnection.getInputStream()));
//
//        String inputLine;
//        StringBuffer response = new StringBuffer();
//
//        while ((inputLine = in.readLine()) != null) {
//            response.append(inputLine);
//        }
//        in.close();
//
//        Gson gson = new Gson();
//
//        Type listType = new TypeToken<SuccessLoginResponse>(){}.getType();
//
//        JsonParser jsonParser = new JsonParser();
//        JsonElement responseElement = jsonParser.parse(response.toString());
//
//        return (SuccessLoginResponse) gson.fromJson(responseElement.toString(), listType);
//    }


    public LogoutResponse sendLogoutRequest(LogoutData logoutData)throws Exception {

        URL urlConnection = new URL(Constants.OPENAM_LOGOUT_URL);
        HttpURLConnection httpURLConnection = (HttpURLConnection) urlConnection.openConnection();

        httpURLConnection.setRequestMethod("POST");
        httpURLConnection.setConnectTimeout(20 * 1000);
        httpURLConnection.setReadTimeout(20 * 1000);

        httpURLConnection.setDoOutput(true);

        httpURLConnection.setRequestProperty("iplanetDirectoryPro", logoutData.getIplanetDirectoryPro());
        httpURLConnection.setRequestProperty("Content-Type", "application/json");
        httpURLConnection.setRequestProperty("Accept-Encoding", "UTF-8");

        BufferedWriter output = new BufferedWriter(new OutputStreamWriter(httpURLConnection.getOutputStream()));
        output.write("");
        output.flush();
        output.close();

        int responseCode = httpURLConnection.getResponseCode();

        BufferedReader in = new BufferedReader(
                new InputStreamReader(httpURLConnection.getInputStream()));
        String inputLine;
        StringBuffer response = new StringBuffer();

        while ((inputLine = in.readLine()) != null) {
            response.append(inputLine);
        }
        in.close();

        Gson gson = new Gson();

        Type listType = new TypeToken<LogoutResponse>(){}.getType();

        JsonParser jsonParser = new JsonParser();
        JsonElement responseElement = jsonParser.parse(response.toString());

        return (LogoutResponse) gson.fromJson(responseElement.toString(), listType);
    }



    public ResponseMessage<BankListResponse> getBankList() {

        ResponseMessage<BankListResponse> responseMessage = null;

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

            URL url = new URL(Constants.SERVICE_URL + "/banks");

            connection = (HttpURLConnection) url.openConnection();
            connection.setDoOutput(true);
            connection.setConnectTimeout(5000);
            connection.setReadTimeout(5000);
            connection.setRequestMethod("POST");
            connection.setRequestProperty("Content-Type", "application/json");
            OutputStream outputStream = connection.getOutputStream();
            outputStream.write(jsonRequest.getBytes());
            outputStream.flush();

            InputStreamReader reader = new InputStreamReader(connection.getInputStream());
            Gson gson = new Gson();
            responseMessage = gson.fromJson(reader, new TypeToken<ResponseMessage<BankListResponse>>() {
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

        return responseMessage;

    }


    //Here

    public ResponseMessage<RegistrationEntryResponse>  registrationEntry(RegistrationEntryRequest registrationEntryRequest){

        ResponseMessage<RegistrationEntryResponse> registrationEntryResponse = null;
        HttpParams httpParameters = new BasicHttpParams();
        int timeoutConnection = 30000;
        HttpConnectionParams.setConnectionTimeout(httpParameters, timeoutConnection);
        int timeoutSocket = 30000;
        HttpConnectionParams.setSoTimeout(httpParameters, timeoutSocket);
        HttpClient httpClient = new DefaultHttpClient(httpParameters);
        HttpHost host = new HttpHost(Constants.SERVER_IP, Constants.SERVER_PORT);
        HttpRequest request = new HttpPost("/users/reg-entry");


        HttpResponse response;
        try {
            StringEntity entity = new StringEntity(createRegistrationEntry(registrationEntryRequest), "UTF-8");
            entity.setContentType("application/json");
            ((HttpPost) request).setEntity(entity);

            response = httpClient.execute(host, request);

            HttpEntity responseEntity = response.getEntity();
            StatusLine responseStatus = response.getStatusLine();
            int statusCode = responseStatus != null ? responseStatus.getStatusCode() : 0;
            String result = EntityUtils.toString(responseEntity);

            GsonBuilder builder = new GsonBuilder();
            builder.registerTypeAdapter(Date.class, new JsonDeserializer<Date>() {
                public Date deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
                    return new Date(json.getAsJsonPrimitive().getAsLong());
                }
            });
            Gson gson = builder.create();


            Type responseType = new com.google.gson.reflect.TypeToken<ResponseMessage<RegistrationEntryResponse>>() {}.getType();
            registrationEntryResponse = gson.fromJson(result, responseType);

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            httpClient.getConnectionManager().shutdown();
        }

        return registrationEntryResponse;

    }

    private String createRegistrationEntry(RegistrationEntryRequest registrationEntryRequest) {
        RequestHeader header = new RequestHeader();
        header.setAuthToken("008ewe");
        header.setVersion("1.0-PA");

        RequestMessage<RegistrationEntryRequest> message = new RequestMessage<RegistrationEntryRequest>();
        message.setRequestHeader(header);
        RegistrationEntryRequest request = registrationEntryRequest;
        request.setRequestUUID("1234");

        message.setService(request);

        Type requestType = new com.google.gson.reflect.TypeToken<RequestMessage<RegistrationEntryRequest>>() {}.getType();
        return new Gson().toJson(message, requestType);
    }


    public ResponseMessage<ContactUsResponse>  contactUsResponse(ContactUsRequest contactUsRequest){

        ResponseMessage<ContactUsResponse> contactUsResponseResponseMessage = null;
        HttpParams httpParameters = new BasicHttpParams();
        int timeoutConnection = 30000;
        HttpConnectionParams.setConnectionTimeout(httpParameters, timeoutConnection);
        int timeoutSocket = 30000;
        HttpConnectionParams.setSoTimeout(httpParameters, timeoutSocket);
        HttpClient httpClient = new DefaultHttpClient(httpParameters);
        HttpHost host = new HttpHost(Constants.SERVER_IP, Constants.SERVER_PORT);


        HttpRequest request = new HttpPost("/contactus");


        HttpResponse response;
        try {
            StringEntity entity = new StringEntity(createContactUsRequest(contactUsRequest), "UTF-8");
            entity.setContentType("application/json");
            ((HttpPost) request).setEntity(entity);

            response = httpClient.execute(host, request);

            HttpEntity responseEntity = response.getEntity();
            StatusLine responseStatus = response.getStatusLine();
            int statusCode = responseStatus != null ? responseStatus.getStatusCode() : 0;
            String result = EntityUtils.toString(responseEntity);

            GsonBuilder builder = new GsonBuilder();
            builder.registerTypeAdapter(Date.class, new JsonDeserializer<Date>() {
                public Date deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
                    return new Date(json.getAsJsonPrimitive().getAsLong());
                }
            });
            Gson gson = builder.create();


            Type responseType = new com.google.gson.reflect.TypeToken<ResponseMessage<ContactUsResponse>>() {}.getType();
            contactUsResponseResponseMessage = gson.fromJson(result, responseType);

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            httpClient.getConnectionManager().shutdown();
        }

        return contactUsResponseResponseMessage;

    }


    private String createContactUsRequest(ContactUsRequest contactUsRequest) {
        RequestHeader header = new RequestHeader();
        header.setAuthToken("008ewe");
        header.setVersion("1.0-PA");

        RequestMessage<ContactUsRequest> message = new RequestMessage<ContactUsRequest>();
        message.setRequestHeader(header);
        ContactUsRequest request = contactUsRequest;
        request.setRequestUUID("1234");

        message.setService(request);

        Type requestType = new com.google.gson.reflect.TypeToken<RequestMessage<ContactUsRequest>>() {}.getType();
        return new Gson().toJson(message, requestType);
    }



    public ResponseMessage<RegistrationSendSmsTokenResponse>  registrationSendSmsToken(RegistrationSendSmsTokenRequest registrationSendSmsTokenRequest){

        ResponseMessage<RegistrationSendSmsTokenResponse> registrationSendSmsTokenResponse = null;
        HttpParams httpParameters = new BasicHttpParams();
        int timeoutConnection = 30000;
        HttpConnectionParams.setConnectionTimeout(httpParameters, timeoutConnection);
        int timeoutSocket = 30000;
        HttpConnectionParams.setSoTimeout(httpParameters, timeoutSocket);
        HttpClient httpClient = new DefaultHttpClient(httpParameters);
        HttpHost host = new HttpHost(Constants.SERVER_IP, Constants.SERVER_PORT);
        HttpRequest request = new HttpPost("/users/reg-sms-token");


        HttpResponse response;
        try {
            StringEntity entity = new StringEntity(createRegistrationSendSmsToken(registrationSendSmsTokenRequest), "UTF-8");
            entity.setContentType("application/json");
            ((HttpPost) request).setEntity(entity);

            response = httpClient.execute(host, request);

            HttpEntity responseEntity = response.getEntity();
            StatusLine responseStatus = response.getStatusLine();
            int statusCode = responseStatus != null ? responseStatus.getStatusCode() : 0;
            String result = EntityUtils.toString(responseEntity);

            GsonBuilder builder = new GsonBuilder();
            builder.registerTypeAdapter(Date.class, new JsonDeserializer<Date>() {
                public Date deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
                    return new Date(json.getAsJsonPrimitive().getAsLong());
                }
            });
            Gson gson = builder.create();


            Type responseType = new com.google.gson.reflect.TypeToken<ResponseMessage<RegistrationSendSmsTokenResponse>>() {}.getType();
            registrationSendSmsTokenResponse = gson.fromJson(result, responseType);

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            httpClient.getConnectionManager().shutdown();
        }

        return registrationSendSmsTokenResponse;

    }


    private String createRegistrationSendSmsToken(RegistrationSendSmsTokenRequest registrationSendSmsTokenRequest) {
        RequestHeader header = new RequestHeader();
        header.setAuthToken("008ewe");
        header.setVersion("1.0-PA");

        RequestMessage<RegistrationSendSmsTokenRequest> message = new RequestMessage<RegistrationSendSmsTokenRequest>();
        message.setRequestHeader(header);
        RegistrationSendSmsTokenRequest request = registrationSendSmsTokenRequest;
        request.setRequestUUID("1234");

        message.setService(request);

        Type requestType = new com.google.gson.reflect.TypeToken<RequestMessage<RegistrationSendSmsTokenRequest>>() {}.getType();
        return new Gson().toJson(message, requestType);
    }



    public ResponseMessage<RegistrationVerifyMobileResponse>  registrationVerifyMobileResponse(RegistrationVerifyMobileRequest registrationVerifyMobileRequest){

        ResponseMessage<RegistrationVerifyMobileResponse> registrationVerifyMobileResponse = null;
        HttpParams httpParameters = new BasicHttpParams();
        int timeoutConnection = 30000;
        HttpConnectionParams.setConnectionTimeout(httpParameters, timeoutConnection);
        int timeoutSocket = 30000;
        HttpConnectionParams.setSoTimeout(httpParameters, timeoutSocket);
        HttpClient httpClient = new DefaultHttpClient(httpParameters);
        HttpHost host = new HttpHost(Constants.SERVER_IP, Constants.SERVER_PORT);

        HttpRequest request = new HttpPost("/users/reg-verify-mobile");

        HttpResponse response;
        try {
            StringEntity entity = new StringEntity(createRegistrationVerifyMobileResponse(registrationVerifyMobileRequest), "UTF-8");
            entity.setContentType("application/json");
            ((HttpPost) request).setEntity(entity);

            response = httpClient.execute(host, request);

            HttpEntity responseEntity = response.getEntity();
            StatusLine responseStatus = response.getStatusLine();
            int statusCode = responseStatus != null ? responseStatus.getStatusCode() : 0;
            String result = EntityUtils.toString(responseEntity);

            GsonBuilder builder = new GsonBuilder();
            builder.registerTypeAdapter(Date.class, new JsonDeserializer<Date>() {
                public Date deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
                    return new Date(json.getAsJsonPrimitive().getAsLong());
                }
            });
            Gson gson = builder.create();


            Type responseType = new com.google.gson.reflect.TypeToken<ResponseMessage<RegistrationVerifyMobileResponse>>() {}.getType();
            registrationVerifyMobileResponse = gson.fromJson(result, responseType);

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            httpClient.getConnectionManager().shutdown();
        }

        return registrationVerifyMobileResponse;

    }


    private String createRegistrationVerifyMobileResponse(RegistrationVerifyMobileRequest registrationVerifyMobileRequest) {
        RequestHeader header = new RequestHeader();
        header.setAuthToken("008ewe");
        header.setVersion("1.0-PA");

        RequestMessage<RegistrationVerifyMobileRequest> message = new RequestMessage<RegistrationVerifyMobileRequest>();
        message.setRequestHeader(header);
        RegistrationVerifyMobileRequest request = registrationVerifyMobileRequest;
        request.setRequestUUID("1234");

        message.setService(request);

        Type requestType = new com.google.gson.reflect.TypeToken<RequestMessage<RegistrationVerifyMobileRequest>>() {}.getType();
        return new Gson().toJson(message, requestType);
    }

    public ResponseMessage<RegistrationFetchUserDataResponse>  registrationFetchUserDataResponse(RegistrationFetchUserDataRequest registrationFetchUserDataRequest){

        ResponseMessage<RegistrationFetchUserDataResponse> registrationFetchUserDataResponse = null;
        HttpParams httpParameters = new BasicHttpParams();
        int timeoutConnection = 30000;
        HttpConnectionParams.setConnectionTimeout(httpParameters, timeoutConnection);
        int timeoutSocket = 30000;
        HttpConnectionParams.setSoTimeout(httpParameters, timeoutSocket);
        HttpClient httpClient = new DefaultHttpClient(httpParameters);
        HttpHost host = new HttpHost(Constants.SERVER_IP, Constants.SERVER_PORT);

        HttpRequest request = new HttpPost("/users/reg-fetch-user-data");

        HttpResponse response;
        try {
            StringEntity entity = new StringEntity(createregistrationFetchUserDataRequest(registrationFetchUserDataRequest), "UTF-8");
            entity.setContentType("application/json");
            ((HttpPost) request).setEntity(entity);

            response = httpClient.execute(host, request);

            HttpEntity responseEntity = response.getEntity();
            StatusLine responseStatus = response.getStatusLine();
            int statusCode = responseStatus != null ? responseStatus.getStatusCode() : 0;
            String result = EntityUtils.toString(responseEntity);

            GsonBuilder builder = new GsonBuilder();
            builder.registerTypeAdapter(Date.class, new JsonDeserializer<Date>() {
                public Date deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
                    return new Date(json.getAsJsonPrimitive().getAsLong());
                }
            });
            Gson gson = builder.create();


            Type responseType = new com.google.gson.reflect.TypeToken<ResponseMessage<RegistrationFetchUserDataResponse>>() {}.getType();
            registrationFetchUserDataResponse = gson.fromJson(result, responseType);

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            httpClient.getConnectionManager().shutdown();
        }

        return registrationFetchUserDataResponse;

    }

    private String createregistrationFetchUserDataRequest(RegistrationFetchUserDataRequest registrationFetchUserDataRequest) {
        RequestHeader header = new RequestHeader();
        header.setAuthToken("008ewe");
        header.setVersion("1.0-PA");

        RequestMessage<RegistrationFetchUserDataRequest> message = new RequestMessage<RegistrationFetchUserDataRequest>();
        message.setRequestHeader(header);
        RegistrationFetchUserDataRequest request = registrationFetchUserDataRequest;
        request.setRequestUUID("1234");

        message.setService(request);

        Type requestType = new com.google.gson.reflect.TypeToken<RequestMessage<RegistrationFetchUserDataRequest>>() {}.getType();
        return new Gson().toJson(message, requestType);
    }


    public ResponseMessage<RegistrationConfirmUserDataResponse>
        registrationConfirmUserDataResponse(RegistrationConfirmUserDataRequest registrationConfirmUserDataRequest){

        ResponseMessage<RegistrationConfirmUserDataResponse> registrationConfirmUserDataResponse = null;
        HttpParams httpParameters = new BasicHttpParams();
        int timeoutConnection = 30000;
        HttpConnectionParams.setConnectionTimeout(httpParameters, timeoutConnection);
        int timeoutSocket = 30000;
        HttpConnectionParams.setSoTimeout(httpParameters, timeoutSocket);
        HttpClient httpClient = new DefaultHttpClient(httpParameters);
        HttpHost host = new HttpHost(Constants.SERVER_IP, Constants.SERVER_PORT);

        HttpRequest request = new HttpPost("/users/reg-confirm-user-data");

        HttpResponse response;
        try {
            StringEntity entity = new StringEntity(createRegistrationConfirmUserDataRequest(registrationConfirmUserDataRequest), "UTF-8");
            entity.setContentType("application/json");
            ((HttpPost) request).setEntity(entity);

            response = httpClient.execute(host, request);

            HttpEntity responseEntity = response.getEntity();
            StatusLine responseStatus = response.getStatusLine();
            int statusCode = responseStatus != null ? responseStatus.getStatusCode() : 0;
            String result = EntityUtils.toString(responseEntity);

            GsonBuilder builder = new GsonBuilder();
            builder.registerTypeAdapter(Date.class, new JsonDeserializer<Date>() {
                public Date deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
                    return new Date(json.getAsJsonPrimitive().getAsLong());
                }
            });
            Gson gson = builder.create();


            Type responseType = new com.google.gson.reflect.TypeToken<ResponseMessage<RegistrationConfirmUserDataResponse>>() {}.getType();
            registrationConfirmUserDataResponse = gson.fromJson(result, responseType);

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            httpClient.getConnectionManager().shutdown();
        }

        return registrationConfirmUserDataResponse;

    }

    private String createRegistrationConfirmUserDataRequest(RegistrationConfirmUserDataRequest registrationConfirmUserDataRequest) {
        RequestHeader header = new RequestHeader();
        header.setAuthToken("008ewe");
        header.setVersion("1.0-PA");

        RequestMessage<RegistrationConfirmUserDataRequest> message = new RequestMessage<RegistrationConfirmUserDataRequest>();
        message.setRequestHeader(header);
        RegistrationConfirmUserDataRequest request = registrationConfirmUserDataRequest;
        request.setRequestUUID("1234");



        message.setService(request);

        Type requestType = new com.google.gson.reflect.TypeToken<RequestMessage<RegistrationConfirmUserDataRequest>>() {}.getType();
        return new Gson().toJson(message, requestType);
    }

    public ResponseMessage<RegistrationPassCodeEntryResponse>
    registrationPassCodeEntryResponse(RegistrationPassCodeEntryRequest registrationPassCodeEntryRequest){

        ResponseMessage<RegistrationPassCodeEntryResponse> registrationPassCodeEntryResponse = null;
        HttpParams httpParameters = new BasicHttpParams();
        int timeoutConnection = 30000;
        HttpConnectionParams.setConnectionTimeout(httpParameters, timeoutConnection);
        int timeoutSocket = 30000;
        HttpConnectionParams.setSoTimeout(httpParameters, timeoutSocket);
        HttpClient httpClient = new DefaultHttpClient(httpParameters);
        HttpHost host = new HttpHost(Constants.SERVER_IP, Constants.SERVER_PORT);

        HttpRequest request = new HttpPost("/users/reg-pass-code-entry");

        HttpResponse response;
        try {
            StringEntity entity = new StringEntity(createRegistrationPassCodeEntryRequest(registrationPassCodeEntryRequest), "UTF-8");
            entity.setContentType("application/json");
            ((HttpPost) request).setEntity(entity);

            response = httpClient.execute(host, request);

            HttpEntity responseEntity = response.getEntity();
            StatusLine responseStatus = response.getStatusLine();
            int statusCode = responseStatus != null ? responseStatus.getStatusCode() : 0;
            String result = EntityUtils.toString(responseEntity);

            GsonBuilder builder = new GsonBuilder();
            builder.registerTypeAdapter(Date.class, new JsonDeserializer<Date>() {
                public Date deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
                    return new Date(json.getAsJsonPrimitive().getAsLong());
                }
            });
            Gson gson = builder.create();


            Type responseType = new com.google.gson.reflect.TypeToken<ResponseMessage<RegistrationPassCodeEntryResponse>>() {}.getType();
            registrationPassCodeEntryResponse = gson.fromJson(result, responseType);

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            httpClient.getConnectionManager().shutdown();
        }

        return registrationPassCodeEntryResponse;

    }

    private String createRegistrationPassCodeEntryRequest(RegistrationPassCodeEntryRequest registrationPassCodeEntryRequest) {
        RequestHeader header = new RequestHeader();
        header.setAuthToken("008ewe");
        header.setVersion("1.0-PA");

        RequestMessage<RegistrationPassCodeEntryRequest> message = new RequestMessage<RegistrationPassCodeEntryRequest>();
        message.setRequestHeader(header);
        RegistrationPassCodeEntryRequest request = registrationPassCodeEntryRequest;
        request.setRequestUUID("1234");


        message.setService(request);

        Type requestType = new com.google.gson.reflect.TypeToken<RequestMessage<RegistrationPassCodeEntryRequest>>() {}.getType();
        return new Gson().toJson(message, requestType);
    }


    public ResponseMessage<ChangePassCodeResponse> changePassCodeResponse(ChangePassCodeRequest changePassCodeRequest) {

        ResponseMessage<ChangePassCodeResponse> responseMessage = null;

        HttpURLConnection connection = null;
        try {

            RequestHeader header = new RequestHeader();
            header.setVersion("1.0-PA");
            header.setAuthToken(prefs.getString(Constants.LOGIN_TOKEN_ID, ""));

            RequestMessage<ChangePassCodeRequest> message = new RequestMessage<ChangePassCodeRequest>();
            message.setRequestHeader(header);
            ChangePassCodeRequest request = changePassCodeRequest;
            request.setRequestUUID("1234");

            message.setService(request);

            Type requestType = new com.google.gson.reflect.TypeToken<RequestMessage<ChangePassCodeRequest>>() {}.getType();
            String jsonRequest = new Gson().toJson(message, requestType);

            URL url = new URL(Constants.SERVICE_URL + "/users/passcode");

            connection = (HttpURLConnection) url.openConnection();
            connection.setDoOutput(true);
            connection.setRequestMethod("PUT");
            connection.setRequestProperty("Content-Type", "application/json");
            OutputStream outputStream = connection.getOutputStream();
            outputStream.write(jsonRequest.getBytes());
            outputStream.flush();

            InputStreamReader reader = new InputStreamReader(connection.getInputStream());
            Gson gson = new Gson();
            responseMessage = gson.fromJson(reader, new TypeToken<ResponseMessage<ChangePassCodeResponse>>() {
            }.getType());

            if( responseMessage != null && responseMessage.getService() != null ) {
                System.out.println("Response requestUUID : " + responseMessage.getService().getRequestUUID());
                System.out.println("Response bank list size : " + responseMessage.getService());
            } else {
                System.out.println("Response is null...");
            }

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (connection != null)
                connection.disconnect();
        }

        return responseMessage;

    }





//    private String createChangePassCodeRequest(ChangePassCodeRequest changePassCodeRequest) {
//        RequestHeader header = new RequestHeader();
//        header.setAuthToken("008ewe");
//        header.setVersion("1.0-PA");
//
//        RequestMessage<ChangePassCodeRequest> message = new RequestMessage<ChangePassCodeRequest>();
//        message.setRequestHeader(header);
//        ChangePassCodeRequest request = changePassCodeRequest;
//        request.setRequestUUID("1234");
//
//
//        message.setService(request);
//
//        Type requestType = new com.google.gson.reflect.TypeToken<RequestMessage<ChangePassCodeRequest>>() {}.getType();
//        return new Gson().toJson(message, requestType);
//    }


    public ResponseMessage<ChangeMemorableWordResponse> changeMemorableWordResponse(ChangeMemorableWordRequest changeMemorableWordRequest) {

        ResponseMessage<ChangeMemorableWordResponse> responseMessage = null;

        HttpURLConnection connection = null;
        try {

            RequestHeader header = new RequestHeader();
            header.setAuthToken(prefs.getString(Constants.LOGIN_TOKEN_ID, ""));

            header.setVersion("1.0-PA");

            RequestMessage<ChangeMemorableWordRequest> message = new RequestMessage<ChangeMemorableWordRequest>();
            message.setRequestHeader(header);
            ChangeMemorableWordRequest request = changeMemorableWordRequest;
            request.setRequestUUID("1234");
            message.setService(request);

            Type requestType = new com.google.gson.reflect.TypeToken<RequestMessage<ChangeMemorableWordRequest>>() {}.getType();
            String jsonRequest = new Gson().toJson(message, requestType);


            URL url = new URL(Constants.SERVICE_URL + "/users/memorable-word");

            connection = (HttpURLConnection) url.openConnection();
            connection.setDoOutput(true);
            connection.setRequestMethod("PUT");
            connection.setRequestProperty("Content-Type", "application/json");
            OutputStream outputStream = connection.getOutputStream();
            outputStream.write(jsonRequest.getBytes());
            outputStream.flush();

            InputStreamReader reader = new InputStreamReader(connection.getInputStream());
            Gson gson = new Gson();
            responseMessage = gson.fromJson(reader, new TypeToken<ResponseMessage<ChangeMemorableWordResponse>>() {
            }.getType());

            if( responseMessage != null && responseMessage.getService() != null ) {
                System.out.println("Response requestUUID : " + responseMessage.getService().getRequestUUID());
                System.out.println("Response bank list size : " + responseMessage.getService());
            } else {
                System.out.println("Response is null...");
            }

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (connection != null)
                connection.disconnect();
        }

        return responseMessage;

    }


    public ResponseMessage<RegistrationMemorableWordEntryResponse>
    registrationMemorableWordEntryResponse(RegistrationMemorableWordEntryRequest registrationMemorableWordEntryRequest){

        ResponseMessage<RegistrationMemorableWordEntryResponse> registrationMemorableWordEntryResponse = null;
        HttpParams httpParameters = new BasicHttpParams();
        int timeoutConnection = 30000;
        HttpConnectionParams.setConnectionTimeout(httpParameters, timeoutConnection);
        int timeoutSocket = 30000;
        HttpConnectionParams.setSoTimeout(httpParameters, timeoutSocket);
        HttpClient httpClient = new DefaultHttpClient(httpParameters);
        HttpHost host = new HttpHost(Constants.SERVER_IP, Constants.SERVER_PORT);

        HttpRequest request = new HttpPost("/users/reg-memorable-word-entry");

        HttpResponse response;
        try {
            StringEntity entity = new StringEntity(createRegistrationMemorableWordEntryRequest(registrationMemorableWordEntryRequest), "UTF-8");
            entity.setContentType("application/json");
            ((HttpPost) request).setEntity(entity);

            response = httpClient.execute(host, request);

            HttpEntity responseEntity = response.getEntity();
            StatusLine responseStatus = response.getStatusLine();
            int statusCode = responseStatus != null ? responseStatus.getStatusCode() : 0;
            String result = EntityUtils.toString(responseEntity);

            GsonBuilder builder = new GsonBuilder();
            builder.registerTypeAdapter(Date.class, new JsonDeserializer<Date>() {
                public Date deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
                    return new Date(json.getAsJsonPrimitive().getAsLong());
                }
            });
            Gson gson = builder.create();


            Type responseType = new com.google.gson.reflect.TypeToken<ResponseMessage<RegistrationMemorableWordEntryResponse>>() {}.getType();
            registrationMemorableWordEntryResponse = gson.fromJson(result, responseType);

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            httpClient.getConnectionManager().shutdown();
        }

        return registrationMemorableWordEntryResponse;

    }

    private String createRegistrationMemorableWordEntryRequest(RegistrationMemorableWordEntryRequest registrationMemorableWordEntryRequest) {
        RequestHeader header = new RequestHeader();
        header.setAuthToken("008ewe");
        header.setVersion("1.0-PA");

        RequestMessage<RegistrationMemorableWordEntryRequest> message = new RequestMessage<RegistrationMemorableWordEntryRequest>();
        message.setRequestHeader(header);
        RegistrationMemorableWordEntryRequest request = registrationMemorableWordEntryRequest;
        request.setRequestUUID("1234");


        message.setService(request);

        Type requestType = new com.google.gson.reflect.TypeToken<RequestMessage<RegistrationMemorableWordEntryRequest>>() {}.getType();
        return new Gson().toJson(message, requestType);
    }

    public ResponseMessage<TACResponse> tACResponse(TACRequest tacRequest){
        ResponseMessage<TACResponse> tACResponse = null;
        HttpParams httpParameters = new BasicHttpParams();
        int timeoutConnection = 30000;
        HttpConnectionParams.setConnectionTimeout(httpParameters, timeoutConnection);
        int timeoutSocket = 30000;
        HttpConnectionParams.setSoTimeout(httpParameters, timeoutSocket);
        HttpClient httpClient = new DefaultHttpClient(httpParameters);
        HttpHost host = new HttpHost(Constants.SERVER_IP, Constants.SERVER_PORT);
        HttpRequest request = new HttpPost("/users/tac");
        HttpResponse response;
        try {
            StringEntity entity = new StringEntity(createTACResponse(tacRequest), "UTF-8");
            entity.setContentType("application/json");
            ((HttpPost) request).setEntity(entity);
            response = httpClient.execute(host, request);
            HttpEntity responseEntity = response.getEntity();
            StatusLine responseStatus = response.getStatusLine();
            int statusCode = responseStatus != null ? responseStatus.getStatusCode() : 0;
            String result = EntityUtils.toString(responseEntity);
            GsonBuilder builder = new GsonBuilder();
            builder.registerTypeAdapter(Date.class, new JsonDeserializer<Date>() {
                public Date deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
                    return new Date(json.getAsJsonPrimitive().getAsLong());
                }
            });
            Gson gson = builder.create();
            Type responseType = new com.google.gson.reflect.TypeToken<ResponseMessage<TACResponse>>() {}.getType();
            tACResponse = gson.fromJson(result, responseType);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            httpClient.getConnectionManager().shutdown();
        }
        return tACResponse;
    }

    private String createTACResponse(TACRequest tacRequest) {
        RequestHeader header = new RequestHeader();
//        header.setAuthToken("008ewe");
        header.setAuthToken(prefs.getString(Constants.LOGIN_TOKEN_ID, ""));
        header.setVersion("1.0-PA");
        RequestMessage<TACRequest> message = new RequestMessage<TACRequest>();
        message.setRequestHeader(header);
        TACRequest request = tacRequest;
        request.setRequestUUID("1234");
        message.setService(request);
        Type requestType = new com.google.gson.reflect.TypeToken<RequestMessage<TACRequest>>() {}.getType();
        return new Gson().toJson(message, requestType);
    }


    public ResponseMessage<TACAcceptResponse> tACAcceptResponse(TACAcceptRequest tacAcceptRequest){
        ResponseMessage<TACAcceptResponse> tACAcceptResponse = null;
        HttpParams httpParameters = new BasicHttpParams();
        int timeoutConnection = 30000;
        HttpConnectionParams.setConnectionTimeout(httpParameters, timeoutConnection);
        int timeoutSocket = 30000;
        HttpConnectionParams.setSoTimeout(httpParameters, timeoutSocket);
        HttpClient httpClient = new DefaultHttpClient(httpParameters);
        HttpHost host = new HttpHost(Constants.SERVER_IP, Constants.SERVER_PORT);
        HttpRequest request = new HttpPost("/users/tacaccept");
        HttpResponse response;
        try {
            StringEntity entity = new StringEntity(createTACAcceptResponse(tacAcceptRequest), "UTF-8");
            entity.setContentType("application/json");
            ((HttpPost) request).setEntity(entity);
            response = httpClient.execute(host, request);
            HttpEntity responseEntity = response.getEntity();
            StatusLine responseStatus = response.getStatusLine();
            int statusCode = responseStatus != null ? responseStatus.getStatusCode() : 0;
            String result = EntityUtils.toString(responseEntity);
            GsonBuilder builder = new GsonBuilder();
            builder.registerTypeAdapter(Date.class, new JsonDeserializer<Date>() {
                public Date deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
                    return new Date(json.getAsJsonPrimitive().getAsLong());
                }
            });
            Gson gson = builder.create();
            Type responseType = new com.google.gson.reflect.TypeToken<ResponseMessage<TACAcceptResponse>>() {}.getType();
            tACAcceptResponse = gson.fromJson(result, responseType);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            httpClient.getConnectionManager().shutdown();
        }
        return tACAcceptResponse;
    }

    private String createTACAcceptResponse(TACAcceptRequest tACAcceptRequest) {
        RequestHeader header = new RequestHeader();
//        header.setAuthToken("008ewe");
        header.setAuthToken(prefs.getString(Constants.LOGIN_TOKEN_ID, ""));
        header.setVersion("1.0-PA");
        RequestMessage<TACAcceptRequest> message = new RequestMessage<TACAcceptRequest>();
        message.setRequestHeader(header);
        TACAcceptRequest request = tACAcceptRequest;
        request.setRequestUUID("1234");
        message.setService(request);
        Type requestType = new com.google.gson.reflect.TypeToken<RequestMessage<TACAcceptRequest>>() {}.getType();
        return new Gson().toJson(message, requestType);
    }

    public ResponseMessage<VerifyAccountResponse> verifyAccountResponse(VerifyAccountRequest verifyAccountRequest){
        ResponseMessage<VerifyAccountResponse> verifyAccountResponse = null;
        HttpParams httpParameters = new BasicHttpParams();
        int timeoutConnection = 30000;
        HttpConnectionParams.setConnectionTimeout(httpParameters, timeoutConnection);
        int timeoutSocket = 30000;
        HttpConnectionParams.setSoTimeout(httpParameters, timeoutSocket);
        HttpClient httpClient = new DefaultHttpClient(httpParameters);
        HttpHost host = new HttpHost(Constants.SERVER_IP, Constants.SERVER_PORT);
        HttpRequest request = new HttpPost("/customers/verify-account");
        HttpResponse response;
        try {
            StringEntity entity = new StringEntity(createVerifyAccountRequest(verifyAccountRequest), "UTF-8");
            entity.setContentType("application/json");
            ((HttpPost) request).setEntity(entity);
            response = httpClient.execute(host, request);
            HttpEntity responseEntity = response.getEntity();
            StatusLine responseStatus = response.getStatusLine();
            int statusCode = responseStatus != null ? responseStatus.getStatusCode() : 0;
            String result = EntityUtils.toString(responseEntity);
            GsonBuilder builder = new GsonBuilder();
            builder.registerTypeAdapter(Date.class, new JsonDeserializer<Date>() {
                public Date deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
                    return new Date(json.getAsJsonPrimitive().getAsLong());
                }
            });
            Gson gson = builder.create();
            Type responseType = new com.google.gson.reflect.TypeToken<ResponseMessage<VerifyAccountResponse>>() {}.getType();
            verifyAccountResponse = gson.fromJson(result, responseType);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            httpClient.getConnectionManager().shutdown();
        }
        return verifyAccountResponse;
    }


    private String createVerifyAccountRequest(VerifyAccountRequest verifyAccountRequest) {
        RequestHeader header = new RequestHeader();
//        header.setAuthToken("008ewe");
        header.setAuthToken(prefs.getString(Constants.LOGIN_TOKEN_ID, ""));
        header.setVersion("1.0-PA");
        RequestMessage<VerifyAccountRequest> message = new RequestMessage<VerifyAccountRequest>();
        message.setRequestHeader(header);
        VerifyAccountRequest request = verifyAccountRequest;
        request.setRequestUUID("1234");
        message.setService(request);
        Type requestType = new com.google.gson.reflect.TypeToken<RequestMessage<VerifyAccountRequest>>() {}.getType();
        return new Gson().toJson(message, requestType);
    }



    public ResponseMessage<VerifyTransferMoneyResponse> verifyTransferMoneyResponse(VerifyTransferMoneyRequest verifyTransferMoneyRequest){
        ResponseMessage<VerifyTransferMoneyResponse> verifyTransferMoneyResponse = null;
        HttpParams httpParameters = new BasicHttpParams();
        int timeoutConnection = 30000;
        HttpConnectionParams.setConnectionTimeout(httpParameters, timeoutConnection);
        int timeoutSocket = 30000;
        HttpConnectionParams.setSoTimeout(httpParameters, timeoutSocket);
        HttpClient httpClient = new DefaultHttpClient(httpParameters);
        HttpHost host = new HttpHost(Constants.SERVER_IP, Constants.SERVER_PORT);
        HttpRequest request = new HttpPost("/customers/verify-xfer");
        HttpResponse response;
        try {
            StringEntity entity = new StringEntity(createVerifyTransferMoneyRequest(verifyTransferMoneyRequest), "UTF-8");
            entity.setContentType("application/json");
            ((HttpPost) request).setEntity(entity);
            response = httpClient.execute(host, request);
            HttpEntity responseEntity = response.getEntity();
            StatusLine responseStatus = response.getStatusLine();
            int statusCode = responseStatus != null ? responseStatus.getStatusCode() : 0;
            String result = EntityUtils.toString(responseEntity);
            GsonBuilder builder = new GsonBuilder();
            builder.registerTypeAdapter(Date.class, new JsonDeserializer<Date>() {
                public Date deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
                    return new Date(json.getAsJsonPrimitive().getAsLong());
                }
            });
            Gson gson = builder.create();
            Type responseType = new com.google.gson.reflect.TypeToken<ResponseMessage<VerifyTransferMoneyResponse>>() {}.getType();
            verifyTransferMoneyResponse = gson.fromJson(result, responseType);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            httpClient.getConnectionManager().shutdown();
        }
        return verifyTransferMoneyResponse;
    }


    private String createVerifyTransferMoneyRequest(VerifyTransferMoneyRequest verifyTransferMoneyRequest) {
        RequestHeader header = new RequestHeader();
//        header.setAuthToken("008ewe");
        header.setAuthToken(prefs.getString(Constants.LOGIN_TOKEN_ID, ""));
        header.setVersion("1.0-PA");
        RequestMessage<VerifyTransferMoneyRequest> message = new RequestMessage<VerifyTransferMoneyRequest>();
        message.setRequestHeader(header);
        VerifyTransferMoneyRequest request = verifyTransferMoneyRequest;
        request.setRequestUUID("1234");
        message.setService(request);
        Type requestType = new com.google.gson.reflect.TypeToken<RequestMessage<VerifyTransferMoneyRequest>>() {}.getType();
        return new Gson().toJson(message, requestType);
    }



    public ResponseMessage<RegistrationVerifyAccountResponse> registrationVerifyAccountResponse(RegistrationVerifyAccountRequest registrationVerifyAccountRequest){
        ResponseMessage<RegistrationVerifyAccountResponse> registrationVerifyAccountResponse = null;
        HttpParams httpParameters = new BasicHttpParams();
        int timeoutConnection = 30000;
        HttpConnectionParams.setConnectionTimeout(httpParameters, timeoutConnection);
        int timeoutSocket = 30000;
        HttpConnectionParams.setSoTimeout(httpParameters, timeoutSocket);
        HttpClient httpClient = new DefaultHttpClient(httpParameters);
        HttpHost host = new HttpHost(Constants.SERVER_IP, Constants.SERVER_PORT);
        HttpRequest request = new HttpPost("/customers/reg-verify-account");
        HttpResponse response;
        try {
            StringEntity entity = new StringEntity(createRegistrationVerifyAccountRequest(registrationVerifyAccountRequest), "UTF-8");
            entity.setContentType("application/json");
            ((HttpPost) request).setEntity(entity);
            response = httpClient.execute(host, request);
            HttpEntity responseEntity = response.getEntity();
            StatusLine responseStatus = response.getStatusLine();
            int statusCode = responseStatus != null ? responseStatus.getStatusCode() : 0;
            String result = EntityUtils.toString(responseEntity);
            GsonBuilder builder = new GsonBuilder();
            builder.registerTypeAdapter(Date.class, new JsonDeserializer<Date>() {
                public Date deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
                    return new Date(json.getAsJsonPrimitive().getAsLong());
                }
            });
            Gson gson = builder.create();
            Type responseType = new com.google.gson.reflect.TypeToken<ResponseMessage<RegistrationVerifyAccountResponse>>() {}.getType();
            registrationVerifyAccountResponse = gson.fromJson(result, responseType);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            httpClient.getConnectionManager().shutdown();
        }
        return registrationVerifyAccountResponse;
    }

    private String createRegistrationVerifyAccountRequest(RegistrationVerifyAccountRequest registrationVerifyAccountRequest) {
        RequestHeader header = new RequestHeader();
//        header.setAuthToken("008ewe");
        header.setAuthToken(prefs.getString(Constants.LOGIN_TOKEN_ID, ""));
        header.setVersion("1.0-PA");
        RequestMessage<RegistrationVerifyAccountRequest> message = new RequestMessage<RegistrationVerifyAccountRequest>();
        message.setRequestHeader(header);
        RegistrationVerifyAccountRequest request = registrationVerifyAccountRequest;
        request.setRequestUUID("1234");
        message.setService(request);
        Type requestType = new com.google.gson.reflect.TypeToken<RequestMessage<RegistrationVerifyAccountRequest>>() {}.getType();
        return new Gson().toJson(message, requestType);
    }



    public ResponseMessage<RegistrationVerifyTransferMoneyResponse> registrationVerifyTransferMoneyResponse(RegistrationVerifyTransferMoneyRequest registrationVerifyTransferMoneyRequest){
        ResponseMessage<RegistrationVerifyTransferMoneyResponse> registrationVerifyTransferMoneyResponse = null;
        HttpParams httpParameters = new BasicHttpParams();
        int timeoutConnection = 30000;
        HttpConnectionParams.setConnectionTimeout(httpParameters, timeoutConnection);
        int timeoutSocket = 30000;
        HttpConnectionParams.setSoTimeout(httpParameters, timeoutSocket);
        HttpClient httpClient = new DefaultHttpClient(httpParameters);
        HttpHost host = new HttpHost(Constants.SERVER_IP, Constants.SERVER_PORT);
        HttpRequest request = new HttpPost("/customers/reg-verify-xfer");
        HttpResponse response;
        try {
            StringEntity entity = new StringEntity(createRegistrationVerifyTransferMoneyResponse(registrationVerifyTransferMoneyRequest), "UTF-8");
            entity.setContentType("application/json");
            ((HttpPost) request).setEntity(entity);
            response = httpClient.execute(host, request);
            HttpEntity responseEntity = response.getEntity();
            StatusLine responseStatus = response.getStatusLine();
            int statusCode = responseStatus != null ? responseStatus.getStatusCode() : 0;
            String result = EntityUtils.toString(responseEntity);
            GsonBuilder builder = new GsonBuilder();
            builder.registerTypeAdapter(Date.class, new JsonDeserializer<Date>() {
                public Date deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
                    return new Date(json.getAsJsonPrimitive().getAsLong());
                }
            });
            Gson gson = builder.create();
            Type responseType = new com.google.gson.reflect.TypeToken<ResponseMessage<RegistrationVerifyTransferMoneyResponse>>() {}.getType();
            registrationVerifyTransferMoneyResponse = gson.fromJson(result, responseType);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            httpClient.getConnectionManager().shutdown();
        }
        return registrationVerifyTransferMoneyResponse;
    }


    private String createRegistrationVerifyTransferMoneyResponse(RegistrationVerifyTransferMoneyRequest registrationVerifyTransferMoneyRequest) {
        RequestHeader header = new RequestHeader();
//        header.setAuthToken("008ewe");
        header.setAuthToken(prefs.getString(Constants.LOGIN_TOKEN_ID, ""));
        header.setVersion("1.0-PA");
        RequestMessage<RegistrationVerifyTransferMoneyRequest> message = new RequestMessage<RegistrationVerifyTransferMoneyRequest>();
        message.setRequestHeader(header);
        RegistrationVerifyTransferMoneyRequest request = registrationVerifyTransferMoneyRequest;
        request.setRequestUUID("1234");
        message.setService(request);
        Type requestType = new com.google.gson.reflect.TypeToken<RequestMessage<RegistrationVerifyTransferMoneyRequest>>() {}.getType();
        return new Gson().toJson(message, requestType);
    }



    public ResponseMessage<UserProfileResponse>  getUserProfile(){

        ResponseMessage<UserProfileResponse> userProfileResponse = null;
        HttpParams httpParameters = new BasicHttpParams();
        int timeoutConnection = 30000;
        HttpConnectionParams.setConnectionTimeout(httpParameters, timeoutConnection);
        int timeoutSocket = 30000;
        HttpConnectionParams.setSoTimeout(httpParameters, timeoutSocket);
        HttpClient httpClient = new DefaultHttpClient(httpParameters);
        HttpHost host = new HttpHost(Constants.SERVER_IP, Constants.SERVER_PORT);
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

    public ResponseMessage<ContactsHampayEnabledResponse>  getEnabledHamPayContacts(ContactsHampayEnabledRequest contactsHampayEnabledRequest){

        ResponseMessage<ContactsHampayEnabledResponse> contactsHampayEnabledResponse = null;

        HttpParams httpParameters = new BasicHttpParams();

        int timeoutConnection = 30000;
        HttpConnectionParams.setConnectionTimeout(httpParameters, timeoutConnection);
        int timeoutSocket = 30000;
        HttpConnectionParams.setSoTimeout(httpParameters, timeoutSocket);

        HttpClient httpClient = new DefaultHttpClient(httpParameters);

        HttpHost host = new HttpHost(Constants.SERVER_IP, Constants.SERVER_PORT);
        HttpRequest request = new HttpPost("/customer/contacts/hp-enabled");

        HttpResponse response;
        try {
            StringEntity entity = new StringEntity(createHamPayContactsJsonMessage(), "UTF-8");
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

            Type responseType = new com.google.gson.reflect.TypeToken<ResponseMessage<ContactsHampayEnabledResponse>>() {}.getType();
            contactsHampayEnabledResponse = gson.fromJson(result, responseType);


        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            httpClient.getConnectionManager().shutdown();
        }

        return contactsHampayEnabledResponse;

    }

    public ResponseMessage<IndividualPaymentConfirmResponse>  individualPaymentConfirm(IndividualPaymentConfirmRequest individualPaymentConfirmRequest){

        ResponseMessage<IndividualPaymentConfirmResponse> individualPaymentConfirmResponse = null;

        HttpParams httpParameters = new BasicHttpParams();

        int timeoutConnection = 30000;
        HttpConnectionParams.setConnectionTimeout(httpParameters, timeoutConnection);
        int timeoutSocket = 30000;
        HttpConnectionParams.setSoTimeout(httpParameters, timeoutSocket);

        HttpClient httpClient = new DefaultHttpClient(httpParameters);

        HttpHost host = new HttpHost(Constants.SERVER_IP, Constants.SERVER_PORT);
        HttpRequest request = new HttpPost("/customers/individual-payment-confirm");

        HttpResponse response;
        try {
            StringEntity entity = new StringEntity(createIndividualConfirmJsonMessage(individualPaymentConfirmRequest), "UTF-8");
            entity.setContentType("application/json");
            ((HttpPost) request).setEntity(entity);

            response = httpClient.execute(host, request);

            HttpEntity responseEntity = response.getEntity();
            StatusLine responseStatus = response.getStatusLine();
            int statusCode = responseStatus != null ? responseStatus.getStatusCode() : 0;
            String result = EntityUtils.toString(responseEntity);

            GsonBuilder builder = new GsonBuilder();
            builder.registerTypeAdapter(Date.class, new JsonDeserializer<Date>() {
                public Date deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
                    return new Date(json.getAsJsonPrimitive().getAsLong());
                }
            });
            Gson gson = builder.create();

            Type responseType = new com.google.gson.reflect.TypeToken<ResponseMessage<IndividualPaymentConfirmResponse>>() {}.getType();
            individualPaymentConfirmResponse = gson.fromJson(result, responseType);

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            httpClient.getConnectionManager().shutdown();
        }

        return individualPaymentConfirmResponse;

    }

    public ResponseMessage<IndividualPaymentResponse>  individualPayment(IndividualPaymentRequest individualPaymentRequest){

        ResponseMessage<IndividualPaymentResponse> individualPaymentResponse = null;

        HttpParams httpParameters = new BasicHttpParams();

        int timeoutConnection = 30000;
        HttpConnectionParams.setConnectionTimeout(httpParameters, timeoutConnection);
        int timeoutSocket = 30000;
        HttpConnectionParams.setSoTimeout(httpParameters, timeoutSocket);

        HttpClient httpClient = new DefaultHttpClient(httpParameters);

        HttpHost host = new HttpHost(Constants.SERVER_IP, Constants.SERVER_PORT);
        HttpRequest request = new HttpPost("/customers/individual-payment");

        HttpResponse response;
        try {

            StringEntity entity = new StringEntity(createIndividualJsonMessage(individualPaymentRequest), "UTF-8");
            entity.setContentType("application/json");
            ((HttpPost) request).setEntity(entity);

            response = httpClient.execute(host, request);

            HttpEntity responseEntity = response.getEntity();
            StatusLine responseStatus = response.getStatusLine();
            int statusCode = responseStatus != null ? responseStatus.getStatusCode() : 0;
            String result = EntityUtils.toString(responseEntity);

            GsonBuilder builder = new GsonBuilder();
            builder.registerTypeAdapter(Date.class, new JsonDeserializer<Date>() {
                public Date deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
                    return new Date(json.getAsJsonPrimitive().getAsLong());
                }
            });
            Gson gson = builder.create();

            Type responseType = new com.google.gson.reflect.TypeToken<ResponseMessage<IndividualPaymentResponse>>() {}.getType();
            individualPaymentResponse = gson.fromJson(result, responseType);


        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            httpClient.getConnectionManager().shutdown();
        }

        return individualPaymentResponse;

    }



    public ResponseMessage<BusinessPaymentConfirmResponse>  businessPaymentConfirm(BusinessPaymentConfirmRequest businessPaymentConfirmRequest){

        ResponseMessage<BusinessPaymentConfirmResponse> businessPaymentConfirmResponse = null;

        HttpParams httpParameters = new BasicHttpParams();

        int timeoutConnection = 30000;
        HttpConnectionParams.setConnectionTimeout(httpParameters, timeoutConnection);
        int timeoutSocket = 30000;
        HttpConnectionParams.setSoTimeout(httpParameters, timeoutSocket);

        HttpClient httpClient = new DefaultHttpClient(httpParameters);

        HttpHost host = new HttpHost(Constants.SERVER_IP, Constants.SERVER_PORT);
        HttpRequest request = new HttpPost("/businesses/business-payment-confirm");

        HttpResponse response;
        try {

            StringEntity entity = new StringEntity(createBusinessConfirmJsonMessage(businessPaymentConfirmRequest), "UTF-8");
            entity.setContentType("application/json");
            ((HttpPost) request).setEntity(entity);

            response = httpClient.execute(host, request);

            HttpEntity responseEntity = response.getEntity();
            StatusLine responseStatus = response.getStatusLine();
            int statusCode = responseStatus != null ? responseStatus.getStatusCode() : 0;
            String result = EntityUtils.toString(responseEntity);

            GsonBuilder builder = new GsonBuilder();
            builder.registerTypeAdapter(Date.class, new JsonDeserializer<Date>() {
                public Date deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
                    return new Date(json.getAsJsonPrimitive().getAsLong());
                }
            });
            Gson gson = builder.create();

            Type responseType = new com.google.gson.reflect.TypeToken<ResponseMessage<BusinessPaymentConfirmResponse>>() {}.getType();
            businessPaymentConfirmResponse = gson.fromJson(result, responseType);

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            httpClient.getConnectionManager().shutdown();
        }



        return businessPaymentConfirmResponse;

    }

    public ResponseMessage<BusinessPaymentResponse>  businessPayment(BusinessPaymentRequest businessPaymentRequest){

        ResponseMessage<BusinessPaymentResponse> businessPaymentResponse = null;

        HttpParams httpParameters = new BasicHttpParams();

        int timeoutConnection = 30000;
        HttpConnectionParams.setConnectionTimeout(httpParameters, timeoutConnection);
        int timeoutSocket = 30000;
        HttpConnectionParams.setSoTimeout(httpParameters, timeoutSocket);

        HttpClient httpClient = new DefaultHttpClient(httpParameters);

        HttpHost host = new HttpHost(Constants.SERVER_IP, Constants.SERVER_PORT);
        HttpRequest request = new HttpPost("/customers/business-payment");

        HttpResponse response;
        try {

            StringEntity entity = new StringEntity(createBusinessJsonMessage(businessPaymentRequest), "UTF-8");
            entity.setContentType("application/json");
            ((HttpPost) request).setEntity(entity);

            response = httpClient.execute(host, request);

            HttpEntity responseEntity = response.getEntity();
            StatusLine responseStatus = response.getStatusLine();
            int statusCode = responseStatus != null ? responseStatus.getStatusCode() : 0;
            String result = EntityUtils.toString(responseEntity);

            GsonBuilder builder = new GsonBuilder();
            builder.registerTypeAdapter(Date.class, new JsonDeserializer<Date>() {
                public Date deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
                    return new Date(json.getAsJsonPrimitive().getAsLong());
                }
            });
            Gson gson = builder.create();

            Type responseType = new com.google.gson.reflect.TypeToken<ResponseMessage<BusinessPaymentResponse>>() {}.getType();
            businessPaymentResponse = gson.fromJson(result, responseType);


        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            httpClient.getConnectionManager().shutdown();
        }

        return businessPaymentResponse;

    }


    public ResponseMessage<BusinessListResponse>  getHamPayBusiness(BusinessListRequest businessListRequest){

        ResponseMessage<BusinessListResponse> businessListResponse = null;

        HttpParams httpParameters = new BasicHttpParams();

        int timeoutConnection = 30000;
        HttpConnectionParams.setConnectionTimeout(httpParameters, timeoutConnection);
        int timeoutSocket = 30000;
        HttpConnectionParams.setSoTimeout(httpParameters, timeoutSocket);

        HttpClient httpClient = new DefaultHttpClient(httpParameters);

        HttpHost host = new HttpHost(Constants.SERVER_IP, Constants.SERVER_PORT);
        HttpRequest request = new HttpPost("/businesses");

        HttpResponse response;
        try {
            StringEntity entity = new StringEntity(createHamPayBusinessJsonMessage(businessListRequest), "UTF-8");
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

            Type responseType = new com.google.gson.reflect.TypeToken<ResponseMessage<BusinessListResponse>>() {}.getType();
            businessListResponse = gson.fromJson(result, responseType);


        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            httpClient.getConnectionManager().shutdown();
        }

        return businessListResponse;

    }

    private String createHamPayBusinessJsonMessage(BusinessListRequest businessListRequest) {
        RequestHeader header = new RequestHeader();
//        header.setAuthToken("008ewe");
        header.setAuthToken(prefs.getString(Constants.LOGIN_TOKEN_ID, ""));
        header.setVersion("1.0-PA");

        RequestMessage<BusinessListRequest> message = new RequestMessage<BusinessListRequest>();
        message.setRequestHeader(header);
        BusinessListRequest request = new BusinessListRequest();

        request.setPageNumber(businessListRequest.getPageNumber());
        request.setPageSize(businessListRequest.getPageSize());

        request.setRequestUUID("1234");

        message.setService(request);

        Type requestType = new com.google.gson.reflect.TypeToken<RequestMessage<BusinessListRequest>>() {}.getType();
        return new Gson().toJson(message, requestType);
    }

    public ResponseMessage<BusinessListResponse>  searchBusinessList(BusinessSearchRequest businessSearchRequest){

        ResponseMessage<BusinessListResponse> businessListResponse = null;

        HttpParams httpParameters = new BasicHttpParams();

        int timeoutConnection = 30000;
        HttpConnectionParams.setConnectionTimeout(httpParameters, timeoutConnection);
        int timeoutSocket = 30000;
        HttpConnectionParams.setSoTimeout(httpParameters, timeoutSocket);

        HttpClient httpClient = new DefaultHttpClient(httpParameters);

        HttpHost host = new HttpHost(Constants.SERVER_IP, Constants.SERVER_PORT);
        HttpRequest request = new HttpPost("/search");

        HttpResponse response;
        try {
            StringEntity entity = new StringEntity(createSearchBusinessJsonMessage(businessSearchRequest), "UTF-8");
            entity.setContentType("application/json");
            ((HttpPost) request).setEntity(entity);

            response = httpClient.execute(host, request);

            HttpEntity responseEntity = response.getEntity();
            StatusLine responseStatus = response.getStatusLine();
            int statusCode = responseStatus != null ? responseStatus.getStatusCode() : 0;
            String result = EntityUtils.toString(responseEntity);


            GsonBuilder builder = new GsonBuilder();
            builder.registerTypeAdapter(Date.class, new JsonDeserializer<Date>() {
                public Date deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
                    return new Date(json.getAsJsonPrimitive().getAsLong());
                }
            });
            Gson gson = builder.create();

            Type responseType = new com.google.gson.reflect.TypeToken<ResponseMessage<BusinessListResponse>>() {}.getType();
            businessListResponse = gson.fromJson(result, responseType);


        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            httpClient.getConnectionManager().shutdown();
        }

        return businessListResponse;

    }


    private String createSearchBusinessJsonMessage(BusinessSearchRequest businessSearchRequest) {
        RequestHeader header = new RequestHeader();
//        header.setAuthToken("008ewe");
        header.setAuthToken(prefs.getString(Constants.LOGIN_TOKEN_ID, ""));
        header.setVersion("1.0-PA");

        RequestMessage<BusinessSearchRequest> message = new RequestMessage<BusinessSearchRequest>();
        message.setRequestHeader(header);
        BusinessSearchRequest request = businessSearchRequest;

        request.setRequestUUID("1234");

        message.setService(request);

        Type requestType = new com.google.gson.reflect.TypeToken<RequestMessage<BusinessSearchRequest>>() {}.getType();
        return new Gson().toJson(message, requestType);
    }



    public ResponseMessage<TransactionListResponse>  getUserTransaction(TransactionListRequest transactionListRequest){

        ResponseMessage<TransactionListResponse> transactionListResponse = null;

        HttpParams httpParameters = new BasicHttpParams();

        int timeoutConnection = 30000;
        HttpConnectionParams.setConnectionTimeout(httpParameters, timeoutConnection);
        int timeoutSocket = 30000;
        HttpConnectionParams.setSoTimeout(httpParameters, timeoutSocket);

        HttpClient httpClient = new DefaultHttpClient(httpParameters);

        HttpHost host = new HttpHost(Constants.SERVER_IP, Constants.SERVER_PORT);
        HttpRequest request = new HttpPost("/transactions");

        HttpResponse response;
        try {
            StringEntity entity = new StringEntity(createUserTransaction(transactionListRequest), "UTF-8");
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

    private String createUserTransaction(TransactionListRequest transactionListRequest) {
        RequestHeader header = new RequestHeader();
        header.setAuthToken(prefs.getString(Constants.LOGIN_TOKEN_ID, ""));
        header.setVersion("1.0-PA");

        RequestMessage<TransactionListRequest> message = new RequestMessage<TransactionListRequest>();
        message.setRequestHeader(header);
        TransactionListRequest request = new TransactionListRequest();
        request.setUserId(prefs.getString(Constants.REGISTERED_NATIONAL_CODE, ""));
        request.setPageSize(transactionListRequest.getPageSize());
        request.setPageNumber(transactionListRequest.getPageNumber());
        request.setRequestUUID("1234");
        message.setService(request);

        Type requestType = new com.google.gson.reflect.TypeToken<RequestMessage<TransactionListRequest>>() {}.getType();
        return new Gson().toJson(message, requestType);
    }

    private String createUserProfileJsonMessage() {
        RequestHeader header = new RequestHeader();
//        header.setAuthToken("008ewe");
        header.setAuthToken(prefs.getString(Constants.LOGIN_TOKEN_ID, ""));
        header.setVersion("1.0-PA");


        RequestMessage<UserProfileRequest> message = new RequestMessage<UserProfileRequest>();
        message.setRequestHeader(header);
        UserProfileRequest request = new UserProfileRequest();
        request.setRequestUUID("1234");
        message.setService(request);

        Type requestType = new com.google.gson.reflect.TypeToken<RequestMessage<UserProfileRequest>>() {}.getType();
        return new Gson().toJson(message, requestType);
    }

    private String createHamPayContactsJsonMessage() {
        RequestHeader header = new RequestHeader();
//        header.setAuthToken("008ewe");
        header.setAuthToken(prefs.getString(Constants.LOGIN_TOKEN_ID, ""));
        header.setVersion("1.0-PA");

        RequestMessage<ContactsHampayEnabledRequest> message = new RequestMessage<ContactsHampayEnabledRequest>();
        message.setRequestHeader(header);
        ContactsHampayEnabledRequest request = new ContactsHampayEnabledRequest();


        List<ContactDTO> contactDTOs = new ArrayList<ContactDTO>();

        Cursor phones = context.getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null,null,null, null);

        int index = 0;

        while (phones.moveToNext()) {
            String contact_name = phones.getString(phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME));
            String contact_phone_no = phones.getString(phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
            ContactDTO contactDTO = new ContactDTO();
            contactDTO.setCellNumber(contact_phone_no);
            contactDTO.setDisplayName(contact_name);

            contactDTOs.add(contactDTO);

        }
        phones.close();

        request.setContacts(contactDTOs);

        request.setRequestUUID("1234");
        message.setService(request);

        Type requestType = new com.google.gson.reflect.TypeToken<RequestMessage<ContactsHampayEnabledRequest>>() {}.getType();
        return new Gson().toJson(message, requestType);
    }

    private String createIndividualConfirmJsonMessage(IndividualPaymentConfirmRequest individualPaymentConfirmRequest) {
        RequestHeader header = new RequestHeader();
//        header.setAuthToken("008ewe");
        header.setAuthToken(prefs.getString(Constants.LOGIN_TOKEN_ID, ""));
        header.setVersion("1.0-PA");

        RequestMessage<IndividualPaymentConfirmRequest> message = new RequestMessage<IndividualPaymentConfirmRequest>();
        message.setRequestHeader(header);
        IndividualPaymentConfirmRequest request = new IndividualPaymentConfirmRequest();

        request.setCellNumber(individualPaymentConfirmRequest.getCellNumber());
        request.setRequestUUID("1234");
        message.setService(request);

        Type requestType = new com.google.gson.reflect.TypeToken<RequestMessage<IndividualPaymentConfirmRequest>>() {}.getType();
        return new Gson().toJson(message, requestType);
    }

    private String createIndividualJsonMessage(IndividualPaymentRequest individualPaymentRequest) {
        RequestHeader header = new RequestHeader();
//        header.setAuthToken("008ewe");
        header.setAuthToken(prefs.getString(Constants.LOGIN_TOKEN_ID, ""));
        header.setVersion("1.0-PA");

        RequestMessage<IndividualPaymentRequest> message = new RequestMessage<IndividualPaymentRequest>();
        message.setRequestHeader(header);
        IndividualPaymentRequest request = new IndividualPaymentRequest();

        request.setCellNumber(individualPaymentRequest.getCellNumber());
        request.setAmount(individualPaymentRequest.getAmount());
        request.setMessage(individualPaymentRequest.getMessage());

        request.setRequestUUID("1234");
        message.setService(request);

        Type requestType = new com.google.gson.reflect.TypeToken<RequestMessage<IndividualPaymentRequest>>() {}.getType();
        return new Gson().toJson(message, requestType);
    }


    private String createBusinessConfirmJsonMessage(BusinessPaymentConfirmRequest businessPaymentConfirmRequest) {
        RequestHeader header = new RequestHeader();
        header.setAuthToken(prefs.getString(Constants.LOGIN_TOKEN_ID, ""));
        header.setVersion("1.0-PA");

        RequestMessage<BusinessPaymentConfirmRequest> message = new RequestMessage<BusinessPaymentConfirmRequest>();
        message.setRequestHeader(header);
        BusinessPaymentConfirmRequest request = new BusinessPaymentConfirmRequest();

        request.setBusinessCode(businessPaymentConfirmRequest.getBusinessCode());

        request.setRequestUUID("1234");
        message.setService(request);

        Type requestType = new com.google.gson.reflect.TypeToken<RequestMessage<BusinessPaymentConfirmRequest>>() {}.getType();
        return new Gson().toJson(message, requestType);
    }

    private String createBusinessJsonMessage(BusinessPaymentRequest businessPaymentRequest) {
        //cls
        RequestHeader header = new RequestHeader();
//        header.setAuthToken("008ewe");
        header.setAuthToken(prefs.getString(Constants.LOGIN_TOKEN_ID, ""));
        header.setVersion("1.0-PA");

        RequestMessage<BusinessPaymentRequest> message = new RequestMessage<BusinessPaymentRequest>();
        message.setRequestHeader(header);
        BusinessPaymentRequest request = new BusinessPaymentRequest();

        request.setBusinessCode(businessPaymentRequest.getBusinessCode());
        request.setAmount(businessPaymentRequest.getAmount());
        request.setMessage(businessPaymentRequest.getMessage());
        request.setRequestUUID("1234");
        message.setService(request);

        Type requestType = new com.google.gson.reflect.TypeToken<RequestMessage<BusinessPaymentRequest>>() {}.getType();
        return new Gson().toJson(message, requestType);
    }

}
