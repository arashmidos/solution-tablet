/*
 * *
 *  * Copyright (c) 2015-2016 www.Tipi.me.
 *  * Created by Ashkan Hesaraki.
 *  * Ashkan.Hesaraki@gmail.com
 *
 */

package com.parsroyal.solutiontablet.ui.adapter;

import android.content.Context;
import android.os.Parcelable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import com.parsroyal.solutiontablet.ui.fragment.GoodImageFragment;

import java.util.List;

public class GoodImagePagerAdapter extends FragmentStatePagerAdapter {

  private final Context context;
  private List<Integer> imagesIds;

  /**
   * Instantiates a new Intro pager adapter.
   *
   * @param fm      the fm
   * @param context the context
   */
  public GoodImagePagerAdapter(FragmentManager fm, Context context, List<Integer> imagesIds) {
    super(fm);
    this.context = context;
    this.imagesIds = imagesIds;
  }

  @Override
  public Fragment getItem(int position) {
    return GoodImageFragment.newInstance(imagesIds.get(position));
  }

  @Override
  public int getCount() {
    return imagesIds.size();
  }

  @Override
  public Parcelable saveState() {
    super.saveState();
    return null;
  }
}
