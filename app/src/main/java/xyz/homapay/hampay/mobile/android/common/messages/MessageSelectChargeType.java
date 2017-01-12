package xyz.homapay.hampay.mobile.android.common.messages;

/**
 * Created by mohammad on 1/11/17.
 */

public class MessageSelectChargeType {
    private int index;
    private String selectedType;

    public MessageSelectChargeType(int index, String selectedType) {
        this.index = index;
        this.selectedType = selectedType;
    }

    public String getSelectedType() {
        return selectedType;
    }

    public int getIndex() {
        return index;
    }
}
