package im.ycz.dailyget;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;
import im.ycz.dailyget.R;
import im.ycz.dailyget.database.Prefs;
import im.ycz.dailyget.model.TaskItem;
import im.ycz.dailyget.utils.AlarmReceiver;
import im.ycz.dailyget.utils.TimeUtils;
import im.ycz.dailyget.view.FlipView;

public final class TaskFragment extends Fragment {

    private static final String KEY_CONTENT = "TaskFragment:Content";
    public static final String ACTION_TASK_CREATED = "action_task_created";
    public static final String ACTION_TASK_DONE_TODAY = "action_task_done_today";

    private int tab_id;
    private static final int TAB_ONE = 1;
    private static final int TAB_TWO = 2;

    private View frontView, backView;
    private EditText titleEdt;
    private RadioGroup daysRadio, startRadio;
    private TextView alarmText;
    private View alarmView;
    private Switch alarmSwitch;
    private Button confirmBtn;

    public TaskFragment() {}
    
    public TaskFragment(int id) {
        this.tab_id = id;
    }

    private String mContent = "???";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if ((savedInstanceState != null) && savedInstanceState.containsKey(KEY_CONTENT)) {
            mContent = savedInstanceState.getString(KEY_CONTENT);
        }

    }

    private View rootView, flipLayout;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View contentView = inflater.inflate(R.layout.fragment_task, container, false);
        rootView = contentView;
        flipLayout = contentView.findViewById(R.id.flipview);

        if (Prefs.goingTaskCount == 0  ||
                ( Prefs.goingTaskCount == 1 && tab_id == 2) ) {
            initFlipView(flipLayout);
        } else {
            initDaysCountView(contentView, false);
        }

        return contentView;
    }

    FlipView flipView;

    private void initFlipView(View flipLayout) {
        flipView = new FlipView(this.getActivity(),
                flipLayout, R.id.front, R.id.back);
        frontView = flipView.getFrontView();
        backView = flipView.getBackView();
        titleEdt = (EditText)backView.findViewById(R.id.edt_title);
        daysRadio = (RadioGroup)backView.findViewById(R.id.radio_check);
        startRadio = (RadioGroup) backView.findViewById(R.id.radio_start);
        alarmText = (TextView)backView.findViewById(R.id.tv_alarm);
        alarmSwitch = (Switch)backView.findViewById(R.id.switcher_alarm);
        confirmBtn = (Button)backView.findViewById(R.id.btn_create_confirm);
        alarmView = backView.findViewById(R.id.alarm_lay);
        alarmView.setAlpha(0.5f);
        alarmView.setClickable(false);
        alarmView.setOnClickListener(listener);
        confirmBtn.setOnClickListener(listener);

        alarmSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener(){

            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean value) {
                if(value){
                    alarmView.setAlpha(1.0f);
                    alarmView.setClickable(true);
                } else {
                    alarmView.setAlpha(0.5f);
                    alarmView.setClickable(false);
                }

            }

        });
    }

    private View.OnClickListener listener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            switch (view.getId()){
                case R.id.btn_create_confirm:
                    mTask = createATask();
                    if(mTask == null) break;
                    initDaysCountView(frontView, true);
                    flipView.flip2Front();
                    flipView.setEnable(false);

                    // update archive: add
                    Intent ii = new Intent(ACTION_TASK_CREATED);
                    ii.putExtra("new_task", mTask);
                    TaskFragment.this.getActivity().sendBroadcast(ii);
                    break;
                case R.id.btn_task_done_today:
                    completedDaysView.setText( (mTask.currentDays + 1) + "" );
                    // increase task days the database
                    
                    mTask.currentDays++;
                    mTask.save();
                    doneBtn.setText("Well Done! Today");
                    doneBtn.setClickable(false);

                    // update archive: add
                    Intent updateDaysIntent = new Intent(ACTION_TASK_DONE_TODAY);
                    updateDaysIntent.putExtra("task_id", mTask.getId());
                    TaskFragment.this.getActivity().sendBroadcast(updateDaysIntent);

                    if(mTask.currentDays == mTask.targetDays) {
                        // change to finish view
                        performCompleteAllAction();
                    }
                    break;
                case R.id.alarm_lay:
                    final Calendar c = Calendar.getInstance();
                    int hour = c.get(Calendar.HOUR_OF_DAY);
                    int minute = c.get(Calendar.MINUTE);

                    // Create a new instance of TimePickerDialog and return it
                    TimePickerDialog dialog = new TimePickerDialog(TaskFragment.this.getActivity(), new TimePickerDialog.OnTimeSetListener(){

                        @Override
                        public void onTimeSet(TimePicker timePicker, int hourOfDay, int minute) {
                            Calendar cc = Calendar.getInstance();
                            cc.set(Calendar.HOUR_OF_DAY, hourOfDay);
                            cc.set(Calendar.MINUTE, minute);
                            SimpleDateFormat ft = new SimpleDateFormat("HH:mm");
                            alarmText.setText(ft.format(cc.getTime()));
                        }

                    }, hour, minute, DateFormat.is24HourFormat(getActivity()));
                    dialog.show();
                    break;
            }
        }
    };

    private void performCompleteAllAction() {
        Animation zoomOut = AnimationUtils.loadAnimation(TaskFragment.this.getActivity(), R.anim.zoom_out);
        Animation zoomIn = AnimationUtils.loadAnimation(TaskFragment.this.getActivity(), R.anim.zoom_in);
        final View finishedView = rootView.findViewById(R.id.task_completed_lay);
        ((TextView)finishedView.findViewById(R.id.completed_task_title)).setText(mTask.title);
        ((TextView)finishedView.findViewById(R.id.completed_task_days)).setText(mTask.targetDays + "");

        zoomOut.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                flipLayout.setVisibility(View.GONE);
                finishedView.setVisibility(View.VISIBLE);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });


        finishedView.clearAnimation();
        finishedView.startAnimation(zoomIn);
        flipLayout.startAnimation(zoomOut);
    }

    private void setNotiAlarm() {
        String alarmStr = alarmText.getText().toString();
        if(alarmStr.equals("")) return;

        String[] time = alarmStr.split(":");
        Calendar cc = Calendar.getInstance();
        cc.set(Calendar.HOUR_OF_DAY, Integer.valueOf(time[0]));
        cc.set(Calendar.MINUTE, Integer.valueOf(time[1]));

        if(startRadio.getCheckedRadioButtonId() == R.id.radio_start_tomorrow) {
            // start tomorrow
            cc.set(Calendar.DAY_OF_YEAR, cc.get(Calendar.DAY_OF_YEAR) + 1);
        }

        Intent intent = new Intent(TaskFragment.this.getActivity(), AlarmReceiver.class);
        intent.setAction("ACTION_ALARM_USER");
        intent.putExtra("task", mTask);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(TaskFragment.this.getActivity(), 0, intent, 0);
        AlarmManager alarmManager = (AlarmManager)TaskFragment.this.getActivity().getSystemService("alarm");
        alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, cc.getTimeInMillis(),
              24 * 3600 * 1000, pendingIntent);
        Log.d(KEY_CONTENT, "Alarm has Set: " + cc.getTime());
    }

    private TaskItem createATask() {
    	
        TaskItem task = new TaskItem();
        task.title = titleEdt.getText().toString();

        if(task.title.equals("")){
            Toast.makeText(this.getActivity(), "title empty", Toast.LENGTH_SHORT).show();
            return null;
        }

        if(daysRadio.getCheckedRadioButtonId() == R.id.days_21) {
            task.targetDays = 21;
        }else if(daysRadio.getCheckedRadioButtonId() == R.id.days_14){
            task.targetDays = 14;
        } else {
            task.targetDays = 7;
        }
        task.currentDays = 0;

        SimpleDateFormat ft = new SimpleDateFormat("yyyy/MM/dd", Locale.getDefault());

        Calendar calendar = Calendar.getInstance();
        task.createTime = calendar.getTimeInMillis();

        int dayOfYear = calendar.get(Calendar.DAY_OF_YEAR);
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);

        if(startRadio.getCheckedRadioButtonId() == R.id.radio_start_tomorrow) {
            calendar.set(Calendar.DAY_OF_YEAR, dayOfYear + 1);
        }

        Log.d("DEBUG", "Start Day" + ft.format(calendar.getTime()));

        task.startTime = calendar.getTimeInMillis();
        task.isCompleted = false;
        task.currentDays = 0;
        task.alarmAt = alarmText.getText().toString();
        task.isAlarm = alarmSwitch.isChecked();
        task.resetTimes = 0;

        task.save();
        
        mTask = task;

        if(alarmSwitch.isChecked()) 
        	setNotiAlarm();

        return task;
    }

    private TextView completedDaysView, titleView;
    private Button doneBtn;
    private TaskItem mTask;

    /* init layout when launch or taskCreated */
    private void initDaysCountView(View contentView, boolean isFlip) {
        completedDaysView = (TextView)contentView.findViewById(R.id.tv_completed_days);
        titleView = (TextView)contentView.findViewById(R.id.tv_task_title);
        doneBtn = (Button)contentView.findViewById(R.id.btn_task_done_today);
        doneBtn.setOnClickListener(listener);

        if(isFlip){
            frontView.findViewById(R.id.click_to_add).setVisibility(View.GONE);
            titleView.setText(mTask.title);
            if(Long.valueOf(mTask.startTime) > Calendar.getInstance().getTimeInMillis()){
                // start tomorrow
                doneBtn.setText("Check tomorrow");
                doneBtn.setClickable(false);
                doneBtn.setEnabled(false);
            }
        } else {
            contentView.findViewById(R.id.click_to_add).setVisibility(View.GONE);
            fillData();
        }
    }

    private void fillData() {
        /* Obtain the going tasks */
        ArrayList<TaskItem> tasks = (ArrayList<TaskItem>) TaskItem.find(TaskItem.class, "current_days = target_days");
        		//TaskDBHelper.getInstance(this.getActivity()).getUnCompetedTasks();
        
        if(tab_id == 1) {
            mTask = tasks.get(0);
        } else {
            mTask = tasks.get(1);
        }
        completedDaysView.setText("" + mTask.currentDays);
        titleView.setText(mTask.title);

        /* Calculate the days interval, check if have done today */
        int days = TimeUtils.getIntervelDays(Long.valueOf(mTask.startTime),
                Calendar.getInstance().getTimeInMillis());
        Log.d("DEBUG", "interval days: " + days);
        if(days+1 <= mTask.currentDays) {
            doneBtn.setText("Well Done ! Today");
            doneBtn.setClickable(false);
        }

        if(Long.valueOf(mTask.startTime) > Calendar.getInstance().getTimeInMillis()){
            // start tomorrow
            doneBtn.setText("Check tomorrow");
            doneBtn.setClickable(false);
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString(KEY_CONTENT, mContent);
    }

}