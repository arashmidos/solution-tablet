package com.parsroyal.solutiontablet.data.model;


import android.content.Context;
import com.parsroyal.solutiontablet.R;
import com.parsroyal.solutiontablet.service.impl.SettingServiceImpl;
import com.parsroyal.solutiontablet.util.PreferenceHelper;
import com.parsroyal.solutiontablet.util.constants.ApplicationKeys;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Shakib
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

    String saleType = PreferenceHelper.getSaleType();

    List<FeatureList> featureList = new ArrayList<>();
    featureList.add(new FeatureList(0, R.drawable.im_near_me_64_dp,
        ApplicationKeys.SALE_DISTRIBUTER.equals(saleType) ? context.getString(R.string.today_request_line)
            : context.getString(R.string.today_paths)));
    featureList.add(
        new FeatureList(0, R.drawable.im_customers_64_dp, context.getString(R.string.customers)));
    featureList.add(new FeatureList(0, R.drawable.im_reports_64_dp,
        context.getString(R.string.reports)));
    featureList.add(new FeatureList(0, R.drawable.im_product_64_dp,
        context.getString(R.string.title_goods_list)));
    featureList
        .add(new FeatureList(0, R.drawable.im_map_64_dp, context.getString(R.string.map)));
    featureList.add(new FeatureList(0, R.drawable.im_survey_64_dp,
        context.getString(R.string.anonymous_questionnaire)));
    featureList.add(
        new FeatureList(0, R.drawable.im_my_reports_64_dp, context.getString(R.string.my_kpi)));
    featureList.add(
        new FeatureList(0, R.drawable.im_setting_64_dp, context.getString(R.string.setting)));

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

