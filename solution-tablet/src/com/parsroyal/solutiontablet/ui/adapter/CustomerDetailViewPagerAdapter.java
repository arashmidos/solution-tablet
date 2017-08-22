package com.parsroyal.solutiontablet.ui.adapter;


import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import java.util.ArrayList;

public class CustomerDetailViewPagerAdapter extends FragmentPagerAdapter {
  ArrayList<String> titles = new ArrayList<>();
  ArrayList<Fragment> fragments = new ArrayList<>();

  public CustomerDetailViewPagerAdapter(FragmentManager fm) {
    super(fm);
  }

  public void add(Fragment fragment, String title) {
    titles.add(title);
    fragments.add(fragment);
  }

  @Override public Fragment getItem(int position) {
    return fragments.get(position);
  }

  @Override public int getCount() {
    return fragments.size();
  }

  @Override public CharSequence getPageTitle(int position) {
    return titles.get(position);
  }
}
