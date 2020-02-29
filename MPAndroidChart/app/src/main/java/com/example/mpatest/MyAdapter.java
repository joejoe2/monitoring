package com.example.mpatest;

import android.content.Context;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import java.util.ArrayList;

public class MyAdapter extends FragmentPagerAdapter {
    int totalTabs;
    Context context;
    public ArrayList<MainFragment> allfrag=new ArrayList<MainFragment>();
    public MyAdapter(Context c, FragmentManager fm, int totalTabs) {
        super(fm);
        context = c;
        this.totalTabs = totalTabs;

    }

    @Override
    public Fragment getItem(int position) {
        //MainFragment tab=new MainFragment();
        // tab;
        return allfrag.get(position);
    }
    @Override
    public int getCount() {
        return totalTabs;
    }
}
