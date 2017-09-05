package com.parsroyal.solutiontablet.ui.fragment;

import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import com.crashlytics.android.Crashlytics;
import com.parsroyal.solutiontablet.R;
import com.parsroyal.solutiontablet.constants.BaseInfoTypes;
import com.parsroyal.solutiontablet.constants.Constants;
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
import com.parsroyal.solutiontablet.ui.observer.FindLocationListener;
import com.parsroyal.solutiontablet.util.CharacterFixUtil;
import com.parsroyal.solutiontablet.util.Empty;
import com.parsroyal.solutiontablet.util.NumberUtil;
import com.parsroyal.solutiontablet.util.ToastUtil;
import java.util.Arrays;
import java.util.List;

public class AddCustomerFragment extends BaseFragment implements View.OnFocusChangeListener {

  private static final String TAG = AddCustomerFragment.class.getName();
  @BindView(R.id.city_spinner)
  Spinner citySpinner;
  @BindView(R.id.state_spinner)
  Spinner stateSpinner;
  @BindView(R.id.ownership_spinner)
  Spinner ownershipSpinner;
  @BindView(R.id.activity_spinner)
  Spinner activitySpinner;
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
  private MainActivity mainActivity;
  private CustomerService customerService;
  private BaseInfoService baseInfoService;
  private LocationService locationService;
  private Customer customer;

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
        customer = customerService.getCustomerById(arguments.getLong(Constants.CUSTOMER_ID));
      } else {
        customer = new Customer();
      }
    } catch (BusinessException ex) {
      Log.e(TAG, ex.getMessage(), ex);
      ToastUtil.toastError(getActivity(), ex);
    } catch (Exception ex) {
      Crashlytics.log(Log.ERROR, "UI Exception",
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
    return view;
  }

  private void setData() {
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
  }

  private void setUpSpinners() {
    List<LabelValue> provinceList = baseInfoService.getAllProvincesLabelValues();
    List<LabelValue> activityList = baseInfoService
        .getAllBaseInfosLabelValuesByTypeId(BaseInfoTypes.ACTIVITY_TYPE.getId());
    List<LabelValue> ownershipList = baseInfoService
        .getAllBaseInfosLabelValuesByTypeId(BaseInfoTypes.OWNERSHIP_TYPE.getId());
    List<LabelValue> customerClassList = baseInfoService
        .getAllBaseInfosLabelValuesByTypeId(BaseInfoTypes.CUSTOMER_CLASS.getId());

    if (Empty.isNotEmpty(provinceList)) {
      initSpinner(stateSpinner, provinceList, getString(R.string.state));
      stateSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
        @Override
        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
          LabelValue selectedProvince = provinceList.get(position);
          if (Empty.isNotEmpty(selectedProvince)) {
            loadCitySpinnerData(selectedProvince.getValue());
          }
        }

        @Override
        public void onNothingSelected(AdapterView<?> parent) {
        }
      });

      if (Empty.isNotEmpty(customer.getProvinceBackendId())) {
        int position = 0;
        for (LabelValue labelValue : provinceList) {
          if (labelValue.getValue().equals(customer.getProvinceBackendId())) {
            stateSpinner.setSelection(position);
            break;
          }
          position++;
        }
      }
    }

    if (Empty.isNotEmpty(activityList)) {
      initSpinner(activitySpinner, activityList, getString(R.string.activity_type));

      if (Empty.isNotEmpty(customer.getActivityBackendId())) {
        int position = 0;
        for (LabelValue labelValue : activityList) {
          if (labelValue.getValue().equals(customer.getActivityBackendId())) {
            activitySpinner.setSelection(position);
            break;
          }
          position++;
        }
      }
    }
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

    LabelValue provinceLabelValue = (LabelValue) stateSpinner.getSelectedItem();
    loadCitySpinnerData(provinceLabelValue.getValue());

    stateSpinner.setFocusableInTouchMode(true);
    stateSpinner.setOnFocusChangeListener(this);
    citySpinner.setFocusableInTouchMode(true);
    citySpinner.setOnFocusChangeListener(this);
    activitySpinner.setFocusableInTouchMode(true);
    activitySpinner.setOnFocusChangeListener(this);
    customerClassSpinner.setFocusableInTouchMode(true);
    customerClassSpinner.setOnFocusChangeListener(this);
    ownershipSpinner.setFocusableInTouchMode(true);
    ownershipSpinner.setOnFocusChangeListener(this);
  }

  private void save() {
    try {
      customer.setFullName(CharacterFixUtil.fixString(fullNameEdt.getText().toString()));
      customer.setPhoneNumber(NumberUtil.digitsToEnglish(phoneNumberEdt.getText().toString()));
      customer.setCellPhone(NumberUtil.digitsToEnglish(mobileEdt.getText().toString()));
      customer.setAddress(CharacterFixUtil.fixString(addressEdt.getText().toString()));
      if (Empty.isNotEmpty(storeMeterEdt.getText()) && !storeMeterEdt.getText().toString()
          .equals("")) {
        customer.setStoreSurface(
            Integer.parseInt(NumberUtil.digitsToEnglish(storeMeterEdt.getText().toString())));
      }
      customer.setProvinceBackendId(
          stateSpinner.getSelectedItemId() <= 0 ? null : stateSpinner.getSelectedItemId());
      customer.setCityBackendId(
          citySpinner.getSelectedItemId() <= 0 ? null : citySpinner.getSelectedItemId());
      customer.setActivityBackendId(activitySpinner.getSelectedItemId());
      customer.setStoreLocationTypeBackendId(
          ownershipSpinner.getSelectedItemId() == -1L ? null : ownershipSpinner.getSelectedItemId());
      customer.setClassBackendId(customerClassSpinner.getSelectedItemId() == -1L ? null
          : customerClassSpinner.getSelectedItemId());
      customer.setShopName(CharacterFixUtil.fixString(shopNameEdt.getText().toString()));
      customer.setNationalCode(NumberUtil.digitsToEnglish(nationalCodeEdt.getText().toString()));
      customer.setMunicipalityCode(
          NumberUtil.digitsToEnglish(regionalMunicipalityEdt.getText().toString()));
      customer.setPostalCode(NumberUtil.digitsToEnglish(postalCodeEdt.getText().toString()));
      customer.setApproved(false);

      if (validate()) {
        customerService.saveCustomer(customer);
        ToastUtil.toastSuccess(getActivity(), R.string.message_customer_save_successfully);
        mainActivity.changeFragment(MainActivity.CUSTOMER_FRAGMENT, false);
      }
    } catch (BusinessException ex) {
      Log.e(TAG, ex.getMessage(), ex);
      ToastUtil.toastError(getActivity(), ex);
    } catch (Exception e) {
      Crashlytics.log(Log.ERROR, "Data Storage Exception",
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

    if (stateSpinner.getSelectedItemId() == -1L) {
      ToastUtil.toastError(getActivity(), R.string.message_province_is_required);
      return false;
    }

    if (citySpinner.getSelectedItemId() <= 0) {
      ToastUtil.toastError(getActivity(), R.string.message_city_is_required);
      return false;
    }

    if (activitySpinner.getSelectedItemId() == -1L) {
      ToastUtil.toastError(getActivity(), R.string.message_activity_type_is_required);
      return false;
    }

    return true;
  }

  private void loadCitySpinnerData(Long provinceId) {
    List<LabelValue> cityLabelValues = baseInfoService.getAllCitiesLabelsValues(provinceId);
    if (Empty.isNotEmpty(cityLabelValues)) {
      initSpinner(citySpinner, cityLabelValues, getString(R.string.city));
    }

    if (Empty.isNotEmpty(customer.getCityBackendId())) {
      int position = 0;
      for (LabelValue labelValue : cityLabelValues) {
        if (labelValue.getValue().equals(customer.getCityBackendId())) {
          citySpinner.setSelection(position);
          break;
        }
        position++;
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
    if (hasFocus) {
      v.performClick();
    }
  }

  @OnClick({R.id.create_btn, R.id.location_lay})
  public void onClick(View view) {
    switch (view.getId()) {
      case R.id.location_lay:
        getLocation();
        break;
      case R.id.create_btn:
        save();
        break;
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
  public int getFragmentId() {
    return MainActivity.NEW_CUSTOMER_DETAIL_FRAGMENT_ID;
  }
}
