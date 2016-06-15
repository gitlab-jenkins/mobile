package xyz.homapay.hampay.mobile.android.model;

/**
 * Created by amir on 6/15/16.
 */
public class SyncPspResult {

    private int id;
    private String responseCode;
    private String productCode;
    private String swTrace;
    private String type;
    private long timestamp;
    private int status;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getResponseCode() {
        return responseCode;
    }

    public void setResponseCode(String responseCode) {
        this.responseCode = responseCode;
    }

    public String getProductCode() {
        return productCode;
    }

    public void setProductCode(String productCode) {
        this.productCode = productCode;
    }

    public String getSwTrace() {
        return swTrace;
    }

    public void setSwTrace(String swTrace) {
        this.swTrace = swTrace;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }
}
