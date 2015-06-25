package com.hampay.mobile.android.webservice;

import android.content.Context;
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
import com.hampay.common.core.model.request.ContactsHampayEnabledRequest;
import com.hampay.common.core.model.request.IndividualPaymentConfirmRequest;
import com.hampay.common.core.model.request.IndividualPaymentRequest;
import com.hampay.common.core.model.request.RegistrationConfirmUserDataRequest;
import com.hampay.common.core.model.request.RegistrationEntryRequest;
import com.hampay.common.core.model.request.RegistrationFetchUserDataRequest;
import com.hampay.common.core.model.request.RegistrationSendSmsTokenRequest;
import com.hampay.common.core.model.request.RegistrationVerifyMobileRequest;
import com.hampay.common.core.model.request.TransactionListRequest;
import com.hampay.common.core.model.request.UserProfileRequest;
import com.hampay.common.core.model.response.BankListResponse;
import com.hampay.common.core.model.response.BusinessListResponse;
import com.hampay.common.core.model.response.BusinessPaymentConfirmResponse;
import com.hampay.common.core.model.response.BusinessPaymentResponse;
import com.hampay.common.core.model.response.ContactsHampayEnabledResponse;
import com.hampay.common.core.model.response.IndividualPaymentConfirmResponse;
import com.hampay.common.core.model.response.IndividualPaymentResponse;
import com.hampay.common.core.model.response.RegistrationConfirmUserDataResponse;
import com.hampay.common.core.model.response.RegistrationEntryResponse;
import com.hampay.common.core.model.response.RegistrationFetchUserDataResponse;
import com.hampay.common.core.model.response.RegistrationSendSmsTokenResponse;
import com.hampay.common.core.model.response.RegistrationVerifyMobileResponse;
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
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by amir on 6/6/15.
 */
public class WebServices  {

    public WebServices(){}

    Context context;

    public WebServices(Context context){

        this.context = context;

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


    public ResponseMessage<RegistrationEntryResponse>  registrationEntry(RegistrationEntryRequest registrationEntryRequest){

        ResponseMessage<RegistrationEntryResponse> registrationEntryResponse = null;
        HttpParams httpParameters = new BasicHttpParams();
        int timeoutConnection = 30000;
        HttpConnectionParams.setConnectionTimeout(httpParameters, timeoutConnection);
        int timeoutSocket = 30000;
        HttpConnectionParams.setSoTimeout(httpParameters, timeoutSocket);
        HttpClient httpClient = new DefaultHttpClient(httpParameters);
        HttpHost host = new HttpHost("176.58.104.158", 9093);
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



    public ResponseMessage<RegistrationSendSmsTokenResponse>  registrationSendSmsToken(RegistrationSendSmsTokenRequest registrationSendSmsTokenRequest){

        ResponseMessage<RegistrationSendSmsTokenResponse> registrationSendSmsTokenResponse = null;
        HttpParams httpParameters = new BasicHttpParams();
        int timeoutConnection = 30000;
        HttpConnectionParams.setConnectionTimeout(httpParameters, timeoutConnection);
        int timeoutSocket = 30000;
        HttpConnectionParams.setSoTimeout(httpParameters, timeoutSocket);
        HttpClient httpClient = new DefaultHttpClient(httpParameters);
        HttpHost host = new HttpHost("176.58.104.158", 9093);
        HttpRequest request = new HttpPost("/users/reg-entry");


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
        HttpHost host = new HttpHost("176.58.104.158", 9093);

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
        HttpHost host = new HttpHost("176.58.104.158", 9093);

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
        HttpHost host = new HttpHost("176.58.104.158", 9093);

        HttpRequest request = new HttpPost("/users/reg-fetch-user-data");

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




    public ResponseMessage<ContactsHampayEnabledResponse>  getHamPayContacts(){

        ResponseMessage<ContactsHampayEnabledResponse> contactsHampayEnabledResponse = null;

        HttpParams httpParameters = new BasicHttpParams();

        int timeoutConnection = 30000;
        HttpConnectionParams.setConnectionTimeout(httpParameters, timeoutConnection);
        int timeoutSocket = 30000;
        HttpConnectionParams.setSoTimeout(httpParameters, timeoutSocket);

        HttpClient httpClient = new DefaultHttpClient(httpParameters);

        HttpHost host = new HttpHost("176.58.104.158", 9093);
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

    public ResponseMessage<IndividualPaymentConfirmResponse>  individualPaymentConfirm(String phoneNumber){

        ResponseMessage<IndividualPaymentConfirmResponse> individualPaymentConfirmResponse = null;

        HttpParams httpParameters = new BasicHttpParams();

        int timeoutConnection = 30000;
        HttpConnectionParams.setConnectionTimeout(httpParameters, timeoutConnection);
        int timeoutSocket = 30000;
        HttpConnectionParams.setSoTimeout(httpParameters, timeoutSocket);

        HttpClient httpClient = new DefaultHttpClient(httpParameters);

        HttpHost host = new HttpHost("176.58.104.158", 9093);
        HttpRequest request = new HttpPost("/customers/individual-payment-confirm");

        HttpResponse response;
        try {

            String req = createIndividualJsonMessage(phoneNumber);

            StringEntity entity = new StringEntity(createIndividualConfirmJsonMessage(phoneNumber), "UTF-8");
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

    public ResponseMessage<IndividualPaymentResponse>  individualPayment(String phoneNumber){

        ResponseMessage<IndividualPaymentResponse> individualPaymentResponse = null;

        HttpParams httpParameters = new BasicHttpParams();

        int timeoutConnection = 30000;
        HttpConnectionParams.setConnectionTimeout(httpParameters, timeoutConnection);
        int timeoutSocket = 30000;
        HttpConnectionParams.setSoTimeout(httpParameters, timeoutSocket);

        HttpClient httpClient = new DefaultHttpClient(httpParameters);

        HttpHost host = new HttpHost("176.58.104.158", 9093);
        HttpRequest request = new HttpPost("/customers/individual-payment");

        HttpResponse response;
        try {

            String req = createIndividualJsonMessage(phoneNumber);

            StringEntity entity = new StringEntity(createIndividualJsonMessage(phoneNumber), "UTF-8");
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



    public ResponseMessage<BusinessPaymentConfirmResponse>  businessPaymentConfirm(String phoneNumber){

        ResponseMessage<BusinessPaymentConfirmResponse> businessPaymentConfirmResponse = null;

        HttpParams httpParameters = new BasicHttpParams();

        int timeoutConnection = 30000;
        HttpConnectionParams.setConnectionTimeout(httpParameters, timeoutConnection);
        int timeoutSocket = 30000;
        HttpConnectionParams.setSoTimeout(httpParameters, timeoutSocket);

        HttpClient httpClient = new DefaultHttpClient(httpParameters);

        HttpHost host = new HttpHost("176.58.104.158", 9093);
        HttpRequest request = new HttpPost("/businesses/business-payment-confirm");

        HttpResponse response;
        try {

            String req = createIndividualJsonMessage(phoneNumber);

            StringEntity entity = new StringEntity(createBusinessConfirmJsonMessage(phoneNumber), "UTF-8");
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

    public ResponseMessage<BusinessPaymentResponse>  businessPayment(String phoneNumber){

        ResponseMessage<BusinessPaymentResponse> businessPaymentResponse = null;

        HttpParams httpParameters = new BasicHttpParams();

        int timeoutConnection = 30000;
        HttpConnectionParams.setConnectionTimeout(httpParameters, timeoutConnection);
        int timeoutSocket = 30000;
        HttpConnectionParams.setSoTimeout(httpParameters, timeoutSocket);

        HttpClient httpClient = new DefaultHttpClient(httpParameters);

        HttpHost host = new HttpHost("176.58.104.158", 9093);
        HttpRequest request = new HttpPost("/customers/business-payment");

        HttpResponse response;
        try {

            String req = createIndividualJsonMessage(phoneNumber);

            StringEntity entity = new StringEntity(createBusinessJsonMessage(phoneNumber), "UTF-8");
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


    public ResponseMessage<BusinessListResponse>  getHamPayBusiness(){

        ResponseMessage<BusinessListResponse> businessListResponse = null;

        HttpParams httpParameters = new BasicHttpParams();

        int timeoutConnection = 30000;
        HttpConnectionParams.setConnectionTimeout(httpParameters, timeoutConnection);
        int timeoutSocket = 30000;
        HttpConnectionParams.setSoTimeout(httpParameters, timeoutSocket);

        HttpClient httpClient = new DefaultHttpClient(httpParameters);

        HttpHost host = new HttpHost("176.58.104.158", 9093);
        HttpRequest request = new HttpPost("/businesses");

        HttpResponse response;
        try {
            StringEntity entity = new StringEntity(createHamPayBusinessJsonMessage(), "UTF-8");
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

    private String createHamPayContactsJsonMessage() {
        RequestHeader header = new RequestHeader();
        header.setAuthToken("008ewe");
        header.setVersion("1.0-PA");

        RequestMessage<ContactsHampayEnabledRequest> message = new RequestMessage<ContactsHampayEnabledRequest>();
        message.setRequestHeader(header);
        ContactsHampayEnabledRequest request = new ContactsHampayEnabledRequest();

        List<ContactDTO> contactDTOs = new ArrayList<ContactDTO>();

        Cursor phones = context.getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null,null,null, null);
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

    private String createIndividualConfirmJsonMessage(String phoneNumber) {
        RequestHeader header = new RequestHeader();
        header.setAuthToken("008ewe");
        header.setVersion("1.0-PA");

        RequestMessage<IndividualPaymentConfirmRequest> message = new RequestMessage<IndividualPaymentConfirmRequest>();
        message.setRequestHeader(header);
        IndividualPaymentConfirmRequest request = new IndividualPaymentConfirmRequest();

        request.setCellNumber(phoneNumber);

        request.setRequestUUID("1234");
        message.setService(request);

        Type requestType = new com.google.gson.reflect.TypeToken<RequestMessage<IndividualPaymentConfirmRequest>>() {}.getType();
        return new Gson().toJson(message, requestType);
    }

    private String createIndividualJsonMessage(String phoneNumber) {
        RequestHeader header = new RequestHeader();
        header.setAuthToken("008ewe");
        header.setVersion("1.0-PA");

        RequestMessage<IndividualPaymentRequest> message = new RequestMessage<IndividualPaymentRequest>();
        message.setRequestHeader(header);
        IndividualPaymentRequest request = new IndividualPaymentRequest();

        request.setCellNumber(phoneNumber);

        request.setRequestUUID("1234");
        message.setService(request);

        Type requestType = new com.google.gson.reflect.TypeToken<RequestMessage<IndividualPaymentRequest>>() {}.getType();
        return new Gson().toJson(message, requestType);
    }


    private String createBusinessConfirmJsonMessage(String businessCode) {
        RequestHeader header = new RequestHeader();
        header.setAuthToken("008ewe");
        header.setVersion("1.0-PA");

        RequestMessage<BusinessPaymentConfirmRequest> message = new RequestMessage<BusinessPaymentConfirmRequest>();
        message.setRequestHeader(header);
        BusinessPaymentConfirmRequest request = new BusinessPaymentConfirmRequest();

        request.setBusinessCode(businessCode);

        request.setRequestUUID("1234");
        message.setService(request);

        Type requestType = new com.google.gson.reflect.TypeToken<RequestMessage<BusinessPaymentConfirmRequest>>() {}.getType();
        return new Gson().toJson(message, requestType);
    }

    private String createBusinessJsonMessage(String businessCode) {
        RequestHeader header = new RequestHeader();
        header.setAuthToken("008ewe");
        header.setVersion("1.0-PA");

        RequestMessage<BusinessPaymentRequest> message = new RequestMessage<BusinessPaymentRequest>();
        message.setRequestHeader(header);
        BusinessPaymentRequest request = new BusinessPaymentRequest();

        request.setBusinessCode(businessCode);

        request.setRequestUUID("1234");
        message.setService(request);

        Type requestType = new com.google.gson.reflect.TypeToken<RequestMessage<BusinessPaymentRequest>>() {}.getType();
        return new Gson().toJson(message, requestType);
    }


    private String createHamPayBusinessJsonMessage() {
        RequestHeader header = new RequestHeader();
        header.setAuthToken("008ewe");
        header.setVersion("1.0-PA");

        RequestMessage<BusinessListRequest> message = new RequestMessage<BusinessListRequest>();
        message.setRequestHeader(header);
        BusinessListRequest request = new BusinessListRequest();

        request.setPageNumber(2);
        request.setPageSize(10);

        request.setRequestUUID("1234");
        message.setService(request);

        Type requestType = new com.google.gson.reflect.TypeToken<RequestMessage<BusinessListRequest>>() {}.getType();
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

        Type requestType = new com.google.gson.reflect.TypeToken<RequestMessage<TransactionListRequest>>() {}.getType();
        return new Gson().toJson(message, requestType);
    }

}
