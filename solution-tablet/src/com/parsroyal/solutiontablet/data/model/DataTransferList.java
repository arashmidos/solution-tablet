package com.parsroyal.solutiontablet.data.model;


import android.content.Context;
import com.parsroyal.solutiontablet.R;
import com.parsroyal.solutiontablet.constants.Constants.TransferGetDistributorOrder;
import com.parsroyal.solutiontablet.constants.Constants.TransferGetOrder;
import com.parsroyal.solutiontablet.constants.Constants.TransferSendOrder;
import com.parsroyal.solutiontablet.util.constants.ApplicationKeys;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Arash
 */
public class DataTransferList {

  private int id;
  private int imageId;
  private String title;
  private String result;
  private int success;
  private int failure;
  private int status;

  private DataTransferList(int id, int imageId, String title) {
    this.id = id;
    this.imageId = imageId;
    this.title = title;
  }

  public static List<DataTransferList> dataTransferGetList(Context context) {
    List<DataTransferList> featureList = new ArrayList<>();
    featureList.add(
        new DataTransferList(TransferGetOrder.PROVINCE, R.drawable.ic_state_24_dp,
            context.getString(R.string.provinces)));
    featureList.add(
        new DataTransferList(TransferGetOrder.CITY, R.drawable.ic_city_24_dp,
            context.getString(R.string.cities)));
    featureList.add(
        new DataTransferList(TransferGetOrder.INFO, R.drawable.ic_info_24_dp,
            context.getString(R.string.basic)));
    featureList.add(new DataTransferList(TransferGetOrder.GOODS_GROUP, R.drawable.ic_category_24_dp,
        context.getString(R.string.goods_category)));
    featureList.add(new DataTransferList(TransferGetOrder.QUESTIONNAIRE, R.drawable.ic_list_24_dp,
        context.getString(R.string.questionnaires)));
    featureList.add(new DataTransferList(TransferGetOrder.GOODS, R.drawable.ic_product_info_24_dp,
        context.getString(R.string.goods)));
    featureList.add(new DataTransferList(TransferGetOrder.VISITLINE, R.drawable.ic_path_24_dp,
        context.getString(R.string.visit_lines)));
    featureList.add(new DataTransferList(TransferGetOrder.GOODS_IMAGES, R.drawable.ic_image_24_dp,
        context.getString(R.string.goods_images)));

    return featureList;
  }

  public static List<DataTransferList> dataTransferSendList(Context context, String saleType) {
    List<DataTransferList> featureList = new ArrayList<>();
    featureList
        .add(new DataTransferList(TransferSendOrder.NEW_CUSTOMERS, R.drawable.ic_customers_24_dp,
            context.getString(R.string.new_customers)));
    featureList.add(new DataTransferList(TransferSendOrder.ADDRESS, R.drawable.ic_address_24_dp,
        context.getString(R.string.address)));
    featureList
        .add(new DataTransferList(TransferSendOrder.POSITION, R.drawable.ic_location_blue_24_dp,
            context.getString(R.string.salesman_location)));
    featureList.add(new DataTransferList(TransferSendOrder.QUESTIONNAIRE, R.drawable.ic_list_24_dp,
        context.getString(R.string.questionnaires)));
    featureList.add(new DataTransferList(TransferSendOrder.PAYMENT, R.drawable.ic_currency_24_dp,
        context.getString(R.string.payment)));
    featureList.add(new DataTransferList(TransferSendOrder.ORDER, R.drawable.ic_cart_24_dp,
        context.getString(R.string.order)));
    //FACTOR HOT SALE
//    featureList.add(new DataTransferList(TransferSendOrder.ORDER, R.drawable.ic_cart_24_dp,
//        context.getString(R.string.order)));
    if (saleType.equals(ApplicationKeys.SALE_DISTRIBUTER)) {
      featureList.add(new DataTransferList(TransferSendOrder.INVOICES, R.drawable.ic_truck_24dp,
        context.getString(R.string.invoices)));
      featureList.add(new DataTransferList(TransferSendOrder.CANCELED_INVOICES, R.drawable.ic_truck_24dp,
          context.getString(R.string.canceled_invoices)));
    }
    featureList.add(new DataTransferList(TransferSendOrder.RETURN_ORDER, R.drawable.ic_return_24_dp,
        context.getString(R.string.return_order)));
    featureList.add(new DataTransferList(TransferSendOrder.VISIT_DETAIL, R.drawable.ic_visit_24_dp,
        context.getString(R.string.visit_detail)));
    //////////
    featureList
        .add(new DataTransferList(TransferSendOrder.CUSTOMER_PICS, R.drawable.ic_camera_24_dp,
            context.getString(R.string.image)));

    return featureList;
  }

  public static List<DataTransferList> dataTransferDistributorGetList(Context context) {
    List<DataTransferList> featureList = new ArrayList<>();
    /*
    featureList.add(new DataTransferList(TransferGetDistributorOrder.GOODS_REQUEST,
        R.drawable.ic_truck_24dp,
        context.getString(R.string.goods_request)));*/
    featureList.add(
        new DataTransferList(TransferGetOrder.PROVINCE, R.drawable.ic_state_24_dp,
            context.getString(R.string.provinces)));
    featureList.add(
        new DataTransferList(TransferGetOrder.CITY, R.drawable.ic_city_24_dp,
            context.getString(R.string.cities)));
    featureList.add(
        new DataTransferList(TransferGetOrder.INFO, R.drawable.ic_info_24_dp,
            context.getString(R.string.basic)));
    featureList.add(new DataTransferList(TransferGetOrder.GOODS_GROUP, R.drawable.ic_category_24_dp,
        context.getString(R.string.goods_category)));
    featureList.add(new DataTransferList(TransferGetOrder.QUESTIONNAIRE, R.drawable.ic_list_24_dp,
        context.getString(R.string.questionnaires)));

    featureList.add(new DataTransferList(TransferGetDistributorOrder.GOODS_FOR_DELIVERY,
        R.drawable.ic_product_info_24_dp,
        context.getString(R.string.goods_for_delivery)));
    featureList.add(new DataTransferList(TransferGetDistributorOrder.VISITLINES_FOR_DELIVERY,
        R.drawable.ic_path_24_dp,
        context.getString(R.string.today_request_line)));
    featureList.add(new DataTransferList(TransferGetDistributorOrder.ORDERS_FOR_DELIVERY,
        R.drawable.ic_cart_24_dp,
        context.getString(R.string.orders_for_delivery)));

    featureList.add(new DataTransferList(TransferGetOrder.GOODS_IMAGES, R.drawable.ic_image_24_dp,
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

  public int getSuccess() {
    return success;
  }

  public void setSuccess(int success) {
    this.success = success;
  }

  public int getFailure() {
    return failure;
  }

  public void setFailure(int failure) {
    this.failure = failure;
  }

  public int getId() {
    return id;
  }

  public void setId(int id) {
    this.id = id;
  }
}

