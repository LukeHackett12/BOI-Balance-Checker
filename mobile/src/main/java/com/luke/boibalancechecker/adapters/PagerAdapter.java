package com.luke.boibalancechecker.adapters;


import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import com.luke.boibalancechecker.tabs.TabFragmentBalance;
import com.luke.boibalancechecker.tabs.TabFragmentSettings;

public class PagerAdapter extends FragmentStatePagerAdapter {
    int numOfTabs;

    public PagerAdapter(FragmentManager fm, int numOfTabs) {
        super(fm);
        this.numOfTabs = numOfTabs;
    }

    @Override
    public Fragment getItem(int position) {

        switch (position) {
            case 0:
                return new TabFragmentBalance();
            case 1:
                return new TabFragmentSettings();
            default:
                return null;
        }
    }

    @Override
    public int getCount() {
        return numOfTabs;
    }
}
