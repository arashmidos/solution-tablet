package com.parsroyal.solutiontablet.data.dao.impl;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import com.parsroyal.solutiontablet.constants.CustomerStatus;
import com.parsroyal.solutiontablet.constants.VisitInformationDetailType;
import com.parsroyal.solutiontablet.data.dao.CustomerDao;
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
import com.parsroyal.solutiontablet.util.CharacterFixUtil;
import com.parsroyal.solutiontablet.util.DateUtil;
import com.parsroyal.solutiontablet.util.Empty;
import com.parsroyal.solutiontablet.util.NumberUtil;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Arash on 1/16/2018.
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
    contentValues.put(Customer.COL_Y_LOCATION, entity.getyLocation());
    contentValues.put(Customer.COL_SHOP_NAME, entity.getShopName());
    contentValues.put(Customer.COL_NATIONAL_CODE, entity.getNationalCode());//20
    contentValues.put(Customer.COL_MUNICIPALITY_CODE, entity.getMunicipalityCode());
    contentValues.put(Customer.COL_POSTAL_CODE, entity.getPostalCode());
    contentValues.put(Customer.COL_APPROVED, entity.isApproved() ? 1 : 0);
    contentValues.put(Customer.COL_DESCRIPTION, entity.getDescription());
    contentValues.put(Customer.COL_REMAINED_CREDIT,
        entity.getRemainedCredit());//25
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
    return new String[]{
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
        Customer.COL_Y_LOCATION,
        Customer.COL_SHOP_NAME,
        Customer.COL_NATIONAL_CODE,//20
        Customer.COL_MUNICIPALITY_CODE,
        Customer.COL_POSTAL_CODE,
        Customer.COL_APPROVED,
        Customer.COL_DESCRIPTION,
        Customer.COL_REMAINED_CREDIT//25
    };
  }

  private CustomerDto createCustomerDtoFromCursor(Cursor cursor) {
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
    customer.setShopName(cursor.getString(19));
    customer.setNationalCode(cursor.getString(20));
    customer.setMunicipalityCode(cursor.getString(21));
    customer.setPostalCode(cursor.getString(22));
    customer.setApproved(cursor.getInt(23) == 1);
    customer.setCustomerDescription(cursor.getString(24));
    customer.setRemainedCredit(cursor.getDouble(25));
    return customer;
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
    customer.setDescription(cursor.getString(24));
    customer.setRemainedCredit(cursor.getDouble(25));
    return customer;
  }

  @Override
  public List<CustomerDto> retrieveAllNewCustomersForSend() {
    CommerDatabaseHelper databaseHelper = CommerDatabaseHelper.getInstance(getContext());
    SQLiteDatabase db = databaseHelper.getReadableDatabase();

    String selection = " " + Customer.COL_STATUS + " = ? " + " OR " + Customer.COL_STATUS + " = ? ";
    String[] args = {String.valueOf(CustomerStatus.NEW.getId()),
        String.valueOf(CustomerStatus.UPDATED.getId())};

    Cursor cursor = db.query(getTableName(), getProjection(), selection, args, null, null, null);
    List<CustomerDto> list = new ArrayList<>();

    while (cursor.moveToNext()) {
      list.add(createCustomerDtoFromCursor(cursor));
    }
    cursor.close();

    return list;
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
    String[] args = {String.valueOf(CustomerStatus.UPDATED_LOCATION.getId())};
    Cursor cursor = db.query(getTableName(), projection, selection, args, null, null, null);
    List<CustomerLocationDto> locationDtoList = new ArrayList<>();
    while (cursor.moveToNext()) {
      locationDtoList.add(new CustomerLocationDto(cursor.getLong(0), cursor.getDouble(2),
          cursor.getDouble(3)));
    }
    cursor.close();
    return locationDtoList;
  }

  @Override
  public List<CustomerLocationDto> retrieveAllCustomersLocationDto(long visitlineBackendId) {
    CommerDatabaseHelper databaseHelper = CommerDatabaseHelper.getInstance(getContext());
    SQLiteDatabase db = databaseHelper.getReadableDatabase();
    String[] projection = {
        Customer.COL_BACKEND_ID,
        Customer.COL_X_LOCATION,
        Customer.COL_Y_LOCATION
    };
    String selection = Customer.COL_VISIT_LINE_BACKEND_ID + " = ? AND "
        + Customer.COL_X_LOCATION + " is not null AND " + Customer.COL_X_LOCATION + " != 0";

    String[] args = {String.valueOf(visitlineBackendId)};
    Cursor cursor = db.query(getTableName(), projection, selection, args, null, null, null);
    List<CustomerLocationDto> locationDtoList = new ArrayList<>();
    while (cursor.moveToNext()) {
      locationDtoList.add(new CustomerLocationDto(cursor.getLong(0), cursor.getDouble(1),
          cursor.getDouble(2)));
    }
    cursor.close();
    return locationDtoList;
  }

  @Override
  public CustomerLocationDto findCustomerLocationDtoByCustomerBackendId(Long customerBackendId) {
    CommerDatabaseHelper databaseHelper = CommerDatabaseHelper.getInstance(getContext());
    SQLiteDatabase db = databaseHelper.getReadableDatabase();
    String[] projection = {
        Customer.COL_BACKEND_ID,
        Customer.COL_STATUS,
        Customer.COL_X_LOCATION,
        Customer.COL_Y_LOCATION
    };
    String selection = Customer.COL_STATUS + " = ? AND " + Customer.COL_BACKEND_ID + " = ? ";
    String[] args = {String.valueOf(CustomerStatus.UPDATED_LOCATION.getId()),
        String.valueOf(customerBackendId)};
    Cursor cursor = db.query(getTableName(), projection, selection, args, null, null, null);
    CustomerLocationDto locationDto = null;
    if (cursor.moveToNext()) {
      locationDto = new CustomerLocationDto(cursor.getLong(0), cursor.getDouble(2),
          cursor.getDouble(3));
    }
    cursor.close();
    return locationDto;
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
  public Customer retrieveCustomerByVisitLineBackendId(Long customerBackendId, Long visitLineId) {
    String selection =
        " " + Customer.COL_BACKEND_ID + " = ? AND " + Customer.COL_VISIT_LINE_BACKEND_ID + " = ? ";
    String[] args = {String.valueOf(customerBackendId), String.valueOf(visitLineId)};
    List<Customer> all = retrieveAll(selection, args, null, null, null);
    if (all.size() > 0) {
      return all.get(0);
    } else {
      return null;
    }
  }

  @Override
  public List<CustomerListModel> getAllCustomersListModelByVisitLineWithConstraint(Long visitLineId,
      String constraint, boolean showOnMap) {
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
        "c." + Customer.COL_BACKEND_ID,//11
        "c." + Customer.COL_SHOP_NAME,//12
        "c." + Customer.COL_VISIT_LINE_BACKEND_ID,//13
        "vi." + VisitInformation.COL_PHONE_VISIT,//14
        "vi." + VisitInformation.COL_END_TIME//15
    };

    String table = getTableName() + " c " +
        "LEFT OUTER JOIN COMMER_VISIT_INFORMATION vi on c." + Customer.COL_BACKEND_ID + " = vi."
        + VisitInformation.COL_CUSTOMER_BACKEND_ID +
        " LEFT OUTER JOIN COMMER_VISIT_INFORMATION_DETAIL vd on vi._id = vd.VISIT_INFORMATION_ID ";

    String orderBy = "c." + Customer.COL_ID + " ASC ";

    String selection = "";
    ArrayList<String> args = new ArrayList<>();
    if (Empty.isNotEmpty(visitLineId)) {

      selection = " c." + Customer.COL_VISIT_LINE_BACKEND_ID + " = ? ";
      args.add(String.valueOf(visitLineId));
    }
    if (showOnMap) {
      //Load all customers has location for Map or ALLCustomerList
      if (!selection.equals("")) {
        selection = selection.concat(" and ");
      }
      selection = selection.concat(
          "c." + Customer.COL_X_LOCATION + " is not null AND " + "c." + Customer.COL_X_LOCATION
              + " != 0");
    }

    Cursor cursor;
    if (Empty.isNotEmpty(constraint)) {
      if (!selection.equals("")) {
        selection = selection + " and ";
      }
      selection = selection + " ( " +
          "c." + Customer.COL_FULL_NAME + " like ? or " + "c." + Customer.COL_ADDRESS
          + " like ? or " +
          "c." + Customer.COL_PHONE_NUMBER + " like ? or c." + Customer.COL_CELL_PHONE
          + " like ? or c." + Customer.COL_CODE + " like ? )";
      constraint = NumberUtil.digitsToEnglish(constraint);
          constraint = "%" + constraint + "%";
      String[] args2 = {constraint, constraint, constraint, constraint, constraint};
      args.addAll(Arrays.asList(args2));
//      cursor = db.query(table, projection, selection, args, null, null, orderBy);
//    } else {
    } else {
      selection =
          selection + (selection.equals("") ? "c." : " and c.") + Customer.COL_STATUS + " <> ? ";
      args.add(String.valueOf(CustomerStatus.NEW.getId()));
    }
    String[] argsArray = new String[args.size()];
    argsArray = args.toArray(argsArray);

    cursor = db.query(table, projection, selection, argsArray, null, null, orderBy);

    Map<Long, CustomerListModel> entitiesMap = new HashMap<>();

    while (cursor.moveToNext()) {
      CustomerListModel listModel = createListModelFromCursor(cursor);
      Long primaryKey = listModel.getPrimaryKey();
      if (!entitiesMap.containsKey(primaryKey)) {
        entitiesMap.put(primaryKey, listModel);
      }
      if ((listModel.hasOrder())) {
        entitiesMap.get(primaryKey).setHasOrder(true);
        entitiesMap.get(primaryKey).addDetail(VisitInformationDetailType.CREATE_ORDER);
      }
      if (listModel.hasRejection()) {
        entitiesMap.get(primaryKey).setHasRejection(true);
      }
      if (listModel.hasNoOrder()) {
        entitiesMap.get(primaryKey).setHasNoOrder(true);
        entitiesMap.get(primaryKey).addDetail(VisitInformationDetailType.NO_ORDER);
      }
      if (listModel.isHasRejectOrder()) {
        entitiesMap.get(primaryKey).sethasRejectOrder(true);
        entitiesMap.get(primaryKey).addDetail(VisitInformationDetailType.CREATE_REJECT);
      }
      if (listModel.hasLocation()) {
        entitiesMap.get(primaryKey).setHasLocation(true);
      }
      if (listModel.hasAnswers()) {
        entitiesMap.get(primaryKey).setHasAnswers(true);
        entitiesMap.get(primaryKey).addDetail(VisitInformationDetailType.FILL_QUESTIONNAIRE);
      }
      if (listModel.isAddLocation()) {
        entitiesMap.get(primaryKey).setAddLocation(true);
        entitiesMap.get(primaryKey).addDetail(VisitInformationDetailType.SAVE_LOCATION);
      }
      if (listModel.isHasPayment()) {
        entitiesMap.get(primaryKey).setHasPayment(true);
        entitiesMap.get(primaryKey).addDetail(VisitInformationDetailType.CASH);
      }
      if (listModel.isHasPicture()) {
        entitiesMap.get(primaryKey).setHasPicture(true);
        entitiesMap.get(primaryKey).addDetail(VisitInformationDetailType.TAKE_PICTURE);
      }
      if (listModel.isHasDelivery()) {
        entitiesMap.get(primaryKey).setHasDelivery(true);
        entitiesMap.get(primaryKey).addDetail(VisitInformationDetailType.DELIVER_ORDER);
      }
      if (listModel.isPhoneVisit()) {
        entitiesMap.get(primaryKey).setPhoneVisit(true);
      }
      if (listModel.hasFreeDelivery()) {
        entitiesMap.get(primaryKey).setHasFreeDelivery(true);
        entitiesMap.get(primaryKey).addDetail(VisitInformationDetailType.DELIVER_FREE_ORDER);
      }
      if (listModel.isIncompleteVisit()) {
        entitiesMap.get(primaryKey).setIncompleteVisit(true);
      }
    }
    cursor.close();
    ArrayList<CustomerListModel> customerListModels = new ArrayList<>(entitiesMap.values());

    Collections.sort(customerListModels,
        (item1, item2) -> item1.getPrimaryKey().compareTo(item2.getPrimaryKey()));
    return customerListModels;
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
    } else if (type == VisitInformationDetailType.NONE.getValue()) {
      customerListModel.setHasRejection(true);
    } else if (type == VisitInformationDetailType.NO_ORDER.getValue()) {
      customerListModel.setHasNoOrder(true);
    } else if (type == VisitInformationDetailType.FILL_QUESTIONNAIRE.getValue()) {
      customerListModel.setHasAnswers(true);
    } else if (type == VisitInformationDetailType.CASH.getValue()) {
      customerListModel.setHasPayment(true);
    } else if (type == VisitInformationDetailType.TAKE_PICTURE.getValue()) {
      customerListModel.setHasPicture(true);
    } else if (type == VisitInformationDetailType.SAVE_LOCATION.getValue()) {
      customerListModel.setAddLocation(true);
    } else if (type == VisitInformationDetailType.CREATE_REJECT.getValue()) {
      customerListModel.sethasRejectOrder(true);
    } else if (type == VisitInformationDetailType.DELIVER_ORDER.getValue()) {
      customerListModel.setHasDelivery(true);
    } else if (type == VisitInformationDetailType.DELIVER_FREE_ORDER.getValue()) {
      customerListModel.setHasFreeDelivery(true);
    }

    customerListModel.setLastVisit(cursor.getString(10));
    customerListModel.setBackendId(cursor.getLong(11));
    customerListModel.setShopName(cursor.getString(12));
    customerListModel.setVisitlineBackendId(cursor.getLong(13));
    customerListModel.setPhoneVisit(cursor.getInt(14) == 1);
    customerListModel.setIncompleteVisit(Empty.isEmpty(cursor.getString(15)));

    return customerListModel;
  }

  @Override
  public CustomerDto getCustomerDtoById(Long customerId) {
    CommerDatabaseHelper databaseHelper = CommerDatabaseHelper.getInstance(getContext());
    SQLiteDatabase db = databaseHelper.getReadableDatabase();

    String selection = getPrimaryKeyColumnName() + " = ?";
    String[] args = {String.valueOf(customerId)};

    Cursor cursor = db.query(getTableName(), getProjection(), selection, args, null, null, null);
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

    String[] projection = {
        Customer.COL_ID,
        Customer.COL_FULL_NAME,
        Customer.COL_PHONE_NUMBER,
        Customer.COL_CELL_PHONE,
        Customer.COL_STATUS,
        Customer.COL_CREATE_DATE_TIME,//5
        Customer.COL_BACKEND_ID,
        Customer.COL_SHOP_NAME
    };
    String selection = " 1=1 ";
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
      if (nCustomerSO.getSent().equals(1)) {
        selection = selection.concat(" AND " + Customer.COL_BACKEND_ID + " is not null AND "
            + Customer.COL_BACKEND_ID + " != 0");
      } else {
        selection = selection.concat(" AND " + Customer.COL_BACKEND_ID + " is  null OR "
            + Customer.COL_BACKEND_ID + " = 0");
      }
    }

    String orderBy = " " + Customer.COL_CREATE_DATE_TIME + " DESC";
    String[] args = new String[argList.size()];
    Cursor cursor = db
        .query(Customer.TABLE_NAME, projection, selection, argList.toArray(args), null, null,
            orderBy);
    List<NCustomerListModel> nCustomers = new ArrayList<>();
    while (cursor.moveToNext()) {
      NCustomerListModel nCustomer = new NCustomerListModel(cursor.getLong(0), cursor.getString(1),
          cursor.getString(2), cursor.getString(3), cursor.getLong(4), cursor.getString(5),
          cursor.getLong(6), cursor.getString(7));
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

  public void bulkInsert(List<Customer> list) {
    CommerDatabaseHelper databaseHelper = new CommerDatabaseHelper(getContext());
    SQLiteDatabase db = databaseHelper.getWritableDatabase();
    db.beginTransaction();
    try {
      for (Customer customer : list) {
        customer.setFullName(CharacterFixUtil.fixString(customer.getFullName()));
        customer.setShopName(CharacterFixUtil.fixString(customer.getShopName()));
        customer.setAddress(CharacterFixUtil.fixString(customer.getAddress()));
        customer.setApproved(true);
        customer.setCreateDateTime(
            DateUtil.convertDate(new Date(), DateUtil.FULL_FORMATTER_GREGORIAN_WITH_TIME, "EN"));
        customer.setUpdateDateTime(
            DateUtil.convertDate(new Date(), DateUtil.FULL_FORMATTER_GREGORIAN_WITH_TIME, "EN"));
        db.insert(getTableName(), null, getContentValues(customer));
      }
      db.setTransactionSuccessful();
    } finally {
      db.endTransaction();
    }
  }
}
