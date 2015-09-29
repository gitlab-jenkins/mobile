package xyz.homapay.hampay.mobile.android.service;

import com.google.android.gms.gcm.GoogleCloudMessaging;
import xyz.homapay.hampay.mobile.android.R;
import xyz.homapay.hampay.mobile.android.activity.HamPayLoginActivity;
import xyz.homapay.hampay.mobile.android.component.headsup.HeadsUp;
import xyz.homapay.hampay.mobile.android.component.headsup.HeadsUpManager;
import xyz.homapay.hampay.mobile.android.receiver.GcmBroadcastReceiver;
import xyz.homapay.hampay.mobile.android.util.Constants;
import xyz.homapay.hampay.mobile.android.util.PersianEnglishDigit;
import xyz.homapay.hampay.mobile.android.util.TimeConvert;

import android.app.ActivityManager;
import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.widget.RemoteViews;
import android.widget.Toast;

import java.util.List;

/**
 * Created by amir on 9/15/15.
 */
public class GcmMessageHandler extends IntentService{

    String type;
    String headsUpTitle;
    String headsUpContent;
    private Handler handler;

    private int code = 1;

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

        GoogleCloudMessaging gcm = GoogleCloudMessaging.getInstance(this);
        // The getMessageType() intent parameter must be the intent you received
        // in your BroadcastReceiver.
        String messageType = gcm.getMessageType(intent);

        type = extras.getString("type");
        headsUpTitle = extras.getString("name");
        headsUpContent = extras.getString("message");
        showToast();
//        Log.i("GCM", "Received : (" + messageType +")  " + extras.getString("title"));

        GcmBroadcastReceiver.completeWakefulIntent(intent);

    }

    public void showToast(){
        handler.post(new Runnable() {
            public void run() {
//                Toast.makeText(getApplicationContext(), mes , Toast.LENGTH_LONG).show();

                notificationHandler.sendEmptyMessage(0);
            }
        });

    }

    private final Handler notificationHandler = new Handler(){
        @Override
        public void handleMessage(Message msg)
        {

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


            ActivityManager activityManager = (ActivityManager) getSystemService( ACTIVITY_SERVICE );

            List<ActivityManager.RunningTaskInfo> taskList = activityManager.getRunningTasks(10);



            int NOTIFICATION_ID = 759;
            String ns = Context.NOTIFICATION_SERVICE;
            NotificationManager notificationManager = (NotificationManager) getSystemService(ns);

            Intent notificationIntent = null;
            PendingIntent pendingIntent = null;

//            if(taskList.size() == 0 || taskList.size() == 1) {

            notificationIntent = new Intent(getApplicationContext(), HamPayLoginActivity.class);
            notificationIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP |
                    Intent.FLAG_ACTIVITY_SINGLE_TOP |
                    Intent.FLAG_ACTIVITY_NEW_TASK);

            notificationIntent.putExtra(Constants.NOTIFICATION, true);

            pendingIntent = PendingIntent.getActivity(getApplicationContext(), 0,
                    notificationIntent, PendingIntent.FLAG_CANCEL_CURRENT);

//            }

            HeadsUpManager manage = HeadsUpManager.getInstant(getApplication());
            HeadsUp.Builder builder = new HeadsUp.Builder(getApplicationContext());

            if(taskList.size() == 0 || taskList.size() == 1) {

//                builder.setAutoCancel(true);
                builder.setContentTitle(headsUpTitle).setDefaults(
                        Notification.DEFAULT_LIGHTS
//                                |Notification.FLAG_AUTO_CANCEL
                                |Notification.DEFAULT_SOUND
                )
                        .setSmallIcon(R.drawable.tiny_notification)
                        .setAutoCancel(true)
                        .setContentIntent(pendingIntent)
                        .setFullScreenIntent(pendingIntent, false)
                        .setContentText(/*new PersianEnglishDigit(headsUpContent).E2P()*/"123");
            }else {
//                builder.setAutoCancel(true);
                builder.setContentTitle(headsUpTitle).setDefaults(
                        Notification.DEFAULT_LIGHTS
//                                |Notification.FLAG_AUTO_CANCEL
                                |Notification.DEFAULT_SOUND)
                        .setSmallIcon(R.drawable.tiny_notification)
                        .setAutoCancel(true)
//                        .setContentIntent(pendingIntent)
//                        .setFullScreenIntent(pendingIntent, false)
                        .setContentText(/*new PersianEnglishDigit(headsUpContent).E2P()*/"123");
            }

            HeadsUp headsUp = builder.buildHeadUp();
            headsUp.setSticky(true);
            manage.notify(code++, headsUp);

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
