package im.ycz.dailyget.adapter;

import android.app.TimePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.TimePicker;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

import im.ycz.dailyget.R;
import im.ycz.dailyget.database.TaskDBHelper;
import im.ycz.dailyget.model.TaskItem;
import im.ycz.dailyget.utils.TimeUtils;

/**
 * Created by tinyao on 11/17/13.
 */
public class TaskListAdapter extends BaseAdapter {

    private LayoutInflater mInflater;
    private ArrayList<TaskItem> tasks;
    private Context context;
//    private ShelfCoverDownloader coverLoader;

    public TaskListAdapter(Context context,
                               ArrayList<TaskItem> list) {
        mInflater = LayoutInflater.from(context);
        this.tasks = list;
        this.context = context;
    }

    @Override
    public int getCount() {
        return tasks.size();
    }

    @Override
    public Object getItem(int i) {
        return tasks.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final ViewHolder holder;
        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.list_item,
                    parent, false);
            holder = new ViewHolder();
            holder.titleTv = (TextView) convertView
                    .findViewById(R.id.item_tv_title);
            holder.daysTv = (TextView) convertView
                    .findViewById(R.id.item_tv_days);
            holder.progressRatingBar = (RatingBar) convertView.findViewById(R.id.item_progress_bar);
            holder.alarmText = (TextView) convertView.findViewById(R.id.item_tv_alarm);
            holder.alarmView = convertView.findViewById(R.id.item_alarm_lay);
            holder.alarmSwitch = (Switch) convertView.findViewById(R.id.item_switcher_alarm);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        final TaskItem task = tasks.get(position);
        holder.titleTv.setText(task.title);
        holder.daysTv.setText(task.currentDays + "/" + task.targetDays);
        holder.progressRatingBar.setNumStars(task.targetDays);
        holder.progressRatingBar.setMax(task.targetDays);
        holder.progressRatingBar.setRating(task.currentDays);

        holder.alarmText.setText(task.alarmAt);
        holder.alarmSwitch.setChecked(task.isAlarm);

        if(task.isCompleted){
            holder.alarmView.setEnabled(false);
        }

        holder.alarmView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final Calendar c = Calendar.getInstance();
                int hour = c.get(Calendar.HOUR_OF_DAY);
                int minute = c.get(Calendar.MINUTE);
                // Create a new instance of TimePickerDialog and return it
                TimePickerDialog dialog = new TimePickerDialog(context, new TimePickerDialog.OnTimeSetListener(){

                    @Override
                    public void onTimeSet(TimePicker timePicker, int hourOfDay, int minute) {
                        Calendar cc = Calendar.getInstance();
                        cc.set(Calendar.HOUR_OF_DAY, hourOfDay);
                        cc.set(Calendar.MINUTE, minute);
                        SimpleDateFormat ft = new SimpleDateFormat("HH:mm");
                        holder.alarmText.setText(ft.format(cc.getTime()));
                        updateAlarm(task);
                    }

                }, hour, minute, DateFormat.is24HourFormat(context));

                dialog.show();
            }
        });

        holder.alarmSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if(b){
                    holder.alarmView.setAlpha(1.0f);
                    holder.alarmView.setClickable(true);
                    task.isAlarm = true;
                }else{
                    holder.alarmView.setAlpha(0.5f);
                    holder.alarmView.setClickable(false);
                    task.isAlarm = false;
                }
                updateAlarm(task);
            }
        });

        return convertView;
    }

    public class ViewHolder {
        TextView titleTv;
        TextView alarmText;
        View alarmView;
        Switch alarmSwitch;
        RatingBar progressRatingBar;
        TextView daysTv;
    }

    private void updateAlarm(TaskItem task) {
        // strore alarm in database
//        TaskDBHelper.getInstance(context).updateAlarm(task);
        task.save();
        
        // cancel previous
        TimeUtils.cancelAlarm(context, task.getId());
        // set a new one
        boolean isTomorrow = Long.valueOf(task.startTime) > Calendar.getInstance().getTimeInMillis();
        TimeUtils.setNotiAlarm(context, task.alarmAt, isTomorrow, task.getId());
    }

}
