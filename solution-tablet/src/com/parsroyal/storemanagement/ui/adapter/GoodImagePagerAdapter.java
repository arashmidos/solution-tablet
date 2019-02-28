/*
 * *
 *  * Copyright (c) 2015-2016 www.Tipi.me.
 *  * Created by Ashkan Hesaraki.
 *  * Ashkan.Hesaraki@gmail.com
 *
 */

package com.parsroyal.storemanagement.ui.adapter;

import android.content.Context;
import android.os.Parcelable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import com.parsroyal.storemanagement.ui.fragment.GoodImageFragment;
import java.util.List;

public class GoodImagePagerAdapter extends FragmentStatePagerAdapter {

  private final Context context;
  private List<String> imagesPaths;

  /**
   * Instantiates a new Intro pager adapter.
   *
   * @param fm the fm
   * @param context the context
   */
  public GoodImagePagerAdapter(FragmentManager fm, Context context, List<String> imagesPaths) {
    super(fm);
    this.context = context;
    this.imagesPaths = imagesPaths;
  }

  @Override
  public Fragment getItem(int position) {
    return GoodImageFragment.newInstance(imagesPaths.get(position));
  }

  @Override
  public int getCount() {
    return imagesPaths.size();
  }

  @Override
  public Parcelable saveState() {
    super.saveState();
    return null;
  }
}
