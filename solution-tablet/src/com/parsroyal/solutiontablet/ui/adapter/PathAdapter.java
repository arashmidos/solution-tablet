package com.parsroyal.solutiontablet.ui.adapter;

import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.RecyclerView.Adapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.parsroyal.solutiontablet.R;
import com.parsroyal.solutiontablet.constants.Constants;
import com.parsroyal.solutiontablet.data.listmodel.VisitLineListModel;
import com.parsroyal.solutiontablet.ui.MainActivity;
import com.parsroyal.solutiontablet.util.NumberUtil;
import java.util.List;

public class PathAdapter extends Adapter<PathAdapter.ViewHolder> {

  //  private final HashSet<MapView> mMaps = new HashSet<>();
  private List<VisitLineListModel> visitLineList;
  private LayoutInflater inflater;
  private MainActivity mainActivity;
//  private LatLng loation = new LatLng(35.6892, 51.3890);


  public PathAdapter(MainActivity mainActivity, List<VisitLineListModel> visitLineList) {
    this.mainActivity = mainActivity;
    this.visitLineList = visitLineList;
    inflater = LayoutInflater.from(mainActivity);
  }

  /*private static void setMapLocation(GoogleMap map, LatLng data) {
    // Add a marker for this item and set the camera
    map.moveCamera(CameraUpdateFactory.newLatLngZoom(data, 13f));
    map.addMarker(new MarkerOptions().position(data));

    // Set the map type back to normal.
    map.setMapType(GoogleMap.MAP_TYPE_NORMAL);
  }*/

  @Override
  public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
    View view = inflater.inflate(R.layout.item_visitline_list, parent, false);
    return new ViewHolder(view);
  }

  @Override
  public void onBindViewHolder(ViewHolder holder, int position) {
    VisitLineListModel model = visitLineList.get(position);
    holder.setData(model, position);
//    holder.initializeMapView();
    // Keep track of MapView
//    mMaps.add(holder.mapView);

//    holder.mapView.setTag(loation);

    // Ensure the map has been initialised by the on map ready callback in ViewHolder.
    // If it is not ready yet, it will be initialised with the NamedLocation set as its tag
    // when the callback is received.
//    if (holder.map != null) {
//       The map is already ready to be used
//      setMapLocation(holder.map, loation);
//    }
  }

  @Override
  public int getItemCount() {
    return visitLineList.size();
  }

  public class ViewHolder extends RecyclerView.ViewHolder/* implements OnMapReadyCallback*/ {

    @BindView(R.id.visitline_name)
    TextView visitlineName;
    @BindView(R.id.list_img)
    ImageView customerList;
    @BindView(R.id.visitline_detail)
    TextView visitlineDetail;
    @BindView(R.id.customer_count)
    TextView customerCount;
    @BindView(R.id.divider)
    View divider;
    @BindView(R.id.map_item)
    MapView mapView;
    GoogleMap map;
    private VisitLineListModel model;
    private int position;

    public ViewHolder(View itemView) {
      super(itemView);
      ButterKnife.bind(this, itemView);
    }

    @OnClick({R.id.list_img, R.id.visitline_lay, R.id.visitline_layout})
    public void onClick(View view) {
      switch (view.getId()) {
        case R.id.visitline_lay:
        case R.id.visitline_layout:
          Bundle bundle = new Bundle();
          bundle.putLong(Constants.VISITLINE_BACKEND_ID, model.getPrimaryKey());
          mainActivity.changeFragment(MainActivity.PATH_DETAIL_FRAGMENT_ID, bundle, true);
          break;
        case R.id.list_img:
//          Bundle clickBundle = new Bundle();
//          clickBundle.putBoolean(Constants.IS_CLICKABLE, true);
//          mainActivity.changeFragment(MainActivity.CUSTOMER_SEARCH_FRAGMENT, clickBundle, true);
          break;
      }
    }

    public void setData(VisitLineListModel model, int position) {
      this.model = model;
      this.position = position;
      if (model.getTitle().equals(mainActivity.getString(R.string.manual_visit_line))) {
        customerList.setVisibility(View.VISIBLE);
        if (model.getCustomerCount() == 1) {
          customerCount.setText(mainActivity.getString(R.string.no_customer_exist));
          divider.setVisibility(View.GONE);
          visitlineDetail.setVisibility(View.GONE);
        } else {
          customerCount.setText(NumberUtil.digitsToPersian(String
              .format(mainActivity.getString(R.string.x_customers), model.getCustomerCount() - 1)));
          divider.setVisibility(View.VISIBLE);
          visitlineDetail.setVisibility(View.VISIBLE);
        }
      } else {
        customerList.setVisibility(View.GONE);
        customerCount.setText(NumberUtil.digitsToPersian(String
            .format(mainActivity.getString(R.string.x_customers), model.getCustomerCount())));
        divider.setVisibility(View.VISIBLE);
        visitlineDetail.setVisibility(View.VISIBLE);
      }
      visitlineName.setText(NumberUtil.digitsToPersian(model.getTitle()));
      visitlineDetail.setText(NumberUtil.digitsToPersian(model.getCode()));
    }

   /* @Override
    public void onMapReady(GoogleMap googleMap) {
      MapsInitializer.initialize(mainActivity.getApplicationContext());
      map = googleMap;
      map.getUiSettings().setMapToolbarEnabled(false);
      map.getUiSettings().setAllGesturesEnabled(false);

      LatLng data = (LatLng) mapView.getTag();
      if (data != null) {
        setMapLocation(map, data);
      }
    }*/

    /**
     * Initialises the MapView by calling its lifecycle methods.
     */
   /* public void initializeMapView() {
      if (mapView != null) {
        // Initialise the MapView
        mapView.onCreate(null);
        // Set the map ready callback to receive the GoogleMap object
        mapView.getMapAsync(this);
      }
    }*/
  }
}
