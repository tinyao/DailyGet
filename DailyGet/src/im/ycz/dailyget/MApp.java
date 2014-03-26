package im.ycz.dailyget;

import im.ycz.dailyget.data.Task;

import java.util.ArrayList;

import android.app.Application;
import android.content.SharedPreferences;


/**
 * Created by tinyao on 11/16/13.
 */
public class MApp extends Application{

    private static  SharedPreferences sp;
    public static final boolean DEBUG = true;
    
    public static ArrayList<Task> AllTasks = null;

    @Override
    public void onCreate() {
        super.onCreate();
        sp = getSharedPreferences("config", 0);
    }

    public static SharedPreferences getSharedPrefs() {
        return sp;
    }
}
