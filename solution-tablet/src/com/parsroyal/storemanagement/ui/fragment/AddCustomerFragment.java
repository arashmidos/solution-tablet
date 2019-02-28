package com.parsroyal.storemanagement.ui.fragment;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.ResultPoint;
import com.google.zxing.client.android.BeepManager;
import com.journeyapps.barcodescanner.BarcodeCallback;
import com.journeyapps.barcodescanner.BarcodeResult;
import com.journeyapps.barcodescanner.DecoratedBarcodeView;
import com.journeyapps.barcodescanner.DefaultDecoderFactory;
import com.parsroyal.storemanagement.R;
import com.parsroyal.storemanagement.SolutionTabletApplication;
import com.parsroyal.storemanagement.constants.BaseInfoTypes;
import com.parsroyal.storemanagement.constants.Constants;
import com.parsroyal.storemanagement.constants.PageStatus;
import com.parsroyal.storemanagement.data.entity.Customer;
import com.parsroyal.storemanagement.data.entity.CustomerPic;
import com.parsroyal.storemanagement.data.entity.Position;
import com.parsroyal.storemanagement.data.model.LabelValue;
import com.parsroyal.storemanagement.exception.BusinessException;
import com.parsroyal.storemanagement.exception.UnknownSystemException;
import com.parsroyal.storemanagement.service.BaseInfoService;
import com.parsroyal.storemanagement.service.CustomerService;
import com.parsroyal.storemanagement.service.impl.BaseInfoServiceImpl;
import com.parsroyal.storemanagement.service.impl.CustomerServiceImpl;
import com.parsroyal.storemanagement.ui.activity.MainActivity;
import com.parsroyal.storemanagement.ui.adapter.LabelValueArrayAdapterWithHint;
import com.parsroyal.storemanagement.ui.adapter.NewCustomerPictureAdapter;
import com.parsroyal.storemanagement.ui.fragment.dialogFragment.CityDialogFragment;
import com.parsroyal.storemanagement.util.CameraManager;
import com.parsroyal.storemanagement.util.CharacterFixUtil;
import com.parsroyal.storemanagement.util.Empty;
import com.parsroyal.storemanagement.util.ImageUtil;
import com.parsroyal.storemanagement.util.Logger;
import com.parsroyal.storemanagement.util.MediaUtil;
import com.parsroyal.storemanagement.util.NumberUtil;
import com.parsroyal.storemanagement.util.ToastUtil;
import com.parsroyal.storemanagement.util.ValidationUtil;
import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Locale;
import java.util.UUID;
import timber.log.Timber;

/**
 * @author Shakib
 */
public class AddCustomerFragment extends BaseFragment implements View.OnFocusChangeListener {

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
  @BindView(R.id.scanner)
  DecoratedBarcodeView scanner;

  private MainActivity mainActivity;
  private CustomerService customerService;
  private BaseInfoService baseInfoService;
  private Customer customer;
  private LabelValue selectedCity;
  private LabelValue selectedProvince;
  private LabelValue selectedActivity;
  private PageStatus pageStatus;
  private NewCustomerPictureAdapter adapter;
  private Uri fileUri;
  private List<String> picList;
  private BeepManager beepManager;
  private String lastText;

  private BarcodeCallback callback = new BarcodeCallback() {
    @Override
    public void barcodeResult(BarcodeResult result) {
      if (result.getText() == null || result.getText().equals(lastText)) {
        // Prevent duplicate scans
        return;
      }

      lastText = result.getText();

      scanner.setStatusText(result.getText());

      fillNationalCodeData(lastText);
      beepManager.playBeepSoundAndVibrate();
    }

    @Override
    public void possibleResultPoints(List<ResultPoint> resultPoints) {
    }
  };
  private boolean autoStart;

  public AddCustomerFragment() {
    // Required empty public constructor
  }

  public static AddCustomerFragment newInstance() {
    return new AddCustomerFragment();
  }

  @Override
  public void onResume() {
    super.onResume();
    scanner.resume();
  }

  @Override
  public void onPause() {
    super.onPause();
    scanner.pause();
  }

  private void initScanner() {
    Collection<BarcodeFormat> formats = Arrays.asList(BarcodeFormat.QR_CODE, BarcodeFormat.CODE_39);
    scanner.getBarcodeView().setDecoderFactory(new DefaultDecoderFactory(formats));
    scanner.initializeFromIntent(mainActivity.getIntent());
    scanner.decodeContinuous(callback);
    scanner.setStatusText(getString(R.string.scan_melli_card_barcode));

    beepManager = new BeepManager(mainActivity);
  }

  private void fillNationalCodeData(String extractedData) {
    nationalCodeEdt.setText(NumberUtil.digitsToPersian(extractedData));
  }

  @Override
  public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
      Bundle savedInstanceState) {
    // Inflate the layout for this fragment
    View view = inflater.inflate(R.layout.fragment_add_customer, container, false);
    mainActivity = (MainActivity) getActivity();
    mainActivity.changeTitle(getString(R.string.add_customer));
    ButterKnife.bind(this, view);

    customerService = new CustomerServiceImpl(mainActivity);
    baseInfoService = new BaseInfoServiceImpl(mainActivity);

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
      Timber.e(ex);
      ToastUtil.toastError(mainActivity, ex);
    } catch (Exception ex) {
      Logger.sendError("UI Exception",
          "Error in creating NCustomerDetailFragment " + ex.getMessage());
      Timber.e(ex);
      ToastUtil.toastError(mainActivity, new UnknownSystemException(ex));
    }

    if (Empty.isEmpty(customer)) {
      ToastUtil.toastError(mainActivity, R.string.message_error_in_loading_or_creating_customer);
      mainActivity.changeFragment(MainActivity.FEATURE_FRAGMENT_ID, true);
    }

    setData();
    setUpSpinners();
    setupRecycler();
    if (!CameraManager.checkPermissions(mainActivity)) {
      autoStart = false;
      CameraManager.requestPermissions(mainActivity);
    }
    initScanner();
    if (pageStatus != null && pageStatus == PageStatus.VIEW) {
      disableItems();
    }

    cityTv.setFocusable(false);
    stateTv.setFocusable(false);
    return view;
  }

  private void setupRecycler() {
    if (customer.getId() != null) {

      picList = customerService.getAllPicturesTitleByCustomerId(customer.getId());
    } else {
      picList = new ArrayList<>();
    }
    LinearLayoutManager layout = new LinearLayoutManager(mainActivity,
        LinearLayoutManager.HORIZONTAL, true);
    layout.setReverseLayout(true);
    photoList.setLayoutManager(layout);
    adapter = new NewCustomerPictureAdapter(mainActivity, picList, this);
    photoList.setAdapter(adapter);
    updateImageCounter();
  }

  private void updateImageCounter() {
    imageCounter.setText(NumberUtil.digitsToPersian(String.format(Locale.getDefault(),
        "%d/%d", adapter.isMaxReached() ? adapter.getItemCount() : adapter.getItemCount() - 1,
        Constants.MAX_NEW_CUSTOMER_PHOTO)));
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
      customer.setDescription(NumberUtil
          .digitsToEnglish(CharacterFixUtil.fixString(customerDescription.getText().toString())));

      if (validate()) {
        Long customerId = customerService.saveCustomer(customer);
        if (Empty.isNotEmpty(customerId)) {

          List<String> picsTitleList = adapter.getCustomerPics();
          List<CustomerPic> customerPics = new ArrayList<>();
          for (int i = 0; i < picsTitleList.size(); i++) {
            CustomerPic c = new CustomerPic(picsTitleList.get(i), customerId);
            customerPics.add(c);
          }
          customerService.savePicture(customerPics, customerId);

          ToastUtil.toastSuccess(mainActivity, R.string.message_customer_save_successfully);
          mainActivity.removeFragment(AddCustomerFragment.this);
        } else {
          ToastUtil.toastError(mainActivity, getString(R.string.error_in_saving_new_customer));
        }
      }
    } catch (BusinessException ex) {
      Timber.e(ex);
      ToastUtil.toastError(mainActivity, ex);
    } catch (Exception e) {
      Logger.sendError("Data Storage Exception",
          "Error in saving new customer data " + e.getMessage());
      Timber.e(e);
      ToastUtil.toastError(mainActivity, new UnknownSystemException(e));
    }
  }

  private boolean validate() {
    if (Empty.isEmpty(customer)) {
      return false;
    }
    if (Empty.isEmpty(customer.getFullName())) {
      ToastUtil.toastError(mainActivity, R.string.message_customer_name_is_required);
      fullNameEdt.requestFocus();
      return false;
    }

    if (Empty.isEmpty(customer.getPhoneNumber())) {
      ToastUtil.toastError(mainActivity, R.string.message_phone_is_required);
      phoneNumberEdt.requestFocus();
      return false;
    }

    if (Empty.isEmpty(customer.getCellPhone())) {
      ToastUtil.toastError(mainActivity, R.string.message_cell_phone_is_required);
      mobileEdt.requestFocus();
      return false;
    }

    if (Empty.isEmpty(customer.getShopName())) {
      ToastUtil.toastError(mainActivity, R.string.message_shop_name_is_required);
      shopNameEdt.requestFocus();
      return false;
    }

    String nationalCode = NumberUtil.digitsToEnglish(customer.getNationalCode());
    if (!Empty.isEmpty(nationalCode) && (!ValidationUtil.isValidNationalCode(nationalCode))) {
      ToastUtil.toastError(mainActivity, R.string.message_national_code_is_not_valid);
      nationalCodeEdt.requestFocus();
      return false;
    }

    String postalCode = customer.getPostalCode().trim();
    if (!Empty.isEmpty(postalCode) && postalCode.length() != 10) {
      ToastUtil.toastError(mainActivity, R.string.message_postal_code_is_not_valid);
      postalCodeEdt.requestFocus();
      return false;
    }

    if (Empty.isEmpty(customer.getAddress())) {
      ToastUtil.toastError(mainActivity, R.string.message_address_is_required);
      addressEdt.requestFocus();
      return false;
    }

    if (selectedProvince == null) {
      ToastUtil.toastError(mainActivity, R.string.message_province_is_required);
      return false;
    }

    if (selectedCity == null) {
      ToastUtil.toastError(mainActivity, R.string.message_city_is_required);
      return false;
    }

    if (Empty.isEmpty(customer.getxLocation()) || Empty.isEmpty(customer.getyLocation())) {
      ToastUtil.toastError(mainActivity, "وارد کردن مختصات مکان مشتری اجباری است");
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
    LabelValueArrayAdapterWithHint adapter = new LabelValueArrayAdapterWithHint(mainActivity,
        items);
    spinner.setAdapter(adapter);
    spinner.setSelection(adapter.getCount());
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
      if (mainActivity != null && !mainActivity.isFinishing() && hasFocus) {
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

    Position position = SolutionTabletApplication.getInstance().getLastSavedPosition();
    if (Empty.isNotEmpty(position)) {

      customer.setyLocation(position.getLatitude());
      customer.setxLocation(position.getLongitude());

      ToastUtil.toastMessage(mainActivity, R.string.message_found_location_successfully);
    } else {
      ToastUtil.toastError(mainActivity, R.string.message_finding_location_timeout);
    }
    dismissProgressDialog();
  }

  @Override
  public void onActivityResult(int requestCode, int resultCode, Intent data) {
    if (requestCode == Constants.CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE) {
      if (resultCode == RESULT_OK) {
        Bitmap bitmap = ImageUtil.decodeSampledBitmapFromUri(mainActivity, fileUri);
        bitmap = ImageUtil.getScaledBitmap(mainActivity, bitmap);

        String fileName = UUID.randomUUID().toString();

        String s = ImageUtil.saveTempImage(bitmap,
            MediaUtil.getOutputMediaFile(MediaUtil.MEDIA_TYPE_IMAGE,
                Constants.CUSTOMER_PICTURE_DIRECTORY_NAME, fileName));

        if (!s.equals("")) {
          File fdelete = new File(fileUri.getPath());
          if (fdelete.exists()) {
            fdelete.delete();
          }
        }

        adapter.add(s);
        updateImageCounter();
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
      autoStart = true;
      startCameraActivity();
    }
  }

  public void startCameraActivity() {
    if (!autoStart) {
      return;
    }
    String fileName = UUID.randomUUID().toString();

    fileUri = MediaUtil.getOutputMediaFileUri(mainActivity, MediaUtil.MEDIA_TYPE_IMAGE,
        Constants.CUSTOMER_PICTURE_DIRECTORY_NAME, fileName); // create a file to save the image
    CameraManager.startCameraActivity(mainActivity, fileUri, this);
  }
}
