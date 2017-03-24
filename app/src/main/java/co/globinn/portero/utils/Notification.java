package co.globinn.portero.utils;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.media.RingtoneManager;
import android.service.notification.StatusBarNotification;
import android.support.v4.app.NotificationCompat;

import co.globinn.portero.R;

/**
 * Created by JulioC on 3/17/17.
 */



/**
 * Created by neifergarcia on 29/11/16.
 */

public class Notification {

    public final static int ID_NOTIFICATION_SOS = 1000;
    public final static int ID_NOTIFICATION_REQUEST = 1001;
    public final static int ID_NOTIFICATION_SERVICE = 1601;
    public final static int ID_NOTIFICATION_GLOBAL = 1602;
    private int REQUEST_CODE = 1001;
    private Context mContext;

    private NotificationManager notificationManager;
    private NotificationCompat.Builder notificationbuilder;

    public Notification(Context context) {
        mContext = context;

        notificationManager = (NotificationManager) mContext.getSystemService(Context.NOTIFICATION_SERVICE);
        notificationbuilder = new NotificationCompat.Builder(mContext);

        notificationbuilder
                .setAutoCancel(true)
                .setLights(Color.BLUE, 1, 0)
                .setVibrate(new long[]{1000, 500, 1000})
                .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))
                .setContentTitle(mContext.getString(R.string.app_name));

        setIntent(new Intent());
    }

    public void setAutoCancel(boolean state){
        notificationbuilder.setAutoCancel(state);
    }

    public void setSmallIcon(int drawable) {
        notificationbuilder.setSmallIcon(drawable);
    }

    public void withSound(boolean withSound){
        if(!withSound)
            notificationbuilder.setSound(null);
        else
            notificationbuilder.setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION));
    }

    public void setPriorityMin() {
        notificationbuilder.setPriority(NotificationCompat.PRIORITY_MIN);
    }

    public void setPriorityMax() {
        notificationbuilder.setPriority(NotificationCompat.PRIORITY_MAX);
    }


    public void setTicker(String ticker) {
        notificationbuilder.setTicker(ticker);
    }

    public void setTitle(String title) {
        notificationbuilder.setContentTitle(title);
    }

    public void setMessage(String message) {
        notificationbuilder.setContentText(message);
    }

    public void setMessage(String message, String submessage) {
        notificationbuilder.setContentText(message);
        notificationbuilder.setSubText(submessage);
    }

    public void setIntent(Intent intent) {
        PendingIntent pendingIntent = PendingIntent.getActivity(mContext, REQUEST_CODE, intent,
                PendingIntent.FLAG_UPDATE_CURRENT);

        notificationbuilder.setDeleteIntent(pendingIntent);
        notificationbuilder.setContentIntent(pendingIntent);
    }

    public android.app.Notification getNotificationBuilder(){
        return notificationbuilder.build();
    }

    public void show(int id) {
        notificationManager.notify(id, notificationbuilder.build());
    }

    public boolean isActive(){
        //notificationManager.

        return true;
    }


    public void dismiss(int id) {
        notificationManager.cancel(id);
    }

}