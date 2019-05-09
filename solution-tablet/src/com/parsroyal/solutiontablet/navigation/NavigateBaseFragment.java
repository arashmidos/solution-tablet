package com.parsroyal.solutiontablet.navigation;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.os.Build.VERSION;
import android.os.Build.VERSION_CODES;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.ViewTreeObserver.OnGlobalLayoutListener;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import com.parsroyal.solutiontablet.BuildConfig;
import com.parsroyal.solutiontablet.R;
import com.parsroyal.solutiontablet.SolutionTabletApplication;
import com.parsroyal.solutiontablet.biz.impl.RestServiceImpl;
import com.parsroyal.solutiontablet.constants.Constants;
import com.parsroyal.solutiontablet.constants.SaleOrderStatus;
import com.parsroyal.solutiontablet.data.entity.Position;
import com.parsroyal.solutiontablet.data.event.NavigateErrorEvent;
import com.parsroyal.solutiontablet.data.listmodel.CustomerListModel;
import com.parsroyal.solutiontablet.data.model.CustomerLocationDto;
import com.parsroyal.solutiontablet.exception.BusinessException;
import com.parsroyal.solutiontablet.exception.UnknownSystemException;
import com.parsroyal.solutiontablet.navigation.PrsMarkerInfoWindow.OnInfoWindowClickListener;
import com.parsroyal.solutiontablet.service.CustomerService;
import com.parsroyal.solutiontablet.service.SaleOrderService;
import com.parsroyal.solutiontablet.service.VisitService;
import com.parsroyal.solutiontablet.service.impl.CustomerServiceImpl;
import com.parsroyal.solutiontablet.service.impl.SaleOrderServiceImpl;
import com.parsroyal.solutiontablet.service.impl.VisitServiceImpl;
import com.parsroyal.solutiontablet.ui.activity.MainActivity;
import com.parsroyal.solutiontablet.ui.adapter.CustomerDetailViewPagerAdapter;
import com.parsroyal.solutiontablet.ui.adapter.PathDetailAdapter;
import com.parsroyal.solutiontablet.ui.fragment.BaseFragment;
import com.parsroyal.solutiontablet.util.Analytics;
import com.parsroyal.solutiontablet.util.AndroidUtil;
import com.parsroyal.solutiontablet.util.DialogUtil;
import com.parsroyal.solutiontablet.util.Empty;
import com.parsroyal.solutiontablet.util.ImageUtil;
import com.parsroyal.solutiontablet.util.LocationUtil;
import com.parsroyal.solutiontablet.util.MultiScreenUtility;
import com.parsroyal.solutiontablet.util.PreferenceHelper;
import com.parsroyal.solutiontablet.util.RtlGridLayoutManager;
import com.parsroyal.solutiontablet.util.ToastUtil;
import com.parsroyal.solutiontablet.vrp.model.Leg;
import com.parsroyal.solutiontablet.vrp.model.LocationResponse;
import com.parsroyal.solutiontablet.vrp.model.OptimizedRouteResponse;
import com.parsroyal.solutiontablet.vrp.model.TripSummaryResponse;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.osmdroid.api.IMapController;
import org.osmdroid.bonuspack.utils.PolylineEncoder;
import org.osmdroid.config.Configuration;
import org.osmdroid.events.MapEventsReceiver;
import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.util.BoundingBox;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.FolderOverlay;
import org.osmdroid.views.overlay.MapEventsOverlay;
import org.osmdroid.views.overlay.Marker;
import org.osmdroid.views.overlay.Overlay;
import org.osmdroid.views.overlay.Polyline;
import org.osmdroid.views.overlay.infowindow.InfoWindow;
import org.osmdroid.views.overlay.mylocation.GpsMyLocationProvider;
import org.osmdroid.views.overlay.mylocation.MyLocationNewOverlay;
import timber.log.Timber;

public class NavigateBaseFragment extends BaseFragment implements MapEventsReceiver,
    OnInfoWindowClickListener {

  @BindView(R.id.mapContainer)
  LinearLayout mapContainer;
  @BindView(R.id.listContainer)
  LinearLayout listContainer;
  @BindView(R.id.listButton)
  TextView listButton;
  @BindView(R.id.mapButton)
  TextView mapButton;
  @BindView(R.id.recycler_view)
  RecyclerView list;
  @BindView(R.id.map)
  MapView map;
  @BindView(R.id.list_bottom_line)
  View listBottomLine;
  @BindView(R.id.list_lay)
  RelativeLayout listLay;
  @BindView(R.id.map_bottom_line)
  View mapBottomLine;
  @BindView(R.id.map_lay)
  RelativeLayout mapLay;
  //
  private PathDetailAdapter adapter;
  private MyLocationNewOverlay mLocationOverlay;
  private OptimizedRouteResponse response;
  private int[] colors = new int[]{R.color.green_return,
      R.color.red,
      R.color.violet,
      R.color.path,
      R.color.orange};

  private CustomerDetailViewPagerAdapter viewPagerAdapter;
  private CustomerService customerService;
  private long visitLineBackendId;
  private List<CustomerLocationDto> customersLocation;
  private List<CustomerListModel> customerList;

  private String visitLineName;
  private MainActivity mainActivity;
  private Unbinder unbinder;
  private Marker lastSelectedMarker;
  private int lastSelectedMarkerIndex = -1;

  public NavigateBaseFragment() {
    // Required empty public constructor
  }

  public static NavigateBaseFragment newInstance() {
    return new NavigateBaseFragment();
  }

  public long getVisitLineBackendId() {
    return visitLineBackendId;
  }

  @Override
  public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
      Bundle savedInstanceState) {
    View view = inflater.inflate(R.layout.activity_navigate, container, false);
    mainActivity = (MainActivity) getActivity();

    Context ctx = mainActivity.getApplicationContext();
    Configuration.getInstance().load(ctx, PreferenceManager.getDefaultSharedPreferences(ctx));
    unbinder = ButterKnife.bind(this, view);

    customerService = new CustomerServiceImpl(mainActivity);
    try {
      setData();
    } catch (IllegalArgumentException ex) {
      ex.printStackTrace();
      return inflater.inflate(R.layout.view_error_page, container, false);
    }
    if (BuildConfig.DEBUG) {
//      PreferenceHelper.clearRoutes();
    }
    initMap();
    setUpRecyclerView();

    selectMapButton();
    return view;
  }

  private void drawMapRoute() {
    draw();
  }

  @Override
  public void onPause() {
    super.onPause();
    map.onPause();
    EventBus.getDefault().unregister(this);
    DialogUtil.dismissProgressDialog();
  }

  @Override
  public void onResume() {
    super.onResume();
    map.onResume();
    EventBus.getDefault().register(this);
    requestData();
  }

  @Subscribe
  public void getMessage(OptimizedRouteResponse response) {
    DialogUtil.dismissProgressDialog();
    this.response = response;
    sortCustomerList();
    drawMapRoute();
    showList();
  }

  @Subscribe
  public void getMessage(NavigateErrorEvent response) {
    DialogUtil.dismissProgressDialog();
    ToastUtil.toastError(mainActivity, "خطا در دریافت مسیر");
  }

  private void setData() {

    Bundle intent = getArguments();
    if (Empty.isEmpty(intent) || intent.getLong(Constants.VISITLINE_BACKEND_ID, -1L) == -1) {
      throw new IllegalArgumentException("VisitLine Id not found");
    }
    visitLineBackendId = intent.getLong(Constants.VISITLINE_BACKEND_ID);
    visitLineName = intent.getString(Constants.VISITLINE_NAME);
    customersLocation = customerService.getAllCustomersLocation(visitLineBackendId);
    customerList = customerService.getFilteredCustomerList(visitLineBackendId, "", true);

    mainActivity.changeTitle(visitLineName);
  }

  public void requestData() {
    OptimizedRouteResponse orr = PreferenceHelper.getOptimizedRoute(visitLineBackendId);
    if (Empty.isNotEmpty(orr)) {
      this.response = orr;
      sortCustomerList();
      showList();
      drawMapRoute();
    } else {
      Location locations = SolutionTabletApplication.getInstance().getLastKnownLocation();
      if (Empty.isEmpty(locations)) {
        ToastUtil.toastError(mainActivity,"موقعیت شما مشخص نیست!");
        return;
      }
      customersLocation
          .add(0, new CustomerLocationDto(0L, locations.getLatitude(), locations.getLongitude()));
      new RestServiceImpl().valOptimizedRoute(mainActivity, visitLineBackendId, customersLocation);
      DialogUtil.showProgressDialog(mainActivity, R.string.message_calculating_route);
    }
  }

  private void sortCustomerList() {
    List<LocationResponse> orderedList = response.getTrip().getLocations();
    List<CustomerListModel> orderListModel = new ArrayList<>(orderedList.size());
    for (int i = 0; i < orderedList.size(); i++) {
      orderListModel.add(new CustomerListModel().withBackendId(
          Long.valueOf(orderedList.get(i).getName())));
    }
    Collections.sort(customerList, (left, right) ->
        Integer.compare(orderListModel.indexOf(left), orderListModel.indexOf(right)));
  }

  public List<CustomerListModel> getCustomerList() {
    return customerList;
  }

  @Override
  public int getFragmentId() {
    return MainActivity.NAVIGATE_BASE_FRAGMENT;
  }

  @Override
  public void onDestroyView() {
    super.onDestroyView();
    unbinder.unbind();
  }

  @OnClick({R.id.list_lay, R.id.map_lay})
  public void onViewClicked(View view) {
    switch (view.getId()) {
      case R.id.list_lay:
        showListContainer();
        break;
      case R.id.map_lay:
        showMapContainer();
        break;
    }
  }

  private void showMapContainer() {
    listContainer.setVisibility(View.GONE);
    mapContainer.setVisibility(View.VISIBLE);
    selectMapButton();
  }

  private void selectMapButton() {
    mapLay.setBackgroundResource(R.drawable.role_selected);
    mapButton.setTextColor(ContextCompat.getColor(mainActivity, R.color.primary));
    mapBottomLine.setVisibility(View.VISIBLE);
    //
    listLay.setBackgroundResource(R.drawable.role_default);
    listButton.setTextColor(ContextCompat.getColor(mainActivity, R.color.login_gray));
    listBottomLine.setVisibility(View.GONE);
  }

  private void showListContainer() {
    listContainer.setVisibility(View.VISIBLE);
    mapContainer.setVisibility(View.GONE);
    selectListButton();
  }

  private void selectListButton() {
    listLay.setBackgroundResource(R.drawable.role_selected);
    listButton.setTextColor(ContextCompat.getColor(mainActivity, R.color.primary));
    listBottomLine.setVisibility(View.VISIBLE);
    //
    mapLay.setBackgroundResource(R.drawable.role_default);
    mapButton.setTextColor(ContextCompat.getColor(mainActivity, R.color.login_gray));
    mapBottomLine.setVisibility(View.GONE);
  }

  //-----------------------------------------Map--------------------------------------------------

  private void initMap() {
    map.setTileSource(TileSourceFactory.MAPNIK);
//    map.getZoomController().setVisibility(Visibility.ALWAYS);

   /* map.setTileSource(new OnlineTileSourceBase("USGS Topo", 0, 18, 256, "",
        new String[] { "http://basemap.nationalmap.gov/ArcGIS/rest/services/USGSTopo/MapServer/tile/" }) {
      @Override
      public String getTileURLString(long pMapTileIndex) {
        return getBaseUrl()
            + MapTileIndex.getZoom(pMapTileIndex)
            + "/" + MapTileIndex.getY(pMapTileIndex)
            + "/" + MapTileIndex.getX(pMapTileIndex)
            + mImageFilenameEnding;
      }
    });*/
    map.setMultiTouchControls(true);

    IMapController mapController = map.getController();
    mapController.setZoom(9.5);
//    GeoPoint startPoint = new GeoPoint(35.6892, 51.3890);
//    mapController.setCenter(startPoint);

    MapEventsOverlay mapEventsOverlay = new MapEventsOverlay(this);
    map.getOverlays().add(0, mapEventsOverlay);

    this.mLocationOverlay = new MyLocationNewOverlay(new GpsMyLocationProvider(mainActivity), map);
    this.mLocationOverlay.enableMyLocation();
//    mLocationOverlay.enableFollowLocation();
    Drawable currentDraw = ResourcesCompat
        .getDrawable(getResources(), R.drawable.ic_lens_blue, null);
    Bitmap currentIcon = null;
    if (currentDraw != null) {
      currentIcon = ((BitmapDrawable) currentDraw).getBitmap();
    }
    mLocationOverlay.setDirectionArrow(currentIcon, currentIcon);
    mLocationOverlay.setDrawAccuracyEnabled(true);
    map.getOverlays().add(this.mLocationOverlay);

  }

  private void draw() {
    List<Leg> legs = response.getTrip().getLegs();
    FolderOverlay legsFolder = new FolderOverlay();
    legsFolder.setName("legs");
    for (int i = 0; i < legs.size(); i++) {

      ArrayList<GeoPoint> legsList = PolylineEncoder
          .decode(legs.get(i).getShape(), 1, false);
      Polyline polyline = new Polyline();
      polyline.setPoints(legsList);
      polyline.setColor(ContextCompat.getColor(mainActivity, colors[3]));
      polyline.setWidth(8);
      legsFolder.add(polyline);
//      map.getOverlayManager().add(polyline);
    }
    map.getOverlayManager().add(legsFolder);

    List<LocationResponse> geoPoints = response.getTrip().getLocations();

    FolderOverlay locationsFolder = new FolderOverlay();
    locationsFolder.setName("locations");
//    PrsMarkerInfoWindow infoWindow =

    for (int i = 0; i < geoPoints.size(); i++) {
      LocationResponse l = geoPoints.get(i);
      PrsMarker startMarker = new PrsMarker(map);
      startMarker.setPosition(new GeoPoint(l.getLat(), l.getLon()));
      startMarker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM);
//      startMarker.setSubDescription("Hello " + l.getName() + " index:" + l.getOriginal_index());
      startMarker.setLocation(l);
      startMarker.setIndex(i);
      if (i < legs.size()) {
        startMarker.setShape(PolylineEncoder.decode(legs.get(i).getShape(), 1, false));
      }
      if (i > 0) {
        CustomerListModel model = customerList.get(i - 1);
        startMarker
            .setInfoWindow(new PrsMarkerInfoWindow(R.layout.osm_info_window, map, model, this));
        startMarker.setTitle(model.getTitle());
        startMarker.setSnippet(model.getSnippet());
        startMarker.setSubDescription(model.getCode());
      }
      startMarker.setOnMarkerClickListener((marker, mapView) -> {
        changeLineColors(marker, mapView);
        if (lastSelectedMarker != null) {
//          lastSelectedMarker.closeInfoWindow();
        }
        map.getController().animateTo(marker.getPosition());
        InfoWindow.closeAllInfoWindowsOn(map);
        marker.showInfoWindow();
        return true;
      });

      if (i == 0) {
        startMarker
            .setIcon(mainActivity.getResources().getDrawable(R.drawable.ic_start_marker_36dp));
      } else {
        startMarker.setIcon(new BitmapDrawable(getResources(),
            ImageUtil.setMarkerDrawable(mainActivity, i, customerList.get(i - 1).isVisited())));
      }

      locationsFolder.add(startMarker);
    }
    map.getOverlayManager().add(locationsFolder);

    TripSummaryResponse summary = response.getTrip().getSummary();
    BoundingBox boundingBox = new BoundingBox(summary.getMax_lat(), summary.getMax_lon(),
        summary.getMin_lat(), summary.getMin_lon());
    zoomToBounds(boundingBox);
  }

  private void changeLineColors(Marker marker, MapView mapView) {
    try {
      lastSelectedMarker = marker;

      FolderOverlay legsOverlay = (FolderOverlay) mapView.getOverlayManager().get(2);
      List<Overlay> items = legsOverlay.getItems();

      if (lastSelectedMarkerIndex != -1 && lastSelectedMarkerIndex < items.size()) {
        ((Polyline) items.get(lastSelectedMarkerIndex))
            .setColor(ContextCompat.getColor(mainActivity, colors[3]));
      }
      PrsMarker prsMarker = (PrsMarker) marker;
      lastSelectedMarkerIndex = prsMarker.getIndex();
      if (lastSelectedMarkerIndex < items.size()) {
        Polyline polyline = (Polyline) items.get(prsMarker.getIndex());
        polyline.setColor(ContextCompat.getColor(mainActivity, colors[0]));
      }
    } catch (Exception ex) {
      ex.printStackTrace();
    }
  }

  public void zoomToBounds(final BoundingBox box) {
    if (map.getHeight() > 0) {
      map.zoomToBoundingBox(box, true);

    } else {
      ViewTreeObserver vto = map.getViewTreeObserver();
      vto.addOnGlobalLayoutListener(new OnGlobalLayoutListener() {

        @Override
        public void onGlobalLayout() {
          map.zoomToBoundingBox(box, true);
          ViewTreeObserver vto2 = map.getViewTreeObserver();
          if (VERSION.SDK_INT < VERSION_CODES.JELLY_BEAN) {
            vto2.removeGlobalOnLayoutListener(this);
          } else {
            vto2.removeOnGlobalLayoutListener(this);
          }
        }
      });
    }
  }
  //-----------------------------------------------LIST--------------------------------------------

  //set up recycler view
  private void setUpRecyclerView() {
    list.setHasFixedSize(true);
    if (MultiScreenUtility.isTablet(mainActivity)) {
      RtlGridLayoutManager gridLayoutManager = new RtlGridLayoutManager(mainActivity, 2);
      list.setLayoutManager(gridLayoutManager);

    } else {
      LinearLayoutManager linearLayoutManager = new LinearLayoutManager(mainActivity);
      list.setLayoutManager(linearLayoutManager);
    }
//    list.setAdapter(adapter);
  }

  public void showList() {
    adapter = new PathDetailAdapter(mainActivity, getCustomerList(), visitLineBackendId, true);
    list.setAdapter(adapter);
  }

  @Override
  public boolean singleTapConfirmedHelper(GeoPoint p) {

    InfoWindow.closeAllInfoWindowsOn(map);
    return true;
  }

  @Override
  public boolean longPressHelper(GeoPoint p) {
    return false;
  }

  @Override
  public void onClick(CustomerListModel model) {

    int distance = LocationUtil.distanceToCustomer(model.getXlocation(), model.getYlocation());

    if (PreferenceHelper.distanceServiceEnabled() && distance > PreferenceHelper
        .getAllowedDistance()) {
      ToastUtil.toastError(mainActivity, R.string.error_distance_too_far_for_action);
      return;
    }

    DialogUtil.showOpenCustomerDialog(mainActivity, distance,
        (dialog, which) -> doEnter(model, distance),
        (dialog, which) -> AndroidUtil
            .navigate(mainActivity, model.getXlocation(), model.getYlocation()));
  }

  public void doEnter(CustomerListModel model, int distance) {
    VisitService visitService = new VisitServiceImpl(mainActivity);
    SaleOrderService orderService = new SaleOrderServiceImpl(mainActivity);
    try {
      final Long visitInformationId = visitService.startVisiting(model.getBackendId(),
          distance);

      Position position = SolutionTabletApplication.getInstance().getLastSavedPosition();

      if (Empty.isNotEmpty(position)) {
        visitService.updateVisitLocation(visitInformationId, position);
      }

      orderService.deleteForAllCustomerOrdersByStatus(model.getBackendId(),
          SaleOrderStatus.DRAFT.getId());//TODO: For draft
      Bundle args = new Bundle();
      args.putLong(Constants.VISIT_ID, visitInformationId);
      args.putLong(Constants.CUSTOMER_ID, model.getPrimaryKey());
      args.putLong(Constants.ORIGIN_VISIT_ID, visitInformationId);
      Analytics.logContentView("Map Visit");
      mainActivity.changeFragment(MainActivity.VISIT_DETAIL_FRAGMENT_ID, args, false);

    } catch (BusinessException e) {
      Timber.e(e);
      ToastUtil.toastError(mainActivity, e);
    } catch (Exception e) {
      Timber.e(e);
      ToastUtil.toastError(mainActivity, new UnknownSystemException(e));
    }
  }

//  ---------------------------------------------------------------------------------------------

}
