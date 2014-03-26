package im.ycz.dailyget;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.tjerkw.slideexpandable.library.SlideExpandableListAdapter;


import java.util.ArrayList;

import im.ycz.dailyget.R;
import im.ycz.dailyget.adapter.TaskListAdapter;
import im.ycz.dailyget.data.TaskDBHelper;
import im.ycz.dailyget.data.Task;
import im.ycz.dailyget.utils.Logg;

public final class ArchiveFragment extends Fragment {
    private static final String KEY_CONTENT = "ArchiveFragment:Content";

    private String mContent = "???";
    private ListView listView;
    private ArrayList<Task> allTasks;
    private TaskListAdapter adapter;

    private TextView rounds, completed, days;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if ((savedInstanceState != null) && savedInstanceState.containsKey(KEY_CONTENT)) {
            mContent = savedInstanceState.getString(KEY_CONTENT);
        }

        registerReceiver();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View contentView = inflater.inflate(R.layout.fragment_archive, container, false);

        listView = (ListView)contentView.findViewById(R.id.list);
        rounds = (TextView)contentView.findViewById(R.id.archive_rounds);
        completed = (TextView)contentView.findViewById(R.id.archive_completed);
        days = (TextView)contentView.findViewById(R.id.archive_days);

        allTasks = (ArrayList<Task>) Task.listAll(Task.class);
        adapter = new TaskListAdapter(this.getActivity(), allTasks);

        listView.setAdapter(
                new SlideExpandableListAdapter(
                        adapter,
                        R.id.expandable_toggle_button,
                        R.id.expandable
                )
        );


        rounds.setText(""+allTasks.size());
        int completedCounts = 0, daysCount = 0;
        for(Task item:allTasks){
            if (item.isCompleted) {
                completedCounts++;
                daysCount += item.targetDays;
            } else  {
                daysCount += item.currentDays;
            }

        }
        completed.setText(""+completedCounts);
        days.setText(""+daysCount);

        return contentView;
    }

    /**
     * Builds dummy data for the test.
     * In a real app this would be an adapter
     * for your data. For example a CursorAdapter
     */
    public ListAdapter buildDummyData() {
        final int SIZE = 20;
        String[] values = new String[SIZE];
        for(int i=0;i<SIZE;i++) {
            values[i] = "Item "+i;
        }
        return new ArrayAdapter<String>(
                this.getActivity(),
                R.layout.list_item,
                R.id.text,
                values
        );
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString(KEY_CONTENT, mContent);
    }

    @Override
    public void onDestroy() {
        // TODO Auto-generated method stub
        this.getActivity().unregisterReceiver(receiver);
        super.onDestroy();
    }

    private RefreshReceiver receiver;
    public static final String ACTION_TASK_CREATED = "action_task_created";
    public static final String ACTION_TASK_DONE_TODAY = "action_task_done_today";

    private void registerReceiver(){
        receiver = new RefreshReceiver();
        IntentFilter filter=new IntentFilter();
        filter.addAction(ACTION_TASK_CREATED);
        filter.addAction(ACTION_TASK_DONE_TODAY);
        //动态注册BroadcastReceiver
        this.getActivity().registerReceiver(receiver, filter);
    }

    class RefreshReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            
        	if(intent.getAction().equals(ACTION_TASK_CREATED)) {
        		
                Task item = (Task) intent.getSerializableExtra("new_task");
                allTasks.add(item);
                adapter.notifyDataSetChanged();
            }
            
            if(intent.getAction().equals(ACTION_TASK_DONE_TODAY)) {
                long task_id = intent.getLongExtra("task_id", -1);
                Log.d("DEBUG", "task_id: " + task_id);
                if(task_id == -1) return;
                for(Task item : allTasks){
                	Log.d("DEBUG", "Long --- " + item.getId());
                    if(item.getId().longValue() == task_id){
                        item.currentDays++;
                        Logg.p("update archive days: " + item.currentDays);
                        adapter.notifyDataSetChanged();
//                        updateView(item);
//                        listView.invalidateViews();
//                        listView.invalidate();
                    }
                }
            }

        }
        
        private void updateView(Task task){
        	int itemIndex = allTasks.indexOf(task);
            int visiblePosition = listView.getFirstVisiblePosition();
            View v = listView.getChildAt(itemIndex - visiblePosition);
            // Do something fancy with your listitem view
            RatingBar ratingBar = (RatingBar) v.findViewById(R.id.item_progress_bar);
            ratingBar.setRating(task.currentDays);
        }
    }
    
}