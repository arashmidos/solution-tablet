package com.parsroyal.solutiontablet.ui.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
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
import android.widget.Toast;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Optional;
import com.crashlytics.android.Crashlytics;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.mikepenz.crossfader.util.UIUtils;
import com.parsroyal.solutiontablet.R;
import com.parsroyal.solutiontablet.biz.impl.RejectedGoodsDataTransferBizImpl;
import com.parsroyal.solutiontablet.constants.Constants;
import com.parsroyal.solutiontablet.constants.SaleOrderStatus;
import com.parsroyal.solutiontablet.constants.StatusCodes;
import com.parsroyal.solutiontablet.data.entity.Customer;
import com.parsroyal.solutiontablet.data.entity.Goods;
import com.parsroyal.solutiontablet.data.event.ActionEvent;
import com.parsroyal.solutiontablet.data.event.ErrorEvent;
import com.parsroyal.solutiontablet.data.event.Event;
import com.parsroyal.solutiontablet.data.model.GoodsDtoList;
import com.parsroyal.solutiontablet.data.model.SaleOrderDto;
import com.parsroyal.solutiontablet.exception.UnknownSystemException;
import com.parsroyal.solutiontablet.service.impl.BaseInfoServiceImpl;
import com.parsroyal.solutiontablet.service.impl.CustomerServiceImpl;
import com.parsroyal.solutiontablet.service.impl.LocationServiceImpl;
import com.parsroyal.solutiontablet.service.impl.SaleOrderServiceImpl;
import com.parsroyal.solutiontablet.service.impl.SettingServiceImpl;
import com.parsroyal.solutiontablet.service.impl.VisitServiceImpl;
import com.parsroyal.solutiontablet.ui.MainActivity;
import com.parsroyal.solutiontablet.util.DialogUtil;
import com.parsroyal.solutiontablet.util.Empty;
import com.parsroyal.solutiontablet.util.MultiScreenUtility;
import com.parsroyal.solutiontablet.util.ToastUtil;
import com.parsroyal.solutiontablet.util.constants.ApplicationKeys;
import java.util.HashSet;
import java.util.List;
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
  private NewVisitDetailFragment parent;
  private boolean expandedMap = false;


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

    customer = customerService.getCustomerById(customerId);

    saleType = settingService.getSettingValue(ApplicationKeys.SETTING_SALE_TYPE);

    setData();

    return view;
  }


  private static void setMapLocation(GoogleMap map, LatLng data) {
    // Add a marker for this item and set the camera
    map.moveCamera(CameraUpdateFactory.newLatLngZoom(data, 15f));
    map.addMarker(new MarkerOptions().position(data));

    // Set the map type back to normal.
    map.setMapType(GoogleMap.MAP_TYPE_NORMAL);
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
  }

  @Optional
  @OnClick({R.id.show_more_tv, R.id.register_order_lay, R.id.register_payment_lay,
      R.id.register_questionnaire_lay, R.id.register_image_lay, R.id.end_and_exit_visit_lay,
      R.id.no_activity_lay, R.id.register_location_btn, R.id.edit_map, R.id.fullscreen_map,
      R.id.register_return_lay})
  public void onClick(View view) {
    switch (view.getId()) {
      case R.id.register_return_lay:
        parent.openOrderDetailFragment(SaleOrderStatus.REJECTED_DRAFT.getId());
        break;
      case R.id.register_order_lay:
        parent.openOrderDetailFragment(SaleOrderStatus.DRAFT.getId());
        break;
      case R.id.register_location_btn:
        mainActivity.changeFragment(MainActivity.SAVE_LOCATION_FRAGMENT_ID, getArguments(), true);
        break;
      case R.id.register_payment_lay:
        goToRegisterPaymentFragment();
        break;
      case R.id.register_questionnaire_lay:
        Toast.makeText(mainActivity, "Questionnaire", Toast.LENGTH_SHORT).show();
        break;
      case R.id.register_image_lay:
        parent.startCameraActivity();
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
        mainActivity.changeFragment(MainActivity.SAVE_LOCATION_FRAGMENT_ID, getArguments(), true);
        break;
      case R.id.fullscreen_map:
        toggleMapFullScreen();
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
      Log.i(TAG, "Fucking error");
      DialogUtil.dismissProgressDialog();
      //TODO Show error message
    }
  }

  @Override
  public int getFragmentId() {
    return MainActivity.CUSTOMER_INFO_FRAGMENT;
  }
}
