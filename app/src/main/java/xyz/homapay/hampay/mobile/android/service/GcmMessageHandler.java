package xyz.homapay.hampay.mobile.android.service;

import com.google.android.gms.gcm.GoogleCloudMessaging;

import br.com.goncalves.pugnotification.notification.PugNotification;
import xyz.homapay.hampay.mobile.android.HamPayApplication;
import xyz.homapay.hampay.mobile.android.R;
import xyz.homapay.hampay.mobile.android.activity.AppSliderActivity;
import xyz.homapay.hampay.mobile.android.activity.HamPayLoginActivity;
import xyz.homapay.hampay.mobile.android.activity.IndividualPaymentPendingActivity;
import xyz.homapay.hampay.mobile.android.activity.InvoicePaymentPendingActivity;
import xyz.homapay.hampay.mobile.android.activity.PendingPurchasePaymentActivity;
import xyz.homapay.hampay.mobile.android.activity.RequestBusinessPayDetailActivity;
import xyz.homapay.hampay.mobile.android.component.headsup.HeadsUp;
import xyz.homapay.hampay.mobile.android.component.headsup.HeadsUpManager;
import xyz.homapay.hampay.mobile.android.model.AppState;
import xyz.homapay.hampay.mobile.android.model.NotificationMessageType;
import xyz.homapay.hampay.mobile.android.receiver.GcmBroadcastReceiver;
import xyz.homapay.hampay.mobile.android.util.Constants;
import xyz.homapay.hampay.mobile.android.util.PersianEnglishDigit;

import android.app.ActivityManager;
import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.widget.Toast;

import java.util.List;

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
        }

//        notificationMessageType = extras.getString("type");


//        headsUpTitle = extras.getString("name");
//        headsUpContent = extras.getString("message");



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
                                    .click(PendingPurchasePaymentActivity.class, bundle)
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
                                    .click(InvoicePaymentPendingActivity.class, bundle)
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

//                            bundle.putString(Constants.CONTACT_PHONE_NO, notificationCallerCellNumber);
//                            bundle.putString(Constants.CONTACT_NAME, notificationName);

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
            }


            if (HamPayApplication.getAppState() == AppState.Stoped){
            }

//            int NOTIFICATION_ID = 759;
//            String ns = Context.NOTIFICATION_SERVICE;
//            NotificationManager notificationManager = (NotificationManager) getSystemService(ns);
//
//            Intent notificationIntent = new Intent(getApplicationContext(), HamPayLoginActivity.class);
//            notificationIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP |
//                    Intent.FLAG_ACTIVITY_SINGLE_TOP |
//                    Intent.FLAG_ACTIVITY_NEW_TASK);
//
//            notificationIntent.putExtra(Constants.NOTIFICATION, true);
//            PendingIntent pendingIntent = PendingIntent.getActivity(getApplicationContext(), 0,
//                    notificationIntent, 0);
//
//            HeadsUpManager manage = HeadsUpManager.getInstant(getApplication());
//            HeadsUp.Builder builder = new HeadsUp.Builder(getApplicationContext());
//
//            builder.setContentTitle(headsUpTitle).setDefaults(
//                    Notification.FLAG_AUTO_CANCEL
//                            | Notification.DEFAULT_SOUND
//                            | Notification.DEFAULT_LIGHTS)
//                    .setSmallIcon(R.drawable.tiny_notification)
//                    .setAutoCancel(true)
//                    .setContentIntent(pendingIntent)
//                    .setFullScreenIntent(pendingIntent,false)
//                    .setContentText(/*new PersianEnglishDigit(headsUpContent).E2P()*/"123");
//
//            HeadsUp headsUp = builder.buildHeadUp();
//            headsUp.setSticky(true);
//            manage.notify(code++, headsUp);
//
//            notificationManager.notify(NOTIFICATION_ID, builder.build());


//            ActivityManager activityManager = (ActivityManager) getSystemService( ACTIVITY_SERVICE );
//
//            List<ActivityManager.RunningTaskInfo> taskList = activityManager.getRunningTasks(10);
//
//
//
//            int NOTIFICATION_ID = 759;
//            String ns = Context.NOTIFICATION_SERVICE;
//            NotificationManager notificationManager = (NotificationManager) getSystemService(ns);
//
//            Intent notificationIntent = null;
//            PendingIntent pendingIntent = null;
//

//            if (notificationMessageType.equalsIgnoreCase("APP_UPDATE")){
//
//                try {
//                    notificationIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + getPackageName()));
//                } catch (android.content.ActivityNotFoundException anfe) {
//                    notificationIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=" + getPackageName()));
//                }
//
//                pendingIntent = PendingIntent.getActivity(getApplicationContext(), 0,
//                        notificationIntent, PendingIntent.FLAG_CANCEL_CURRENT);
//
//
//                HeadsUpManager manage = HeadsUpManager.getInstant(getApplication());
//                HeadsUp.Builder builder = new HeadsUp.Builder(getApplicationContext());
//
//
//                builder.setContentTitle(headsUpTitle).setDefaults(
//                        Notification.DEFAULT_LIGHTS
////                                |Notification.FLAG_AUTO_CANCEL
//                                | Notification.DEFAULT_SOUND
//                )
//                        .setSmallIcon(R.drawable.tiny_notification)
//                        .setAutoCancel(true)
//                        .setContentIntent(pendingIntent)
//                        .setFullScreenIntent(pendingIntent, false)
//                        .setContentText(new PersianEnglishDigit(headsUpContent).E2P());
//
//
//                HeadsUp headsUp = builder.buildHeadUp();
//                headsUp.setSticky(true);
//                manage.notify(code++, headsUp);
//
//            }else if (notificationMessageType.equalsIgnoreCase("JOINT")){
//
//
//
//            }else if (notificationMessageType.equalsIgnoreCase("PAYMENT") || notificationMessageType.equalsIgnoreCase("CREDIT_REQUEST")) {
//
//                notificationIntent = new Intent(getApplicationContext(), HamPayLoginActivity.class);
//                notificationIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP |
//                        Intent.FLAG_ACTIVITY_SINGLE_TOP |
//                        Intent.FLAG_ACTIVITY_NEW_TASK);
//
//                notificationIntent.putExtra(Constants.NOTIFICATION, true);
//
//                pendingIntent = PendingIntent.getActivity(getApplicationContext(), 0,
//                        notificationIntent, PendingIntent.FLAG_CANCEL_CURRENT);
//
//
//                HeadsUpManager manage = HeadsUpManager.getInstant(getApplication());
//                HeadsUp.Builder builder = new HeadsUp.Builder(getApplicationContext());
//
//                if (taskList.size() == 0 || taskList.size() == 1) {
//
////                builder.setAutoCancel(true);
//                    builder.setContentTitle(headsUpTitle).setDefaults(
//                            Notification.DEFAULT_LIGHTS
////                                |Notification.FLAG_AUTO_CANCEL
//                                    | Notification.DEFAULT_SOUND
//                    )
//                            .setSmallIcon(R.drawable.tiny_notification)
//                            .setAutoCancel(true)
//                            .setContentIntent(pendingIntent)
//                            .setFullScreenIntent(pendingIntent, false)
//                            .setContentText(new PersianEnglishDigit(headsUpContent).E2P());
//                } else {
////                builder.setAutoCancel(true);
//                    builder.setContentTitle(headsUpTitle).setDefaults(
//                            Notification.DEFAULT_LIGHTS
////                                |Notification.FLAG_AUTO_CANCEL
//                                    | Notification.DEFAULT_SOUND)
//                            .setSmallIcon(R.drawable.tiny_notification)
//                            .setAutoCancel(true)
////                        .setContentIntent(pendingIntent)
////                        .setFullScreenIntent(pendingIntent, false)
//                            .setContentText(new PersianEnglishDigit(headsUpContent).E2P());
//                }
//
//                HeadsUp headsUp = builder.buildHeadUp();
//                headsUp.setSticky(true);
//                manage.notify(code++, headsUp);
//            }
//            else if (type.equalsIgnoreCase("CREDIT_REQUEST")){
//
//            }

//            notificationManager.notify(NOTIFICATION_ID, builder.build());




//            int NOTIFICATION_ID = 759;
//            String ns = Context.NOTIFICATION_SERVICE;
//            NotificationManager mNotificationManager = (NotificationManager) getSystemService(ns);
//
//            int icon = R.drawable.tiny_notification;
//            long when = System.currentTimeMillis();
//            Notification notification = new Notification(icon, getString(R.string.app_name), when);
//            notification.flags = Notification.FLAG_AUTO_CANCEL;
////            notification.flags = Notification.DEFAULT_LIGHTS | Notification.FLAG_AUTO_CANCEL | Notification.FLAG_ONLY_ALERT_ONCE;
//
//            RemoteViews contentView = new RemoteViews(getPackageName(), R.layout.server_notification);
//            contentView.setImageViewResource(R.id.notification_image, R.mipmap.ic_launcher);
////            if (type.equalsIgnoreCase("PAYMENT")) {
////                contentView.setTextViewText(R.id.service_type_value, " " + "1");
////            }else if (type.equalsIgnoreCase("APP_UPDATE")){
////                contentView.setTextViewText(R.id.service_type_value, " " + "1");
////            }else if (type.equalsIgnoreCase("JOINT")){
////                contentView.setTextViewText(R.id.service_type_value, " " + "1");
////            }
//            contentView.setTextViewText(R.id.service_time, new PersianEnglishDigit().E2P(new TimeConvert(when).timeStampToTime()));
//            contentView.setTextViewText(R.id.service_message_value, " " + /*new PersianEnglishDigit(message).E2P()*/"123");
//            notification.contentView = contentView;
//
//            Intent notificationIntent = new Intent(getApplicationContext(), HamPayLoginActivity.class);
//
//            notificationIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP |
//                    Intent.FLAG_ACTIVITY_SINGLE_TOP |
//                    Intent.FLAG_ACTIVITY_NEW_TASK);
//            notificationIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
//            notificationIntent.putExtra(Constants.NOTIFICATION, true);
////            PendingIntent contentIntent = PendingIntent.getActivity(getApplicationContext(), 0, notificationIntent, 0);
//            PendingIntent contentIntent = PendingIntent.getActivity(getApplicationContext(), 0,
//                    notificationIntent, 0);
//            notification.contentIntent = contentIntent;
//
//            //notification.flags |= Notification.FLAG_NO_CLEAR; //Do not clear the notification
//            notification.defaults |= Notification.DEFAULT_LIGHTS; // LED
//            notification.defaults |= Notification.DEFAULT_VIBRATE; //Vibration
//            notification.defaults |= Notification.DEFAULT_SOUND; // Sound
//
//            mNotificationManager.notify(NOTIFICATION_ID, notification);

        }
    };

}
