package xyz.homapay.hampay.mobile.android.service;

import android.app.ActivityManager;
import android.app.IntentService;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;

import com.github.tamir7.contacts.Contact;
import com.github.tamir7.contacts.Contacts;
import com.github.tamir7.contacts.Query;
import com.google.android.gms.gcm.GoogleCloudMessaging;

import java.util.List;

import br.com.goncalves.pugnotification.notification.PugNotification;
import xyz.homapay.hampay.mobile.android.HamPayApplication;
import xyz.homapay.hampay.mobile.android.R;
import xyz.homapay.hampay.mobile.android.activity.ActivityPendingRequestList;
import xyz.homapay.hampay.mobile.android.activity.InvoicePendingConfirmationActivity;
import xyz.homapay.hampay.mobile.android.activity.ProfileEntryActivity;
import xyz.homapay.hampay.mobile.android.activity.RequestBusinessPayDetailActivity;
import xyz.homapay.hampay.mobile.android.activity.TransactionsListActivity;
import xyz.homapay.hampay.mobile.android.model.AppState;
import xyz.homapay.hampay.mobile.android.model.NotificationMessageType;
import xyz.homapay.hampay.mobile.android.receiver.GcmBroadcastReceiver;
import xyz.homapay.hampay.mobile.android.util.Constants;

/**
 * Created by amir on 9/15/15.
 */
public class GcmMessageHandler extends IntentService {

    private Handler handler;
    private String googleMessageType;
    private NotificationMessageType notificationMessageType;
    private String notificationMessage;
    private String notificationName;
    private Long notificationValue;
    private String notificationCallerCellNumber;
    private String purchaseCode;
    private SharedPreferences prefs;
    private String cellNumber;
    private final Handler notificationHandler = new Handler() {
        @Override
        public void handleMessage(Message message) {

            try {
                Uri notification = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
                Ringtone ringtone = RingtoneManager.getRingtone(getApplicationContext(), notification);
                ringtone.play();
            } catch (Exception e) {
                e.printStackTrace();
            }

            AppState appState = AppState.Stoped;
            ActivityManager activityManager = (ActivityManager) getSystemService(ACTIVITY_SERVICE);
            List<ActivityManager.RunningTaskInfo> runningTaskInfos = activityManager.getRunningTasks(1024);
            for (int i = 0; i < runningTaskInfos.size(); i++) {
                if (runningTaskInfos.get(i).baseActivity.getPackageName().equalsIgnoreCase(getApplicationContext().getPackageName())) {
                    if (runningTaskInfos.get(i).baseActivity.getShortClassName().contains("HamPayLoginActivity")) {
                        appState = AppState.Stoped;
                    } else {
                        appState = AppState.Resumed;
                    }
                    break;
                }
            }
            Bundle bundle = new Bundle();
            switch (notificationMessageType) {

                case JOINT:
                    if (cellNumber != null && !cellNumber.equals("")) {
                        if (isThisCellPhoneInContacts()) {
                            PugNotification.with(getApplicationContext())
                                    .load()
                                    .identifier(Constants.PAYMENT_NOTIFICATION_IDENTIFIER)
                                    .title(notificationName)
                                    .message(notificationMessage)
                                    .message(notificationMessage)
                                    .bigTextStyle(notificationMessage)
                                    .smallIcon(R.mipmap.ic_notification)
                                    .color(R.color.colorPrimary)
                                    .lights(Color.rgb(Constants.HAMPAY_RED, Constants.HAMPAY_GREEN, Constants.HAMPAY_BLUE), 2000, 1000)
                                    .ticker(notificationName)
                                    .autoCancel(true)
                                    .simple()
                                    .build();
                        }
                    }
                    break;

                case APP_UPDATE:
                    Intent appStoreIntent;
                    try {
                        appStoreIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + getPackageName()));
                    } catch (android.content.ActivityNotFoundException anfe) {
                        appStoreIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=" + getPackageName()));
                    }
                    PendingIntent pendingIntent = PendingIntent.getActivity(getApplicationContext(), 0,
                            appStoreIntent, PendingIntent.FLAG_CANCEL_CURRENT);
                    PugNotification.with(getApplicationContext())
                            .load()
                            .identifier(Constants.COMMON_NOTIFICATION_IDENTIFIER)
                            .title(notificationName)
                            .message(notificationMessage)
                            .bigTextStyle(notificationMessage)
                            .smallIcon(R.mipmap.ic_notification)
                            .click(pendingIntent)
                            .color(R.color.colorPrimary)
                            .lights(Color.rgb(Constants.HAMPAY_RED, Constants.HAMPAY_GREEN, Constants.HAMPAY_BLUE), 2000, 1000)
                            .ticker(Constants.NOTIFICATION_APP_UPDATE)
                            .autoCancel(true)
                            .simple()
                            .build();
                    break;

                case PAYMENT:

                    switch (appState) {
                        case Stoped:
                            bundle.putBoolean(Constants.HAS_NOTIFICATION, true);
                            bundle.putString(Constants.NOTIFICATION_TYPE, notificationMessageType.getNotificationMessageType());

                            PugNotification.with(getApplicationContext())
                                    .load()
                                    .identifier(Constants.PAYMENT_NOTIFICATION_IDENTIFIER)
                                    .title(notificationName)
                                    .message(notificationMessage)
                                    .message(notificationMessage)
                                    .bigTextStyle(notificationMessage)
                                    .smallIcon(R.mipmap.ic_notification)
                                    .click(ProfileEntryActivity.class, bundle)
                                    .color(R.color.colorPrimary)
                                    .lights(Color.rgb(Constants.HAMPAY_RED, Constants.HAMPAY_GREEN, Constants.HAMPAY_BLUE), 2000, 1000)
                                    .ticker(Constants.NOTIFICATION_PAYMENT)
                                    .autoCancel(true)
                                    .simple()
                                    .build();

                            break;


                        case Resumed:

                            bundle.putString(Constants.CONTACT_PHONE_NO, notificationCallerCellNumber);
                            bundle.putString(Constants.CONTACT_NAME, notificationName);

                            PugNotification.with(getApplicationContext())
                                    .load()
                                    .identifier(Constants.PAYMENT_NOTIFICATION_IDENTIFIER)
                                    .title(notificationName)
                                    .message(notificationMessage)
                                    .bigTextStyle(notificationMessage)
                                    .smallIcon(R.mipmap.ic_notification)
                                    .click(ActivityPendingRequestList.class, bundle)
                                    .color(R.color.colorPrimary)
                                    .lights(Color.rgb(Constants.HAMPAY_RED, Constants.HAMPAY_GREEN, Constants.HAMPAY_BLUE), 2000, 1000)
                                    .ticker(Constants.NOTIFICATION_PAYMENT)
                                    .autoCancel(true)
                                    .simple()
                                    .build();
                            break;

                    }

                    break;

                case CREDIT_REQUEST:

                    switch (appState) {
                        case Stoped:
                            bundle.putBoolean(Constants.HAS_NOTIFICATION, true);
                            bundle.putString(Constants.NOTIFICATION_TYPE, notificationMessageType.getNotificationMessageType());
                            bundle.putString(Constants.CONTACT_PHONE_NO, notificationCallerCellNumber);
                            bundle.putString(Constants.CONTACT_NAME, notificationName);

                            PugNotification.with(getApplicationContext())
                                    .load()
                                    .identifier(Constants.INVOICE_NOTIFICATION_IDENTIFIER)
                                    .title(notificationName)
                                    .message(notificationMessage)
                                    .bigTextStyle(notificationMessage)
                                    .smallIcon(R.mipmap.ic_notification)
                                    .click(ProfileEntryActivity.class, bundle)
                                    .color(R.color.colorPrimary)
                                    .lights(Color.rgb(Constants.HAMPAY_RED, Constants.HAMPAY_GREEN, Constants.HAMPAY_BLUE), 2000, 1000)
                                    .ticker(Constants.NOTIFICATION_CREDIT_REQUEST)
                                    .autoCancel(true)
                                    .simple()
                                    .build();

                            break;


                        case Resumed:

                            bundle.putString(Constants.CONTACT_PHONE_NO, notificationCallerCellNumber);
                            bundle.putString(Constants.CONTACT_NAME, notificationName);

                            PugNotification.with(getApplicationContext())
                                    .load()
                                    .identifier(Constants.INVOICE_NOTIFICATION_IDENTIFIER)
                                    .title(notificationName)
                                    .message(notificationMessage)
                                    .bigTextStyle(notificationMessage)
                                    .smallIcon(R.mipmap.ic_notification)
                                    .click(InvoicePendingConfirmationActivity.class, bundle)
                                    .color(R.color.colorPrimary)
                                    .lights(Color.rgb(Constants.HAMPAY_RED, Constants.HAMPAY_GREEN, Constants.HAMPAY_BLUE), 2000, 1000)
                                    .ticker(Constants.NOTIFICATION_CREDIT_REQUEST)
                                    .autoCancel(true)
                                    .simple()
                                    .build();
                            break;

                    }

                    break;

                case PURCHASE:
                    bundle.putBoolean(Constants.HAS_NOTIFICATION, true);
                    bundle.putString(Constants.NOTIFICATION_TYPE, notificationMessageType.getNotificationMessageType());
                    bundle.putString(Constants.BUSINESS_PURCHASE_CODE, purchaseCode);
                    switch (appState) {
                        case Stoped:
                            PugNotification.with(getApplicationContext())
                                    .load()
                                    .identifier(Constants.MERCHANT_NOTIFICATION_IDENTIFIER)
                                    .title(notificationName)
                                    .message(notificationMessage)
                                    .bigTextStyle(notificationMessage)
                                    .smallIcon(R.mipmap.ic_notification)
                                    .click(ProfileEntryActivity.class, bundle)
                                    .color(R.color.colorPrimary)
                                    .lights(Color.rgb(Constants.HAMPAY_RED, Constants.HAMPAY_GREEN, Constants.HAMPAY_BLUE), 2000, 1000)
                                    .ticker(Constants.NOTIFICATION_CREDIT_REQUEST)
                                    .autoCancel(true)
                                    .simple()
                                    .build();

                            break;
                        case Resumed:
                            PugNotification.with(getApplicationContext())
                                    .load()
                                    .identifier(Constants.MERCHANT_NOTIFICATION_IDENTIFIER)
                                    .title(notificationName)
                                    .message(notificationMessage)
                                    .bigTextStyle(notificationMessage)
                                    .smallIcon(R.mipmap.ic_notification)
                                    .click(RequestBusinessPayDetailActivity.class, bundle)
                                    .color(R.color.colorPrimary)
                                    .lights(Color.rgb(Constants.HAMPAY_RED, Constants.HAMPAY_GREEN, Constants.HAMPAY_BLUE), 2000, 1000)
                                    .ticker(Constants.NOTIFICATION_CREDIT_REQUEST)
                                    .autoCancel(true)
                                    .simple()
                                    .build();
                            break;

                    }

                    break;

                case USER_PAYMENT_CONFIRM:
                    switch (appState) {
                        case Stoped:
                            bundle.putBoolean(Constants.HAS_NOTIFICATION, true);
                            bundle.putString(Constants.NOTIFICATION_TYPE, notificationMessageType.getNotificationMessageType());
                            PugNotification.with(getApplicationContext())
                                    .load()
                                    .identifier(Constants.TRANSACTIONS_NOTIFICATION_IDENTIFIER)
                                    .title(notificationName)
                                    .message(notificationMessage)
                                    .bigTextStyle(notificationMessage)
                                    .smallIcon(R.mipmap.ic_notification)
                                    .click(ProfileEntryActivity.class, bundle)
                                    .color(R.color.colorPrimary)
                                    .lights(Color.rgb(Constants.HAMPAY_RED, Constants.HAMPAY_GREEN, Constants.HAMPAY_BLUE), 2000, 1000)
                                    .ticker(Constants.NOTIFICATION_USER_PAYMENT_CONFIRM)
                                    .autoCancel(true)
                                    .simple()
                                    .build();

                            break;
                        case Resumed:
                            PugNotification.with(getApplicationContext())
                                    .load()
                                    .identifier(Constants.TRANSACTIONS_NOTIFICATION_IDENTIFIER)
                                    .title(notificationName)
                                    .message(notificationMessage)
                                    .bigTextStyle(notificationMessage)
                                    .smallIcon(R.mipmap.ic_notification)
                                    .click(TransactionsListActivity.class, bundle)
                                    .color(R.color.colorPrimary)
                                    .lights(Color.rgb(Constants.HAMPAY_RED, Constants.HAMPAY_GREEN, Constants.HAMPAY_BLUE), 2000, 1000)
                                    .ticker(Constants.NOTIFICATION_USER_PAYMENT_CONFIRM)
                                    .autoCancel(true)
                                    .simple()
                                    .build();
                            break;
                    }
                    break;

                case USER_PAYMENT_CANCEL:
                    switch (appState) {
                        case Stoped:
                            bundle.putBoolean(Constants.HAS_NOTIFICATION, true);
                            bundle.putString(Constants.NOTIFICATION_TYPE, notificationMessageType.getNotificationMessageType());
                            PugNotification.with(getApplicationContext())
                                    .load()
                                    .identifier(Constants.TRANSACTIONS_NOTIFICATION_IDENTIFIER)
                                    .title(notificationName)
                                    .message(notificationMessage)
                                    .bigTextStyle(notificationMessage)
                                    .smallIcon(R.mipmap.ic_notification)
                                    .click(ProfileEntryActivity.class, bundle)
                                    .color(R.color.colorPrimary)
                                    .lights(Color.rgb(Constants.HAMPAY_RED, Constants.HAMPAY_GREEN, Constants.HAMPAY_BLUE), 2000, 1000)
                                    .ticker(Constants.NOTIFICATION_USER_PAYMENT_CANCEL)
                                    .autoCancel(true)
                                    .simple()
                                    .build();

                            break;
                        case Resumed:
                            PugNotification.with(getApplicationContext())
                                    .load()
                                    .identifier(Constants.TRANSACTIONS_NOTIFICATION_IDENTIFIER)
                                    .title(notificationName)
                                    .message(notificationMessage)
                                    .bigTextStyle(notificationMessage)
                                    .smallIcon(R.mipmap.ic_notification)
                                    .click(TransactionsListActivity.class, bundle)
                                    .color(R.color.colorPrimary)
                                    .lights(Color.rgb(Constants.HAMPAY_RED, Constants.HAMPAY_GREEN, Constants.HAMPAY_BLUE), 2000, 1000)
                                    .ticker(Constants.NOTIFICATION_USER_PAYMENT_CANCEL)
                                    .autoCancel(true)
                                    .simple()
                                    .build();
                            break;
                    }
                    break;

                case IMAGE_UPDATED:
                    break;
            }
            if (HamPayApplication.getAppState() == AppState.Stoped) {
            }
        }
    };

    public GcmMessageHandler() {
        super("GcmMessageHandler");
    }

    @Override
    public void onCreate() {
        // TODO Auto-generated method stub
        super.onCreate();
        handler = new Handler();
        prefs = getSharedPreferences(Constants.APP_PREFERENCE_NAME, MODE_PRIVATE);
    }

    private boolean isThisCellPhoneInContacts() {
        Query query = Contacts.getQuery();
        query.hasPhoneNumber();
        query.whereContains(Contact.Field.PhoneNumber, cellNumber);
        return query.find().size() == 0 ? false : true;
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        Bundle extras = intent.getExtras();

        if (!prefs.getBoolean(Constants.NOTIFICATION_STATUS, false)) {
            return;
        }

        GoogleCloudMessaging googleCloudMessaging = GoogleCloudMessaging.getInstance(this);

        googleMessageType = googleCloudMessaging.getMessageType(intent);

        if (extras.getString("type").equalsIgnoreCase(NotificationMessageType.APP_UPDATE.getNotificationMessageType())) {
            Intent intentNotification = new Intent("notification.intent.MAIN").putExtra("get_update", true);
            sendBroadcast(intentNotification);
            notificationMessageType = NotificationMessageType.APP_UPDATE;
            sendMessage();
            GcmBroadcastReceiver.completeWakefulIntent(intent);
        } else if (extras.getString("type").equalsIgnoreCase(NotificationMessageType.JOINT.getNotificationMessageType())) {
            Intent intentNotification = new Intent("notification.intent.MAIN").putExtra("get_update", true);
            sendBroadcast(intentNotification);
            notificationMessageType = NotificationMessageType.JOINT;
            notificationMessage = extras.getString("message");
            notificationName = extras.getString("name");
            cellNumber = extras.getString("cellNumber");
            sendMessage();
            GcmBroadcastReceiver.completeWakefulIntent(intent);
        } else if (extras.getString("type").equalsIgnoreCase(NotificationMessageType.PAYMENT.getNotificationMessageType())) {
            Intent intentNotification = new Intent("notification.intent.MAIN").putExtra("get_update", true);
            sendBroadcast(intentNotification);
            notificationMessageType = NotificationMessageType.PAYMENT;
            sendMessage();
            GcmBroadcastReceiver.completeWakefulIntent(intent);
        } else if (extras.getString("type").equalsIgnoreCase(NotificationMessageType.CREDIT_REQUEST.getNotificationMessageType())) {
            Intent intentNotification = new Intent("notification.intent.MAIN").putExtra("get_update", true);
            sendBroadcast(intentNotification);
            notificationMessageType = NotificationMessageType.CREDIT_REQUEST;
            notificationMessage = extras.getString("message");
            notificationName = extras.getString("name");
            notificationValue = extras.getLong("amount");
            notificationCallerCellNumber = extras.getString("callerCellNumber");
            sendMessage();
            GcmBroadcastReceiver.completeWakefulIntent(intent);
        } else if (extras.getString("type").equalsIgnoreCase(NotificationMessageType.PURCHASE.getNotificationMessageType())) {
            Intent intentNotification = new Intent("notification.intent.MAIN").putExtra("get_update", true);
            sendBroadcast(intentNotification);
            notificationMessageType = NotificationMessageType.PURCHASE;
            notificationMessage = extras.getString("message");
            notificationName = extras.getString("name");
            notificationValue = extras.getLong("amount");
            notificationCallerCellNumber = extras.getString("callerCellNumber");
            purchaseCode = extras.getString("purchaseCode");
            sendMessage();
            GcmBroadcastReceiver.completeWakefulIntent(intent);
        } else if (extras.getString("type").equalsIgnoreCase(NotificationMessageType.USER_PAYMENT_CONFIRM.getNotificationMessageType())) {
            Intent intentNotification = new Intent("notification.intent.MAIN").putExtra("get_update", true);
            sendBroadcast(intentNotification);
            notificationMessageType = NotificationMessageType.USER_PAYMENT_CONFIRM;
            notificationMessage = extras.getString("message");
            notificationName = extras.getString("name");
            sendMessage();
            GcmBroadcastReceiver.completeWakefulIntent(intent);
        } else if (extras.getString("type").equalsIgnoreCase(NotificationMessageType.USER_PAYMENT_CANCEL.getNotificationMessageType())) {
            Intent intentNotification = new Intent("notification.intent.MAIN").putExtra("get_update", true);
            sendBroadcast(intentNotification);
            notificationMessageType = NotificationMessageType.USER_PAYMENT_CANCEL;
            notificationMessage = extras.getString("message");
            notificationName = extras.getString("name");
            sendMessage();
            GcmBroadcastReceiver.completeWakefulIntent(intent);
        } else if (extras.getString("type").equalsIgnoreCase(NotificationMessageType.IMAGE_UPDATED.getNotificationMessageType())) {
            notificationMessageType = NotificationMessageType.IMAGE_UPDATED;
        }

    }

    public void sendMessage() {
        handler.post(() -> notificationHandler.sendEmptyMessage(0));

    }

}
