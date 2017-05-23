package com.cuelogic.firebase.chat.ui.adapters;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.cuelogic.firebase.chat.ui.fragments.RecentChatsFragment;
import com.cuelogic.firebase.chat.ui.fragments.SettingsFragment;
import com.cuelogic.firebase.chat.ui.fragments.UsersFragment;

public class DashboardPagerAdapter extends FragmentPagerAdapter {
    private static final Fragment[] sFragments = new Fragment[]{
            RecentChatsFragment.newInstance(UsersFragment.TYPE_ALL),
            UsersFragment.newInstance(UsersFragment.TYPE_ALL),
            SettingsFragment.newInstance()
    };
    private static final String[] sTitles = new String[]{"Chats", "Users", "Settings"};

    public DashboardPagerAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {
        return sFragments[position];
    }

    @Override
    public int getCount() {
        return sFragments.length;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return sTitles[position];
    }
}
