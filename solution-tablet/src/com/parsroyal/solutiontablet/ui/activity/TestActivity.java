package com.parsroyal.solutiontablet.ui.activity;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.preference.PreferenceManager;
import androidx.core.content.ContextCompat;
import androidx.appcompat.app.AppCompatActivity;
import android.widget.Toast;
import com.parsroyal.solutiontablet.R;
import com.parsroyal.solutiontablet.biz.impl.RestServiceImpl;
import com.parsroyal.solutiontablet.data.entity.Customer;
import com.parsroyal.solutiontablet.util.DialogUtil;
import com.parsroyal.solutiontablet.util.NumberUtil;
import com.parsroyal.solutiontablet.vrp.model.Leg;
import com.parsroyal.solutiontablet.vrp.model.LocationResponse;
import com.parsroyal.solutiontablet.vrp.model.OptimizedRouteResponse;
import java.util.ArrayList;
import java.util.List;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.osmdroid.api.IMapController;
import org.osmdroid.bonuspack.utils.PolylineEncoder;
import org.osmdroid.config.Configuration;
import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.CustomZoomButtonsController.Visibility;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.ItemizedIconOverlay;
import org.osmdroid.views.overlay.Marker;
import org.osmdroid.views.overlay.OverlayItem;
import org.osmdroid.views.overlay.Polyline;
import org.osmdroid.views.overlay.mylocation.GpsMyLocationProvider;
import org.osmdroid.views.overlay.mylocation.MyLocationNewOverlay;

public class TestActivity extends AppCompatActivity {

  private MapView map;
  private MyLocationNewOverlay mLocationOverlay;
  private OptimizedRouteResponse response;
  private int[] colors = new int[]{R.color.green,
      R.color.red,
      R.color.violet,
      R.color.blue,
      R.color.orange};
  private ItemizedIconOverlay<OverlayItem> myLocationOverlay;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    Context ctx = getApplicationContext();
    Configuration.getInstance().load(ctx, PreferenceManager.getDefaultSharedPreferences(ctx));
    //setting this before the layout is inflated is a good idea
    //it 'should' ensure that the map has a writable location for the map cache, even without permissions
    //if no tiles are displayed, you can try overriding the cache path using Configuration.getInstance().setCachePath
    //see also StorageUtils
    //note, the load method also sets the HTTP User Agent to your application's package name, abusing osm's tile servers will get you banned based on this string

    setContentView(R.layout.activity_test);

    map = findViewById(R.id.map);
    map.setTileSource(TileSourceFactory.MAPNIK);
    map.getZoomController().setVisibility(Visibility.ALWAYS);
    map.setMultiTouchControls(true);

    IMapController mapController = map.getController();
    mapController.setZoom(9.5);
    GeoPoint startPoint = new GeoPoint(35.6892, 51.3890);
    mapController.setCenter(startPoint);

    this.mLocationOverlay = new MyLocationNewOverlay(new GpsMyLocationProvider(this), map);
    this.mLocationOverlay.enableMyLocation();
    map.getOverlays().add(this.mLocationOverlay);

    Marker startMarker = new Marker(map);
    startMarker.setPosition(startPoint);
    startMarker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM);
    map.getOverlays().add(startMarker);

    new RestServiceImpl().testValOR(this);
  }

  @Override
  protected void onResume() {
    super.onResume();
    EventBus.getDefault().register(this);
    map.onResume();

  }

  @Override
  protected void onPause() {
    super.onPause();
    EventBus.getDefault().unregister(this);
    map.onPause();
  }

  @Subscribe
  public void getMessage(OptimizedRouteResponse response) {
    DialogUtil.dismissProgressDialog();
    Toast.makeText(this, "Total:" + response.getTrip().getLocations().size(), Toast.LENGTH_SHORT)
        .show();
    this.response = response;
    draw();

    ArrayList<Long> ids = new ArrayList<>();
    ArrayList<Customer> customers = new ArrayList<>();
//    Collections.sort(customers,);

  }

  private void draw() {
    List<Leg> legs = response.getTrip().getLegs();

    for (int i = 0; i < legs.size(); i++) {

      ArrayList<GeoPoint> legsList = PolylineEncoder
          .decode(legs.get(i).getShape(), 1, false);
      Polyline polyline = new Polyline();
      polyline.setPoints(legsList);
      polyline.setColor(ContextCompat.getColor(this, colors[3]));
      polyline.setWidth(5);

      map.getOverlayManager().add(polyline);
    }
    List<LocationResponse> geoPoints = response.getTrip().getLocations();
    for (int i = 0; i < geoPoints.size(); i++) {
      LocationResponse l = geoPoints.get(i);
      Marker startMarker = new Marker(map);
      startMarker.setPosition(new GeoPoint(l.getLat(), l.getLon()));
      startMarker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM);

      startMarker.setIcon(new BitmapDrawable(getResources(),setMarkerDrawable(i)));

      map.getOverlays().add(startMarker);
    }
//    IMapController mapController = map.getController();
//    mapController.setZoom(14.0);
//    mapController.setCenter(legsList.get(0));
    Toast.makeText(this, "Route complete", Toast.LENGTH_SHORT).show();
  }

  public Bitmap setMarkerDrawable(int number) {
    int background = R.drawable.ic_marker_blue_empty_24dp;

    /* DO SOMETHING TO THE ICON BACKGROUND HERE IF NECESSARY */
    /* (e.g. change its tint color if the number is over a certain threshold) */

    return drawTextToBitmap(background, NumberUtil.digitsToPersian(number));
  }

  public Bitmap drawTextToBitmap(int gResId, String gText) {
    Resources resources = getResources();
    float scale = resources.getDisplayMetrics().density;
    Bitmap bitmap = BitmapFactory.decodeResource(resources, gResId);
    android.graphics.Bitmap.Config bitmapConfig = bitmap.getConfig();

    if ( bitmapConfig == null ) {
      bitmapConfig = android.graphics.Bitmap.Config.ARGB_8888;
    }
    bitmap = bitmap.copy(bitmapConfig, true);
    Canvas canvas = new Canvas(bitmap);

    Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
    /* SET FONT COLOR (e.g. WHITE -> rgb(255,255,255)) */
    paint.setColor(Color.rgb(255, 255, 255));
    /* SET FONT SIZE (e.g. 15) */
    paint.setTextSize((int) (10 * scale));
    /* SET SHADOW WIDTH, POSITION AND COLOR (e.g. BLACK) */
    paint.setShadowLayer(1f, 0f, 1f, Color.BLACK);

    Rect bounds = new Rect();
    paint.getTextBounds(gText, 0, gText.length(), bounds);
    int x = (bitmap.getWidth() - bounds.width())/2;
    int y = (bitmap.getHeight() + bounds.height())/2;
    canvas.drawText(gText, x, y, paint);

    return bitmap;
  }
}
