package com.example.firstapplication.ui.main;

import android.content.Context;

import androidx.annotation.Nullable;
import androidx.annotation.StringRes;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import com.example.firstapplication.R;
import com.example.firstapplication.fragment.HistoryFragment;
import com.example.firstapplication.fragment.FutureFragment;
import com.example.firstapplication.fragment.TodayFragment;

import static com.example.firstapplication.shared.Constant.JOB_MODE_CURRENT;
import static com.example.firstapplication.shared.Constant.JOB_MODE_FUTURE;
import static com.example.firstapplication.shared.Constant.JOB_MODE_HISTORY;

/**
 * A [FragmentPagerAdapter] that returns a fragment corresponding to
 * one of the sections/tabs/pages.
 */
public class SectionsPagerAdapter extends FragmentPagerAdapter {

    @StringRes
    private static final int[] TAB_TITLES = new int[]{R.string.tab_text_1, R.string.tab_text_2, R.string.tab_text_3};
    private Context context;
    private Fragment[] fragments;

    public SectionsPagerAdapter(Context context, FragmentManager fm) {
        super(fm);
        this.context = context;
        fragments = new Fragment[]{
                new FutureFragment(JOB_MODE_FUTURE),
                new HistoryFragment(JOB_MODE_HISTORY),
                new TodayFragment(JOB_MODE_CURRENT)
        };
    }

    @Override
    public Fragment getItem(int position) {
        return fragments[position];
    }

    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {
        return context.getResources().getString(TAB_TITLES[position]);
    }

    @Override
    public int getCount() {
        return TAB_TITLES.length;
    }

}