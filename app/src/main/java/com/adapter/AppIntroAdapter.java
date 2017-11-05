package com.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import com.fragment.AppIntroFragment;

import java.util.ArrayList;


/**
 * Created by hp1 on 21-01-2015.
 */
public class AppIntroAdapter extends FragmentStatePagerAdapter {


    ArrayList<AppIntroFragment> listFragments;

    public AppIntroAdapter(FragmentManager fm, ArrayList<AppIntroFragment> listFragments) {
        super(fm);

        this.listFragments = listFragments;

    }

    // This method return the fragment for the every position in the View Pager
    @Override
    public Fragment getItem(int position) {

        return listFragments.get(position);
    }

    // This method return the titles for the Tabs in the Tab Strip

    @Override
    public CharSequence getPageTitle(int position) {
        return "";
    }

    // This method return the Number of tabs for the tabs Strip

    @Override
    public int getCount() {
        return listFragments.size();
    }
}
