package xyz.homapay.hampay.mobile.android.common.messages;

/**
 * Created by mohammad on 1/22/17.
 */

public class MessageSheetStateChanged {

    private boolean open;

    public MessageSheetStateChanged(boolean open) {
        this.open = open;
    }

    public boolean isOpen() {
        return open;
    }
}
