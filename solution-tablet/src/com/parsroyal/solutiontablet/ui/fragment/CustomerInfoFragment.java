package com.parsroyal.solutiontablet.ui.fragment;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AlertDialog.Builder;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.crashlytics.android.Crashlytics;
import com.parsroyal.solutiontablet.R;
import com.parsroyal.solutiontablet.constants.BaseInfoTypes;
import com.parsroyal.solutiontablet.constants.Constants;
import com.parsroyal.solutiontablet.constants.SaleOrderStatus;
import com.parsroyal.solutiontablet.constants.VisitInformationDetailType;
import com.parsroyal.solutiontablet.data.entity.Customer;
import com.parsroyal.solutiontablet.data.entity.CustomerPic;
import com.parsroyal.solutiontablet.data.entity.VisitInformation;
import com.parsroyal.solutiontablet.data.entity.VisitInformationDetail;
import com.parsroyal.solutiontablet.data.model.LabelValue;
import com.parsroyal.solutiontablet.data.model.SaleOrderDto;
import com.parsroyal.solutiontablet.exception.BusinessException;
import com.parsroyal.solutiontablet.exception.UnknownSystemException;
import com.parsroyal.solutiontablet.service.impl.BaseInfoServiceImpl;
import com.parsroyal.solutiontablet.service.impl.CustomerServiceImpl;
import com.parsroyal.solutiontablet.service.impl.LocationServiceImpl;
import com.parsroyal.solutiontablet.service.impl.SaleOrderServiceImpl;
import com.parsroyal.solutiontablet.service.impl.SettingServiceImpl;
import com.parsroyal.solutiontablet.service.impl.VisitServiceImpl;
import com.parsroyal.solutiontablet.ui.MainActivity;
import com.parsroyal.solutiontablet.ui.adapter.LabelValueArrayAdapter;
import com.parsroyal.solutiontablet.ui.adapter.RegisterNoLabelValueArrayAdapter;
import com.parsroyal.solutiontablet.ui.observer.FindLocationListener;
import com.parsroyal.solutiontablet.util.DialogUtil;
import com.parsroyal.solutiontablet.util.Empty;
import com.parsroyal.solutiontablet.util.ImageUtil;
import com.parsroyal.solutiontablet.util.MediaUtil;
import com.parsroyal.solutiontablet.util.ToastUtil;
import com.parsroyal.solutiontablet.util.constants.ApplicationKeys;

import java.util.Date;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class CustomerInfoFragment extends Fragment {

  private static final String TAG = CustomerInfoFragment.class.getName();
  private static final int CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE = 100;
  private static final int RESULT_OK = -1;
  private static final int RESULT_CANCELED = 0;
  @BindView(R.id.store_tv)
  TextView storeTv;
  @BindView(R.id.drop_img)
  ImageView dropImg;
  @BindView(R.id.show_more_tv)
  TextView showMoreTv;
  @BindView(R.id.location_tv)
  TextView locationTv;
  @BindView(R.id.mobile_tv)
  TextView mobileTv;
  @BindView(R.id.phone_tv)
  TextView phoneTv;
  @BindView(R.id.customer_detail_lay)
  LinearLayout customerDetailLay;
  @BindView(R.id.add_order_tv)
  TextView addOrderTv;
  @BindView(R.id.register_order_lay)
  RelativeLayout registerOrderLay;
  private boolean isShowMore = true;
  private long customerId;
  private long visitId;
  private CustomerServiceImpl customerService;
  private SettingServiceImpl settingService;
  private VisitServiceImpl visitService;
  private BaseInfoServiceImpl baseInfoService;
  private LocationServiceImpl locationService;
  private Customer customer;
  private String saleType;
  private MainActivity mainActivity;
  private SaleOrderServiceImpl saleOrderService;
  private Uri fileUri;
  private SaleOrderDto orderDto;


  public CustomerInfoFragment() {
    // Required empty public constructor
  }

  public static CustomerInfoFragment newInstance(Bundle arguments) {
    CustomerInfoFragment fragment = new CustomerInfoFragment();
    fragment.setArguments(arguments);
    return fragment;
  }

  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container,
                           Bundle savedInstanceState) {
    // Inflate the layout for this fragment
    View view = inflater.inflate(R.layout.fragment_customer_info, container, false);
    ButterKnife.bind(this, view);
    Bundle args = getArguments();
    customerId = args.getLong(Constants.CUSTOMER_ID);
    visitId = args.getLong(Constants.VISIT_ID);
    mainActivity = (MainActivity) getActivity();
    customerService = new CustomerServiceImpl(mainActivity);
    settingService = new SettingServiceImpl(mainActivity);
    visitService = new VisitServiceImpl(mainActivity);
    baseInfoService = new BaseInfoServiceImpl(mainActivity);
    locationService = new LocationServiceImpl(mainActivity);
    saleOrderService = new SaleOrderServiceImpl(mainActivity);

//    customer = customerService.getCustomerDtoById(customerId);
    customer = customerService.getCustomerById(customerId);

    saleType = settingService.getSettingValue(ApplicationKeys.SETTING_SALE_TYPE);

    setData();
    return view;
  }

  @Override
  public void setUserVisibleHint(boolean isVisibleToUser) {
//    if (isVisibleToUser && getView() == null)
  }

  private void setData() {
    phoneTv.setText(customer.getPhoneNumber());
    mobileTv.setText(customer.getCellPhone());
    locationTv.setText(customer.getAddress());
    storeTv.setText(customer.getFullName());

    if (saleType.equals(ApplicationKeys.SALE_HOT)) {
      addOrderTv.setText(String.format("ثبت %s", getString(R.string.title_factor)));
    }

    if (saleType.equals(ApplicationKeys.SALE_DISTRIBUTER)) {
      registerOrderLay.setVisibility(View.GONE);
    }
  }

  @OnClick({R.id.show_more_tv, R.id.register_order_lay, R.id.register_payment_lay,
      R.id.register_questionnaire_lay, R.id.register_image_lay, R.id.end_and_exit_visit_lay,
      R.id.no_activity_lay})
  public void onClick(View view) {
    switch (view.getId()) {
      case R.id.register_order_lay:
        openOrderDetailFragment(SaleOrderStatus.DRAFT.getId());
        break;
      case R.id.register_payment_lay:
        goToRegisterPaymentFragment();
        break;
      case R.id.register_questionnaire_lay:
        Toast.makeText(mainActivity, "Questionnaire", Toast.LENGTH_SHORT).show();
        break;
      case R.id.register_image_lay:
        startCameraActivity();
        break;
      case R.id.end_and_exit_visit_lay:
        finishVisiting();
        break;
      case R.id.no_activity_lay:
        showNoDialog();
        break;
      case R.id.show_more_tv:
        onShowMoreTapped();
        break;
    }
  }

  private void onShowMoreTapped() {
    if (isShowMore) {
      dropImg.setImageResource(R.drawable.ic_arrow_drop_up);
      showMoreTv.setText(getString(R.string.show_less));
      customerDetailLay.setVisibility(View.VISIBLE);
    } else {
      dropImg.setImageResource(R.drawable.ic_arrow_drop_down);
      showMoreTv.setText(getString(R.string.show_more));
      customerDetailLay.setVisibility(View.GONE);
    }
    isShowMore = !isShowMore;
  }

  private SaleOrderDto createDraftOrder(Customer customer, Long statusID) {
    try {
      SaleOrderDto orderDto = new SaleOrderDto(statusID, customer);
      Long id = saleOrderService.saveOrder(orderDto);
      orderDto.setId(id);
      return orderDto;
    } catch (Exception e) {
      Crashlytics.log(Log.ERROR, "Data Storage Exception",
          "Error in creating draft order " + e.getMessage());
      Log.e(TAG, e.getMessage(), e);
      ToastUtil.toastError(mainActivity, new UnknownSystemException(e));
    }
    return null;
  }

  /**
   * @param statusID Could be DRAFT for both AddInvoice/AddOrder or REJECTED_DRAFT for
   *                 ReturnedOrder
   */
  private void openOrderDetailFragment(Long statusID) {

    orderDto = saleOrderService
        .findOrderDtoByCustomerBackendIdAndStatus(customer.getBackendId(), statusID);
    if (Empty.isEmpty(orderDto) || statusID.equals(SaleOrderStatus.REJECTED_DRAFT.getId())) {
      orderDto = createDraftOrder(customer, statusID);
    }

    if (Empty.isNotEmpty(orderDto) && Empty.isNotEmpty(orderDto.getId())) {
      if (statusID.equals(SaleOrderStatus.REJECTED_DRAFT.getId())) {
        invokeGetRejectedData();
      } else {
        Bundle args = new Bundle();
        args.putLong(Constants.ORDER_ID, orderDto.getId());
        args.putString(Constants.SALE_TYPE, saleType);
        args.putLong(Constants.VISIT_ID, visitId);
        args.putBoolean(Constants.READ_ONLY, false);
        mainActivity.changeFragment(MainActivity.GOODS_LIST_FRAGMENT_ID, args, false);
      }

    } else {
      if (statusID.equals(SaleOrderStatus.REJECTED_DRAFT.getId())) {
        ToastUtil.toastError(mainActivity, R.string.message_cannot_create_rejected_right_now);
      } else if (statusID.equals(SaleOrderStatus.DRAFT.getId()) && saleType
          .equals(ApplicationKeys.SALE_COLD)) {
        ToastUtil.toastError(mainActivity, R.string.message_cannot_create_order_right_now);
      } else {
        ToastUtil.toastError(mainActivity, R.string.message_cannot_create_factor_right_now);
      }
    }
  }

  private void goToRegisterPaymentFragment() {
    Bundle args = new Bundle();
    args.putLong(Constants.CUSTOMER_BACKEND_ID, customer.getBackendId());
    args.putLong(Constants.VISIT_ID, visitId);
    mainActivity.changeFragment(MainActivity.REGISTER_PAYMENT_FRAGMENT, args, false);
  }

  private void invokeGetRejectedData() {

    //TODO: Uncomment this
    /*showProgressDialog(getString(R.string.message_transferring_rejected_goods_data));
    Thread thread = new Thread(() ->
    {
      try {
        DataTransferService dataTransferService = new DataTransferServiceImpl(oldMainActivity);
        rejectedGoodsList = dataTransferService
            .getRejectedData(VisitDetailFragment.this, customer.getBackendId());
        if (rejectedGoodsList != null) {

          final Bundle args = new Bundle();
          args.putLong(Constants.ORDER_ID, orderDto.getId());
          args.putString(Constants.SALE_TYPE, saleType);
          args.putSerializable(Constants.REJECTED_LIST, rejectedGoodsList);
          args.putLong(Constants.VISIT_ID, visitId);
          oldMainActivity.runOnUiThread(() ->
          {
            dismissProgressDialog();
            oldMainActivity.changeFragment(OldMainActivity.ORDER_DETAIL_FRAGMENT_ID, args, false);
          });
        } else {
          runOnUiThread(() ->
          {
            dismissProgressDialog();
            ToastUtil.toastError(getActivity(), getString(R.string.err_reject_order_not_possible));
          });
        }
      } catch (final BusinessException ex) {
        Log.e(TAG, ex.getMessage(), ex);
        runOnUiThread(() -> ToastUtil.toastError(getActivity(), ex));
      } catch (final Exception ex) {
        Crashlytics
            .log(Log.ERROR, "Data transfer", "Error in getting rejected data " + ex.getMessage());
        Log.e(TAG, ex.getMessage(), ex);
        runOnUiThread(() -> ToastUtil.toastError(getActivity(), new UnknownSystemException(ex)));
      }
    });

    thread.start();*/
  }

  private void startCameraActivity() {
    try {
      Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
      // Create a media file name
      String postfix = String.valueOf((new Date().getTime()) % 1000);
      fileUri = MediaUtil.getOutputMediaFileUri(MediaUtil.MEDIA_TYPE_IMAGE,
          Constants.CUSTOMER_PICTURE_DIRECTORY_NAME,
          "IMG_" + customer.getCode() + "_" + postfix); // create a file to save the image
      intent.putExtra(MediaStore.EXTRA_OUTPUT, fileUri); // set the image file name
      if (intent.resolveActivity(getActivity().getPackageManager()) != null) {
        startActivityForResult(intent, CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE);
      }
    } catch (Exception e) {
      Crashlytics.log(Log.ERROR, "General Exception", "Error in opening camera " + e.getMessage());
      e.printStackTrace();
    }
  }

  @Override
  public void onActivityResult(int requestCode, int resultCode, Intent data) {
    if (requestCode == CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE) {
      if (resultCode == RESULT_OK) {
        Bitmap bitmap = ImageUtil.decodeSampledBitmapFromUri(getActivity(), fileUri);
        bitmap = ImageUtil.getScaledBitmap(getActivity(), bitmap);

        String s = ImageUtil.saveTempImage(bitmap, MediaUtil
            .getOutputMediaFile(MediaUtil.MEDIA_TYPE_IMAGE,
                Constants.CUSTOMER_PICTURE_DIRECTORY_NAME,
                "IMG_" + customer.getCode() + "_" + new Date().getTime()));

        CustomerPic cPic = new CustomerPic();
        cPic.setTitle(s);
        cPic.setCustomer_backend_id(customer.getBackendId());

        long typeId = customerService.savePicture(cPic);
        visitService.saveVisitDetail(
            new VisitInformationDetail(visitId, VisitInformationDetailType.TAKE_PICTURE, typeId));
        ToastUtil.toastSuccess(mainActivity, R.string.message_picutre_saved_successfully);

      } else if (resultCode == RESULT_CANCELED) {
        // User cancelled the image capture
      } else {
        // Image capture failed, advise user
      }
    }
  }

  private void finishVisiting() {
    try {
      List<VisitInformationDetail> detailList = visitService.getAllVisitDetailById(visitId);
      if (detailList.size() == 0) {
        DialogUtil.showConfirmDialog(mainActivity, getString(R.string.title_attention),
            getString(R.string.message_error_no_visit_detail_found),
            (dialogInterface, i) -> showWantsDialog());
        return;
      }
      VisitInformation visitInformation = visitService.getVisitInformationById(visitId);
      if (Empty.isEmpty(visitInformation.getxLocation()) || Empty
          .isEmpty(visitInformation.getyLocation())) {
        showDialogForEmptyLocation();
      } else {
        doFinishVisiting();
      }
    } catch (Exception ex) {
      Crashlytics
          .log(Log.ERROR, "General Exception", "Error in finishing visit " + ex.getMessage());
      Log.e(TAG, ex.getMessage(), ex);
      ToastUtil.toastError(getActivity(), new UnknownSystemException(ex));
    }
  }

  private void showWantsDialog() {//TODO shakib old style
    List<VisitInformationDetail> detailList = visitService.getAllVisitDetailById(visitId);
    if (detailList.size() > 0) {
      ToastUtil.toastError(mainActivity, R.string.message_error_wants_denied);
      return;
    }
    Builder dialogBuilder = new Builder(mainActivity);

    List<LabelValue> wants = baseInfoService
        .getAllBaseInfosLabelValuesByTypeId(BaseInfoTypes.WANT_TYPE.getId());
    if (Empty.isEmpty(wants)) {
      ToastUtil.toastError(mainActivity, R.string.message_found_no_wants_information);
      return;
    }

    View wantsDialogView = mainActivity.getLayoutInflater().inflate(R.layout.dialog_wants, null);
    final Spinner wantsSpinner = (Spinner) wantsDialogView.findViewById(R.id.wantsSP);
    CheckBox notVisitedCb = (CheckBox) wantsDialogView.findViewById((R.id.not_visited_cb));

    notVisitedCb.setOnCheckedChangeListener(
        (compoundButton, isChecked) -> wantsSpinner.setEnabled(!isChecked));
    LabelValueArrayAdapter labelValueArrayAdapter = new LabelValueArrayAdapter(mainActivity,
        wants);
    wantsSpinner.setAdapter(labelValueArrayAdapter);

    dialogBuilder.setView(wantsDialogView);
    dialogBuilder.setPositiveButton(R.string.button_ok, (dialog, which) ->
    {
      LabelValue selectedItem = (LabelValue) wantsSpinner.getSelectedItem();
      updateVisitResult(selectedItem, notVisitedCb.isChecked());
    });
    dialogBuilder.setNegativeButton(R.string.button_cancel, (dialog, which) ->
    {
    });
    dialogBuilder.setTitle(R.string.title_save_want);
    dialogBuilder.create().show();
  }

  private void showNoDialog() {
    List<VisitInformationDetail> detailList = visitService.getAllVisitDetailById(visitId);
    if (detailList.size() > 0) {
      ToastUtil.toastError(mainActivity, R.string.message_error_wants_denied);
      return;
    }
    AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(mainActivity);

    List<LabelValue> wants = baseInfoService
        .getAllBaseInfosLabelValuesByTypeId(BaseInfoTypes.WANT_TYPE.getId());
    if (Empty.isEmpty(wants)) {
      ToastUtil.toastError(mainActivity, R.string.message_found_no_wants_information);
      return;
    }
    LayoutInflater inflater = mainActivity.getLayoutInflater();
    View dialogView = inflater.inflate(R.layout.dialog_register_no, null);
    dialogBuilder.setView(dialogView);

    Button registerNoBtn = (Button) dialogView.findViewById(R.id.register_btn);
    TextView cancelTv = (TextView) dialogView.findViewById(R.id.cancel_tv);
    Spinner noSpinner = (Spinner) dialogView.findViewById(R.id.no_spinner);
    AlertDialog alertDialog = dialogBuilder.create();

    wants.add(new LabelValue(-1l, getString(R.string.reason_register_no)));
    RegisterNoLabelValueArrayAdapter labelValueArrayAdapter = new RegisterNoLabelValueArrayAdapter(mainActivity,
        wants);
    noSpinner.setAdapter(labelValueArrayAdapter);
    noSpinner.setSelection(wants.size() - 1);
    alertDialog.show();
    registerNoBtn.setOnClickListener(v -> {
      LabelValue selectedItem = (LabelValue) noSpinner.getSelectedItem();
      updateVisitResult(selectedItem, false);
    });
    cancelTv.setOnClickListener(v -> alertDialog.cancel());
  }

  private void updateVisitResult(LabelValue selectedItem, boolean notVisited) {
    try {
      VisitInformationDetail visitInformationDetail = new VisitInformationDetail(
          visitId,
          notVisited ? VisitInformationDetailType.NONE : VisitInformationDetailType.NO_ORDER,
          selectedItem.getValue());
      visitService.saveVisitDetail(visitInformationDetail);
    } catch (Exception ex) {
      Crashlytics.log(Log.ERROR, "Data Storage Exception",
          "Error in updating visit result " + ex.getMessage());
      ToastUtil.toastError(mainActivity, new UnknownSystemException(ex));
      Log.e(TAG, ex.getMessage(), ex);
    }
  }

  private void showDialogForEmptyLocation() {//TODO Shakib old style
    Builder builder = new Builder(getActivity());
    builder.setTitle(getString(R.string.visit_location));
    builder.setMessage(getString(R.string.visit_empty_location_message));
    builder.setPositiveButton(getString(R.string.visit_empty_location_dialog_try_again),
        (dialogInterface, i) ->
        {
          dialogInterface.dismiss();
          tryFindingLocation();
        });

    builder.setNegativeButton(getString(R.string.visit_empty_location_dialog_finish),
        (dialogInterface, i) ->
        {
          dialogInterface.dismiss();
          doFinishVisiting();
        });

    builder.create().show();
  }

  private void tryFindingLocation() {
    try {
      final ProgressDialog progressDialog = new ProgressDialog(mainActivity);
      progressDialog.setIndeterminate(true);
      progressDialog.setCancelable(Boolean.FALSE);
      progressDialog.setIcon(R.drawable.ic_action_info);
      progressDialog.setTitle(R.string.message_please_wait);
      progressDialog.setMessage(getString(R.string.message_please_wait_finding_your_location));
      progressDialog.show();

      locationService.findCurrentLocation(new FindLocationListener() {
        @Override
        public void foundLocation(Location location) {
          progressDialog.dismiss();
          try {
            visitService.updateVisitLocation(visitId, location);
          } catch (Exception e) {
            Crashlytics.log(Log.ERROR, "Data Storage Exception",
                "Error in updating visit location " + e.getMessage());
            Log.e(TAG, e.getMessage(), e);
          }

          mainActivity.runOnUiThread(() -> showFoundLocationDialog());
        }

        @Override
        public void timeOut() {
          progressDialog.dismiss();
          mainActivity.runOnUiThread(() ->
          {
            ToastUtil.toastError(getActivity(), getString(R.string.visit_found_no_location));
            showDialogForEmptyLocation();
          });
        }
      });

    } catch (BusinessException ex) {
      Log.e(TAG, ex.getMessage(), ex);
      ToastUtil.toastError(getActivity(), ex);
    } catch (Exception ex) {
      Crashlytics
          .log(Log.ERROR, "Location Service", "Error in finding location " + ex.getMessage());
      Log.e(TAG, ex.getMessage(), ex);
      ToastUtil.toastError(getActivity(), new UnknownSystemException(ex));
    }
  }

  private void showFoundLocationDialog() {//TODO shakib old style
    Builder builder = new Builder(getActivity());
    builder.setTitle(getString(R.string.visit_location));
    builder.setMessage(getString(R.string.visit_found_location_message));
    builder.setPositiveButton(getString(R.string.finish_visit),
        (dialogInterface, i) -> doFinishVisiting());

    builder.create().show();
  }

  private void doFinishVisiting() {
    try {
      visitService.finishVisiting(visitId);
      Customer customer = customerService.getCustomerById(customerId);
      saleOrderService.deleteForAllCustomerOrdersByStatus(customer.getBackendId(),
          SaleOrderStatus.DRAFT.getId());
      mainActivity.removeFragment(this);
    } catch (Exception ex) {
      Crashlytics
          .log(Log.ERROR, "General Exception", "Error in finishing visit " + ex.getMessage());
      Log.e(TAG, ex.getMessage(), ex);
      ToastUtil.toastError(mainActivity, new UnknownSystemException(ex));
    }
  }

  @Override
  public void onResume() {
    super.onResume();
    if (getView() == null) {
      return;
    }

    getView().setFocusableInTouchMode(true);
    getView().requestFocus();
    getView().setOnKeyListener((v, keyCode, event) ->
    {
      if (event.getAction() == KeyEvent.ACTION_UP && keyCode == KeyEvent.KEYCODE_BACK) {
        finishVisiting();
        return true;
      }
      return false;
    });
  }

}
