package xyz.homapay.hampay.mobile.android.model;

/**
 * Created by amir on 2/1/16.
 */
public enum NotificationMessageType {

    JOINT("JOINT"),
    PAYMENT("PAYMENT"),
    CREDIT_REQUEST("CREDIT_REQUEST"),
    APP_UPDATE("APP_UPDATE"),
    PURCHASE("PURCHASE");

    private String messageType;

    private NotificationMessageType(String messageType) {
        this.messageType = messageType;
    }

    public String getNotificationMessageType(){
        return this.messageType;
    }
}
