package com.parsroyal.solutiontablet.ui.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import com.parsroyal.solutiontablet.BuildConfig;
import com.parsroyal.solutiontablet.R;
import com.parsroyal.solutiontablet.service.impl.SettingServiceImpl;
import com.parsroyal.solutiontablet.ui.MainActivity;
import com.parsroyal.solutiontablet.util.Empty;
import com.parsroyal.solutiontablet.util.constants.ApplicationKeys;

public class NavigationDrawerFragment extends Fragment {

  @BindView(R.id.body)
  LinearLayout body;
  @BindView(R.id.body_log_out)
  RelativeLayout bodyLogOut;
  @BindView(R.id.footer)
  LinearLayout footer;
  @BindView(R.id.footer_log_out)
  TextView footerLogOut;
  @BindView(R.id.drop_img)
  ImageView dropImg;
  @BindView(R.id.user_name_tv)
  TextView userNameTv;

  private boolean isLogOutMode = false;
  private MainActivity mainActivity;
  private SettingServiceImpl settingService;

  public NavigationDrawerFragment() {
    // Required empty public constructor
  }


  public static NavigationDrawerFragment newInstance() {
    return new NavigationDrawerFragment();
  }

  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container,
      Bundle savedInstanceState) {
    // Inflate the layout for this fragment
    View view = inflater.inflate(R.layout.fragment_navigation_drawer, container, false);
    ButterKnife.bind(this, view);
    mainActivity = (MainActivity) getActivity();
    settingService = new SettingServiceImpl(mainActivity);

    footerLogOut.setText(String.format(getString(R.string.name_version), BuildConfig.VERSION_NAME));
    String fullName = settingService.getSettingValue(ApplicationKeys.USER_FULL_NAME);
    userNameTv.setText(Empty.isNotEmpty(fullName) ? fullName : "");
    return view;
  }

  @OnClick({R.id.user_name_tv, R.id.features_list_lay, R.id.today_paths_lay, R.id.reports_lay,
      R.id.customers_lay, R.id.map_lay, R.id.setting_lay, R.id.about_us, R.id.body_log_out,
      R.id.get_data_lay, R.id.send_data_lay})
  public void onClick(View view) {
    switch (view.getId()) {
      case R.id.user_name_tv:
        changeMode();
        break;
      case R.id.features_list_lay:
        mainActivity.changeFragment(MainActivity.FEATURE_FRAGMENT_ID, true);
        break;
      case R.id.customers_lay:
        Toast.makeText(getActivity(), "customers", Toast.LENGTH_SHORT).show();//TODO: update
        break;
      case R.id.today_paths_lay:
        Toast.makeText(getActivity(), "path", Toast.LENGTH_SHORT).show();//TODO: update
        break;
      case R.id.reports_lay:
        Toast.makeText(getActivity(), "report", Toast.LENGTH_SHORT).show();//TODO: update
        break;
      case R.id.map_lay:
        Toast.makeText(getActivity(), "map", Toast.LENGTH_SHORT).show(); //TODO: update
        break;
      case R.id.setting_lay:
        mainActivity.changeFragment(MainActivity.SETTING_FRAGMENT_ID, true);
        break;
      case R.id.about_us:
        mainActivity.changeFragment(MainActivity.ABOUT_US_FRAGMENT_ID, true);
        break;
      case R.id.body_log_out:
        Toast.makeText(getActivity(), "log out", Toast.LENGTH_SHORT)
            .show();//TODO: update after login
        break;
      case R.id.get_data_lay:
        mainActivity.changeFragment(MainActivity.DATA_TRANSFER_FRAGMENT_ID, true);
        break;
      case R.id.send_data_lay:
        mainActivity.changeFragment(MainActivity.DATA_TRANSFER_FRAGMENT_ID, true);
        break;
    }
    mainActivity.closeDrawer();
  }

  private void changeMode() {
    isLogOutMode = !isLogOutMode;
    if (isLogOutMode) {
      dropImg.setImageResource(R.drawable.ic_arrow_drop_up);
      dropImg.setColorFilter(ContextCompat.getColor(getActivity(), R.color.login_gray));
      body.setVisibility(View.GONE);
      bodyLogOut.setVisibility(View.VISIBLE);
      footer.setVisibility(View.GONE);
      footerLogOut.setVisibility(View.VISIBLE);
    } else {
      dropImg.setImageResource(R.drawable.ic_arrow_drop_down);
      dropImg.setColorFilter(ContextCompat.getColor(getActivity(), R.color.login_gray));
      body.setVisibility(View.VISIBLE);
      bodyLogOut.setVisibility(View.GONE);
      footer.setVisibility(View.VISIBLE);
      footerLogOut.setVisibility(View.GONE);
    }
  }

  @Override
  public void onDestroyView() {
    super.onDestroyView();
  }
}