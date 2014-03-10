package im.ycz.dailyget.model;


import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import android.content.Context;

import com.orm.SugarRecord;
import com.orm.dsl.Ignore;

/**
 * Created by tinyao on 11/15/13.
 */
public class TaskItem extends SugarRecord<TaskItem> implements Serializable{

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
    
    public TaskItem() {
		super();
	}
    
    public TaskItem(Context ctx, String title, int targetDays,
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
    
    public static ArrayList<TaskItem> getUncompleted() {
    	return (ArrayList<TaskItem>) TaskItem.find(TaskItem.class, "current_days = target_days");
    }
    
    public static int getUncompletedCounts() {
    	ArrayList<TaskItem> unlist = getUncompleted();
    	if(unlist == null) {
    		return 0;
    	} else {
    		return unlist.size();
    	}
    }

}
