package com.conta.comer.ui.fragment;

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
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.conta.comer.R;
import com.conta.comer.data.entity.Customer;
import com.conta.comer.data.model.PositionModel;
import com.conta.comer.service.CustomerService;
import com.conta.comer.service.PositionService;
import com.conta.comer.service.impl.CustomerServiceImpl;
import com.conta.comer.service.impl.PositionServiceImpl;
import com.conta.comer.ui.MainActivity;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.ErrorDialogFragment;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Polyline;
import com.google.maps.android.clustering.ClusterManager;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindInt;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by Arash on 2016-11-12
 */
public class CustomerTrackingFragment extends BaseContaFragment implements
        GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener,
        OnMapReadyCallback
{
    public static final String TAG = CustomerTrackingFragment.class.getSimpleName();

    @BindInt(R.integer.camera_zoom_tracking)
    int cameraZoom;
    @BindView(R.id.error_msg)
    TextView errorMsg;
    @BindView(R.id.layout_container)
    FrameLayout layoutContainer;
    @BindView(R.id.filter)
    ImageView filter;
    @BindView(R.id.filter_layout)
    LinearLayout filterLayout;

    private double lat, lng = 0.0;
    private long customerId;
    private GoogleApiClient googleApiClient;
    private Location currentLocation;
    private GoogleMap map;
    private LatLng currentLatlng;
    private PositionService positionService;
    private CustomerService customerService;
    private ClusterManager<PositionModel> clusterManager;

    private ArrayList<Polyline> polylines = new ArrayList<>();
    private int[] colors = new int[]{R.color.green,
            R.color.red,
            R.color.violet,
            R.color.blue,
            R.color.orange};
    private boolean mResolvingError = false;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.fragment_customer_tracking, null);
        ButterKnife.bind(this, view);

        positionService = new PositionServiceImpl(getActivity());
        customerService = new CustomerServiceImpl(getActivity());

        googleApiClient = new GoogleApiClient.Builder(getActivity())
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API).build();

        return view;
    }

    @Override
    public void onStart()
    {
        super.onStart();

        showProgressDialog(getString(R.string.message_loading_map));
        googleApiClient.connect();
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
        return MainActivity.CUSTOMER_TRACKING_FRAGMENT_ID;
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
            errorMsg.setText(String.format(getString(R.string.error_google_play_not_available), result.getErrorCode()));
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
            //If user location is not available in the rare situation, point it to center of Tehran
//            currentLatlng = new LatLng(35.6961, 51.4231);
            Toast.makeText(getActivity(), "Location is NULL", Toast.LENGTH_SHORT).show();
        } else
        {
            currentLatlng = new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude());
        }

//        map.moveCamera(CameraUpdateFactory.newLatLngZoom(currentLatlng, cameraZoom));

        clusterManager = new ClusterManager<PositionModel>(getActivity(), map);
//        clusterManager.setRenderer(new CustomerRenderer());
        map.setOnCameraIdleListener(clusterManager);
        map.setOnMarkerClickListener(clusterManager);
        map.setOnInfoWindowClickListener(clusterManager);
//        clusterManager.setOnClusterClickListener(this);
//        clusterManager.setOnClusterInfoWindowClickListener(this);
//        clusterManager.setOnClusterItemClickListener(this);
//        clusterManager.setOnClusterItemInfoWindowClickListener(this);

        addItems();
        clusterManager.cluster();

        map.setMyLocationEnabled(true);
        map.getUiSettings().setAllGesturesEnabled(true);
        map.getUiSettings().setZoomControlsEnabled(true);
        map.animateCamera(CameraUpdateFactory.newLatLngZoom(currentLatlng, cameraZoom), 4000, null);

//        map.setOnCameraChangeListener(this);

//        List<LatLng> route = positionService.getAllPositionLatLng();
//
//        Log.d(TAG, "Route size: " + route.size());
//        Toast.makeText(getActivity(), "Route size: " + route.size(), Toast.LENGTH_LONG).show();
//
//        PolylineOptions polyOptions = new PolylineOptions();
//        polyOptions.color(getResources().getColor(colors[3]));
//        polyOptions.width(10);
//        polyOptions.addAll(route);
//        Polyline polyline = map.addPolyline(polyOptions);
//        polylines.add(polyline);
    }

    private void addItems()
    {
        List<PositionModel> customerPositionList = /*sampleData();*/customerService.getCustomerPositions(null);
        clusterManager.addItems(customerPositionList);
        Toast.makeText(getActivity(), "Position Size:" + customerPositionList.size(), Toast.LENGTH_SHORT).show();
    }

    private List<PositionModel> sampleData()
    {
        List<PositionModel> list = new ArrayList<>();
        for (int i = 0; i < 10; i++)
        {
            Customer c = new Customer();
            double offset = i / 60d;
            c.setxLocation(38.0307597598503 + offset);
            c.setyLocation(46.385442950588 + offset);
            list.add(new PositionModel(c));
        }

        return list;
    }

    @OnClick(R.id.filter)
    public void onClick()
    {
        filterLayout.setVisibility(View.VISIBLE);
        filter.setVisibility(View.GONE);
    }

    @OnClick({R.id.cancel_btn, R.id.filter_btn})
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
        }
    }

    private void doFilter()
    {

    }

}
