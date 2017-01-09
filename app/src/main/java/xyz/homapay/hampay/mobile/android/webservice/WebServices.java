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

import xyz.homapay.hampay.common.common.request.RequestMessage;
import xyz.homapay.hampay.common.common.response.ResponseMessage;
import xyz.homapay.hampay.common.core.model.request.BusinessListRequest;
import xyz.homapay.hampay.common.core.model.request.BusinessPaymentConfirmRequest;
import xyz.homapay.hampay.common.core.model.request.BusinessSearchRequest;
import xyz.homapay.hampay.common.core.model.request.CalculateVatRequest;
import xyz.homapay.hampay.common.core.model.request.CancelFundRequest;
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
import xyz.homapay.hampay.common.core.model.request.IndividualPaymentRequest;
import xyz.homapay.hampay.common.core.model.request.LatestInvoiceContactsRequest;
import xyz.homapay.hampay.common.core.model.request.LatestPaymentRequest;
import xyz.homapay.hampay.common.core.model.request.LatestPurchaseRequest;
import xyz.homapay.hampay.common.core.model.request.MobileRegistrationIdEntryRequest;
import xyz.homapay.hampay.common.core.model.request.PSPResultRequest;
import xyz.homapay.hampay.common.core.model.request.PaymentDetailRequest;
import xyz.homapay.hampay.common.core.model.request.PendingCountRequest;
import xyz.homapay.hampay.common.core.model.request.PendingFundListRequest;
import xyz.homapay.hampay.common.core.model.request.PendingPOListRequest;
import xyz.homapay.hampay.common.core.model.request.PurchaseDetailRequest;
import xyz.homapay.hampay.common.core.model.request.PurchaseInfoRequest;
import xyz.homapay.hampay.common.core.model.request.RecentPendingFundRequest;
import xyz.homapay.hampay.common.core.model.request.RegistrationCredentialsRequest;
import xyz.homapay.hampay.common.core.model.request.RegistrationEntryRequest;
import xyz.homapay.hampay.common.core.model.request.RegistrationSendSmsTokenRequest;
import xyz.homapay.hampay.common.core.model.request.RegistrationVerifyMobileRequest;
import xyz.homapay.hampay.common.core.model.request.RemoveUserImageRequest;
import xyz.homapay.hampay.common.core.model.request.TACAcceptRequest;
import xyz.homapay.hampay.common.core.model.request.TACRequest;
import xyz.homapay.hampay.common.core.model.request.TransactionDetailRequest;
import xyz.homapay.hampay.common.core.model.request.TransactionListRequest;
import xyz.homapay.hampay.common.core.model.request.UnlinkUserRequest;
import xyz.homapay.hampay.common.core.model.request.UploadImageRequest;
import xyz.homapay.hampay.common.core.model.request.UserPaymentRequest;
import xyz.homapay.hampay.common.core.model.request.UserProfileRequest;
import xyz.homapay.hampay.common.core.model.response.BusinessListResponse;
import xyz.homapay.hampay.common.core.model.response.BusinessPaymentConfirmResponse;
import xyz.homapay.hampay.common.core.model.response.CalculateVatResponse;
import xyz.homapay.hampay.common.core.model.response.CancelFundResponse;
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
import xyz.homapay.hampay.common.core.model.response.IndividualPaymentResponse;
import xyz.homapay.hampay.common.core.model.response.LatestInvoiceContactsResponse;
import xyz.homapay.hampay.common.core.model.response.LatestPaymentResponse;
import xyz.homapay.hampay.common.core.model.response.LatestPurchaseResponse;
import xyz.homapay.hampay.common.core.model.response.MobileRegistrationIdEntryResponse;
import xyz.homapay.hampay.common.core.model.response.PSPResultResponse;
import xyz.homapay.hampay.common.core.model.response.PaymentDetailResponse;
import xyz.homapay.hampay.common.core.model.response.PendingCountResponse;
import xyz.homapay.hampay.common.core.model.response.PendingFundListResponse;
import xyz.homapay.hampay.common.core.model.response.PendingPOListResponse;
import xyz.homapay.hampay.common.core.model.response.PurchaseDetailResponse;
import xyz.homapay.hampay.common.core.model.response.PurchaseInfoResponse;
import xyz.homapay.hampay.common.core.model.response.RecentPendingFundResponse;
import xyz.homapay.hampay.common.core.model.response.RegistrationCredentialsResponse;
import xyz.homapay.hampay.common.core.model.response.RegistrationEntryResponse;
import xyz.homapay.hampay.common.core.model.response.RegistrationSendSmsTokenResponse;
import xyz.homapay.hampay.common.core.model.response.RegistrationVerifyMobileResponse;
import xyz.homapay.hampay.common.core.model.response.RemoveUserImageResponse;
import xyz.homapay.hampay.common.core.model.response.TACAcceptResponse;
import xyz.homapay.hampay.common.core.model.response.TACResponse;
import xyz.homapay.hampay.common.core.model.response.TransactionDetailResponse;
import xyz.homapay.hampay.common.core.model.response.TransactionListResponse;
import xyz.homapay.hampay.common.core.model.response.UnlinkUserResponse;
import xyz.homapay.hampay.common.core.model.response.UploadImageResponse;
import xyz.homapay.hampay.common.core.model.response.UserPaymentResponse;
import xyz.homapay.hampay.common.core.model.response.UserProfileResponse;
import xyz.homapay.hampay.mobile.android.model.BillsTokenDoWork;
import xyz.homapay.hampay.mobile.android.model.DoWorkInfo;
import xyz.homapay.hampay.mobile.android.model.LogoutData;
import xyz.homapay.hampay.mobile.android.model.LogoutResponse;
import xyz.homapay.hampay.mobile.android.ssl.AllowHamPaySSL;
import xyz.homapay.hampay.mobile.android.util.Constants;
import xyz.homapay.hampay.mobile.android.util.UserContacts;
import xyz.homapay.hampay.mobile.android.webservice.psp.CBUArrayOfKeyValueOfstringstring;
import xyz.homapay.hampay.mobile.android.webservice.psp.CBUBasicHttpBinding_IGeneralService;
import xyz.homapay.hampay.mobile.android.webservice.psp.bills.MKAArrayOfKeyValueOfstringstring;
import xyz.homapay.hampay.mobile.android.webservice.psp.bills.MKABasicHttpBinding_IGeneralService;

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


        IllegalAppListRequest illegalAppListRequest = new IllegalAppListRequest();
        illegalAppListRequest.setRequestUUID(UUID.randomUUID().toString());

        RequestMessage<IllegalAppListRequest> message = new RequestMessage<>(illegalAppListRequest, authToken, Constants.API_LEVEL, System.currentTimeMillis());

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

        registrationEntryRequest.setRequestUUID(UUID.randomUUID().toString());

        RequestMessage<RegistrationEntryRequest> message = new RequestMessage<>(registrationEntryRequest, authToken, Constants.API_LEVEL, System.currentTimeMillis());

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

        contactUsRequest.setRequestUUID(UUID.randomUUID().toString());
        RequestMessage<ContactUsRequest> message = new RequestMessage<>(contactUsRequest, authToken, Constants.API_LEVEL, System.currentTimeMillis());

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

        registrationSendSmsTokenRequest.setRequestUUID(UUID.randomUUID().toString());
        RequestMessage<RegistrationSendSmsTokenRequest> message = new RequestMessage<>(registrationSendSmsTokenRequest, authToken, Constants.API_LEVEL, System.currentTimeMillis());

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

        registrationVerifyMobileRequest.setRequestUUID(UUID.randomUUID().toString());
        RequestMessage<RegistrationVerifyMobileRequest> message = new RequestMessage<>(registrationVerifyMobileRequest, authToken, Constants.API_LEVEL, System.currentTimeMillis());

        Type requestType = new com.google.gson.reflect.TypeToken<RequestMessage<RegistrationVerifyMobileRequest>>() {}.getType();
        String jsonRequest = new Gson().toJson(message, requestType);
        proxyService.setJsonBody(jsonRequest);

        Gson gson = new Gson();

        responseMessage = gson.fromJson(proxyService.getInputStreamReader(), new TypeToken<ResponseMessage<RegistrationVerifyMobileResponse>>() {}.getType());

        proxyService.closeConnection();

        return responseMessage;
    }


    public ResponseMessage<RegistrationCredentialsResponse> registrationCredentialsResponse(RegistrationCredentialsRequest registrationCredentialsRequest) throws IOException{


        ResponseMessage<RegistrationCredentialsResponse> responseMessage = null;
        url = new URL(serviceURL + "/users/reg-credential-entry");
        ProxyService proxyService = new ProxyService(context, connectionType, ConnectionMethod.POST, url, true);

        UserContacts userContacts = new UserContacts(context);
        registrationCredentialsRequest.setContacts(userContacts.read());
        registrationCredentialsRequest.setRequestUUID(UUID.randomUUID().toString());
        RequestMessage<RegistrationCredentialsRequest> message = new RequestMessage<>(registrationCredentialsRequest, authToken, Constants.API_LEVEL, System.currentTimeMillis());

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

        mobileRegistrationIdEntryRequest.setRequestUUID(UUID.randomUUID().toString());
        RequestMessage<MobileRegistrationIdEntryRequest> message = new RequestMessage<>(mobileRegistrationIdEntryRequest, authToken, Constants.API_LEVEL, System.currentTimeMillis());

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

        tacRequest.setRequestUUID(UUID.randomUUID().toString());
        RequestMessage<TACRequest> message = new RequestMessage<>(tacRequest, authToken, Constants.API_LEVEL, System.currentTimeMillis());

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

        uploadImageRequest.setRequestUUID(UUID.randomUUID().toString());
        RequestMessage<UploadImageRequest> message = new RequestMessage<>(uploadImageRequest, authToken, Constants.API_LEVEL, System.currentTimeMillis());

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

        getUserIdTokenRequest.setRequestUUID(UUID.randomUUID().toString());
        RequestMessage<GetUserIdTokenRequest> message = new RequestMessage<>(getUserIdTokenRequest, authToken, Constants.API_LEVEL, System.currentTimeMillis());

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

        tacAcceptRequest.setRequestUUID(UUID.randomUUID().toString());
        RequestMessage<TACAcceptRequest> message = new RequestMessage<>(tacAcceptRequest, authToken, Constants.API_LEVEL, System.currentTimeMillis());

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

        userProfileRequest.setRequestUUID(UUID.randomUUID().toString());
        RequestMessage<UserProfileRequest> message = new RequestMessage<>(userProfileRequest, authToken, Constants.API_LEVEL, System.currentTimeMillis());

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


        transactionListRequest.setRequestUUID(UUID.randomUUID().toString());
        RequestMessage<TransactionListRequest> message = new RequestMessage<>(transactionListRequest, authToken, Constants.API_LEVEL, System.currentTimeMillis());

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

        UserContacts userContacts = new UserContacts(context);
        contactsHampayEnabledRequest.setContacts(userContacts.read());
        contactsHampayEnabledRequest.setRequestUUID(UUID.randomUUID().toString());
        RequestMessage<ContactsHampayEnabledRequest> message = new RequestMessage<>(contactsHampayEnabledRequest, authToken, Constants.API_LEVEL, System.currentTimeMillis());

        Type requestType = new com.google.gson.reflect.TypeToken<RequestMessage<ContactsHampayEnabledRequest>>() {}.getType();
        String jsonRequest = new Gson().toJson(message, requestType);
        proxyService.setJsonBody(jsonRequest);

        Gson gson = builder.getDatebuilder().create();

        responseMessage = gson.fromJson(proxyService.getInputStreamReader(), new TypeToken<ResponseMessage<ContactsHampayEnabledResponse>>() {}.getType());

        proxyService.closeConnection();

        return responseMessage;
    }


    public ResponseMessage<IndividualPaymentResponse> individualPayment(IndividualPaymentRequest individualPaymentRequest) throws IOException{

        ResponseMessage<IndividualPaymentResponse> responseMessage = null;
        url = new URL(serviceURL + "/customers/individual-payment");
        ProxyService proxyService = new ProxyService(context, connectionType, ConnectionMethod.POST, url);

        individualPaymentRequest.setRequestUUID(UUID.randomUUID().toString());
        RequestMessage<IndividualPaymentRequest> message = new RequestMessage<>(individualPaymentRequest, authToken, Constants.API_LEVEL, System.currentTimeMillis());

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

        businessListRequest.setRequestUUID(UUID.randomUUID().toString());
        RequestMessage<BusinessListRequest> message = new RequestMessage<>(businessListRequest, authToken, Constants.API_LEVEL, System.currentTimeMillis());

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
        url = new URL(serviceURL + "/businesses/business-payment-confirm");
        ProxyService proxyService = new ProxyService(context, connectionType, ConnectionMethod.POST, url);

        businessPaymentConfirmRequest.setRequestUUID(UUID.randomUUID().toString());
        RequestMessage<BusinessPaymentConfirmRequest> message = new RequestMessage<>(businessPaymentConfirmRequest, authToken, Constants.API_LEVEL, System.currentTimeMillis());

        Type requestType = new com.google.gson.reflect.TypeToken<RequestMessage<BusinessPaymentConfirmRequest>>() {}.getType();
        String jsonRequest = new Gson().toJson(message, requestType);
        proxyService.setJsonBody(jsonRequest);

        Gson gson = builder.getDatebuilder().create();

        responseMessage = gson.fromJson(proxyService.getInputStreamReader(), new TypeToken<ResponseMessage<BusinessPaymentConfirmResponse>>() {}.getType());
        proxyService.closeConnection();

        return responseMessage;
    }


    public ResponseMessage<BusinessListResponse> searchBusinessList(BusinessSearchRequest businessSearchRequest) throws IOException{

        ResponseMessage<BusinessListResponse> responseMessage = null;
        url = new URL(serviceURL + "/businesses/search");
        ProxyService proxyService = new ProxyService(context, connectionType, ConnectionMethod.POST, url);

        businessSearchRequest.setRequestUUID(UUID.randomUUID().toString());
        RequestMessage<BusinessSearchRequest> message = new RequestMessage<>(businessSearchRequest, authToken, Constants.API_LEVEL, System.currentTimeMillis());

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

        changePassCodeRequest.setRequestUUID(UUID.randomUUID().toString());
        RequestMessage<ChangePassCodeRequest> message = new RequestMessage<>(changePassCodeRequest, authToken, Constants.API_LEVEL, System.currentTimeMillis());

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

        changeMemorableWordRequest.setRequestUUID(UUID.randomUUID().toString());
        RequestMessage<ChangeMemorableWordRequest> message = new RequestMessage<>(changeMemorableWordRequest, authToken, Constants.API_LEVEL, System.currentTimeMillis());

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

        unlinkUserRequest.setRequestUUID(UUID.randomUUID().toString());
        RequestMessage<UnlinkUserRequest> message = new RequestMessage<>(unlinkUserRequest, authToken, Constants.API_LEVEL, System.currentTimeMillis());

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

        changeEmailRequest.setRequestUUID(UUID.randomUUID().toString());
        RequestMessage<ChangeEmailRequest> message = new RequestMessage<>(changeEmailRequest, authToken, Constants.API_LEVEL, System.currentTimeMillis());

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


    public CBUArrayOfKeyValueOfstringstring purchaseResponse(DoWorkInfo doWorkInfo, String tokenPayUrl) throws Exception {

        AllowHamPaySSL allowHamPaySSL = new AllowHamPaySSL(context);
        allowHamPaySSL.enableHamPaySSL();

//        CBUBasicHttpBinding_IGeneralService basicHttpBinding_iTokenPay = new CBUBasicHttpBinding_IGeneralService(null,"https://" + HpConstants.SERVER + "/saman/psp/pay");
//        CBUBasicHttpBinding_IGeneralService basicHttpBinding_iTokenPay = new CBUBasicHttpBinding_IGeneralService(null, "https://" + "uat.hampay.ir" + "/saman/psp/netpay");
        CBUBasicHttpBinding_IGeneralService basicHttpBinding_iTokenPay = new CBUBasicHttpBinding_IGeneralService(null, tokenPayUrl);
        CBUArrayOfKeyValueOfstringstring responseMessage = basicHttpBinding_iTokenPay.DoWork(doWorkInfo.getUserName(), doWorkInfo.getPassword(), doWorkInfo.getCellNumber(),null,doWorkInfo.getVectorstring2stringMapEntry());

        return responseMessage;
    }


    public MKAArrayOfKeyValueOfstringstring tokenBills(BillsTokenDoWork doWorkInfo, String payUrl) throws Exception {

        AllowHamPaySSL allowHamPaySSL = new AllowHamPaySSL(context);
        allowHamPaySSL.enableHamPaySSL();
        MKABasicHttpBinding_IGeneralService basicHttpBinding_iTokenPay = new MKABasicHttpBinding_IGeneralService(null, payUrl);
        MKAArrayOfKeyValueOfstringstring responseMessage = basicHttpBinding_iTokenPay.DoWork(doWorkInfo.getUserName(), doWorkInfo.getPassword(), doWorkInfo.getCellNumber(),null, doWorkInfo.getVectorstring2stringMapEntry());

        return responseMessage;
    }


    public ResponseMessage<LatestPurchaseResponse> latestUserPurchase(LatestPurchaseRequest latestPurchaseRequest) throws IOException{

        ResponseMessage<LatestPurchaseResponse> responseMessage = null;
        url = new URL(serviceURL + "/purchase/latest");
        ProxyService proxyService = new ProxyService(context, connectionType, ConnectionMethod.POST, url);

        latestPurchaseRequest.setRequestUUID(UUID.randomUUID().toString());
        RequestMessage<LatestPurchaseRequest> message = new RequestMessage<>(latestPurchaseRequest, authToken, Constants.API_LEVEL, System.currentTimeMillis());

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

        latestPaymentRequest.setRequestUUID(UUID.randomUUID().toString());
        RequestMessage<LatestPaymentRequest> message = new RequestMessage<>(latestPaymentRequest, authToken, Constants.API_LEVEL);

        Type requestType = new com.google.gson.reflect.TypeToken<RequestMessage<LatestPaymentRequest>>() {}.getType();
        String jsonRequest = new Gson().toJson(message, requestType);
        proxyService.setJsonBody(jsonRequest);

        Gson gson = builder.getDatebuilder().create();

        responseMessage = gson.fromJson(proxyService.getInputStreamReader(), new TypeToken<ResponseMessage<LatestPaymentResponse>>() {}.getType());
        proxyService.closeConnection();

        return responseMessage;
    }

    public ResponseMessage<PSPResultResponse> pspResult(PSPResultRequest pspResultRequest, int type) throws IOException{

        ResponseMessage<PSPResultResponse> responseMessage = null;
        if (type == 1) {
            url = new URL(serviceURL + "/purchase/psp-result");
        }else if (type == 2){
            url = new URL(serviceURL + "/payment/psp-result");
        }
        ProxyService proxyService = new ProxyService(context, connectionType, ConnectionMethod.POST, url);

        pspResultRequest.setRequestUUID(UUID.randomUUID().toString());
        RequestMessage<PSPResultRequest> message = new RequestMessage<>(pspResultRequest, authToken, Constants.API_LEVEL, System.currentTimeMillis());

        Type requestType = new com.google.gson.reflect.TypeToken<RequestMessage<PSPResultRequest>>() {}.getType();
        String jsonRequest = new Gson().toJson(message, requestType);
        proxyService.setJsonBody(jsonRequest);

        Gson gson = builder.getDatebuilder().create();

        responseMessage = gson.fromJson(proxyService.getInputStreamReader(), new TypeToken<ResponseMessage<PSPResultResponse>>() {}.getType());
        proxyService.closeConnection();

        return responseMessage;
    }


    public ResponseMessage<CancelFundResponse> cancelFund(CancelFundRequest cancelFundRequest) throws IOException{

        ResponseMessage<CancelFundResponse> responseMessage = null;
        url = new URL(serviceURL +  "/purchase/cancel");
        ProxyService proxyService = new ProxyService(context, connectionType, ConnectionMethod.POST, url);

        cancelFundRequest.setRequestUUID(UUID.randomUUID().toString());
        RequestMessage<CancelFundRequest> message = new RequestMessage<>(cancelFundRequest, authToken, Constants.API_LEVEL, System.currentTimeMillis());

        Type requestType = new com.google.gson.reflect.TypeToken<RequestMessage<CancelFundRequest>>() {}.getType();
        String jsonRequest = new Gson().toJson(message, requestType);
        proxyService.setJsonBody(jsonRequest);

        Gson gson = builder.getDatebuilder().create();

        responseMessage = gson.fromJson(proxyService.getInputStreamReader(), new TypeToken<ResponseMessage<CancelFundResponse>>() {}.getType());
        proxyService.closeConnection();

        return responseMessage;
    }

    public ResponseMessage<UserPaymentResponse> userPaymentResponse(UserPaymentRequest userPaymentRequest) throws IOException{

        ResponseMessage<UserPaymentResponse> responseMessage = null;
        url = new URL(serviceURL +  "/users/payment-request");
        ProxyService proxyService = new ProxyService(context, connectionType, ConnectionMethod.POST, url);

        userPaymentRequest.setRequestUUID(UUID.randomUUID().toString());
        RequestMessage<UserPaymentRequest> message = new RequestMessage<>(userPaymentRequest, authToken, Constants.API_LEVEL, System.currentTimeMillis());

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

        ibanConfirmationRequest.setRequestUUID(UUID.randomUUID().toString());
        RequestMessage<IBANConfirmationRequest> message = new RequestMessage<>(ibanConfirmationRequest, authToken, Constants.API_LEVEL, System.currentTimeMillis());

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

        ibanChangeRequest.setRequestUUID(UUID.randomUUID().toString());
        RequestMessage<IBANChangeRequest> message = new RequestMessage<>(ibanChangeRequest, authToken, Constants.API_LEVEL, System.currentTimeMillis());

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

        cardProfileRequest.setRequestUUID(UUID.randomUUID().toString());
        RequestMessage<CardProfileRequest> message = new RequestMessage<CardProfileRequest>(cardProfileRequest, authToken, Constants.API_LEVEL, System.currentTimeMillis());

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

        purchaseInfoRequest.setRequestUUID(UUID.randomUUID().toString());
        RequestMessage<PurchaseInfoRequest> message = new RequestMessage<>(purchaseInfoRequest, authToken, Constants.API_LEVEL, System.currentTimeMillis());

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

        latestInvoiceContactsRequest.setRequestUUID(UUID.randomUUID().toString());
        RequestMessage<LatestInvoiceContactsRequest> message = new RequestMessage<>(latestInvoiceContactsRequest, authToken, Constants.API_LEVEL, System.currentTimeMillis());

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
            url = new URL(serviceURL + "/purchase/token");
        }else if (transactionType == 1){
            url = new URL(serviceURL + "/payment/token");
        }
        ProxyService proxyService = new ProxyService(context, connectionType, ConnectionMethod.POST, url);

        getTokenFromPSPRequest.setRequestUUID(UUID.randomUUID().toString());
        RequestMessage<GetTokenFromPSPRequest> message = new RequestMessage<>(getTokenFromPSPRequest, authToken, Constants.API_LEVEL, System.currentTimeMillis());

        Type requestType = new com.google.gson.reflect.TypeToken<RequestMessage<GetTokenFromPSPRequest>>() {}.getType();
        String jsonRequest = new Gson().toJson(message, requestType);
        proxyService.setJsonBody(jsonRequest);

        Gson gson = builder.getDatebuilder().create();

        responseMessage = gson.fromJson(proxyService.getInputStreamReader(), new TypeToken<ResponseMessage<GetTokenFromPSPResponse>>() {}.getType());

        proxyService.closeConnection();

        return responseMessage;
    }

    public ResponseMessage<PaymentDetailResponse> paymentDetail(PaymentDetailRequest paymentDetailRequest) throws IOException{

        ResponseMessage<PaymentDetailResponse> responseMessage = null;
        url = new URL(serviceURL + "/payment/detail");
        ProxyService proxyService = new ProxyService(context, connectionType, ConnectionMethod.POST, url);

        paymentDetailRequest.setRequestUUID(UUID.randomUUID().toString());
        RequestMessage<PaymentDetailRequest> message = new RequestMessage<>(paymentDetailRequest, authToken, Constants.API_LEVEL, System.currentTimeMillis());

        Type requestType = new com.google.gson.reflect.TypeToken<RequestMessage<PaymentDetailRequest>>() {}.getType();
        String jsonRequest = new Gson().toJson(message, requestType);
        proxyService.setJsonBody(jsonRequest);

        Gson gson = builder.getDatebuilder().create();

        responseMessage = gson.fromJson(proxyService.getInputStreamReader(), new TypeToken<ResponseMessage<PaymentDetailResponse>>() {}.getType());

        proxyService.closeConnection();

        return responseMessage;
    }

    public ResponseMessage<PurchaseDetailResponse> purchaseDetail(PurchaseDetailRequest purchaseDetailRequest) throws IOException{

        ResponseMessage<PurchaseDetailResponse> responseMessage = null;
        url = new URL(serviceURL + "/purchase/detail");
        ProxyService proxyService = new ProxyService(context, connectionType, ConnectionMethod.POST, url);

        purchaseDetailRequest.setRequestUUID(UUID.randomUUID().toString());
        RequestMessage<PurchaseDetailRequest> message = new RequestMessage<>(purchaseDetailRequest, authToken, Constants.API_LEVEL, System.currentTimeMillis());

        Type requestType = new com.google.gson.reflect.TypeToken<RequestMessage<PurchaseDetailRequest>>() {}.getType();
        String jsonRequest = new Gson().toJson(message, requestType);
        proxyService.setJsonBody(jsonRequest);

        Gson gson = builder.getDatebuilder().create();

        responseMessage = gson.fromJson(proxyService.getInputStreamReader(), new TypeToken<ResponseMessage<PurchaseDetailResponse>>() {}.getType());

        proxyService.closeConnection();

        return responseMessage;
    }

    public ResponseMessage<RemoveUserImageResponse> removeUserImageResponse(RemoveUserImageRequest removeUserImageRequest) throws IOException{

        ResponseMessage<RemoveUserImageResponse> responseMessage = null;
        url = new URL(serviceURL + "/users/remove-image");
        ProxyService proxyService = new ProxyService(context, connectionType, ConnectionMethod.POST, url);

        removeUserImageRequest.setRequestUUID(UUID.randomUUID().toString());
        RequestMessage<RemoveUserImageRequest> message = new RequestMessage<>(removeUserImageRequest, authToken, Constants.API_LEVEL, System.currentTimeMillis());

        Type requestType = new com.google.gson.reflect.TypeToken<RequestMessage<RemoveUserImageRequest>>() {}.getType();
        String jsonRequest = new Gson().toJson(message, requestType);
        proxyService.setJsonBody(jsonRequest);

        Gson gson = builder.getDatebuilder().create();

        responseMessage = gson.fromJson(proxyService.getInputStreamReader(), new TypeToken<ResponseMessage<RemoveUserImageResponse>>() {}.getType());

        proxyService.closeConnection();

        return responseMessage;
    }

    public ResponseMessage<PendingPOListResponse> pendingPOList(PendingPOListRequest pendingPOListRequest) throws IOException{

        ResponseMessage<PendingPOListResponse> responseMessage = null;
        url = new URL(serviceURL + "/payment/pending-po-list");
        ProxyService proxyService = new ProxyService(context, connectionType, ConnectionMethod.POST, url);

        pendingPOListRequest.setRequestUUID(UUID.randomUUID().toString());
        RequestMessage<PendingPOListRequest> message = new RequestMessage<>(pendingPOListRequest, authToken, Constants.API_LEVEL, System.currentTimeMillis());

        Type requestType = new com.google.gson.reflect.TypeToken<RequestMessage<PendingPOListRequest>>() {}.getType();
        String jsonRequest = new Gson().toJson(message, requestType);
        proxyService.setJsonBody(jsonRequest);

        Gson gson = builder.getDatebuilder().create();

        responseMessage = gson.fromJson(proxyService.getInputStreamReader(), new TypeToken<ResponseMessage<PendingPOListResponse>>() {}.getType());

        proxyService.closeConnection();

        return responseMessage;
    }

    public ResponseMessage<CalculateVatResponse> calculateVAT(CalculateVatRequest calculateVatRequest) throws IOException{

        ResponseMessage<CalculateVatResponse> responseMessage = null;
        url = new URL(serviceURL + "/payment/calculate-vat");
        ProxyService proxyService = new ProxyService(context, connectionType, ConnectionMethod.POST, url);

        calculateVatRequest.setRequestUUID(UUID.randomUUID().toString());
        RequestMessage<CalculateVatRequest> message = new RequestMessage<>(calculateVatRequest, authToken, Constants.API_LEVEL, System.currentTimeMillis());

        Type requestType = new com.google.gson.reflect.TypeToken<RequestMessage<CalculateVatRequest>>() {}.getType();
        String jsonRequest = new Gson().toJson(message, requestType);
        proxyService.setJsonBody(jsonRequest);

        Gson gson = builder.getDatebuilder().create();

        responseMessage = gson.fromJson(proxyService.getInputStreamReader(), new TypeToken<ResponseMessage<CalculateVatResponse>>() {}.getType());

        proxyService.closeConnection();

        return responseMessage;
    }

    public ResponseMessage<RecentPendingFundResponse> recentPendingFund(RecentPendingFundRequest recentPendingFundRequest) throws IOException{

        ResponseMessage<RecentPendingFundResponse> responseMessage = null;
        url = new URL(serviceURL + "/fund/recent-pending");
        ProxyService proxyService = new ProxyService(context, connectionType, ConnectionMethod.POST, url);

        recentPendingFundRequest.setRequestUUID(UUID.randomUUID().toString());
        RequestMessage<RecentPendingFundRequest> message = new RequestMessage<>(recentPendingFundRequest, authToken, Constants.API_LEVEL, System.currentTimeMillis());

        Type requestType = new com.google.gson.reflect.TypeToken<RequestMessage<RecentPendingFundRequest>>() {}.getType();
        String jsonRequest = new Gson().toJson(message, requestType);
        proxyService.setJsonBody(jsonRequest);

        Gson gson = new Gson();
        responseMessage = gson.fromJson(proxyService.getInputStreamReader(), new TypeToken<ResponseMessage<RecentPendingFundResponse>>() {}.getType());

        proxyService.closeConnection();

        return responseMessage;
    }

    public ResponseMessage<PendingFundListResponse> fundListResponse(PendingFundListRequest pendingFundListRequest) throws IOException{
        ResponseMessage<PendingFundListResponse> responseMessage = null;
        url = new URL(serviceURL + "/fund/pending-list");
        ProxyService proxyService = new ProxyService(context, connectionType, ConnectionMethod.POST, url);
        pendingFundListRequest.setRequestUUID(UUID.randomUUID().toString());
        RequestMessage<PendingFundListRequest> message = new RequestMessage<>(pendingFundListRequest, authToken, Constants.API_LEVEL, System.currentTimeMillis());
        Type requestType = new com.google.gson.reflect.TypeToken<RequestMessage<PendingFundListRequest>>() {}.getType();
        String jsonRequest = new Gson().toJson(message, requestType);
        proxyService.setJsonBody(jsonRequest);
        Gson gson = builder.getDatebuilder().create();
        responseMessage = gson.fromJson(proxyService.getInputStreamReader(), new TypeToken<ResponseMessage<PendingFundListResponse>>() {}.getType());
        proxyService.closeConnection();
        return responseMessage;
    }

    public ResponseMessage<TransactionDetailResponse> transactionDetailResponse(TransactionDetailRequest transactionDetailRequest) throws IOException{
        ResponseMessage<TransactionDetailResponse> responseMessage = null;
        url = new URL(serviceURL + "/transactions/detail");
        ProxyService proxyService = new ProxyService(context, connectionType, ConnectionMethod.POST, url);
        transactionDetailRequest.setRequestUUID(UUID.randomUUID().toString());
        RequestMessage<TransactionDetailRequest> message = new RequestMessage<>(transactionDetailRequest, authToken, Constants.API_LEVEL, System.currentTimeMillis());
        Type requestType = new com.google.gson.reflect.TypeToken<RequestMessage<TransactionDetailRequest>>() {}.getType();
        String jsonRequest = new Gson().toJson(message, requestType);
        proxyService.setJsonBody(jsonRequest);
        Gson gson = builder.getDatebuilder().create();
        responseMessage = gson.fromJson(proxyService.getInputStreamReader(), new TypeToken<ResponseMessage<TransactionDetailResponse>>() {}.getType());
        proxyService.closeConnection();
        return responseMessage;
    }

    public ResponseMessage<PendingCountResponse> pendingCount(PendingCountRequest pendingCountRequest) throws IOException{
        ResponseMessage<PendingCountResponse> responseMessage = null;
        url = new URL(serviceURL + "/fund/count-pending");
        ProxyService proxyService = new ProxyService(context, connectionType, ConnectionMethod.POST, url);
        pendingCountRequest.setRequestUUID(UUID.randomUUID().toString());
        RequestMessage<PendingCountRequest> message = new RequestMessage<>(pendingCountRequest, authToken, Constants.API_LEVEL, System.currentTimeMillis());
        Type requestType = new com.google.gson.reflect.TypeToken<RequestMessage<PendingCountRequest>>() {}.getType();
        String jsonRequest = new Gson().toJson(message, requestType);
        proxyService.setJsonBody(jsonRequest);
        Gson gson = builder.getDatebuilder().create();
        responseMessage = gson.fromJson(proxyService.getInputStreamReader(), new TypeToken<ResponseMessage<PendingCountResponse>>() {}.getType());
        proxyService.closeConnection();
        return responseMessage;
    }

}
