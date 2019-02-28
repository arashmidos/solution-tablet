package com.parsroyal.storemanagement.service.impl;

import android.content.Context;
import com.google.android.gms.maps.model.LatLng;
import com.parsroyal.storemanagement.R;
import com.parsroyal.storemanagement.constants.StatusCodes;
import com.parsroyal.storemanagement.data.event.ErrorEvent;
import com.parsroyal.storemanagement.data.response.MapRoadResponse;
import com.parsroyal.storemanagement.service.MapService;
import com.parsroyal.storemanagement.service.ServiceGenerator;
import com.parsroyal.storemanagement.util.Empty;
import com.parsroyal.storemanagement.util.Logger;
import com.parsroyal.storemanagement.util.NetworkUtil;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import org.greenrobot.eventbus.EventBus;
import retrofit2.Call;
import retrofit2.Response;

/**
 * Created by Arash on 06/07/2017.
 */
public class MapServiceImpl {

  private static final String API_MAP_SNAP_TO_ROAD_URL = "http://173.212.199.107:50002/api/snapToRoads";


  public static List<LatLng> snapToRoads(final Context context, List<LatLng> route) {
    if (!NetworkUtil.isNetworkAvailable(context)) {
      EventBus.getDefault().post(new ErrorEvent(StatusCodes.NO_NETWORK));
    }

    MapService mapService = ServiceGenerator.createService(MapService.class);

    int start = 0;
    List<LatLng> snappedList = new ArrayList<>();
    try {
      do {
        String path = createString(route.subList(start, Math.min(start + 100, route.size())));
        Call<MapRoadResponse> call = mapService.snapToRoads(API_MAP_SNAP_TO_ROAD_URL, path,
            context.getString(R.string.google_map_snap_to_roads_key));

        Response<MapRoadResponse> response = call.execute();
        if (response.isSuccessful()) {
          MapRoadResponse mapRoadResponse = response.body();
          if (Empty.isNotEmpty(mapRoadResponse)) {
            snappedList.addAll(mapRoadResponse.getLatLngList());
          }
        }
        start += 100;
      } while (start < route.size());
    } catch (IOException ex) {
      ex.printStackTrace();
      Logger.sendError("Snap to Road", ex.getMessage());
      return null;
    }
    return snappedList;
  }

  private static String createString(List<LatLng> route) {
    StringBuilder path = new StringBuilder();
    for (int i = 0; i < route.size(); i++) {
      LatLng latLng = route.get(i);
      path.append(latLng.latitude).append(",").append(latLng.longitude);
      if (i < route.size() - 1) {
        path.append("|");
      }
    }
    return path.toString();
  }
}
