package xyz.homapay.hampay.mobile.android.webservice;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.google.gson.reflect.TypeToken;

import java.io.IOException;
import java.lang.reflect.Type;
import java.net.URL;
import java.util.UUID;

import xyz.homapay.hampay.common.common.request.RequestHeader;
import xyz.homapay.hampay.common.common.request.RequestMessage;
import xyz.homapay.hampay.common.common.response.ResponseMessage;
import xyz.homapay.hampay.common.core.model.request.BusinessListRequest;
import xyz.homapay.hampay.common.core.model.request.BusinessPaymentConfirmRequest;
import xyz.homapay.hampay.common.core.model.request.BusinessPaymentRequest;
import xyz.homapay.hampay.common.core.model.request.BusinessSearchRequest;
import xyz.homapay.hampay.common.core.model.request.CancelPurchasePaymentRequest;
import xyz.homapay.hampay.common.core.model.request.CancelUserPaymentRequest;
import xyz.homapay.hampay.common.core.model.request.CardProfileRequest;
import xyz.homapay.hampay.common.core.model.request.ChangeEmailRequest;
import xyz.homapay.hampay.common.core.model.request.ChangeMemorableWordRequest;
import xyz.homapay.hampay.common.core.model.request.ChangePassCodeRequest;
import xyz.homapay.hampay.common.core.model.request.ContactUsRequest;
import xyz.homapay.hampay.common.core.model.request.ContactsHampayEnabledRequest;
import xyz.homapay.hampay.common.core.model.request.GetTokenFromPSPRequest;
import xyz.homapay.hampay.common.core.model.request.GetUserIdTokenRequest;
import xyz.homapay.hampay.common.core.model.request.IBANChangeRequest;
import xyz.homapay.hampay.common.core.model.request.IBANConfirmationRequest;
import xyz.homapay.hampay.common.core.model.request.IllegalAppListRequest;
import xyz.homapay.hampay.common.core.model.request.IndividualPaymentConfirmRequest;
import xyz.homapay.hampay.common.core.model.request.IndividualPaymentRequest;
import xyz.homapay.hampay.common.core.model.request.LatestInvoiceContactsRequest;
import xyz.homapay.hampay.common.core.model.request.LatestPaymentRequest;
import xyz.homapay.hampay.common.core.model.request.LatestPurchaseRequest;
import xyz.homapay.hampay.common.core.model.request.MobileRegistrationIdEntryRequest;
import xyz.homapay.hampay.common.core.model.request.PSPResultRequest;
import xyz.homapay.hampay.common.core.model.request.PendingPaymentListRequest;
import xyz.homapay.hampay.common.core.model.request.PendingPurchaseListRequest;
import xyz.homapay.hampay.common.core.model.request.PurchaseInfoRequest;
import xyz.homapay.hampay.common.core.model.request.RegistrationCredentialsRequest;
import xyz.homapay.hampay.common.core.model.request.RegistrationEntryRequest;
import xyz.homapay.hampay.common.core.model.request.RegistrationSendSmsTokenRequest;
import xyz.homapay.hampay.common.core.model.request.RegistrationVerifyMobileRequest;
import xyz.homapay.hampay.common.core.model.request.TACAcceptRequest;
import xyz.homapay.hampay.common.core.model.request.TACRequest;
import xyz.homapay.hampay.common.core.model.request.TransactionListRequest;
import xyz.homapay.hampay.common.core.model.request.UnlinkUserRequest;
import xyz.homapay.hampay.common.core.model.request.UploadImageRequest;
import xyz.homapay.hampay.common.core.model.request.UserPaymentRequest;
import xyz.homapay.hampay.common.core.model.request.UserProfileRequest;
import xyz.homapay.hampay.common.core.model.response.BusinessListResponse;
import xyz.homapay.hampay.common.core.model.response.BusinessPaymentConfirmResponse;
import xyz.homapay.hampay.common.core.model.response.BusinessPaymentResponse;
import xyz.homapay.hampay.common.core.model.response.CancelPurchasePaymentResponse;
import xyz.homapay.hampay.common.core.model.response.CancelUserPaymentResponse;
import xyz.homapay.hampay.common.core.model.response.CardProfileResponse;
import xyz.homapay.hampay.common.core.model.response.ChangeEmailResponse;
import xyz.homapay.hampay.common.core.model.response.ChangeMemorableWordResponse;
import xyz.homapay.hampay.common.core.model.response.ChangePassCodeResponse;
import xyz.homapay.hampay.common.core.model.response.ContactUsResponse;
import xyz.homapay.hampay.common.core.model.response.ContactsHampayEnabledResponse;
import xyz.homapay.hampay.common.core.model.response.GetTokenFromPSPResponse;
import xyz.homapay.hampay.common.core.model.response.GetUserIdTokenResponse;
import xyz.homapay.hampay.common.core.model.response.IBANChangeResponse;
import xyz.homapay.hampay.common.core.model.response.IBANConfirmationResponse;
import xyz.homapay.hampay.common.core.model.response.IllegalAppListResponse;
import xyz.homapay.hampay.common.core.model.response.IndividualPaymentConfirmResponse;
import xyz.homapay.hampay.common.core.model.response.IndividualPaymentResponse;
import xyz.homapay.hampay.common.core.model.response.LatestInvoiceContactsResponse;
import xyz.homapay.hampay.common.core.model.response.LatestPaymentResponse;
import xyz.homapay.hampay.common.core.model.response.LatestPurchaseResponse;
import xyz.homapay.hampay.common.core.model.response.MobileRegistrationIdEntryResponse;
import xyz.homapay.hampay.common.core.model.response.PSPResultResponse;
import xyz.homapay.hampay.common.core.model.response.PendingPaymentListResponse;
import xyz.homapay.hampay.common.core.model.response.PendingPurchaseListResponse;
import xyz.homapay.hampay.common.core.model.response.PurchaseInfoResponse;
import xyz.homapay.hampay.common.core.model.response.RegistrationCredentialsResponse;
import xyz.homapay.hampay.common.core.model.response.RegistrationEntryResponse;
import xyz.homapay.hampay.common.core.model.response.RegistrationSendSmsTokenResponse;
import xyz.homapay.hampay.common.core.model.response.RegistrationVerifyMobileResponse;
import xyz.homapay.hampay.common.core.model.response.TACAcceptResponse;
import xyz.homapay.hampay.common.core.model.response.TACResponse;
import xyz.homapay.hampay.common.core.model.response.TransactionListResponse;
import xyz.homapay.hampay.common.core.model.response.UnlinkUserResponse;
import xyz.homapay.hampay.common.core.model.response.UploadImageResponse;
import xyz.homapay.hampay.common.core.model.response.UserPaymentResponse;
import xyz.homapay.hampay.common.core.model.response.UserProfileResponse;
import xyz.homapay.hampay.mobile.android.model.DoWorkInfo;
import xyz.homapay.hampay.mobile.android.model.LogoutData;
import xyz.homapay.hampay.mobile.android.model.LogoutResponse;
import xyz.homapay.hampay.mobile.android.util.Constants;
import xyz.homapay.hampay.mobile.android.util.PersianEnglishDigit;
import xyz.homapay.hampay.mobile.android.util.UserContacts;
import xyz.homapay.hampay.mobile.android.webservice.psp.PayThPartyApp;
import xyz.homapay.hampay.mobile.android.webservice.psp.Vectorstring2stringMapEntry;

/**
 * Created by amir on 6/6/15.
 */
public class WebServices  {

    public WebServices(){}
    private Context context;
    private SharedPreferences prefs;
    private DateGsonBuilder builder;
    private ConnectionType connectionType;
    private URL url;
    private String serviceURL = "";
    private String authToken = "";

    public WebServices(Context context, ConnectionType connectionType){
        this.context = context;
        prefs =  context.getSharedPreferences(Constants.APP_PREFERENCE_NAME, context.MODE_PRIVATE);
        builder = new DateGsonBuilder();
        this.connectionType = connectionType;
        if (connectionType == ConnectionType.HTTPS){
            serviceURL = Constants.HTTPS_SERVER_IP;
        }else {
            serviceURL = Constants.HTTP_SERVER_IP;
        }

        this.authToken = prefs.getString(Constants.LOGIN_TOKEN_ID, "");
    }

    public WebServices(Context context){
        this.context = context;
        prefs =  context.getSharedPreferences(Constants.APP_PREFERENCE_NAME, context.MODE_PRIVATE);
        builder = new DateGsonBuilder();
    }

    public LogoutResponse logoutRequest(LogoutData logoutData) throws Exception {

        if (connectionType == ConnectionType.HTTPS) {
            url = new URL(Constants.HTTPS_OPENAM_LOGOUT_URL);
        }else {
            url = new URL(Constants.HTTP_OPENAM_LOGOUT_URL);
        }
        ProxyService proxyService = new ProxyService(context, connectionType, ConnectionMethod.POST, url);
        Gson gson = new Gson();
        Type listType = new TypeToken<LogoutResponse>(){}.getType();
        JsonParser jsonParser = new JsonParser();
        JsonElement responseElement = jsonParser.parse(proxyService.hamPayLogout(logoutData).toString());
        proxyService.closeConnection();
        return (LogoutResponse) gson.fromJson(responseElement.toString(), listType);
    }

    public ResponseMessage<IllegalAppListResponse> getIllegalAppList() throws IOException{

        ResponseMessage<IllegalAppListResponse> responseMessage = null;
        url = new URL(serviceURL + "/illegal-apps");
        ProxyService proxyService = new ProxyService(context, connectionType, ConnectionMethod.POST, url);

        RequestHeader header = new CreateHeader(authToken, Constants.REQUEST_VERSION).createHeader();

        RequestMessage<IllegalAppListRequest> message = new RequestMessage<>();
        message.setRequestHeader(header);
        IllegalAppListRequest illegalAppListRequest = new IllegalAppListRequest();
        illegalAppListRequest.setRequestUUID(UUID.randomUUID().toString());
        message.setService(illegalAppListRequest);

        Type requestType = new com.google.gson.reflect.TypeToken<RequestMessage<IllegalAppListRequest>>() {}.getType();
        String jsonRequest = new Gson().toJson(message, requestType);
        proxyService.setJsonBody(jsonRequest);

        Gson gson = new Gson();

        responseMessage = gson.fromJson(proxyService.getInputStreamReader(), new TypeToken<ResponseMessage<IllegalAppListResponse>>() {}.getType());

        proxyService.closeConnection();

        return  responseMessage;
    }

    public ResponseMessage<RegistrationEntryResponse> registrationEntry(RegistrationEntryRequest registrationEntryRequest) throws IOException {

        ResponseMessage<RegistrationEntryResponse> responseMessage = null;
        url = new URL(serviceURL + "/users/reg-entry");
        ProxyService proxyService = new ProxyService(context, connectionType, ConnectionMethod.POST, url, true);

        RequestHeader header = new CreateHeader(authToken, Constants.REQUEST_VERSION).createHeader();

        RequestMessage<RegistrationEntryRequest> message = new RequestMessage<>();
        message.setRequestHeader(header);
        registrationEntryRequest.setRequestUUID(UUID.randomUUID().toString());
        message.setService(registrationEntryRequest);

        Type requestType = new com.google.gson.reflect.TypeToken<RequestMessage<RegistrationEntryRequest>>() {}.getType();
        String jsonRequest = new Gson().toJson(message, requestType);
        proxyService.setJsonBody(jsonRequest);

        Gson gson = new Gson();

        responseMessage = gson.fromJson(proxyService.getInputStreamReader(), new TypeToken<ResponseMessage<RegistrationEntryResponse>>() {}.getType());

        proxyService.closeConnection();

        return  responseMessage;
    }

    public ResponseMessage<ContactUsResponse> contactUsResponse(ContactUsRequest contactUsRequest) throws IOException{


        ResponseMessage<ContactUsResponse> responseMessage = null;
        url = new URL(serviceURL + "/contactus");
        ProxyService proxyService = new ProxyService(context, connectionType, ConnectionMethod.POST, url);

        RequestHeader header = new CreateHeader(authToken, Constants.REQUEST_VERSION).createHeader();

        RequestMessage<ContactUsRequest> message = new RequestMessage<>();
        message.setRequestHeader(header);
        contactUsRequest.setRequestUUID(UUID.randomUUID().toString());
        message.setService(contactUsRequest);

        Type requestType = new com.google.gson.reflect.TypeToken<RequestMessage<ContactUsRequest>>() {}.getType();
        String jsonRequest = new Gson().toJson(message, requestType);
        proxyService.setJsonBody(jsonRequest);

        Gson gson = new Gson();

        responseMessage = gson.fromJson(proxyService.getInputStreamReader(), new TypeToken<ResponseMessage<ContactUsResponse>>() {}.getType());

        proxyService.closeConnection();

        return  responseMessage;
    }

    public ResponseMessage<RegistrationSendSmsTokenResponse> registrationSendSmsToken(RegistrationSendSmsTokenRequest registrationSendSmsTokenRequest) throws IOException{

        ResponseMessage<RegistrationSendSmsTokenResponse> responseMessage = null;
        url = new URL(serviceURL + "/users/reg-sms-token");
        ProxyService proxyService = new ProxyService(context, connectionType, ConnectionMethod.POST, url);

        RequestHeader header = new CreateHeader(authToken, Constants.REQUEST_VERSION).createHeader();

        RequestMessage<RegistrationSendSmsTokenRequest> message = new RequestMessage<>();
        message.setRequestHeader(header);
        registrationSendSmsTokenRequest.setRequestUUID(UUID.randomUUID().toString());
        message.setService(registrationSendSmsTokenRequest);

        Type requestType = new com.google.gson.reflect.TypeToken<RequestMessage<RegistrationSendSmsTokenRequest>>() {}.getType();
        String jsonRequest = new Gson().toJson(message, requestType);
        proxyService.setJsonBody(jsonRequest);

        Gson gson = new Gson();

        responseMessage = gson.fromJson(proxyService.getInputStreamReader(), new TypeToken<ResponseMessage<RegistrationSendSmsTokenResponse>>() {}.getType());

        proxyService.closeConnection();

        return responseMessage;
    }


    public ResponseMessage<RegistrationVerifyMobileResponse> registrationVerifyMobileResponse(RegistrationVerifyMobileRequest registrationVerifyMobileRequest) throws IOException{

        ResponseMessage<RegistrationVerifyMobileResponse> responseMessage = null;
        url = new URL(serviceURL + "/users/reg-verify-mobile");
        ProxyService proxyService = new ProxyService(context, connectionType, ConnectionMethod.POST, url);

        RequestHeader header = new CreateHeader(authToken, Constants.REQUEST_VERSION).createHeader();

        RequestMessage<RegistrationVerifyMobileRequest> message = new RequestMessage<>();
        message.setRequestHeader(header);
        registrationVerifyMobileRequest.setRequestUUID(UUID.randomUUID().toString());
        message.setService(registrationVerifyMobileRequest);

        Type requestType = new com.google.gson.reflect.TypeToken<RequestMessage<RegistrationVerifyMobileRequest>>() {}.getType();
        String jsonRequest = new Gson().toJson(message, requestType);
        proxyService.setJsonBody(jsonRequest);

        Gson gson = new Gson();

        responseMessage = gson.fromJson(proxyService.getInputStreamReader(), new TypeToken<ResponseMessage<RegistrationVerifyMobileResponse>>() {}.getType());

        proxyService.closeConnection();

        return responseMessage;
    }


//    public ResponseMessage<RegisterCardResponse> registerCardResponse(RegisterCardRequest registerCardRequest) throws IOException{
//
//        ResponseMessage<RegisterCardResponse> responseMessage = null;
//        url = new URL(serviceURL + "/psp/registerCard");
//        ProxyService proxyService = new ProxyService(context, connectionType, ConnectionMethod.POST, url);
//
//        RequestHeader header = new CreateHeader(authToken, Constants.REQUEST_VERSION).createHeader();
//
//        RequestMessage<RegisterCardRequest> message = new RequestMessage<>();
//        message.setRequestHeader(header);
//        registerCardRequest.setRequestUUID(UUID.randomUUID().toString());
//        message.setService(registerCardRequest);
//
//        Type requestType = new com.google.gson.reflect.TypeToken<RequestMessage<RegisterCardRequest>>() {}.getType();
//        String jsonRequest = new Gson().toJson(message, requestType);
//        proxyService.setJsonBody(jsonRequest);
//
//        Gson gson = new Gson();
//
//        responseMessage = gson.fromJson(proxyService.getInputStreamReader(), new TypeToken<ResponseMessage<RegisterCardResponse>>() {}.getType());
//
//        proxyService.closeConnection();
//
//        return responseMessage;
//    }


    public ResponseMessage<RegistrationCredentialsResponse> registrationCredentialsResponse(RegistrationCredentialsRequest registrationCredentialsRequest) throws IOException{


        ResponseMessage<RegistrationCredentialsResponse> responseMessage = null;
        url = new URL(serviceURL + "/users/reg-credential-entry");
        ProxyService proxyService = new ProxyService(context, connectionType, ConnectionMethod.POST, url, true);

        RequestHeader header = new CreateHeader(authToken, Constants.REQUEST_VERSION).createHeader();

        RequestMessage<RegistrationCredentialsRequest> message = new RequestMessage<>();
        message.setRequestHeader(header);
        UserContacts userContacts = new UserContacts(context);
        registrationCredentialsRequest.setContacts(userContacts.read());
        registrationCredentialsRequest.setRequestUUID(UUID.randomUUID().toString());
        message.setService(registrationCredentialsRequest);

        Type requestType = new com.google.gson.reflect.TypeToken<RequestMessage<RegistrationCredentialsRequest>>() {}.getType();
        String jsonRequest = new Gson().toJson(message, requestType);
        proxyService.setJsonBody(jsonRequest);

        Gson gson = new Gson();

        responseMessage = gson.fromJson(proxyService.getInputStreamReader(), new TypeToken<ResponseMessage<RegistrationCredentialsResponse>>() {}.getType());

        proxyService.closeConnection();

        return responseMessage;
    }


    public ResponseMessage<MobileRegistrationIdEntryResponse> registrationDeviceRegId(MobileRegistrationIdEntryRequest mobileRegistrationIdEntryRequest) throws IOException{

        ResponseMessage<MobileRegistrationIdEntryResponse> responseMessage = null;
        url = new URL(serviceURL + "/users/mobile-reg-id-entry");
        ProxyService proxyService = new ProxyService(context, connectionType, ConnectionMethod.POST, url);

        RequestHeader header = new CreateHeader(authToken, Constants.REQUEST_VERSION).createHeader();

        RequestMessage<MobileRegistrationIdEntryRequest> message = new RequestMessage<>();
        message.setRequestHeader(header);
        mobileRegistrationIdEntryRequest.setRequestUUID(UUID.randomUUID().toString());
        message.setService(mobileRegistrationIdEntryRequest);

        Type requestType = new com.google.gson.reflect.TypeToken<RequestMessage<MobileRegistrationIdEntryRequest>>() {}.getType();
        String jsonRequest = new Gson().toJson(message, requestType);
        proxyService.setJsonBody(jsonRequest);

        Gson gson = new Gson();

        responseMessage = gson.fromJson(proxyService.getInputStreamReader(), new TypeToken<ResponseMessage<MobileRegistrationIdEntryResponse>>() {}.getType());

        proxyService.closeConnection();

        return responseMessage;
    }


    public ResponseMessage<TACResponse> tacResponse(TACRequest tacRequest) throws IOException{

        ResponseMessage<TACResponse> responseMessage = null;
        url = new URL(serviceURL + "/users/tac");
        ProxyService proxyService = new ProxyService(context, connectionType, ConnectionMethod.POST, url);

        RequestHeader header = new CreateHeader(authToken, Constants.REQUEST_VERSION).createHeader();

        RequestMessage<TACRequest> message = new RequestMessage<>();
        message.setRequestHeader(header);
        tacRequest.setRequestUUID(UUID.randomUUID().toString());
        message.setService(tacRequest);

        Type requestType = new com.google.gson.reflect.TypeToken<RequestMessage<TACRequest>>() {}.getType();
        String jsonRequest = new Gson().toJson(message, requestType);
        proxyService.setJsonBody(jsonRequest);

        Gson gson = builder.getDatebuilder().create();

        responseMessage = gson.fromJson(proxyService.getInputStreamReader(), new TypeToken<ResponseMessage<TACResponse>>() {}.getType());

        proxyService.closeConnection();

        return responseMessage;
    }

    public ResponseMessage<UploadImageResponse> uploadImage(UploadImageRequest uploadImageRequest) throws IOException{

        ResponseMessage<UploadImageResponse> responseMessage = null;
        url = new URL(serviceURL + "/users/upload-image");
        ProxyService proxyService = new ProxyService(context, connectionType, ConnectionMethod.POST, url);

        RequestHeader header = new CreateHeader(authToken, Constants.REQUEST_VERSION).createHeader();

        RequestMessage<UploadImageRequest> message = new RequestMessage<>();
        message.setRequestHeader(header);
        uploadImageRequest.setRequestUUID(prefs.getString(Constants.UUID, ""));
        message.setService(uploadImageRequest);

        Type requestType = new com.google.gson.reflect.TypeToken<RequestMessage<UploadImageRequest>>() {}.getType();
        String jsonRequest = new Gson().toJson(message, requestType);
        proxyService.setJsonBody(jsonRequest);

        Gson gson = builder.getDatebuilder().create();

        responseMessage = gson.fromJson(proxyService.getInputStreamReader(), new TypeToken<ResponseMessage<UploadImageResponse>>() {}.getType());

        proxyService.closeConnection();

        return responseMessage;
    }


    public ResponseMessage<GetUserIdTokenResponse> getUserIdTokenResponse(GetUserIdTokenRequest getUserIdTokenRequest) throws IOException{

        ResponseMessage<GetUserIdTokenResponse> responseMessage = null;
        url = new URL(serviceURL + "/users/get-user-id-token");
        ProxyService proxyService = new ProxyService(context, connectionType, ConnectionMethod.POST, url);

        RequestHeader header = new CreateHeader(authToken, Constants.REQUEST_VERSION).createHeader();

        RequestMessage<GetUserIdTokenRequest> message = new RequestMessage<>();
        message.setRequestHeader(header);
        getUserIdTokenRequest.setRequestUUID(prefs.getString(Constants.UUID, ""));
        message.setService(getUserIdTokenRequest);

        Type requestType = new com.google.gson.reflect.TypeToken<RequestMessage<GetUserIdTokenRequest>>() {}.getType();
        String jsonRequest = new Gson().toJson(message, requestType);
        proxyService.setJsonBody(jsonRequest);

        Gson gson = builder.getDatebuilder().create();

        responseMessage = gson.fromJson(proxyService.getInputStreamReader(), new TypeToken<ResponseMessage<GetUserIdTokenResponse>>() {}.getType());

        proxyService.closeConnection();

        return responseMessage;
    }


    public ResponseMessage<TACAcceptResponse> tacAcceptResponse(TACAcceptRequest tacAcceptRequest) throws IOException{

        ResponseMessage<TACAcceptResponse> responseMessage = null;
        url = new URL(serviceURL + "/users/tacaccept");
        ProxyService proxyService = new ProxyService(context, connectionType, ConnectionMethod.POST, url);

        RequestHeader header = new CreateHeader(authToken, Constants.REQUEST_VERSION).createHeader();

        RequestMessage<TACAcceptRequest> message = new RequestMessage<>();
        message.setRequestHeader(header);
        tacAcceptRequest.setRequestUUID(prefs.getString(Constants.UUID, ""));
        message.setService(tacAcceptRequest);

        Type requestType = new com.google.gson.reflect.TypeToken<RequestMessage<TACAcceptRequest>>() {}.getType();
        String jsonRequest = new Gson().toJson(message, requestType);
        proxyService.setJsonBody(jsonRequest);

        Gson gson = builder.getDatebuilder().create();

        responseMessage = gson.fromJson(proxyService.getInputStreamReader(), new TypeToken<ResponseMessage<TACAcceptResponse>>() {}.getType());

        proxyService.closeConnection();

        return responseMessage;
    }


    public ResponseMessage<UserProfileResponse> getUserProfile(UserProfileRequest userProfileRequest) throws IOException{

        ResponseMessage<UserProfileResponse> responseMessage = null;
        url = new URL(serviceURL + "/users/profile");
        ProxyService proxyService = new ProxyService(context, connectionType, ConnectionMethod.POST, url);

        RequestHeader header = new CreateHeader(authToken, Constants.REQUEST_VERSION).createHeader();

        RequestMessage<UserProfileRequest> message = new RequestMessage<>();
        message.setRequestHeader(header);
        userProfileRequest.setRequestUUID(prefs.getString(Constants.UUID, ""));
        message.setService(userProfileRequest);

        Type requestType = new com.google.gson.reflect.TypeToken<RequestMessage<UserProfileRequest>>() {}.getType();
        String jsonRequest = new Gson().toJson(message, requestType);
        proxyService.setJsonBody(jsonRequest);

        Gson gson = builder.getDatebuilder().create();

        responseMessage = gson.fromJson(proxyService.getInputStreamReader(), new TypeToken<ResponseMessage<UserProfileResponse>>() {}.getType());

        proxyService.closeConnection();

        return responseMessage;
    }


    public ResponseMessage<TransactionListResponse> userTransaction(TransactionListRequest transactionListRequest) throws IOException{

        ResponseMessage<TransactionListResponse> responseMessage = null;
        url = new URL(serviceURL + "/transactions");
        ProxyService proxyService = new ProxyService(context, connectionType, ConnectionMethod.POST, url);

        RequestHeader header = new CreateHeader(authToken, Constants.REQUEST_VERSION).createHeader();

        RequestMessage<TransactionListRequest> message = new RequestMessage<>();
        message.setRequestHeader(header);
        transactionListRequest.setUserId(new PersianEnglishDigit(prefs.getString(Constants.REGISTERED_NATIONAL_CODE, "")).P2E());
        transactionListRequest.setRequestUUID(prefs.getString(Constants.UUID, ""));
        message.setService(transactionListRequest);

        Type requestType = new com.google.gson.reflect.TypeToken<RequestMessage<TransactionListRequest>>() {}.getType();
        String jsonRequest = new Gson().toJson(message, requestType);
        proxyService.setJsonBody(jsonRequest);

        Gson gson = builder.getDatebuilder().create();

        responseMessage = gson.fromJson(proxyService.getInputStreamReader(), new TypeToken<ResponseMessage<TransactionListResponse>>() {}.getType());

        proxyService.closeConnection();

        return responseMessage;
    }


    public ResponseMessage<ContactsHampayEnabledResponse> getEnabledHamPayContacts(ContactsHampayEnabledRequest contactsHampayEnabledRequest) throws IOException{

        ResponseMessage<ContactsHampayEnabledResponse> responseMessage = null;
        url = new URL(serviceURL + "/customer/contacts/hp-enabled");
        ProxyService proxyService = new ProxyService(context, connectionType, ConnectionMethod.POST, url, true);

        RequestHeader header = new CreateHeader(authToken, Constants.REQUEST_VERSION).createHeader();

        RequestMessage<ContactsHampayEnabledRequest> message = new RequestMessage<>();
        message.setRequestHeader(header);
        UserContacts userContacts = new UserContacts(context);
        contactsHampayEnabledRequest.setContacts(userContacts.read());
        contactsHampayEnabledRequest.setRequestUUID(prefs.getString(Constants.UUID, ""));
        message.setService(contactsHampayEnabledRequest);

        Type requestType = new com.google.gson.reflect.TypeToken<RequestMessage<ContactsHampayEnabledRequest>>() {}.getType();
        String jsonRequest = new Gson().toJson(message, requestType);
        proxyService.setJsonBody(jsonRequest);

        Gson gson = builder.getDatebuilder().create();

        responseMessage = gson.fromJson(proxyService.getInputStreamReader(), new TypeToken<ResponseMessage<ContactsHampayEnabledResponse>>() {}.getType());

        proxyService.closeConnection();

        return responseMessage;
    }


    public ResponseMessage<IndividualPaymentConfirmResponse> individualPaymentConfirm(IndividualPaymentConfirmRequest individualPaymentConfirmRequest) throws IOException{

        ResponseMessage<IndividualPaymentConfirmResponse> responseMessage = null;
        url = new URL(serviceURL + "/customers/individual-payment-confirm");
        ProxyService proxyService = new ProxyService(context, connectionType, ConnectionMethod.POST, url);

        RequestHeader header = new CreateHeader(authToken, Constants.REQUEST_VERSION).createHeader();

        RequestMessage<IndividualPaymentConfirmRequest> message = new RequestMessage<>();
        message.setRequestHeader(header);
        individualPaymentConfirmRequest.setRequestUUID(prefs.getString(Constants.UUID, ""));
        message.setService(individualPaymentConfirmRequest);

        Type requestType = new com.google.gson.reflect.TypeToken<RequestMessage<IndividualPaymentConfirmRequest>>() {}.getType();
        String jsonRequest = new Gson().toJson(message, requestType);
        proxyService.setJsonBody(jsonRequest);

        Gson gson = builder.getDatebuilder().create();

        responseMessage = gson.fromJson(proxyService.getInputStreamReader(), new TypeToken<ResponseMessage<IndividualPaymentConfirmResponse>>() {}.getType());

        proxyService.closeConnection();


        return responseMessage;
    }

    public ResponseMessage<IndividualPaymentResponse> individualPayment(IndividualPaymentRequest individualPaymentRequest) throws IOException{

        ResponseMessage<IndividualPaymentResponse> responseMessage = null;
        url = new URL(serviceURL + "/customers/individual-payment");
        ProxyService proxyService = new ProxyService(context, connectionType, ConnectionMethod.POST, url);

        RequestHeader header = new CreateHeader(authToken, Constants.REQUEST_VERSION).createHeader();

        RequestMessage<IndividualPaymentRequest> message = new RequestMessage<>();
        message.setRequestHeader(header);
        individualPaymentRequest.setRequestUUID(prefs.getString(Constants.UUID, ""));
        message.setService(individualPaymentRequest);

        Type requestType = new com.google.gson.reflect.TypeToken<RequestMessage<IndividualPaymentRequest>>() {}.getType();
        String jsonRequest = new Gson().toJson(message, requestType);
        proxyService.setJsonBody(jsonRequest);

        Gson gson = builder.getDatebuilder().create();

        responseMessage = gson.fromJson(proxyService.getInputStreamReader(), new TypeToken<ResponseMessage<IndividualPaymentResponse>>() {}.getType());
        proxyService.closeConnection();

        return responseMessage;
    }


    public ResponseMessage<BusinessListResponse> getHamPayBusinesses(BusinessListRequest businessListRequest) throws IOException{

        ResponseMessage<BusinessListResponse> responseMessage = null;
        url = new URL(serviceURL + "/businesses");
        ProxyService proxyService = new ProxyService(context, connectionType, ConnectionMethod.POST, url);

        RequestHeader header = new CreateHeader(authToken, Constants.REQUEST_VERSION).createHeader();

        RequestMessage<BusinessListRequest> message = new RequestMessage<>();
        message.setRequestHeader(header);
        businessListRequest.setRequestUUID(prefs.getString(Constants.UUID, ""));
        message.setService(businessListRequest);

        Type requestType = new com.google.gson.reflect.TypeToken<RequestMessage<BusinessListRequest>>() {}.getType();
        String jsonRequest = new Gson().toJson(message, requestType);
        proxyService.setJsonBody(jsonRequest);

        Gson gson = builder.getDatebuilder().create();

        responseMessage = gson.fromJson(proxyService.getInputStreamReader(), new TypeToken<ResponseMessage<BusinessListResponse>>() {}.getType());
        proxyService.closeConnection();

        return responseMessage;
    }


    public ResponseMessage<BusinessPaymentConfirmResponse> businessPaymentConfirm(BusinessPaymentConfirmRequest businessPaymentConfirmRequest) throws IOException{

        ResponseMessage<BusinessPaymentConfirmResponse> responseMessage = null;
        url = new URL(serviceURL + "/payment/info");
        ProxyService proxyService = new ProxyService(context, connectionType, ConnectionMethod.POST, url);

        RequestHeader header = new CreateHeader(authToken, Constants.REQUEST_VERSION).createHeader();

        RequestMessage<BusinessPaymentConfirmRequest> message = new RequestMessage<>();
        message.setRequestHeader(header);
        businessPaymentConfirmRequest.setRequestUUID(prefs.getString(Constants.UUID, ""));
        message.setService(businessPaymentConfirmRequest);

        Type requestType = new com.google.gson.reflect.TypeToken<RequestMessage<BusinessPaymentConfirmRequest>>() {}.getType();
        String jsonRequest = new Gson().toJson(message, requestType);
        proxyService.setJsonBody(jsonRequest);

        Gson gson = builder.getDatebuilder().create();

        responseMessage = gson.fromJson(proxyService.getInputStreamReader(), new TypeToken<ResponseMessage<BusinessPaymentConfirmResponse>>() {}.getType());
        proxyService.closeConnection();

        return responseMessage;
    }


    public ResponseMessage<BusinessPaymentResponse> businessPayment(BusinessPaymentRequest businessPaymentRequest) throws IOException{

        ResponseMessage<BusinessPaymentResponse> responseMessage = null;
        url = new URL(serviceURL + "/customers/business-payment");
        ProxyService proxyService = new ProxyService(context, connectionType, ConnectionMethod.POST, url);

        RequestHeader header = new CreateHeader(authToken, Constants.REQUEST_VERSION).createHeader();

        RequestMessage<BusinessPaymentRequest> message = new RequestMessage<>();
        message.setRequestHeader(header);
        businessPaymentRequest.setRequestUUID(prefs.getString(Constants.UUID, ""));
        message.setService(businessPaymentRequest);

        Type requestType = new com.google.gson.reflect.TypeToken<RequestMessage<BusinessPaymentRequest>>() {}.getType();
        String jsonRequest = new Gson().toJson(message, requestType);
        proxyService.setJsonBody(jsonRequest);

        Gson gson = builder.getDatebuilder().create();

        responseMessage = gson.fromJson(proxyService.getInputStreamReader(), new TypeToken<ResponseMessage<BusinessPaymentResponse>>() {}.getType());
        proxyService.closeConnection();

        return responseMessage;
    }



    public ResponseMessage<BusinessListResponse> searchBusinessList(BusinessSearchRequest businessSearchRequest) throws IOException{

        ResponseMessage<BusinessListResponse> responseMessage = null;
        url = new URL(serviceURL + "/search");
        ProxyService proxyService = new ProxyService(context, connectionType, ConnectionMethod.POST, url);

        RequestHeader header = new CreateHeader(authToken, Constants.REQUEST_VERSION).createHeader();

        RequestMessage<BusinessSearchRequest> message = new RequestMessage<>();
        message.setRequestHeader(header);
        businessSearchRequest.setRequestUUID(prefs.getString(Constants.UUID, ""));
        message.setService(businessSearchRequest);

        Type requestType = new com.google.gson.reflect.TypeToken<RequestMessage<BusinessListRequest>>() {}.getType();
        String jsonRequest = new Gson().toJson(message, requestType);
        proxyService.setJsonBody(jsonRequest);

        Gson gson = builder.getDatebuilder().create();

        responseMessage = gson.fromJson(proxyService.getInputStreamReader(), new TypeToken<ResponseMessage<BusinessListResponse>>() {}.getType());
        proxyService.closeConnection();

        return responseMessage;
    }


    public ResponseMessage<ChangePassCodeResponse> changePassCodeResponse(ChangePassCodeRequest changePassCodeRequest) throws IOException{

        ResponseMessage<ChangePassCodeResponse> responseMessage = null;
        url = new URL(serviceURL + "/users/passcode");
        ProxyService proxyService = new ProxyService(context, connectionType, ConnectionMethod.PUT, url);

        RequestHeader header = new CreateHeader(authToken, Constants.REQUEST_VERSION).createHeader();

        RequestMessage<ChangePassCodeRequest> message = new RequestMessage<>();
        message.setRequestHeader(header);
        changePassCodeRequest.setRequestUUID(prefs.getString(Constants.UUID, ""));
        message.setService(changePassCodeRequest);

        Type requestType = new com.google.gson.reflect.TypeToken<RequestMessage<ChangePassCodeRequest>>() {}.getType();
        String jsonRequest = new Gson().toJson(message, requestType);
        proxyService.setJsonBody(jsonRequest);

        Gson gson = builder.getDatebuilder().create();

        responseMessage = gson.fromJson(proxyService.getInputStreamReader(), new TypeToken<ResponseMessage<ChangePassCodeResponse>>() {}.getType());
        proxyService.closeConnection();

        return responseMessage;
    }

    public ResponseMessage<ChangeMemorableWordResponse> changeMemorableWordResponse(ChangeMemorableWordRequest changeMemorableWordRequest) throws IOException{

        ResponseMessage<ChangeMemorableWordResponse> responseMessage = null;
        url = new URL(serviceURL + "/users/memorable-word");
        ProxyService proxyService = new ProxyService(context, connectionType, ConnectionMethod.PUT, url);

        RequestHeader header = new CreateHeader(authToken, Constants.REQUEST_VERSION).createHeader();

        RequestMessage<ChangeMemorableWordRequest> message = new RequestMessage<>();
        message.setRequestHeader(header);
        changeMemorableWordRequest.setRequestUUID(prefs.getString(Constants.UUID, ""));
        message.setService(changeMemorableWordRequest);

        Type requestType = new com.google.gson.reflect.TypeToken<RequestMessage<ChangeMemorableWordRequest>>() {}.getType();
        String jsonRequest = new Gson().toJson(message, requestType);
        proxyService.setJsonBody(jsonRequest);

        Gson gson = builder.getDatebuilder().create();

        responseMessage = gson.fromJson(proxyService.getInputStreamReader(), new TypeToken<ResponseMessage<ChangeMemorableWordResponse>>() {}.getType());
        proxyService.closeConnection();

        return responseMessage;
    }



    public ResponseMessage<UnlinkUserResponse> unlinkUserResponse(UnlinkUserRequest unlinkUserRequest) throws IOException{

        ResponseMessage<UnlinkUserResponse> responseMessage = null;
        url = new URL(serviceURL + "/users/unlink");
        ProxyService proxyService = new ProxyService(context, connectionType, ConnectionMethod.POST, url);

        RequestHeader header = new CreateHeader(authToken, Constants.REQUEST_VERSION).createHeader();

        RequestMessage<UnlinkUserRequest> message = new RequestMessage<>();
        message.setRequestHeader(header);
        unlinkUserRequest.setRequestUUID(prefs.getString(Constants.UUID, ""));
        message.setService(unlinkUserRequest);

        Type requestType = new com.google.gson.reflect.TypeToken<RequestMessage<UnlinkUserRequest>>() {}.getType();
        String jsonRequest = new Gson().toJson(message, requestType);
        proxyService.setJsonBody(jsonRequest);

        Gson gson = builder.getDatebuilder().create();

        responseMessage = gson.fromJson(proxyService.getInputStreamReader(), new TypeToken<ResponseMessage<UnlinkUserResponse>>() {}.getType());
        proxyService.closeConnection();

        return responseMessage;
    }


    public ResponseMessage<ChangeEmailResponse> changeEmailResponse(ChangeEmailRequest changeEmailRequest) throws IOException{

        ResponseMessage<ChangeEmailResponse> responseMessage = null;
        url = new URL(serviceURL + "/users/change-email");
        ProxyService proxyService = new ProxyService(context, connectionType, ConnectionMethod.POST, url);

        RequestHeader header = new CreateHeader(authToken, Constants.REQUEST_VERSION).createHeader();

        RequestMessage<ChangeEmailRequest> message = new RequestMessage<>();
        message.setRequestHeader(header);
        changeEmailRequest.setRequestUUID(prefs.getString(Constants.UUID, ""));
        message.setService(changeEmailRequest);

        Type requestType = new com.google.gson.reflect.TypeToken<RequestMessage<ChangeEmailRequest>>() {}.getType();
        String jsonRequest = new Gson().toJson(message, requestType);
        proxyService.setJsonBody(jsonRequest);

        Gson gson = new Gson();

        responseMessage = gson.fromJson(proxyService.getInputStreamReader(), new TypeToken<ResponseMessage<ChangeEmailResponse>>() {}.getType());
        proxyService.closeConnection();

        return responseMessage;
    }


    public Bitmap imageDownloader(String imageId) throws IOException{

        url = new URL(serviceURL + imageId);
        ProxyService proxyService = new ProxyService(context, connectionType, ConnectionMethod.GET, url);
        Bitmap bitmap = proxyService.imageInputStream();
        proxyService.closeConnection();
        return bitmap;
    }


    public Vectorstring2stringMapEntry newPurchaseResponse(DoWorkInfo doWorkInfo){

        PayThPartyApp payThPartyApp = new PayThPartyApp(context);
        Vectorstring2stringMapEntry responseMessage = payThPartyApp.DoWork(
                doWorkInfo.getUserName(),
                doWorkInfo.getPassword(),
                doWorkInfo.getCellNumber(),
                doWorkInfo.getLangAByte(),
                doWorkInfo.isLangABoolean(),
                doWorkInfo.getVectorstring2stringMapEntry());

        return responseMessage;
    }


    public ResponseMessage<LatestPurchaseResponse> latestUserPurchase(LatestPurchaseRequest latestPurchaseRequest) throws IOException{

        ResponseMessage<LatestPurchaseResponse> responseMessage = null;
        url = new URL(serviceURL + "/purchase/latest");
        ProxyService proxyService = new ProxyService(context, connectionType, ConnectionMethod.POST, url);

        RequestHeader header = new CreateHeader(authToken, Constants.REQUEST_VERSION).createHeader();

        RequestMessage<LatestPurchaseRequest> message = new RequestMessage<>();
        message.setRequestHeader(header);
        latestPurchaseRequest.setRequestUUID(prefs.getString(Constants.UUID, ""));
        message.setService(latestPurchaseRequest);

        Type requestType = new com.google.gson.reflect.TypeToken<RequestMessage<LatestPurchaseRequest>>() {}.getType();
        String jsonRequest = new Gson().toJson(message, requestType);
        proxyService.setJsonBody(jsonRequest);

        Gson gson = builder.getDatebuilder().create();

        responseMessage = gson.fromJson(proxyService.getInputStreamReader(), new TypeToken<ResponseMessage<LatestPurchaseResponse>>() {}.getType());
        proxyService.closeConnection();

        return responseMessage;
    }

    public ResponseMessage<LatestPaymentResponse> latestUserPayment(LatestPaymentRequest latestPaymentRequest) throws IOException{

        ResponseMessage<LatestPaymentResponse> responseMessage = null;
        url = new URL(serviceURL + "/payment/latest");
        ProxyService proxyService = new ProxyService(context, connectionType, ConnectionMethod.POST, url);

        RequestHeader header = new CreateHeader(authToken, Constants.REQUEST_VERSION).createHeader();

        RequestMessage<LatestPaymentRequest> message = new RequestMessage<>();
        message.setRequestHeader(header);
        latestPaymentRequest.setRequestUUID(prefs.getString(Constants.UUID, ""));
        message.setService(latestPaymentRequest);

        Type requestType = new com.google.gson.reflect.TypeToken<RequestMessage<LatestPaymentRequest>>() {}.getType();
        String jsonRequest = new Gson().toJson(message, requestType);
        proxyService.setJsonBody(jsonRequest);

        Gson gson = builder.getDatebuilder().create();

        responseMessage = gson.fromJson(proxyService.getInputStreamReader(), new TypeToken<ResponseMessage<LatestPaymentResponse>>() {}.getType());
        proxyService.closeConnection();

        return responseMessage;
    }

    public ResponseMessage<PSPResultResponse> pspResult(PSPResultRequest pspResultRequest) throws IOException{

        ResponseMessage<PSPResultResponse> responseMessage = null;
        url = new URL(serviceURL + "/purchase/psp-result");
        ProxyService proxyService = new ProxyService(context, connectionType, ConnectionMethod.POST, url);

        RequestHeader header = new CreateHeader(authToken, Constants.REQUEST_VERSION).createHeader();

        RequestMessage<PSPResultRequest> message = new RequestMessage<>();
        message.setRequestHeader(header);
        pspResultRequest.setRequestUUID(prefs.getString(Constants.UUID, ""));
        message.setService(pspResultRequest);

        Type requestType = new com.google.gson.reflect.TypeToken<RequestMessage<PSPResultRequest>>() {}.getType();
        String jsonRequest = new Gson().toJson(message, requestType);
        proxyService.setJsonBody(jsonRequest);

        Gson gson = builder.getDatebuilder().create();

        responseMessage = gson.fromJson(proxyService.getInputStreamReader(), new TypeToken<ResponseMessage<PSPResultResponse>>() {}.getType());
        proxyService.closeConnection();

        return responseMessage;
    }

    public ResponseMessage<PendingPurchaseListResponse> pendingPurchase(PendingPurchaseListRequest pendingPurchaseListRequest) throws IOException{

        ResponseMessage<PendingPurchaseListResponse> responseMessage = null;
        url = new URL(serviceURL + "/purchase/pendingList");
        ProxyService proxyService = new ProxyService(context, connectionType, ConnectionMethod.POST, url);

        RequestHeader header = new CreateHeader(authToken, Constants.REQUEST_VERSION).createHeader();

        RequestMessage<PendingPurchaseListRequest> message = new RequestMessage<>();
        message.setRequestHeader(header);
        pendingPurchaseListRequest.setRequestUUID(prefs.getString(Constants.UUID, ""));
        message.setService(pendingPurchaseListRequest);

        Type requestType = new com.google.gson.reflect.TypeToken<RequestMessage<PendingPurchaseListRequest>>() {}.getType();
        String jsonRequest = new Gson().toJson(message, requestType);
        proxyService.setJsonBody(jsonRequest);

        Gson gson = builder.getDatebuilder().create();

        responseMessage = gson.fromJson(proxyService.getInputStreamReader(), new TypeToken<ResponseMessage<PendingPurchaseListResponse>>() {}.getType());
        proxyService.closeConnection();

        return responseMessage;
    }


    public ResponseMessage<PendingPaymentListResponse> pendingPayment(PendingPaymentListRequest pendingPaymentListRequest) throws IOException{

        ResponseMessage<PendingPaymentListResponse> responseMessage = null;
        url = new URL(serviceURL +  "/payment/pendingList");
        ProxyService proxyService = new ProxyService(context, connectionType, ConnectionMethod.POST, url);

        RequestHeader header = new CreateHeader(authToken, Constants.REQUEST_VERSION).createHeader();

        RequestMessage<PendingPaymentListRequest> message = new RequestMessage<>();
        message.setRequestHeader(header);
        pendingPaymentListRequest.setRequestUUID(prefs.getString(Constants.UUID, ""));
        message.setService(pendingPaymentListRequest);

        Type requestType = new com.google.gson.reflect.TypeToken<RequestMessage<PendingPaymentListRequest>>() {}.getType();
        String jsonRequest = new Gson().toJson(message, requestType);
        proxyService.setJsonBody(jsonRequest);

        Gson gson = builder.getDatebuilder().create();

        responseMessage = gson.fromJson(proxyService.getInputStreamReader(), new TypeToken<ResponseMessage<PendingPaymentListResponse>>() {}.getType());
        proxyService.closeConnection();

        return responseMessage;
    }

    public ResponseMessage<CancelPurchasePaymentResponse> cancelPurchasePaymentResponse(CancelPurchasePaymentRequest cancelPurchasePaymentRequest) throws IOException{

        ResponseMessage<CancelPurchasePaymentResponse> responseMessage = null;
        url = new URL(serviceURL +  "/purchase/cancel");
        ProxyService proxyService = new ProxyService(context, connectionType, ConnectionMethod.POST, url);

        RequestHeader header = new CreateHeader(authToken, Constants.REQUEST_VERSION).createHeader();

        RequestMessage<CancelPurchasePaymentRequest> message = new RequestMessage<>();
        message.setRequestHeader(header);
        cancelPurchasePaymentRequest.setRequestUUID(prefs.getString(Constants.UUID, ""));
        message.setService(cancelPurchasePaymentRequest);

        Type requestType = new com.google.gson.reflect.TypeToken<RequestMessage<CancelPurchasePaymentRequest>>() {}.getType();
        String jsonRequest = new Gson().toJson(message, requestType);
        proxyService.setJsonBody(jsonRequest);

        Gson gson = builder.getDatebuilder().create();

        responseMessage = gson.fromJson(proxyService.getInputStreamReader(), new TypeToken<ResponseMessage<CancelPurchasePaymentResponse>>() {}.getType());
        proxyService.closeConnection();

        return responseMessage;
    }

    public ResponseMessage<CancelUserPaymentResponse> cancelUserPaymentResponse(CancelUserPaymentRequest cancelUserPaymentRequest) throws IOException{

        ResponseMessage<CancelUserPaymentResponse> responseMessage = null;
        url = new URL(serviceURL +  "/payment/cancel");
        ProxyService proxyService = new ProxyService(context, connectionType, ConnectionMethod.POST, url);

        RequestHeader header = new CreateHeader(authToken, Constants.REQUEST_VERSION).createHeader();

        RequestMessage<CancelUserPaymentRequest> message = new RequestMessage<>();
        message.setRequestHeader(header);
        cancelUserPaymentRequest.setRequestUUID(prefs.getString(Constants.UUID, ""));
        message.setService(cancelUserPaymentRequest);

        Type requestType = new com.google.gson.reflect.TypeToken<RequestMessage<CancelUserPaymentRequest>>() {}.getType();
        String jsonRequest = new Gson().toJson(message, requestType);
        proxyService.setJsonBody(jsonRequest);

        Gson gson = builder.getDatebuilder().create();

        responseMessage = gson.fromJson(proxyService.getInputStreamReader(), new TypeToken<ResponseMessage<CancelUserPaymentResponse>>() {}.getType());
        proxyService.closeConnection();

        return responseMessage;
    }

    public ResponseMessage<UserPaymentResponse> userPaymentResponse(UserPaymentRequest userPaymentRequest) throws IOException{

        ResponseMessage<UserPaymentResponse> responseMessage = null;
        url = new URL(serviceURL +  "/users/credit-request");
        ProxyService proxyService = new ProxyService(context, connectionType, ConnectionMethod.POST, url);

        RequestHeader header = new CreateHeader(authToken, Constants.REQUEST_VERSION).createHeader();

        RequestMessage<UserPaymentRequest> message = new RequestMessage<>();
        message.setRequestHeader(header);
        userPaymentRequest.setRequestUUID(prefs.getString(Constants.UUID, ""));
        message.setService(userPaymentRequest);

        Type requestType = new com.google.gson.reflect.TypeToken<RequestMessage<UserPaymentRequest>>() {}.getType();
        String jsonRequest = new Gson().toJson(message, requestType);
        proxyService.setJsonBody(jsonRequest);

        Gson gson = builder.getDatebuilder().create();

        responseMessage = gson.fromJson(proxyService.getInputStreamReader(), new TypeToken<ResponseMessage<UserPaymentResponse>>() {}.getType());
        proxyService.closeConnection();

        return responseMessage;
    }


    public ResponseMessage<IBANConfirmationResponse> ibanConfirmation(IBANConfirmationRequest ibanConfirmationRequest) throws IOException{

        ResponseMessage<IBANConfirmationResponse> responseMessage = null;
        url = new URL(serviceURL +  "/iban/confirmation");
        ProxyService proxyService = new ProxyService(context, connectionType, ConnectionMethod.POST, url);

        RequestHeader header = new CreateHeader(authToken, Constants.REQUEST_VERSION).createHeader();

        RequestMessage<IBANConfirmationRequest> message = new RequestMessage<>();
        message.setRequestHeader(header);
        ibanConfirmationRequest.setRequestUUID(prefs.getString(Constants.UUID, ""));
        message.setService(ibanConfirmationRequest);

        Type requestType = new com.google.gson.reflect.TypeToken<RequestMessage<IBANConfirmationRequest>>() {}.getType();
        String jsonRequest = new Gson().toJson(message, requestType);
        proxyService.setJsonBody(jsonRequest);

        Gson gson = builder.getDatebuilder().create();

        responseMessage = gson.fromJson(proxyService.getInputStreamReader(), new TypeToken<ResponseMessage<IBANConfirmationResponse>>() {}.getType());
        proxyService.closeConnection();

        return responseMessage;
    }



    public ResponseMessage<IBANChangeResponse> ibanChange(IBANChangeRequest ibanChangeRequest) throws IOException{

        ResponseMessage<IBANChangeResponse> responseMessage = null;
        url = new URL(serviceURL +  "/iban/change");
        ProxyService proxyService = new ProxyService(context, connectionType, ConnectionMethod.POST, url);

        RequestHeader header = new CreateHeader(authToken, Constants.REQUEST_VERSION).createHeader();

        RequestMessage<IBANChangeRequest> message = new RequestMessage<>();
        message.setRequestHeader(header);
        ibanChangeRequest.setRequestUUID(prefs.getString(Constants.UUID, ""));
        message.setService(ibanChangeRequest);

        Type requestType = new com.google.gson.reflect.TypeToken<RequestMessage<IBANChangeRequest>>() {}.getType();
        String jsonRequest = new Gson().toJson(message, requestType);
        proxyService.setJsonBody(jsonRequest);

        Gson gson = builder.getDatebuilder().create();

        responseMessage = gson.fromJson(proxyService.getInputStreamReader(), new TypeToken<ResponseMessage<IBANChangeResponse>>() {}.getType());
        proxyService.closeConnection();

        return responseMessage;
    }

    public ResponseMessage<CardProfileResponse> getCardProfile(CardProfileRequest cardProfileRequest) throws IOException {

        ResponseMessage<CardProfileResponse> responseMessage = null;
        url = new URL(serviceURL + "/card/info");
        ProxyService proxyService = new ProxyService(context, connectionType, ConnectionMethod.POST, url);

        RequestHeader header = new CreateHeader(authToken, Constants.REQUEST_VERSION).createHeader();

        RequestMessage<CardProfileRequest> message = new RequestMessage<CardProfileRequest>();
        message.setRequestHeader(header);
        cardProfileRequest.setRequestUUID(UUID.randomUUID().toString());
        message.setService(cardProfileRequest);

        Type requestType = new com.google.gson.reflect.TypeToken<RequestMessage<CardProfileRequest>>() {}.getType();
        String jsonRequest = new Gson().toJson(message, requestType);
        proxyService.setJsonBody(jsonRequest);

        Gson gson = builder.getDatebuilder().create();

        responseMessage = gson.fromJson(proxyService.getInputStreamReader(), new TypeToken<ResponseMessage<CardProfileResponse>>() {}.getType());
        proxyService.closeConnection();

        return  responseMessage;
    }

    public ResponseMessage<PurchaseInfoResponse> purchaseInfo(PurchaseInfoRequest purchaseInfoRequest) throws IOException{

        ResponseMessage<PurchaseInfoResponse> responseMessage = null;
        url = new URL(serviceURL + "/purchase/info");
        ProxyService proxyService = new ProxyService(context, connectionType, ConnectionMethod.POST, url);

        RequestHeader header = new CreateHeader(authToken, Constants.REQUEST_VERSION).createHeader();

        RequestMessage<PurchaseInfoRequest> message = new RequestMessage<>();
        message.setRequestHeader(header);
        purchaseInfoRequest.setRequestUUID(UUID.randomUUID().toString());
        message.setService(purchaseInfoRequest);

        Type requestType = new com.google.gson.reflect.TypeToken<RequestMessage<PurchaseInfoRequest>>() {}.getType();
        String jsonRequest = new Gson().toJson(message, requestType);
        proxyService.setJsonBody(jsonRequest);

        Gson gson = builder.getDatebuilder().create();

        responseMessage = gson.fromJson(proxyService.getInputStreamReader(), new TypeToken<ResponseMessage<PurchaseInfoResponse>>() {}.getType());

        proxyService.closeConnection();

        return responseMessage;
    }

    public ResponseMessage<LatestInvoiceContactsResponse> latestInvoiceContacts(LatestInvoiceContactsRequest latestInvoiceContactsRequest) throws IOException{

        ResponseMessage<LatestInvoiceContactsResponse> responseMessage = null;
        url = new URL(serviceURL + "/payment/recent-contacts");
        ProxyService proxyService = new ProxyService(context, connectionType, ConnectionMethod.POST, url);

        RequestHeader header = new CreateHeader(authToken, Constants.REQUEST_VERSION).createHeader();

        RequestMessage<LatestInvoiceContactsRequest> message = new RequestMessage<>();
        message.setRequestHeader(header);
        latestInvoiceContactsRequest.setRequestUUID(UUID.randomUUID().toString());
        message.setService(latestInvoiceContactsRequest);

        Type requestType = new com.google.gson.reflect.TypeToken<RequestMessage<LatestInvoiceContactsRequest>>() {}.getType();
        String jsonRequest = new Gson().toJson(message, requestType);
        proxyService.setJsonBody(jsonRequest);

        Gson gson = builder.getDatebuilder().create();

        responseMessage = gson.fromJson(proxyService.getInputStreamReader(), new TypeToken<ResponseMessage<LatestInvoiceContactsResponse>>() {}.getType());

        proxyService.closeConnection();

        return responseMessage;
    }

    public ResponseMessage<GetTokenFromPSPResponse> getTokenFromPSP(GetTokenFromPSPRequest getTokenFromPSPRequest, int transactionType) throws IOException{

        ResponseMessage<GetTokenFromPSPResponse> responseMessage = null;

        if (transactionType == 0) {
            url = new URL(serviceURL + "/purchase/saman-token");
        }else if (transactionType == 1){
            url = new URL(serviceURL + "/payment/saman-token");
        }
        ProxyService proxyService = new ProxyService(context, connectionType, ConnectionMethod.POST, url);

        RequestHeader header = new CreateHeader(authToken, Constants.REQUEST_VERSION).createHeader();

        RequestMessage<GetTokenFromPSPRequest> message = new RequestMessage<>();
        message.setRequestHeader(header);
        getTokenFromPSPRequest.setRequestUUID(UUID.randomUUID().toString());
        message.setService(getTokenFromPSPRequest);

        Type requestType = new com.google.gson.reflect.TypeToken<RequestMessage<GetTokenFromPSPRequest>>() {}.getType();
        String jsonRequest = new Gson().toJson(message, requestType);
        proxyService.setJsonBody(jsonRequest);

        Gson gson = builder.getDatebuilder().create();

        responseMessage = gson.fromJson(proxyService.getInputStreamReader(), new TypeToken<ResponseMessage<GetTokenFromPSPResponse>>() {}.getType());

        proxyService.closeConnection();

        return responseMessage;
    }

}
