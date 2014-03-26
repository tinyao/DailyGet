package im.ycz.dailyget.utils;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import im.ycz.dailyget.R;
import im.ycz.dailyget.TaskFragment;
import im.ycz.dailyget.data.Task;

/**
 * Created by tinyao on 11/16/13.
 */
public class TimeUtils {

    public static  int getIntervelDays(long start_timemills, long end_timemills) {
        long timemills = end_timemills - start_timemills;
        int day = (int) (timemills / (24*3600*1000));
        return day;
    }

    // set alarm and put "task" into intent
    public static void setNotiAlarm(Context con, Task task) {
    	boolean isTomorrow = Long.valueOf(task.startTime) > Calendar.getInstance().getTimeInMillis();
    	if(task.alarmAt.equals("")) return;

        String[] time = task.alarmAt.split(":");
        Calendar cc = Calendar.getInstance();
        cc.set(Calendar.HOUR_OF_DAY, Integer.valueOf(time[0]));
        cc.set(Calendar.MINUTE, Integer.valueOf(time[1]));
        cc.set(Calendar.SECOND, 0);

        if(isTomorrow) {
            // start tomorrow
            cc.set(Calendar.DAY_OF_YEAR, cc.get(Calendar.DAY_OF_YEAR) + 1);
        }

        Intent intent = new Intent(con, AlarmReceiver.class);
        intent.setAction("ACTION_ALARM_USER");
        intent.putExtra("task", task);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(con, (int) (long) task.getId(), intent, 0);
        AlarmManager alarmManager = (AlarmManager) con.getSystemService("alarm");
        alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, cc.getTimeInMillis(),
              24 * 3600 * 1000, pendingIntent);
        Logg.p("Alarm has Set for task " + task.getId() + " -- " + cc.getTime());
    }
    
//    public static void updateNotiAlarm(Context con, String alarmStr, boolean startTomorrow, long task_id) {
//        if(alarmStr.equals("")) return;
//
//        String[] time = alarmStr.split(":");
//        Calendar cc = Calendar.getInstance();
//        cc.set(Calendar.HOUR_OF_DAY, Integer.valueOf(time[0]));
//        cc.set(Calendar.MINUTE, Integer.valueOf(time[1]));
//        cc.set(Calendar.SECOND, 0);
//
//        if(startTomorrow) {
//            // start tomorrow
//            cc.set(Calendar.DAY_OF_YEAR, cc.get(Calendar.DAY_OF_YEAR) + 1);
//        }
//
//        Intent intent = new Intent(con, AlarmReceiver.class);
//        intent.setAction("ACTION_ALARM_USER");
//        PendingIntent pendingIntent = PendingIntent.getBroadcast(con, (int)task_id, intent, 0);
//        AlarmManager alarmManager = (AlarmManager)con.getSystemService("alarm");
//        alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, cc.getTimeInMillis(),
//                24 * 3600 * 1000, pendingIntent);
//        Logg.p("Alarm has update for task " + task_id + " -- " + cc.getTime());
//    }

    public static void cancelAlarm(Context con, long task_id){
        Intent intent = new Intent(con, AlarmReceiver.class);
        intent.setAction("ACTION_ALARM_USER");
        PendingIntent pendingIntent = PendingIntent.getBroadcast(con, (int)task_id, intent, 0);
        AlarmManager alarmManager = (AlarmManager)con.getSystemService("alarm");
        alarmManager.cancel(pendingIntent);
        Logg.p("Alarm canceled for task " + task_id);
    }
    
    public static long getDateStartMills(Calendar cal) {
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);
    	return cal.getTimeInMillis();
    }

}
