package com.parsroyal.solutiontablet.data.model;


import android.content.Context;
import com.parsroyal.solutiontablet.R;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Shakib
 */
public class FeatureList {

  private int badger;
  private int imageId;
  private String title;

  public FeatureList(int badger, int imageId, String title) {
    this.badger = badger;
    this.imageId = imageId;
    this.title = title;
  }

  public static List<FeatureList> getFeatureList(Context context) {
    List<FeatureList> featureList = new ArrayList<>();
    featureList.add(new FeatureList(1, R.drawable.ic_near_me_black_48dp,
        context.getString(R.string.today_paths)));
    featureList.add(
        new FeatureList(0, R.drawable.ic_group_black_48dp, context.getString(R.string.customers)));
    featureList.add(new FeatureList(0, R.drawable.ic_assignment_black_48dp,
        context.getString(R.string.reports)));
    featureList
        .add(new FeatureList(0, R.drawable.ic_map_black_48dp, context.getString(R.string.map)));
    featureList.add(
        new FeatureList(0, R.drawable.ic_settings_black_48dp, context.getString(R.string.setting)));
    return featureList;
  }

  public int getBadger() {
    return badger;
  }

  public void setBadger(int badger) {
    this.badger = badger;
  }

  public int getImageId() {
    return imageId;
  }

  public void setImageId(int imageId) {
    this.imageId = imageId;
  }

  public String getTitle() {
    return title;
  }

  public void setTitle(String title) {
    this.title = title;
  }
}
