package im.ycz.dailyget;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import com.viewpagerindicator.IconPagerAdapter;
import com.viewpagerindicator.TabIconPageIndicator;
import android.view.Menu;

import java.util.Calendar;

import im.ycz.dailyget.R;
import im.ycz.dailyget.database.Prefs;
import im.ycz.dailyget.database.TaskDBHelper;
import im.ycz.dailyget.model.TaskItem;

public class MainActivity extends FragmentActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        FragmentPagerAdapter adapter = new DailyFragmentPagerAdapter(getSupportFragmentManager());

        ViewPager pager = (ViewPager)findViewById(R.id.pager);
        pager.setOffscreenPageLimit(2);
        pager.setAdapter(adapter);

        TabIconPageIndicator indicator = (TabIconPageIndicator)findViewById(R.id.indicator);
        indicator.setViewPager(pager);
        
        pager.setCurrentItem(1);

        Prefs.goingTaskCount = TaskItem.getUncompletedCounts();
        
//      generateData();

    }

    private void generateData() {

        TaskItem item0 = new TaskItem();
        item0.title = "Take Exercise";
        item0.createTime = (Calendar.getInstance().getTimeInMillis() - 14*24*3600*1000);
        item0.isCompleted = true;
        item0.startTime = (Calendar.getInstance().getTimeInMillis() - 14*24*3600*1000 - 30000);
        item0.isAlarm = false;
        item0.alarmAt = "21:00";
        item0.currentDays = 14;
        item0.targetDays = 14;
        item0.save();

        TaskItem item = new TaskItem();
        item.title = "One-hour Reading";
        item.createTime = (Calendar.getInstance().getTimeInMillis() - 20*24*3600*1000);
        item.isCompleted = false;
        item.startTime = (Calendar.getInstance().getTimeInMillis() - 20*24*3600*1000 - 30000);
        item.isAlarm = false;
        item.alarmAt = "22:30";
        item.currentDays = 20;
        item.targetDays = 21;
        item.save();
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

}
