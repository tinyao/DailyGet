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
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;
import android.widget.ToggleButton;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;

import im.ycz.dailyget.R;
import im.ycz.dailyget.data.Prefs;
import im.ycz.dailyget.data.Task;
import im.ycz.dailyget.utils.TimeUtils;
import im.ycz.dailyget.view.FlipLayout;

public final class TaskFragment extends Fragment {

    private static final String KEY_CONTENT = "TaskFragment:Content";
    public static final String ACTION_TASK_CREATED = "action_task_created";
    public static final String ACTION_TASK_DONE_TODAY = "action_task_done_today";

    private int tab_id;
    private static final int TAB_ONE = 1;
    private static final int TAB_TWO = 2;

//    private View frontView, backView;
    private EditText titleEdt;
    private RadioGroup daysRadio, startRadio;
    private TextView alarmText;
    private View alarmView;
    private Switch alarmSwitch;
    private Button confirmBtn, cancelBtn;

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

    private View rootView;
    private FlipLayout flipLayout;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View contentView = inflater.inflate(R.layout.fragment_task, container, false);
        flipLayout = (FlipLayout) contentView.findViewById(R.id.flip_lay);
        flipLayout.setFlipView(R.layout.task_front, R.layout.task_back_create);
        initFlipView();
        
        return contentView;
    }

    private void initFlipView() {
		// TODO Auto-generated method stub
    	View frontAddView = flipLayout.findViewById(R.id.task_front_add_layout);
        View frontCheckView = flipLayout.findViewById(R.id.task_front_check_layout);
        View backView = flipLayout.getBackView();
        
        /* frontview: click to add */
        TextView clickAdd = (TextView) frontAddView.findViewById(R.id.click_to_add);
        
        /* frontview: check task */
        completedDaysView = (TextView)frontCheckView.findViewById(R.id.tv_completed_days);
        titleView = (TextView)frontCheckView.findViewById(R.id.tv_task_title);
        doneBtn = (Button)frontCheckView.findViewById(R.id.btn_task_done_today);
        doneBtn.setOnClickListener(listener);
		
		// 检查显示 "click to add" 还是 "check task"
        if (Prefs.goingTaskCount == 0 || (Prefs.goingTaskCount == 1 && tab_id == TAB_TWO) ) {
        	// checkview gone, clickview visible
        	frontAddView.setVisibility(View.VISIBLE);
        	initBackViews(backView);
        } else {
        	// checkview visible, clickview gone
        	frontAddView.setVisibility(View.GONE);
        	fillData();
        }
        
		clickAdd.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				flipLayout.flip();
			}
			
		});
	}
    
    private void initBackViews(View backView) {
    	/* back view */
        titleEdt = (EditText)backView.findViewById(R.id.task_create_title);
		daysRadio = (RadioGroup) backView.findViewById(R.id.radio_check);
		startRadio = (RadioGroup) backView.findViewById(R.id.radio_start);
		alarmText = (TextView) backView.findViewById(R.id.tv_alarm);
		alarmSwitch = (Switch) backView.findViewById(R.id.switcher_alarm);
		confirmBtn = (Button) backView.findViewById(R.id.btn_create_confirm);
		cancelBtn = (Button) backView.findViewById(R.id.btn_create_cancel);
		alarmView = backView.findViewById(R.id.alarm_lay);
		alarmView.setAlpha(0.5f);
		alarmView.setClickable(false);
		alarmView.setOnClickListener(listener);
		confirmBtn.setOnClickListener(listener);
		cancelBtn.setOnClickListener(listener);
		
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
                    mTask.getId();
                    
                    if(mTask == null) break;
                    
                    flipLayout.flip();
                    fillCheckViewData();
                    sendTaskBroadcast(ACTION_TASK_CREATED);
                    if(mTask.isAlarm) {
                    	TimeUtils.setNotiAlarm(TaskFragment.this.getActivity(), mTask);
                    }
                    break;
                case R.id.btn_create_cancel:
                	flipLayout.flip();
                	break;
                case R.id.btn_task_done_today:
                	// TODO try to perform an animation here
                    completedDaysView.setText( (mTask.currentDays + 1) + "" );
                    doneBtn.setText("Well Done! Today");
                    doneBtn.setClickable(false);
               
                    mTask.currentDays++;
                    mTask.save();
                    
                    sendTaskBroadcast(ACTION_TASK_DONE_TODAY);

                    if(mTask.currentDays == mTask.targetDays) {
                        performCompleteAllAction();	// push out the finish view
                    }
                    break;
                case R.id.alarm_lay:
                	showTimePickerDialog();
                	break;
            }
        }
    };
    
    private void showTimePickerDialog() {
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
                SimpleDateFormat ft = new SimpleDateFormat("HH:mm", Locale.getDefault());
                alarmText.setText(ft.format(cc.getTime()));
            }

        }, hour, minute, DateFormat.is24HourFormat(getActivity()));
        dialog.show();
    }

    protected void fillCheckViewData() {
    	flipLayout.getFrontView().findViewById(R.id.task_front_add_layout).setVisibility(View.GONE);
    	titleView.setText(mTask.title);
    	if(mTask.isWaitForTomorrow()){
    		// start tomorrow
    		doneBtn.setText("Check tomorrow");
    		doneBtn.setClickable(false);
    		doneBtn.setEnabled(false);
    	}
	}

//	private void setNotiAlarm() {
//        if(mTask.alarmAt.equals("")) return;
//
//        String[] time = mTask.alarmAt.split(":");
//        Calendar cc = Calendar.getInstance();
//        cc.set(Calendar.HOUR_OF_DAY, Integer.valueOf(time[0]));
//        cc.set(Calendar.MINUTE, Integer.valueOf(time[1]));
//
//        // start tomorrow
//        if(startRadio.getCheckedRadioButtonId() == R.id.radio_start_tomorrow) {
//            cc.set(Calendar.DAY_OF_YEAR, cc.get(Calendar.DAY_OF_YEAR) + 1);
//        }
//
//        Intent intent = new Intent(TaskFragment.this.getActivity(), AlarmReceiver.class);
//        intent.setAction("ACTION_ALARM_USER");
//        intent.putExtra("task", mTask);
//        PendingIntent pendingIntent = PendingIntent.getBroadcast(TaskFragment.this.getActivity(), 0, intent, 0);
//        AlarmManager alarmManager = (AlarmManager)TaskFragment.this.getActivity().getSystemService("alarm");
//        alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, cc.getTimeInMillis(),
//              24 * 3600 * 1000, pendingIntent);
//        Log.d(KEY_CONTENT, "Alarm has Set: " + cc.getTime());
//    }

	/**
	 * create a task from the form input
	 * @return
	 */
    private Task createATask() {
    	
        Task task = new Task();
        
        task.title = titleEdt.getText().toString();
        if(task.title.equals("")){
            Toast.makeText(this.getActivity(), R.string.msg_no_title, Toast.LENGTH_SHORT).show();
            return null;
        }

        if(daysRadio.getCheckedRadioButtonId() == R.id.days_21) {
            task.targetDays = 21;
        }else if(daysRadio.getCheckedRadioButtonId() == R.id.days_14){
            task.targetDays = 14;
        } else {
            task.targetDays = 7;
        }
        
        Calendar calendar = Calendar.getInstance();
        task.createTime = calendar.getTimeInMillis();
        if(startRadio.getCheckedRadioButtonId() == R.id.radio_start_tomorrow) {
            calendar.set(Calendar.DAY_OF_YEAR, 
            		calendar.get(Calendar.DAY_OF_YEAR) + 1);
            task.startTime = TimeUtils.getDateStartMills(calendar);
        }else {
        	task.startTime = TimeUtils.getDateStartMills(calendar);
        }

        task.isCompleted = false;
        task.currentDays = 0;
        task.alarmAt = alarmText.getText().toString();
        task.isAlarm = alarmSwitch.isChecked();
        task.resetTimes = 0;

        task.save();
        return task;
    }

    private TextView completedDaysView, titleView;
    private Button doneBtn;
    private Task mTask;

    /**
     * Fill the corresponding task data to the CheckLayout
     */
    private void fillData() {
        /* Obtain the ongoing tasks */
        ArrayList<Task> tasks = Task.getUncompleted();
        mTask = tasks.get(tab_id - 1);
        completedDaysView.setText("" + mTask.currentDays);
        titleView.setText(mTask.title);

        /* Calculate the days interval, check if have done today */
        if(mTask.isDoneToday()) {
        	doneBtn.setText("Well Done ! Today");
            doneBtn.setEnabled(false);
        }
        /* Check if task start at tomorrow */
        if(mTask.isWaitForTomorrow()){
            // start from tomorrow
            doneBtn.setText("Check tomorrow");
            doneBtn.setEnabled(false);
        }
    }
    
    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString(KEY_CONTENT, mContent);
    }

    private void sendTaskBroadcast(String ACTION_NAME) {
    	// update archive: add
        Intent taskIntent = new Intent(ACTION_NAME);
        if(ACTION_NAME.equals(ACTION_TASK_DONE_TODAY)) {
        	taskIntent.putExtra("task_id", (long) mTask.getId());
        }
        if(ACTION_NAME.equals(ACTION_TASK_CREATED)) {
        	taskIntent.putExtra("new_task", mTask);
        }
        
        TaskFragment.this.getActivity().sendBroadcast(taskIntent);
    }
    
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
    
}