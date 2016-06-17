package xyz.homapay.hampay.mobile.android.webservice;

import xyz.homapay.hampay.common.common.request.RequestHeader;

/**
 * Created by amir on 3/7/16.
 */
public class CreateHeader {

    private String authToken;
    private String version;

    public CreateHeader(String authToken, String version){
        this.authToken = authToken;
        this.version = version;
    }

    public RequestHeader createHeader(){
        RequestHeader header = new RequestHeader(authToken, version, System.currentTimeMillis());
        return header;
    }

}
