package xyz.homapay.hampay.mobile.android.common.messages;

/**
 * Created by mohammad on 1/23/2017 AD.
 */

public class MessageKeyboardStateChanged {

    private boolean open;

    public MessageKeyboardStateChanged(boolean open) {
        this.open = open;
    }

    public boolean isOpen() {
        return open;
    }
}
