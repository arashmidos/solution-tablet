package com.parsroyal.storemanagement.data.model;


import android.content.Context;
import com.parsroyal.storemanagement.R;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Arash
 */
public class FeatureList {

  private int badger;
  private int imageId;
  private String title;

  private FeatureList(int badger, int imageId, String title) {
    this.badger = badger;
    this.imageId = imageId;
    this.title = title;
  }

  public static List<FeatureList> getFeatureList(Context context) {

    List<FeatureList> featureList = new ArrayList<>();
    featureList.add(new FeatureList(0, R.drawable.ic_near_me_black_48dp,
        context.getString(R.string.code_recognition)));
    featureList.add(
        new FeatureList(0, R.drawable.ic_group_black_48dp, context.getString(R.string.packer)));
    featureList.add(new FeatureList(0, R.drawable.ic_assignment_black_48dp,
        context.getString(R.string.warehouse_handling)));
    featureList.add(new FeatureList(0, R.drawable.ic_products_48_dp,
        context.getString(R.string.goods_receipt)));
    featureList.add(
        new FeatureList(0, R.drawable.ic_map_black_48dp, context.getString(R.string.good_draft)));

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

