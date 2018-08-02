package com.parsroyal.solutiontablet.constants;

import com.parsroyal.solutiontablet.R;

/**
 * Created by Mahyar on 9/4/2015.
 */
public class Constants {

  public static final String APPLICATION_NAME = "SolutionMobile";
  public static final String CUSTOMER_PICTURE_DIRECTORY_NAME = "Customer-Pics";
  public static final String GPS_TRACKER_ACTION = "com.parsroyal.solutiontablet.TrackerAlarmReceiver";
  public static final String CUSTOMER_BACKEND_ID = "CUSTOMER_BACKEND_ID";
  public static final String PAYMENT_ID = "paymentId";
  public static final String PARENT = "PARENT";
  public static final String CUSTOMER_ID = "customerId";
  public static final String QUESTIONAIRE_ID = "qnId";
  public static final String PAGE_STATUS = "pageStatus";
  public static final String NEW = "new";
  public static final String EDIT = "edit";
  public static final String VIEW = "view";
  public static final String QUESTIONNAIRE_BACKEND_ID = "QUESTIONNAIRE_BACKEND_ID";
  public static final String QUESTIONNAIRE_OBJ = "QUESTIONNAIRE_OBJ";
  public static final String GOODS_BACKEND_ID = "goodsBackendId";
  public static final String QUESTIONNAIRE_CATEGORY = "QUESTIONNAIRE_CATEGORY";
  public static final String GOOD_QUESTIONNAIRE = "GOOD_QUESTIONNAIRE";
  public static final String GENERAL_QUESTIONNAIRE = "GENERAL_QUESTIONNAIRE";
  public static final String GOODS_INVOICE_ID = "goodsInvoiceId";
  public static final String GOODS_SALE_RATE = "goodsSaleRate";
  public static final String GOODS_GROUP_BACKEND_ID = "ggBi";
  public static final String VISITLINE_BACKEND_ID = "visitLineBackendId";
  public static final String ANSWERS_GROUP_NO = "answersGroupNo";
  public static final String COUNT = "count";
  public static final String SELECTED_UNIT = "selectedUnit";
  public static final String VISIT_ID = "visitId";
  public static final String ORDER_ID = "orderId";
  public static final String SALE_TYPE = "saleType";
  public static final String DISABLED = "disabled";
  public static final String ORDER_STATUS = "orderStatus";
  public static final String REJECTED_LIST = "rejectedList";
  public static final String STATUS_ID = "statusId";
  public static final String UPDATE_USER = "center";
  public static final String UPDATE_PASS = "center1234";
  public static final int ICON_MESSAGE = R.drawable.ic_info_outline_black_24dp;
  public static final int ICON_WARNING = R.drawable.ic_warning_24dp;
  public static final Float MAX_DISTANCE = 800.0f;
  public static final int FULL_UPDATE = 1024;
  public static final int PARTIAL_UPDATE = 512;
  public static final int NO_UPDATE = 0;
  public static final int GPS_SEND_INTERVAL_IN_SECOND = 60;
  public static final String FIRST_POSITION = "firstPosition";

  //new version
  //login
  public static final String DELIVERY = "DELIVERY";
  public static final String SALE_MAN = "SALE_MAN";
  public static final String CHART = "CHART";

  public static final String DATA_TRANSFER_ACTION = "dataTrasfer";
  public static final String DATA_TRANSFER_GET = "dataTransfer.get";
  public static final String DATA_TRANSFER_SEND_DATA = "dataTransfer.send.data";
  public static final String DATA_TRANSFER_GET_GOODS = "dataTransfer.get.goods";
  public static final String READ_ONLY = "readOnly";
  public static final String ApplicationKey = "solution-mobile";
  public static final String DEFAULT_LANGUAGE = "fa";

  public static final String TITLE = "TITLE";
  public static final String QUESTION_COUNT = "QUESTION_COUNT";
  public static final String QUESTION_POSITION = "QUESTION_POSITION";
  public static final String QUESTION_DTO = "QUESTION_DTO";
  public static final String ORIGIN_VISIT_ID = "ORIGIN_VISIT_ID";
  public static final int MAX_NEW_CUSTOMER_PHOTO = 10;
  public static final int CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE = 100;
  public static final String REPORT_TYPE = "REPORT_TYPE";
  public static final String REPORT_SALESMAN = "REPORT_SALESMAN";
  public static final String REPORT_CUSTOMER = "REPORT_CUSTOMER";
  public static final String REPORT_ITEM = "REPORT_ITEM";
  public static final String REPORT_CUSTOMER_ID = "REPORT_CUSTOMER_ID";
  public static final String REJECT_TYPE_ID = "REJECT_TYPE_ID";

  public class TransferStatus {

    public static final int READY = 0;
    public static final int IN_PROGRESS = 1;
    public static final int ERROR = 2;
    public static final int DONE = 3;
    public static final int CANCELED = 4;
  }

  public class TransferGetOrder {

    public static final int PROVINCE = 1;
    public static final int CITY = 2;
    public static final int INFO = 3;
    public static final int GOODS_GROUP = 4;
    public static final int QUESTIONNAIRE = 5;
    public static final int GOODS = 6;
    public static final int VISITLINE = 7;
    public static final int GOODS_IMAGES = 8;
  }

  public class TransferGetDistributorOrder {

    public static final int GOODS_FOR_DELIVERY = 10;
    public static final int VISITLINES_FOR_DELIVERY = 11;
    public static final int ORDERS_FOR_DELIVERY = 12;
    public static final int GOODS_REQUEST = 13;
  }

  public class TransferSendOrder {

    public static final int NEW_CUSTOMERS = 20;
    public static final int ADDRESS = 21;
    public static final int POSITION = 22;
    public static final int QUESTIONNAIRE = 23;
    public static final int PAYMENT = 24;
    public static final int ORDER = 25;
    public static final int RETURN_ORDER = 26;
    public static final int VISIT_DETAIL = 27;
    public static final int INVOICES = 28;
    public static final int CUSTOMER_PICS = 29;
    public static final int CANCELED_INVOICES = 30;
  }
}
