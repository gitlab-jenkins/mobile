package com.hampay.mobile.android.util;


public class Constants {

    public static String SERVER_IP = "176.58.104.158";//England Server
//    public static final String SERVER_IP = "192.168.1.132";//Sima Server
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
    public static final String REGISTERED_USER = "registeredUser";
    public static final String TOKEN_ID = "TokenId";
    public static final String  MOBILE_TIME_OUT = "mobile_time_out";
    public static final long MOBILE_TIME_OUT_INTERVAL = 300000;

}
