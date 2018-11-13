package com.parsroyal.solutiontablet.ui.fragment;

import android.content.IntentSender.SendIntentException;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import butterknife.BindInt;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import com.alirezaafkar.sundatepicker.DatePicker;
import com.alirezaafkar.sundatepicker.components.JDF;
import com.alirezaafkar.sundatepicker.interfaces.DateSetListener;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.GoogleApiClient.Builder;
import com.google.android.gms.common.api.GoogleApiClient.ConnectionCallbacks;
import com.google.android.gms.common.api.GoogleApiClient.OnConnectionFailedListener;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.InfoWindowAdapter;
import com.google.android.gms.maps.GoogleMap.OnCameraChangeListener;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.maps.android.clustering.Cluster;
import com.google.maps.android.clustering.ClusterManager;
import com.google.maps.android.clustering.view.DefaultClusterRenderer;
import com.parsroyal.solutiontablet.BuildConfig;
import com.parsroyal.solutiontablet.R;
import com.parsroyal.solutiontablet.biz.KeyValueBiz;
import com.parsroyal.solutiontablet.biz.impl.KeyValueBizImpl;
import com.parsroyal.solutiontablet.constants.Constants;
import com.parsroyal.solutiontablet.constants.SaleOrderStatus;
import com.parsroyal.solutiontablet.data.entity.Customer;
import com.parsroyal.solutiontablet.data.entity.KeyValue;
import com.parsroyal.solutiontablet.data.entity.Position;
import com.parsroyal.solutiontablet.data.event.GPSEvent;
import com.parsroyal.solutiontablet.data.listmodel.CustomerListModel;
import com.parsroyal.solutiontablet.exception.BusinessException;
import com.parsroyal.solutiontablet.exception.UnknownSystemException;
import com.parsroyal.solutiontablet.service.CustomerService;
import com.parsroyal.solutiontablet.service.PositionService;
import com.parsroyal.solutiontablet.service.SettingService;
import com.parsroyal.solutiontablet.service.VisitService;
import com.parsroyal.solutiontablet.service.impl.CustomerServiceImpl;
import com.parsroyal.solutiontablet.service.impl.PositionServiceImpl;
import com.parsroyal.solutiontablet.service.impl.SaleOrderServiceImpl;
import com.parsroyal.solutiontablet.service.impl.SettingServiceImpl;
import com.parsroyal.solutiontablet.service.impl.VisitServiceImpl;
import com.parsroyal.solutiontablet.ui.MainActivity;
import com.parsroyal.solutiontablet.ui.fragment.bottomsheet.MapInfoWindowChooser;
import com.parsroyal.solutiontablet.util.Analytics;
import com.parsroyal.solutiontablet.util.DateUtil;
import com.parsroyal.solutiontablet.util.Empty;
import com.parsroyal.solutiontablet.util.ImageUtil;
import com.parsroyal.solutiontablet.util.LocationUtil;
import com.parsroyal.solutiontablet.util.Logger;
import com.parsroyal.solutiontablet.util.NotificationUtil;
import com.parsroyal.solutiontablet.util.NumberUtil;
import com.parsroyal.solutiontablet.util.SunDate;
import com.parsroyal.solutiontablet.util.ToastUtil;
import com.parsroyal.solutiontablet.util.constants.ApplicationKeys;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

/**
 * Created by Arash on 2016-09-14.
 */
public class UserTrackingFragment extends BaseFragment implements ConnectionCallbacks,
    OnConnectionFailedListener, OnMapReadyCallback, OnCameraChangeListener, DateSetListener {

  public static final String TAG = UserTrackingFragment.class.getSimpleName();

  @BindInt(R.integer.camera_zoom_tracking)
  int cameraZoom;
  @BindView(R.id.error_msg)
  TextView errorMsg;
  @BindView(R.id.layout_container)
  FrameLayout layoutContainer;
  @BindView(R.id.filter)
  ImageView filter;
  @BindView(R.id.toDate)
  EditText toDate;
  @BindView(R.id.fromDate)
  EditText fromDate;
  @BindView(R.id.filter_layout)
  ViewGroup filterLayout;
  @BindView(R.id.show_customers)
  CheckBox showCustomers;
  @BindView(R.id.show_track)
  CheckBox showTrack;
  @BindView(R.id.mainLayout)
  RelativeLayout mainLayout;
  //  @BindView(R.id.show_snapped_track)
//  CheckBox showSnappedTrack;
  @BindView(R.id.show_waypoints)
  CheckBox showWaypoints;

  private GoogleApiClient googleApiClient;
  private Location currentLocation;
  private GoogleMap map;
  private LatLng currentLatlng;
  private PositionService positionService;
  private SettingService settingService;
  private VisitService visitService;
  private SaleOrderServiceImpl orderService;
  private SunDate startDate = new SunDate();
  private SunDate endDate = new SunDate();
  private KeyValueBiz keyValueBiz;

  private ArrayList<Polyline> polylines = new ArrayList<>();
  private int[] colors = new int[]{R.color.green,
      R.color.red,
      R.color.violet,
      R.color.blue,
      R.color.orange};
  private boolean mResolvingError = false;
  private KeyValue salesmanId;
  private CustomerService customerService;
  private ClusterManager<CustomerListModel> clusterManager;
  private Polyline polyline;
  private Polyline snappedPolyline;
  private Cluster<CustomerListModel> clickedCluster;
  private CustomerListModel clickedClusterItem;
  private Marker startMarker;
  private Marker endMarker;
  private List<Marker> waypoints = new ArrayList<>();
  private boolean distanceServiceEnabled;
  private MainActivity context;
  private List<LatLng> lastRoute;
  private float distanceAllowed;

  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container,
      Bundle savedInstanceState) {
    context = (MainActivity) getActivity();
    context.changeTitle(getString(R.string.map));

    initServices();
    salesmanId = keyValueBiz.findByKey(ApplicationKeys.SALESMAN_ID);
    if (Empty.isEmpty(salesmanId)) {
      View view = inflater.inflate((R.layout.view_error_page), null);
      TextView errorView = view.findViewById(R.id.error_msg);
      errorView.setText(String.format("%s\n\nاطلاعات کاربری یافت نشد!", errorView.getText()));
      return view;
    }

    View view = inflater.inflate(R.layout.fragment_user_tracking, null);

    ButterKnife.bind(this, view);

    setListeners();

    distanceServiceEnabled = Boolean.valueOf(settingService
        .getSettingValue(ApplicationKeys.SETTING_CALCULATE_DISTANCE_ENABLE));
    if (BuildConfig.DEBUG) {
      distanceServiceEnabled = false;
    }
    String distance = settingService
        .getSettingValue(ApplicationKeys.SETTING_DISTANCE_CUSTOMER_VALUE);
    try {
      distanceAllowed = Empty.isEmpty(distance) || "null".equals(distance) ? Constants.MAX_DISTANCE
          : Float.valueOf(distance);
    } catch (NumberFormatException ex) {
      ex.printStackTrace();
    }
    loadCalendars();

    googleApiClient = new Builder(context)
        .addConnectionCallbacks(this)
        .addOnConnectionFailedListener(this)
        .addApi(LocationServices.API).build();

    return view;
  }

  private void initServices() {
    keyValueBiz = new KeyValueBizImpl(context);
    positionService = new PositionServiceImpl(context);
    customerService = new CustomerServiceImpl(context);
    settingService = new SettingServiceImpl(context);
    visitService = new VisitServiceImpl(context);
    orderService = new SaleOrderServiceImpl(context);
  }

  private void setListeners() {
    showCustomers.setOnCheckedChangeListener((buttonView, isChecked) ->
    {
      if (isChecked) {
        showCustomers();
      } else {
        if (Empty.isNotEmpty(clusterManager)) {
          clusterManager.clearItems();
          clusterManager.cluster();
        }
      }
    });

    showTrack.setOnCheckedChangeListener((buttonView, isChecked) ->
    {
//      showSnappedTrack.setVisibility(isChecked ? View.VISIBLE : View.INVISIBLE);
      showWaypoints.setVisibility(isChecked ? View.VISIBLE : View.INVISIBLE);
      if (isChecked) {
        doFilter();
      } else {
        clearMapRoute();
      }
    });

//    showSnappedTrack.setOnCheckedChangeListener((buttonView, isChecked) -> {
//      if (isChecked && Empty.isNotEmpty(lastRoute)) {
//        new AsyncRouteLoader().execute(lastRoute);
//      } else {
//        if (Empty.isNotEmpty(snappedPolyline)) {
//          snappedPolyline.remove();
//          polylines.remove(snappedPolyline);
//        }
//      }
//    });

    showWaypoints.setOnCheckedChangeListener((buttonView, isChecked) ->
    {
      if (isChecked) {
        if (Empty.isNotEmpty(lastRoute) && lastRoute.size() > 2) {
          for (int i = 1; i < lastRoute.size() - 1; i++) {
            LatLng waypoint = lastRoute.get(i);

            waypoints.add(map.addMarker(new MarkerOptions()
                .position(waypoint)
                .icon(BitmapDescriptorFactory.fromBitmap(ImageUtil.getBitmapFromVectorDrawable(
                    getActivity(), R.drawable.ic_stay_point_24dp)))));
          }
        }
      } else {
        clearWaypoints();
      }
    });
  }

  private void clearWaypoints() {
    for (int i = 0; i < waypoints.size(); i++) {
      Marker marker = waypoints.get(i);
      marker.remove();
    }
    waypoints.clear();
  }

  private void clearMapRoute() {
    if (Empty.isNotEmpty(polyline)) {
      polyline.remove();
      polylines.remove(polyline);
    }

    if (Empty.isNotEmpty(snappedPolyline)) {
      snappedPolyline.remove();
      polylines.remove(snappedPolyline);
    }

    if (Empty.isNotEmpty(startMarker)) {
      startMarker.remove();
    }

    if (Empty.isNotEmpty(endMarker)) {
      endMarker.remove();
    }

    clearWaypoints();
  }

  private void loadCalendars() {
    toDate.setHint(endDate.getYear() % 100 + "/" + endDate.getMonth() + "/" + endDate.getDay());
    Calendar calendar = endDate.getCalendar();
//    calendar.add(Calendar.DAY_OF_YEAR, -1);
    startDate.setDate(new JDF(calendar));
    fromDate
        .setHint(startDate.getYear() % 100 + "/" + startDate.getMonth() + "/" + startDate.getDay());
  }

  @Override
  public void onStart() {
    super.onStart();
    if (Empty.isNotEmpty(salesmanId)) {
      showProgressDialog(getString(R.string.message_loading_map));
      if (googleApiClient != null) {
        googleApiClient.connect();
      }
    }
    EventBus.getDefault().register(this);
  }

  @Override
  public void onStop() {
    super.onStop();
    if (googleApiClient != null && googleApiClient.isConnected()) {
      googleApiClient.disconnect();
    }
    EventBus.getDefault().unregister(this);
  }

  @Subscribe
  public void getMessage(GPSEvent event) {
    if (showTrack.isChecked()) {
      runOnUiThread(this::doFilter);
    }
  }


  @Override
  public int getFragmentId() {
    return MainActivity.USER_TRACKING_FRAGMENT_ID;
  }

  @Override
  public void onConnected(@Nullable Bundle bundle) {
    if (!isAdded()) {
      return;
    }
    layoutContainer.setVisibility(View.VISIBLE);
    errorMsg.setVisibility(View.GONE);
    FragmentManager fm = getChildFragmentManager();
    Fragment fragment = fm.findFragmentById(R.id.map);
    if (fragment == null) {
      fragment = SupportMapFragment.newInstance();
    }
    ((SupportMapFragment) fragment).getMapAsync(this);
  }

  @Override
  public void onConnectionSuspended(int i) {
    dismissProgressDialog();
    Log.d(TAG, "Connection suspended");
    googleApiClient.connect();
  }

  @Override
  public void onConnectionFailed(@NonNull ConnectionResult result) {
    dismissProgressDialog();
    if (mResolvingError) {
      errorMsg.setText(String.format(Locale.US, getString(R.string.error_google_play_not_available),
          result.getErrorCode()));
      // Already attempting to resolve an error.
    } else if (result.hasResolution()) {
      try {
        mResolvingError = true;
        result.startResolutionForResult(getActivity(), 1001);
      } catch (SendIntentException e) {
        // There was an error with the resolution intent. Try again.
        googleApiClient.connect();
      }
    } else {
      // Show dialog using GoogleApiAvailability.getErrorDialog()
      showErrorDialog(result.getErrorCode());
      mResolvingError = true;
    }
  }

  private void showErrorDialog(int errorCode) {
    ToastUtil.toastError(getActivity(), String
        .format(getString(R.string.error_google_play_not_available), errorCode));
  }

  @Override
  public void onMapReady(GoogleMap googleMap) {
    dismissProgressDialog();

    map = googleMap;

    currentLocation = LocationServices.FusedLocationApi.getLastLocation(googleApiClient);

    if (currentLocation == null) {
      Position position = positionService.getLastPosition();
      if (position != null) {
        map.animateCamera(CameraUpdateFactory.newLatLngZoom(
            new LatLng(position.getLatitude(), position.getLongitude()), cameraZoom), 4000, null);
      } else {
        NotificationUtil.showGPSDisabled(getActivity());
      }
    } else {
      currentLatlng = new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude());
    }

    map.setMyLocationEnabled(true);
    map.getUiSettings().setAllGesturesEnabled(true);
    map.getUiSettings().setZoomControlsEnabled(true);
    if (Empty.isNotEmpty(currentLatlng)) {
      map.animateCamera(CameraUpdateFactory.newLatLngZoom(currentLatlng, cameraZoom), 4000, null);
    }

    map.setOnCameraChangeListener(this);

    doFilter();
    showCustomers();
  }

  private void showCustomers() {
    clusterManager = new ClusterManager<>(getActivity(), map);
    clusterManager.setRenderer(new CustomRenderer());
    map.setOnCameraIdleListener(clusterManager);
    map.setOnMarkerClickListener(clusterManager);
    map.setOnInfoWindowClickListener(clusterManager);
    map.setInfoWindowAdapter(clusterManager.getMarkerManager());
    map.setOnInfoWindowClickListener(marker -> {
      Float distance = clickedClusterItem.getDistance();
      if (distanceServiceEnabled && distance > distanceAllowed) {
        ToastUtil.toastError(getActivity(), R.string.error_distance_too_far_for_action);
        return;
      }
      MapInfoWindowChooser mapInfoWindowChooser = MapInfoWindowChooser.newInstance(this, marker);
      mapInfoWindowChooser.show(getActivity().getSupportFragmentManager(), "detail bottom sheet");

    });
    clusterManager.getMarkerCollection().setOnInfoWindowAdapter(new CustomerMarkerAdapter());
    clusterManager.setOnClusterClickListener(cluster ->
    {
      clickedCluster = cluster; // remember for use later in the Adapter
      return false;
    });
    clusterManager.setOnClusterItemClickListener(item ->
    {
      clickedClusterItem = item;
      return false;
    });

    addItems();
    clusterManager.cluster();
  }

  public void doEnter() {
    try {
      final Long visitInformationId = visitService.startVisiting(clickedClusterItem.getBackendId(),
          getDistance());

      Position position = positionService.getLastPosition();

      if (Empty.isNotEmpty(position)) {
        visitService.updateVisitLocation(visitInformationId, position);
      }

      orderService.deleteForAllCustomerOrdersByStatus(clickedClusterItem.getBackendId(),
          SaleOrderStatus.DRAFT.getId());
      Bundle args = new Bundle();
      args.putLong(Constants.VISIT_ID, visitInformationId);
      args.putLong(Constants.CUSTOMER_ID, clickedClusterItem.getPrimaryKey());
      args.putLong(Constants.ORIGIN_VISIT_ID, visitInformationId);
      Analytics.logContentView("Map Visit");
      context.changeFragment(MainActivity.VISIT_DETAIL_FRAGMENT_ID, args, false);

    } catch (BusinessException e) {
      Log.e(TAG, e.getMessage(), e);
      ToastUtil.toastError(context, e);
    } catch (Exception e) {
      Logger
          .sendError("General Exception", "Error in entering customer from map " + e.getMessage());
      Log.e(TAG, e.getMessage(), e);
      ToastUtil.toastError(context, new UnknownSystemException(e));
    } finally {
      dismissProgressDialog();
    }
  }

  private int getDistance() {

    Customer customer = customerService.getCustomerByBackendId(clickedClusterItem.getBackendId());
    Position position = positionService.getLastPosition();
    Float distance;

    double lat2 = customer.getxLocation();
    double long2 = customer.getyLocation();

    if (lat2 == 0.0 || long2 == 0.0) {
      //Location not set for customer
      return 0;
    }

    if (Empty.isNotEmpty(position)) {
      distance = LocationUtil
          .distanceBetween(position.getLatitude(), position.getLongitude(), lat2, long2);
      return distance.intValue();
    } else {
      return 0;
    }
  }

  private void addItems() {
    List<CustomerListModel> customerPositionList = customerService
        .getFilteredCustomerList(null, null);
    clusterManager.addItems(customerPositionList);
  }

  @Override
  public void onCameraChange(CameraPosition cameraPosition) {
  }

  @OnClick({R.id.cancel_btn, R.id.filter_btn, R.id.toDate, R.id.fromDate, R.id.filter})
  public void onClick(View view) {
    switch (view.getId()) {
      case R.id.filter_btn:
        doFilter();
        filterLayout.setVisibility(View.GONE);
        filter.setVisibility(View.VISIBLE);
        break;
      case R.id.cancel_btn:
        filterLayout.setVisibility(View.GONE);
        filter.setVisibility(View.VISIBLE);
        break;
      case R.id.toDate:
        DatePicker.Builder builder = new DatePicker.Builder().id(2);
        builder.date(endDate.getDay(), endDate.getMonth(), endDate.getYear());
        builder.future(false);
        builder.build(UserTrackingFragment.this).show(getFragmentManager(), "");
        break;
      case R.id.fromDate:
        DatePicker.Builder builder2 = new DatePicker.Builder().id(3);
        builder2.future(false);
        builder2.date(startDate.getDay(), startDate.getMonth(), startDate.getYear());
        builder2.build(UserTrackingFragment.this).show(getFragmentManager(), "");
        break;
      case R.id.filter:
        filterLayout.setVisibility(View.VISIBLE);
        filter.setVisibility(View.GONE);
        break;
    }
  }

  private void doFilter() {
    Calendar c1 = startDate.getCalendar();
    Calendar c2 = endDate.getCalendar();
    if (validate(c1, c2)) {

      Date from = DateUtil.startOfDay(c1);
      Date to = DateUtil.endOfDay(from);

      lastRoute = positionService.getAllPositionLatLngByDate(from, to);
      drawRoute(lastRoute);

//      if (showSnappedTrack.isChecked() && Empty.isNotEmpty(lastRoute)) {
//        new AsyncRouteLoader().execute(lastRoute);
//      }
      Analytics.logContentView("Map Filter");
    }
  }

  private boolean validate(Calendar c1, Calendar c2) {
    if (fromDate.getHint().equals("--")) {
      ToastUtil.toastError(getActivity(), getString(R.string.error_tracking_cal1_empty));
      return false;
    }
    /*if (toDate.getHint().equals("--")) {
      ToastUtil.toastError(getActivity(), getString(R.string.error_tracking_cal2_empty));
      return false;
    }*/
    /*
    long days = DateUtil.compareDatesInDays(c1, c2);

    if (days + 1 > 5) {
      ToastUtil.toastError(getActivity(), getString(R.string.error_report_is_huge));
      return false;
    } else if (days < 0) {
      ToastUtil.toastError(getActivity(), getString(R.string.error_report_date_invalid));
      return false;
    } else {*/
    return true;
  }

  private void drawRoute(List<LatLng> route) {
    if (Empty.isEmpty(map)) {
      return;
    }
    clearMapRoute();
    if (route.size() == 0) {
      return;
    }
    if (route.size() > 0) {
      startMarker = map.addMarker(new MarkerOptions()
          .position(route.get(0))
          .icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_start_marker_36dp)));
    }
    if (route.size() > 1) {
      endMarker = map.addMarker(new MarkerOptions()
          .position(route.get(route.size() - 1))
          .icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_end_marker_36dp)));
    }

    if (route.size() > 2 && showWaypoints.isChecked()) {
      for (int i = 1; i < route.size() - 1; i++) {
        LatLng waypoint = route.get(i);

        waypoints.add(map.addMarker(new MarkerOptions()
            .position(waypoint)
            .icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_stay_point_24dp))));
      }
    }

    PolylineOptions polyOptions = new PolylineOptions();
    polyOptions.color(ContextCompat.getColor(context, colors[3]));
    polyOptions.width(4);
    polyOptions.addAll(route);
    polyline = map.addPolyline(polyOptions);
    polylines.add(polyline);
  }

  @Override
  public void onDateSet(int id, @Nullable Calendar calendar, int day, int month, int year) {
    int tempYear = year % 100;
    if (id == 2) {
      toDate.setHint(tempYear + "/" + month + "/" + day);
      endDate.setDate(day, month, year);
    } else if (id == 3) {
      fromDate.setHint(tempYear + "/" + month + "/" + day);
      startDate.setDate(day, month, year);
    }
  }

  @Override
  public void onDestroyView() {
    super.onDestroyView();
    Fragment f = getChildFragmentManager().findFragmentById(R.id.map);
    if (f != null) {
      getChildFragmentManager().beginTransaction().remove(f).commitAllowingStateLoss();
    }
  }

  class CustomerMarkerAdapter implements InfoWindowAdapter {

    @BindView(R.id.customer_name)
    TextView customerName;
    @BindView(R.id.customer_address)
    TextView customerAddress;
    @BindView(R.id.customer_last_visit)
    TextView customerLastVisit;
    @BindView(R.id.customer_id)
    TextView customerId;

    @Override
    public View getInfoWindow(Marker marker) {
      View v = getActivity().getLayoutInflater().inflate(R.layout.map_popup, null);
      ButterKnife.bind(this, v);

      customerName.setText(clickedClusterItem.getTitle());
      customerAddress.setText(clickedClusterItem.getSnippet());
      if (TextUtils.isEmpty(clickedClusterItem.getLastVisit()) || clickedClusterItem.getLastVisit()
          .equals("null")) {
        customerLastVisit.setText("--");
      } else {
        customerLastVisit.setText(NumberUtil
            .digitsToPersian(
                String.format(Locale.getDefault(), "%s", clickedClusterItem.getLastVisit())));
      }
      customerId.setText(NumberUtil
          .digitsToPersian(
              String.format(Locale.getDefault(), "%s", clickedClusterItem.getCode())));
      return v;

    }

    @Override
    public View getInfoContents(Marker marker) {
      return null;
    }

//    @OnClick({R.id.enter_btn, R.id.navigation_tv})
//    public void onViewClicked(View view) {
//      switch (view.getId()) {
//        case R.id.enter_btn:
//          Float distance = clickedClusterItem.getDistance();
//          if (distanceServiceEnabled && distance > distanceAllowed) {
//            ToastUtil.toastError(getActivity(), R.string.error_distance_too_far_for_action);
//            return;
//          }
//          doEnter();
//          break;
//        case R.id.navigation_tv:
//          Toast.makeText(context, "navigation", Toast.LENGTH_SHORT).show();
//          break;
//      }
//    }
  }

  class CustomRenderer extends DefaultClusterRenderer<CustomerListModel> {

    public CustomRenderer() {
      super(context.getApplicationContext(), map, clusterManager);
    }

    @Override
    protected void onBeforeClusterItemRendered(CustomerListModel item,
        MarkerOptions markerOptions) {
      super.onBeforeClusterItemRendered(item, markerOptions);
      markerOptions.title(item.getTitle());

      BitmapDescriptor icon = BitmapDescriptorFactory.fromResource(R.drawable.ic_marker_red_36dp);

      if (item.hasOrder()) {
        icon = BitmapDescriptorFactory.fromResource(R.drawable.ic_marker_green_36dp);
      } else if (item.hasRejection()) {
        icon = BitmapDescriptorFactory.fromResource(R.drawable.ic_marker_black_36dp);
      } else if (item.isVisited()) {
        icon = BitmapDescriptorFactory.fromResource(R.drawable.ic_marker_blue_36dp);
      }

      markerOptions.icon(icon);
    }
  }
}
