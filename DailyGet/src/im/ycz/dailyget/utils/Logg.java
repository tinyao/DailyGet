package im.ycz.dailyget.utils;

import android.util.Log;
import im.ycz.dailyget.MApp;

public class Logg {
	
	public static void p(String msg){
		if(MApp.DEBUG){
			Log.d("DEBUG", msg);
		}
	}
	
	public static void p(String tag, String msg){
		if(MApp.DEBUG){
			Log.d(tag, msg);
		}
	}

}
