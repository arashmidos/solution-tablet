package com.parsroyal.solutiontablet.ui.fragment;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import butterknife.BindView;
import butterknife.ButterKnife;
import com.crashlytics.android.Crashlytics;
import com.parsroyal.solutiontablet.R;
import com.parsroyal.solutiontablet.biz.impl.RejectedGoodsDataTransferBizImpl;
import com.parsroyal.solutiontablet.constants.BaseInfoTypes;
import com.parsroyal.solutiontablet.constants.Constants;
import com.parsroyal.solutiontablet.constants.SaleOrderStatus;
import com.parsroyal.solutiontablet.constants.StatusCodes;
import com.parsroyal.solutiontablet.constants.VisitInformationDetailType;
import com.parsroyal.solutiontablet.data.entity.Customer;
import com.parsroyal.solutiontablet.data.entity.CustomerPic;
import com.parsroyal.solutiontablet.data.entity.Goods;
import com.parsroyal.solutiontablet.data.entity.VisitInformation;
import com.parsroyal.solutiontablet.data.entity.VisitInformationDetail;
import com.parsroyal.solutiontablet.data.event.ActionEvent;
import com.parsroyal.solutiontablet.data.event.ErrorEvent;
import com.parsroyal.solutiontablet.data.model.GoodsDtoList;
import com.parsroyal.solutiontablet.data.model.LabelValue;
import com.parsroyal.solutiontablet.data.model.SaleOrderDto;
import com.parsroyal.solutiontablet.exception.BusinessException;
import com.parsroyal.solutiontablet.exception.UnknownSystemException;
import com.parsroyal.solutiontablet.service.LocationService;
import com.parsroyal.solutiontablet.service.impl.BaseInfoServiceImpl;
import com.parsroyal.solutiontablet.service.impl.CustomerServiceImpl;
import com.parsroyal.solutiontablet.service.impl.LocationServiceImpl;
import com.parsroyal.solutiontablet.service.impl.SaleOrderServiceImpl;
import com.parsroyal.solutiontablet.service.impl.SettingServiceImpl;
import com.parsroyal.solutiontablet.service.impl.VisitServiceImpl;
import com.parsroyal.solutiontablet.ui.MainActivity;
import com.parsroyal.solutiontablet.ui.adapter.CustomerDetailViewPagerAdapter;
import com.parsroyal.solutiontablet.ui.adapter.LabelValueArrayAdapterWithHint;
import com.parsroyal.solutiontablet.ui.fragment.dialogFragment.SingleDataTransferDialogFragment;
import com.parsroyal.solutiontablet.ui.observer.FindLocationListener;
import com.parsroyal.solutiontablet.util.CameraManager;
import com.parsroyal.solutiontablet.util.DialogUtil;
import com.parsroyal.solutiontablet.util.Empty;
import com.parsroyal.solutiontablet.util.ImageUtil;
import com.parsroyal.solutiontablet.util.Logger;
import com.parsroyal.solutiontablet.util.MediaUtil;
import com.parsroyal.solutiontablet.util.MultiScreenUtility;
import com.parsroyal.solutiontablet.util.ToastUtil;
import com.parsroyal.solutiontablet.util.constants.ApplicationKeys;
import java.io.File;
import java.util.Date;
import java.util.List;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

public class VisitDetailFragment extends BaseFragment {

  private static final String TAG = VisitDetailFragment.class.getName();
  private static final int RESULT_OK = -1;
  private static final int RESULT_CANCELED = 0;

  @BindView(R.id.tabs)
  TabLayout tabs;
  @BindView(R.id.viewpager)
  ViewPager viewpager;

  private Uri fileUri;
  private CustomerServiceImpl customerService;
  private MainActivity mainActivity;
  private SaleOrderServiceImpl saleOrderService;
  private long customerId;
  private long visitId;
  private VisitServiceImpl visitService;
  private BaseInfoServiceImpl baseInfoService;
  private LocationService locationService;
  private CustomerDetailViewPagerAdapter viewPagerAdapter;
  private CustomerInfoFragment customerInfoFragment;
  private OrderListFragment orderListFragment;
  private PaymentListFragment paymentListFragment;
  private ReturnListFragment returnListFragment;
  private PictureFragment pictureFragment;
  private AllQuestionnaireListFragment allQuestionnaireListFragment;
  private Customer customer;
  private SaleOrderDto orderDto;
  private String saleType;


  public VisitDetailFragment() {
    // Required empty public constructor
  }

  public static VisitDetailFragment newInstance() {
    return new VisitDetailFragment();
  }

  @Override
  public void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
  }

  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container,
      Bundle savedInstanceState) {

    // Inflate the layout for this fragment
    View view = inflater.inflate(R.layout.fragment_new_visit_detail, container, false);
    ButterKnife.bind(this, view);
    Bundle args = getArguments();
    if (Empty.isNotEmpty(args)) {
      customerId = args.getLong(Constants.CUSTOMER_ID);
      mainActivity = (MainActivity) getActivity();
      baseInfoService = new BaseInfoServiceImpl(mainActivity);
      customerService = new CustomerServiceImpl(mainActivity);
      locationService = new LocationServiceImpl(mainActivity);
      visitService = new VisitServiceImpl(mainActivity);
      customer = customerService.getCustomerById(customerId);
      if (customer == null) {
        return inflater.inflate(R.layout.empty_view, container, false);
      }
      saleOrderService = new SaleOrderServiceImpl(mainActivity);

      visitId = args.getLong(Constants.ORIGIN_VISIT_ID);
      saleType = new SettingServiceImpl(mainActivity)
          .getSettingValue(ApplicationKeys.SETTING_SALE_TYPE);

      tabs.setupWithViewPager(viewpager);
      initFragments();
      setUpViewPager();
      viewpager.setCurrentItem(viewPagerAdapter.getCount());
      viewpager.setOffscreenPageLimit(viewPagerAdapter.getCount());
      if (MultiScreenUtility.isTablet(mainActivity)) {
        mainActivity.changeTitle("");
      }
      return view;
    } else {
      return inflater.inflate(R.layout.empty_view, container, false);
    }
  }

  public void finishVisiting() {
    try {
      List<VisitInformationDetail> detailList = visitService.getAllVisitDetailById(visitId);
      if (detailList.size() == 0) {
        DialogUtil.showConfirmDialog(mainActivity, mainActivity.getString(R.string.title_attention),
            mainActivity.getString(R.string.message_error_no_visit_detail_found),
            (dialogInterface, i) -> showNoDialog());
        return;
      }
      VisitInformation visitInformation = visitService.getVisitInformationById(visitId);
      if (Empty.isEmpty(visitInformation.getxLocation()) || Empty
          .isEmpty(visitInformation.getyLocation())) {
        showDialogForEmptyLocation();
      } else {
        saveVisit();
      }
    } catch (Exception ex) {
      Crashlytics
          .log(Log.ERROR, "General Exception", "Error in finishing visit " + ex.getMessage());
      Log.e(TAG, ex.getMessage(), ex);
      ToastUtil.toastError(mainActivity, new UnknownSystemException(ex));
    }
  }

  public void showNoDialog() {
    List<VisitInformationDetail> detailList = visitService
        .searchVisitDetail(visitId, VisitInformationDetailType.CREATE_ORDER);
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
    TextView errorMessage = (TextView) dialogView.findViewById(R.id.error_msg);
    Spinner noSpinner = (Spinner) dialogView.findViewById(R.id.no_spinner);
    AlertDialog alertDialog = dialogBuilder.create();

    wants.add(new LabelValue(-1l, mainActivity.getString(R.string.reason_register_no)));
    LabelValueArrayAdapterWithHint labelValueArrayAdapter = new LabelValueArrayAdapterWithHint(
        mainActivity, wants);
    noSpinner.setAdapter(labelValueArrayAdapter);
    noSpinner.setSelection(wants.size() - 1);
    alertDialog.show();
    registerNoBtn.setOnClickListener(v -> {
      LabelValue selectedItem = (LabelValue) noSpinner.getSelectedItem();
      if (selectedItem.getValue() != -1l) {
        errorMessage.setVisibility(View.INVISIBLE);
        updateVisitResult(selectedItem, false);
        alertDialog.dismiss();
      } else {
        errorMessage.setVisibility(View.VISIBLE);
      }
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
      doFinishVisiting();

    } catch (Exception ex) {
      Logger.sendError("Data Storage Exception",
          "Error in updating visit result " + ex.getMessage());
      ToastUtil.toastError(mainActivity, new UnknownSystemException(ex));
      Log.e(TAG, ex.getMessage(), ex);
    }
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

  private void tryFindingLocation() {
    try {
      final ProgressDialog progressDialog = new ProgressDialog(mainActivity);
      progressDialog.setIndeterminate(true);
      progressDialog.setCancelable(Boolean.FALSE);
      progressDialog.setIcon(R.drawable.ic_action_info);
      progressDialog.setTitle(R.string.message_please_wait);
      progressDialog
          .setMessage(mainActivity.getString(R.string.message_please_wait_finding_your_location));
      progressDialog.show();

      locationService.findCurrentLocation(new FindLocationListener() {
        @Override
        public void foundLocation(Location location) {
          progressDialog.dismiss();
          try {
            visitService.updateVisitLocation(visitId, location);
          } catch (Exception e) {
            Logger.sendError("Data Storage Exception",
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
            ToastUtil
                .toastError(mainActivity, mainActivity.getString(R.string.visit_found_no_location));
            showDialogForEmptyLocation();
          });
        }
      });

    } catch (BusinessException ex) {
      Log.e(TAG, ex.getMessage(), ex);
      ToastUtil.toastError(mainActivity, ex);
    } catch (Exception ex) {
      Crashlytics
          .log(Log.ERROR, "Location Service", "Error in finding location " + ex.getMessage());
      Log.e(TAG, ex.getMessage(), ex);
      ToastUtil.toastError(mainActivity, new UnknownSystemException(ex));
    }
  }

  private void showFoundLocationDialog() {
    DialogUtil.showCustomDialog(mainActivity, mainActivity.getString(R.string.visit_location),
        mainActivity.getString(R.string.visit_found_location_message),
        mainActivity.getString(R.string.finish_visit),
        (dialog, which) -> saveVisit(), "", null, Constants.ICON_MESSAGE);
  }

  private void saveVisit() {
    DialogUtil.showCustomDialog(mainActivity, mainActivity.getString(R.string.send_data),
        getString(R.string.message_confirm_send_visit_data_instantly), "نمایش جزئیات ارسال",
        (dialog, which) -> {
          visitService.finishVisiting(visitId);
          FragmentTransaction ft = mainActivity.getSupportFragmentManager().beginTransaction();
          Bundle args = new Bundle();
          args.putLong(Constants.VISIT_ID, visitId);
          SingleDataTransferDialogFragment singleDataTransferDialogFragment = SingleDataTransferDialogFragment
              .newInstance(args);
          singleDataTransferDialogFragment.show(ft, "data_transfer");
          dialog.dismiss();
        }, mainActivity.getString(R.string.cancel_btn), (dialog, which) -> doFinishVisiting(),
        Constants.ICON_MESSAGE);
  }

  private void showDialogForEmptyLocation() {
    DialogUtil.showCustomDialog(mainActivity, mainActivity.getString(R.string.visit_location),
        mainActivity.getString(R.string.visit_empty_location_message),
        mainActivity.getString(R.string.visit_empty_location_dialog_try_again),
        (dialog, which) -> tryFindingLocation(),
        mainActivity.getString(R.string.visit_empty_location_dialog_finish),
        (dialog, which) -> saveVisit(), Constants.ICON_MESSAGE);
  }

  public Customer getCustomer() {
    return customer;
  }

  public long getCustomerId() {
    return customerId;
  }

  public long getCustomerBackendId() {
    return customer.getBackendId();
  }

  public long getVisitId() {
    return visitId;
  }

  private void initFragments() {
    Bundle arguments = getArguments();
    arguments.putLong(Constants.CUSTOMER_BACKEND_ID, customer.getBackendId());
    paymentListFragment = PaymentListFragment.newInstance(arguments, this);
    pictureFragment = PictureFragment.newInstance(arguments);
    orderListFragment = OrderListFragment.newInstance(arguments, this);
    returnListFragment = ReturnListFragment.newInstance(arguments, this);
    customerInfoFragment = CustomerInfoFragment.newInstance(arguments, this);
    allQuestionnaireListFragment = AllQuestionnaireListFragment.newInstance(arguments);
  }

  private void setUpViewPager() {
    viewPagerAdapter = new CustomerDetailViewPagerAdapter(getChildFragmentManager());
    viewPagerAdapter.add(pictureFragment, getString(R.string.images));
    viewPagerAdapter.add(allQuestionnaireListFragment, getString(R.string.questionnaire));
    viewPagerAdapter.add(paymentListFragment, getString(R.string.payments));
    viewPagerAdapter.add(returnListFragment, getString(R.string.returns));
    viewPagerAdapter.add(orderListFragment, getString(R.string.orders));
    viewPagerAdapter.add(customerInfoFragment, getString(R.string.customer_information));
    viewpager.setAdapter(viewPagerAdapter);
  }

  @Override
  public int getFragmentId() {
    return MainActivity.VISIT_DETAIL_FRAGMENT_ID;
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
                "IMG_" + customer.getCode() + "_" + (new Date().getTime())%1000));

        if (!s.equals("")) {
          File fdelete = new File(fileUri.getPath());
          if (fdelete.exists()) {
            fdelete.delete();
          }
        }

        CustomerPic cPic = new CustomerPic();
        cPic.setTitle(s);
        cPic.setCustomerBackendId(customer.getBackendId());
        cPic.setVisitId(visitId);

        long typeId = customerService.savePicture(cPic);
        visitService.saveVisitDetail(
            new VisitInformationDetail(visitId, VisitInformationDetailType.TAKE_PICTURE, typeId));
        ToastUtil.toastSuccess(mainActivity, R.string.message_picutre_saved_successfully);
        pictureFragment.update();

      } else if (resultCode == RESULT_CANCELED) {
        // User cancelled the image capture
      } else {
        // Image capture failed, advise user
      }
    }
  }

  /**
   * @param statusID Could be DRAFT for both AddInvoice/AddOrder or REJECTED_DRAFT for
   * ReturnedOrder
   */
  public void openOrderDetailFragment(Long statusID) {

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
        args.putString(Constants.PAGE_STATUS, Constants.NEW);
        mainActivity.changeFragment(MainActivity.GOODS_LIST_FRAGMENT_ID, args, true);
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

  private SaleOrderDto createDraftOrder(Customer customer, Long statusID) {
    try {
      SaleOrderDto orderDto = new SaleOrderDto(statusID, customer);
      Long id = saleOrderService.saveOrder(orderDto);
      orderDto.setId(id);
      return orderDto;
    } catch (Exception e) {
      Logger.sendError("Data Storage Exception", "Error in creating draft order " + e.getMessage());
      Log.e(TAG, e.getMessage(), e);
      ToastUtil.toastError(mainActivity, new UnknownSystemException(e));
    }
    return null;
  }

  private void invokeGetRejectedData() {

    DialogUtil.showProgressDialog(mainActivity,
        getString(R.string.message_transferring_rejected_goods_data));

    new RejectedGoodsDataTransferBizImpl(mainActivity).getAllRejectedData(customer.getBackendId());
  }

  @Override
  public void onResume() {
    super.onResume();
    EventBus.getDefault().register(this);
    mainActivity.showNav();
  }

  @Override
  public void onPause() {
    super.onPause();
    EventBus.getDefault().unregister(this);
  }

  @Subscribe
  public void getMessage(ActionEvent event) {
    if (event.getStatusCode() == StatusCodes.ACTION_START_CAMERA) {
      if (!CameraManager.checkPermissions(mainActivity)) {
        CameraManager.requestPermissions(mainActivity);
      } else {
        startCameraActivity();
      }
    } else if (event.getStatusCode() == StatusCodes.SUCCESS) {
      doFinishVisiting();
    }
  }

  @Subscribe
  public void getMessage(ErrorEvent errorEvent) {
    if (!errorEvent.getMessage().equals("reject")) {
      DialogUtil.dismissProgressDialog();
      ToastUtil.toastError(mainActivity, getString(R.string.error_unknown_system_exception));
    }
  }

  @Subscribe
  public void getMessage(GoodsDtoList goodsDtoList) {
    DialogUtil.dismissProgressDialog();

    List<Goods> goodsList = goodsDtoList.getGoodsDtoList();
    if (goodsList != null) {
      final Bundle args = new Bundle();
      args.putLong(Constants.ORDER_ID, orderDto.getId());
      args.putString(Constants.SALE_TYPE, saleType);
      args.putSerializable(Constants.REJECTED_LIST, goodsDtoList);
      args.putLong(Constants.VISIT_ID, visitId);
      args.putBoolean(Constants.READ_ONLY, false);
      args.putString(Constants.PAGE_STATUS, Constants.NEW);

      mainActivity.changeFragment(MainActivity.GOODS_LIST_FRAGMENT_ID, args, false);
    } else {
      ToastUtil.toastError(getActivity(), getString(R.string.err_reject_order_not_possible));
    }
  }

  public void startCameraActivity() {
    String postfix = String.valueOf((new Date().getTime()) % 1000);
    fileUri = MediaUtil.getOutputMediaFileUri(mainActivity, MediaUtil.MEDIA_TYPE_IMAGE,
        Constants.CUSTOMER_PICTURE_DIRECTORY_NAME,
        "IMG_" + customer.getCode() + "_" + postfix); // create a file to save the image
    CameraManager.startCameraActivity(mainActivity, fileUri, this);
  }
}
