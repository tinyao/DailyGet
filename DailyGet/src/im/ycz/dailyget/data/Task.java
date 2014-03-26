package im.ycz.dailyget.data;


import im.ycz.dailyget.utils.TimeUtils;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import android.content.Context;
import android.util.Log;

import com.orm.SugarRecord;
import com.orm.dsl.Ignore;

/**
 * Created by tinyao on 11/15/13.
 */
public class Task extends SugarRecord<Task> implements Serializable{

    @Ignore
	private static final long serialVersionUID = -7060210544600464481L;

    public String title;
    public int targetDays, currentDays;
    
    public long createTime;
    public long startTime;
    
    public boolean isCompleted;
    
    public String alarmAt;
    public boolean isAlarm;
    
    public int resetTimes; // reset frequency
    
    public Task() {
		super();
	}
    
    public Task(Context ctx, String title, int targetDays,
    		long createTime, long startTime, String alarmAt, boolean isAlarm){
        super();
        this.title = title;
        this.targetDays = targetDays;
        this.currentDays = 0;
        this.createTime = createTime;
        this.startTime = startTime;
        this.isCompleted = false;
        this.alarmAt = alarmAt;
        this.isAlarm = isAlarm;
        this.resetTimes = 0;
    }
    
    public static ArrayList<Task> getUncompleted() {
    	return (ArrayList<Task>) Task.find(Task.class, "current_days <> target_days");
    }
    
    public static int getUncompletedCounts() {
    	ArrayList<Task> unlist = getUncompleted();
    	if(unlist == null) {
    		return 0;
    	} else {
    		return unlist.size();
    	}
    }
    
    public boolean isDoneToday() {
    	int intervalDays = TimeUtils.getIntervelDays(Long.valueOf(startTime),
                Calendar.getInstance().getTimeInMillis());
        Log.d("DEBUG", "Interval days: " + intervalDays);
    	return intervalDays + 1 <= currentDays;
    }
    
    public boolean isWaitForTomorrow() {
    	return Long.valueOf(startTime) > Calendar.getInstance().getTimeInMillis();
    }

}
