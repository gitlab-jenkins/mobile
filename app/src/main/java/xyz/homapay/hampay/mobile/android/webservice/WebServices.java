package xyz.homapay.hampay.mobile.android.webservice;

import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.provider.ContactsContract;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.JsonParser;
import com.google.gson.reflect.TypeToken;
import xyz.homapay.hampay.common.common.request.RequestHeader;
import xyz.homapay.hampay.common.common.request.RequestMessage;
import xyz.homapay.hampay.common.common.response.ResponseMessage;
import xyz.homapay.hampay.common.core.model.dto.ContactDTO;
import xyz.homapay.hampay.common.core.model.request.BankListRequest;
import xyz.homapay.hampay.common.core.model.request.BusinessListRequest;
import xyz.homapay.hampay.common.core.model.request.BusinessPaymentConfirmRequest;
import xyz.homapay.hampay.common.core.model.request.BusinessPaymentRequest;
import xyz.homapay.hampay.common.core.model.request.BusinessSearchRequest;
import xyz.homapay.hampay.common.core.model.request.ChangeEmailRequest;
import xyz.homapay.hampay.common.core.model.request.ChangeMemorableWordRequest;
import xyz.homapay.hampay.common.core.model.request.ChangePassCodeRequest;
import xyz.homapay.hampay.common.core.model.request.ContactUsRequest;
import xyz.homapay.hampay.common.core.model.request.ContactsHampayEnabledRequest;
import xyz.homapay.hampay.common.core.model.request.GetUserIdTokenRequest;
import xyz.homapay.hampay.common.core.model.request.IllegalAppListRequest;
import xyz.homapay.hampay.common.core.model.request.IndividualPaymentConfirmRequest;
import xyz.homapay.hampay.common.core.model.request.IndividualPaymentRequest;
import xyz.homapay.hampay.common.core.model.request.MobileRegistrationIdEntryRequest;
import xyz.homapay.hampay.common.core.model.request.RegistrationConfirmUserDataRequest;
import xyz.homapay.hampay.common.core.model.request.RegistrationCredentialsRequest;
import xyz.homapay.hampay.common.core.model.request.RegistrationEntryRequest;
import xyz.homapay.hampay.common.core.model.request.RegistrationFetchUserDataRequest;
import xyz.homapay.hampay.common.core.model.request.RegistrationSendSmsTokenRequest;
import xyz.homapay.hampay.common.core.model.request.RegistrationVerifyAccountRequest;
import xyz.homapay.hampay.common.core.model.request.RegistrationVerifyMobileRequest;
import xyz.homapay.hampay.common.core.model.request.RegistrationVerifyTransferMoneyRequest;
import xyz.homapay.hampay.common.core.model.request.TACAcceptRequest;
import xyz.homapay.hampay.common.core.model.request.TACRequest;
import xyz.homapay.hampay.common.core.model.request.TransactionListRequest;
import xyz.homapay.hampay.common.core.model.request.UnlinkUserRequest;
import xyz.homapay.hampay.common.core.model.request.UploadImageRequest;
import xyz.homapay.hampay.common.core.model.request.UserProfileRequest;
import xyz.homapay.hampay.common.core.model.request.VerifyAccountRequest;
import xyz.homapay.hampay.common.core.model.request.VerifyTransferMoneyRequest;
import xyz.homapay.hampay.common.core.model.response.BankListResponse;
import xyz.homapay.hampay.common.core.model.response.BusinessListResponse;
import xyz.homapay.hampay.common.core.model.response.BusinessPaymentConfirmResponse;
import xyz.homapay.hampay.common.core.model.response.BusinessPaymentResponse;
import xyz.homapay.hampay.common.core.model.response.ChangeEmailResponse;
import xyz.homapay.hampay.common.core.model.response.ChangeMemorableWordResponse;
import xyz.homapay.hampay.common.core.model.response.ChangePassCodeResponse;
import xyz.homapay.hampay.common.core.model.response.ContactUsResponse;
import xyz.homapay.hampay.common.core.model.response.ContactsHampayEnabledResponse;
import xyz.homapay.hampay.common.core.model.response.GetUserIdTokenResponse;
import xyz.homapay.hampay.common.core.model.response.IllegalAppListResponse;
import xyz.homapay.hampay.common.core.model.response.IndividualPaymentConfirmResponse;
import xyz.homapay.hampay.common.core.model.response.IndividualPaymentResponse;
import xyz.homapay.hampay.common.core.model.response.MobileRegistrationIdEntryResponse;
import xyz.homapay.hampay.common.core.model.response.RegistrationConfirmUserDataResponse;
import xyz.homapay.hampay.common.core.model.response.RegistrationCredentialsResponse;
import xyz.homapay.hampay.common.core.model.response.RegistrationEntryResponse;
import xyz.homapay.hampay.common.core.model.response.RegistrationFetchUserDataResponse;
import xyz.homapay.hampay.common.core.model.response.RegistrationSendSmsTokenResponse;
import xyz.homapay.hampay.common.core.model.response.RegistrationVerifyAccountResponse;
import xyz.homapay.hampay.common.core.model.response.RegistrationVerifyMobileResponse;
import xyz.homapay.hampay.common.core.model.response.RegistrationVerifyTransferMoneyResponse;
import xyz.homapay.hampay.common.core.model.response.TACAcceptResponse;
import xyz.homapay.hampay.common.core.model.response.TACResponse;
import xyz.homapay.hampay.common.core.model.response.TransactionListResponse;
import xyz.homapay.hampay.common.core.model.response.UnlinkUserResponse;
import xyz.homapay.hampay.common.core.model.response.UploadImageResponse;
import xyz.homapay.hampay.common.core.model.response.UserProfileResponse;
import xyz.homapay.hampay.common.core.model.response.VerifyAccountResponse;
import xyz.homapay.hampay.common.core.model.response.VerifyTransferMoneyResponse;
import xyz.homapay.hampay.mobile.android.model.LogoutData;
import xyz.homapay.hampay.mobile.android.model.LogoutResponse;
import xyz.homapay.hampay.mobile.android.util.Constants;
import xyz.homapay.hampay.mobile.android.util.DeviceInfo;
import xyz.homapay.hampay.mobile.android.util.PersianEnglishDigit;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.lang.reflect.Type;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;
import java.util.zip.Inflater;

import javax.net.ssl.HttpsURLConnection;

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

    public LogoutResponse sendLogoutRequest(LogoutData logoutData)throws Exception {

        SSLConnection sslConnection = new SSLConnection(context, Constants.HTTPSOPENAM_LOGOUT_URL);
        HttpsURLConnection connection = sslConnection.setUpHttpsURLConnection();

        connection.setRequestMethod("POST");
        connection.setConnectTimeout(20 * 1000);
        connection.setReadTimeout(20 * 1000);

        connection.setDoOutput(true);

        connection.setRequestProperty("iplanetDirectoryPro", logoutData.getIplanetDirectoryPro());
        connection.setRequestProperty("Content-Type", "application/json");
        connection.setRequestProperty("Accept-Encoding", "UTF-8");

        BufferedWriter output = new BufferedWriter(new OutputStreamWriter(connection.getOutputStream()));
        output.write("");
        output.flush();
        output.close();

        int responseCode = connection.getResponseCode();

        BufferedReader in = new BufferedReader(
                new InputStreamReader(connection.getInputStream()));
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

    public ResponseMessage<IllegalAppListResponse> getIllegalAppList() {

        ResponseMessage<IllegalAppListResponse> responseMessage = null;
        SSLConnection sslConnection = new SSLConnection(context, Constants.HTTPS_SERVER_IP + "/illegal-apps");
        HttpsURLConnection connection = sslConnection.setUpHttpsURLConnection();

        try {

            RequestHeader header = new RequestHeader();
            header.setAuthToken("008ewe");
            header.setVersion("1.0-PA");

            RequestMessage<IllegalAppListRequest> message = new RequestMessage<IllegalAppListRequest>();
            message.setRequestHeader(header);
            IllegalAppListRequest request = new IllegalAppListRequest();
            request.setRequestUUID(UUID.randomUUID().toString());
            message.setService(request);

            Type requestType = new com.google.gson.reflect.TypeToken<RequestMessage<IllegalAppListRequest>>() {}.getType();
            String jsonRequest = new Gson().toJson(message, requestType);

            connection.setConnectTimeout(5000);
            connection.setReadTimeout(5000);
            connection.setRequestMethod("POST");
            connection.setRequestProperty("Content-Type", "application/json");
            OutputStream outputStream = connection.getOutputStream();
            outputStream.write(jsonRequest.getBytes());
            outputStream.flush();

            String encoding = connection.getHeaderField("Content-Encoding");
            boolean gzipped = encoding != null && encoding.toLowerCase().contains("gzip");
            InputStreamReader reader;
            if (gzipped){
                InputStream gzipInputStream = new GZIPInputStream(connection.getInputStream());
                reader = new InputStreamReader(gzipInputStream);
            }else {
                reader = new InputStreamReader(connection.getInputStream());
            }

            Gson gson = new Gson();
            responseMessage = gson.fromJson(reader, new TypeToken<ResponseMessage<IllegalAppListResponse>>() {}.getType());

        } catch (IOException e) {
            e.printStackTrace();
        }
        finally {
            if (connection != null)
                connection.disconnect();
        }

        return responseMessage;
    }

    public ResponseMessage<BankListResponse> getBankList() {

        ResponseMessage<BankListResponse> responseMessage = null;
        SSLConnection sslConnection = new SSLConnection(context, Constants.HTTPS_SERVER_IP + "/banks");
        HttpsURLConnection connection = sslConnection.setUpHttpsURLConnection();

        try {

            RequestHeader header = new RequestHeader();
            header.setAuthToken("008ewe");
            header.setVersion("1.0-PA");

            RequestMessage<BankListRequest> message = new RequestMessage<BankListRequest>();
            message.setRequestHeader(header);
            BankListRequest request = new BankListRequest();
            request.setRequestUUID(UUID.randomUUID().toString());
            message.setService(request);

            Type requestType = new com.google.gson.reflect.TypeToken<RequestMessage<BankListRequest>>() {}.getType();
            String jsonRequest = new Gson().toJson(message, requestType);

            connection.setConnectTimeout(5000);
            connection.setReadTimeout(5000);
            connection.setRequestMethod("POST");
            connection.setRequestProperty("Content-Type", "application/json");
            OutputStream outputStream = connection.getOutputStream();
            outputStream.write(jsonRequest.getBytes());
            outputStream.flush();

            String encoding = connection.getHeaderField("Content-Encoding");
            boolean gzipped = encoding != null && encoding.toLowerCase().contains("gzip");
            InputStreamReader reader;
            if (gzipped){
                InputStream gzipInputStream = new GZIPInputStream(connection.getInputStream());
                reader = new InputStreamReader(gzipInputStream);
            }else {
                reader = new InputStreamReader(connection.getInputStream());
            }

            Gson gson = new Gson();
            responseMessage = gson.fromJson(reader, new TypeToken<ResponseMessage<BankListResponse>>() {}.getType());
        } catch (IOException e) {
            e.printStackTrace();
        }
        finally {
            if (connection != null)
                connection.disconnect();
        }
        return responseMessage;
    }

    public ResponseMessage<RegistrationEntryResponse> newRegistrationEntry(RegistrationEntryRequest registrationEntryRequest){

        ResponseMessage<RegistrationEntryResponse> responseMessage = null;
        SSLConnection sslConnection = new SSLConnection(context, Constants.HTTPS_SERVER_IP + "/users/reg-entry");
        HttpsURLConnection connection = sslConnection.setUpHttpsURLConnection();

        try {

            RequestHeader header = new RequestHeader();
            header.setAuthToken("008ewe");
            header.setVersion("1.0-PA");

            RequestMessage<RegistrationEntryRequest> message = new RequestMessage<RegistrationEntryRequest>();
            message.setRequestHeader(header);
            RegistrationEntryRequest request = registrationEntryRequest;
            request.setRequestUUID(UUID.randomUUID().toString());
            message.setService(request);

            Type requestType = new com.google.gson.reflect.TypeToken<RequestMessage<RegistrationEntryRequest>>() {}.getType();
            String jsonRequest = new Gson().toJson(message, requestType);

            connection.setConnectTimeout(30000);
            connection.setReadTimeout(30000);
            connection.setRequestMethod("POST");

            connection.setRequestProperty("Content-Encoding", "gzip");
            connection.setRequestProperty("Accept-Encoding", "gzip");

            OutputStream outputStream = connection.getOutputStream();

//            String raw = new String(decompress(gzip(jsonRequest.getBytes())));

            outputStream.write(gzip(jsonRequest.getBytes()));
            outputStream.flush();

            String encoding = connection.getHeaderField("Content-Encoding");
            boolean gzipped = encoding != null && encoding.toLowerCase().contains("gzip");
            InputStreamReader reader;
            if (gzipped){
                InputStream gzipInputStream = new GZIPInputStream(connection.getInputStream());
                reader = new InputStreamReader(gzipInputStream);
            }else {
                reader = new InputStreamReader(connection.getInputStream());
            }

            Gson gson = new Gson();
            responseMessage = gson.fromJson(reader, new TypeToken<ResponseMessage<RegistrationEntryResponse>>() {}.getType());

        } catch (IOException e) {
            e.printStackTrace();
        }
        finally {
            if (connection != null)
                connection.disconnect();
        }
        return responseMessage;
    }

    static byte[] gzip(byte[] input) {
        GZIPOutputStream gzipOS = null;
        try {
            ByteArrayOutputStream byteArrayOS = new ByteArrayOutputStream();
            gzipOS = new GZIPOutputStream(byteArrayOS);
            gzipOS.write(input);
            gzipOS.flush();
            gzipOS.close();
            gzipOS = null;

            long size = byteArrayOS.size();

            return byteArrayOS.toByteArray();
        } catch (Exception e) {
//            throw new WebbException(e); // <-- just a RuntimeException
        } finally {
            if (gzipOS != null) {
                try { gzipOS.close(); } catch (Exception ignored) {}
            }
        }
        return null;
    }


    public static String decompress(byte[] compressed) throws IOException {
        final int BUFFER_SIZE = 32;
        ByteArrayInputStream is = new ByteArrayInputStream(compressed);
        GZIPInputStream gis = new GZIPInputStream(is, BUFFER_SIZE);
        StringBuilder string = new StringBuilder();
        byte[] data = new byte[BUFFER_SIZE];
        int bytesRead;
        while ((bytesRead = gis.read(data)) != -1) {
            string.append(new String(data, 0, bytesRead));
        }
        gis.close();
        is.close();
        return string.toString();
    }

    public ResponseMessage<ContactUsResponse> newContactUsResponse(ContactUsRequest contactUsRequest){

        ResponseMessage<ContactUsResponse> responseMessage = null;
        SSLConnection sslConnection = new SSLConnection(context, Constants.HTTPS_SERVER_IP + "/contactus");
        HttpsURLConnection connection = sslConnection.setUpHttpsURLConnection();

        try {

            RequestHeader header = new RequestHeader();
            header.setAuthToken("008ewe");
            header.setVersion("1.0-PA");

            RequestMessage<ContactUsRequest> message = new RequestMessage<ContactUsRequest>();
            message.setRequestHeader(header);
            ContactUsRequest request = contactUsRequest;
            request.setRequestUUID(UUID.randomUUID().toString());
            message.setService(request);

            Type requestType = new com.google.gson.reflect.TypeToken<RequestMessage<ContactUsRequest>>() {}.getType();
            String jsonRequest = new Gson().toJson(message, requestType);

            connection.setConnectTimeout(30000);
            connection.setReadTimeout(30000);
            connection.setRequestMethod("POST");
            connection.setRequestProperty("Content-Type", "application/json");
            OutputStream outputStream = connection.getOutputStream();
            outputStream.write(jsonRequest.getBytes());
            outputStream.flush();

            String encoding = connection.getHeaderField("Content-Encoding");
            boolean gzipped = encoding != null && encoding.toLowerCase().contains("gzip");
            InputStreamReader reader;
            if (gzipped){
                InputStream gzipInputStream = new GZIPInputStream(connection.getInputStream());
                reader = new InputStreamReader(gzipInputStream);
            }else {
                reader = new InputStreamReader(connection.getInputStream());
            }

            Gson gson = new Gson();
            responseMessage = gson.fromJson(reader, new TypeToken<ResponseMessage<ContactUsResponse>>() {}.getType());

        } catch (IOException e) {e.printStackTrace();}
        finally {
            if (connection != null)
                connection.disconnect();
        }
        return responseMessage;
    }

    public ResponseMessage<RegistrationSendSmsTokenResponse> newRegistrationSendSmsToken(RegistrationSendSmsTokenRequest registrationSendSmsTokenRequest){

        ResponseMessage<RegistrationSendSmsTokenResponse> responseMessage = null;
        SSLConnection sslConnection = new SSLConnection(context, Constants.HTTPS_SERVER_IP + "/users/reg-sms-token");
        HttpsURLConnection connection = sslConnection.setUpHttpsURLConnection();

        try {

            RequestHeader header = new RequestHeader();
            header.setAuthToken("008ewe");
            header.setVersion("1.0-PA");

            RequestMessage<RegistrationSendSmsTokenRequest> message = new RequestMessage<RegistrationSendSmsTokenRequest>();
            message.setRequestHeader(header);
            RegistrationSendSmsTokenRequest request = registrationSendSmsTokenRequest;
            request.setRequestUUID(UUID.randomUUID().toString());
            message.setService(request);

            Type requestType = new com.google.gson.reflect.TypeToken<RequestMessage<RegistrationSendSmsTokenRequest>>() {}.getType();
            String jsonRequest = new Gson().toJson(message, requestType);

            connection.setConnectTimeout(30000);
            connection.setReadTimeout(30000);
            connection.setRequestMethod("POST");
            connection.setRequestProperty("Content-Type", "application/json");
            OutputStream outputStream = connection.getOutputStream();
            outputStream.write(jsonRequest.getBytes());
            outputStream.flush();

            String encoding = connection.getHeaderField("Content-Encoding");
            boolean gzipped = encoding != null && encoding.toLowerCase().contains("gzip");
            InputStreamReader reader;
            if (gzipped){
                InputStream gzipInputStream = new GZIPInputStream(connection.getInputStream());
                reader = new InputStreamReader(gzipInputStream);
            }else {
                reader = new InputStreamReader(connection.getInputStream());
            }

            Gson gson = new Gson();
            responseMessage = gson.fromJson(reader, new TypeToken<ResponseMessage<RegistrationSendSmsTokenResponse>>() {}.getType());

        } catch (IOException e) {e.printStackTrace();}
        finally {
            if (connection != null)
                connection.disconnect();
        }
        return responseMessage;
    }


    public ResponseMessage<RegistrationVerifyMobileResponse> newRegistrationVerifyMobileResponse(RegistrationVerifyMobileRequest registrationVerifyMobileRequest){

        ResponseMessage<RegistrationVerifyMobileResponse> responseMessage = null;
        SSLConnection sslConnection = new SSLConnection(context, Constants.HTTPS_SERVER_IP + "/users/reg-verify-mobile");
        HttpsURLConnection connection = sslConnection.setUpHttpsURLConnection();

        try {

            RequestHeader header = new RequestHeader();
            header.setAuthToken("008ewe");
            header.setVersion("1.0-PA");

            RequestMessage<RegistrationVerifyMobileRequest> message = new RequestMessage<RegistrationVerifyMobileRequest>();
            message.setRequestHeader(header);
            RegistrationVerifyMobileRequest request = registrationVerifyMobileRequest;
            request.setRequestUUID(UUID.randomUUID().toString());
            message.setService(request);

            Type requestType = new com.google.gson.reflect.TypeToken<RequestMessage<RegistrationVerifyMobileRequest>>() {}.getType();
            String jsonRequest = new Gson().toJson(message, requestType);

            connection.setConnectTimeout(30000);
            connection.setReadTimeout(30000);
            connection.setRequestMethod("POST");
            connection.setRequestProperty("Content-Type", "application/json");
            OutputStream outputStream = connection.getOutputStream();
            outputStream.write(jsonRequest.getBytes());
            outputStream.flush();

            String encoding = connection.getHeaderField("Content-Encoding");
            boolean gzipped = encoding != null && encoding.toLowerCase().contains("gzip");
            InputStreamReader reader;
            if (gzipped){
                InputStream gzipInputStream = new GZIPInputStream(connection.getInputStream());
                reader = new InputStreamReader(gzipInputStream);
            }else {
                reader = new InputStreamReader(connection.getInputStream());
            }

            Gson gson = new Gson();
            responseMessage = gson.fromJson(reader, new TypeToken<ResponseMessage<RegistrationVerifyMobileResponse>>() {}.getType());

        } catch (IOException e) {e.printStackTrace();}
        finally {
            if (connection != null)
                connection.disconnect();
        }
        return responseMessage;
    }

    public ResponseMessage<RegistrationFetchUserDataResponse> newRegistrationFetchUserDataResponse(RegistrationFetchUserDataRequest registrationFetchUserDataRequest){

        ResponseMessage<RegistrationFetchUserDataResponse> responseMessage = null;
        SSLConnection sslConnection = new SSLConnection(context, Constants.HTTPS_SERVER_IP + "/users/reg-fetch-user-data");
        HttpsURLConnection connection = sslConnection.setUpHttpsURLConnection();

        try {

            RequestHeader header = new RequestHeader();
            header.setAuthToken("008ewe");
            header.setVersion("1.0-PA");

            RequestMessage<RegistrationFetchUserDataRequest> message = new RequestMessage<RegistrationFetchUserDataRequest>();
            message.setRequestHeader(header);
            RegistrationFetchUserDataRequest request = registrationFetchUserDataRequest;
            request.setRequestUUID(UUID.randomUUID().toString());
            message.setService(request);

            Type requestType = new com.google.gson.reflect.TypeToken<RequestMessage<RegistrationFetchUserDataRequest>>() {}.getType();
            String jsonRequest = new Gson().toJson(message, requestType);

            connection.setConnectTimeout(30000);
            connection.setReadTimeout(30000);
            connection.setRequestMethod("POST");
            connection.setRequestProperty("Content-Type", "application/json");
            OutputStream outputStream = connection.getOutputStream();
            outputStream.write(jsonRequest.getBytes());
            outputStream.flush();

            String encoding = connection.getHeaderField("Content-Encoding");
            boolean gzipped = encoding != null && encoding.toLowerCase().contains("gzip");
            InputStreamReader reader;
            if (gzipped){
                InputStream gzipInputStream = new GZIPInputStream(connection.getInputStream());
                reader = new InputStreamReader(gzipInputStream);
            }else {
                reader = new InputStreamReader(connection.getInputStream());
            }

            Gson gson = new Gson();
            responseMessage = gson.fromJson(reader, new TypeToken<ResponseMessage<RegistrationFetchUserDataResponse>>() {}.getType());

        } catch (IOException e) {e.printStackTrace();}
        finally {
            if (connection != null)
                connection.disconnect();
        }
        return responseMessage;
    }


    public ResponseMessage<RegistrationConfirmUserDataResponse> newRegistrationConfirmUserDataResponse(RegistrationConfirmUserDataRequest registrationConfirmUserDataRequest){

        ResponseMessage<RegistrationConfirmUserDataResponse> responseMessage = null;
        SSLConnection sslConnection = new SSLConnection(context, Constants.HTTPS_SERVER_IP + "/users/reg-confirm-user-data");
        HttpsURLConnection connection = sslConnection.setUpHttpsURLConnection();

        try {

            RequestHeader header = new RequestHeader();
            header.setAuthToken("008ewe");
            header.setVersion("1.0-PA");

            RequestMessage<RegistrationConfirmUserDataRequest> message = new RequestMessage<RegistrationConfirmUserDataRequest>();
            message.setRequestHeader(header);
            RegistrationConfirmUserDataRequest request = registrationConfirmUserDataRequest;
            request.setRequestUUID(UUID.randomUUID().toString());
            message.setService(request);

            Type requestType = new com.google.gson.reflect.TypeToken<RequestMessage<RegistrationConfirmUserDataRequest>>() {}.getType();
            String jsonRequest = new Gson().toJson(message, requestType);

            connection.setConnectTimeout(30000);
            connection.setReadTimeout(30000);
            connection.setRequestMethod("POST");
            connection.setRequestProperty("Content-Type", "application/json");
            OutputStream outputStream = connection.getOutputStream();
            outputStream.write(jsonRequest.getBytes());
            outputStream.flush();

            String encoding = connection.getHeaderField("Content-Encoding");
            boolean gzipped = encoding != null && encoding.toLowerCase().contains("gzip");
            InputStreamReader reader;
            if (gzipped){
                InputStream gzipInputStream = new GZIPInputStream(connection.getInputStream());
                reader = new InputStreamReader(gzipInputStream);
            }else {
                reader = new InputStreamReader(connection.getInputStream());
            }

            GsonBuilder builder = new GsonBuilder();
            builder.registerTypeAdapter(Date.class, new JsonDeserializer<Date>() {
                public Date deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
                    return new Date(json.getAsJsonPrimitive().getAsLong());
                }
            });

            Gson gson = builder.create();
            responseMessage = gson.fromJson(reader, new TypeToken<ResponseMessage<RegistrationConfirmUserDataResponse>>() {}.getType());

        } catch (Exception e) {
            e.printStackTrace();
        }
        finally {
            if (connection != null)
                connection.disconnect();
        }
        return responseMessage;
    }



    public ResponseMessage<RegistrationCredentialsResponse> newRegistrationCredentialsResponse(RegistrationCredentialsRequest registrationCredentialsRequest){

        ResponseMessage<RegistrationCredentialsResponse> responseMessage = null;
        SSLConnection sslConnection = new SSLConnection(context, Constants.HTTPS_SERVER_IP + "/users/reg-credential-entry");
        HttpsURLConnection connection = sslConnection.setUpHttpsURLConnection();

        try {

            RequestHeader header = new RequestHeader();
            header.setAuthToken("008ewe");
            header.setVersion("1.0-PA");

            RequestMessage<RegistrationCredentialsRequest> message = new RequestMessage<RegistrationCredentialsRequest>();
            message.setRequestHeader(header);
            RegistrationCredentialsRequest request = registrationCredentialsRequest;
            request.setRequestUUID(UUID.randomUUID().toString());


            List<ContactDTO> contactDTOs = new ArrayList<ContactDTO>();
            Cursor phones = context.getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null,null,null, null);

            while (phones.moveToNext()) {
                String contact_name = phones.getString(phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME));
                String contact_phone_no = phones.getString(phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));

                if (contact_phone_no.trim().replace(" ", "").startsWith("00989")
                        || contact_phone_no.trim().replace(" ", "").startsWith("+989")
                        || contact_phone_no.trim().replace(" ", "").startsWith("09")) {
                    ContactDTO contactDTO = new ContactDTO();
                    contactDTO.setCellNumber(contact_phone_no);
                    contactDTO.setDisplayName(contact_name);
                    contactDTOs.add(contactDTO);
                }



            }
            phones.close();

            request.setContacts(contactDTOs);

            message.setService(request);

            Type requestType = new com.google.gson.reflect.TypeToken<RequestMessage<RegistrationCredentialsRequest>>() {}.getType();
            String jsonRequest = new Gson().toJson(message, requestType);

            connection.setConnectTimeout(30000);
            connection.setReadTimeout(30000);
            connection.setRequestMethod("POST");

            connection.setRequestProperty("Content-Encoding", "gzip");
            connection.setRequestProperty("Accept-Encoding", "gzip");

            OutputStream outputStream = connection.getOutputStream();
            outputStream.write(gzip(jsonRequest.getBytes()));
            outputStream.flush();

            String encoding = connection.getHeaderField("Content-Encoding");
            boolean gzipped = encoding != null && encoding.toLowerCase().contains("gzip");
            InputStreamReader reader;
            if (gzipped){
                InputStream gzipInputStream = new GZIPInputStream(connection.getInputStream());
                reader = new InputStreamReader(gzipInputStream);
            }else {
                reader = new InputStreamReader(connection.getInputStream());
            }

            Gson gson = new Gson();
            responseMessage = gson.fromJson(reader, new TypeToken<ResponseMessage<RegistrationCredentialsResponse>>() {}.getType());

        } catch (Exception e) {
            e.printStackTrace();
        }
        finally {
            if (connection != null)
                connection.disconnect();
        }
        return responseMessage;
    }


    public ResponseMessage<RegistrationVerifyAccountResponse> newRegistrationVerifyAccountResponse(RegistrationVerifyAccountRequest registrationVerifyAccountRequest){

        ResponseMessage<RegistrationVerifyAccountResponse> responseMessage = null;
        SSLConnection sslConnection = new SSLConnection(context, Constants.HTTPS_SERVER_IP + "/customers/reg-verify-account");
        HttpsURLConnection connection = sslConnection.setUpHttpsURLConnection();

        try {

            RequestHeader header = new RequestHeader();
            header.setAuthToken("008ewe");
            header.setVersion("1.0-PA");

            RequestMessage<RegistrationVerifyAccountRequest> message = new RequestMessage<RegistrationVerifyAccountRequest>();
            message.setRequestHeader(header);
            RegistrationVerifyAccountRequest request = registrationVerifyAccountRequest;
            request.setRequestUUID(UUID.randomUUID().toString());
            message.setService(request);

            Type requestType = new com.google.gson.reflect.TypeToken<RequestMessage<RegistrationVerifyAccountRequest>>() {}.getType();
            String jsonRequest = new Gson().toJson(message, requestType);

            connection.setConnectTimeout(30000);
            connection.setReadTimeout(30000);
            connection.setRequestMethod("POST");
            connection.setRequestProperty("Content-Type", "application/json");
            OutputStream outputStream = connection.getOutputStream();
            outputStream.write(jsonRequest.getBytes());
            outputStream.flush();

            String encoding = connection.getHeaderField("Content-Encoding");
            boolean gzipped = encoding != null && encoding.toLowerCase().contains("gzip");
            InputStreamReader reader;
            if (gzipped){
                InputStream gzipInputStream = new GZIPInputStream(connection.getInputStream());
                reader = new InputStreamReader(gzipInputStream);
            }else {
                reader = new InputStreamReader(connection.getInputStream());
            }

            Gson gson = new Gson();
            responseMessage = gson.fromJson(reader, new TypeToken<ResponseMessage<RegistrationVerifyAccountResponse>>() {}.getType());

        } catch (Exception e) {
            e.printStackTrace();
        }
        finally {
            if (connection != null)
                connection.disconnect();
        }
        return responseMessage;
    }


    public ResponseMessage<RegistrationVerifyTransferMoneyResponse> newRegistrationVerifyTransferMoneyResponse(RegistrationVerifyTransferMoneyRequest registrationVerifyTransferMoneyRequest){

        ResponseMessage<RegistrationVerifyTransferMoneyResponse> responseMessage = null;
        SSLConnection sslConnection = new SSLConnection(context, Constants.HTTPS_SERVER_IP + "/customers/reg-verify-xfer");
        HttpsURLConnection connection = sslConnection.setUpHttpsURLConnection();

        try {

            RequestHeader header = new RequestHeader();
            header.setAuthToken("008ewe");
            header.setVersion("1.0-PA");

            RequestMessage<RegistrationVerifyTransferMoneyRequest> message = new RequestMessage<RegistrationVerifyTransferMoneyRequest>();
            message.setRequestHeader(header);
            RegistrationVerifyTransferMoneyRequest request = registrationVerifyTransferMoneyRequest;
            request.setRequestUUID(UUID.randomUUID().toString());
            message.setService(request);

            Type requestType = new com.google.gson.reflect.TypeToken<RequestMessage<RegistrationVerifyTransferMoneyRequest>>() {}.getType();
            String jsonRequest = new Gson().toJson(message, requestType);

            connection.setConnectTimeout(30000);
            connection.setReadTimeout(30000);
            connection.setRequestMethod("POST");
            connection.setRequestProperty("Content-Type", "application/json");
            OutputStream outputStream = connection.getOutputStream();
            outputStream.write(jsonRequest.getBytes());
            outputStream.flush();

            String encoding = connection.getHeaderField("Content-Encoding");
            boolean gzipped = encoding != null && encoding.toLowerCase().contains("gzip");
            InputStreamReader reader;
            if (gzipped){
                InputStream gzipInputStream = new GZIPInputStream(connection.getInputStream());
                reader = new InputStreamReader(gzipInputStream);
            }else {
                reader = new InputStreamReader(connection.getInputStream());
            }

            Gson gson = new Gson();
            responseMessage = gson.fromJson(reader, new TypeToken<ResponseMessage<RegistrationVerifyTransferMoneyResponse>>() {}.getType());

        } catch (Exception e) {
            e.printStackTrace();
        }
        finally {
            if (connection != null)
                connection.disconnect();
        }
        return responseMessage;
    }



    public ResponseMessage<MobileRegistrationIdEntryResponse> newRegistrationDeviceRegId(MobileRegistrationIdEntryRequest mobileRegistrationIdEntryRequest){

        ResponseMessage<MobileRegistrationIdEntryResponse> responseMessage = null;
        SSLConnection sslConnection = new SSLConnection(context, Constants.HTTPS_SERVER_IP + "/users/mobile-reg-id-entry");
        HttpsURLConnection connection = sslConnection.setUpHttpsURLConnection();

        try {

            RequestHeader header = new RequestHeader();
            header.setAuthToken(prefs.getString(Constants.LOGIN_TOKEN_ID, ""));
            header.setVersion("1.0-PA");

            RequestMessage<MobileRegistrationIdEntryRequest> message = new RequestMessage<MobileRegistrationIdEntryRequest>();
            message.setRequestHeader(header);
            MobileRegistrationIdEntryRequest request = mobileRegistrationIdEntryRequest;
            request.setRequestUUID(UUID.randomUUID().toString());
            message.setService(request);

            Type requestType = new com.google.gson.reflect.TypeToken<RequestMessage<MobileRegistrationIdEntryRequest>>() {}.getType();
            String jsonRequest = new Gson().toJson(message, requestType);

            connection.setConnectTimeout(30000);
            connection.setReadTimeout(30000);
            connection.setRequestMethod("POST");
            connection.setRequestProperty("Content-Type", "application/json");
            OutputStream outputStream = connection.getOutputStream();
            outputStream.write(jsonRequest.getBytes());
            outputStream.flush();

            String encoding = connection.getHeaderField("Content-Encoding");
            boolean gzipped = encoding != null && encoding.toLowerCase().contains("gzip");
            InputStreamReader reader;
            if (gzipped){
                InputStream gzipInputStream = new GZIPInputStream(connection.getInputStream());
                reader = new InputStreamReader(gzipInputStream);
            }else {
                reader = new InputStreamReader(connection.getInputStream());
            }

            Gson gson = new Gson();
            responseMessage = gson.fromJson(reader, new TypeToken<ResponseMessage<MobileRegistrationIdEntryResponse>>() {}.getType());

        } catch (IOException e) {e.printStackTrace();}
        finally {
            if (connection != null)
                connection.disconnect();
        }
        return responseMessage;
    }


    public ResponseMessage<TACResponse> newTACResponse(TACRequest tacRequest){

        ResponseMessage<TACResponse> responseMessage = null;
        SSLConnection sslConnection = new SSLConnection(context, Constants.HTTPS_SERVER_IP + "/users/tac");
        HttpsURLConnection connection = sslConnection.setUpHttpsURLConnection();

        try {

            RequestHeader header = new RequestHeader();
            header.setAuthToken(prefs.getString(Constants.LOGIN_TOKEN_ID, ""));
            header.setVersion("1.0-PA");

            RequestMessage<TACRequest> message = new RequestMessage<TACRequest>();
            message.setRequestHeader(header);
            TACRequest request = tacRequest;
            request.setRequestUUID(UUID.randomUUID().toString());
            message.setService(request);

            Type requestType = new com.google.gson.reflect.TypeToken<RequestMessage<TACRequest>>() {}.getType();
            String jsonRequest = new Gson().toJson(message, requestType);

            connection.setConnectTimeout(30000);
            connection.setReadTimeout(30000);
            connection.setRequestMethod("POST");
            connection.setRequestProperty("Content-Type", "application/json");
            OutputStream outputStream = connection.getOutputStream();
            outputStream.write(jsonRequest.getBytes());
            outputStream.flush();

            String encoding = connection.getHeaderField("Content-Encoding");
            boolean gzipped = encoding != null && encoding.toLowerCase().contains("gzip");
            InputStreamReader reader;
            if (gzipped){
                InputStream gzipInputStream = new GZIPInputStream(connection.getInputStream());
                reader = new InputStreamReader(gzipInputStream);
            }else {
                reader = new InputStreamReader(connection.getInputStream());
            }

            GsonBuilder builder = new GsonBuilder();
            builder.registerTypeAdapter(Date.class, new JsonDeserializer<Date>() {
                public Date deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
                    return new Date(json.getAsJsonPrimitive().getAsLong());
                }
            });

            Gson gson = builder.create();
            responseMessage = gson.fromJson(reader, new TypeToken<ResponseMessage<TACResponse>>() {}.getType());



        } catch (Exception e) {
            e.printStackTrace();
        }
        finally {
            if (connection != null)
                connection.disconnect();
        }
        return responseMessage;
    }

    public ResponseMessage<UploadImageResponse> newUploadImage(UploadImageRequest uploadImageRequest){

        ResponseMessage<UploadImageResponse> responseMessage = null;
        SSLConnection sslConnection = new SSLConnection(context, Constants.HTTPS_SERVER_IP + "/users/upload-image");
        HttpsURLConnection connection = sslConnection.setUpHttpsURLConnection();

        try {

            RequestHeader header = new RequestHeader();
            header.setAuthToken(prefs.getString(Constants.LOGIN_TOKEN_ID, ""));
            header.setVersion("1.0-PA");

            RequestMessage<UploadImageRequest> message = new RequestMessage<UploadImageRequest>();
            message.setRequestHeader(header);
            UploadImageRequest request = uploadImageRequest;
            request.setRequestUUID(prefs.getString(Constants.UUID, ""));
            message.setService(request);

            Type requestType = new com.google.gson.reflect.TypeToken<RequestMessage<UploadImageRequest>>() {}.getType();
            String jsonRequest = new Gson().toJson(message, requestType);

            connection.setConnectTimeout(30000);
            connection.setReadTimeout(30000);
            connection.setRequestMethod("POST");
            connection.setRequestProperty("Content-Type", "application/json");
            OutputStream outputStream = connection.getOutputStream();
            outputStream.write(jsonRequest.getBytes());
            outputStream.flush();

            String encoding = connection.getHeaderField("Content-Encoding");
            boolean gzipped = encoding != null && encoding.toLowerCase().contains("gzip");
            InputStreamReader reader;
            if (gzipped){
                InputStream gzipInputStream = new GZIPInputStream(connection.getInputStream());
                reader = new InputStreamReader(gzipInputStream);
            }else {
                reader = new InputStreamReader(connection.getInputStream());
            }

            GsonBuilder builder = new GsonBuilder();
            builder.registerTypeAdapter(Date.class, new JsonDeserializer<Date>() {
                public Date deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
                    return new Date(json.getAsJsonPrimitive().getAsLong());
                }
            });

            Gson gson = builder.create();
            responseMessage = gson.fromJson(reader, new TypeToken<ResponseMessage<UploadImageResponse>>() {}.getType());



        } catch (Exception e) {
            e.printStackTrace();
        }
        finally {
            if (connection != null)
                connection.disconnect();
        }
        return responseMessage;
    }


    public ResponseMessage<GetUserIdTokenResponse> newGetUserIdTokenResponse(GetUserIdTokenRequest getUserIdTokenRequest){

        ResponseMessage<GetUserIdTokenResponse> responseMessage = null;
        SSLConnection sslConnection = new SSLConnection(context, Constants.HTTPS_SERVER_IP + "/users/get-user-id-token");
        HttpsURLConnection connection = sslConnection.setUpHttpsURLConnection();

        try {

            RequestHeader header = new RequestHeader();
            header.setAuthToken(prefs.getString(Constants.LOGIN_TOKEN_ID, ""));
            header.setVersion("1.0-PA");

            RequestMessage<GetUserIdTokenRequest> message = new RequestMessage<GetUserIdTokenRequest>();
            message.setRequestHeader(header);
            GetUserIdTokenRequest request = getUserIdTokenRequest;
            request.setRequestUUID(prefs.getString(Constants.UUID, ""));
            message.setService(request);

            Type requestType = new com.google.gson.reflect.TypeToken<RequestMessage<GetUserIdTokenRequest>>() {}.getType();
            String jsonRequest = new Gson().toJson(message, requestType);

            connection.setConnectTimeout(30000);
            connection.setReadTimeout(30000);
            connection.setRequestMethod("POST");
            connection.setRequestProperty("Content-Type", "application/json");
            OutputStream outputStream = connection.getOutputStream();
            outputStream.write(jsonRequest.getBytes());
            outputStream.flush();

            String encoding = connection.getHeaderField("Content-Encoding");
            boolean gzipped = encoding != null && encoding.toLowerCase().contains("gzip");
            InputStreamReader reader;
            if (gzipped){
                InputStream gzipInputStream = new GZIPInputStream(connection.getInputStream());
                reader = new InputStreamReader(gzipInputStream);
            }else {
                reader = new InputStreamReader(connection.getInputStream());
            }

            GsonBuilder builder = new GsonBuilder();
            builder.registerTypeAdapter(Date.class, new JsonDeserializer<Date>() {
                public Date deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
                    return new Date(json.getAsJsonPrimitive().getAsLong());
                }
            });

            Gson gson = builder.create();
            responseMessage = gson.fromJson(reader, new TypeToken<ResponseMessage<GetUserIdTokenResponse>>() {}.getType());



        } catch (Exception e) {
            e.printStackTrace();
        }
        finally {
            if (connection != null)
                connection.disconnect();
        }
        return responseMessage;
    }


    public ResponseMessage<TACAcceptResponse> newTACAcceptResponse(TACAcceptRequest tacAcceptRequest){

        ResponseMessage<TACAcceptResponse> responseMessage = null;
        SSLConnection sslConnection = new SSLConnection(context, Constants.HTTPS_SERVER_IP + "/users/tacaccept");
        HttpsURLConnection connection = sslConnection.setUpHttpsURLConnection();

        try {

            RequestHeader header = new RequestHeader();
            header.setAuthToken(prefs.getString(Constants.LOGIN_TOKEN_ID, ""));
            header.setVersion("1.0-PA");

            RequestMessage<TACAcceptRequest> message = new RequestMessage<TACAcceptRequest>();
            message.setRequestHeader(header);
            TACAcceptRequest request = tacAcceptRequest;
            request.setRequestUUID(UUID.randomUUID().toString());
            message.setService(request);

            Type requestType = new com.google.gson.reflect.TypeToken<RequestMessage<TACAcceptRequest>>() {}.getType();
            String jsonRequest = new Gson().toJson(message, requestType);

            connection.setConnectTimeout(30000);
            connection.setReadTimeout(30000);
            connection.setRequestMethod("POST");
            connection.setRequestProperty("Content-Type", "application/json");
            OutputStream outputStream = connection.getOutputStream();
            outputStream.write(jsonRequest.getBytes());
            outputStream.flush();

            String encoding = connection.getHeaderField("Content-Encoding");
            boolean gzipped = encoding != null && encoding.toLowerCase().contains("gzip");
            InputStreamReader reader;
            if (gzipped){
                InputStream gzipInputStream = new GZIPInputStream(connection.getInputStream());
                reader = new InputStreamReader(gzipInputStream);
            }else {
                reader = new InputStreamReader(connection.getInputStream());
            }

            GsonBuilder builder = new GsonBuilder();
            builder.registerTypeAdapter(Date.class, new JsonDeserializer<Date>() {
                public Date deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
                    return new Date(json.getAsJsonPrimitive().getAsLong());
                }
            });

            Gson gson = builder.create();
            responseMessage = gson.fromJson(reader, new TypeToken<ResponseMessage<TACAcceptResponse>>() {}.getType());

        } catch (Exception e) {
            e.printStackTrace();
        }
        finally {
            if (connection != null)
                connection.disconnect();
        }
        return responseMessage;
    }


    public ResponseMessage<UserProfileResponse> newGetUserProfile(UserProfileRequest userProfileRequest){

        ResponseMessage<UserProfileResponse> responseMessage = null;
        SSLConnection sslConnection = new SSLConnection(context, Constants.HTTPS_SERVER_IP + "/users/profile");
        HttpsURLConnection connection = sslConnection.setUpHttpsURLConnection();

        try {

            RequestHeader header = new RequestHeader();
            header.setAuthToken(prefs.getString(Constants.LOGIN_TOKEN_ID, ""));
            header.setVersion("1.0-PA");

            RequestMessage<UserProfileRequest> message = new RequestMessage<UserProfileRequest>();
            message.setRequestHeader(header);
            UserProfileRequest request = userProfileRequest;
            request.setRequestUUID(UUID.randomUUID().toString());
            message.setService(request);

            Type requestType = new com.google.gson.reflect.TypeToken<RequestMessage<UserProfileRequest>>() {}.getType();
            String jsonRequest = new Gson().toJson(message, requestType);

            connection.setConnectTimeout(30000);
            connection.setReadTimeout(30000);
            connection.setRequestMethod("POST");
            connection.setRequestProperty("Content-Type", "application/json");
            OutputStream outputStream = connection.getOutputStream();
            outputStream.write(jsonRequest.getBytes());
            outputStream.flush();

            String encoding = connection.getHeaderField("Content-Encoding");
            boolean gzipped = encoding != null && encoding.toLowerCase().contains("gzip");
            InputStreamReader reader;
            if (gzipped){
                InputStream gzipInputStream = new GZIPInputStream(connection.getInputStream());
                reader = new InputStreamReader(gzipInputStream);
            }else {
                reader = new InputStreamReader(connection.getInputStream());
            }

            GsonBuilder builder = new GsonBuilder();
            builder.registerTypeAdapter(Date.class, new JsonDeserializer<Date>() {
                public Date deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
                    return new Date(json.getAsJsonPrimitive().getAsLong());
                }
            });

            Gson gson = builder.create();
            responseMessage = gson.fromJson(reader, new TypeToken<ResponseMessage<UserProfileResponse>>() {}.getType());

        } catch (Exception e) {
            e.printStackTrace();
        }
        finally {
            if (connection != null)
                connection.disconnect();
        }
        return responseMessage;
    }


    public ResponseMessage<VerifyAccountResponse> newVerifyAccountResponse(VerifyAccountRequest verifyAccountRequest){

        ResponseMessage<VerifyAccountResponse> responseMessage = null;
        SSLConnection sslConnection = new SSLConnection(context, Constants.HTTPS_SERVER_IP + "/customers/verify-account");
        HttpsURLConnection connection = sslConnection.setUpHttpsURLConnection();

        try {

            RequestHeader header = new RequestHeader();
            header.setAuthToken(prefs.getString(Constants.LOGIN_TOKEN_ID, ""));
            header.setVersion("1.0-PA");

            RequestMessage<VerifyAccountRequest> message = new RequestMessage<VerifyAccountRequest>();
            message.setRequestHeader(header);
            VerifyAccountRequest request = verifyAccountRequest;
            request.setRequestUUID(UUID.randomUUID().toString());
            message.setService(request);

            Type requestType = new com.google.gson.reflect.TypeToken<RequestMessage<VerifyAccountRequest>>() {}.getType();
            String jsonRequest = new Gson().toJson(message, requestType);

            connection.setConnectTimeout(30000);
            connection.setReadTimeout(30000);
            connection.setRequestMethod("POST");
            connection.setRequestProperty("Content-Type", "application/json");
            OutputStream outputStream = connection.getOutputStream();
            outputStream.write(jsonRequest.getBytes());
            outputStream.flush();

            String encoding = connection.getHeaderField("Content-Encoding");
            boolean gzipped = encoding != null && encoding.toLowerCase().contains("gzip");
            InputStreamReader reader;
            if (gzipped){
                InputStream gzipInputStream = new GZIPInputStream(connection.getInputStream());
                reader = new InputStreamReader(gzipInputStream);
            }else {
                reader = new InputStreamReader(connection.getInputStream());
            }

            GsonBuilder builder = new GsonBuilder();
            builder.registerTypeAdapter(Date.class, new JsonDeserializer<Date>() {
                public Date deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
                    return new Date(json.getAsJsonPrimitive().getAsLong());
                }
            });

            Gson gson = builder.create();
            responseMessage = gson.fromJson(reader, new TypeToken<ResponseMessage<VerifyAccountResponse>>() {}.getType());

        } catch (Exception e) {
            e.printStackTrace();
        }
        finally {
            if (connection != null)
                connection.disconnect();
        }
        return responseMessage;
    }


    public ResponseMessage<VerifyTransferMoneyResponse> newVerifyTransferMoneyResponse(VerifyTransferMoneyRequest verifyTransferMoneyRequest){

        ResponseMessage<VerifyTransferMoneyResponse> responseMessage = null;
        SSLConnection sslConnection = new SSLConnection(context, Constants.HTTPS_SERVER_IP + "/customers/verify-xfer");
        HttpsURLConnection connection = sslConnection.setUpHttpsURLConnection();

        try {

            RequestHeader header = new RequestHeader();
            header.setAuthToken(prefs.getString(Constants.LOGIN_TOKEN_ID, ""));
            header.setVersion("1.0-PA");

            RequestMessage<VerifyTransferMoneyRequest> message = new RequestMessage<VerifyTransferMoneyRequest>();
            message.setRequestHeader(header);
            VerifyTransferMoneyRequest request = verifyTransferMoneyRequest;
            request.setRequestUUID(UUID.randomUUID().toString());
            message.setService(request);

            Type requestType = new com.google.gson.reflect.TypeToken<RequestMessage<VerifyTransferMoneyRequest>>() {}.getType();
            String jsonRequest = new Gson().toJson(message, requestType);

            connection.setConnectTimeout(30000);
            connection.setReadTimeout(30000);
            connection.setRequestMethod("POST");
            connection.setRequestProperty("Content-Type", "application/json");
            OutputStream outputStream = connection.getOutputStream();
            outputStream.write(jsonRequest.getBytes());
            outputStream.flush();

            String encoding = connection.getHeaderField("Content-Encoding");
            boolean gzipped = encoding != null && encoding.toLowerCase().contains("gzip");
            InputStreamReader reader;
            if (gzipped){
                InputStream gzipInputStream = new GZIPInputStream(connection.getInputStream());
                reader = new InputStreamReader(gzipInputStream);
            }else {
                reader = new InputStreamReader(connection.getInputStream());
            }

            GsonBuilder builder = new GsonBuilder();
            builder.registerTypeAdapter(Date.class, new JsonDeserializer<Date>() {
                public Date deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
                    return new Date(json.getAsJsonPrimitive().getAsLong());
                }
            });

            Gson gson = builder.create();
            responseMessage = gson.fromJson(reader, new TypeToken<ResponseMessage<VerifyTransferMoneyResponse>>() {}.getType());

        } catch (Exception e) {
            e.printStackTrace();
        }
        finally {
            if (connection != null)
                connection.disconnect();
        }
        return responseMessage;
    }


    public ResponseMessage<TransactionListResponse> newGetUserTransaction(TransactionListRequest transactionListRequest){

        ResponseMessage<TransactionListResponse> responseMessage = null;
        SSLConnection sslConnection = new SSLConnection(context, Constants.HTTPS_SERVER_IP + "/transactions");
        HttpsURLConnection connection = sslConnection.setUpHttpsURLConnection();

        try {

            RequestHeader header = new RequestHeader();
            header.setAuthToken(prefs.getString(Constants.LOGIN_TOKEN_ID, ""));
            header.setVersion("1.0-PA");

            RequestMessage<TransactionListRequest> message = new RequestMessage<TransactionListRequest>();
            message.setRequestHeader(header);
            TransactionListRequest request = new TransactionListRequest();
            request.setUserId(new PersianEnglishDigit(prefs.getString(Constants.REGISTERED_NATIONAL_CODE, "")).P2E());
            request.setPageSize(transactionListRequest.getPageSize());
            request.setPageNumber(transactionListRequest.getPageNumber());
            request.setRequestUUID(UUID.randomUUID().toString());
            message.setService(request);

            Type requestType = new com.google.gson.reflect.TypeToken<RequestMessage<TransactionListRequest>>() {}.getType();
            String jsonRequest = new Gson().toJson(message, requestType);

            connection.setConnectTimeout(30000);
            connection.setReadTimeout(30000);
            connection.setRequestMethod("POST");
            connection.setRequestProperty("Content-Type", "application/json");
            OutputStream outputStream = connection.getOutputStream();
            outputStream.write(jsonRequest.getBytes());
            outputStream.flush();

            String encoding = connection.getHeaderField("Content-Encoding");
            boolean gzipped = encoding != null && encoding.toLowerCase().contains("gzip");
            InputStreamReader reader;
            if (gzipped){
                InputStream gzipInputStream = new GZIPInputStream(connection.getInputStream());
                reader = new InputStreamReader(gzipInputStream);
            }else {
                reader = new InputStreamReader(connection.getInputStream());
            }

            GsonBuilder builder = new GsonBuilder();
            builder.registerTypeAdapter(Date.class, new JsonDeserializer<Date>() {
                public Date deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
                    return new Date(json.getAsJsonPrimitive().getAsLong());
                }
            });

            Gson gson = builder.create();
            responseMessage = gson.fromJson(reader, new TypeToken<ResponseMessage<TransactionListResponse>>() {}.getType());

        } catch (Exception e) {
            e.printStackTrace();
        }
        finally {
            if (connection != null)
                connection.disconnect();
        }
        return responseMessage;
    }


    public ResponseMessage<ContactsHampayEnabledResponse> newGetEnabledHamPayContacts(ContactsHampayEnabledRequest contactsHampayEnabledRequest){

        ResponseMessage<ContactsHampayEnabledResponse> responseMessage = null;
        SSLConnection sslConnection = new SSLConnection(context, Constants.HTTPS_SERVER_IP + "/customer/contacts/hp-enabled");
        HttpsURLConnection connection = sslConnection.setUpHttpsURLConnection();

        try {

            RequestHeader header = new RequestHeader();
            header.setAuthToken(prefs.getString(Constants.LOGIN_TOKEN_ID, ""));
            header.setVersion("1.0-PA");

            RequestMessage<ContactsHampayEnabledRequest> message = new RequestMessage<ContactsHampayEnabledRequest>();
            message.setRequestHeader(header);
            ContactsHampayEnabledRequest request = new ContactsHampayEnabledRequest();


            List<ContactDTO> contactDTOs = new ArrayList<ContactDTO>();
            Cursor phones = context.getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null,null,null, null);


            while (phones.moveToNext()) {
                String contact_name = phones.getString(phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME));
                String contact_phone_no = phones.getString(phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));

                if (contact_phone_no.trim().replace(" ", "").startsWith("00989")
                        || contact_phone_no.trim().replace(" ", "").startsWith("+989")
                        || contact_phone_no.trim().replace(" ", "").startsWith("09")) {

                    ContactDTO contactDTO = new ContactDTO();
                    contactDTO.setCellNumber(contact_phone_no);
                    contactDTO.setDisplayName(contact_name);
                    contactDTOs.add(contactDTO);

                }

            }
            phones.close();

            request.setContacts(contactDTOs);

            request.setRequestUUID(UUID.randomUUID().toString());
            message.setService(request);


            Type requestType = new com.google.gson.reflect.TypeToken<RequestMessage<ContactsHampayEnabledRequest>>() {}.getType();
            String jsonRequest = new Gson().toJson(message, requestType);



            connection.setDoOutput(false);
            connection.setConnectTimeout(30000);
            connection.setReadTimeout(30000);
            connection.setRequestMethod("POST");
            connection.setRequestProperty("Content-Encoding", "gzip");
            connection.setRequestProperty("Accept-Encoding", "gzip");

            OutputStream outputStream = connection.getOutputStream();
            outputStream.write(gzip(jsonRequest.getBytes()));
            outputStream.flush();

            String encoding = connection.getHeaderField("Content-Encoding");
            boolean gzipped = encoding != null && encoding.toLowerCase().contains("gzip");
            InputStreamReader reader;
            if (gzipped){
                InputStream gzipInputStream = new GZIPInputStream(connection.getInputStream());
                reader = new InputStreamReader(gzipInputStream);
            }else {
                reader = new InputStreamReader(connection.getInputStream());
            }

            GsonBuilder builder = new GsonBuilder();
            builder.registerTypeAdapter(Date.class, new JsonDeserializer<Date>() {
                public Date deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
                    return new Date(json.getAsJsonPrimitive().getAsLong());
                }
            });

            Gson gson = builder.create();
            responseMessage = gson.fromJson(reader, new TypeToken<ResponseMessage<ContactsHampayEnabledResponse>>() {}.getType());

        } catch (Exception e) {
            e.printStackTrace();
        }
        finally {
            if (connection != null)
                connection.disconnect();
        }
        return responseMessage;
    }


    public ResponseMessage<IndividualPaymentConfirmResponse> newIndividualPaymentConfirm(IndividualPaymentConfirmRequest individualPaymentConfirmRequest){

        ResponseMessage<IndividualPaymentConfirmResponse> responseMessage = null;
        SSLConnection sslConnection = new SSLConnection(context, Constants.HTTPS_SERVER_IP + "/customers/individual-payment-confirm");
        HttpsURLConnection connection = sslConnection.setUpHttpsURLConnection();

        try {

            RequestHeader header = new RequestHeader();
            header.setAuthToken(prefs.getString(Constants.LOGIN_TOKEN_ID, ""));
            header.setVersion("1.0-PA");

            RequestMessage<IndividualPaymentConfirmRequest> message = new RequestMessage<IndividualPaymentConfirmRequest>();
            message.setRequestHeader(header);

            individualPaymentConfirmRequest.setRequestUUID(prefs.getString(Constants.UUID, ""));
            message.setService(individualPaymentConfirmRequest);

            Type requestType = new com.google.gson.reflect.TypeToken<RequestMessage<IndividualPaymentConfirmRequest>>() {}.getType();
            String jsonRequest = new Gson().toJson(message, requestType);

            connection.setConnectTimeout(30000);
            connection.setReadTimeout(30000);
            connection.setRequestMethod("POST");
            connection.setRequestProperty("Content-Type", "application/json");
            OutputStream outputStream = connection.getOutputStream();
            outputStream.write(jsonRequest.getBytes());
            outputStream.flush();

            String encoding = connection.getHeaderField("Content-Encoding");
            boolean gzipped = encoding != null && encoding.toLowerCase().contains("gzip");
            InputStreamReader reader;
            if (gzipped){
                InputStream gzipInputStream = new GZIPInputStream(connection.getInputStream());
                reader = new InputStreamReader(gzipInputStream);
            }else {
                reader = new InputStreamReader(connection.getInputStream());
            }

            GsonBuilder builder = new GsonBuilder();
            builder.registerTypeAdapter(Date.class, new JsonDeserializer<Date>() {
                public Date deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
                    return new Date(json.getAsJsonPrimitive().getAsLong());
                }
            });

            Gson gson = builder.create();
            responseMessage = gson.fromJson(reader, new TypeToken<ResponseMessage<IndividualPaymentConfirmResponse>>() {}.getType());

        } catch (Exception e) {
            e.printStackTrace();
        }
        finally {
            if (connection != null)
                connection.disconnect();
        }
        return responseMessage;
    }

    public ResponseMessage<IndividualPaymentResponse> newIndividualPayment(IndividualPaymentRequest individualPaymentRequest){

        ResponseMessage<IndividualPaymentResponse> responseMessage = null;
        SSLConnection sslConnection = new SSLConnection(context, Constants.HTTPS_SERVER_IP + "/customers/individual-payment");
        HttpsURLConnection connection = sslConnection.setUpHttpsURLConnection();

        try {

            RequestHeader header = new RequestHeader();
            header.setAuthToken(prefs.getString(Constants.LOGIN_TOKEN_ID, ""));
            header.setVersion("1.0-PA");

            RequestMessage<IndividualPaymentRequest> message = new RequestMessage<IndividualPaymentRequest>();
            message.setRequestHeader(header);
            IndividualPaymentRequest request = new IndividualPaymentRequest();

            request.setCellNumber(individualPaymentRequest.getCellNumber());
            request.setAmount(individualPaymentRequest.getAmount());
            request.setMessage(individualPaymentRequest.getMessage());

            request.setRequestUUID(UUID.randomUUID().toString());
            message.setService(request);

            Type requestType = new com.google.gson.reflect.TypeToken<RequestMessage<IndividualPaymentRequest>>() {}.getType();
            String jsonRequest = new Gson().toJson(message, requestType);

            connection.setConnectTimeout(30000);
            connection.setReadTimeout(30000);
            connection.setRequestMethod("POST");
            connection.setRequestProperty("Content-Type", "application/json");
            OutputStream outputStream = connection.getOutputStream();
            outputStream.write(jsonRequest.getBytes());
            outputStream.flush();

            String encoding = connection.getHeaderField("Content-Encoding");
            boolean gzipped = encoding != null && encoding.toLowerCase().contains("gzip");
            InputStreamReader reader;
            if (gzipped){
                InputStream gzipInputStream = new GZIPInputStream(connection.getInputStream());
                reader = new InputStreamReader(gzipInputStream);
            }else {
                reader = new InputStreamReader(connection.getInputStream());
            }

            GsonBuilder builder = new GsonBuilder();
            builder.registerTypeAdapter(Date.class, new JsonDeserializer<Date>() {
                public Date deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
                    return new Date(json.getAsJsonPrimitive().getAsLong());
                }
            });

            Gson gson = builder.create();
            responseMessage = gson.fromJson(reader, new TypeToken<ResponseMessage<IndividualPaymentResponse>>() {}.getType());

        } catch (Exception e) {
            e.printStackTrace();
        }
        finally {
            if (connection != null)
                connection.disconnect();
        }
        return responseMessage;
    }


    public ResponseMessage<BusinessListResponse> newGetHamPayBusiness(BusinessListRequest businessListRequest){

        ResponseMessage<BusinessListResponse> responseMessage = null;
        SSLConnection sslConnection = new SSLConnection(context, Constants.HTTPS_SERVER_IP + "/businesses");
        HttpsURLConnection connection = sslConnection.setUpHttpsURLConnection();

        try {

            RequestHeader header = new RequestHeader();
            header.setAuthToken(prefs.getString(Constants.LOGIN_TOKEN_ID, ""));
            header.setVersion("1.0-PA");

            RequestMessage<BusinessListRequest> message = new RequestMessage<BusinessListRequest>();
            message.setRequestHeader(header);
            BusinessListRequest request = new BusinessListRequest();

            request.setPageNumber(businessListRequest.getPageNumber());
            request.setPageSize(businessListRequest.getPageSize());

            request.setRequestUUID(UUID.randomUUID().toString());

            message.setService(request);

            Type requestType = new com.google.gson.reflect.TypeToken<RequestMessage<BusinessListRequest>>() {}.getType();
            String jsonRequest = new Gson().toJson(message, requestType);

            connection.setConnectTimeout(30000);
            connection.setReadTimeout(30000);
            connection.setRequestMethod("POST");
            connection.setRequestProperty("Content-Type", "application/json");
            OutputStream outputStream = connection.getOutputStream();
            outputStream.write(jsonRequest.getBytes());
            outputStream.flush();

            String encoding = connection.getHeaderField("Content-Encoding");
            boolean gzipped = encoding != null && encoding.toLowerCase().contains("gzip");
            InputStreamReader reader;
            if (gzipped){
                InputStream gzipInputStream = new GZIPInputStream(connection.getInputStream());
                reader = new InputStreamReader(gzipInputStream);
            }else {
                reader = new InputStreamReader(connection.getInputStream());
            }

            GsonBuilder builder = new GsonBuilder();
            builder.registerTypeAdapter(Date.class, new JsonDeserializer<Date>() {
                public Date deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
                    return new Date(json.getAsJsonPrimitive().getAsLong());
                }
            });

            Gson gson = builder.create();
            responseMessage = gson.fromJson(reader, new TypeToken<ResponseMessage<BusinessListResponse>>() {}.getType());

        } catch (Exception e) {
            e.printStackTrace();
        }
        finally {
            if (connection != null)
                connection.disconnect();
        }
        return responseMessage;
    }


    public ResponseMessage<BusinessPaymentConfirmResponse> newBusinessPaymentConfirm(BusinessPaymentConfirmRequest businessPaymentConfirmRequest){

        ResponseMessage<BusinessPaymentConfirmResponse> responseMessage = null;
        SSLConnection sslConnection = new SSLConnection(context, Constants.HTTPS_SERVER_IP + "/businesses/business-payment-confirm");
        HttpsURLConnection connection = sslConnection.setUpHttpsURLConnection();

        try {

            RequestHeader header = new RequestHeader();
            header.setAuthToken(prefs.getString(Constants.LOGIN_TOKEN_ID, ""));
            header.setVersion("1.0-PA");

            RequestMessage<BusinessPaymentConfirmRequest> message = new RequestMessage<BusinessPaymentConfirmRequest>();
            message.setRequestHeader(header);
            BusinessPaymentConfirmRequest request = new BusinessPaymentConfirmRequest();

            request.setBusinessCode(businessPaymentConfirmRequest.getBusinessCode());

            request.setRequestUUID(UUID.randomUUID().toString());
            message.setService(request);

            Type requestType = new com.google.gson.reflect.TypeToken<RequestMessage<BusinessPaymentConfirmRequest>>() {}.getType();
            String jsonRequest = new Gson().toJson(message, requestType);

            connection.setConnectTimeout(30000);
            connection.setReadTimeout(30000);
            connection.setRequestMethod("POST");
            connection.setRequestProperty("Content-Type", "application/json");
            OutputStream outputStream = connection.getOutputStream();
            outputStream.write(jsonRequest.getBytes());
            outputStream.flush();

            String encoding = connection.getHeaderField("Content-Encoding");
            boolean gzipped = encoding != null && encoding.toLowerCase().contains("gzip");
            InputStreamReader reader;
            if (gzipped){
                InputStream gzipInputStream = new GZIPInputStream(connection.getInputStream());
                reader = new InputStreamReader(gzipInputStream);
            }else {
                reader = new InputStreamReader(connection.getInputStream());
            }

            GsonBuilder builder = new GsonBuilder();
            builder.registerTypeAdapter(Date.class, new JsonDeserializer<Date>() {
                public Date deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
                    return new Date(json.getAsJsonPrimitive().getAsLong());
                }
            });

            Gson gson = builder.create();
            responseMessage = gson.fromJson(reader, new TypeToken<ResponseMessage<BusinessPaymentConfirmResponse>>() {}.getType());

        } catch (Exception e) {
            e.printStackTrace();
        }
        finally {
            if (connection != null)
                connection.disconnect();
        }
        return responseMessage;
    }


    public ResponseMessage<BusinessPaymentResponse> newBusinessPayment(BusinessPaymentRequest businessPaymentRequest){

        ResponseMessage<BusinessPaymentResponse> responseMessage = null;
        SSLConnection sslConnection = new SSLConnection(context, Constants.HTTPS_SERVER_IP + "/customers/business-payment");
        HttpsURLConnection connection = sslConnection.setUpHttpsURLConnection();

        try {

            RequestHeader header = new RequestHeader();
            header.setAuthToken(prefs.getString(Constants.LOGIN_TOKEN_ID, ""));
            header.setVersion("1.0-PA");

            RequestMessage<BusinessPaymentRequest> message = new RequestMessage<BusinessPaymentRequest>();
            message.setRequestHeader(header);
            BusinessPaymentRequest request = new BusinessPaymentRequest();

            request.setBusinessCode(businessPaymentRequest.getBusinessCode());
            request.setAmount(businessPaymentRequest.getAmount());
            request.setMessage(businessPaymentRequest.getMessage());
            request.setRequestUUID(UUID.randomUUID().toString());
            message.setService(request);

            Type requestType = new com.google.gson.reflect.TypeToken<RequestMessage<BusinessPaymentRequest>>() {}.getType();
            String jsonRequest = new Gson().toJson(message, requestType);

            connection.setConnectTimeout(30000);
            connection.setReadTimeout(30000);
            connection.setRequestMethod("POST");
            connection.setRequestProperty("Content-Type", "application/json");
            OutputStream outputStream = connection.getOutputStream();
            outputStream.write(jsonRequest.getBytes());
            outputStream.flush();

            String encoding = connection.getHeaderField("Content-Encoding");
            boolean gzipped = encoding != null && encoding.toLowerCase().contains("gzip");
            InputStreamReader reader;
            if (gzipped){
                InputStream gzipInputStream = new GZIPInputStream(connection.getInputStream());
                reader = new InputStreamReader(gzipInputStream);
            }else {
                reader = new InputStreamReader(connection.getInputStream());
            }

            GsonBuilder builder = new GsonBuilder();
            builder.registerTypeAdapter(Date.class, new JsonDeserializer<Date>() {
                public Date deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
                    return new Date(json.getAsJsonPrimitive().getAsLong());
                }
            });

            Gson gson = builder.create();
            responseMessage = gson.fromJson(reader, new TypeToken<ResponseMessage<BusinessPaymentResponse>>() {}.getType());

        } catch (Exception e) {
            e.printStackTrace();
        }
        finally {
            if (connection != null)
                connection.disconnect();
        }
        return responseMessage;
    }



    public ResponseMessage<BusinessListResponse> newSearchBusinessList(BusinessSearchRequest businessSearchRequest){

        ResponseMessage<BusinessListResponse> responseMessage = null;
        SSLConnection sslConnection = new SSLConnection(context, Constants.HTTPS_SERVER_IP + "/search");
        HttpsURLConnection connection = sslConnection.setUpHttpsURLConnection();

        try {

            RequestHeader header = new RequestHeader();
            header.setAuthToken(prefs.getString(Constants.LOGIN_TOKEN_ID, ""));
            header.setVersion("1.0-PA");

            RequestMessage<BusinessSearchRequest> message = new RequestMessage<BusinessSearchRequest>();
            message.setRequestHeader(header);
            BusinessSearchRequest request = businessSearchRequest;

            request.setRequestUUID(UUID.randomUUID().toString());

            message.setService(request);

            Type requestType = new com.google.gson.reflect.TypeToken<RequestMessage<BusinessListRequest>>() {}.getType();
            String jsonRequest = new Gson().toJson(message, requestType);

            connection.setConnectTimeout(30000);
            connection.setReadTimeout(30000);
            connection.setRequestMethod("POST");
            connection.setRequestProperty("Content-Type", "application/json");
            OutputStream outputStream = connection.getOutputStream();
            outputStream.write(jsonRequest.getBytes());
            outputStream.flush();

            String encoding = connection.getHeaderField("Content-Encoding");
            boolean gzipped = encoding != null && encoding.toLowerCase().contains("gzip");
            InputStreamReader reader;
            if (gzipped){
                InputStream gzipInputStream = new GZIPInputStream(connection.getInputStream());
                reader = new InputStreamReader(gzipInputStream);
            }else {
                reader = new InputStreamReader(connection.getInputStream());
            }

            GsonBuilder builder = new GsonBuilder();
            builder.registerTypeAdapter(Date.class, new JsonDeserializer<Date>() {
                public Date deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
                    return new Date(json.getAsJsonPrimitive().getAsLong());
                }
            });

            Gson gson = builder.create();
            responseMessage = gson.fromJson(reader, new TypeToken<ResponseMessage<BusinessListResponse>>() {}.getType());

        } catch (Exception e) {
            e.printStackTrace();
        }
        finally {
            if (connection != null)
                connection.disconnect();
        }
        return responseMessage;
    }





    public ResponseMessage<ChangePassCodeResponse> changePassCodeResponse(ChangePassCodeRequest changePassCodeRequest) {

        ResponseMessage<ChangePassCodeResponse> responseMessage = null;

        SSLConnection sslConnection = new SSLConnection(context, Constants.HTTPS_SERVER_IP + "/users/passcode");
        HttpsURLConnection connection = sslConnection.setUpHttpsURLConnection();

        try {

            RequestHeader header = new RequestHeader();
            header.setVersion("1.0-PA");
            header.setAuthToken(prefs.getString(Constants.LOGIN_TOKEN_ID, ""));

            RequestMessage<ChangePassCodeRequest> message = new RequestMessage<ChangePassCodeRequest>();
            message.setRequestHeader(header);
            ChangePassCodeRequest request = changePassCodeRequest;
            request.setRequestUUID(UUID.randomUUID().toString());

            message.setService(request);

            Type requestType = new com.google.gson.reflect.TypeToken<RequestMessage<ChangePassCodeRequest>>() {}.getType();
            String jsonRequest = new Gson().toJson(message, requestType);


            connection.setDoOutput(true);
            connection.setRequestMethod("PUT");
            connection.setRequestProperty("Content-Type", "application/json");
            OutputStream outputStream = connection.getOutputStream();
            outputStream.write(jsonRequest.getBytes());
            outputStream.flush();

            String encoding = connection.getHeaderField("Content-Encoding");
            boolean gzipped = encoding != null && encoding.toLowerCase().contains("gzip");
            InputStreamReader reader;
            if (gzipped){
                InputStream gzipInputStream = new GZIPInputStream(connection.getInputStream());
                reader = new InputStreamReader(gzipInputStream);
            }else {
                reader = new InputStreamReader(connection.getInputStream());
            }

            Gson gson = new Gson();
            responseMessage = gson.fromJson(reader, new TypeToken<ResponseMessage<ChangePassCodeResponse>>() {}.getType());

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (connection != null)
                connection.disconnect();
        }
        return responseMessage;
    }

    public ResponseMessage<ChangeMemorableWordResponse> changeMemorableWordResponse(ChangeMemorableWordRequest changeMemorableWordRequest) {

        ResponseMessage<ChangeMemorableWordResponse> responseMessage = null;

        SSLConnection sslConnection = new SSLConnection(context, Constants.HTTPS_SERVER_IP + "/users/memorable-word");
        HttpsURLConnection connection = sslConnection.setUpHttpsURLConnection();

        try {

            RequestHeader header = new RequestHeader();
            header.setAuthToken(prefs.getString(Constants.LOGIN_TOKEN_ID, ""));

            header.setVersion("1.0-PA");

            RequestMessage<ChangeMemorableWordRequest> message = new RequestMessage<ChangeMemorableWordRequest>();
            message.setRequestHeader(header);
            ChangeMemorableWordRequest request = changeMemorableWordRequest;
            request.setRequestUUID(UUID.randomUUID().toString());
            message.setService(request);

            Type requestType = new com.google.gson.reflect.TypeToken<RequestMessage<ChangeMemorableWordRequest>>() {}.getType();
            String jsonRequest = new Gson().toJson(message, requestType);

            connection.setDoOutput(true);
            connection.setRequestMethod("PUT");
            connection.setRequestProperty("Content-Type", "application/json");
            OutputStream outputStream = connection.getOutputStream();
            outputStream.write(jsonRequest.getBytes());
            outputStream.flush();

            String encoding = connection.getHeaderField("Content-Encoding");
            boolean gzipped = encoding != null && encoding.toLowerCase().contains("gzip");
            InputStreamReader reader;
            if (gzipped){
                InputStream gzipInputStream = new GZIPInputStream(connection.getInputStream());
                reader = new InputStreamReader(gzipInputStream);
            }else {
                reader = new InputStreamReader(connection.getInputStream());
            }

            Gson gson = new Gson();
            responseMessage = gson.fromJson(reader, new TypeToken<ResponseMessage<ChangeMemorableWordResponse>>() {}.getType());

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (connection != null)
                connection.disconnect();
        }

        return responseMessage;

    }



    public ResponseMessage<UnlinkUserResponse> unlinkUserResponse(UnlinkUserRequest unlinkUserRequest) {

        ResponseMessage<UnlinkUserResponse> responseMessage = null;

        SSLConnection sslConnection = new SSLConnection(context, Constants.HTTPS_SERVER_IP + "/users/unlink");
        HttpsURLConnection connection = sslConnection.setUpHttpsURLConnection();

        try {

            RequestHeader header = new RequestHeader();
            header.setAuthToken(prefs.getString(Constants.LOGIN_TOKEN_ID, ""));

            header.setVersion("1.0-PA");

            RequestMessage<UnlinkUserRequest> message = new RequestMessage<UnlinkUserRequest>();
            message.setRequestHeader(header);
            UnlinkUserRequest request = unlinkUserRequest;
            request.setRequestUUID(prefs.getString(Constants.UUID, ""));
            message.setService(request);

            Type requestType = new com.google.gson.reflect.TypeToken<RequestMessage<UnlinkUserRequest>>() {}.getType();
            String jsonRequest = new Gson().toJson(message, requestType);

            connection.setDoOutput(true);
            connection.setRequestMethod("POST");
            connection.setRequestProperty("Content-Type", "application/json");
            OutputStream outputStream = connection.getOutputStream();
            outputStream.write(jsonRequest.getBytes());
            outputStream.flush();

            String encoding = connection.getHeaderField("Content-Encoding");
            boolean gzipped = encoding != null && encoding.toLowerCase().contains("gzip");
            InputStreamReader reader;
            if (gzipped){
                InputStream gzipInputStream = new GZIPInputStream(connection.getInputStream());
                reader = new InputStreamReader(gzipInputStream);
            }else {
                reader = new InputStreamReader(connection.getInputStream());
            }

            Gson gson = new Gson();
            responseMessage = gson.fromJson(reader, new TypeToken<ResponseMessage<UnlinkUserResponse>>() {}.getType());

            if( responseMessage != null && responseMessage.getService() != null ) { }
            else { }

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (connection != null)
                connection.disconnect();
        }

        return responseMessage;

    }

    public ResponseMessage<ChangeEmailResponse> changeEmailResponse(ChangeEmailRequest changeEmailRequest) {

        ResponseMessage<ChangeEmailResponse> responseMessage = null;

        SSLConnection sslConnection = new SSLConnection(context, Constants.HTTPS_SERVER_IP + "/users/change-email");
        HttpsURLConnection connection = sslConnection.setUpHttpsURLConnection();

        try {

            RequestHeader header = new RequestHeader();
            header.setAuthToken(prefs.getString(Constants.LOGIN_TOKEN_ID, ""));

            header.setVersion("1.0-PA");

            RequestMessage<ChangeEmailRequest> message = new RequestMessage<ChangeEmailRequest>();
            message.setRequestHeader(header);
            ChangeEmailRequest request = changeEmailRequest;
            request.setRequestUUID(prefs.getString(Constants.UUID, ""));
            message.setService(request);

            Type requestType = new com.google.gson.reflect.TypeToken<RequestMessage<ChangeEmailRequest>>() {}.getType();
            String jsonRequest = new Gson().toJson(message, requestType);

            connection.setDoOutput(true);
            connection.setRequestMethod("POST");
            connection.setRequestProperty("Content-Type", "application/json");
            OutputStream outputStream = connection.getOutputStream();
            outputStream.write(jsonRequest.getBytes());
            outputStream.flush();

            String encoding = connection.getHeaderField("Content-Encoding");
            boolean gzipped = encoding != null && encoding.toLowerCase().contains("gzip");
            InputStreamReader reader;
            if (gzipped){
                InputStream gzipInputStream = new GZIPInputStream(connection.getInputStream());
                reader = new InputStreamReader(gzipInputStream);
            }else {
                reader = new InputStreamReader(connection.getInputStream());
            }

            Gson gson = new Gson();
            responseMessage = gson.fromJson(reader, new TypeToken<ResponseMessage<ChangeEmailResponse>>() {}.getType());

            if( responseMessage != null && responseMessage.getService() != null ) { }
            else { }

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (connection != null)
                connection.disconnect();
        }

        return responseMessage;

    }


    public Bitmap newImageDownloader(String url){
        Bitmap bitmap = null;
        SSLConnection sslConnection = new SSLConnection(context, url);
        HttpsURLConnection connection = sslConnection.setUpHttpsURLConnection();
        try {
            connection.setConnectTimeout(30000);
            connection.setReadTimeout(30000);
            connection.setRequestMethod("GET");
            InputStream inputStream;
            inputStream = connection.getInputStream();
            bitmap = BitmapFactory.decodeStream(inputStream);
        } catch (Exception e) {
            e.printStackTrace();
        }
        finally {
            if (connection != null)
                connection.disconnect();
        }
        return bitmap;
    }
}
