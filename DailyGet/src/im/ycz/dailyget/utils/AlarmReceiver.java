package im.ycz.dailyget.utils;

import android.app.AlertDialog;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.RingtoneManager;
import android.net.Uri;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import java.io.IOException;

import im.ycz.dailyget.EmptyWindows;
import im.ycz.dailyget.MainActivity;
import im.ycz.dailyget.R;
import im.ycz.dailyget.data.Task;

/**
 * Created by tinyao on 11/16/13.
 */
public class AlarmReceiver extends BroadcastReceiver{

    Context context;
    NotificationManager mNotificationManager;

    @Override
    public void onReceive(Context context, Intent intent) {

        this.context = context;

        mNotificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        if (intent.getAction().equals("ACTION_ALARM_USER")) {
            Log.d("DEBUG", "receiver_get");
            Task task = (Task) intent.getSerializableExtra("task");
            if(task != null) {
            	showNotification(task);
                playAlarm(task);
            }
        }

        if (intent.getAction().equals("ACTION_CLEAR_GOT_IT")) {
            cancelNotification();
        }
    }

    private void playAlarm(Task taskItem) {
        Intent in = new Intent(context, EmptyWindows.class);
        in.putExtra("task", taskItem);
        in.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(in);
    }


    private static final int NOTI_TASK = 0;

    private void cancelNotification() {
        mNotificationManager.cancel(NOTI_TASK);
    }

    private void showNotification(Task task){

        PendingIntent contentIntent = PendingIntent.getActivity(context, 0,
                new Intent(context, MainActivity.class), PendingIntent.FLAG_UPDATE_CURRENT);
//
//        Intent discardIntent = new Intent(context, MainActivity.class);
//        discardIntent.putExtra("piAction", "discard");

//        PendingIntent piDiscard = PendingIntent.getActivity(context, 0,
//                new Intent("ACTION_CLEAR_GOT_IT"), PendingIntent.FLAG_CANCEL_CURRENT);

        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(context);
        mBuilder.setSmallIcon(R.drawable.ic_launcher);
        mBuilder.setContentTitle("DailyGet: Task Time");
        mBuilder.setContentText("It's time for '" + task.title + "'");

        mBuilder.setStyle(new NotificationCompat.BigTextStyle()
                .bigText("It's time for '" + task.title + "'" + ", blablabla"));

        mBuilder.setContentIntent(contentIntent);
        Notification notification = mBuilder.build();
        notification.flags |= Notification.FLAG_AUTO_CANCEL;
        mNotificationManager.notify(NOTI_TASK, notification);

//        int icon = R.drawable.ic_launcher;        // icon from resources
//        CharSequence tickerText = "DailyGet: " + "Time for You";              // ticker-text
//        long when = System.currentTimeMillis();         // notification time
//        CharSequence contentTitle = "DailyGet: " + "One-hour Reading";  // message title
//        CharSequence contentText = "It's the 6th day, keep going !";      // message text
//
//        // the next two lines initialize the Notification, using the configurations above
//        Notification notification = new Notification(icon, tickerText, when);
//        notification.setLatestEventInfo(context, contentTitle, contentText, null);
//        notification.flags = Notification.FLAG_AUTO_CANCEL;
//        //PendingIntent
//        PendingIntent contentIntent = PendingIntent.getActivity(
//                context,
//                R.string.app_name,
//                new Intent(context, MainActivity.class),
//                PendingIntent.FLAG_UPDATE_CURRENT);
//        notification.contentIntent = contentIntent;
//        NotificationManager mNotificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
//        mNotificationManager.notify(0, notification);
    }


}
