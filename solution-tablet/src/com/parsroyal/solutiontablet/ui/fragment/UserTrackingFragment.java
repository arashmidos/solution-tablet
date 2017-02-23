package com.parsroyal.solutiontablet.ui.fragment;

import android.content.IntentSender;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.alirezaafkar.sundatepicker.DatePicker;
import com.alirezaafkar.sundatepicker.components.JDF;
import com.alirezaafkar.sundatepicker.interfaces.DateSetListener;
import com.parsroyal.solutiontablet.R;
import com.parsroyal.solutiontablet.biz.KeyValueBiz;
import com.parsroyal.solutiontablet.biz.impl.KeyValueBizImpl;
import com.parsroyal.solutiontablet.data.entity.KeyValue;
import com.parsroyal.solutiontablet.data.model.PositionModel;
import com.parsroyal.solutiontablet.service.CustomerService;
import com.parsroyal.solutiontablet.service.PositionService;
import com.parsroyal.solutiontablet.service.impl.CustomerServiceImpl;
import com.parsroyal.solutiontablet.service.impl.PositionServiceImpl;
import com.parsroyal.solutiontablet.ui.MainActivity;
import com.parsroyal.solutiontablet.util.DateUtil;
import com.parsroyal.solutiontablet.util.Empty;
import com.parsroyal.solutiontablet.util.NotificationUtil;
import com.parsroyal.solutiontablet.util.SunDate;
import com.parsroyal.solutiontablet.util.ToastUtil;
import com.parsroyal.solutiontablet.util.constants.ApplicationKeys;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.ErrorDialogFragment;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.maps.android.clustering.ClusterManager;

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
        GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, OnMapReadyCallback, GoogleMap.OnCameraChangeListener, DateSetListener
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

    private double lat, lng = 0.0;
    private long customerId;
    private GoogleApiClient googleApiClient;
    private Location currentLocation;
    private GoogleMap map;
    private LatLng currentLatlng;
    private PositionService positionService;
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
    private ClusterManager<PositionModel> clusterManager;
    private Polyline polyline;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        keyValueBiz = new KeyValueBizImpl(getActivity());
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
        showCustomers.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener()
        {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked)
            {
                if (isChecked)
                {
                    showCustomers();
                } else
                {
                    clusterManager.clearItems();
                    clusterManager.cluster();
                }
            }
        });

        showTrack.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener()
        {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked)
            {
                if (isChecked)
                {
                    doFilter();
                } else
                {
                    polylines.remove(polyline);
                    polyline.remove();
                }
            }
        });

        positionService = new PositionServiceImpl(getActivity());
        customerService = new CustomerServiceImpl(getActivity());

        loadCalendars();

        googleApiClient = new GoogleApiClient.Builder(getActivity())
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
        fromDate.setHint(startDate.getYear() % 100 + "/" + startDate.getMonth() + "/" + startDate.getDay());
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
            errorMsg.setText(String.format(Locale.US,getString(R.string.error_google_play_not_available), result.getErrorCode()));
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
            NotificationUtil.showGPSDisabled(getActivity());
        } else
        {
            currentLatlng = new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude());

        }

        map.setMyLocationEnabled(true);
        map.getUiSettings().setAllGesturesEnabled(true);
        map.getUiSettings().setZoomControlsEnabled(true);
        map.animateCamera(CameraUpdateFactory.newLatLngZoom(currentLatlng, cameraZoom), 4000, null);

        map.setOnCameraChangeListener(this);

        doFilter();
        showCustomers();
    }

    private void showCustomers()
    {
        clusterManager = new ClusterManager<>(getActivity(), map);
//        clusterManager.setRenderer(new CustomerRenderer());
        map.setOnCameraIdleListener(clusterManager);
        map.setOnMarkerClickListener(clusterManager);
        map.setOnInfoWindowClickListener(clusterManager);
        addItems();
        clusterManager.cluster();
    }

    private void addItems()
    {
        List<PositionModel> customerPositionList = /*sampleData();*/customerService.getCustomerPositions(null);
        clusterManager.addItems(customerPositionList);
        Toast.makeText(getActivity(), "Position Size:" + customerPositionList.size(), Toast.LENGTH_SHORT).show();
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
            ToastUtil.toastMessage(getActivity(), getString(R.string.error_tracking_cal1_empty));
            return;
        }
        if (toDate.getHint().equals("--"))
        {
            ToastUtil.toastMessage(getActivity(), getString(R.string.error_tracking_cal2_empty));
            return;
        }
        Calendar c1 = startDate.getCalendar();
        Calendar c2 = endDate.getCalendar();

        long days = DateUtil.compareDatesInDays(c1, c2);

        if (days + 1 > 5)
        {
            ToastUtil.toastMessage(getActivity(), getString(R.string.error_report_is_huge));
        } else if (days < 0)
        {
            ToastUtil.toastMessage(getActivity(), getString(R.string.error_report_date_invalid));
        } else
        {
            Date from = DateUtil.startOfDay(c1);
            Date to = DateUtil.endOfDay(c2);

            List<LatLng> route = positionService.getAllPositionLatLngByDate(from, to);

            PolylineOptions polyOptions = new PolylineOptions();
            polyOptions.color(getResources().getColor(colors[3]));
            polyOptions.width(4);
            polyOptions.addAll(route);
            polyline = map.addPolyline(polyOptions);
            polylines.add(polyline);
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
}
