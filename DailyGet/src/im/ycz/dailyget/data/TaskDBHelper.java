package im.ycz.dailyget.data;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;
import android.provider.BaseColumns;

import java.util.ArrayList;


/**
 * Created by tinyao on 11/15/13.
 */
public class TaskDBHelper extends SQLiteOpenHelper {

	public TaskDBHelper(Context context, String name, CursorFactory factory,
			int version) {
		super(context, name, factory, version);
		// TODO Auto-generated constructor stub
	}

	@Override
	public void onCreate(SQLiteDatabase arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onUpgrade(SQLiteDatabase arg0, int arg1, int arg2) {
		// TODO Auto-generated method stub
		
	}

	/**
	
    // If you change the database schema, you must increment the database version.
    public static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "Tasks.db";

    public static abstract class TaskEntry implements BaseColumns {
        public static final String TABLE_NAME = "task";
        public static final String COLUMN_TITLE = "title";
        public static final String COLUMN_CREATE = "create_at";
        public static final String COLUMN_START = "start_at";
        public static final String COLUMN_TARGET_DAYS = "target_days";
        public static final String COLUMN_CURRENT_DAYS = "current_days";
        public static final String COLUMN_COMPELETED = "is_completed";
        public static final String COLUMN_ALARM_TIME = "alarm_time";
        public static final String COLUMN_ALARM_ON = "alarm_on";
        public static final String COLUMN_RESET_TIMES = "reset_times";
    }

    private static final String INTEGER_TYPE = " INTEGER";
    private static final String TEXT_TYPE = " TEXT";
    private static final String COMMA_SEP = ",";
    private static final String SQL_CREATE_ENTRIES =
            "CREATE TABLE " + TaskEntry.TABLE_NAME + " (" +
                    TaskEntry._ID + " INTEGER PRIMARY KEY," +
                    TaskEntry.COLUMN_TITLE + TEXT_TYPE + COMMA_SEP +
                    TaskEntry.COLUMN_CREATE + TEXT_TYPE + COMMA_SEP +
                    TaskEntry.COLUMN_START + TEXT_TYPE + COMMA_SEP +
                    TaskEntry.COLUMN_TARGET_DAYS + INTEGER_TYPE + COMMA_SEP +
                    TaskEntry.COLUMN_CURRENT_DAYS + INTEGER_TYPE + COMMA_SEP +
                    TaskEntry.COLUMN_COMPELETED + INTEGER_TYPE + COMMA_SEP +
                    TaskEntry.COLUMN_ALARM_TIME + TEXT_TYPE + COMMA_SEP +
                    TaskEntry.COLUMN_ALARM_ON + INTEGER_TYPE + COMMA_SEP +
                    TaskEntry.COLUMN_RESET_TIMES + INTEGER_TYPE +
            " )";

    private static final String SQL_DELETE_ENTRIES =
            "DROP TABLE IF EXISTS " + TaskEntry.TABLE_NAME;

    private Context context;
    private static TaskDBHelper mDbHelper = null;
    private static SQLiteDatabase db;

    public static TaskDBHelper getInstance(Context con) {
        if(mDbHelper == null){
            mDbHelper = new TaskDBHelper(con);
            db = mDbHelper.getWritableDatabase();
            return mDbHelper;
        }else{
            return mDbHelper;
        }
    }

    public TaskDBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        this.context = context;
    }

    public void onCreate(SQLiteDatabase db) {
        db.execSQL(SQL_CREATE_ENTRIES);
    }

    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // This database is only a cache for online data, so its upgrade policy is
        // to simply to discard the data and start over
        db.execSQL(SQL_DELETE_ENTRIES);
        onCreate(db);
    }
    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        onUpgrade(db, oldVersion, newVersion);
    }


    public long insertItem(TaskItem item) {

        // Create a new map of values, where column names are the keys
        ContentValues values = new ContentValues();
        values.put(TaskEntry.COLUMN_TITLE, item.title);
        values.put(TaskEntry.COLUMN_CREATE, item.createTime);
        values.put(TaskEntry.COLUMN_START, item.startTime);
        values.put(TaskEntry.COLUMN_TARGET_DAYS, item.targetDays);
        values.put(TaskEntry.COLUMN_CURRENT_DAYS, item.currentDays);
        values.put(TaskEntry.COLUMN_COMPELETED, item.isCompleted ? 1 : 0);
        values.put(TaskEntry.COLUMN_ALARM_TIME, item.alarmAt);
        values.put(TaskEntry.COLUMN_ALARM_ON, item.alarmOn ? 1 : 0);
        values.put(TaskEntry.COLUMN_RESET_TIMES, item.resetTimes);

        // Insert the new row, returning the primary key value of the new row
        long newRowId;
        newRowId = db.insert(
                TaskEntry.TABLE_NAME,
                TaskEntry.COLUMN_TITLE,
                values);
        return newRowId;

    }

    public void increaseDoneDays(TaskItem item) {
		String strFilter = TaskEntry._ID + "=" + item.id;
		ContentValues args = new ContentValues();
        if(item.targetDays == item.currentDays + 1){
            args.put(TaskEntry.COLUMN_COMPELETED, 1);
        }
		args.put(TaskEntry.COLUMN_CURRENT_DAYS, item.currentDays+1);
		db.update(TaskEntry.TABLE_NAME, args, strFilter, null);
    }

    public void updateAlarm(TaskItem item){
        String strFilter = TaskEntry._ID + "=" + item.id;
        ContentValues args = new ContentValues();
        args.put(TaskEntry.COLUMN_ALARM_ON, item.alarmOn ? 1 : 0);
        args.put(TaskEntry.COLUMN_ALARM_TIME, item.alarmAt);
        db.update(TaskEntry.TABLE_NAME, args, strFilter, null);
    }

    public Cursor getUnCompeleted() {
        String[] parms = { "0" };
        Cursor cursor = db.query(TaskEntry.TABLE_NAME, null,
                TaskEntry.COLUMN_COMPELETED + "=?",
                parms, null, null, "_id ASC");
        return cursor;
    }

    public ArrayList<TaskItem> getAllTasks() {
        Cursor cursor = db.query(TaskEntry.TABLE_NAME, null,
                null, null, null, null, "_id DESC");
        ArrayList<TaskItem> tasks = new ArrayList<TaskItem>();
        if(cursor == null || cursor.getCount() == 0) return tasks;
        cursor.moveToFirst();
        for(int i=0; i<cursor.getCount(); i++){
            tasks.add(buildTaskItem(cursor));
            cursor.moveToNext();
        }
        return tasks;
    }

    public ArrayList<TaskItem> getUnCompetedTasks() {
        Cursor cc = getUnCompeleted();
        if(cc==null || cc.getCount()==0) return null;
        cc.moveToFirst();
        ArrayList<TaskItem> tasks = new ArrayList<TaskItem>();
        for(int i=0; i < cc.getCount(); i++){
            tasks.add(buildTaskItem(cc));
            cc.moveToNext();
        }
        return tasks;
    }

    private TaskItem buildTaskItem(Cursor cc){
        TaskItem item = new TaskItem();
        item.id = cc.getInt(cc.getColumnIndexOrThrow(TaskEntry._ID));
        item.title = cc.getString(cc.getColumnIndexOrThrow(TaskEntry.COLUMN_TITLE));
        item.createTime = cc.getString(cc.getColumnIndexOrThrow(TaskEntry.COLUMN_CREATE));
        item.startTime = cc.getString(cc.getColumnIndexOrThrow(TaskEntry.COLUMN_START));
        item.targetDays = cc.getInt(cc.getColumnIndexOrThrow(TaskEntry.COLUMN_TARGET_DAYS));
        item.currentDays = cc.getInt(cc.getColumnIndexOrThrow(TaskEntry.COLUMN_CURRENT_DAYS));
        item.isCompleted = cc.getInt(cc.getColumnIndexOrThrow(TaskEntry.COLUMN_COMPELETED)) == 1;
        item.alarmAt = cc.getString(cc.getColumnIndexOrThrow(TaskEntry.COLUMN_ALARM_TIME));
        item.alarmOn = cc.getInt(cc.getColumnIndexOrThrow(TaskEntry.COLUMN_ALARM_ON)) == 1;
        item.resetTimes = cc.getInt(cc.getColumnIndexOrThrow(TaskEntry.COLUMN_RESET_TIMES));
        return item;
    }

    public int getUnCompeletedCount() {
        Cursor c = getUnCompeleted();
        if (c!=null) {
            return c.getCount();
        }
        return 0;
    }
    
    **/
}
