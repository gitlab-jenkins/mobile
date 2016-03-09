package xyz.homapay.hampay.mobile.android.util;


import xyz.homapay.hampay.mobile.android.webservice.ConnectionType;

public class Constants {
    //    public static String SERVER_IP = "176.58.104.158";//England Server
    public static String URL_PREFIX = "/hampay";
    public static String HTTP_SERVER_IP = "http://176.58.104.158:90" + URL_PREFIX;//England Server
    public static String IPG_URL = "http://176.58.104.158:9090" + URL_PREFIX;//England Server
    public static ConnectionType CONNECTION_TYPE = ConnectionType.HTTPS;
//    public static final String HTTPS_SERVER_IP = "https://192.168.1.101" + URL_PREFIX; //Sima Server
        public static String HTTPS_SERVER_IP = "https://176.58.104.158" + URL_PREFIX;//England Server
//    public static String HTTPS_SERVER_IP = "https://176.58.114.27" + URL_PREFIX;//England Server
//    public static String HTTPS_SERVER_IP = "https://192.168.1.109" + URL_PREFIX;//Masoud Server
    //        public static String HTTPS_SERVER_IP = "https://mobile.tejaratbourse.com" + URL_PREFIX;//Tejarat bourse Server
    public static final String HTTPS_OPENAM_LOGIN_URL = /*HTTPS_SERVER_IP*/ "https://176.58.104.158" + URL_PREFIX + "/auth";
    public static final String HTTPS_OPENAM_LOGOUT_URL = /*HTTPS_SERVER_IP*/ "https://176.58.104.158" + URL_PREFIX + "/unauth";
    public static final String HTTP_OPENAM_LOGIN_URL = /*HTTPS_SERVER_IP*/ "http://176.58.104.158" + URL_PREFIX + "/auth";
    public static final String HTTP_OPENAM_LOGOUT_URL = /*HTTPS_SERVER_IP*/ "http://176.58.104.158" + URL_PREFIX + "/unauth";
    public static final String PSP_SOAO_SERVICE_URL = "176.58.104.158";
    public static final String REQUEST_VERSION = "2.0-PA";
    public static final int SERVICE_CONNECTION_TIMEOUT = 30000;
    public static final int SERVICE_READ_TIMEOUT = 30000;
    public static final String SERVICE_CONTENT_TYPE = "application/json";
    public static final String WEB_PAGE_ADDRESS = "web_page_address";
    public static final String MEMORABLE_WORD = "MemorableWord";
    public static final String LAUNCH_APP_COUNT = "launch_app_count";
    public static final String FORCE_FETCH_ILLEGAL_APPS = "forceFetchIllegalApps";
    public static final String APP_PREFERENCE_NAME = "HamPay_Preferences";
    public static final String USER_PROFILE_DTO = "UserProfile";
    public static final String PENDING_PURCHASE_PAYMENT_ID = "pendingPurchasePaymentId";
    public static final String PENDING_PURCHASE_PAYMENT_COUNT = "pendingPurchasePaymentCount";
    public static final String USER_TRANSACTION_DTO = "UserTransaction";
    public static final String CONTACT_PHONE_NO = "contact_phone_no";
    public static final String CONTACT_NAME = "contact_name";
    public static final String REGISTERED_ACTIVITY_DATA = "registeredActivityData";
    public static final String REGISTERED_CELL_NUMBER = "registeredCellNumber";
    public static final String REGISTERED_BANK_ID = "registeredBankID";
    public static final String REGISTERED_BANK_ACCOUNT_NO_FORMAT = "registeredBankAccountNoFormat";
    public static final String REGISTERED_ACCOUNT_NO = "registeredAccountNo";
    public static final String REGISTERED_NATIONAL_CODE = "registeredNatioalCode";
    public static final String REGISTERED_USER_ID_TOKEN = "registeredUserIdToken";
    public static final String REGISTERED_USER_EMAIL = "registeredUserEmail";
    public static final String REGISTERED_USER_NAME = "registeredUserName";
    public static final String LOGIN_TOKEN_ID = "login_token_id";
    public static final String USER_ACCOUNT_LOCKET = "User Account Locked";
    public static final String SEND_MOBILE_REGISTER_ID = "send_mobile_register_id";
    public static final String RECENT_PAY_TO_ONE_LIMIT = "10";
    public static final int DEFAULT_PAGE_SIZE = 15;
    public static final String WEB_URL_REGEX = "\\(?\\b(http://|https://|www[.]|app[.])[-A-Za-z0-9+&@#/%?=~_()|!:,.;]*[-A-Za-z0-9+&@#/%=~_()|]";
    public static final String REGISTERED_USER = "registeredUser";
    public static final String FETCHED_HAMPAY_ENABLED = "fetched_hampay_enabled";
    public static final String VERIFIED_USER = "verified_user";
    public static final String  MOBILE_TIME_OUT = "mobile_time_out";
    public static final long MOBILE_TIME_OUT_INTERVAL = 300000;
    public static final String FORCE_USER_PROFILE = "force_user_profile";
    public static final String RECEIVED_SMS_ACTIVATION = "received_sms_activation";
    public static final String MAX_BUSINESS_XFER_AMOUNT = "MaxBusinessXferAmount";
    public static final String MIN_BUSINESS_XFER_AMOUNT = "MinBusinessXferAmount";
    public static final String MAX_INDIVIDUAL_XFER_AMOUNT = "MaxIndividualXferAmount";
    public static final String MIN_INDIVIDUAL_XFER_AMOUNT = "MinIndividualXferAmount";
    public static final String USER_ENTRY_PASSWORD = "userEntryPassword";
    public static final String LOCAL_ERROR_CODE = "۲۰۰۰";
    public static final String NOTIFICATION = "notification";
    public static final String USER_ID_TOKEN = "userIdToken";
    public static final String UUID = "uuid";
    public static final String IMAGE_PROFILE_SOURCE = "image_source";
    public static final String CAMERA_SELECT = "camera_select";
    public static final String CONTENT_SELECT = "content_select";
    public static final int DEFAULT_ASPECT_RATIO_VALUES = 20;
    public static final String ASPECT_RATIO_X = "ASPECT_RATIO_X";
    public static final String ASPECT_RATIO_Y = "ASPECT_RATIO_Y";
    public static final String PENDING_PAYMENT_REQUEST_LIST = "pending_payment_request_list";
    public static final String CARD_INFO = "card_info";
    public static final String PSP_NAME = "psp_name";
    public static final String HAS_NOTIFICATION = "has_notification";
    public static final String NOTIFICATION_TYPE = "notification_type";
    public static final String NOTIFICATION_APP_UPDATE = "نسخه جدید هم‌پی";
    public static final String NOTIFICATION_JOINT = "دوست هم‌پی جدید";
    public static final String NOTIFICATION_PAYMENT = "انتقال وجه";
    public static final String NOTIFICATION_CREDIT_REQUEST = "درخواست وجه";
    public static final String USER_IMAGE_PROFILE_ID = "userImageProfileId";
    public static final String PAYMENT_INFO = "paymantInfo";
    public static final String PSP_INFO = "pspInfo";
}
