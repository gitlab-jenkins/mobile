package xyz.homapay.hampay.mobile.android.model;

/**
 * Created by amir on 1/16/16.
 */
public class LatestPurchase {

    private String purchaseRequestId;
    private String isCanceled;

    public String getPurchaseRequestId() {
        return purchaseRequestId;
    }

    public void setPurchaseRequestId(String purchaseRequestId) {
        this.purchaseRequestId = purchaseRequestId;
    }

    public String getIsCanceled() {
        return isCanceled;
    }

    public void setIsCanceled(String isCanceled) {
        this.isCanceled = isCanceled;
    }
}
