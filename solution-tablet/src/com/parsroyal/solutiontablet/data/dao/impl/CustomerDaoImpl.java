package com.parsroyal.solutiontablet.data.dao.impl;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import com.parsroyal.solutiontablet.constants.CustomerStatus;
import com.parsroyal.solutiontablet.constants.VisitInformationDetailType;
import com.parsroyal.solutiontablet.data.dao.CustomerDao;
import com.parsroyal.solutiontablet.data.entity.BaseInfo;
import com.parsroyal.solutiontablet.data.entity.Customer;
import com.parsroyal.solutiontablet.data.entity.VisitInformation;
import com.parsroyal.solutiontablet.data.entity.VisitInformationDetail;
import com.parsroyal.solutiontablet.data.helper.CommerDatabaseHelper;
import com.parsroyal.solutiontablet.data.listmodel.CustomerListModel;
import com.parsroyal.solutiontablet.data.listmodel.NCustomerListModel;
import com.parsroyal.solutiontablet.data.model.CustomerDto;
import com.parsroyal.solutiontablet.data.model.CustomerLocationDto;
import com.parsroyal.solutiontablet.data.model.PositionModel;
import com.parsroyal.solutiontablet.data.searchobject.NCustomerSO;
import com.parsroyal.solutiontablet.util.DateUtil;
import com.parsroyal.solutiontablet.util.Empty;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Mahyar on 6/19/2015.
 */
public class CustomerDaoImpl extends AbstractDao<Customer, Long> implements CustomerDao {

  private Context context;

  public CustomerDaoImpl(Context context) {
    this.context = context;
  }

  @Override
  protected Context getContext() {
    return context;
  }

  @Override
  protected ContentValues getContentValues(Customer entity) {
    ContentValues contentValues = new ContentValues();
    contentValues.put(Customer.COL_ID, entity.getId());
    contentValues.put(Customer.COL_BACKEND_ID, entity.getBackendId());
    contentValues.put(Customer.COL_FULL_NAME, entity.getFullName());
    contentValues.put(Customer.COL_PHONE_NUMBER, entity.getPhoneNumber());
    contentValues.put(Customer.COL_CELL_PHONE, entity.getCellPhone());
    contentValues.put(Customer.COL_PROVINCE_BACKEND_ID, entity.getProvinceBackendId());//5
    contentValues.put(Customer.COL_CITY_BACKEND_ID, entity.getCityBackendId());
    contentValues.put(Customer.COL_ADDRESS, entity.getAddress());
    contentValues.put(Customer.COL_ACTIVITY_BACKEND_ID, entity.getActivityBackendId());
    contentValues.put(Customer.COL_STORE_SURFACE, entity.getStoreSurface());
    contentValues.put(Customer.COL_STORE_LOCATION_TYPE_BACKEND_ID,
        entity.getStoreLocationTypeBackendId());//10
    contentValues.put(Customer.COL_CLASS_BACKEND_ID, entity.getClassBackendId());
    contentValues.put(Customer.COL_STATUS, entity.getStatus());
    contentValues.put(Customer.COL_CODE, entity.getCode());
    contentValues.put(Customer.COL_VISIT_LINE_BACKEND_ID, entity.getVisitLineBackendId());
    contentValues.put(Customer.COL_CREATE_DATE_TIME, entity.getCreateDateTime());//15
    contentValues.put(Customer.COL_UPDATE_DATE_TIME, entity.getUpdateDateTime());
    contentValues.put(Customer.COL_X_LOCATION, entity.getxLocation());
    contentValues.put(Customer.COL_Y_LOCATION, entity.getyLocation());//18
    contentValues.put(Customer.COL_SHOP_NAME, entity.getShopName());//19
    contentValues.put(Customer.COL_NATIONAL_CODE, entity.getNationalCode());//20
    contentValues.put(Customer.COL_MUNICIPALITY_CODE, entity.getMunicipalityCode());//21
    contentValues.put(Customer.COL_POSTAL_CODE, entity.getPostalCode());//22
    contentValues.put(Customer.COL_APPROVED, entity.isApproved() ? 1 : 0);
    return contentValues;
  }

  @Override
  protected String getTableName() {
    return Customer.TABLE_NAME;
  }

  @Override
  protected String getPrimaryKeyColumnName() {
    return Customer.COL_ID;
  }

  @Override
  protected String[] getProjection() {
    String[] projection = {
        Customer.COL_ID,
        Customer.COL_BACKEND_ID,
        Customer.COL_FULL_NAME,
        Customer.COL_PHONE_NUMBER,
        Customer.COL_CELL_PHONE,
        Customer.COL_PROVINCE_BACKEND_ID,//5
        Customer.COL_CITY_BACKEND_ID,
        Customer.COL_ADDRESS,
        Customer.COL_ACTIVITY_BACKEND_ID,
        Customer.COL_STORE_SURFACE,
        Customer.COL_STORE_LOCATION_TYPE_BACKEND_ID,//10
        Customer.COL_CLASS_BACKEND_ID,
        Customer.COL_STATUS,
        Customer.COL_CODE,
        Customer.COL_VISIT_LINE_BACKEND_ID,
        Customer.COL_CREATE_DATE_TIME,//15
        Customer.COL_UPDATE_DATE_TIME,
        Customer.COL_X_LOCATION,
        Customer.COL_Y_LOCATION,//18
        Customer.COL_SHOP_NAME,
        Customer.COL_NATIONAL_CODE,
        Customer.COL_MUNICIPALITY_CODE,
        Customer.COL_POSTAL_CODE,//22
        Customer.COL_APPROVED
    };
    return projection;
  }

  @Override
  protected Customer createEntityFromCursor(Cursor cursor) {
    Customer customer = new Customer();
    customer.setId(cursor.getLong(0));
    customer.setBackendId(cursor.getLong(1));
    customer.setFullName(cursor.getString(2));
    customer.setPhoneNumber(cursor.getString(3));
    customer.setCellPhone(cursor.getString(4));
    customer.setProvinceBackendId(cursor.getLong(5));
    customer.setCityBackendId(cursor.getLong(6));
    customer.setAddress(cursor.getString(7));
    customer.setActivityBackendId(cursor.getLong(8));
    customer.setStoreSurface(cursor.getInt(9));
    customer.setStoreLocationTypeBackendId(cursor.getLong(10));
    customer.setClassBackendId(cursor.getLong(11));
    customer.setStatus(cursor.getLong(12));
    customer.setCode(cursor.getString(13));
    customer.setVisitLineBackendId(cursor.getLong(14));
    customer.setCreateDateTime(cursor.getString(15));
    customer.setUpdateDateTime(cursor.getString(16));
    customer.setxLocation(cursor.getDouble(17));
    customer.setyLocation(cursor.getDouble(18));
    customer.setShopName(cursor.getString(19));
    customer.setNationalCode(cursor.getString(20));
    customer.setMunicipalityCode(cursor.getString(21));
    customer.setPostalCode(cursor.getString(22));
    customer.setApproved(cursor.getInt(23) == 1);
    return customer;
  }

  @Override
  public List<Customer> retrieveAllNewCustomersForSend() {
    String selection =
        " " + Customer.COL_STATUS + " = ? " + " AND (" + Customer.COL_BACKEND_ID + " is null or "
            + Customer.COL_BACKEND_ID + " = 0)";
    String[] args = {String.valueOf(CustomerStatus.NEW.getId())};
    return retrieveAll(selection, args, null, null, null);
  }

  @Override
  public List<Customer> retrieveAllNewCustomers() {
    String selection = " " + Customer.COL_STATUS + " = ? ";
    String[] args = {String.valueOf(CustomerStatus.NEW.getId())};
    return retrieveAll(selection, args, null, null, null);
  }

  @Override
  public List<CustomerLocationDto> retrieveAllUpdatedCustomerLocationDto() {
    CommerDatabaseHelper databaseHelper = CommerDatabaseHelper.getInstance(getContext());
    SQLiteDatabase db = databaseHelper.getReadableDatabase();
    String[] projection = {
        Customer.COL_BACKEND_ID,
        Customer.COL_STATUS,
        Customer.COL_X_LOCATION,
        Customer.COL_Y_LOCATION
    };
    String selection = Customer.COL_STATUS + " = ?";
    String[] args = {String.valueOf(CustomerStatus.UPDATED.getId())};
    Cursor cursor = db.query(getTableName(), projection, selection, args, null, null, null);
    List<CustomerLocationDto> locationDtoList = new ArrayList<>();
    while (cursor.moveToNext()) {
      CustomerLocationDto locationDto = new CustomerLocationDto();
      locationDto.setCustomerBackendId(cursor.getLong(0));
      locationDto.setLatitude(cursor.getDouble(2));
      locationDto.setLongitude(cursor.getDouble(3));
      locationDtoList.add(locationDto);
    }
    cursor.close();
    return locationDtoList;
  }

  @Override
  public void deleteAllCustomersRelatedToVisitLines() {
    String selection = " " + Customer.COL_VISIT_LINE_BACKEND_ID + " is not null";
    delete(selection, null);
  }

  @Override
  public List<Customer> retrieveAllCustomersByVisitLineBackendId(Long visitLineId) {
    String selection = " " + Customer.COL_VISIT_LINE_BACKEND_ID + " = ? ";
    String[] args = {String.valueOf(visitLineId)};
    return retrieveAll(selection, args, null, null, null);
  }

  @Override
  public List<CustomerListModel> getAllCustomersListModelByVisitLineWithConstraint(Long visitLineId,
      String constraint) {
    CommerDatabaseHelper databaseHelper = CommerDatabaseHelper.getInstance(getContext());
    SQLiteDatabase db = databaseHelper.getReadableDatabase();
    String[] projection = {
        "c." + Customer.COL_ID,
        "c." + Customer.COL_CODE,
        "c." + Customer.COL_FULL_NAME,
        "c." + Customer.COL_PHONE_NUMBER,
        "c." + Customer.COL_CELL_PHONE,
        "c." + Customer.COL_ADDRESS,//5
        "c." + Customer.COL_X_LOCATION,
        "c." + Customer.COL_Y_LOCATION,
        "vi." + VisitInformation.COL_UPDATE_DATE_TIME,
        "vd." + VisitInformationDetail.COL_TYPE,
        "vi." + VisitInformation.COL_VISIT_DATE,//10
        "c." + Customer.COL_BACKEND_ID};

    String table = getTableName() + " c " +
        "LEFT OUTER JOIN COMMER_VISIT_INFORMATION vi on c." + Customer.COL_BACKEND_ID + " = vi."
        + VisitInformation.COL_CUSTOMER_BACKEND_ID +
        " LEFT OUTER JOIN COMMER_VISIT_INFORMATION_DETAIL vd on vi._id = vd.VISIT_INFORMATION_ID ";

    String orderBy = "c." + Customer.COL_ID + " ";

    String selection = "";
    String[] args = null;
    if (Empty.isNotEmpty(visitLineId)) {
      //Load all customers for Map
      selection = " c." + Customer.COL_VISIT_LINE_BACKEND_ID + " = ? ";
      args = new String[]{String.valueOf(visitLineId)};
    } else {
      selection =
          "c." + Customer.COL_X_LOCATION + " is not null AND " + "c." + Customer.COL_X_LOCATION
              + " != 0";
    }

    Cursor cursor;
    if (Empty.isNotEmpty(constraint)) {
      selection = selection + " and ( " +
          "c." + Customer.COL_FULL_NAME + " like ? or " + "c." + Customer.COL_ADDRESS
          + " like ? or " +
          "c." + Customer.COL_PHONE_NUMBER + " like ? or c." + Customer.COL_CELL_PHONE
          + " like ? or c." + Customer.COL_CODE + " like ? )";
      constraint = "%" + constraint + "%";
      String[] args2 = {String.valueOf(visitLineId), constraint, constraint, constraint, constraint,
          constraint};
      cursor = db.query(table, projection, selection, args2, null, null, orderBy);
    } else {
      cursor = db.query(table, projection, selection, args, null, null, orderBy);
    }

    Map<Long, CustomerListModel> entitiesMap = new HashMap<>();

    while (cursor.moveToNext()) {
      CustomerListModel listModel = createListModelFromCursor(cursor);
      if (entitiesMap.containsKey(listModel.getPrimaryKey())) {
        if ((listModel.hasOrder() || listModel.hasRejection())) {
          entitiesMap.put(listModel.getPrimaryKey(), listModel);
        }
      } else {
        entitiesMap.put(listModel.getPrimaryKey(), listModel);
      }
    }
    cursor.close();
    return new ArrayList<>(entitiesMap.values());
  }

  private CustomerListModel createListModelFromCursor(Cursor cursor) {
    CustomerListModel customerListModel = new CustomerListModel();
    customerListModel.setPrimaryKey(cursor.getLong(0));
    customerListModel.setCode(cursor.getString(1));
    customerListModel.setCodeNumber(cursor.getLong(1));
    customerListModel.setTitle(cursor.getString(2));
    customerListModel.setPhoneNumber(cursor.getString(3));
    customerListModel.setCellPhone(cursor.getString(4));
    customerListModel.setAddress(cursor.getString(5));
    String location = cursor.getString(6);

    customerListModel.setHasLocation(Empty.isNotEmpty(location) && !location.equals("0"));
    if (customerListModel.hasLocation()) {
      customerListModel.setXlocation(cursor.getDouble(6));
      customerListModel.setYlocation(cursor.getDouble(7));
    } else {
      customerListModel.setXlocation(0.0);
      customerListModel.setYlocation(0.0);
    }

    String visitDate = cursor.getString(10);

    String today = DateUtil.convertDate(new Date(), DateUtil.GLOBAL_FORMATTER, "FA");

    customerListModel.setVisited(visitDate != null && visitDate.equals(today));

    int type = cursor.getInt(9);
    if (type == VisitInformationDetailType.CREATE_ORDER.getValue()) {
      customerListModel.setHasOrder(true);
    } else if (type == VisitInformationDetailType.NONE.getValue()
        || type == VisitInformationDetailType.NO_ORDER.getValue()) {
      customerListModel.setHasRejection(true);
    }

    customerListModel.setLastVisit(cursor.getString(10));
    customerListModel.setBackendId(cursor.getLong(11));

    return customerListModel;
  }

  @Override
  public CustomerDto getCustomerDtoById(Long customerId) {
    CommerDatabaseHelper databaseHelper = CommerDatabaseHelper.getInstance(getContext());
    SQLiteDatabase db = databaseHelper.getReadableDatabase();
    String[] projection = {
        " cu." + Customer.COL_ID,
        " cu." + Customer.COL_BACKEND_ID,
        " cu." + Customer.COL_FULL_NAME,
        " cu." + Customer.COL_PHONE_NUMBER,
        " cu." + Customer.COL_CELL_PHONE,
        " cu." + Customer.COL_PROVINCE_BACKEND_ID, //5
        " cu." + Customer.COL_CITY_BACKEND_ID,
        " cu." + Customer.COL_ADDRESS,
        " cu." + Customer.COL_ACTIVITY_BACKEND_ID,
        " cu." + Customer.COL_STORE_SURFACE,
        " cu." + Customer.COL_STORE_LOCATION_TYPE_BACKEND_ID,//10
        " cu." + Customer.COL_STORE_LOCATION_TYPE_BACKEND_ID,
        " cu." + Customer.COL_STATUS,
        " cu." + Customer.COL_CODE,
        " cu." + Customer.COL_VISIT_LINE_BACKEND_ID,
        " cu." + Customer.COL_CREATE_DATE_TIME,//15
        " cu." + Customer.COL_UPDATE_DATE_TIME,
        " cu." + Customer.COL_X_LOCATION,
        " cu." + Customer.COL_Y_LOCATION,
        " bi." + BaseInfo.COL_TITLE + " activityTitle",
        " cu." + Customer.COL_SHOP_NAME, //20
        " cu." + Customer.COL_NATIONAL_CODE,
        " cu." + Customer.COL_MUNICIPALITY_CODE,
        " cu." + Customer.COL_POSTAL_CODE,
        " cu." + Customer.COL_APPROVED
    };
    String selection = " cu." + getPrimaryKeyColumnName() + " = ?";
    String[] args = {String.valueOf(customerId)};
    String table = getTableName() + " cu " +
        " LEFT OUTER JOIN " + BaseInfo.TABLE_NAME + " bi on bi." + BaseInfo.COL_ID + " = cu."
        + Customer.COL_ACTIVITY_BACKEND_ID;
    Cursor cursor = db.query(table, projection, selection, args, null, null, null);
    CustomerDto customerDto = null;
    if (cursor.moveToFirst()) {
      customerDto = createCustomerDtoFromCursor(cursor);
    }
    cursor.close();
    return customerDto;
  }

  @Override
  public List<NCustomerListModel> searchForNCustomers(NCustomerSO nCustomerSO) {
    CommerDatabaseHelper databaseHelper = CommerDatabaseHelper.getInstance(getContext());
    SQLiteDatabase db = databaseHelper.getReadableDatabase();

    String[] projection = {Customer.COL_ID, Customer.COL_FULL_NAME, Customer.COL_PHONE_NUMBER,
        Customer.COL_CELL_PHONE, Customer.COL_STATUS, Customer.COL_CREATE_DATE_TIME,
        Customer.COL_BACKEND_ID};
    String selection = " 1 = 1";
    List<String> argList = new ArrayList<>();

    selection = selection.concat(" AND " + Customer.COL_STATUS + " = ?");
    argList.add(String.valueOf(CustomerStatus.NEW.getId()));

    String constraint = nCustomerSO.getConstraint();
    if (Empty.isNotEmpty(constraint)) {
      selection = selection.concat(" AND (" + Customer.COL_FULL_NAME + " LIKE ? OR");
      argList.add(constraint);
      selection = selection.concat(" " + Customer.COL_ADDRESS + " LIKE ? )");
      argList.add(constraint);
    }

    if (Empty.isNotEmpty(nCustomerSO.getSent())) {
      if (nCustomerSO.getSent().equals(Integer.valueOf(1))) {
        selection = selection.concat(
            " AND " + Customer.COL_BACKEND_ID + " is not null AND " + Customer.COL_BACKEND_ID +
                " != 0");
      } else {
        selection = selection
            .concat(" AND " + Customer.COL_BACKEND_ID + " is  null OR " + Customer.COL_BACKEND_ID +
                " = 0");
      }
    }

    String orderBy = " " + Customer.COL_CREATE_DATE_TIME + " DESC";
    String[] args = new String[argList.size()];
    Cursor cursor = db
        .query(Customer.TABLE_NAME, projection, selection, argList.toArray(args), null, null,
            orderBy);
    List<NCustomerListModel> nCustomers = new ArrayList<>();
    while (cursor.moveToNext()) {
      NCustomerListModel nCustomer = new NCustomerListModel();
      nCustomer.setPrimaryKey(cursor.getLong(0));
      nCustomer.setTitle(cursor.getString(1));
      nCustomer.setPhoneNumber(cursor.getString(2));
      nCustomer.setCellPhone(cursor.getString(3));
      nCustomer.setStatus(cursor.getLong(4));
      nCustomer.setCreateDateTime(cursor.getString(5));
      nCustomer.setBackendId(cursor.getLong(6));
      nCustomers.add(nCustomer);
    }

    cursor.close();
    return nCustomers;
  }

  @Override
  public List<Customer> getCustomersVisitLineBackendId(Long visitLineBackendId) {
    CommerDatabaseHelper databaseHelper = CommerDatabaseHelper.getInstance(getContext());
    SQLiteDatabase db = databaseHelper.getReadableDatabase();
    String selection = " " + Customer.COL_VISIT_LINE_BACKEND_ID + " = ? ";
    String[] args = {String.valueOf(visitLineBackendId)};

    Cursor cursor = db.query(getTableName(), getProjection(), selection, args, null, null, null);

    List<Customer> entities = new ArrayList<>();
    while (cursor.moveToNext()) {
      entities.add(createEntityFromCursor(cursor));
    }
    cursor.close();
    return entities;
  }

  @Override
  public Customer retrieveByBackendId(Long customerBackendId) {
    CommerDatabaseHelper databaseHelper = CommerDatabaseHelper.getInstance(getContext());
    SQLiteDatabase db = databaseHelper.getReadableDatabase();
    String selection = Customer.COL_BACKEND_ID + " = ?";
    String[] args = {String.valueOf(customerBackendId)};
    Cursor cursor = db.query(getTableName(), getProjection(), selection, args, null, null, null);
    Customer entity = null;
    if (cursor.moveToFirst()) {
      entity = createEntityFromCursor(cursor);
    }
    cursor.close();
    return entity;
  }

  @Override
  public void updateAllSentCustomer() {
    CommerDatabaseHelper databaseHelper = CommerDatabaseHelper.getInstance(getContext());
    SQLiteDatabase db = databaseHelper.getWritableDatabase();
    db.beginTransaction();
    String whereClause = Customer.COL_STATUS + " = ?";
    String[] args = {String.valueOf(CustomerStatus.UPDATED.getId())};
    ContentValues contentValues = new ContentValues();
    contentValues.put(Customer.COL_STATUS, CustomerStatus.SENT.getId());
    db.update(getTableName(), contentValues, whereClause, args);
    db.setTransactionSuccessful();
    db.endTransaction();
  }

  @Override
  public List<PositionModel> getAllCusromerPostionModel(NCustomerSO nCustomerSO) {
    if (nCustomerSO != null) {

    }

    CommerDatabaseHelper databaseHelper = CommerDatabaseHelper.getInstance(getContext());
    SQLiteDatabase db = databaseHelper.getReadableDatabase();
    String selection =
        Customer.COL_X_LOCATION + " is not null AND " + Customer.COL_X_LOCATION + " != 0";
    Cursor cursor = db.query(getTableName(), getProjection(), selection, null, null, null, null);
    List<PositionModel> positionModelList = new ArrayList<>();

    while (cursor.moveToNext()) {
      positionModelList.add(new PositionModel(createEntityFromCursor(cursor)));
    }
    cursor.close();
    return positionModelList;
  }

  protected CustomerDto createCustomerDtoFromCursor(Cursor cursor) {
    CustomerDto customer = new CustomerDto();
    customer.setId(cursor.getLong(0));
    customer.setBackendId(cursor.getLong(1));
    customer.setFullName(cursor.getString(2));
    customer.setPhoneNumber(cursor.getString(3));
    customer.setCellPhone(cursor.getString(4));
    customer.setProvinceBackendId(cursor.getLong(5));
    customer.setCityBackendId(cursor.getLong(6));
    customer.setAddress(cursor.getString(7));
    customer.setActivityBackendId(cursor.getLong(8));
    customer.setStoreSurface(cursor.getInt(9));
    customer.setStoreLocationTypeBackendId(cursor.getLong(10));
    customer.setClassBackendId(cursor.getLong(11));
    customer.setStatus(cursor.getLong(12));
    customer.setCode(cursor.getString(13));
    customer.setVisitLineBackendId(cursor.getLong(14));
    customer.setCreateDateTime(cursor.getString(15));
    customer.setUpdateDateTime(cursor.getString(16));
    customer.setxLocation(cursor.getDouble(17));
    customer.setyLocation(cursor.getDouble(18));
    customer.setActivityTitle(cursor.getString(19));
    customer.setShopName(cursor.getString(20));
    customer.setNationalCode(cursor.getString(21));
    customer.setMunicipalityCode(cursor.getString(22));
    customer.setPostalCode(cursor.getString(23));
    customer.setApproved(cursor.getInt(24) == 1);
    return customer;
  }
}