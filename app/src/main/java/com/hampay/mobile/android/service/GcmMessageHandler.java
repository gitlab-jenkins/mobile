package com.hampay.mobile.android.service;

import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.hampay.mobile.android.R;
import com.hampay.mobile.android.activity.HamPayLoginActivity;
import com.hampay.mobile.android.receiver.GcmBroadcastReceiver;
import com.hampay.mobile.android.util.Constants;

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

/**
 * Created by amir on 9/15/15.
 */
public class GcmMessageHandler extends IntentService{

    String mes;
    private Handler handler;
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

        mes = extras.getString("price");
        showToast();
        Log.i("GCM", "Received : (" + messageType +")  " + extras.getString("title"));

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
            int NOTIFICATION_ID = 759;
            String ns = Context.NOTIFICATION_SERVICE;
            NotificationManager mNotificationManager = (NotificationManager) getSystemService(ns);

            int icon = R.drawable.ball;
            long when = System.currentTimeMillis();
            Notification notification = new Notification(icon, getString(R.string.app_name), when);
            notification.flags = Notification.FLAG_AUTO_CANCEL;
//            notification.flags = Notification.DEFAULT_LIGHTS | Notification.FLAG_AUTO_CANCEL | Notification.FLAG_ONLY_ALERT_ONCE;

            RemoteViews contentView = new RemoteViews(getPackageName(), R.layout.server_notification);
            contentView.setImageViewResource(R.id.notification_image, R.mipmap.ic_launcher);
            contentView.setTextViewText(R.id.service_type_value, " " + "دریافت");
            contentView.setTextViewText(R.id.service_message_value, " " + "هومن، پولی که طلب داشتی واریز کردم، مخلص.");
            notification.contentView = contentView;

            Intent notificationIntent = new Intent(getApplicationContext(), HamPayLoginActivity.class);

            notificationIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP |
                    Intent.FLAG_ACTIVITY_SINGLE_TOP |
                    Intent.FLAG_ACTIVITY_NEW_TASK);
            notificationIntent.putExtra(Constants.NOTIFICATION, true);
//            PendingIntent contentIntent = PendingIntent.getActivity(getApplicationContext(), 0, notificationIntent, 0);
            PendingIntent contentIntent = PendingIntent.getActivity(getApplicationContext(), 0,
                    notificationIntent, 0);
            notification.contentIntent = contentIntent;

            //notification.flags |= Notification.FLAG_NO_CLEAR; //Do not clear the notification
            notification.defaults |= Notification.DEFAULT_LIGHTS; // LED
            notification.defaults |= Notification.DEFAULT_VIBRATE; //Vibration
            notification.defaults |= Notification.DEFAULT_SOUND; // Sound

            mNotificationManager.notify(NOTIFICATION_ID, notification);

        }
    };

}
