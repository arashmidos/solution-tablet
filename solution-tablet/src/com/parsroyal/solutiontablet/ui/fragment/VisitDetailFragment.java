package com.parsroyal.solutiontablet.ui.fragment;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.FragmentTransaction;
import androidx.viewpager.widget.ViewPager;
import butterknife.BindView;
import butterknife.ButterKnife;
import com.crashlytics.android.Crashlytics;
import com.google.android.material.tabs.TabLayout;
import com.parsroyal.solutiontablet.R;
import com.parsroyal.solutiontablet.SolutionTabletApplication;
import com.parsroyal.solutiontablet.biz.impl.RejectedGoodsDataTransferBizImpl;
import com.parsroyal.solutiontablet.constants.Authority;
import com.parsroyal.solutiontablet.constants.BaseInfoTypes;
import com.parsroyal.solutiontablet.constants.Constants;
import com.parsroyal.solutiontablet.constants.SaleOrderStatus;
import com.parsroyal.solutiontablet.constants.StatusCodes;
import com.parsroyal.solutiontablet.constants.VisitInformationDetailType;
import com.parsroyal.solutiontablet.data.entity.Customer;
import com.parsroyal.solutiontablet.data.entity.CustomerPic;
import com.parsroyal.solutiontablet.data.entity.Goods;
import com.parsroyal.solutiontablet.data.entity.Position;
import com.parsroyal.solutiontablet.data.entity.VisitInformation;
import com.parsroyal.solutiontablet.data.entity.VisitInformationDetail;
import com.parsroyal.solutiontablet.data.event.ActionEvent;
import com.parsroyal.solutiontablet.data.event.ErrorEvent;
import com.parsroyal.solutiontablet.data.model.GoodsDtoList;
import com.parsroyal.solutiontablet.data.model.LabelValue;
import com.parsroyal.solutiontablet.data.model.SaleOrderDto;
import com.parsroyal.solutiontablet.exception.UnknownSystemException;
import com.parsroyal.solutiontablet.service.impl.BaseInfoServiceImpl;
import com.parsroyal.solutiontablet.service.impl.CustomerServiceImpl;
import com.parsroyal.solutiontablet.service.impl.SaleOrderServiceImpl;
import com.parsroyal.solutiontablet.service.impl.VisitServiceImpl;
import com.parsroyal.solutiontablet.ui.activity.MainActivity;
import com.parsroyal.solutiontablet.ui.adapter.CustomerDetailViewPagerAdapter;
import com.parsroyal.solutiontablet.ui.adapter.LabelValueArrayAdapterWithHint;
import com.parsroyal.solutiontablet.ui.fragment.dialogFragment.SingleDataTransferDialogFragment;
import com.parsroyal.solutiontablet.util.CameraManager;
import com.parsroyal.solutiontablet.util.DialogUtil;
import com.parsroyal.solutiontablet.util.Empty;
import com.parsroyal.solutiontablet.util.ImageUtil;
import com.parsroyal.solutiontablet.util.LocationUtil;
import com.parsroyal.solutiontablet.util.Logger;
import com.parsroyal.solutiontablet.util.MediaUtil;
import com.parsroyal.solutiontablet.util.PreferenceHelper;
import com.parsroyal.solutiontablet.util.SaleUtil;
import com.parsroyal.solutiontablet.util.ToastUtil;
import java.io.File;
import java.util.List;
import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;
import java.util.UUID;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import timber.log.Timber;

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
  private CustomerDetailViewPagerAdapter viewPagerAdapter;
  private CustomerInfoFragment customerInfoFragment;
  private OrderListFragment orderListFragment;
  private DeliveryListFragment deliveryListFragment;
  private PaymentListFragment paymentListFragment;
  private ReturnListFragment returnListFragment;
  private PictureFragment pictureFragment;
  private FreeOrderListFragment freeOrderListFragment;
  private AllQuestionnaireListFragment allQuestionnaireListFragment;
  private Customer customer;
  private SaleOrderDto orderDto;
  private Timer timer;
  private int seconds = 0, minutes = 0, hours = 0;
  private ReturnListFragment requestReturnListFragment;

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
  public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
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
      visitService = new VisitServiceImpl(mainActivity);
      customer = customerService.getCustomerById(customerId);
      if (customer == null) {
        return inflater.inflate(R.layout.empty_view, container, false);
      }
      saleOrderService = new SaleOrderServiceImpl(mainActivity);

      visitId = args.getLong(Constants.ORIGIN_VISIT_ID);

      tabs.setupWithViewPager(viewpager);
      initFragments();
      setUpViewPager();
      viewpager.setCurrentItem(viewPagerAdapter.getCount());
      viewpager.setOffscreenPageLimit(viewPagerAdapter.getCount());

      setUpTimer();
      return view;
    } else {
      return inflater.inflate(R.layout.empty_view, container, false);
    }
  }

  private void setUpTimer() {
    //Stop previous timer
    mainActivity.showTimer();
    if (timer != null) {
      timer.cancel();
      timer.purge();
      timer = null;
    }
    timer = new Timer();

    timer.scheduleAtFixedRate(new TimerTask() {
      @Override
      public void run() {
        if (seconds == 60) {
          seconds = 0;
          minutes++;
          if (minutes == 60) {
            minutes = 0;
            hours++;
          }
        }
        if (hours > 0) {
          mainActivity
              .setTimer(String.format(Locale.US, "%02d:%02d:%02d", hours, minutes, seconds));
        } else {
          mainActivity.setTimer(String.format(Locale.US, "%02d:%02d", minutes, seconds));
        }
        seconds += 1;
      }
    }, 0, 1000);
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
      Timber.e(ex);
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

    Button registerNoBtn = dialogView.findViewById(R.id.register_btn);
    TextView cancelTv = dialogView.findViewById(R.id.cancel_tv);
    TextView errorMessage = dialogView.findViewById(R.id.error_msg);
    Spinner noSpinner = dialogView.findViewById(R.id.no_spinner);
    AlertDialog alertDialog = dialogBuilder.create();

    wants.add(new LabelValue(-1L, mainActivity.getString(R.string.reason_register_no)));
    LabelValueArrayAdapterWithHint labelValueArrayAdapter = new LabelValueArrayAdapterWithHint(
        mainActivity, wants);
    noSpinner.setAdapter(labelValueArrayAdapter);
    noSpinner.setSelection(wants.size() - 1);
    alertDialog.show();
    registerNoBtn.setOnClickListener(v -> {
      LabelValue selectedItem = (LabelValue) noSpinner.getSelectedItem();
      if (selectedItem.getValue() != -1L) {
        errorMessage.setVisibility(View.INVISIBLE);
        updateVisitResult(selectedItem);
        alertDialog.dismiss();
      } else {
        errorMessage.setVisibility(View.VISIBLE);
      }
    });
    cancelTv.setOnClickListener(v -> alertDialog.cancel());
  }

  private void updateVisitResult(LabelValue selectedItem) {
    try {
      VisitInformationDetail visitInformationDetail = new VisitInformationDetail(visitId,
          VisitInformationDetailType.NO_ORDER, selectedItem.getValue());
      visitService.saveVisitDetail(visitInformationDetail);
      doFinishVisiting();

    } catch (Exception ex) {
      Logger.sendError("Data Storage Exception",
          "Error in updating visit result " + ex.getMessage());
      ToastUtil.toastError(mainActivity, new UnknownSystemException(ex));
      Timber.e(ex);
    }
  }

  private void doFinishVisiting() {
    try {

      visitService.finishVisiting(visitId,
          LocationUtil.distanceToCustomer(customer.getxLocation(), customer.getyLocation()));
      saleOrderService.deleteForAllCustomerOrdersByStatus(customer.getBackendId(),
          SaleOrderStatus.DRAFT.getId());
      mainActivity.removeFragment(this);
    } catch (Exception ex) {

      Timber.e(ex);
      ToastUtil.toastError(mainActivity, new UnknownSystemException(ex));
    }
  }

  private void tryFindingLocation() {

    Position position = SolutionTabletApplication.getInstance().getLastSavedPosition();

    if (Empty.isNotEmpty(position)) {
      visitService.updateVisitLocation(visitId, position);
      showFoundLocationDialog();
    } else {
      showDialogForEmptyLocation();
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
          visitService.finishVisiting(visitId,
              LocationUtil.distanceToCustomer(customer.getxLocation(), customer.getyLocation()));
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
    if (PreferenceHelper.isDistributor()) {
      deliveryListFragment = DeliveryListFragment.newInstance(arguments, this);
    } else {
      orderListFragment = OrderListFragment.newInstance(arguments, this);
      freeOrderListFragment = FreeOrderListFragment.newInstance(arguments, this);
    }
    returnListFragment = ReturnListFragment.newInstance(arguments, this);
    requestReturnListFragment = ReturnListFragment.newInstance(arguments, this);
    customerInfoFragment = CustomerInfoFragment.newInstance(arguments, this);
    allQuestionnaireListFragment = AllQuestionnaireListFragment.newInstance(arguments);
  }

  private void setUpViewPager() {
    viewPagerAdapter = new CustomerDetailViewPagerAdapter(getChildFragmentManager());
    if (SolutionTabletApplication.getInstance().hasAccess(Authority.ADD_PICTURE)) {
      viewPagerAdapter.add(pictureFragment, getString(R.string.images));
    }
    if (SolutionTabletApplication.getInstance().hasAccess(Authority.ADD_QUESTIONNAIRE)) {
      viewPagerAdapter.add(allQuestionnaireListFragment, getString(R.string.questionnaire));
    }
    if (SolutionTabletApplication.getInstance().hasAccess(Authority.ADD_PAYMENT)) {
      viewPagerAdapter.add(paymentListFragment, getString(R.string.payments));
    }
    if (SolutionTabletApplication.getInstance().hasAccess(Authority.ADD_REJECT)) {
      viewPagerAdapter.add(returnListFragment, getString(R.string.returns));
    }
    if (SolutionTabletApplication.getInstance().hasAccess(Authority.ADD_REQUEST_REJECT)) {
      viewPagerAdapter.add(requestReturnListFragment, getString(R.string.request_returns));
    }
    if (PreferenceHelper.isDistributor()) {
      if (SolutionTabletApplication.getInstance().hasAccess(Authority.ADD_DELIVERY)) {
        viewPagerAdapter.add(deliveryListFragment, getString(R.string.orders_for_delivery));
      }
    } else {
      if (SolutionTabletApplication.getInstance().hasAccess(Authority.ADD_FREE_ORDER)) {
        viewPagerAdapter.add(freeOrderListFragment, getString(R.string.free_orders));
      }
      if (SolutionTabletApplication.getInstance().hasAccess(Authority.ADD_ORDER)) {
        viewPagerAdapter.add(orderListFragment, getString(R.string.orders));
      }
    }
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
        Bitmap bitmap = ImageUtil.decodeSampledBitmapFromUri(mainActivity, fileUri);
        bitmap = ImageUtil.getScaledBitmap(getActivity(), bitmap);

        String fileName = UUID.randomUUID().toString();
        String s = ImageUtil.saveTempImage(bitmap, MediaUtil
            .getOutputMediaFile(MediaUtil.MEDIA_TYPE_IMAGE,
                Constants.CUSTOMER_PICTURE_DIRECTORY_NAME, fileName));

        if (!s.equals("")) {//TODO:NOT working
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
      }
    }
  }

  /**
   * @param statusID Could be DRAFT for both AddInvoice/AddOrder or REJECTED_DRAFT for
   * ReturnedOrder
   */
  void openOrderDetailFragment(Long statusID, boolean isCashOrder) {

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
        args.putLong(Constants.VISIT_ID, visitId);
        args.putBoolean(Constants.READ_ONLY, false);
        args.putString(Constants.PAGE_STATUS, Constants.NEW);
        args.putBoolean(Constants.CASH_ORDER, isCashOrder);
        args.putBoolean(Constants.REQUEST_REJECT_ORDER, SaleUtil.isRequestReject(statusID));
        mainActivity.changeFragment(MainActivity.GOODS_LIST_FRAGMENT_ID, args, true);
      }

    } else {
      if (statusID.equals(SaleOrderStatus.REJECTED_DRAFT.getId())) {
        ToastUtil.toastError(mainActivity, R.string.message_cannot_create_rejected_right_now);
      } else if (statusID.equals(SaleOrderStatus.DRAFT.getId()) && PreferenceHelper.isVisitor()) {
        ToastUtil.toastError(mainActivity, R.string.message_cannot_create_order_right_now);
      } else if (statusID.equals(SaleOrderStatus.REQUEST_REJECTED_DRAFT.getId())) {
        ToastUtil.toastError(mainActivity, R.string.message_cannot_create_request_reject_right_now);
      } else {
        ToastUtil.toastError(mainActivity, R.string.message_cannot_create_factor_right_now);
      }
    }
  }


  public void openFreeOrderDetailFragment() {

    orderDto = saleOrderService.findOrderDtoByCustomerBackendIdAndStatus(customer.getBackendId(),
        SaleOrderStatus.FREE_ORDER_DRAFT.getId());
    if (Empty.isEmpty(orderDto)) {
      orderDto = createDraftOrder(customer, SaleOrderStatus.FREE_ORDER_DRAFT.getId());
    }

    if (Empty.isNotEmpty(orderDto) && Empty.isNotEmpty(orderDto.getId())) {
      Bundle args = new Bundle();
      args.putLong(Constants.ORDER_ID, orderDto.getId());
      args.putLong(Constants.VISIT_ID, visitId);
      args.putBoolean(Constants.READ_ONLY, false);
      args.putString(Constants.PAGE_STATUS, Constants.NEW);
      args.putBoolean(Constants.COMPLIMENTARY, true);
      mainActivity.changeFragment(MainActivity.GOODS_LIST_FRAGMENT_ID, args, true);

    } else {
      ToastUtil.toastError(mainActivity, R.string.message_cannot_create_request_reject_right_now);
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
      Timber.e(e);
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
    mainActivity.showTimer();
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
    DialogUtil.dismissProgressDialog();
    if (!errorEvent.getMessage().equals("reject")) {
      if (errorEvent.getStatusCode().equals(StatusCodes.NETWORK_ERROR)) {
        Toast.makeText(mainActivity, "خطا در شبکه", Toast.LENGTH_LONG).show();
      } else {
        ToastUtil.toastError(mainActivity, getString(R.string.error_unknown_system_exception));
      }
    }
  }

  @Subscribe
  public void getMessage(GoodsDtoList goodsDtoList) {
    DialogUtil.dismissProgressDialog();

    List<Goods> goodsList = goodsDtoList.getGoodsDtoList();
    if (goodsList != null) {
      final Bundle args = new Bundle();
      args.putLong(Constants.ORDER_ID, orderDto.getId());
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
    String fileName = UUID.randomUUID().toString();

    fileUri = MediaUtil.getOutputMediaFileUri(mainActivity, MediaUtil.MEDIA_TYPE_IMAGE,
        Constants.CUSTOMER_PICTURE_DIRECTORY_NAME, fileName); // create a file to save the image
    CameraManager.startCameraActivity(mainActivity, fileUri, this);
  }

  @Override
  public void onDestroy() {
    super.onDestroy();
    if (timer != null) {
      timer.cancel();
      timer.purge();
      timer = null;
      minutes = seconds = hours = 0;
      mainActivity.hideTimer();
    }
  }
}
