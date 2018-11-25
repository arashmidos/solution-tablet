package com.parsroyal.solutiontablet.ui.fragment.bottomsheet;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomSheetDialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import com.google.android.gms.maps.model.Marker;
import com.parsroyal.solutiontablet.R;
import com.parsroyal.solutiontablet.ui.fragment.UserTrackingFragment;
import com.parsroyal.solutiontablet.util.NumberUtil;
import com.parsroyal.solutiontablet.util.PreferenceHelper;
import com.parsroyal.solutiontablet.util.ToastUtil;
import java.util.Locale;

/**
 * Created by shkbhbb on 10/22/18.
 */

public class MapInfoWindowChooser extends BottomSheetDialogFragment {

  @BindView(R.id.main_lay)
  LinearLayout mainLay;
  @BindView(R.id.distance_tv)
  TextView distanceTv;

  private UserTrackingFragment userTrackingFragment;
  private Marker marker;
  private float distance;

  public static MapInfoWindowChooser newInstance(
      UserTrackingFragment userTrackingFragment, Marker marker, float distance) {
    MapInfoWindowChooser mapInfoWindowChooser = new MapInfoWindowChooser();
    mapInfoWindowChooser.userTrackingFragment = userTrackingFragment;
    mapInfoWindowChooser.marker = marker;
    mapInfoWindowChooser.distance = distance;
    return mapInfoWindowChooser;
  }

  @Nullable
  @Override
  public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
      @Nullable Bundle savedInstanceState) {
    View view = inflater.inflate(R.layout.bottom_sheet_map_chooser, container, false);
    ButterKnife.bind(this, view);
    distanceTv.setText(NumberUtil.digitsToPersian(String.format(
        getString(R.string.distance_to_customer), String.valueOf((int) distance))));

    return view;
  }


  @OnClick({R.id.enter_tv, R.id.navigation_tv})
  public void onViewClicked(View view) {
    switch (view.getId()) {
      case R.id.enter_tv:
        userTrackingFragment.doEnter();
        dismiss();
        break;
      case R.id.navigation_tv:
        if ("google".equals(PreferenceHelper.getDefaultNavigator())) {
          Intent i = new Intent(Intent.ACTION_VIEW, Uri.parse(
              "google.navigation:q=" + marker.getPosition().latitude + "," + marker
                  .getPosition().longitude));
          i.setPackage("com.google.android.apps.maps");
          if (i.resolveActivity(getActivity().getPackageManager()) != null) {
            startActivity(i);
          } else {
            ToastUtil.toastError(mainLay, getString(R.string.error_google_not_installed));
          }
        } else {
          try {
            Intent i = new Intent(Intent.ACTION_VIEW,
                Uri.parse(String.format(Locale.UK, "waze://?ll=%s,%s&navigate=yes",
                    marker.getPosition().latitude, marker.getPosition().longitude)));
            startActivity(i);
          } catch (ActivityNotFoundException ex) {
            // If Waze is not installed, open it in Google Play:
            Intent intent = new Intent(Intent.ACTION_VIEW,
                Uri.parse("market://details?id=com.waze"));
            startActivity(intent);
          }
        }
        dismiss();
        break;
    }
  }

}
