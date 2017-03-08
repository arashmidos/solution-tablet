package com.parsroyal.solutiontablet.ui.fragment;

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
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.parsroyal.solutiontablet.R;
import com.parsroyal.solutiontablet.constants.Constants;
import com.parsroyal.solutiontablet.constants.CustomerStatus;
import com.parsroyal.solutiontablet.constants.VisitInformationDetailType;
import com.parsroyal.solutiontablet.data.entity.Customer;
import com.parsroyal.solutiontablet.data.entity.VisitInformationDetail;
import com.parsroyal.solutiontablet.service.CustomerService;
import com.parsroyal.solutiontablet.service.VisitService;
import com.parsroyal.solutiontablet.service.impl.CustomerServiceImpl;
import com.parsroyal.solutiontablet.ui.MainActivity;
import com.parsroyal.solutiontablet.util.Empty;

import java.util.Locale;

import butterknife.BindInt;
import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Arash on 2016-08-04.
 */
public class SaveLocationFragment extends BaseFragment implements
        GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, OnMapReadyCallback
{
    public static final String TAG = SaveLocationFragment.class.getSimpleName();

    @BindInt(R.integer.camera_zoom)
    int cameraZoom;
    @BindView(R.id.error_msg)
    TextView errorMsg;
    @BindView(R.id.layout_container)
    FrameLayout layoutContainer;
    @BindView(R.id.locationMarkertext)
    TextView locationMarkertext;
    @BindView(R.id.locationMarker)
    LinearLayout markerLayout;
    private double lat, lng = 0.0;
    private long customerId;
    private long visitId;
    private GoogleApiClient googleApiClient;
    private Location currentLocation;
    private GoogleMap map;
    private LatLng currentLatlng;
    private CustomerService customerService;
    private VisitService visitService;
    private Customer customer;
    private boolean isFirstTime = true;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.fragment_save_location, null);
        ButterKnife.bind(this, view);

        Bundle arguments = getArguments();

        customerService = new CustomerServiceImpl(getActivity());
        customerId = arguments.getLong(Constants.CUSTOMER_ID);
        visitId = arguments.getLong(Constants.VISIT_ID);
        customer = customerService.getCustomerById(customerId);

        lat = customer.getxLocation();
        lng = customer.getyLocation();

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

        googleApiClient.connect();
    }

    @Override
    public void onStop()
    {
        super.onStop();
        if (googleApiClient.isConnected())
        {
            googleApiClient.disconnect();
        }
    }

    @Override
    public int getFragmentId()
    {
        return MainActivity.SAVE_LOCATION_FRAGMENT_ID;
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

        Log.d(TAG, "Connection suspended");
        googleApiClient.connect();
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult)
    {
        errorMsg.setText(String.format(Locale.US, getString(R.string.error_google_play_not_available), connectionResult.getErrorCode()));
    }

    @Override
    public void onMapReady(GoogleMap googleMap)
    {
        map = googleMap;
        currentLocation = LocationServices.FusedLocationApi.getLastLocation(googleApiClient);

        //If we've set it before
        if (lat != 0 && lng != 0)
        {
            Marker m = map.addMarker(new MarkerOptions()
                    .position(new LatLng(lat, lng)).title("").snippet("").icon(BitmapDescriptorFactory
                            .fromResource(R.drawable.ic_action_flag)));
            markerLayout.setVisibility(View.GONE);
            isFirstTime = false;
        }
        map.setMyLocationEnabled(true);
        map.getUiSettings().setZoomControlsEnabled(true);
        if (Empty.isNotEmpty(currentLocation))
        {
            currentLatlng = new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude());
            map.animateCamera(CameraUpdateFactory.newLatLngZoom(currentLatlng, cameraZoom), 4000, null);
        }

        map.setOnCameraChangeListener(new GoogleMap.OnCameraChangeListener()
        {
            @Override
            public void onCameraChange(CameraPosition cameraPosition)
            {
                currentLatlng = map.getCameraPosition().target;
                locationMarkertext.setText(getString(R.string.set_your_location));
                if (isFirstTime)
                {
                    map.clear();
                }
                markerLayout.setVisibility(View.VISIBLE);
            }
        });

        markerLayout.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                map.clear();
                LatLng temp = new LatLng(currentLatlng.latitude, currentLatlng.longitude);

                //Save customer location
                customer.setxLocation(currentLatlng.latitude);
                customer.setyLocation(currentLatlng.longitude);
                customer.setStatus(CustomerStatus.UPDATED.getId());
                customerService.saveCustomer(customer);
                //
                VisitInformationDetail visitDetail = new VisitInformationDetail(visitId, VisitInformationDetailType.SAVE_LOCATION, 0);
                visitService.saveVisitDetail(visitDetail);

                Marker m = map.addMarker(new MarkerOptions()
                        .position(temp).title(getString(R.string.location_set)).snippet("").icon(BitmapDescriptorFactory
                                .fromResource(R.drawable.ic_action_flag)));
                isFirstTime = true;
                m.setDraggable(true);
                markerLayout.setVisibility(View.GONE);
            }
        });
    }
}
