package xyz.homapay.hampay.mobile.android.common.charge;

/**
 * Created by mohammad on 1/11/17.
 */

public class ChargeAdapterModel {

    private int index;
    private String type;
    private String desc;

    public ChargeAdapterModel(int index, String type, String desc) {
        this.index = index;
        this.type = type;
        this.desc = desc;
    }

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }
}
