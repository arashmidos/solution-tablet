package com.parsroyal.solutiontablet.ui.fragment;

import android.content.IntentSender;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.alirezaafkar.sundatepicker.DatePicker;
import com.alirezaafkar.sundatepicker.components.JDF;
import com.alirezaafkar.sundatepicker.interfaces.DateSetListener;
import com.crashlytics.android.answers.Answers;
import com.crashlytics.android.answers.ContentViewEvent;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.ErrorDialogFragment;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
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
import com.parsroyal.solutiontablet.R;
import com.parsroyal.solutiontablet.biz.KeyValueBiz;
import com.parsroyal.solutiontablet.biz.impl.KeyValueBizImpl;
import com.parsroyal.solutiontablet.constants.Constants;
import com.parsroyal.solutiontablet.constants.SaleOrderStatus;
import com.parsroyal.solutiontablet.data.entity.KeyValue;
import com.parsroyal.solutiontablet.data.listmodel.CustomerListModel;
import com.parsroyal.solutiontablet.exception.BusinessException;
import com.parsroyal.solutiontablet.exception.GPSIsNotEnabledException;
import com.parsroyal.solutiontablet.exception.UnknownSystemException;
import com.parsroyal.solutiontablet.service.CustomerService;
import com.parsroyal.solutiontablet.service.LocationService;
import com.parsroyal.solutiontablet.service.PositionService;
import com.parsroyal.solutiontablet.service.SettingService;
import com.parsroyal.solutiontablet.service.VisitService;
import com.parsroyal.solutiontablet.service.impl.CustomerServiceImpl;
import com.parsroyal.solutiontablet.service.impl.LocationServiceImpl;
import com.parsroyal.solutiontablet.service.impl.PositionServiceImpl;
import com.parsroyal.solutiontablet.service.impl.SettingServiceImpl;
import com.parsroyal.solutiontablet.service.impl.VisitServiceImpl;
import com.parsroyal.solutiontablet.service.order.impl.SaleOrderServiceImpl;
import com.parsroyal.solutiontablet.ui.MainActivity;
import com.parsroyal.solutiontablet.ui.observer.FindLocationListener;
import com.parsroyal.solutiontablet.util.DateUtil;
import com.parsroyal.solutiontablet.util.Empty;
import com.parsroyal.solutiontablet.util.ImageUtil;
import com.parsroyal.solutiontablet.util.NotificationUtil;
import com.parsroyal.solutiontablet.util.SunDate;
import com.parsroyal.solutiontablet.util.ToastUtil;
import com.parsroyal.solutiontablet.util.constants.ApplicationKeys;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import butterknife.BindInt;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by Arash on 2016-09-14.
 */
public class UserTrackingFragment extends BaseFragment implements
        GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener,
        OnMapReadyCallback,
        GoogleMap.OnCameraChangeListener, DateSetListener
{

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
    LinearLayout filterLayout;
    @BindView(R.id.show_customers)
    CheckBox showCustomers;
    @BindView(R.id.show_track)
    CheckBox showTrack;

    private GoogleApiClient googleApiClient;
    private Location currentLocation;
    private GoogleMap map;
    private LatLng currentLatlng;
    private PositionService positionService;
    private SettingService settingService;
    private VisitService visitService;
    private LocationService locationService;
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
    private Cluster<CustomerListModel> clickedCluster;
    private CustomerListModel clickedClusterItem;
    private Marker startMarker;
    private Marker endMarker;
    private boolean distanceServiceEnabled;
    private FragmentActivity context;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
    {
        context = getActivity();
        keyValueBiz = new KeyValueBizImpl(context);
        salesmanId = keyValueBiz.findByKey(ApplicationKeys.SALESMAN_ID);
        if (Empty.isEmpty(salesmanId))
        {
            View view = inflater.inflate((R.layout.view_error_page), null);
            TextView errorView = (TextView) view.findViewById(R.id.error_msg);
            errorView.setText(errorView.getText() + "\n\n" + "به قسمت تنظیمات مراجعه کنید");
            return view;
        }

        View view = inflater.inflate(R.layout.fragment_user_tracking, null);

        ButterKnife.bind(this, view);
        showCustomers.setOnCheckedChangeListener((buttonView, isChecked) ->
        {
            if (isChecked)
            {
                showCustomers();
            } else
            {
                if (Empty.isNotEmpty(clusterManager))
                {
                    clusterManager.clearItems();
                    clusterManager.cluster();
                }
            }
        });

        showTrack.setOnCheckedChangeListener((buttonView, isChecked) ->
        {
            if (isChecked)
            {
                doFilter();
            } else
            {
                polylines.remove(polyline);
                polyline.remove();
                if (Empty.isNotEmpty(startMarker))
                {
                    startMarker.remove();
                    endMarker.remove();
                }
            }
        });

        positionService = new PositionServiceImpl(context);
        customerService = new CustomerServiceImpl(context);
        settingService = new SettingServiceImpl(context);
        visitService = new VisitServiceImpl(context);
        locationService = new LocationServiceImpl(context);
        orderService = new SaleOrderServiceImpl(context);

        String distanceEnabled = settingService
                .getSettingValue(ApplicationKeys.SETTING_CALCULATE_DISTANCE_ENABLE);
        distanceServiceEnabled = Empty.isNotEmpty(distanceEnabled) && distanceEnabled.equals("1");

        loadCalendars();

        googleApiClient = new GoogleApiClient.Builder(context)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API).build();

        return view;
    }

    private void loadCalendars()
    {
        toDate.setHint(endDate.getYear() % 100 + "/" + endDate.getMonth() + "/" + endDate.getDay());
        Calendar calendar = endDate.getCalendar();
        calendar.add(Calendar.DAY_OF_YEAR, -1);
        startDate.setDate(new JDF(calendar));
        fromDate
                .setHint(startDate.getYear() % 100 + "/" + startDate.getMonth() + "/" + startDate.getDay());
    }

    @Override
    public void onStart()
    {
        super.onStart();
        if (Empty.isNotEmpty(salesmanId))
        {
            showProgressDialog(getString(R.string.message_loading_map));
            if (googleApiClient != null)
            {
                googleApiClient.connect();
            }
        }
    }

    @Override
    public void onStop()
    {
        super.onStop();
        if (googleApiClient != null && googleApiClient.isConnected())
        {
            googleApiClient.disconnect();
        }
    }

    @Override
    public int getFragmentId()
    {
        return MainActivity.USER_TRACKING_FRAGMENT_ID;
    }

    @Override
    public void onConnected(@Nullable Bundle bundle)
    {
        layoutContainer.setVisibility(View.VISIBLE);
        errorMsg.setVisibility(View.GONE);
        FragmentManager fm = getChildFragmentManager();
        Fragment fragment = fm.findFragmentById(R.id.map);
        if (fragment == null)
        {
            fragment = SupportMapFragment.newInstance();
        }
        ((SupportMapFragment) fragment).getMapAsync(this);
    }

    @Override
    public void onConnectionSuspended(int i)
    {
        dismissProgressDialog();
        Log.d(TAG, "Connection suspended");
        googleApiClient.connect();
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult result)
    {
        dismissProgressDialog();
        if (mResolvingError)
        {
            errorMsg.setText(String.format(Locale.US, getString(R.string.error_google_play_not_available),
                    result.getErrorCode()));
            // Already attempting to resolve an error.
            return;
        } else if (result.hasResolution())
        {
            try
            {
                mResolvingError = true;
                result.startResolutionForResult(getActivity(), 1001);
            } catch (IntentSender.SendIntentException e)
            {
                // There was an error with the resolution intent. Try again.
                googleApiClient.connect();
            }
        } else
        {
            // Show dialog using GoogleApiAvailability.getErrorDialog()
            showErrorDialog(result.getErrorCode());
            mResolvingError = true;
        }
    }

    private void showErrorDialog(int errorCode)
    {
        // Create a fragment for the error dialog
        ErrorDialogFragment dialogFragment = new ErrorDialogFragment();
        // Pass the error that should be displayed
        Bundle args = new Bundle();
        args.putInt("dialog_error", errorCode);
        dialogFragment.setArguments(args);
        dialogFragment.show(getActivity().getFragmentManager(), "errordialog");
    }

    @Override
    public void onMapReady(GoogleMap googleMap)
    {
        dismissProgressDialog();

        map = googleMap;

        currentLocation = LocationServices.FusedLocationApi.getLastLocation(googleApiClient);

        if (currentLocation == null)
        {
            try
            {
                new LocationServiceImpl(getContext()).findCurrentLocation(new FindLocationListener()
                {
                    @Override
                    public void foundLocation(Location location)
                    {
                        map.animateCamera(CameraUpdateFactory.newLatLngZoom(
                                new LatLng(location.getLatitude(), location.getLongitude())
                                , cameraZoom), 4000, null);
                    }

                    @Override
                    public void timeOut()
                    {
                    }
                });
            } catch (GPSIsNotEnabledException ignore)
            {
                NotificationUtil.showGPSDisabled(getActivity());
            }

        } else
        {
            currentLatlng = new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude());
        }

        map.setMyLocationEnabled(true);
        map.getUiSettings().setAllGesturesEnabled(true);
        map.getUiSettings().setZoomControlsEnabled(true);
        if (Empty.isNotEmpty(currentLatlng))
        {
            map.animateCamera(CameraUpdateFactory.newLatLngZoom(currentLatlng, cameraZoom), 4000, null);
        }

        map.setOnCameraChangeListener(this);

        doFilter();
        showCustomers();
    }

    private void showCustomers()
    {
        clusterManager = new ClusterManager<>(getActivity(), map);
        clusterManager.setRenderer(new CustomRenderer());
        map.setOnCameraIdleListener(clusterManager);
        map.setOnMarkerClickListener(clusterManager);
        map.setOnInfoWindowClickListener(clusterManager);
        map.setInfoWindowAdapter(clusterManager.getMarkerManager());
        map.setOnInfoWindowClickListener(marker ->
        {
            Float distance = clickedClusterItem.getDistance();
            if (distanceServiceEnabled && distance > Constants.MAX_DISTANCE)
            {
                ToastUtil.toastError(getActivity(), getString(R.string.error_distance_too_far_for_action));
                return;
            }
            doEnter();
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

    private void doEnter()
    {
        try
        {
            final Long visitInformationId = visitService.startVisiting(clickedClusterItem.getBackendId());

            locationService.findCurrentLocation(new FindLocationListener()
            {
                @Override
                public void foundLocation(Location location)
                {
                    try
                    {
                        visitService.updateVisitLocation(visitInformationId, location);
                    } catch (Exception e)
                    {
                        Log.e(TAG, e.getMessage(), e);
                    }
                }

                @Override
                public void timeOut()
                {
                }
            });

            orderService.deleteForAllCustomerOrdersByStatus(clickedClusterItem.getBackendId(),
                    SaleOrderStatus.DRAFT.getId());
            Bundle args = new Bundle();
            args.putLong(Constants.VISIT_ID, visitInformationId);
            args.putLong(Constants.CUSTOMER_ID, clickedClusterItem.getPrimaryKey());
            Answers.getInstance().logContentView(new ContentViewEvent().putContentName("Map Visit"));
            ((MainActivity) context).changeFragment(MainActivity.VISIT_DETAIL_FRAGMENT_ID, args, false);


        } catch (BusinessException e)
        {
            Log.e(TAG, e.getMessage(), e);
            ToastUtil.toastError(context, e);
        } catch (Exception e)
        {
            Log.e(TAG, e.getMessage(), e);
            ToastUtil.toastError(context, new UnknownSystemException(e));
        } finally
        {
            dismissProgressDialog();
        }
    }

    private void addItems()
    {
        List<CustomerListModel> customerPositionList = customerService
                .getFilteredCustomerList(null, null);
        clusterManager.addItems(customerPositionList);
    }

    @Override
    public void onCameraChange(CameraPosition cameraPosition)
    {
    }

    @OnClick(R.id.filter)
    public void onClick()
    {
        filterLayout.setVisibility(View.VISIBLE);
        filter.setVisibility(View.GONE);
    }

    @OnClick({R.id.cancel_btn, R.id.filter_btn, R.id.toDate, R.id.fromDate})
    public void onClick(View view)
    {
        switch (view.getId())
        {
            case R.id.filter_btn:
                doFilter();
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
        }
    }

    private void doFilter()
    {
        if (fromDate.getHint().equals("--"))
        {
            ToastUtil.toastError(getActivity(), getString(R.string.error_tracking_cal1_empty));
            return;
        }
        if (toDate.getHint().equals("--"))
        {
            ToastUtil.toastError(getActivity(), getString(R.string.error_tracking_cal2_empty));
            return;
        }
        Calendar c1 = startDate.getCalendar();
        Calendar c2 = endDate.getCalendar();

        long days = DateUtil.compareDatesInDays(c1, c2);

        if (days + 1 > 5)
        {
            ToastUtil.toastError(getActivity(), getString(R.string.error_report_is_huge));
        } else if (days < 0)
        {
            ToastUtil.toastError(getActivity(), getString(R.string.error_report_date_invalid));
        } else
        {
            Date from = DateUtil.startOfDay(c1);
            Date to = DateUtil.endOfDay(c2);

            List<LatLng> route = positionService.getAllPositionLatLngByDate(from, to);
            if (route.size() > 0)
            {
                startMarker = map.addMarker(new MarkerOptions()
                        .position(route.get(0))
                        .icon(BitmapDescriptorFactory.fromBitmap(ImageUtil.getBitmapFromVectorDrawable(
                                getActivity(), R.drawable.ic_place_green_48dp))));
            }
            if (route.size() > 1)
            {
                endMarker = map.addMarker(new MarkerOptions()
                        .position(route.get(route.size() - 1))
                        .icon(BitmapDescriptorFactory.fromBitmap(ImageUtil.getBitmapFromVectorDrawable(
                                getActivity(), R.drawable.ic_place_red_48dp))));
            }
            PolylineOptions polyOptions = new PolylineOptions();
            polyOptions.color(getResources().getColor(colors[3]));
            polyOptions.width(4);
            polyOptions.addAll(route);
            polyline = map.addPolyline(polyOptions);
            polylines.add(polyline);

            Answers.getInstance().logContentView(new ContentViewEvent().putContentName("Map Filter"));
        }
    }

    @Override
    public void onDateSet(int id, @Nullable Calendar calendar, int day, int month, int year)
    {
        int tempYear = year % 100;
        if (id == 2)
        {
            toDate.setHint(tempYear + "/" + month + "/" + day);
            endDate.setDate(day, month, year);
        } else if (id == 3)
        {
            fromDate.setHint(tempYear + "/" + month + "/" + day);
            startDate.setDate(day, month, year);
        }
    }

    @Override
    public void onDestroyView()
    {
        super.onDestroyView();
        Fragment f = getChildFragmentManager().findFragmentById(R.id.map);
        if (f != null)
        {
            getChildFragmentManager().beginTransaction().remove(f).commitAllowingStateLoss();
        }
    }

    class CustomerMarkerAdapter implements GoogleMap.InfoWindowAdapter
    {

        @BindView(R.id.customer_name)
        TextView customerName;
        @BindView(R.id.customer_address)
        TextView customerAddress;
        @BindView(R.id.customer_last_visit)
        TextView customerLastVisit;
        @BindView(R.id.enter_btn)
        Button enterBtn;

        @Override
        public View getInfoWindow(Marker marker)
        {
            return null;
        }

        @Override
        public View getInfoContents(Marker marker)
        {
            View v = getActivity().getLayoutInflater().inflate(R.layout.map_popup, null);
            ButterKnife.bind(this, v);

            customerName.setText(clickedClusterItem.getTitle());
            customerAddress.setText(clickedClusterItem.getSnippet());
            customerLastVisit.setText(clickedClusterItem.getLastVisit());
            return v;
        }
    }

    class CustomRenderer extends DefaultClusterRenderer<CustomerListModel>
    {

        public CustomRenderer()
        {
            super(context.getApplicationContext(), map, clusterManager);
        }

        @Override
        protected void onBeforeClusterItemRendered(CustomerListModel item,
                                                   MarkerOptions markerOptions)
        {
            super.onBeforeClusterItemRendered(item, markerOptions);
            markerOptions.title(item.getTitle());

            BitmapDescriptor icon = BitmapDescriptorFactory
                    .fromBitmap(ImageUtil.getBitmapFromVectorDrawable(
                            getActivity(), R.drawable.ic_location_on_red_36dp));

            if (item.hasOrder())
            {
                icon = BitmapDescriptorFactory.fromBitmap(ImageUtil.getBitmapFromVectorDrawable(
                        getActivity(), R.drawable.ic_location_on_green_36dp));
            } else if (item.hasRejection())
            {
                icon = BitmapDescriptorFactory.fromBitmap(ImageUtil.getBitmapFromVectorDrawable(
                        getActivity(), R.drawable.ic_location_on_black_36dp));
            } else if (item.isVisited())
            {
                icon = BitmapDescriptorFactory.fromBitmap(ImageUtil.getBitmapFromVectorDrawable(
                        getActivity(), R.drawable.ic_location_on_blue_36dp));
            }

            markerOptions.icon(icon);
        }
    }
}
