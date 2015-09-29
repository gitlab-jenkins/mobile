package xyz.homapay.hampay.mobile.android.model;

/**
 * Created by amir on 7/23/15.
 */
public class SuccessLoginResponse {

    private String tokenId;
    private String successUrl;

    public String getTokenId() {
        return tokenId;
    }

    public void setTokenId(String tokenId) {
        this.tokenId = tokenId;
    }

    public String getSuccessUrl() {
        return successUrl;
    }

    public void setSuccessUrl(String successUrl) {
        this.successUrl = successUrl;
    }

}
