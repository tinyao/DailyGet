package im.ycz.dailyget.utils;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import java.util.Calendar;

import im.ycz.dailyget.R;
import im.ycz.dailyget.model.TaskItem;

/**
 * Created by tinyao on 11/16/13.
 */
public class TimeUtils {

    public static  int getIntervelDays(long start_timemills, long end_timemills) {
        long timemills = end_timemills - start_timemills;
        int day = (int) (timemills / (24*3600*1000));
        return day;
    }

    public static void setNotiAlarm(Context con, String alarmStr, boolean startTomorrow, long task_id) {
//        String alarmStr = alarmText.getText().toString();
        if(alarmStr.equals("")) return;

        String[] time = alarmStr.split(":");
        Calendar cc = Calendar.getInstance();
        cc.set(Calendar.HOUR_OF_DAY, Integer.valueOf(time[0]));
        cc.set(Calendar.MINUTE, Integer.valueOf(time[1]));

        if(startTomorrow) {
            // start tomorrow
            cc.set(Calendar.DAY_OF_YEAR, cc.get(Calendar.DAY_OF_YEAR) + 1);
        }

        Intent intent = new Intent(con, AlarmReceiver.class);
        intent.setAction("ACTION_ALARM_USER");
        PendingIntent pendingIntent = PendingIntent.getBroadcast(con, (int)task_id, intent, 0);
        AlarmManager alarmManager = (AlarmManager)con.getSystemService("alarm");
        alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, cc.getTimeInMillis(),
                24 * 3600 * 1000, pendingIntent);
        Log.d("DEBUG", "Alarm has Set: " + cc.getTime());
    }

    public static void cancelAlarm(Context con, long task_id){
        Intent intent = new Intent(con, AlarmReceiver.class);
        intent.setAction("ACTION_ALARM_USER");
        PendingIntent pendingIntent = PendingIntent.getBroadcast(con, (int)task_id, intent, 0);
        AlarmManager alarmManager = (AlarmManager)con.getSystemService("alarm");
        alarmManager.cancel(pendingIntent);
    }

}
