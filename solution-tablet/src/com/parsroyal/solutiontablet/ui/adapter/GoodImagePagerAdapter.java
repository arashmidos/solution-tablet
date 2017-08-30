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
import com.parsroyal.solutiontablet.ui.fragment.dialogFragment.AddOrderDialogFragment;
import java.util.List;

public class GoodImagePagerAdapter extends FragmentStatePagerAdapter {

  private final Context context;
  private AddOrderDialogFragment fragment;
  private List<String> imagesPaths;

  /**
   * Instantiates a new Intro pager adapter.
   *
   * @param fm the fm
   * @param context the context
   */
  public GoodImagePagerAdapter(FragmentManager fm, Context context, List<String> imagesPaths,
      AddOrderDialogFragment fragment) {
    super(fm);
    this.context = context;
    this.fragment = fragment;
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
