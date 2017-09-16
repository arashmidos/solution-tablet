package com.parsroyal.solutiontablet.ui.fragment;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import com.crashlytics.android.Crashlytics;
import com.parsroyal.solutiontablet.R;
import com.parsroyal.solutiontablet.constants.Constants;
import com.parsroyal.solutiontablet.constants.SaleOrderStatus;
import com.parsroyal.solutiontablet.constants.StatusCodes;
import com.parsroyal.solutiontablet.constants.VisitInformationDetailType;
import com.parsroyal.solutiontablet.data.entity.Customer;
import com.parsroyal.solutiontablet.data.entity.CustomerPic;
import com.parsroyal.solutiontablet.data.entity.VisitInformationDetail;
import com.parsroyal.solutiontablet.data.event.ActionEvent;
import com.parsroyal.solutiontablet.data.model.SaleOrderDto;
import com.parsroyal.solutiontablet.exception.UnknownSystemException;
import com.parsroyal.solutiontablet.service.impl.BaseInfoServiceImpl;
import com.parsroyal.solutiontablet.service.impl.CustomerServiceImpl;
import com.parsroyal.solutiontablet.service.impl.LocationServiceImpl;
import com.parsroyal.solutiontablet.service.impl.SaleOrderServiceImpl;
import com.parsroyal.solutiontablet.service.impl.SettingServiceImpl;
import com.parsroyal.solutiontablet.service.impl.VisitServiceImpl;
import com.parsroyal.solutiontablet.ui.MainActivity;
import com.parsroyal.solutiontablet.util.Empty;
import com.parsroyal.solutiontablet.util.ImageUtil;
import com.parsroyal.solutiontablet.util.MediaUtil;
import com.parsroyal.solutiontablet.util.ToastUtil;
import com.parsroyal.solutiontablet.util.constants.ApplicationKeys;
import java.util.Date;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

/**
 * @author Shakib
 */
public class CustomerInfoFragment extends BaseFragment {

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
  private NewVisitDetailFragment parent;
  private SaleOrderDto orderDto;


  public CustomerInfoFragment() {
    // Required empty public constructor
  }

  public static CustomerInfoFragment newInstance(Bundle arguments,
      NewVisitDetailFragment newVisitDetailFragment) {
    CustomerInfoFragment fragment = new CustomerInfoFragment();
    fragment.setArguments(arguments);
    fragment.parent = newVisitDetailFragment;
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
        parent.finishVisiting();
        break;
      case R.id.no_activity_lay:
        parent.showNoDialog();
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
   * ReturnedOrder
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

  private void goToRegisterPaymentFragment() {
    Bundle args = new Bundle();
    args.putLong(Constants.CUSTOMER_BACKEND_ID, customer.getBackendId());
    args.putLong(Constants.VISIT_ID, visitId);
    mainActivity.changeFragment(MainActivity.REGISTER_PAYMENT_FRAGMENT, args, true);
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
        cPic.setCustomerBackendId(customer.getBackendId());

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


  @Override
  public void onResume() {
    super.onResume();
    EventBus.getDefault().register(this);
  }

  @Override
  public void onPause() {
    super.onPause();
    EventBus.getDefault().unregister(this);
  }

  @Subscribe
  public void getMessage(ActionEvent event) {
    if (event.getStatusCode() == StatusCodes.ACTION_ADD_ORDER) {
      openOrderDetailFragment(SaleOrderStatus.DRAFT.getId());
    } else if (event.getStatusCode() == StatusCodes.ACTION_ADD_PAYMENT) {
      goToRegisterPaymentFragment();
    }
  }

  @Override
  public int getFragmentId() {
    return MainActivity.CUSTOMER_INFO_FRAGMENT;
  }
}
