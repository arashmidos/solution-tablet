package com.parsroyal.solutiontablet.data.dao.impl;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import com.parsroyal.solutiontablet.constants.BaseInfoTypes;
import com.parsroyal.solutiontablet.constants.SaleOrderStatus;
import com.parsroyal.solutiontablet.constants.VisitInformationDetailType;
import com.parsroyal.solutiontablet.data.dao.SaleOrderDao;
import com.parsroyal.solutiontablet.data.entity.BaseInfo;
import com.parsroyal.solutiontablet.data.entity.Customer;
import com.parsroyal.solutiontablet.data.entity.SaleOrder;
import com.parsroyal.solutiontablet.data.entity.VisitInformationDetail;
import com.parsroyal.solutiontablet.data.helper.CommerDatabaseHelper;
import com.parsroyal.solutiontablet.data.listmodel.SaleOrderListModel;
import com.parsroyal.solutiontablet.data.model.BaseSaleDocument;
import com.parsroyal.solutiontablet.data.model.SaleInvoiceDocument;
import com.parsroyal.solutiontablet.data.model.SaleOrderDocument;
import com.parsroyal.solutiontablet.data.model.SaleOrderDto;
import com.parsroyal.solutiontablet.data.model.SaleRejectDocument;
import com.parsroyal.solutiontablet.data.searchobject.SaleOrderSO;
import com.parsroyal.solutiontablet.service.SettingService;
import com.parsroyal.solutiontablet.service.impl.SettingServiceImpl;
import com.parsroyal.solutiontablet.util.DateUtil;
import com.parsroyal.solutiontablet.util.Empty;
import com.parsroyal.solutiontablet.util.constants.ApplicationKeys;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by Mahyar on 8/21/2015.
 */
public class SaleOrderDaoImpl extends AbstractDao<SaleOrder, Long> implements SaleOrderDao {

  private Context context;
  private SettingService settingService;

  public SaleOrderDaoImpl(Context context) {
    this.context = context;
    settingService = new SettingServiceImpl(context);
  }

  @Override
  protected Context getContext() {
    return context;
  }

  @Override
  protected ContentValues getContentValues(SaleOrder entity) {
    ContentValues contentValues = new ContentValues();
    contentValues.put(SaleOrder.COL_ID, entity.getId());
    contentValues.put(SaleOrder.COL_NUMBER, entity.getNumber());
    contentValues.put(SaleOrder.COL_AMOUNT, entity.getAmount());
    contentValues.put(SaleOrder.COL_DATE, entity.getDate());
    contentValues.put(SaleOrder.COL_PAYMENT_TYPE_BACKEND_ID, entity.getPaymentTypeBackendId());
    contentValues.put(SaleOrder.COL_SALESMAN_ID, entity.getSalesmanId());
    contentValues.put(SaleOrder.COL_CUSTOMER_BACKEND_ID, entity.getCustomerBackendId());
    contentValues.put(SaleOrder.COL_DESCRIPTION, entity.getDescription());
    contentValues.put(SaleOrder.COL_STATUS, entity.getStatus());
    contentValues.put(SaleOrder.COL_BACKEND_ID, entity.getBackendId());
    contentValues.put(SaleOrder.COL_INVOICE_BACKEND_ID, entity.getInvoiceBackendId());
    contentValues.put(SaleOrder.COL_CREATE_DATE_TIME, entity.getCreateDateTime());
    contentValues.put(SaleOrder.COL_UPDATE_DATE_TIME, entity.getUpdateDateTime());
    contentValues.put(SaleOrder.COL_REJECT_TYPE_BACKEND_ID, entity.getRejectType());
    contentValues.put(SaleOrder.COL_VISITLINE_BACKEND_ID, entity.getVisitlineBackendId());
    return contentValues;
  }

  @Override
  protected String getTableName() {
    return SaleOrder.TABLE_NAME;
  }

  @Override
  protected String getPrimaryKeyColumnName() {
    return SaleOrder.COL_ID;
  }

  @Override
  protected String[] getProjection() {
    return new String[]{
        SaleOrder.COL_ID,
        SaleOrder.COL_NUMBER,
        SaleOrder.COL_AMOUNT,
        SaleOrder.COL_DATE,
        SaleOrder.COL_PAYMENT_TYPE_BACKEND_ID,
        SaleOrder.COL_SALESMAN_ID,//5
        SaleOrder.COL_CUSTOMER_BACKEND_ID,
        SaleOrder.COL_DESCRIPTION,
        SaleOrder.COL_STATUS,
        SaleOrder.COL_BACKEND_ID,
        SaleOrder.COL_INVOICE_BACKEND_ID,
        SaleOrder.COL_CREATE_DATE_TIME,
        SaleOrder.COL_UPDATE_DATE_TIME,
        SaleOrder.COL_REJECT_TYPE_BACKEND_ID,//13
        SaleOrder.COL_VISITLINE_BACKEND_ID
    };
  }

  @Override
  protected SaleOrder createEntityFromCursor(Cursor cursor) {
    SaleOrder saleOrder = new SaleOrder();

    saleOrder.setId(cursor.getLong(0));
    saleOrder.setNumber(cursor.getLong(1));
    saleOrder.setAmount(cursor.getLong(2));
    saleOrder.setDate(cursor.getString(3));
    saleOrder.setPaymentTypeBackendId(cursor.getLong(4));
    saleOrder.setSalesmanId(cursor.getLong(5));
    saleOrder.setCustomerBackendId(cursor.getLong(6));
    saleOrder.setDescription(cursor.getString(7));
    saleOrder.setStatus(cursor.getLong(8));
    saleOrder.setBackendId(cursor.getLong(9));
    saleOrder.setInvoiceBackendId(cursor.getLong(10));
    saleOrder.setCreateDateTime(cursor.getString(11));
    saleOrder.setUpdateDateTime(cursor.getString(12));
    saleOrder.setRejectType(cursor.getLong(13));
    saleOrder.setVisitlineBackendId(cursor.getLong(14));
    return saleOrder;
  }

  protected SaleOrderDto createOrderDtoFromCursor(Cursor cursor) {
    SaleOrderDto saleOrder = new SaleOrderDto();

    saleOrder.setId(cursor.getLong(0));
    saleOrder.setNumber(cursor.getLong(1));
    saleOrder.setAmount(cursor.getLong(2));
    saleOrder.setDate(cursor.getString(3));
    saleOrder.setPaymentTypeBackendId(cursor.getLong(4));
    saleOrder.setSalesmanId(cursor.getLong(5));
    saleOrder.setCustomerBackendId(cursor.getLong(6));
    saleOrder.setDescription(cursor.getString(7));
    saleOrder.setStatus(cursor.getLong(8));
    saleOrder.setBackendId(cursor.getLong(9));
    saleOrder.setInvoiceBackendId(cursor.getLong(10));
    saleOrder.setCreateDateTime(cursor.getString(11));
    saleOrder.setUpdateDateTime(cursor.getString(12));
    saleOrder.setRejectType(cursor.getLong(13));
    saleOrder.setVisitlineBackendId(cursor.getLong(14));
    return saleOrder;
  }

  protected BaseSaleDocument createSaleDocumentFromCursor(Cursor cursor) {
    BaseSaleDocument saleOrder = null;
    Long statusId = cursor.getLong(8);
    if (SaleOrderStatus.READY_TO_SEND.getId().equals(statusId)) {
      saleOrder = new SaleOrderDocument();
      String orderType = settingService.getSettingValue(ApplicationKeys.SETTING_ORDER_TYPE);
      saleOrder.setType(Empty.isEmpty(orderType) ? 0 : Integer.valueOf(orderType));
      //Set export date to tomorrow
      String saleDate = cursor.getString(3);
      Date date = DateUtil.convertStringToDate(saleDate, DateUtil.GLOBAL_FORMATTER, "FA");
      date = DateUtil.addDaysToDate(date, 1, false);
      ((SaleOrderDocument) saleOrder)
          .setExportDate(DateUtil.convertDate(date, DateUtil.GLOBAL_FORMATTER, "FA"));
    } else if (SaleOrderStatus.INVOICED.getId().equals(statusId) || SaleOrderStatus.DELIVERED
        .getId().equals(statusId) || SaleOrderStatus.CANCELED.getId().equals(statusId)) {
      saleOrder = new SaleInvoiceDocument();
      saleOrder.setType(
          Integer.valueOf(settingService.getSettingValue(ApplicationKeys.SETTING_INVOICE_TYPE)));

      ((SaleInvoiceDocument) saleOrder).setSaleOrderId(cursor.getLong(9));
      ((SaleInvoiceDocument) saleOrder).setRejectType(cursor.getLong(13));
      ((SaleInvoiceDocument) saleOrder).setVisitlineBackendId(cursor.getLong(14));
      saleOrder.setStatusCode(statusId);
    } else if (SaleOrderStatus.REJECTED.getId().equals(statusId)) {
      saleOrder = new SaleRejectDocument();
      saleOrder.setType(
          Integer.valueOf(settingService.getSettingValue(ApplicationKeys.SETTING_REJECT_TYPE)));
      ((SaleRejectDocument) saleOrder).setSaleOrderId(cursor.getLong(10));
    } else if (SaleOrderStatus.GIFT.getId().equals(statusId)) {
      saleOrder = new SaleOrderDocument();
      String orderType = settingService.getSettingValue(ApplicationKeys.SETTING_ORDER_TYPE);
      saleOrder.setType(Empty.isEmpty(orderType) ? 0 : Integer.valueOf(orderType));
      //Set export date to tomorrow
      String saleDate = cursor.getString(3);
      Date date = DateUtil.convertStringToDate(saleDate, DateUtil.GLOBAL_FORMATTER, "FA");
      date = DateUtil.addDaysToDate(date, 1, false);
      ((SaleOrderDocument) saleOrder)
          .setExportDate(DateUtil.convertDate(date, DateUtil.GLOBAL_FORMATTER, "FA"));
    }

    saleOrder.setId(cursor.getLong(0));
    saleOrder.setPrice(cursor.getLong(2));
    saleOrder.setDate(cursor.getString(3));
    saleOrder.setPaymentType(cursor.getLong(4));
    saleOrder.setSalesman(cursor.getLong(5));
    saleOrder.setCustomer(cursor.getLong(6));
    saleOrder.setDescription(Empty.isEmpty(cursor.getString(7)) ? "" : cursor.getString(7));
    saleOrder.setCompanyId(
        Integer.valueOf(settingService.getSettingValue(ApplicationKeys.USER_COMPANY_ID)));
    if (SaleOrderStatus.REJECTED.getId().equals(statusId)) {
      try {
        saleOrder.setStockCode(
            Integer.valueOf(settingService.getSettingValue(ApplicationKeys.SETTING_STOCK_CODE)));
        saleOrder.setOfficeCode(
            Integer.valueOf(settingService.getSettingValue(ApplicationKeys.SETTING_BRANCH_CODE)));
      } catch (Exception ignore) {
      }
    } else {
      saleOrder.setStockCode(
          Integer.valueOf(settingService.getSettingValue(ApplicationKeys.SETTING_STOCK_ID)));
      saleOrder.setOfficeCode(
          Integer.valueOf(settingService.getSettingValue(ApplicationKeys.SETTING_BRANCH_ID)));
    }
    return saleOrder;
  }

  @Override
  public List<SaleOrder> retrieveSaleOrderByStatus(Long statusId) {
    CommerDatabaseHelper databaseHelper = CommerDatabaseHelper.getInstance(getContext());
    SQLiteDatabase db = databaseHelper.getWritableDatabase();

    List<SaleOrder> saleOrders = new ArrayList<>();
    String sql =
        "SELECT * FROM " + SaleOrder.TABLE_NAME + " WHERE " + SaleOrder.COL_STATUS + " = ?";
    String[] args = {String.valueOf(statusId)};

    Cursor cursor = db.rawQuery(sql, args);
    while (cursor.moveToNext()) {
      saleOrders.add(createEntityFromCursor(cursor));
    }
    cursor.close();

    return saleOrders;
  }

  @Override
  public List<SaleOrderListModel> searchForOrders(SaleOrderSO saleOrderSO) {
    CommerDatabaseHelper databaseHelper = CommerDatabaseHelper.getInstance(getContext());
    SQLiteDatabase db = databaseHelper.getWritableDatabase();

    List<SaleOrderListModel> returnOrders = new ArrayList<>();
    List<String> argsList = new ArrayList<>();
    String sql = "SELECT DISTINCT " +
        "o." + SaleOrder.COL_ID + "," +
        "o." + SaleOrder.COL_BACKEND_ID + "," +
        "o." + SaleOrder.COL_DATE + "," +
        "o." + SaleOrder.COL_AMOUNT + "," +
        "o." + SaleOrder.COL_STATUS + "," +
        "c." + Customer.COL_FULL_NAME + "," +//5
        "i." + BaseInfo.COL_TITLE + "," +
        "c." + Customer.COL_BACKEND_ID + "," +
        "o." + SaleOrder.COL_CREATE_DATE_TIME + "," +
        "o." + SaleOrder.COL_DESCRIPTION + "," +
        "vd." + VisitInformationDetail.COL_VISIT_INFORMATION_ID + "," +
        "o." + SaleOrder.COL_NUMBER +//11
        " FROM " + SaleOrder.TABLE_NAME + " o " +
        " INNER JOIN " + Customer.TABLE_NAME + " c on c." + Customer.COL_BACKEND_ID + " = o."
        + SaleOrder.COL_CUSTOMER_BACKEND_ID +
        " LEFT OUTER JOIN " + BaseInfo.TABLE_NAME + " i on i."
        + BaseInfo.COL_BACKEND_ID + " = o." + SaleOrder.COL_PAYMENT_TYPE_BACKEND_ID
        + " AND i." + BaseInfo.COL_TYPE + " = " + BaseInfoTypes.PAYMENT_TYPE.getId()
        + " LEFT JOIN " + VisitInformationDetail.TABLE_NAME + " vd on vd."
        + VisitInformationDetail.COL_TYPE_ID + " = o." + SaleOrder.COL_ID
        + " AND vd." + VisitInformationDetail.COL_TYPE + " = ?"
        + " WHERE 1 = 1";

    if (Empty.isNotEmpty(saleOrderSO)) {
      Long statusId = saleOrderSO.getStatusId();
      if (Empty.isNotEmpty(statusId)) {

        if (statusId.equals(SaleOrderStatus.READY_TO_SEND.getId())) {
          argsList.add(String.valueOf(VisitInformationDetailType.CREATE_ORDER.getValue()));
          sql = sql.concat(" ").concat(
              " AND ( o." + SaleOrder.COL_STATUS + " = ? OR o." + SaleOrder.COL_STATUS + " = ? ) ");
          argsList.add(String.valueOf(statusId));
          argsList.add(String.valueOf(SaleOrderStatus.SENT.getId()));
        } else if (statusId.equals(SaleOrderStatus.DELIVERED.getId())) {
          argsList.add(String.valueOf(VisitInformationDetailType.DELIVER_ORDER.getValue()));
          sql = sql.concat(" AND ( o." + SaleOrder.COL_STATUS + " = ? OR o."
              + SaleOrder.COL_STATUS + " = ? OR o." + SaleOrder.COL_STATUS + " = ?) ");
          argsList.add(String.valueOf(statusId));
          argsList.add(String.valueOf(SaleOrderStatus.DELIVERABLE_SENT.getId()));
          argsList.add(String.valueOf(SaleOrderStatus.CANCELED.getId()));
        } else if (statusId.equals(SaleOrderStatus.DELIVERABLE.getId())) {
          argsList.add("0");
          sql = sql.concat(" AND ( o." + SaleOrder.COL_STATUS + " = ? OR o."
              + SaleOrder.COL_STATUS + " = ? OR o." + SaleOrder.COL_STATUS
              + " = ? OR o." + SaleOrder.COL_STATUS + " = ?) ");
          argsList.add(String.valueOf(statusId));
          argsList.add(String.valueOf(SaleOrderStatus.DELIVERED.getId()));
          argsList.add(String.valueOf(SaleOrderStatus.DELIVERABLE_SENT.getId()));
          argsList.add(String.valueOf(SaleOrderStatus.CANCELED.getId()));
        } else if (statusId.equals(SaleOrderStatus.REJECTED.getId())) {
          argsList.add(String.valueOf(VisitInformationDetailType.CREATE_REJECT.getValue()));
          sql = sql.concat(" ").concat(
              " AND ( o." + SaleOrder.COL_STATUS + " = ? OR o." + SaleOrder.COL_STATUS + " = ? ) ");
          argsList.add(String.valueOf(statusId));
          argsList.add(String.valueOf(SaleOrderStatus.REJECTED_SENT.getId()));
        } else {
          argsList.add("0");//DO NOT JOIN WITH VD
          sql = sql.concat(" ").concat(" AND o." + SaleOrder.COL_STATUS + " = ?");
          argsList.add(String.valueOf(statusId));
        }
      }

      if (Empty.isNotEmpty(saleOrderSO.getCustomerBackendId())) {
        sql = sql.concat(" ").concat(" AND o." + SaleOrder.COL_CUSTOMER_BACKEND_ID + " = ?");
        argsList.add(String.valueOf(saleOrderSO.getCustomerBackendId()));
      }

      if (saleOrderSO.isIgnoreDraft()) {
        sql = sql.concat(" ").concat(" AND o." + SaleOrder.COL_STATUS + " != ?")
            .concat(" AND o." + SaleOrder.COL_STATUS + " != ?");
        argsList.add(String.valueOf(SaleOrderStatus.DRAFT.getId()));
        argsList.add(String.valueOf(SaleOrderStatus.REJECTED_DRAFT.getId()));
      }
    }

    String[] args = {};
    Cursor cursor = db.rawQuery(sql, argsList.toArray(args));

    while (cursor.moveToNext()) {
      SaleOrderListModel order = new SaleOrderListModel();
      order.setId(cursor.getLong(0));
      order.setBackendId(cursor.getLong(1));
      order.setDate(cursor.getString(2));
      order.setAmount(cursor.getLong(3));
      order.setStatus(cursor.getLong(4));
      order.setCustomerName(cursor.getString(5));
      order.setPaymentTypeTitle(cursor.getString(6));
      order.setCustomerBackendId(cursor.getLong(7));
      order.setCreatedDateTime(cursor.getString(8));
      order.setDescription(cursor.getString(9));
      order.setVisitId(cursor.getLong(10));
      order.setNumber(cursor.getLong(11));
      returnOrders.add(order);
    }

    cursor.close();
    return returnOrders;
  }

  @Override
  public SaleOrderDto getOrderDtoById(Long orderId) {
    CommerDatabaseHelper databaseHelper = CommerDatabaseHelper.getInstance(getContext());
    SQLiteDatabase db = databaseHelper.getReadableDatabase();
    String selection = getPrimaryKeyColumnName() + " = ?";
    String[] args = {String.valueOf(orderId)};
    Cursor cursor = db.query(getTableName(), getProjection(), selection, args, null, null, null);
    SaleOrderDto orderDto = null;
    if (cursor.moveToFirst()) {
      orderDto = createOrderDtoFromCursor(cursor);
    }
    cursor.close();
    return orderDto;
  }

  @Override
  public SaleOrderDto getOrderDtoByCustomerBackendIdAndStatus(Long customerBackendId,
      Long statusId) {
    CommerDatabaseHelper databaseHelper = CommerDatabaseHelper.getInstance(getContext());
    SQLiteDatabase db = databaseHelper.getReadableDatabase();
    String selection =
        SaleOrder.COL_CUSTOMER_BACKEND_ID + " = ? AND " + SaleOrder.COL_STATUS + " = ?";
    String[] args = {String.valueOf(customerBackendId), String.valueOf(statusId)};
    Cursor cursor = db.query(getTableName(), getProjection(), selection, args, null, null, null);
    SaleOrderDto orderDto = null;
    if (cursor.moveToFirst()) {
      orderDto = createOrderDtoFromCursor(cursor);
    }
    cursor.close();
    return orderDto;
  }

  @Override
  public List<BaseSaleDocument> findOrderDocumentsByStatusId(Long statusId) {
    CommerDatabaseHelper databaseHelper = CommerDatabaseHelper.getInstance(getContext());
    SQLiteDatabase db = databaseHelper.getReadableDatabase();
    String selection = SaleOrder.COL_STATUS + " = ?";
    String[] args = {String.valueOf(statusId)};
    Cursor cursor = db.query(getTableName(), getProjection(), selection, args, null, null, null);

    List<BaseSaleDocument> saleOrderList = new ArrayList<>();

    while (cursor.moveToNext()) {
      saleOrderList.add(createSaleDocumentFromCursor(cursor));
    }

    cursor.close();
    return saleOrderList;
  }

  @Override
  public BaseSaleDocument findOrderDocumentByOrderId(Long orderId) {
    CommerDatabaseHelper databaseHelper = CommerDatabaseHelper.getInstance(getContext());
    SQLiteDatabase db = databaseHelper.getReadableDatabase();
    String selection = SaleOrder.COL_ID + " = ? AND ("
        + SaleOrder.COL_STATUS + " = ? OR "
        + SaleOrder.COL_STATUS + " = ? OR "
        + SaleOrder.COL_STATUS + " = ? OR "
        + SaleOrder.COL_STATUS + " = ? OR "
        + SaleOrder.COL_STATUS + " = ? OR "
        + SaleOrder.COL_STATUS + " = ? )";
    String[] args = {String.valueOf(orderId),
        String.valueOf(SaleOrderStatus.READY_TO_SEND.getId()),
        String.valueOf(SaleOrderStatus.REJECTED.getId()),
        String.valueOf(SaleOrderStatus.INVOICED.getId()),
        String.valueOf(SaleOrderStatus.DELIVERED.getId()),
        String.valueOf(SaleOrderStatus.CANCELED.getId()),
        String.valueOf(SaleOrderStatus.GIFT.getId())
    };
    Cursor cursor = db.query(getTableName(), getProjection(), selection, args, null, null, null);

    BaseSaleDocument baseSaleDocument = null;

    if (cursor.moveToNext()) {
      baseSaleDocument = createSaleDocumentFromCursor(cursor);
    }

    cursor.close();
    return baseSaleDocument;
  }

  @Override
  public void deleteByCustomerBackendIdAndStatus(Long customerBackendId, Long statusId) {
    CommerDatabaseHelper databaseHelper = CommerDatabaseHelper.getInstance(getContext());
    SQLiteDatabase db = databaseHelper.getWritableDatabase();
    String sql =
        " DELETE FROM " + SaleOrder.TABLE_NAME + " WHERE " + SaleOrder.COL_CUSTOMER_BACKEND_ID
            + " = ? AND " +
            " " + SaleOrder.COL_STATUS + " = ?";
    String[] args = {String.valueOf(customerBackendId), String.valueOf(statusId)};
    db.rawQuery(sql, args);
  }
}
