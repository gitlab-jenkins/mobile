package xyz.homapay.hampay.mobile.android.util;


public class Constants {

//    public static String SERVER_IP = "176.58.104.158";//England Server
    public static String URL_PREFIX = "/hampay";
    //    public static String URL_PREFIX = "";
    //    public static final String SERVER_IP = "192.168.1.132";//Sima Server
//    public static final int SERVER_PORT = 9090;
//    public static String SERVICE_URL = "http://" + SERVER_IP  + ":9090";
//    public static final String OPENAM_LOGIN_URL = "http://"+ SERVER_IP + ":8080/openam/json/authenticate";
//    public static final String OPENAM_LOGOUT_URL = "http://" + SERVER_IP + ":8080/openam/json/sessions/?_action=logout";

//        public static final String HTTPS_SERVER_IP = "https://192.168.1.102" + URL_PREFIX; //Sima Server
    public static String HTTPS_SERVER_IP = "https://176.58.104.158" + URL_PREFIX;//England Server
//    public static String HTTPS_SERVER_IP = "https://176.58.114.27" + URL_PREFIX;//England Server
//        public static String HTTPS_SERVER_IP = "https://mobile.tejaratbourse.com" + URL_PREFIX;//Tejarat bourse Server
    public static final String HTTPS_OPENAM_LOGIN_URL = HTTPS_SERVER_IP + "/auth";
    public static final String HTTPSOPENAM_LOGOUT_URL = HTTPS_SERVER_IP + "/unauth";

    public static final String WEB_PAGE_ADDRESS = "web_page_address";

    public static final String MEMORABLE_WORD = "MemorableWord";
    public static final String USER_VERIFICATION_STATUS = "UserVerificationStatus";

    public static final String LAUNCH_APP_COUNT = "launch_app_count";
    public static final String FORCE_FETCH_ILLEGAL_APPS = "forceFetchIllegalApps";
    public static final String APP_PREFERENCE_NAME = "HamPay_Preferences";
    public static final String TRANSFER_MONEY_COMMENT = "TransferMoneyComment";
    public static final String USER_PROFILE_DTO = "UserProfile";
    public static final String USER_TRANSACTION_DTO = "UserTransaction";
    public static final String CONTACT_PHONE_NO = "contact_phone_no";
    public static final String CONTACT_NAME = "contact_name";
    public static final String REGISTERED_ACTIVITY_DATA = "registeredActivityData";
//    public static final String REGISTERED_CELL_NUMBER = "registeredCellNumber";
    public static final String REGISTERED_BANK_ID = "registeredBankID";
    public static final String REGISTERED_BANK_ACCOUNT_NO_FORMAT = "registeredBankAccountNoFormat";
    public static final String REGISTERED_ACCOUNT_NO = "registeredAccountNo";
    public static final String REGISTERED_NATIONAL_CODE = "registeredNatioalCode";
    public static final String REGISTERED_USER_ID_TOKEN = "registeredUserIdToken";
    public static final String REGISTERED_USER_EMAIL = "registeredUserEmail";
    public static final String REGISTERED_USER_FAMILY = "registeredUserFamily";
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
    public static final String TOKEN_ID = "TokenId";
    public static final String  MOBILE_TIME_OUT = "mobile_time_out";
    public static final long MOBILE_TIME_OUT_INTERVAL = 300000;
    public static final String FORCE_USER_PROFILE = "force_user_profile";
    public static final String RECEIVED_SMS_ACTIVATION = "received_sms_activation";
    public static final String MAX_XFER_Amount = "MaxXferAmount";
    public static final String MIN_XFER_Amount = "MinXferAmount";
    public static final String USER_ENTRY_PASSWORD = "userEntryPassword";
    public static final String LOCAL_ERROR_CODE = "۲۰۰۰";
    public static final String NOTIFICATION = "notification";
    public static final String USER_ID_TOKEN = "userIdToken";
    public static final String UUID = "uuid";
    public static final String IMAGE_PROFILE_SOURCE = "image_source";
    public static final String CAMERA_SELECT = "camera_select";
    public static final String CONTENT_SELECT = "content_select";
    public static final int DEFAULT_ASPECT_RATIO_VALUES = 20;
    public static final int ROTATE_NINETY_DEGREES = 90;
    public static final String ASPECT_RATIO_X = "ASPECT_RATIO_X";
    public static final String ASPECT_RATIO_Y = "ASPECT_RATIO_Y";
    public static final int ON_TOUCH = 1;


}
