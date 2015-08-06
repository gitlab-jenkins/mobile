package com.hampay.mobile.android.util;


public class Constants {

    public static String SERVER_IP = "176.58.104.158";//England Server
//    public static final String SERVER_IP = "192.168.1.102";//Sima Server
    public static final int SERVER_PORT = 9090;
    public static String SERVICE_URL = "http://" + SERVER_IP  + ":9090";
    public static final String OPENAM_LOGIN_URL = "http://"+ SERVER_IP + ":8080/openam/json/authenticate";
    public static final String OPENAM_LOGOUT_URL = "http://" + SERVER_IP +":8080/openam/json/sessions/?_action=logout";
    public static final String WEB_PAGE_ADDRESS = "web_page_address";

    public static final String MEMORABLE_WORD = "MemorableWord";

    public static final String APP_PREFERENCE_NAME = "HamPay_Preferences";
    public static final String TRANSFER_MONEY_COMMENT = "TransferMoneyComment";
    public static final String USER_PROFILE_DTO = "UserProfile";
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
    public static final String REGISTERED_USER_FAMILY = "registeredUserFamily";
    public static final String LOGIN_TOKEN_ID = "login_token_id";
    public static final String RECENT_PAY_TO_ONE_LIMIT = "10";
    public static final int DEFAULT_PAGE_SIZE = 15;
    public static final String WEB_URL_REGEX = "\\(?\\b(http://|https://|www[.]|app[.])[-A-Za-z0-9+&@#/%?=~_()|!:,.;]*[-A-Za-z0-9+&@#/%=~_()|]";
    public static final String VALID_IP_ADDRESS_SERVICE = "http://www.telize.com/ip";
    public static final String REGISTERED_USER = "registeredUser";
    public static final String TOKEN_ID = "TokenId";


    public static final String ARGS_URI = "com.pooyabyte.varadid.android.messaging.ARGS_URI";
    public static final String ARGS_PARAMS = "com.pooyabyte.varadid.android.messaging.ARGS_PARAMS";
    //    public static final String SERVICE_NAMESPACE = "/ws/";
    public static final String SERVICE_NAMESPACE = "/";
    public static final String MESSAGE_RESPONSE_EXTRA = "messageResponse";
    public static final String MESSAGE_STATUS_EXTRA = "messageStatus";
    public static final String MESSAGE_TYPE_EXTRA = "messageType";
    public static final int MESSAGE_TIMEOUT = 70000;


    public static final String UTF_ENCODING = "UTF-8";
    public static final String JSON_CONTENT_TYPE = "application/json";

    //HomeActivity related extras
    public static final String STRING_EXTRA_FUND_ID = "fundId";
    public static final String STRING_EXTRA_ASSET_ID = "assetId";
    public static final String STRING_EXTRA_ORDER_ID = "orderId";

    public static final String REQUEST_JSON_ENTITY = "requestJsonEntity";

    public static final int REQUEST_CODE_HOME_ACTIVITY = 101;
    public static final int RESULT_CODE_HOME_ACTIVITY = 102;

    public static final int REQUEST_CODE_BASKET_ACTIVITY = 110;
    public static final int RESULT_CODE_BASKET_ACTIVITY = 111;

    public static final int REQUEST_CODE_PURCHASE_ACTIVITY = 112;
    public static final int RESULT_CODE_PURCHASE_ACTIVITY = 113;

    public static final int REQUEST_CODE_PRODUCT_ACTIVITY = 114;
    public static final int RESULT_CODE_PRODUCT_ACTIVITY = 115;

    public static final int REQUEST_CODE_DISMISS_ACTIVITY = 116;
    public static final int RESULT_CODE_DISMISS_ACTIVITY = 117;

    public static final int REQUEST_CODE_REPORT_ACTIVITY = 118;
    public static final int RESULT_CODE_REPORT_ACTIVITY = 119;

//    public static final String DEFAULT_TYPEFACE_NAME = "IRAN Sans.ttf";
    public static final String DEFAULT_TYPEFACE_NAME = "iran-sans.ttf";
    public static final String NUMERIC_TYPEFACE_NAME = "BNaznnBd.ttf";
    public static final String PASSWORD_TYPEFACE_NAME = "BNaznnBd.ttf";

    public static final long PURCHASE_MAXIMUM_THRESHOLD = 500000000;
    public static final long PURCHASE_MINIMUM_THRESHOLD = 10000000;

    public static final int INVALID_SERVER_ERROR_CODE = 4500;
}
