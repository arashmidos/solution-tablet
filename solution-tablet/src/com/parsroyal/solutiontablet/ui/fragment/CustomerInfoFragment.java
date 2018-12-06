package com.parsroyal.solutiontablet.ui.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Optional;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.mikepenz.crossfader.util.UIUtils;
import com.parsroyal.solutiontablet.R;
import com.parsroyal.solutiontablet.constants.Constants;
import com.parsroyal.solutiontablet.constants.SaleOrderStatus;
import com.parsroyal.solutiontablet.constants.StatusCodes;
import com.parsroyal.solutiontablet.data.entity.Customer;
import com.parsroyal.solutiontablet.data.event.ActionEvent;
import com.parsroyal.solutiontablet.data.event.ErrorEvent;
import com.parsroyal.solutiontablet.data.event.Event;
import com.parsroyal.solutiontablet.service.impl.CustomerServiceImpl;
import com.parsroyal.solutiontablet.service.impl.QuestionnaireServiceImpl;
import com.parsroyal.solutiontablet.service.impl.SettingServiceImpl;
import com.parsroyal.solutiontablet.ui.activity.MainActivity;
import com.parsroyal.solutiontablet.ui.activity.MobileReportListActivity;
import com.parsroyal.solutiontablet.ui.activity.TabletReportListActivity;
import com.parsroyal.solutiontablet.util.CameraManager;
import com.parsroyal.solutiontablet.util.DialogUtil;
import com.parsroyal.solutiontablet.util.Empty;
import com.parsroyal.solutiontablet.util.MultiScreenUtility;
import com.parsroyal.solutiontablet.util.NumberUtil;
import com.parsroyal.solutiontablet.util.ToastUtil;
import com.parsroyal.solutiontablet.util.constants.ApplicationKeys;
import java.util.HashSet;
import java.util.Locale;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

/**
 * @author Shakib
 */
public class CustomerInfoFragment extends BaseFragment implements OnMapReadyCallback {

  private static final String TAG = CustomerInfoFragment.class.getName();
  private final HashSet<MapView> mMaps = new HashSet<>();
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
  @BindView(R.id.register_location_btn)
  Button registerLocationBtn;
  @BindView(R.id.no_map_layout)
  ViewGroup noMapLayout;
  @BindView(R.id.map_layout)
  RelativeLayout mapLayout;
  @BindView(R.id.map_item)
  MapView mapView;
  GoogleMap map;
  @BindView(R.id.edit_map)
  ImageView editMapButton;
  @BindView(R.id.fullscreen_map)
  ImageView fullscreenMapButton;
  @BindView(R.id.item_bar_lay)
  ScrollView itemBarLayout;
  @Nullable
  @BindView(R.id.customer_menu)
  LinearLayout customerMenu;
  @Nullable
  @BindView(R.id.scroll_container)
  LinearLayout scrollContainer;
  @BindView(R.id.minus_img)
  ImageView minusImg;
  @BindView(R.id.credit_tv)
  TextView creditTv;
  @BindView(R.id.alert_img)
  ImageView alertImg;

  private boolean isShowMore = true;
  private long customerId;
  private long visitId;
  private CustomerServiceImpl customerService;
  private SettingServiceImpl settingService;
  private Customer customer;
  private String saleType;
  private MainActivity mainActivity;
  private VisitDetailFragment parent;
  private boolean expandedMap = false;
  private QuestionnaireServiceImpl questionnaireService;
  private Double creditRemained;
  private boolean checkCreditEnabled;

  public CustomerInfoFragment() {
    // Required empty public constructor
  }

  public static CustomerInfoFragment newInstance(Bundle arguments,
      VisitDetailFragment visitDetailFragment) {
    CustomerInfoFragment fragment = new CustomerInfoFragment();
    fragment.setArguments(arguments);
    fragment.parent = visitDetailFragment;
    return fragment;
  }

  private static void setMapLocation(GoogleMap map, LatLng data) {
    // Add a marker for this item and set the camera
    map.moveCamera(CameraUpdateFactory.newLatLngZoom(data, 15f));
    map.addMarker(new MarkerOptions().position(data));

    // Set the map type back to normal.
    map.setMapType(GoogleMap.MAP_TYPE_NORMAL);
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
    questionnaireService = new QuestionnaireServiceImpl(mainActivity);

    customer = customerService.getCustomerById(customerId);

    if (Empty.isEmpty(customer)) {
      return inflater.inflate(R.layout.empty_view, container, false);
    }

    saleType = settingService.getSettingValue(ApplicationKeys.SETTING_SALE_TYPE);
    String checkCredit = settingService
        .getSettingValue(ApplicationKeys.SETTING_CHECK_CREDIT_ENABLE);
    checkCreditEnabled = Empty.isEmpty(checkCredit) || "null".equals(checkCredit) ? false
        : Boolean.valueOf(checkCredit);

    setData();

    return view;
  }

  @Override
  public void onMapReady(GoogleMap googleMap) {
    MapsInitializer.initialize(mainActivity.getApplicationContext());
    map = googleMap;
    map.getUiSettings().setMapToolbarEnabled(false);
    LatLng data = (LatLng) mapView.getTag();
    if (data != null) {
      setMapLocation(map, data);
    }
  }

  /**
   * Initialises the MapView by calling its lifecycle methods.
   */
  public void initializeMapView() {
    if (mapView != null) {
      // Initialise the MapView
      mapView.onCreate(null);
      // Set the map ready callback to receive the GoogleMap object
      mapView.getMapAsync(this);
    }
  }

  @Override
  public void setUserVisibleHint(boolean isVisibleToUser) {
//    if (isVisibleToUser && getView() == null)
  }

  private void setData() {
    mainActivity.changeTitle(customer.getFullName());

    phoneTv.setText(NumberUtil.digitsToPersian(customer.getPhoneNumber()));
    mobileTv.setText(NumberUtil.digitsToPersian(customer.getCellPhone()));
    locationTv.setText(NumberUtil.digitsToPersian(customer.getAddress()));
    storeTv.setText(NumberUtil.digitsToPersian(customer.getFullName()));

    if (saleType.equals(ApplicationKeys.SALE_HOT)) {
      addOrderTv.setText(String.format("ثبت %s", getString(R.string.title_factor)));
    }

    if (saleType.equals(ApplicationKeys.SALE_DISTRIBUTER)) {
      registerOrderLay.setVisibility(View.GONE);
    }

    if (customer.getxLocation() != null && customer.getxLocation() != 0.0) {
      noMapLayout.setVisibility(View.GONE);
      mapLayout.setVisibility(View.VISIBLE);
      initializeMapView();
      mMaps.add(mapView);

      LatLng loation = new LatLng(customer.getxLocation(), customer.getyLocation());
      mapView.setTag(loation);

      // Ensure the map has been initialised by the on map ready callback in ViewHolder.
      // If it is not ready yet, it will be initialised with the NamedLocation set as its tag
      // when the callback is received.
      if (map != null) {
        // The map is already ready to be used
        setMapLocation(map, loation);
      }
    } else if (MultiScreenUtility.isTablet(mainActivity)) {
      noMapLayout.setVisibility(View.VISIBLE);
      mapLayout.setVisibility(View.GONE);
    }

    creditRemained = customer.getRemainedCredit();
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
        alertImg.setVisibility(View.VISIBLE);
      } else {
        minusImg.setVisibility(View.GONE);
        alertImg.setVisibility(View.GONE);
      }
    } else {
      creditTv.setText(R.string.unknown);
    }
  }

  @Optional
  @OnClick({R.id.show_more_tv, R.id.register_order_lay, R.id.register_payment_lay,
      R.id.register_questionnaire_lay, R.id.register_image_lay, R.id.end_and_exit_visit_lay,
      R.id.no_activity_lay, R.id.register_location_btn, R.id.edit_map, R.id.fullscreen_map,
      R.id.register_return_lay, R.id.edit_map_layout, R.id.fullscreen_map_layout,
      R.id.customer_report_lay})
  public void onClick(View view) {
    switch (view.getId()) {
      case R.id.register_return_lay:
        parent.openOrderDetailFragment(SaleOrderStatus.REJECTED_DRAFT.getId(), false);
        break;
      case R.id.register_order_lay:
        if (checkCreditEnabled && creditRemained != null && creditRemained <= 0) {
          DialogUtil.showConfirmDialog(mainActivity, getString(R.string.warning),
              getString(R.string.message_only_cash_order), getString(R.string.has_order),
              (dialogInterface, i) ->
                  parent.openOrderDetailFragment(SaleOrderStatus.DRAFT.getId(), true),
              getString(R.string.cancel));
        } else {
          parent.openOrderDetailFragment(SaleOrderStatus.DRAFT.getId(), false);
        }
        break;
      case R.id.register_location_btn:
        mainActivity.changeFragment(MainActivity.SAVE_LOCATION_FRAGMENT_ID, getArguments(), true);
        break;
      case R.id.register_payment_lay:
        goToRegisterPaymentFragment();
        break;
      case R.id.register_questionnaire_lay:
        Bundle bundle = getArguments();
        bundle.putInt(Constants.PARENT, MainActivity.CUSTOMER_INFO_FRAGMENT);
        bundle.putLong(Constants.ANSWERS_GROUP_NO, questionnaireService.getNextAnswerGroupNo());
        mainActivity
            .changeFragment(MainActivity.QUESTIONNAIRE_CATEGORY_FRAGMENT_ID, bundle, true);
        break;
      case R.id.register_image_lay:
        if (!CameraManager.checkPermissions(mainActivity)) {
          CameraManager.requestPermissions(mainActivity);
        } else {
          parent.startCameraActivity();
        }
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
      case R.id.edit_map:
      case R.id.edit_map_layout:
        if (Empty.isNotEmpty(customer.getxLocation()) && customer.getxLocation() != 0.0) {
          ToastUtil.toastError(getActivity(), getString(R.string.edit_location_permission_denied));
        } else {
          mainActivity.changeFragment(MainActivity.SAVE_LOCATION_FRAGMENT_ID, getArguments(), true);
        }
        break;
      case R.id.fullscreen_map_layout:
      case R.id.fullscreen_map:
        toggleMapFullScreen();
        break;
      case R.id.customer_report_lay:
        Intent intent = new Intent(mainActivity,
            MultiScreenUtility.isTablet(mainActivity) ? TabletReportListActivity.class
                : MobileReportListActivity.class);
        intent.putExtra(Constants.REPORT_TYPE, Constants.REPORT_CUSTOMER);
        intent.putExtra(Constants.REPORT_CUSTOMER_ID, customer.getBackendId());
        mainActivity.startActivity(intent);
        break;
    }
  }


  private void toggleMapFullScreen() {
    expandedMap = !expandedMap;
    if (MultiScreenUtility.isTablet(mainActivity)) {
      itemBarLayout.setVisibility(expandedMap ? View.GONE : View.VISIBLE);
    } else {
      customerMenu.setVisibility(expandedMap ? View.GONE : View.VISIBLE);
      LayoutParams params = mapLayout.getLayoutParams();
      if (expandedMap) {
        params.height = LinearLayout.LayoutParams.MATCH_PARENT;

      } else {
        params.height = (int) UIUtils.convertDpToPixel(128, mainActivity);
      }
      mapLayout.setLayoutParams(params);
    }

    fullscreenMapButton.setImageResource(
        expandedMap ? R.drawable.ic_zoom_in_24dp : R.drawable.ic_zoom_out_24dp);
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

  private void goToRegisterPaymentFragment() {
    Bundle args = new Bundle();
    args.putLong(Constants.CUSTOMER_BACKEND_ID, customer.getBackendId());
    args.putLong(Constants.VISIT_ID, visitId);
    mainActivity.changeFragment(MainActivity.REGISTER_PAYMENT_FRAGMENT, args, true);
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
  public void getMessage(Event event) {
    if (event instanceof ActionEvent) {
      if (event.getStatusCode() == StatusCodes.ACTION_ADD_PAYMENT) {
        goToRegisterPaymentFragment();
      }
    } else if (event instanceof ErrorEvent) {
      DialogUtil.dismissProgressDialog();
      ToastUtil.toastError(mainActivity, getString(R.string.operation_not_permitted));
    }
  }

  @Override
  public int getFragmentId() {
    return MainActivity.CUSTOMER_INFO_FRAGMENT;
  }

  @Override
  public void onDestroyView() {
    super.onDestroyView();
  }
}
