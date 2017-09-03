package com.parsroyal.solutiontablet.ui.fragment;

import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import butterknife.BindView;
import butterknife.ButterKnife;
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
import com.parsroyal.solutiontablet.ui.adapter.LabelValueArrayAdapter;
import com.parsroyal.solutiontablet.ui.adapter.LabelValueArrayAdapterWithHint;
import com.parsroyal.solutiontablet.util.Empty;
import com.parsroyal.solutiontablet.util.ToastUtil;
import java.util.Arrays;
import java.util.List;

public class AddCustomerFragment extends BaseFragment {

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
    }
//    initSpinner(citySpinner, list, getString(R.string.city));
    initSpinner(ownershipSpinner, ownershipList, getString(R.string.ownership_type));
    initSpinner(activitySpinner, activityList, getString(R.string.activity_type));
    initSpinner(customerClassSpinner, customerClassList, getString(R.string.customer_class));
  }

  private void initSpinner(Spinner spinner, List<LabelValue> items, String hint) {

    items.add(new LabelValue(-1L, hint));
    LabelValueArrayAdapterWithHint adapter = new LabelValueArrayAdapterWithHint(getActivity(), items);
    spinner.setAdapter(adapter);
    spinner.setSelection(adapter.getCount());
  }

  @Override
  public int getFragmentId() {
    return MainActivity.NEW_CUSTOMER_DETAIL_FRAGMENT_ID;
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
}
