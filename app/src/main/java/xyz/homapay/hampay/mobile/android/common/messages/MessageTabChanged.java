package xyz.homapay.hampay.mobile.android.common.messages;

/**
 * Created by mohammad on 2/2/2017 AD.
 */

public class MessageTabChanged {

    private int selectedPosition;

    public MessageTabChanged(int selectedPosition) {
        this.selectedPosition = selectedPosition;
    }

    public int getSelectedPosition() {
        return selectedPosition;
    }

    public void setSelectedPosition(int selectedPosition) {
        this.selectedPosition = selectedPosition;
    }
}
