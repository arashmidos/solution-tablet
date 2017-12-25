package com.parsroyal.solutiontablet.data.model;


import android.content.Context;
import com.parsroyal.solutiontablet.R;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Arash
 */
public class DataTransferList {

  private int imageId;
  private String title;
  private String result;
  private int status;

  public DataTransferList(int imageId, String title) {
    this.imageId = imageId;
    this.title = title;
  }

  public static List<DataTransferList> dataTransferGetList(Context context) {
    List<DataTransferList> featureList = new ArrayList<>();
    featureList.add(
        new DataTransferList(R.drawable.ic_state_24_dp, context.getString(R.string.provinces)));
    featureList.add(
        new DataTransferList(R.drawable.ic_city_24_dp, context.getString(R.string.cities)));
    featureList.add(
        new DataTransferList(R.drawable.ic_info_24_dp, context.getString(R.string.basic)));
    featureList.add(new DataTransferList(R.drawable.ic_category_24_dp,
        context.getString(R.string.goods_category)));
    featureList.add(new DataTransferList(R.drawable.ic_list_24_dp,
        context.getString(R.string.questionnaires)));
    featureList.add(new DataTransferList(R.drawable.ic_product_info_24_dp,
        context.getString(R.string.goods)));
    featureList.add(new DataTransferList(R.drawable.ic_path_24_dp,
        context.getString(R.string.visit_lines)));
    featureList.add(new DataTransferList(R.drawable.ic_image_24_dp,
        context.getString(R.string.goods_images)));

    return featureList;
  }

  public static List<DataTransferList> dataTransferSendList(Context context) {
    List<DataTransferList> featureList = new ArrayList<>();
    featureList.add(
        new DataTransferList(R.drawable.ic_customers_24_dp, context.getString(R.string.new_customers)));



    featureList.add(
        new DataTransferList(R.drawable.ic_city_24_dp, context.getString(R.string.cities)));
    featureList.add(
        new DataTransferList(R.drawable.ic_info_24_dp, context.getString(R.string.basic)));
    featureList.add(new DataTransferList(R.drawable.ic_category_24_dp,
        context.getString(R.string.goods_category)));
    featureList.add(new DataTransferList(R.drawable.ic_list_24_dp,
        context.getString(R.string.questionnaires)));
    featureList.add(new DataTransferList(R.drawable.ic_product_info_24_dp,
        context.getString(R.string.goods)));
    featureList.add(new DataTransferList(R.drawable.ic_path_24_dp,
        context.getString(R.string.visit_lines)));
    featureList.add(new DataTransferList(R.drawable.ic_image_24_dp,
        context.getString(R.string.goods_images)));

    return featureList;
  }

  public String getResult() {
    return result;
  }

  public void setResult(String result) {
    this.result = result;
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

  public int getStatus() {
    return status;
  }

  public void setStatus(int status) {
    this.status = status;
  }
}

