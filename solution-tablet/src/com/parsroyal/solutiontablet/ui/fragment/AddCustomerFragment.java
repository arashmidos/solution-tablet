package com.parsroyal.solutiontablet.ui.fragment;

import android.content.Intent;
import android.graphics.Bitmap;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import com.crashlytics.android.Crashlytics;
import com.parsroyal.solutiontablet.R;
import com.parsroyal.solutiontablet.constants.BaseInfoTypes;
import com.parsroyal.solutiontablet.constants.Constants;
import com.parsroyal.solutiontablet.constants.PageStatus;
import com.parsroyal.solutiontablet.data.entity.Customer;
import com.parsroyal.solutiontablet.data.model.LabelValue;
import com.parsroyal.solutiontablet.exception.BusinessException;
import com.parsroyal.solutiontablet.exception.UnknownSystemException;
import com.parsroyal.solutiontablet.service.BaseInfoService;
import com.parsroyal.solutiontablet.service.CustomerService;
import com.parsroyal.solutiontablet.service.LocationService;
import com.parsroyal.solutiontablet.service.impl.BaseInfoServiceImpl;
import com.parsroyal.solutiontablet.service.impl.CustomerServiceImpl;
import com.parsroyal.solutiontablet.service.impl.LocationServiceImpl;
import com.parsroyal.solutiontablet.ui.MainActivity;
import com.parsroyal.solutiontablet.ui.adapter.LabelValueArrayAdapterWithHint;
import com.parsroyal.solutiontablet.ui.adapter.NewCustomerPictureAdapter;
import com.parsroyal.solutiontablet.ui.fragment.dialogFragment.CityDialogFragment;
import com.parsroyal.solutiontablet.ui.observer.FindLocationListener;
import com.parsroyal.solutiontablet.util.CameraManager;
import com.parsroyal.solutiontablet.util.CharacterFixUtil;
import com.parsroyal.solutiontablet.util.Empty;
import com.parsroyal.solutiontablet.util.ImageUtil;
import com.parsroyal.solutiontablet.util.Logger;
import com.parsroyal.solutiontablet.util.MediaUtil;
import com.parsroyal.solutiontablet.util.NumberUtil;
import com.parsroyal.solutiontablet.util.ToastUtil;
import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * @author Shakib
 */
public class AddCustomerFragment extends BaseFragment implements View.OnFocusChangeListener {

  private static final String TAG = AddCustomerFragment.class.getName();
  private static final int RESULT_OK = -1;
  private static final int RESULT_CANCELED = 0;

  @BindView(R.id.city_tv)
  TextView cityTv;
  @BindView(R.id.state_tv)
  TextView stateTv;
  @BindView(R.id.activity_tv)
  TextView activityTv;
  @BindView(R.id.ownership_spinner)
  Spinner ownershipSpinner;
  @BindView(R.id.customer_class_spinner)
  Spinner customerClassSpinner;
  @BindView(R.id.full_name_edt)
  EditText fullNameEdt;
  @BindView(R.id.national_code_edt)
  EditText nationalCodeEdt;
  @BindView(R.id.shop_name_edt)
  EditText shopNameEdt;
  @BindView(R.id.phone_number_edt)
  EditText phoneNumberEdt;
  @BindView(R.id.mobile_edt)
  EditText mobileEdt;
  @BindView(R.id.address_edt)
  EditText addressEdt;
  @BindView(R.id.postal_code_edt)
  EditText postalCodeEdt;
  @BindView(R.id.regional_municipality_edt)
  EditText regionalMunicipalityEdt;
  @BindView(R.id.store_meter_edt)
  EditText storeMeterEdt;
  @BindView(R.id.create_btn)
  Button createBtn;
  @BindView(R.id.description_edt)
  EditText customerDescription;
  @BindView(R.id.photo_list)
  RecyclerView photoList;
  @BindView(R.id.image_counter)
  TextView imageCounter;

  private MainActivity mainActivity;
  private CustomerService customerService;
  private BaseInfoService baseInfoService;
  private LocationService locationService;
  private Customer customer;
  private LabelValue selectedCity;
  private LabelValue selectedProvince;
  private LabelValue selectedActivity;
  private PageStatus pageStatus;
  private NewCustomerPictureAdapter adapter;
  private Uri fileUri;

  public AddCustomerFragment() {
    // Required empty public constructor
  }

  public static AddCustomerFragment newInstance() {
    return new AddCustomerFragment();
  }

  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container,
      Bundle savedInstanceState) {
    // Inflate the layout for this fragment
    View view = inflater.inflate(R.layout.fragment_add_customer, container, false);
    mainActivity = (MainActivity) getActivity();
    mainActivity.changeTitle(getString(R.string.add_customer));
    ButterKnife.bind(this, view);

    customerService = new CustomerServiceImpl(mainActivity);
    baseInfoService = new BaseInfoServiceImpl(mainActivity);
    locationService = new LocationServiceImpl(mainActivity);

    try {
      Bundle arguments = getArguments();
      if (Empty.isNotEmpty(arguments)) {

        pageStatus = (PageStatus) arguments.getSerializable(Constants.PAGE_STATUS);
        customer = customerService.getCustomerById(arguments.getLong(Constants.CUSTOMER_ID));
        if (Empty.isEmpty(customer)) {
          customer = new Customer();
        }
      }
    } catch (BusinessException ex) {
      Log.e(TAG, ex.getMessage(), ex);
      ToastUtil.toastError(getActivity(), ex);
    } catch (Exception ex) {
      Logger.sendError("UI Exception",
          "Error in creating NCustomerDetailFragment " + ex.getMessage());
      Log.e(TAG, ex.getMessage(), ex);
      ToastUtil.toastError(getActivity(), new UnknownSystemException(ex));
    }

    if (Empty.isEmpty(customer)) {
      ToastUtil.toastError(getActivity(), R.string.message_error_in_loading_or_creating_customer);
      mainActivity.changeFragment(MainActivity.FEATURE_FRAGMENT_ID, true);
    }

    setData();
    setUpSpinners();
    setupRecycler();
    if (pageStatus != null && pageStatus == PageStatus.VIEW) {
      disableItems();
    }
    return view;
  }

  private void setupRecycler() {

    LinearLayoutManager layout = new LinearLayoutManager(getActivity(),
        LinearLayoutManager.HORIZONTAL, true);
    layout.setReverseLayout(true);
    photoList.setLayoutManager(layout);
    adapter = new NewCustomerPictureAdapter(getActivity(), new ArrayList<>(), this);
    photoList.setAdapter(adapter);
    updateImageCounter();
  }

  private void updateImageCounter() {
    imageCounter.setText(NumberUtil.digitsToPersian(String.format(Locale.getDefault(),
        "%d/%d", adapter.getItemCount() - 1, Constants.MAX_NEW_CUSTOMER_PHOTO)));
  }

  private void disableItems() {
    fullNameEdt.setEnabled(false);
    phoneNumberEdt.setEnabled(false);
    mobileEdt.setEnabled(false);
    shopNameEdt.setEnabled(false);
    nationalCodeEdt.setEnabled(false);
    regionalMunicipalityEdt.setEnabled(false);
    postalCodeEdt.setEnabled(false);
    storeMeterEdt.setEnabled(false);
    addressEdt.setEnabled(false);
    stateTv.setEnabled(false);
    stateTv.setClickable(false);
    cityTv.setEnabled(false);
    cityTv.setClickable(false);
    activityTv.setEnabled(false);
    activityTv.setClickable(false);
    ownershipSpinner.setEnabled(false);
    customerClassSpinner.setEnabled(false);
    customerDescription.setEnabled(false);
    createBtn.setVisibility(View.GONE);
  }

  private void setData() {
    List<LabelValue> provinceList = baseInfoService.getAllProvincesLabelValues();
    if (Empty.isNotEmpty(customer.getProvinceBackendId())) {
      for (LabelValue labelValue : provinceList) {
        if (labelValue.getValue().equals(customer.getProvinceBackendId())) {
          selectedProvince = labelValue;
          stateTv.setText(selectedProvince.getLabel());
          loadCity(selectedProvince.getValue());
          break;
        }
      }
    }
    fullNameEdt.setText(customer.getFullName());
    phoneNumberEdt.setText(customer.getPhoneNumber());
    mobileEdt.setText(customer.getCellPhone());
    shopNameEdt.setText(customer.getShopName());
    nationalCodeEdt.setText(customer.getNationalCode());
    regionalMunicipalityEdt.setText(customer.getMunicipalityCode());
    postalCodeEdt.setText(customer.getPostalCode());

    if (customer.getStoreSurface() != 0) {
      storeMeterEdt.setText(String.valueOf(customer.getStoreSurface()));
    }

    addressEdt.setText(customer.getAddress());
    customerDescription.setText(customer.getDescription());
    if (customer.getActivityBackendId() != null) {
      loadActivity(customer.getActivityBackendId());
    }
  }

  public void setSelectedItem(LabelValue selectedItem, int datatype) {
    switch (datatype) {
      case -1:
        selectedProvince = selectedItem;
        stateTv.setText(selectedProvince.getLabel());
        break;
      case -2:
        selectedActivity = selectedItem;
        activityTv.setText(selectedActivity.getLabel());
        break;
      case -3:
        selectedCity = selectedItem;
        cityTv.setText(selectedCity.getLabel());
        break;
    }
  }

  private void setUpSpinners() {
    List<LabelValue> ownershipList = baseInfoService
        .getAllBaseInfosLabelValuesByTypeId(BaseInfoTypes.OWNERSHIP_TYPE.getId());
    List<LabelValue> customerClassList = baseInfoService
        .getAllBaseInfosLabelValuesByTypeId(BaseInfoTypes.CUSTOMER_CLASS.getId());

    if (Empty.isNotEmpty(ownershipList)) {
      initSpinner(ownershipSpinner, ownershipList, getString(R.string.ownership_type));

      if (Empty.isNotEmpty(customer.getStoreLocationTypeBackendId())) {
        int position = 0;
        for (LabelValue labelValue : ownershipList) {
          if (labelValue.getValue().equals(customer.getStoreLocationTypeBackendId())) {
            ownershipSpinner.setSelection(position);
            break;
          }
          position++;
        }
      }
    }

    if (Empty.isNotEmpty(customerClassList)) {
      initSpinner(customerClassSpinner, customerClassList, getString(R.string.customer_class));

      if (Empty.isNotEmpty(customer.getClassBackendId())) {
        int position = 0;
        for (LabelValue labelValue : customerClassList) {
          if (labelValue.getValue().equals(customer.getClassBackendId())) {
            customerClassSpinner.setSelection(position);
            break;
          }
          position++;
        }
      }
    }

    customerClassSpinner.setFocusableInTouchMode(true);
    customerClassSpinner.setOnFocusChangeListener(this);
    ownershipSpinner.setFocusableInTouchMode(true);
    ownershipSpinner.setOnFocusChangeListener(this);
  }

  private void showCityDialog(long provinceId) {
    FragmentTransaction ft = mainActivity.getSupportFragmentManager().beginTransaction();
    CityDialogFragment cityDialogFragment = CityDialogFragment
        .newInstance(this, provinceId);
    cityDialogFragment.show(ft, "city");
  }

  private void save() {
    try {
      customer.setFullName(
          NumberUtil.digitsToEnglish(CharacterFixUtil.fixString(fullNameEdt.getText().toString())));
      customer.setPhoneNumber(NumberUtil.digitsToEnglish(phoneNumberEdt.getText().toString()));
      customer.setCellPhone(NumberUtil.digitsToEnglish(mobileEdt.getText().toString()));
      customer.setAddress(
          NumberUtil.digitsToEnglish(CharacterFixUtil.fixString(addressEdt.getText().toString())));
      if (Empty.isNotEmpty(storeMeterEdt.getText()) && !storeMeterEdt.getText().toString()
          .equals("")) {
        customer.setStoreSurface(
            Integer.parseInt(NumberUtil.digitsToEnglish(storeMeterEdt.getText().toString())));
      }
      customer.setProvinceBackendId(selectedProvince == null ? null : selectedProvince.getValue());
      customer.setCityBackendId(selectedCity == null ? null : selectedCity.getValue());
      customer.setActivityBackendId(selectedActivity == null ? null : selectedActivity.getValue());
      customer.setStoreLocationTypeBackendId(ownershipSpinner.getSelectedItemId() < 0 ? null
          : ownershipSpinner.getSelectedItemId());
      customer.setClassBackendId(customerClassSpinner.getSelectedItemId() < 0 ? null
          : customerClassSpinner.getSelectedItemId());
      customer.setShopName(
          NumberUtil.digitsToEnglish(CharacterFixUtil.fixString(shopNameEdt.getText().toString())));
      customer.setNationalCode(NumberUtil.digitsToEnglish(nationalCodeEdt.getText().toString()));
      customer.setMunicipalityCode(
          NumberUtil.digitsToEnglish(regionalMunicipalityEdt.getText().toString()));
      customer.setPostalCode(NumberUtil.digitsToEnglish(postalCodeEdt.getText().toString()));
      customer.setApproved(false);
      customer.setDescription(customerDescription.getText().toString());

      if (validate()) {
        customerService.saveCustomer(customer);
        ToastUtil.toastSuccess(getActivity(), R.string.message_customer_save_successfully);
        mainActivity.removeFragment(AddCustomerFragment.this);
      }
    } catch (BusinessException ex) {
      Log.e(TAG, ex.getMessage(), ex);
      ToastUtil.toastError(getActivity(), ex);
    } catch (Exception e) {
      Logger.sendError("Data Storage Exception",
          "Error in saving new customer data " + e.getMessage());
      Log.e(TAG, e.getMessage(), e);
      ToastUtil.toastError(getActivity(), new UnknownSystemException(e));
    }
  }

  private boolean validate() {
    if (Empty.isEmpty(customer)) {
      return false;
    }
    if (Empty.isEmpty(customer.getFullName())) {
      ToastUtil.toastError(getActivity(), R.string.message_customer_name_is_required);
      fullNameEdt.requestFocus();
      return false;
    }

    if (Empty.isEmpty(customer.getPhoneNumber())) {
      ToastUtil.toastError(getActivity(), R.string.message_phone_is_required);
      phoneNumberEdt.requestFocus();
      return false;
    }

    if (Empty.isEmpty(customer.getCellPhone())) {
      ToastUtil.toastError(getActivity(), R.string.message_cell_phone_is_required);
      mobileEdt.requestFocus();
      return false;
    }

    if (Empty.isEmpty(customer.getShopName())) {
      ToastUtil.toastError(getActivity(), R.string.message_shop_name_is_required);
      shopNameEdt.requestFocus();
      return false;
    }

    String nationalCode = NumberUtil.digitsToEnglish(customer.getNationalCode());
    if (!Empty.isEmpty(nationalCode) && (!isValidNationalCode(nationalCode))) {
      ToastUtil.toastError(getActivity(), R.string.message_national_code_is_not_valid);
      nationalCodeEdt.requestFocus();
      return false;
    }

    String postalCode = customer.getPostalCode().trim();
    if (!Empty.isEmpty(postalCode) && postalCode.length() != 10) {
      ToastUtil.toastError(getActivity(), R.string.message_postal_code_is_not_valid);
      postalCodeEdt.requestFocus();
      return false;
    }

    if (Empty.isEmpty(customer.getAddress())) {
      ToastUtil.toastError(getActivity(), R.string.message_address_is_required);
      addressEdt.requestFocus();
      return false;
    }

    if (selectedProvince == null) {
      ToastUtil.toastError(getActivity(), R.string.message_province_is_required);
      return false;
    }

    if (selectedCity == null) {
      ToastUtil.toastError(getActivity(), R.string.message_city_is_required);
      return false;
    }

    if (Empty.isEmpty(customer.getxLocation()) || Empty.isEmpty(customer.getyLocation())) {
      ToastUtil.toastError(getActivity(), "وارد کردن مختصات مکان مشتری اجباری است");
      return false;
    }

    return true;
  }

  private void loadCity(Long provinceId) {
    List<LabelValue> cityLabelValues = baseInfoService.getAllCitiesLabelsValues(provinceId);

    if (Empty.isNotEmpty(customer.getCityBackendId())) {
      for (LabelValue labelValue : cityLabelValues) {
        if (labelValue.getValue().equals(customer.getCityBackendId())) {
          selectedCity = labelValue;
          cityTv.setText(selectedCity.getLabel());
          break;
        }
      }
    }
  }

  private void loadActivity(Long activityId) {
    List<LabelValue> activityLabelValueList = baseInfoService
        .getAllBaseInfosLabelValuesByTypeId(BaseInfoTypes.ACTIVITY_TYPE.getId());

    for (LabelValue labelValue : activityLabelValueList) {
      if (labelValue.getValue().equals(activityId)) {
        selectedActivity = labelValue;
        activityTv.setText(selectedActivity.getLabel());
        break;
      }
    }
  }

  private void initSpinner(Spinner spinner, List<LabelValue> items, String hint) {

    items.add(new LabelValue(-1L, hint));
    LabelValueArrayAdapterWithHint adapter = new LabelValueArrayAdapterWithHint(getActivity(),
        items);
    spinner.setAdapter(adapter);
    spinner.setSelection(adapter.getCount());
  }

  private boolean isValidNationalCode(String nationalCode) {
    if (nationalCode.length() != 10) {
      return false;
    } else {
      //Check for equal numbers
      String[] allDigitEqual = {"0000000000", "1111111111", "2222222222", "3333333333",
          "4444444444", "5555555555", "6666666666", "7777777777", "8888888888", "9999999999"};
      if (Arrays.asList(allDigitEqual).contains(nationalCode)) {
        return false;
      } else {
        int sum = 0;
        int lenght = 10;
        for (int i = 0; i < lenght - 1; i++) {
          sum += Integer.parseInt(String.valueOf(nationalCode.charAt(i))) * (lenght - i);
        }

        int r = Integer.parseInt(String.valueOf(nationalCode.charAt(9)));

        int c = sum % 11;

        return (((c < 2) && (r == c)) || ((c >= 2) && ((11 - c) == r)));
      }

    }
  }

  /**
   * Called when the focus state of a view has changed.
   *
   * @param v The view whose state has changed.
   * @param hasFocus The new focus state of v.
   */
  @Override
  public void onFocusChange(View v, boolean hasFocus) {
    try {
      if (getActivity() != null && !getActivity().isFinishing() && hasFocus) {
        v.performClick();
      }
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  @OnClick({R.id.create_btn, R.id.location_lay, R.id.city_tv, R.id.state_tv, R.id.activity_tv})
  public void onClick(View view) {
    switch (view.getId()) {
      case R.id.location_lay:
        if (pageStatus != null && pageStatus == PageStatus.EDIT) {
          getLocation();
        }
        break;
      case R.id.create_btn:
        save();
        break;
      case R.id.city_tv:
        if (selectedProvince != null) {
          showCityDialog(selectedProvince.getValue());
        }
        break;
      case R.id.state_tv:
        showCityDialog(-1L);
        break;
      case R.id.activity_tv:
        showCityDialog(-2L);
    }
  }

  private void getLocation() {
    showProgressDialog(getString(R.string.message_finding_location));

    try {
      locationService.findCurrentLocation(new FindLocationListener() {
        @Override
        public void foundLocation(Location location) {
          customer.setyLocation(location.getLatitude());
          customer.setxLocation(location.getLongitude());
          ToastUtil.toastMessage(getActivity(), R.string.message_found_location_successfully);
          dismissProgressDialog();
        }

        @Override
        public void timeOut() {
          runOnUiThread(() -> {
            ToastUtil.toastError(getActivity(), R.string.message_finding_location_timeout);
            dismissProgressDialog();
          });
        }
      });
    } catch (BusinessException ex) {
      ToastUtil.toastError(getActivity(), ex);
      dismissProgressDialog();
    } catch (Exception e) {
      Crashlytics
          .log(Log.ERROR, "Location Service", "Error in finding location " + e.getMessage());
      ToastUtil.toastError(getActivity(), new UnknownSystemException(e));
      Log.e(TAG, e.getMessage(), e);
      dismissProgressDialog();
    }
  }

  @Override
  public void onActivityResult(int requestCode, int resultCode, Intent data) {
    if (requestCode == Constants.CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE) {
      if (resultCode == RESULT_OK) {
        Bitmap bitmap = ImageUtil.decodeSampledBitmapFromUri(getActivity(), fileUri);
        bitmap = ImageUtil.getScaledBitmap(getActivity(), bitmap);

        String s = ImageUtil.saveTempImage(bitmap, MediaUtil
            .getOutputMediaFile(MediaUtil.MEDIA_TYPE_IMAGE,
                Constants.CUSTOMER_PICTURE_DIRECTORY_NAME,
                "IMG_" + customer.getCode() + "_" + (new Date().getTime()) % 1000));

        if (!s.equals("")) {
          File fdelete = new File(fileUri.getPath());
          if (fdelete.exists()) {
            fdelete.delete();
          }
        }

        adapter.add(s);
        updateImageCounter();
        if (adapter.getItemCount() == Constants.MAX_NEW_CUSTOMER_PHOTO + 1) {
          Toast.makeText(mainActivity, "REMOVE ADD PHOTO BUTTON", Toast.LENGTH_SHORT).show();
        }
      } else if (resultCode == RESULT_CANCELED) {
        // User cancelled the image capture
      } else {
        // Image capture failed, advise user
      }
    }
  }

  @Override
  public int getFragmentId() {
    return MainActivity.NEW_CUSTOMER_DETAIL_FRAGMENT_ID;
  }

  public void takePhoto() {
    if (!CameraManager.checkPermissions(mainActivity)) {
      CameraManager.requestPermissions(mainActivity);
    } else {
      startCameraActivity();
    }
  }

  public void startCameraActivity() {
    String postfix = String.valueOf((new Date().getTime()) % 1000);
    fileUri = MediaUtil.getOutputMediaFileUri(mainActivity, MediaUtil.MEDIA_TYPE_IMAGE,
        Constants.CUSTOMER_PICTURE_DIRECTORY_NAME,
        "IMG_N_" + postfix); // create a file to save the image
    CameraManager.startCameraActivity(mainActivity, fileUri, this);
  }
}
