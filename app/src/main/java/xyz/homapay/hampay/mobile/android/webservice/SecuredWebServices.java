package xyz.homapay.hampay.mobile.android.webservice;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.IOException;
import java.lang.reflect.Type;
import java.net.URL;
import java.util.UUID;

import xyz.homapay.hampay.common.common.encrypt.EncryptionException;
import xyz.homapay.hampay.common.common.request.LoginRequest;
import xyz.homapay.hampay.common.common.request.LogoutRequest;
import xyz.homapay.hampay.common.common.request.RequestMessage;
import xyz.homapay.hampay.common.common.response.LoginResponse;
import xyz.homapay.hampay.common.common.response.LogoutResponse;
import xyz.homapay.hampay.common.common.response.ResponseMessage;
import xyz.homapay.hampay.common.core.model.request.BusinessPaymentConfirmRequest;
import xyz.homapay.hampay.common.core.model.request.CalcFeeChargeRequest;
import xyz.homapay.hampay.common.core.model.request.CalculateVatRequest;
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
import xyz.homapay.hampay.common.core.model.request.PurchaseDetailRequest;
import xyz.homapay.hampay.common.core.model.request.PurchaseInfoRequest;
import xyz.homapay.hampay.common.core.model.request.RecentPendingFundRequest;
import xyz.homapay.hampay.common.core.model.request.RegistrationVerifyMobileRequest;
import xyz.homapay.hampay.common.core.model.request.RemoveUserImageRequest;
import xyz.homapay.hampay.common.core.model.request.SignToPayRequest;
import xyz.homapay.hampay.common.core.model.request.TACAcceptRequest;
import xyz.homapay.hampay.common.core.model.request.TACRequest;
import xyz.homapay.hampay.common.core.model.request.TransactionDetailRequest;
import xyz.homapay.hampay.common.core.model.request.TransactionListRequest;
import xyz.homapay.hampay.common.core.model.request.UnlinkUserRequest;
import xyz.homapay.hampay.common.core.model.request.UploadImageRequest;
import xyz.homapay.hampay.common.core.model.request.UserMerchantInquiryRequest;
import xyz.homapay.hampay.common.core.model.request.UserMerchantRequest;
import xyz.homapay.hampay.common.core.model.request.UserPaymentRequest;
import xyz.homapay.hampay.common.core.model.request.UserProfileRequest;
import xyz.homapay.hampay.common.core.model.request.UtilityBillDetailRequest;
import xyz.homapay.hampay.common.core.model.request.UtilityBillRequest;
import xyz.homapay.hampay.common.core.model.response.BusinessPaymentConfirmResponse;
import xyz.homapay.hampay.common.core.model.response.CalcFeeChargeResponse;
import xyz.homapay.hampay.common.core.model.response.CalculateVatResponse;
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
import xyz.homapay.hampay.common.core.model.response.PurchaseDetailResponse;
import xyz.homapay.hampay.common.core.model.response.PurchaseInfoResponse;
import xyz.homapay.hampay.common.core.model.response.RecentPendingFundResponse;
import xyz.homapay.hampay.common.core.model.response.RegistrationVerifyMobileResponse;
import xyz.homapay.hampay.common.core.model.response.RemoveUserImageResponse;
import xyz.homapay.hampay.common.core.model.response.SignToPayResponse;
import xyz.homapay.hampay.common.core.model.response.TACAcceptResponse;
import xyz.homapay.hampay.common.core.model.response.TACResponse;
import xyz.homapay.hampay.common.core.model.response.TransactionDetailResponse;
import xyz.homapay.hampay.common.core.model.response.TransactionListResponse;
import xyz.homapay.hampay.common.core.model.response.UnlinkUserResponse;
import xyz.homapay.hampay.common.core.model.response.UploadImageResponse;
import xyz.homapay.hampay.common.core.model.response.UserMerchantInquiryResponse;
import xyz.homapay.hampay.common.core.model.response.UserMerchantResponse;
import xyz.homapay.hampay.common.core.model.response.UserPaymentResponse;
import xyz.homapay.hampay.common.core.model.response.UserProfileResponse;
import xyz.homapay.hampay.common.core.model.response.UtilityBillDetailResponse;
import xyz.homapay.hampay.common.core.model.response.UtilityBillResponse;
import xyz.homapay.hampay.mobile.android.util.Constants;

/**
 * Created by amir on 6/6/15.
 */
public class SecuredWebServices {

    private Context context;
    private SharedPreferences prefs;
    private DateGsonBuilder builder;
    private ConnectionType connectionType;
    private URL url;
    private String serviceURL = "";
    private String authToken = "";

    public SecuredWebServices() {
    }

    public SecuredWebServices(Context context, ConnectionType connectionType, String authToken) {
        this.context = context;
        builder = new DateGsonBuilder();
        this.connectionType = connectionType;
        if (connectionType == ConnectionType.HTTPS) {
            serviceURL = Constants.HTTPS_SERVER_IP;
        } else {
            serviceURL = Constants.HTTP_SERVER_IP;
        }
        this.authToken = authToken;
    }

    public SecuredWebServices(Context context, ConnectionType connectionType) {
        this.context = context;
        prefs = context.getSharedPreferences(Constants.APP_PREFERENCE_NAME, Context.MODE_PRIVATE);
        builder = new DateGsonBuilder();
        this.connectionType = connectionType;
        if (connectionType == ConnectionType.HTTPS) {
            serviceURL = Constants.HTTPS_SERVER_IP;
        } else {
            serviceURL = Constants.HTTP_SERVER_IP;
        }
        this.authToken = prefs.getString(Constants.LOGIN_TOKEN_ID, "");
    }

    public SecuredWebServices(Context context) {
        this.context = context;
        prefs = context.getSharedPreferences(Constants.APP_PREFERENCE_NAME, Context.MODE_PRIVATE);
        builder = new DateGsonBuilder();

        this.authToken = prefs.getString(Constants.LOGIN_TOKEN_ID, "");

    }

    public ResponseMessage<IllegalAppListResponse> getIllegalAppList() throws IOException, EncryptionException {

        ResponseMessage<IllegalAppListResponse> responseMessage = null;
        url = new URL(serviceURL + "/illegal-apps");
        SecuredProxyService proxyService = new SecuredProxyService(true, context, connectionType, ConnectionMethod.POST, url);
        IllegalAppListRequest illegalAppListRequest = new IllegalAppListRequest();
        illegalAppListRequest.setRequestUUID(UUID.randomUUID().toString());
        RequestMessage<IllegalAppListRequest> message = new RequestMessage<>(illegalAppListRequest, authToken, Constants.API_LEVEL, System.currentTimeMillis());
        Type requestType = new TypeToken<RequestMessage<IllegalAppListRequest>>() {
        }.getType();
        String jsonRequest = new Gson().toJson(message, requestType);
        proxyService.setJsonBody(jsonRequest);
        Gson gson = new Gson();
        responseMessage = gson.fromJson(proxyService.getResponse(), new TypeToken<ResponseMessage<IllegalAppListResponse>>() {
        }.getType());
        proxyService.closeConnection();
        return responseMessage;
    }

    public ResponseMessage<ContactUsResponse> contactUsResponse(ContactUsRequest contactUsRequest) throws IOException, EncryptionException {


        ResponseMessage<ContactUsResponse> responseMessage = null;
        url = new URL(serviceURL + "/contactus");
        SecuredProxyService proxyService = new SecuredProxyService(true, context, connectionType, ConnectionMethod.POST, url);

        contactUsRequest.setRequestUUID(UUID.randomUUID().toString());
        RequestMessage<ContactUsRequest> message = new RequestMessage<>(contactUsRequest, authToken, Constants.API_LEVEL, System.currentTimeMillis());

        Type requestType = new TypeToken<RequestMessage<ContactUsRequest>>() {
        }.getType();
        String jsonRequest = new Gson().toJson(message, requestType);
        proxyService.setJsonBody(jsonRequest);

        Gson gson = new Gson();

        responseMessage = gson.fromJson(proxyService.getResponse(), new TypeToken<ResponseMessage<ContactUsResponse>>() {
        }.getType());

        proxyService.closeConnection();

        return responseMessage;

    }

    public ResponseMessage<RegistrationVerifyMobileResponse> registrationVerifyMobileResponse(RegistrationVerifyMobileRequest registrationVerifyMobileRequest) throws IOException, EncryptionException {

        ResponseMessage<RegistrationVerifyMobileResponse> responseMessage = null;
        url = new URL(serviceURL + "/users/reg-verify-mobile");
        SecuredProxyService proxyService = new SecuredProxyService(true, context, connectionType, ConnectionMethod.POST, url);

        registrationVerifyMobileRequest.setRequestUUID(UUID.randomUUID().toString());
        RequestMessage<RegistrationVerifyMobileRequest> message = new RequestMessage<>(registrationVerifyMobileRequest, authToken, Constants.API_LEVEL, System.currentTimeMillis());

        Type requestType = new TypeToken<RequestMessage<RegistrationVerifyMobileRequest>>() {
        }.getType();
        String jsonRequest = new Gson().toJson(message, requestType);
        proxyService.setJsonBody(jsonRequest);

        Gson gson = new Gson();

        responseMessage = gson.fromJson(proxyService.getResponse(), new TypeToken<ResponseMessage<RegistrationVerifyMobileResponse>>() {
        }.getType());

        proxyService.closeConnection();

        return responseMessage;
    }

    public ResponseMessage<MobileRegistrationIdEntryResponse> registrationDeviceRegId(MobileRegistrationIdEntryRequest mobileRegistrationIdEntryRequest) throws IOException, EncryptionException {

        ResponseMessage<MobileRegistrationIdEntryResponse> responseMessage = null;
        url = new URL(serviceURL + "/users/mobile-reg-id-entry");
        SecuredProxyService proxyService = new SecuredProxyService(true, context, connectionType, ConnectionMethod.POST, url);

        mobileRegistrationIdEntryRequest.setRequestUUID(UUID.randomUUID().toString());
        RequestMessage<MobileRegistrationIdEntryRequest> message = new RequestMessage<>(mobileRegistrationIdEntryRequest, authToken, Constants.API_LEVEL, System.currentTimeMillis());

        Type requestType = new TypeToken<RequestMessage<MobileRegistrationIdEntryRequest>>() {
        }.getType();
        String jsonRequest = new Gson().toJson(message, requestType);
        proxyService.setJsonBody(jsonRequest);

        Gson gson = new Gson();

        responseMessage = gson.fromJson(proxyService.getResponse(), new TypeToken<ResponseMessage<MobileRegistrationIdEntryResponse>>() {
        }.getType());

        proxyService.closeConnection();

        return responseMessage;
    }


    public ResponseMessage<TACResponse> tacResponse(TACRequest tacRequest) throws IOException, EncryptionException {

        ResponseMessage<TACResponse> responseMessage = null;
        url = new URL(serviceURL + "/users/tac");
        SecuredProxyService proxyService = new SecuredProxyService(true, context, connectionType, ConnectionMethod.POST, url);

        tacRequest.setRequestUUID(UUID.randomUUID().toString());
        RequestMessage<TACRequest> message = new RequestMessage<>(tacRequest, authToken, Constants.API_LEVEL, System.currentTimeMillis());

        Type requestType = new TypeToken<RequestMessage<TACRequest>>() {
        }.getType();
        String jsonRequest = new Gson().toJson(message, requestType);
        proxyService.setJsonBody(jsonRequest);

        Gson gson = builder.getDatebuilder().create();

        responseMessage = gson.fromJson(proxyService.getResponse(), new TypeToken<ResponseMessage<TACResponse>>() {
        }.getType());

        proxyService.closeConnection();

        return responseMessage;
    }


    public ResponseMessage<UploadImageResponse> uploadImage(UploadImageRequest uploadImageRequest) throws IOException, EncryptionException {

        ResponseMessage<UploadImageResponse> responseMessage = null;
        url = new URL(serviceURL + "/image/upload");
        SecuredProxyService proxyService = new SecuredProxyService(true, context, connectionType, ConnectionMethod.POST, url);

        uploadImageRequest.setRequestUUID(UUID.randomUUID().toString());
        RequestMessage<UploadImageRequest> message = new RequestMessage<>(uploadImageRequest, authToken, Constants.API_LEVEL, System.currentTimeMillis());

        Type requestType = new TypeToken<RequestMessage<UploadImageRequest>>() {
        }.getType();
        String jsonRequest = new Gson().toJson(message, requestType);
        proxyService.setJsonBody(jsonRequest);

        Gson gson = builder.getDatebuilder().create();

        responseMessage = gson.fromJson(proxyService.getResponse(), new TypeToken<ResponseMessage<UploadImageResponse>>() {
        }.getType());

        proxyService.closeConnection();

        return responseMessage;
    }


    public ResponseMessage<GetUserIdTokenResponse> getUserIdTokenResponse(GetUserIdTokenRequest getUserIdTokenRequest) throws IOException, EncryptionException {

        ResponseMessage<GetUserIdTokenResponse> responseMessage = null;
        url = new URL(serviceURL + "/users/get-user-id-token");
        SecuredProxyService proxyService = new SecuredProxyService(true, context, connectionType, ConnectionMethod.POST, url);

        getUserIdTokenRequest.setRequestUUID(UUID.randomUUID().toString());
        RequestMessage<GetUserIdTokenRequest> message = new RequestMessage<>(getUserIdTokenRequest, authToken, Constants.API_LEVEL, System.currentTimeMillis());

        Type requestType = new TypeToken<RequestMessage<GetUserIdTokenRequest>>() {
        }.getType();
        String jsonRequest = new Gson().toJson(message, requestType);
        proxyService.setJsonBody(jsonRequest);

        Gson gson = builder.getDatebuilder().create();

        responseMessage = gson.fromJson(proxyService.getResponse(), new TypeToken<ResponseMessage<GetUserIdTokenResponse>>() {
        }.getType());

        proxyService.closeConnection();

        return responseMessage;
    }


    public ResponseMessage<TACAcceptResponse> tacAcceptResponse(TACAcceptRequest tacAcceptRequest) throws IOException, EncryptionException {

        ResponseMessage<TACAcceptResponse> responseMessage = null;
        url = new URL(serviceURL + "/users/tacaccept");
        SecuredProxyService proxyService = new SecuredProxyService(true, context, connectionType, ConnectionMethod.POST, url);

        tacAcceptRequest.setRequestUUID(UUID.randomUUID().toString());
        RequestMessage<TACAcceptRequest> message = new RequestMessage<>(tacAcceptRequest, authToken, Constants.API_LEVEL, System.currentTimeMillis());

        Type requestType = new TypeToken<RequestMessage<TACAcceptRequest>>() {
        }.getType();
        String jsonRequest = new Gson().toJson(message, requestType);
        proxyService.setJsonBody(jsonRequest);

        Gson gson = builder.getDatebuilder().create();

        responseMessage = gson.fromJson(proxyService.getResponse(), new TypeToken<ResponseMessage<TACAcceptResponse>>() {
        }.getType());

        proxyService.closeConnection();

        return responseMessage;
    }


    public ResponseMessage<UserProfileResponse> getUserProfile(UserProfileRequest userProfileRequest) throws IOException, EncryptionException {

        ResponseMessage<UserProfileResponse> responseMessage = null;
        url = new URL(serviceURL + "/users/profile");
        SecuredProxyService proxyService = new SecuredProxyService(true, context, connectionType, ConnectionMethod.POST, url);

        userProfileRequest.setRequestUUID(UUID.randomUUID().toString());
        RequestMessage<UserProfileRequest> message = new RequestMessage<>(userProfileRequest, authToken, Constants.API_LEVEL, System.currentTimeMillis());

        Type requestType = new TypeToken<RequestMessage<UserProfileRequest>>() {
        }.getType();
        String jsonRequest = new Gson().toJson(message, requestType);
        proxyService.setJsonBody(jsonRequest);

        Gson gson = builder.getDatebuilder().create();

        responseMessage = gson.fromJson(proxyService.getResponse(), new TypeToken<ResponseMessage<UserProfileResponse>>() {
        }.getType());

        proxyService.closeConnection();


        return responseMessage;
    }


    public ResponseMessage<TransactionListResponse> userTransaction(TransactionListRequest transactionListRequest) throws IOException, EncryptionException {

        ResponseMessage<TransactionListResponse> responseMessage = null;
        url = new URL(serviceURL + "/transactions");
        SecuredProxyService proxyService = new SecuredProxyService(true, context, connectionType, ConnectionMethod.POST, url);


        transactionListRequest.setRequestUUID(UUID.randomUUID().toString());
        RequestMessage<TransactionListRequest> message = new RequestMessage<>(transactionListRequest, authToken, Constants.API_LEVEL, System.currentTimeMillis());

        Type requestType = new TypeToken<RequestMessage<TransactionListRequest>>() {
        }.getType();
        String jsonRequest = new Gson().toJson(message, requestType);

        proxyService.setJsonBody(jsonRequest);

        Gson gson = builder.getDatebuilder().create();

        String res = proxyService.getResponse();

        responseMessage = gson.fromJson(res, new TypeToken<ResponseMessage<TransactionListResponse>>() {
        }.getType());

        proxyService.closeConnection();

        return responseMessage;
    }


    public ResponseMessage<ContactsHampayEnabledResponse> getEnabledHamPayContacts(ContactsHampayEnabledRequest contactsHampayEnabledRequest) throws IOException, EncryptionException {

        ResponseMessage<ContactsHampayEnabledResponse> responseMessage = null;
        url = new URL(serviceURL + "/customer/contacts/hp-enabled");
        SecuredProxyService proxyService = new SecuredProxyService(true, context, connectionType, ConnectionMethod.POST, url, true);

        contactsHampayEnabledRequest.setRequestUUID(UUID.randomUUID().toString());
        RequestMessage<ContactsHampayEnabledRequest> message = new RequestMessage<>(contactsHampayEnabledRequest, authToken, Constants.API_LEVEL, System.currentTimeMillis());

        Type requestType = new TypeToken<RequestMessage<ContactsHampayEnabledRequest>>() {
        }.getType();
        String jsonRequest = new Gson().toJson(message, requestType);
        proxyService.setJsonBody(jsonRequest);

        Gson gson = builder.getDatebuilder().create();

        responseMessage = gson.fromJson(proxyService.getResponse(), new TypeToken<ResponseMessage<ContactsHampayEnabledResponse>>() {
        }.getType());

        proxyService.closeConnection();

        return responseMessage;
    }


    public ResponseMessage<IndividualPaymentResponse> individualPayment(IndividualPaymentRequest individualPaymentRequest) throws IOException, EncryptionException {

        ResponseMessage<IndividualPaymentResponse> responseMessage = null;
        url = new URL(serviceURL + "/customers/individual-payment");
        SecuredProxyService proxyService = new SecuredProxyService(true, context, connectionType, ConnectionMethod.POST, url);

        individualPaymentRequest.setRequestUUID(UUID.randomUUID().toString());
        RequestMessage<IndividualPaymentRequest> message = new RequestMessage<>(individualPaymentRequest, authToken, Constants.API_LEVEL, System.currentTimeMillis());

        Type requestType = new TypeToken<RequestMessage<IndividualPaymentRequest>>() {
        }.getType();
        String jsonRequest = new Gson().toJson(message, requestType);
        proxyService.setJsonBody(jsonRequest);

        Gson gson = builder.getDatebuilder().create();

        responseMessage = gson.fromJson(proxyService.getResponse(), new TypeToken<ResponseMessage<IndividualPaymentResponse>>() {
        }.getType());
        proxyService.closeConnection();

        return responseMessage;
    }

    public ResponseMessage<BusinessPaymentConfirmResponse> businessPaymentConfirm(BusinessPaymentConfirmRequest businessPaymentConfirmRequest) throws IOException, EncryptionException {

        ResponseMessage<BusinessPaymentConfirmResponse> responseMessage = null;
        url = new URL(serviceURL + "/businesses/business-payment-confirm");
        SecuredProxyService proxyService = new SecuredProxyService(true, context, connectionType, ConnectionMethod.POST, url);

        businessPaymentConfirmRequest.setRequestUUID(UUID.randomUUID().toString());
        RequestMessage<BusinessPaymentConfirmRequest> message = new RequestMessage<>(businessPaymentConfirmRequest, authToken, Constants.API_LEVEL, System.currentTimeMillis());

        Type requestType = new TypeToken<RequestMessage<BusinessPaymentConfirmRequest>>() {
        }.getType();
        String jsonRequest = new Gson().toJson(message, requestType);
        proxyService.setJsonBody(jsonRequest);

        Gson gson = builder.getDatebuilder().create();

        responseMessage = gson.fromJson(proxyService.getResponse(), new TypeToken<ResponseMessage<BusinessPaymentConfirmResponse>>() {
        }.getType());
        proxyService.closeConnection();

        return responseMessage;
    }

    public ResponseMessage<ChangePassCodeResponse> changePassCodeResponse(ChangePassCodeRequest changePassCodeRequest) throws IOException, EncryptionException {

        ResponseMessage<ChangePassCodeResponse> responseMessage = null;
        url = new URL(serviceURL + "/users/passcode");
        SecuredProxyService proxyService = new SecuredProxyService(true, context, connectionType, ConnectionMethod.PUT, url);

        changePassCodeRequest.setRequestUUID(UUID.randomUUID().toString());
        RequestMessage<ChangePassCodeRequest> message = new RequestMessage<>(changePassCodeRequest, authToken, Constants.API_LEVEL, System.currentTimeMillis());

        Type requestType = new TypeToken<RequestMessage<ChangePassCodeRequest>>() {
        }.getType();
        String jsonRequest = new Gson().toJson(message, requestType);
        proxyService.setJsonBody(jsonRequest);

        Gson gson = builder.getDatebuilder().create();

        responseMessage = gson.fromJson(proxyService.getResponse(), new TypeToken<ResponseMessage<ChangePassCodeResponse>>() {
        }.getType());
        proxyService.closeConnection();

        return responseMessage;
    }

    public ResponseMessage<ChangeMemorableWordResponse> changeMemorableWordResponse(ChangeMemorableWordRequest changeMemorableWordRequest) throws IOException, EncryptionException {

        ResponseMessage<ChangeMemorableWordResponse> responseMessage = null;
        url = new URL(serviceURL + "/users/memorable-word");
        SecuredProxyService proxyService = new SecuredProxyService(true, context, connectionType, ConnectionMethod.PUT, url);

        changeMemorableWordRequest.setRequestUUID(UUID.randomUUID().toString());
        RequestMessage<ChangeMemorableWordRequest> message = new RequestMessage<>(changeMemorableWordRequest, authToken, Constants.API_LEVEL, System.currentTimeMillis());

        Type requestType = new TypeToken<RequestMessage<ChangeMemorableWordRequest>>() {
        }.getType();
        String jsonRequest = new Gson().toJson(message, requestType);
        proxyService.setJsonBody(jsonRequest);

        Gson gson = builder.getDatebuilder().create();

        responseMessage = gson.fromJson(proxyService.getResponse(), new TypeToken<ResponseMessage<ChangeMemorableWordResponse>>() {
        }.getType());
        proxyService.closeConnection();

        return responseMessage;
    }


    public ResponseMessage<UnlinkUserResponse> unlinkUserResponse(UnlinkUserRequest unlinkUserRequest) throws IOException, EncryptionException {

        ResponseMessage<UnlinkUserResponse> responseMessage = null;
        url = new URL(serviceURL + "/users/unlink");
        SecuredProxyService proxyService = new SecuredProxyService(true, context, connectionType, ConnectionMethod.POST, url);

        unlinkUserRequest.setRequestUUID(UUID.randomUUID().toString());
        RequestMessage<UnlinkUserRequest> message = new RequestMessage<>(unlinkUserRequest, authToken, Constants.API_LEVEL, System.currentTimeMillis());

        Type requestType = new TypeToken<RequestMessage<UnlinkUserRequest>>() {
        }.getType();
        String jsonRequest = new Gson().toJson(message, requestType);
        proxyService.setJsonBody(jsonRequest);

        Gson gson = builder.getDatebuilder().create();

        responseMessage = gson.fromJson(proxyService.getResponse(), new TypeToken<ResponseMessage<UnlinkUserResponse>>() {
        }.getType());
        proxyService.closeConnection();

        return responseMessage;
    }


    public ResponseMessage<ChangeEmailResponse> changeEmailResponse(ChangeEmailRequest changeEmailRequest) throws IOException, EncryptionException {

        ResponseMessage<ChangeEmailResponse> responseMessage = null;
        url = new URL(serviceURL + "/users/change-email");
        SecuredProxyService proxyService = new SecuredProxyService(true, context, connectionType, ConnectionMethod.POST, url);

        changeEmailRequest.setRequestUUID(UUID.randomUUID().toString());
        RequestMessage<ChangeEmailRequest> message = new RequestMessage<>(changeEmailRequest, authToken, Constants.API_LEVEL, System.currentTimeMillis());

        Type requestType = new TypeToken<RequestMessage<ChangeEmailRequest>>() {
        }.getType();
        String jsonRequest = new Gson().toJson(message, requestType);
        proxyService.setJsonBody(jsonRequest);

        Gson gson = new Gson();

        responseMessage = gson.fromJson(proxyService.getResponse(), new TypeToken<ResponseMessage<ChangeEmailResponse>>() {
        }.getType());
        proxyService.closeConnection();

        return responseMessage;
    }


    public Bitmap imageDownloader(String imageId) throws IOException {

        url = new URL(serviceURL + imageId);
        ProxyService proxyService = new ProxyService(context, connectionType, ConnectionMethod.GET, url);
        Bitmap bitmap = proxyService.imageInputStream();
        proxyService.closeConnection();
        return bitmap;
    }


    public ResponseMessage<LatestPurchaseResponse> latestUserPurchase(LatestPurchaseRequest latestPurchaseRequest) throws IOException, EncryptionException {

        ResponseMessage<LatestPurchaseResponse> responseMessage = null;
        url = new URL(serviceURL + "/purchase/latest");
        SecuredProxyService proxyService = new SecuredProxyService(true, context, connectionType, ConnectionMethod.POST, url);

        latestPurchaseRequest.setRequestUUID(UUID.randomUUID().toString());
        RequestMessage<LatestPurchaseRequest> message = new RequestMessage<>(latestPurchaseRequest, authToken, Constants.API_LEVEL, System.currentTimeMillis());

        Type requestType = new TypeToken<RequestMessage<LatestPurchaseRequest>>() {
        }.getType();
        String jsonRequest = new Gson().toJson(message, requestType);
        proxyService.setJsonBody(jsonRequest);

        Gson gson = builder.getDatebuilder().create();

        responseMessage = gson.fromJson(proxyService.getResponse(), new TypeToken<ResponseMessage<LatestPurchaseResponse>>() {
        }.getType());
        proxyService.closeConnection();

        return responseMessage;
    }

    public ResponseMessage<LatestPaymentResponse> latestUserPayment(LatestPaymentRequest latestPaymentRequest) throws IOException, EncryptionException {

        ResponseMessage<LatestPaymentResponse> responseMessage = null;
        url = new URL(serviceURL + "/payment/latest");
        SecuredProxyService proxyService = new SecuredProxyService(true, context, connectionType, ConnectionMethod.POST, url);

        latestPaymentRequest.setRequestUUID(UUID.randomUUID().toString());
        RequestMessage<LatestPaymentRequest> message = new RequestMessage<>(latestPaymentRequest, authToken, Constants.API_LEVEL);

        Type requestType = new TypeToken<RequestMessage<LatestPaymentRequest>>() {
        }.getType();
        String jsonRequest = new Gson().toJson(message, requestType);
        proxyService.setJsonBody(jsonRequest);

        Gson gson = builder.getDatebuilder().create();

        responseMessage = gson.fromJson(proxyService.getResponse(), new TypeToken<ResponseMessage<LatestPaymentResponse>>() {
        }.getType());
        proxyService.closeConnection();

        return responseMessage;
    }

    public ResponseMessage<PSPResultResponse> pspResult(PSPResultRequest pspResultRequest) throws IOException, EncryptionException {

        ResponseMessage<PSPResultResponse> responseMessage = null;
        url = new URL(serviceURL + "/fund/psp-result");
        SecuredProxyService proxyService = new SecuredProxyService(true, context, connectionType, ConnectionMethod.POST, url);

        pspResultRequest.setRequestUUID(UUID.randomUUID().toString());
        RequestMessage<PSPResultRequest> message = new RequestMessage<>(pspResultRequest, authToken, Constants.API_LEVEL, System.currentTimeMillis());

        Type requestType = new TypeToken<RequestMessage<PSPResultRequest>>() {
        }.getType();
        String jsonRequest = new Gson().toJson(message, requestType);
        proxyService.setJsonBody(jsonRequest);

        Gson gson = builder.getDatebuilder().create();

        responseMessage = gson.fromJson(proxyService.getResponse(), new TypeToken<ResponseMessage<PSPResultResponse>>() {
        }.getType());
        proxyService.closeConnection();

        return responseMessage;
    }

    public ResponseMessage<UserPaymentResponse> userPaymentResponse(UserPaymentRequest userPaymentRequest) throws IOException, EncryptionException {

        ResponseMessage<UserPaymentResponse> responseMessage = null;
        url = new URL(serviceURL + "/users/payment-request");
        SecuredProxyService proxyService = new SecuredProxyService(true, context, connectionType, ConnectionMethod.POST, url);

        userPaymentRequest.setRequestUUID(UUID.randomUUID().toString());
        RequestMessage<UserPaymentRequest> message = new RequestMessage<>(userPaymentRequest, authToken, Constants.API_LEVEL, System.currentTimeMillis());

        Type requestType = new TypeToken<RequestMessage<UserPaymentRequest>>() {
        }.getType();
        String jsonRequest = new Gson().toJson(message, requestType);
        proxyService.setJsonBody(jsonRequest);

        Gson gson = builder.getDatebuilder().create();

        responseMessage = gson.fromJson(proxyService.getResponse(), new TypeToken<ResponseMessage<UserPaymentResponse>>() {
        }.getType());
        proxyService.closeConnection();

        return responseMessage;
    }


    public ResponseMessage<IBANConfirmationResponse> ibanConfirmation(IBANConfirmationRequest ibanConfirmationRequest) throws IOException, EncryptionException {

        ResponseMessage<IBANConfirmationResponse> responseMessage = null;
        url = new URL(serviceURL + "/iban/confirmation");
        SecuredProxyService proxyService = new SecuredProxyService(true, context, connectionType, ConnectionMethod.POST, url);
        ibanConfirmationRequest.setRequestUUID(UUID.randomUUID().toString());
        RequestMessage<IBANConfirmationRequest> message = new RequestMessage<>(ibanConfirmationRequest, authToken, Constants.API_LEVEL, System.currentTimeMillis());
        Type requestType = new TypeToken<RequestMessage<IBANConfirmationRequest>>() {
        }.getType();
        String jsonRequest = new Gson().toJson(message, requestType);
        proxyService.setJsonBody(jsonRequest);
        Gson gson = builder.getDatebuilder().create();
        responseMessage = gson.fromJson(proxyService.getResponse(), new TypeToken<ResponseMessage<IBANConfirmationResponse>>() {
        }.getType());
        proxyService.closeConnection();
        return responseMessage;
    }


    public ResponseMessage<IBANChangeResponse> ibanChange(IBANChangeRequest ibanChangeRequest) throws IOException, EncryptionException {

        ResponseMessage<IBANChangeResponse> responseMessage = null;
        url = new URL(serviceURL + "/iban/change");
        SecuredProxyService proxyService = new SecuredProxyService(true, context, connectionType, ConnectionMethod.POST, url);

        ibanChangeRequest.setRequestUUID(UUID.randomUUID().toString());
        RequestMessage<IBANChangeRequest> message = new RequestMessage<>(ibanChangeRequest, authToken, Constants.API_LEVEL, System.currentTimeMillis());

        Type requestType = new TypeToken<RequestMessage<IBANChangeRequest>>() {
        }.getType();
        String jsonRequest = new Gson().toJson(message, requestType);
        proxyService.setJsonBody(jsonRequest);

        Gson gson = builder.getDatebuilder().create();

        responseMessage = gson.fromJson(proxyService.getResponse(), new TypeToken<ResponseMessage<IBANChangeResponse>>() {
        }.getType());
        proxyService.closeConnection();

        return responseMessage;
    }

    public ResponseMessage<CardProfileResponse> getCardProfile(CardProfileRequest cardProfileRequest) throws IOException, EncryptionException {

        ResponseMessage<CardProfileResponse> responseMessage = null;
        url = new URL(serviceURL + "/card/info");
        SecuredProxyService proxyService = new SecuredProxyService(true, context, connectionType, ConnectionMethod.POST, url);

        cardProfileRequest.setRequestUUID(UUID.randomUUID().toString());
        RequestMessage<CardProfileRequest> message = new RequestMessage<CardProfileRequest>(cardProfileRequest, authToken, Constants.API_LEVEL, System.currentTimeMillis());

        Type requestType = new TypeToken<RequestMessage<CardProfileRequest>>() {
        }.getType();
        String jsonRequest = new Gson().toJson(message, requestType);
        proxyService.setJsonBody(jsonRequest);

        Gson gson = builder.getDatebuilder().create();

        responseMessage = gson.fromJson(proxyService.getResponse(), new TypeToken<ResponseMessage<CardProfileResponse>>() {
        }.getType());
        proxyService.closeConnection();

        return responseMessage;
    }

    public ResponseMessage<PurchaseInfoResponse> purchaseInfo(PurchaseInfoRequest purchaseInfoRequest) throws IOException, EncryptionException {

        ResponseMessage<PurchaseInfoResponse> responseMessage = null;
        url = new URL(serviceURL + "/purchase/info");
        SecuredProxyService proxyService = new SecuredProxyService(true, context, connectionType, ConnectionMethod.POST, url);

        purchaseInfoRequest.setRequestUUID(UUID.randomUUID().toString());
        RequestMessage<PurchaseInfoRequest> message = new RequestMessage<>(purchaseInfoRequest, authToken, Constants.API_LEVEL, System.currentTimeMillis());

        Type requestType = new TypeToken<RequestMessage<PurchaseInfoRequest>>() {
        }.getType();
        String jsonRequest = new Gson().toJson(message, requestType);
        proxyService.setJsonBody(jsonRequest);

        Gson gson = builder.getDatebuilder().create();

        responseMessage = gson.fromJson(proxyService.getResponse(), new TypeToken<ResponseMessage<PurchaseInfoResponse>>() {
        }.getType());

        proxyService.closeConnection();

        return responseMessage;
    }

    public ResponseMessage<LatestInvoiceContactsResponse> latestInvoiceContacts(LatestInvoiceContactsRequest latestInvoiceContactsRequest) throws IOException, EncryptionException {

        ResponseMessage<LatestInvoiceContactsResponse> responseMessage = null;
        url = new URL(serviceURL + "/payment/recent-contacts");
        SecuredProxyService proxyService = new SecuredProxyService(true, context, connectionType, ConnectionMethod.POST, url);

        latestInvoiceContactsRequest.setRequestUUID(UUID.randomUUID().toString());
        RequestMessage<LatestInvoiceContactsRequest> message = new RequestMessage<>(latestInvoiceContactsRequest, authToken, Constants.API_LEVEL, System.currentTimeMillis());

        Type requestType = new TypeToken<RequestMessage<LatestInvoiceContactsRequest>>() {
        }.getType();
        String jsonRequest = new Gson().toJson(message, requestType);
        proxyService.setJsonBody(jsonRequest);

        Gson gson = builder.getDatebuilder().create();

        responseMessage = gson.fromJson(proxyService.getResponse(), new TypeToken<ResponseMessage<LatestInvoiceContactsResponse>>() {
        }.getType());

        proxyService.closeConnection();

        return responseMessage;
    }

    public ResponseMessage<GetTokenFromPSPResponse> getTokenFromPSP(GetTokenFromPSPRequest getTokenFromPSPRequest, int transactionType) throws IOException, EncryptionException {

        ResponseMessage<GetTokenFromPSPResponse> responseMessage = null;

        if (transactionType == 0) {
            url = new URL(serviceURL + "/purchase/token");
        } else if (transactionType == 1) {
            url = new URL(serviceURL + "/payment/token");
        }
        SecuredProxyService proxyService = new SecuredProxyService(true, context, connectionType, ConnectionMethod.POST, url);

        getTokenFromPSPRequest.setRequestUUID(UUID.randomUUID().toString());
        RequestMessage<GetTokenFromPSPRequest> message = new RequestMessage<>(getTokenFromPSPRequest, authToken, Constants.API_LEVEL, System.currentTimeMillis());

        Type requestType = new TypeToken<RequestMessage<GetTokenFromPSPRequest>>() {
        }.getType();
        String jsonRequest = new Gson().toJson(message, requestType);
        proxyService.setJsonBody(jsonRequest);

        Gson gson = builder.getDatebuilder().create();

        responseMessage = gson.fromJson(proxyService.getResponse(), new TypeToken<ResponseMessage<GetTokenFromPSPResponse>>() {
        }.getType());

        proxyService.closeConnection();

        return responseMessage;
    }

    public ResponseMessage<PaymentDetailResponse> paymentDetail(PaymentDetailRequest paymentDetailRequest) throws IOException, EncryptionException {

        ResponseMessage<PaymentDetailResponse> responseMessage = null;
        url = new URL(serviceURL + "/payment/detail");
        SecuredProxyService proxyService = new SecuredProxyService(true, context, connectionType, ConnectionMethod.POST, url);

        paymentDetailRequest.setRequestUUID(UUID.randomUUID().toString());
        RequestMessage<PaymentDetailRequest> message = new RequestMessage<>(paymentDetailRequest, authToken, Constants.API_LEVEL, System.currentTimeMillis());

        Type requestType = new TypeToken<RequestMessage<PaymentDetailRequest>>() {
        }.getType();
        String jsonRequest = new Gson().toJson(message, requestType);
        proxyService.setJsonBody(jsonRequest);

        Gson gson = builder.getDatebuilder().create();

        responseMessage = gson.fromJson(proxyService.getResponse(), new TypeToken<ResponseMessage<PaymentDetailResponse>>() {
        }.getType());

        proxyService.closeConnection();

        return responseMessage;
    }

    public ResponseMessage<PurchaseDetailResponse> purchaseDetail(PurchaseDetailRequest purchaseDetailRequest) throws IOException, EncryptionException {

        ResponseMessage<PurchaseDetailResponse> responseMessage = null;
        url = new URL(serviceURL + "/purchase/detail");
        SecuredProxyService proxyService = new SecuredProxyService(true, context, connectionType, ConnectionMethod.POST, url);

        purchaseDetailRequest.setRequestUUID(UUID.randomUUID().toString());
        RequestMessage<PurchaseDetailRequest> message = new RequestMessage<>(purchaseDetailRequest, authToken, Constants.API_LEVEL, System.currentTimeMillis());

        Type requestType = new TypeToken<RequestMessage<PurchaseDetailRequest>>() {
        }.getType();
        String jsonRequest = new Gson().toJson(message, requestType);
        proxyService.setJsonBody(jsonRequest);

        Gson gson = builder.getDatebuilder().create();

        responseMessage = gson.fromJson(proxyService.getResponse(), new TypeToken<ResponseMessage<PurchaseDetailResponse>>() {
        }.getType());

        proxyService.closeConnection();

        return responseMessage;
    }

    public ResponseMessage<RemoveUserImageResponse> removeUserImageResponse(RemoveUserImageRequest removeUserImageRequest) throws IOException, EncryptionException {

        ResponseMessage<RemoveUserImageResponse> responseMessage = null;
        url = new URL(serviceURL + "/image/remove");
        SecuredProxyService proxyService = new SecuredProxyService(true, context, connectionType, ConnectionMethod.POST, url);

        removeUserImageRequest.setRequestUUID(UUID.randomUUID().toString());
        RequestMessage<RemoveUserImageRequest> message = new RequestMessage<>(removeUserImageRequest, authToken, Constants.API_LEVEL, System.currentTimeMillis());

        Type requestType = new TypeToken<RequestMessage<RemoveUserImageRequest>>() {
        }.getType();
        String jsonRequest = new Gson().toJson(message, requestType);
        proxyService.setJsonBody(jsonRequest);

        Gson gson = builder.getDatebuilder().create();

        responseMessage = gson.fromJson(proxyService.getResponse(), new TypeToken<ResponseMessage<RemoveUserImageResponse>>() {
        }.getType());

        proxyService.closeConnection();

        return responseMessage;
    }

    public ResponseMessage<CalculateVatResponse> calculateVAT(CalculateVatRequest calculateVatRequest) throws IOException, EncryptionException {

        ResponseMessage<CalculateVatResponse> responseMessage = null;
        url = new URL(serviceURL + "/payment/calculate-vat");
        SecuredProxyService proxyService = new SecuredProxyService(true, context, connectionType, ConnectionMethod.POST, url);

        calculateVatRequest.setRequestUUID(UUID.randomUUID().toString());
        RequestMessage<CalculateVatRequest> message = new RequestMessage<>(calculateVatRequest, authToken, Constants.API_LEVEL, System.currentTimeMillis());

        Type requestType = new TypeToken<RequestMessage<CalculateVatRequest>>() {
        }.getType();
        String jsonRequest = new Gson().toJson(message, requestType);
        proxyService.setJsonBody(jsonRequest);

        Gson gson = builder.getDatebuilder().create();

        responseMessage = gson.fromJson(proxyService.getResponse(), new TypeToken<ResponseMessage<CalculateVatResponse>>() {
        }.getType());

        proxyService.closeConnection();

        return responseMessage;
    }

    public ResponseMessage<CalcFeeChargeResponse> calculateFeeCharge(CalcFeeChargeRequest calcFeeChargeRequest) throws IOException, EncryptionException {

        ResponseMessage<CalcFeeChargeResponse> responseMessage = null;
        url = new URL(serviceURL + "/payment/calculate-vat");
        SecuredProxyService proxyService = new SecuredProxyService(true, context, connectionType, ConnectionMethod.POST, url);

        calcFeeChargeRequest.setRequestUUID(UUID.randomUUID().toString());
        RequestMessage<CalcFeeChargeRequest> message = new RequestMessage<>(calcFeeChargeRequest, authToken, Constants.API_LEVEL, System.currentTimeMillis());

        Type requestType = new TypeToken<RequestMessage<CalcFeeChargeRequest>>() {
        }.getType();
        String jsonRequest = new Gson().toJson(message, requestType);
        proxyService.setJsonBody(jsonRequest);

        Gson gson = builder.getDatebuilder().create();

        responseMessage = gson.fromJson(proxyService.getResponse(), new TypeToken<ResponseMessage<CalcFeeChargeResponse>>() {
        }.getType());

        proxyService.closeConnection();

        return responseMessage;
    }

    public ResponseMessage<RecentPendingFundResponse> recentPendingFund(RecentPendingFundRequest recentPendingFundRequest) throws IOException, EncryptionException {

        ResponseMessage<RecentPendingFundResponse> responseMessage = null;
        url = new URL(serviceURL + "/fund/recent-pending");
        SecuredProxyService proxyService = new SecuredProxyService(true, context, connectionType, ConnectionMethod.POST, url);

        recentPendingFundRequest.setRequestUUID(UUID.randomUUID().toString());
        RequestMessage<RecentPendingFundRequest> message = new RequestMessage<>(recentPendingFundRequest, "", Constants.API_LEVEL, System.currentTimeMillis());

        Type requestType = new TypeToken<RequestMessage<RecentPendingFundRequest>>() {
        }.getType();
        String jsonRequest = new Gson().toJson(message, requestType);
        proxyService.setJsonBody(jsonRequest);

        Gson gson = builder.getDatebuilder().create();
        responseMessage = gson.fromJson(proxyService.getResponse(), new TypeToken<ResponseMessage<RecentPendingFundResponse>>() {
        }.getType());

        proxyService.closeConnection();

        return responseMessage;
    }

    public ResponseMessage<TransactionDetailResponse> transactionDetailResponse(TransactionDetailRequest transactionDetailRequest) throws IOException, EncryptionException {
        ResponseMessage<TransactionDetailResponse> responseMessage = null;
        url = new URL(serviceURL + "/transactions/detail");
        SecuredProxyService proxyService = new SecuredProxyService(true, context, connectionType, ConnectionMethod.POST, url);
        transactionDetailRequest.setRequestUUID(UUID.randomUUID().toString());
        RequestMessage<TransactionDetailRequest> message = new RequestMessage<>(transactionDetailRequest, authToken, Constants.API_LEVEL, System.currentTimeMillis());
        Type requestType = new TypeToken<RequestMessage<TransactionDetailRequest>>() {
        }.getType();
        String jsonRequest = new Gson().toJson(message, requestType);
        proxyService.setJsonBody(jsonRequest);
        Gson gson = builder.getDatebuilder().create();
        responseMessage = gson.fromJson(proxyService.getResponse(), new TypeToken<ResponseMessage<TransactionDetailResponse>>() {
        }.getType());
        proxyService.closeConnection();
        return responseMessage;
    }

    public ResponseMessage<PendingCountResponse> pendingCount(PendingCountRequest pendingCountRequest) throws IOException, EncryptionException {
        ResponseMessage<PendingCountResponse> responseMessage = null;
        url = new URL(serviceURL + "/fund/count-pending");
        SecuredProxyService proxyService = new SecuredProxyService(true, context, connectionType, ConnectionMethod.POST, url);
        pendingCountRequest.setRequestUUID(UUID.randomUUID().toString());
        RequestMessage<PendingCountRequest> message = new RequestMessage<>(pendingCountRequest, authToken, Constants.API_LEVEL, System.currentTimeMillis());
        Type requestType = new TypeToken<RequestMessage<PendingCountRequest>>() {
        }.getType();
        String jsonRequest = new Gson().toJson(message, requestType);
        proxyService.setJsonBody(jsonRequest);
        Gson gson = builder.getDatebuilder().create();
        responseMessage = gson.fromJson(proxyService.getResponse(), new TypeToken<ResponseMessage<PendingCountResponse>>() {
        }.getType());
        proxyService.closeConnection();
        return responseMessage;
    }

    public ResponseMessage<LoginResponse> newLogin(LoginRequest loginRequest, String apiLevel) throws IOException, EncryptionException {

        ResponseMessage<LoginResponse> responseMessage = null;
        url = new URL(serviceURL + "/auth");
        SecuredProxyService proxyService = new SecuredProxyService(true, context, connectionType, ConnectionMethod.POST, url);

        loginRequest.setRequestUUID(UUID.randomUUID().toString());
        RequestMessage<LoginRequest> message = new RequestMessage<>(loginRequest, authToken, apiLevel, System.currentTimeMillis());

        Type requestType = new TypeToken<RequestMessage<LoginRequest>>() {
        }.getType();
        String jsonRequest = new Gson().toJson(message, requestType);
        proxyService.setJsonBody(jsonRequest);

        Gson gson = builder.getDatebuilder().create();

        responseMessage = gson.fromJson(proxyService.getResponse(), new TypeToken<ResponseMessage<LoginResponse>>() {
        }.getType());

        proxyService.closeConnection();

        return responseMessage;
    }

    public ResponseMessage<LogoutResponse> newLogout(LogoutRequest logoutRequest) throws IOException, EncryptionException {

        ResponseMessage<LogoutResponse> responseMessage = null;
        url = new URL(serviceURL + "/unauth");
        SecuredProxyService proxyService = new SecuredProxyService(true, context, connectionType, ConnectionMethod.POST, url);

        logoutRequest.setRequestUUID(UUID.randomUUID().toString());
        RequestMessage<LogoutRequest> message = new RequestMessage<>(logoutRequest, authToken, Constants.API_LEVEL, System.currentTimeMillis());

        Type requestType = new TypeToken<RequestMessage<LogoutRequest>>() {
        }.getType();
        String jsonRequest = new Gson().toJson(message, requestType);
        proxyService.setJsonBody(jsonRequest);

        Gson gson = builder.getDatebuilder().create();

        responseMessage = gson.fromJson(proxyService.getResponse(), new TypeToken<ResponseMessage<LogoutResponse>>() {
        }.getType());

        proxyService.closeConnection();

        return responseMessage;
    }

    public ResponseMessage<UserMerchantInquiryResponse> userMerchantInquiry(UserMerchantInquiryRequest request) throws IOException, EncryptionException {
        ResponseMessage<UserMerchantInquiryResponse> responseMessage = null;
        url = new URL(serviceURL + "/users/merchant/inquiry");
        SecuredProxyService proxyService = new SecuredProxyService(true, context, connectionType, ConnectionMethod.POST, url);
        request.setRequestUUID(UUID.randomUUID().toString());
        RequestMessage<UserMerchantInquiryRequest> message = new RequestMessage<>(request, authToken, Constants.API_LEVEL, System.currentTimeMillis());
        Type requestType = new TypeToken<RequestMessage<UserMerchantInquiryRequest>>() {
        }.getType();
        String jsonRequest = new Gson().toJson(message, requestType);
        proxyService.setJsonBody(jsonRequest);
        Gson gson = new Gson();
        responseMessage = gson.fromJson(proxyService.getResponse(), new TypeToken<ResponseMessage<UserMerchantInquiryResponse>>() {
        }.getType());
        proxyService.closeConnection();
        return responseMessage;
    }

    public ResponseMessage<UserMerchantResponse> userMerchant(UserMerchantRequest request) throws IOException, EncryptionException {
        ResponseMessage<UserMerchantResponse> responseMessage = null;
        url = new URL(serviceURL + "/users/merchant/request");
        SecuredProxyService proxyService = new SecuredProxyService(true, context, connectionType, ConnectionMethod.POST, url);
        request.setRequestUUID(UUID.randomUUID().toString());
        RequestMessage<UserMerchantRequest> message = new RequestMessage<>(request, authToken, Constants.API_LEVEL, System.currentTimeMillis());
        Type requestType = new TypeToken<RequestMessage<UserMerchantRequest>>() {
        }.getType();
        String jsonRequest = new Gson().toJson(message, requestType);
        proxyService.setJsonBody(jsonRequest);
        Gson gson = new Gson();
        responseMessage = gson.fromJson(proxyService.getResponse(), new TypeToken<ResponseMessage<UserMerchantResponse>>() {
        }.getType());
        proxyService.closeConnection();
        return responseMessage;
    }

    public ResponseMessage<SignToPayResponse> signToPay(SignToPayRequest request) throws IOException, EncryptionException {
        ResponseMessage<SignToPayResponse> responseMessage = null;
        url = new URL(serviceURL + "/fund/sign");
        SecuredProxyService proxyService = new SecuredProxyService(true, context, connectionType, ConnectionMethod.POST, url);
        request.setRequestUUID(UUID.randomUUID().toString());
        RequestMessage<SignToPayRequest> message = new RequestMessage<>(request, authToken, Constants.API_LEVEL, System.currentTimeMillis());
        Type requestType = new TypeToken<RequestMessage<SignToPayRequest>>() {
        }.getType();
        String jsonRequest = new Gson().toJson(message, requestType);
        proxyService.setJsonBody(jsonRequest);
        Gson gson = new Gson();
        responseMessage = gson.fromJson(proxyService.getResponse(), new TypeToken<ResponseMessage<SignToPayResponse>>() {
        }.getType());
        proxyService.closeConnection();
        return responseMessage;
    }

    public ResponseMessage<UtilityBillResponse> utilityBill(UtilityBillRequest request) throws IOException, EncryptionException {
        ResponseMessage<UtilityBillResponse> responseMessage = null;
        url = new URL(serviceURL + "/bill/request");
        SecuredProxyService proxyService = new SecuredProxyService(true, context, connectionType, ConnectionMethod.POST, url);
        request.setRequestUUID(UUID.randomUUID().toString());
        RequestMessage<UtilityBillRequest> message = new RequestMessage<>(request, authToken, Constants.API_LEVEL, System.currentTimeMillis());
        Type requestType = new TypeToken<RequestMessage<UtilityBillRequest>>() {
        }.getType();
        String jsonRequest = new Gson().toJson(message, requestType);
        proxyService.setJsonBody(jsonRequest);
        Gson gson = builder.getDatebuilder().create();
        responseMessage = gson.fromJson(proxyService.getResponse(), new TypeToken<ResponseMessage<UtilityBillResponse>>() {
        }.getType());
        proxyService.closeConnection();
        return responseMessage;
    }

    public ResponseMessage<UtilityBillDetailResponse> utilityBillDetail(UtilityBillDetailRequest request) throws IOException, EncryptionException {
        ResponseMessage<UtilityBillDetailResponse> responseMessage = null;
        url = new URL(serviceURL + "/bill/detail");
        SecuredProxyService proxyService = new SecuredProxyService(true, context, connectionType, ConnectionMethod.POST, url);
        request.setRequestUUID(UUID.randomUUID().toString());
        RequestMessage<UtilityBillDetailRequest> message = new RequestMessage<>(request, authToken, Constants.API_LEVEL, System.currentTimeMillis());
        Type requestType = new TypeToken<RequestMessage<UtilityBillDetailRequest>>() {
        }.getType();
        String jsonRequest = new Gson().toJson(message, requestType);
        proxyService.setJsonBody(jsonRequest);
        Gson gson = builder.getDatebuilder().create();
        responseMessage = gson.fromJson(proxyService.getResponse(), new TypeToken<ResponseMessage<UtilityBillDetailResponse>>() {
        }.getType());
        proxyService.closeConnection();
        return responseMessage;
    }

}
