package im.ycz.dailyget;

import android.app.Application;
import android.content.SharedPreferences;


/**
 * Created by tinyao on 11/16/13.
 */
public class MApplication extends Application{

    private static  SharedPreferences sp;

    @Override
    public void onCreate() {
        super.onCreate();
        sp = getSharedPreferences("config", 0);
    }

    public static SharedPreferences getSharedPrefs() {
        return sp;
    }
}
