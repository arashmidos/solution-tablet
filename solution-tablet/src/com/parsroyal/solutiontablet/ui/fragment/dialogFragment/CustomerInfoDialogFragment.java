package com.parsroyal.solutiontablet.ui.fragment.dialogFragment;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.fragment.app.DialogFragment;
import androidx.core.content.ContextCompat;
import androidx.core.widget.NestedScrollView;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import com.parsroyal.solutiontablet.R;
import com.parsroyal.solutiontablet.SolutionTabletApplication;
import com.parsroyal.solutiontablet.constants.Authority;
import com.parsroyal.solutiontablet.constants.Constants;
import com.parsroyal.solutiontablet.constants.PageStatus;
import com.parsroyal.solutiontablet.constants.SaleOrderStatus;
import com.parsroyal.solutiontablet.constants.VisitInformationDetailType;
import com.parsroyal.solutiontablet.data.entity.Position;
import com.parsroyal.solutiontablet.data.entity.VisitInformationDetail;
import com.parsroyal.solutiontablet.data.event.GPSEvent;
import com.parsroyal.solutiontablet.data.listmodel.CustomerListModel;
import com.parsroyal.solutiontablet.data.model.CustomerDto;
import com.parsroyal.solutiontablet.exception.BusinessException;
import com.parsroyal.solutiontablet.exception.UnknownSystemException;
import com.parsroyal.solutiontablet.service.PositionService;
import com.parsroyal.solutiontablet.service.SaleOrderService;
import com.parsroyal.solutiontablet.service.VisitService;
import com.parsroyal.solutiontablet.service.impl.CustomerServiceImpl;
import com.parsroyal.solutiontablet.service.impl.PositionServiceImpl;
import com.parsroyal.solutiontablet.service.impl.SaleOrderServiceImpl;
import com.parsroyal.solutiontablet.service.impl.SettingServiceImpl;
import com.parsroyal.solutiontablet.service.impl.VisitServiceImpl;
import com.parsroyal.solutiontablet.ui.activity.MainActivity;
import com.parsroyal.solutiontablet.ui.activity.MobileReportListActivity;
import com.parsroyal.solutiontablet.ui.activity.TabletReportListActivity;
import com.parsroyal.solutiontablet.ui.adapter.PathDetailAdapter;
import com.parsroyal.solutiontablet.ui.adapter.VisitActivityAdapter;
import com.parsroyal.solutiontablet.util.DialogUtil;
import com.parsroyal.solutiontablet.util.Empty;
import com.parsroyal.solutiontablet.util.LocationUtil;
import com.parsroyal.solutiontablet.util.MultiScreenUtility;
import com.parsroyal.solutiontablet.util.NumberUtil;
import com.parsroyal.solutiontablet.util.PreferenceHelper;
import com.parsroyal.solutiontablet.util.ToastUtil;
import com.parsroyal.solutiontablet.util.constants.ApplicationKeys;
import java.util.HashSet;
import java.util.Locale;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

public class CustomerInfoDialogFragment extends DialogFragment {

  private static final String TAG = CustomerInfoDialogFragment.class.getSimpleName();
  protected MainActivity mainActivity;
  protected CustomerListModel model;
  protected CustomerServiceImpl customerService;
  protected SettingServiceImpl settingService;
  protected boolean distanceServiceEnabled;
  protected float distanceAllowed;
  protected VisitService visitService;
  protected PositionService positionService;
  protected SaleOrderService orderService;
  protected PathDetailAdapter adapter;
  protected int position;
  protected VisitActivityAdapter activityAdapter;
  @BindView(R.id.customer_name_tv)
  TextView customerNameTv;
  @BindView(R.id.customer_shop_name_tv)
  TextView customerShopNameTv;
  @BindView(R.id.customer_code_tv)
  TextView customerCodeTv;
  @BindView(R.id.customer_address_tv)
  TextView customerAddressTv;
  @BindView(R.id.customer_mobile_tv)
  TextView customerMobileTv;
  @BindView(R.id.customer_phone_tv)
  TextView customerPhoneTv;
  @BindView(R.id.has_location_tv)
  TextView hasLocationTv;
  @BindView(R.id.customer_visit_tv)
  TextView customerVisitTv;
  @BindView(R.id.customer_activity_tv)
  TextView customerActivityTv;
  @BindView(R.id.list)
  RecyclerView list;
  @BindView(R.id.location_img)
  ImageView locationImg;
  @BindView(R.id.visit_today_img)
  ImageView visitTodayImg;
  @BindView(R.id.root)
  LinearLayout root;
  @BindView(R.id.activity_layout)
  RelativeLayout activityLayout;
  @BindView(R.id.list_layout)
  NestedScrollView listLayout;
  @BindView(R.id.minus_img)
  ImageView minusImg;
  @BindView(R.id.credit_tv)
  TextView creditTv;
  @BindView(R.id.distance_tv)
  TextView distanceTv;
  @BindView(R.id.edit_customer_tv)
  TextView editCustomerTv;
  @BindView(R.id.phone_visit_btn)
  Button phoneVisitButton;

  private CustomerDto customer;

  public CustomerInfoDialogFragment() {
    // Required empty public constructor
  }

  public static CustomerInfoDialogFragment newInstance(PathDetailAdapter adapter,
      CustomerListModel model, int position) {
    CustomerInfoDialogFragment fragment = new CustomerInfoDialogFragment();
    fragment.model = model;
    fragment.adapter = adapter;
    fragment.position = position;
    return fragment;
  }

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    if (!getTAG().contains("Sheet")) {
      setStyle(DialogFragment.STYLE_NORMAL, R.style.myDialog);
    }
    setRetainInstance(true);
  }

  protected String getTAG() {
    return TAG;
  }

  @Override
  public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
      Bundle savedInstanceState) {
    // Inflate the layout for this fragment
    View view = inflater.inflate(getLayout(), container, false);
    ButterKnife.bind(this, view);
    mainActivity = (MainActivity) getActivity();
    initServices();
    if (model == null) {
      return inflater.inflate(R.layout.empty_view, container, false);
    }
    customer = customerService.getCustomerDtoById(model.getPrimaryKey());

    initialize();
    setData();
    setupRecycler();
    setPermissions();
    return view;
  }

  private void setPermissions() {
    if (!SolutionTabletApplication.getInstance().hasAccess(Authority.ADD_PHONE_ORDER)) {
      phoneVisitButton.setEnabled(false);
      phoneVisitButton.setTextColor(ContextCompat.getColor(mainActivity, R.color.gray));
    }

    phoneVisitButton.setVisibility(PreferenceHelper.isDistributor() ? View.GONE : View.VISIBLE);
  }

  private void initServices() {
    customerService = new CustomerServiceImpl(mainActivity);
    settingService = new SettingServiceImpl();
    visitService = new VisitServiceImpl(mainActivity);
    positionService = new PositionServiceImpl(mainActivity);
    orderService = new SaleOrderServiceImpl(mainActivity);
  }

  protected int getLayout() {
    if (!getTAG().contains("Sheet")) {
      setStyle(DialogFragment.STYLE_NORMAL, R.style.myDialog);
      return R.layout.fragment_customer_detail;
    }
    return R.layout.fragment_customer_detail_bottom_sheet;
  }

  protected void setupRecycler() {

    HashSet<VisitInformationDetailType> details = model.getDetails();
    if (details.isEmpty()) {
      activityLayout.setVisibility(View.GONE);
      listLayout.setVisibility(View.GONE);
    } else {
      activityAdapter = new VisitActivityAdapter(getActivity(), details);
      list.setLayoutManager(new LinearLayoutManager(getActivity()));
      list.setAdapter(activityAdapter);
    }
  }

  protected void initialize() {
    distanceServiceEnabled = Boolean.valueOf(settingService
        .getSettingValue(ApplicationKeys.SETTING_CALCULATE_DISTANCE_ENABLE));
    String distance = settingService
        .getSettingValue(ApplicationKeys.SETTING_DISTANCE_CUSTOMER_VALUE);

    if (distanceServiceEnabled) {
      try {
        distanceAllowed =
            Empty.isEmpty(distance) || "null".equals(distance) ? Constants.MAX_DISTANCE
                : Float.valueOf(distance);
      } catch (NumberFormatException ex) {
        ex.printStackTrace();
      }
    }
  }

  protected void setData() {
    if (SolutionTabletApplication.getInstance().hasAccess(Authority.EDIT_CUSTOMER)) {
      editCustomerTv.setVisibility(View.VISIBLE);
    } else {
      editCustomerTv.setVisibility(View.GONE);
    }
    Double creditRemained = customer.getRemainedCredit();
    if (creditRemained != null) {
      String text2;
      try {
        String text = NumberUtil.digitsToPersian(
            String.format(Locale.getDefault(), "%,10.0f", Math.abs(creditRemained / 1000)));
        if (text.contains(".")) {
          text2 = text.substring(0, text.indexOf("."));
        } else {
          text2 = text;
        }
      } catch (Exception ex) {
        text2 = "--";
      }
      creditTv.setText(String.format("%s %s", text2, getString(R.string.common_irr_currency)));
      if (creditRemained < 0) {
        creditTv.setTextColor(getResources().getColor(R.color.remove_red));
        minusImg.setVisibility(View.VISIBLE);
      } else {
        minusImg.setVisibility(View.GONE);
      }
    } else {
      creditTv.setText(R.string.unknown);
    }

    customerShopNameTv.setText(model.getShopName());
    customerNameTv.setText(model.getTitle());
    customerCodeTv.setText(String.format("کد : %s", NumberUtil.digitsToPersian(model.getCode())));
    if (model.hasLocation()) {
      locationImg.setImageResource(R.drawable.ic_gps_24_dp);
      locationImg.setColorFilter(ContextCompat.getColor(mainActivity, R.color.primary));

    } else {
      locationImg.setImageResource(R.drawable.ic_gps_off_black_24_px);
      locationImg.setColorFilter(ContextCompat.getColor(mainActivity, R.color.login_gray));
    }

    //set visit icon
    if (model.isVisited()) {
      if (model.isPhoneVisit()) {
        visitTodayImg.setImageResource(R.drawable.ic_call);
        visitTodayImg
            .setColorFilter(ContextCompat.getColor(mainActivity, R.color.log_in_enter_bg));
      } else {
        visitTodayImg
            .setColorFilter(ContextCompat.getColor(mainActivity, R.color.log_in_enter_bg));
      }
    } else {
      visitTodayImg.setColorFilter(ContextCompat.getColor(mainActivity, R.color.login_gray));
    }
    if (model.hasRejection()) {
      visitTodayImg
          .setColorFilter(ContextCompat.getColor(mainActivity, R.color.badger_background));
    }
    if (model.hasNoOrder()) {
      visitTodayImg
          .setColorFilter(ContextCompat.getColor(mainActivity, R.color.orange));
    }

    customerAddressTv.setText(NumberUtil.digitsToPersian(model.getAddress()));
    customerMobileTv.setText(NumberUtil.digitsToPersian(model.getCellPhone()));
    customerPhoneTv.setText(NumberUtil.digitsToPersian(model.getPhoneNumber()));

    Location position = SolutionTabletApplication.getInstance().getLastKnownLocation();
    float distance;
    if (Empty.isEmpty(position)) {
      distanceTv.setText(getString(R.string.unknown_distance));
    } else {
      distance = LocationUtil.distanceBetween(position.getLatitude(), position.getLongitude(),
          customer.getxLocation(), customer.getyLocation());
      distanceTv.setText(NumberUtil.digitsToPersian(String.format(
          getString(R.string.distance_to_customer), String.valueOf((int) distance))));
    }
  }

  @Override
  public void onDestroyView() {
    //workaround for this issue: https://code.google.com/p/android/issues/detail?id=17423 (unable to retain instance after configuration change)
    if (getDialog() != null && getRetainInstance()) {
      getDialog().setDismissMessage(null);
    }
    super.onDestroyView();
  }

  @OnClick({R.id.close_btn, R.id.customer_report_tv, R.id.customer_report_layout,
      R.id.no_visit_btn,
      R.id.enter_btn, R.id.phone_visit_btn, R.id.call_layout, R.id.phone_layout,
      R.id.location_layout, R.id.edit_customer_tv})
  public void onViewClicked(View view) {
    switch (view.getId()) {
      case R.id.phone_visit_btn:
        doEnter(true);
        dismiss();
        break;
      case R.id.close_btn:
        dismiss();
        break;
      case R.id.edit_customer_tv:
        dismiss();
        Bundle bundle = new Bundle();
        bundle.putSerializable(Constants.PAGE_STATUS, PageStatus.EDIT);
        bundle.putSerializable(Constants.CUSTOMER_ID, model.getPrimaryKey());
        mainActivity.changeFragment(MainActivity.NEW_CUSTOMER_DETAIL_FRAGMENT_ID, bundle, true);
        break;
      case R.id.customer_report_tv:
      case R.id.customer_report_layout:
        Intent intent = new Intent(mainActivity,
            MultiScreenUtility.isTablet(mainActivity) ? TabletReportListActivity.class
                : MobileReportListActivity.class);
        intent.putExtra(Constants.REPORT_TYPE, Constants.REPORT_CUSTOMER);
        intent.putExtra(Constants.REPORT_CUSTOMER_ID, model.getBackendId());
        mainActivity.startActivity(intent);
        dismiss();
        break;
      case R.id.enter_btn:
        checkEnter();
        break;
      case R.id.no_visit_btn:
        showWantsDialog();
        break;
      case R.id.call_layout:
        showDialog(customer.getPhoneNumber());
        break;
      case R.id.phone_layout:
        showDialog(customer.getCellPhone());
        break;
      case R.id.location_layout:
        if (Empty.isNotEmpty(customer.getxLocation()) && Empty.isNotEmpty(customer.getyLocation())
            && customer.getxLocation() != 0.0) {
          if ("google".equals(PreferenceHelper.getDefaultNavigator())) {
            Intent i = new Intent(Intent.ACTION_VIEW, Uri.parse(
                "google.navigation:q=" + customer.getxLocation() + "," + customer
                    .getyLocation()));
            i.setPackage("com.google.android.apps.maps");
            if (i.resolveActivity(getActivity().getPackageManager()) != null) {
              startActivity(i);
            } else {
              ToastUtil.toastError(root, getString(R.string.error_google_not_installed));
            }
          } else {
            try {
              Intent i = new Intent(Intent.ACTION_VIEW,
                  Uri.parse(String.format(Locale.UK, "waze://?ll=%s,%s&navigate=yes",
                      customer.getxLocation(), customer.getyLocation())));
              startActivity(i);
            } catch (ActivityNotFoundException ex) {
              // If Waze is not installed, open it in Google Play:
              Intent intent2 = new Intent(Intent.ACTION_VIEW,
                  Uri.parse("market://details?id=com.waze"));
              startActivity(intent2);
            }
          }
        }
    }
  }

  private void showDialog(String phoneNumber) {
    AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(mainActivity);
    LayoutInflater inflater1 = mainActivity.getLayoutInflater();
    View dialogView = inflater1.inflate(R.layout.bottom_sheet_customer_contact, null);
    LinearLayout callLay = dialogView.findViewById(R.id.call_layout);
    LinearLayout smsLay = dialogView.findViewById(R.id.sms_layout);
    dialogBuilder.setView(dialogView);
    AlertDialog alertDialog = dialogBuilder.create();
    alertDialog.show();
    callLay.setOnClickListener(v -> {
      Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("tel:" + phoneNumber));
      startActivity(intent);
      alertDialog.dismiss();
    });
    smsLay.setOnClickListener(v -> {
      Intent intent2 = new Intent(Intent.ACTION_VIEW,
          Uri.parse("sms:" + phoneNumber));
      startActivity(intent2);
      alertDialog.dismiss();
    });

  }

  protected void checkEnter() {

    if (distanceServiceEnabled) {
      if (hasAcceptableDistance()) {
        doEnter(false);
        dismiss();
      } else {
        ToastUtil.toastError(root, R.string.error_distance_too_far_for_action);
      }
    } else {
      if (SolutionTabletApplication.getInstance().getLastSavedPosition() == null) {
        DialogUtil.showCustomDialog(mainActivity, getString(R.string.warning),
            getString(R.string.visit_empty_location_message),
            getString(R.string.enter), (dialog, which) -> {
              doEnter(false);
              dialog.dismiss();
              dismiss();
            }, getString(R.string.retry), (dialog, which) -> {

              //TODOO:
              dialog.dismiss();
            }, Constants.ICON_WARNING);
      } else {
        doEnter(false);
        dismiss();
      }
    }
  }

  protected void showWantsDialog() {
    DialogUtil.showConfirmDialog(mainActivity, "", "آیا از ثبت عدم ویزیت اطمینان دارید؟",
        (dialog, which) -> {
          addNotVisited();
          dialog.dismiss();
        });
  }

  protected void addNotVisited() {
    Long visitId = visitService.startVisiting(model.getBackendId(), getDistance());
    VisitInformationDetail visitInformationDetail = new VisitInformationDetail(
        visitId, VisitInformationDetailType.NONE, 0);
    visitService.saveVisitDetail(visitInformationDetail);
    visitService.finishVisiting(visitId,
        LocationUtil.distanceToCustomer(model.getXlocation(), model.getYlocation()));
    ToastUtil.toastMessage(mainActivity, R.string.none_added_successfully);
    adapter.notifyItemHasRejection(position);
    dismiss();
  }

  protected boolean hasAcceptableDistance() {

    Position position = SolutionTabletApplication.getInstance().getLastSavedPosition();
    Float distance;

    double lat2 = customer.getxLocation();
    double long2 = customer.getyLocation();

    if (lat2 == 0.0 || long2 == 0.0) {
      //Location not set for customer
      return true;
    }

    if (Empty.isNotEmpty(position)) {
      distance = LocationUtil
          .distanceBetween(position.getLatitude(), position.getLongitude(), lat2, long2);
    } else {
      ToastUtil.toastError(root, R.string.error_salesman_location_not_found);
      return false;
    }

    return distance <= distanceAllowed;
  }

  private int getDistance() {

    return LocationUtil.distanceToCustomer(customer.getxLocation(), customer.getyLocation());
  }

  protected void doEnter(boolean isPhoneVisit) {
    try {
      final Long visitInformationId;
      if (isPhoneVisit) {
        visitInformationId = visitService.startPhoneVisiting(customer.getBackendId());
      } else {
        visitInformationId = visitService.startVisiting(customer.getBackendId(), getDistance());
      }

      Position position = SolutionTabletApplication.getInstance().getLastSavedPosition();
      if (Empty.isNotEmpty(position)) {
        visitService.updateVisitLocation(visitInformationId, position);
      }

      orderService.deleteForAllCustomerOrdersByStatus(customer.getBackendId(),
          SaleOrderStatus.DRAFT.getId());//TODO DO NOT DELETE DRAFTS
      Bundle args = new Bundle();
      args.putLong(Constants.VISIT_ID, visitInformationId);
      args.putLong(Constants.ORIGIN_VISIT_ID, visitInformationId);
      args.putLong(Constants.CUSTOMER_ID, customer.getId());
      args.putLong(Constants.VISITLINE_BACKEND_ID, model.getVisitlineBackendId());
//      args.putBoolean(Constants.PHONE_VISIT, isPhoneVisit);
      if (isPhoneVisit) {
        mainActivity.setPhoneVisit(true);
        mainActivity.changeFragment(MainActivity.PHONE_VISIT_DETAIL_FRAGMENT_ID, args, true);
      } else {
        mainActivity.changeFragment(MainActivity.VISIT_DETAIL_FRAGMENT_ID, args, true);
      }

    } catch (BusinessException e) {
      Log.e(TAG, e.getMessage(), e);
      ToastUtil.toastError(root, e);
    } catch (Exception e) {
      Log.e(TAG, e.getMessage(), e);
      ToastUtil.toastError(root, new UnknownSystemException(e));
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
  public void getMessage(GPSEvent event) {
    float distance = LocationUtil
        .distanceBetween(event.getLocation().getLatitude(), event.getLocation().getLongitude(),
            customer.getxLocation(), customer.getyLocation());

    mainActivity.runOnUiThread(() -> distanceTv.setText(NumberUtil.digitsToPersian(String.format(
        getString(R.string.distance_to_customer), String.valueOf((int) distance)))));
  }
}
