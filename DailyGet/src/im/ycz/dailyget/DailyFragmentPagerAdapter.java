package im.ycz.dailyget;

import im.ycz.dailyget.R;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.viewpagerindicator.IconPagerAdapter;

/**
 * Created by tinyao on 11/15/13.
 */
public class DailyFragmentPagerAdapter extends FragmentPagerAdapter implements IconPagerAdapter {

	private static final String[] CONTENT = new String[] { "", "", ""};

    private static final int[] TAB_ICONS = new int[] {
        R.drawable.tab_taskone_icon,
        R.drawable.tab_archive_icon,
        R.drawable.tab_tasktwo_icon,
    };
    
    private static final int FRAGMENT_ARCHIVE = 1;
    private static final int FRAGMENT_TASK_ONE = 0;
    private static final int FRAGMENT_TASK_TWO = 2;
	
    final int PAGE_COUNT = 3;
    
    private TaskFragment oneFragment, twoFragment;
    private ArchiveFragment archiveFragment;
    

    static final class TabInfo
    {
        private final Class<?> clss;
        private final Bundle args;

        TabInfo(Class<?> _class, Bundle _args)
        {
            clss = _class;
            args = _args;
        }
    }

    /** Constructor of the class */
    public DailyFragmentPagerAdapter(FragmentManager fm) {
        super(fm);
    }

    /** This method will be invoked when a page is requested to create */
    @Override
    public Fragment getItem(int id) {
        Bundle data = new Bundle();
        switch(id){
            case FRAGMENT_ARCHIVE:
                if(archiveFragment == null){
                    archiveFragment = new ArchiveFragment();
                }
                data.putInt("current_page", FRAGMENT_ARCHIVE);
                archiveFragment.setArguments(data);
                return archiveFragment;
            case FRAGMENT_TASK_ONE:
                if(oneFragment == null){
                    oneFragment = new TaskFragment(1); // give a id
                }
                data.putInt("current_page", FRAGMENT_TASK_ONE);
                oneFragment.setArguments(data);
                return oneFragment;
            case FRAGMENT_TASK_TWO:
                if(twoFragment == null) {
                    twoFragment = new TaskFragment(2);
                }
                data.putInt("current_page", FRAGMENT_TASK_TWO);
                twoFragment.setArguments(data);
                return twoFragment;
        }

        return null;
    }

    @Override
    public int getCount() {
        return PAGE_COUNT;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return CONTENT[position % CONTENT.length].toUpperCase();
    }

    @Override public int getIconResId(int index) {
        return TAB_ICONS[index % TAB_ICONS.length];
    }
}
