package xyz.homapay.hampay.mobile.android.service;

import android.app.ActivityManager;
import android.app.IntentService;
import android.app.Notification;
import android.app.PendingIntent;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;

import com.google.android.gms.gcm.GoogleCloudMessaging;

import java.util.List;

import br.com.goncalves.pugnotification.notification.PugNotification;
import xyz.homapay.hampay.mobile.android.HamPayApplication;
import xyz.homapay.hampay.mobile.android.R;
import xyz.homapay.hampay.mobile.android.activity.AppSliderActivity;
import xyz.homapay.hampay.mobile.android.activity.InvoicePendingConfirmationActivity;
import xyz.homapay.hampay.mobile.android.activity.MainActivity;
import xyz.homapay.hampay.mobile.android.activity.PendingPurchasePaymentListActivity;
import xyz.homapay.hampay.mobile.android.activity.RequestBusinessPayDetailActivity;
import xyz.homapay.hampay.mobile.android.activity.TransactionsListActivity;
import xyz.homapay.hampay.mobile.android.model.AppState;
import xyz.homapay.hampay.mobile.android.model.NotificationMessageType;
import xyz.homapay.hampay.mobile.android.receiver.GcmBroadcastReceiver;
import xyz.homapay.hampay.mobile.android.util.Constants;

/**
 * Created by amir on 9/15/15.
 */
public class GcmMessageHandler extends IntentService{


    String headsUpTitle;
    String headsUpContent;
    private Handler handler;

    private int code = 1;

    String googleMessageType;
//    String notificationMessageType;

    NotificationMessageType notificationMessageType;
    String notificationMessage;
    String notificationName;
    Long notificationValue;
    String notificationCallerCellNumber;



    public GcmMessageHandler() {
        super("GcmMessageHandler");
    }

    @Override
    public void onCreate() {
        // TODO Auto-generated method stub
        super.onCreate();
        handler = new Handler();
    }
    @Override
    protected void onHandleIntent(Intent intent) {
        Bundle extras = intent.getExtras();

        GoogleCloudMessaging googleCloudMessaging = GoogleCloudMessaging.getInstance(this);

        googleMessageType = googleCloudMessaging.getMessageType(intent);

        if (extras.getString("type").equalsIgnoreCase(NotificationMessageType.APP_UPDATE.getNotificationMessageType())){
            notificationMessageType = NotificationMessageType.APP_UPDATE;
        }else if (extras.getString("type").equalsIgnoreCase(NotificationMessageType.JOINT.getNotificationMessageType())){
            notificationMessageType = NotificationMessageType.JOINT;
        }else if (extras.getString("type").equalsIgnoreCase(NotificationMessageType.PAYMENT.getNotificationMessageType())){
            notificationMessageType = NotificationMessageType.PAYMENT;
        }else if (extras.getString("type").equalsIgnoreCase(NotificationMessageType.CREDIT_REQUEST.getNotificationMessageType())){
            notificationMessageType = NotificationMessageType.CREDIT_REQUEST;
            notificationMessage = extras.getString("message");
            notificationName = extras.getString("name");
            notificationValue = extras.getLong("amount");
            notificationCallerCellNumber = extras.getString("callerCellNumber");
        }else if (extras.getString("type").equalsIgnoreCase(NotificationMessageType.PURCHASE.getNotificationMessageType())){
            notificationMessageType = NotificationMessageType.PURCHASE;
            notificationMessage = extras.getString("message");
            notificationName = extras.getString("name");
            notificationValue = extras.getLong("amount");
            notificationCallerCellNumber = extras.getString("callerCellNumber");
        }else if (extras.getString("type").equalsIgnoreCase(NotificationMessageType.USER_PAYMENT_CONFIRM.getNotificationMessageType())){
            notificationMessageType = NotificationMessageType.USER_PAYMENT_CONFIRM;
            notificationMessage = extras.getString("message");
            notificationName = extras.getString("name");
        }else if (extras.getString("type").equalsIgnoreCase(NotificationMessageType.USER_PAYMENT_CANCEL.getNotificationMessageType())){
            notificationMessageType = NotificationMessageType.USER_PAYMENT_CANCEL;
            notificationMessage = extras.getString("message");
            notificationName = extras.getString("name");
        }

        sendMessage();

        GcmBroadcastReceiver.completeWakefulIntent(intent);

    }

    public void sendMessage(){
        handler.post(new Runnable() {
            public void run() {
                notificationHandler.sendEmptyMessage(0);
            }
        });

    }

    private final Handler notificationHandler = new Handler(){
        @Override
        public void handleMessage(Message message)
        {

            AppState appState = AppState.Stoped;

            ActivityManager activityManager = (ActivityManager) getSystemService( ACTIVITY_SERVICE );

            List<ActivityManager.RunningTaskInfo> runningTaskInfos = activityManager.getRunningTasks(1024);

            for(int i = 0; i < runningTaskInfos.size(); i++)
            {
                if(runningTaskInfos.get(i).baseActivity.getPackageName().equalsIgnoreCase(getApplicationContext().getPackageName()))
                {
                    if (runningTaskInfos.get(i).baseActivity.getShortClassName().contains("HamPayLoginActivity")){
                        appState = AppState.Stoped;

                    }else {
                        appState = AppState.Resumed;
                    }

                    break;
                }
            }

            Bundle bundle = new Bundle();

            switch (notificationMessageType){

                case JOINT:
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
                            .identifier(1020)
                            .title(notificationName)
                            .message(notificationMessage)
                            .smallIcon(R.mipmap.ic_launcher)
                            .flags(Notification.DEFAULT_ALL)
                            .click(pendingIntent)
                            .color(R.color.colorPrimary)
                            .ticker(Constants.NOTIFICATION_APP_UPDATE)
                            .autoCancel(true)
                            .simple()
                            .build();


                    break;

                case PAYMENT:

                    switch (appState){
                        case Stoped:
                            bundle.putBoolean(Constants.HAS_NOTIFICATION, true);
                            bundle.putString(Constants.NOTIFICATION_TYPE, notificationMessageType.getNotificationMessageType());

                            PugNotification.with(getApplicationContext())
                                    .load()
                                    .identifier(1020)
                                    .title(notificationName)
                                    .message(notificationMessage)
                                    .smallIcon(R.mipmap.ic_launcher)
                                    .flags(Notification.DEFAULT_ALL)
                                    .click(AppSliderActivity.class, bundle)
                                    .color(R.color.colorPrimary)
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
                                    .identifier(1020)
                                    .title(notificationName)
                                    .message(notificationMessage)
                                    .smallIcon(R.mipmap.ic_launcher)
                                    .flags(Notification.DEFAULT_ALL)
                                    .click(PendingPurchasePaymentListActivity.class, bundle)
                                    .color(R.color.colorPrimary)
                                    .ticker(Constants.NOTIFICATION_PAYMENT)
                                    .autoCancel(true)
                                    .simple()
                                    .build();
                            break;

                    }

                    break;

                case CREDIT_REQUEST:

                    switch (appState){
                        case Stoped:
                            bundle.putBoolean(Constants.HAS_NOTIFICATION, true);
                            bundle.putString(Constants.NOTIFICATION_TYPE, notificationMessageType.getNotificationMessageType());
                            bundle.putString(Constants.CONTACT_PHONE_NO, notificationCallerCellNumber);
                            bundle.putString(Constants.CONTACT_NAME, notificationName);

                            PugNotification.with(getApplicationContext())
                                    .load()
                                    .identifier(1020)
                                    .title(notificationName)
                                    .message(notificationMessage)
                                    .smallIcon(R.mipmap.ic_launcher)
                                    .flags(Notification.DEFAULT_ALL)
                                    .click(AppSliderActivity.class, bundle)
                                    .color(R.color.colorPrimary)
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
                                    .identifier(1020)
                                    .title(notificationName)
                                    .message(notificationMessage)
                                    .smallIcon(R.mipmap.ic_launcher)
                                    .flags(Notification.DEFAULT_ALL)
                                    .click(InvoicePendingConfirmationActivity.class, bundle)
                                    .color(R.color.colorPrimary)
                                    .ticker(Constants.NOTIFICATION_CREDIT_REQUEST)
                                    .autoCancel(true)
                                    .simple()
                                    .build();
                            break;

                    }

                    break;

                case PURCHASE:
                    switch (appState){
                        case Stoped:
                            bundle.putBoolean(Constants.HAS_NOTIFICATION, true);
                            bundle.putString(Constants.NOTIFICATION_TYPE, notificationMessageType.getNotificationMessageType());
                            PugNotification.with(getApplicationContext())
                                    .load()
                                    .identifier(1020)
                                    .title(notificationName)
                                    .message(notificationMessage)
                                    .smallIcon(R.mipmap.ic_launcher)
                                    .flags(Notification.DEFAULT_ALL)
                                    .click(AppSliderActivity.class, bundle)
                                    .color(R.color.colorPrimary)
                                    .ticker(Constants.NOTIFICATION_CREDIT_REQUEST)
                                    .autoCancel(true)
                                    .simple()
                                    .build();

                            break;
                        case Resumed:
                            PugNotification.with(getApplicationContext())
                                    .load()
                                    .identifier(1020)
                                    .title(notificationName)
                                    .message(notificationMessage)
                                    .smallIcon(R.mipmap.ic_launcher)
                                    .flags(Notification.DEFAULT_ALL)
                                    .click(RequestBusinessPayDetailActivity.class, bundle)
                                    .color(R.color.colorPrimary)
                                    .ticker(Constants.NOTIFICATION_CREDIT_REQUEST)
                                    .autoCancel(true)
                                    .simple()
                                    .build();
                            break;

                    }

                    break;

                case USER_PAYMENT_CONFIRM:
                    switch (appState){
                        case Stoped:
                            bundle.putBoolean(Constants.HAS_NOTIFICATION, true);
                            bundle.putString(Constants.NOTIFICATION_TYPE, notificationMessageType.getNotificationMessageType());
                            PugNotification.with(getApplicationContext())
                                    .load()
                                    .identifier(1020)
                                    .title(notificationName)
                                    .message(notificationMessage)
                                    .smallIcon(R.mipmap.ic_launcher)
                                    .flags(Notification.DEFAULT_ALL)
                                    .click(AppSliderActivity.class, bundle)
                                    .color(R.color.colorPrimary)
                                    .ticker(Constants.NOTIFICATION_USER_PAYMENT_CONFIRM)
                                    .autoCancel(true)
                                    .simple()
                                    .build();

                            break;
                        case Resumed:
                            PugNotification.with(getApplicationContext())
                                    .load()
                                    .identifier(1020)
                                    .title(notificationName)
                                    .message(notificationMessage)
                                    .smallIcon(R.mipmap.ic_launcher)
                                    .flags(Notification.DEFAULT_ALL)
                                    .click(TransactionsListActivity.class, bundle)
                                    .color(R.color.colorPrimary)
                                    .ticker(Constants.NOTIFICATION_USER_PAYMENT_CONFIRM)
                                    .autoCancel(true)
                                    .simple()
                                    .build();
                            break;
                    }
                    break;

                case USER_PAYMENT_CANCEL:
                    switch (appState){
                        case Stoped:
                            bundle.putBoolean(Constants.HAS_NOTIFICATION, true);
                            bundle.putString(Constants.NOTIFICATION_TYPE, notificationMessageType.getNotificationMessageType());
                            PugNotification.with(getApplicationContext())
                                    .load()
                                    .identifier(1020)
                                    .title(notificationName)
                                    .message(notificationMessage)
                                    .smallIcon(R.mipmap.ic_launcher)
                                    .flags(Notification.DEFAULT_ALL)
//                                    .click(AppSliderActivity.class, bundle)
                                    .color(R.color.colorPrimary)
                                    .ticker(Constants.NOTIFICATION_USER_PAYMENT_CANCEL)
                                    .autoCancel(true)
                                    .simple()
                                    .build();

                            break;
                        case Resumed:
                            PugNotification.with(getApplicationContext())
                                    .load()
                                    .identifier(1020)
                                    .title(notificationName)
                                    .message(notificationMessage)
                                    .smallIcon(R.mipmap.ic_launcher)
                                    .flags(Notification.DEFAULT_ALL)
//                                    .click(TransactionsListActivity.class, bundle)
                                    .color(R.color.colorPrimary)
                                    .ticker(Constants.NOTIFICATION_USER_PAYMENT_CANCEL)
                                    .autoCancel(true)
                                    .simple()
                                    .build();
                            break;
                    }
                    break;
            }


            if (HamPayApplication.getAppState() == AppState.Stoped){
            }


        }
    };

}
