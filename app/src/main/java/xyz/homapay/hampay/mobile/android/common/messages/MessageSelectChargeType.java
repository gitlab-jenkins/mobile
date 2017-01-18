package xyz.homapay.hampay.mobile.android.common.messages;

/**
 * Created by mohammad on 1/11/17.
 */

public class MessageSelectChargeType {
    private int index;
    private String selectedType;
    private String selectedDescrption;

    public MessageSelectChargeType(int index, String selectedType, String selectedDescrption) {
        this.index = index;
        this.selectedDescrption = selectedDescrption;
        this.selectedType = selectedType;
    }

    public String getSelectedType() {
        return selectedType;
    }

    public int getIndex() {
        return index;
    }

    public String getSelectedDescrption() {
        return selectedDescrption;
    }
}
