package com.parsroyal.solutiontablet.ui.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import com.parsroyal.solutiontablet.BuildConfig;
import com.parsroyal.solutiontablet.R;
import com.parsroyal.solutiontablet.constants.Constants;
import com.parsroyal.solutiontablet.service.impl.BaseInfoServiceImpl;
import com.parsroyal.solutiontablet.service.impl.SettingServiceImpl;
import com.parsroyal.solutiontablet.ui.LoginActivity;
import com.parsroyal.solutiontablet.ui.MainActivity;
import com.parsroyal.solutiontablet.util.Empty;
import com.parsroyal.solutiontablet.util.ToastUtil;
import com.parsroyal.solutiontablet.util.constants.ApplicationKeys;

public class NavigationDrawerFragment extends BaseFragment {

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
  private BaseInfoServiceImpl baseInfoService;
  private boolean hasData = true;

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
    baseInfoService = new BaseInfoServiceImpl(mainActivity);

    return view;
  }

  @OnClick({R.id.user_name_tv, R.id.features_list_lay, R.id.today_paths_lay, R.id.reports_lay,
      R.id.customers_lay, R.id.map_lay, R.id.setting_lay, R.id.about_us, R.id.body_log_out,
      R.id.get_data_lay, R.id.send_data_lay, R.id.goods_lay, R.id.questionnaire_lay, R.id.header,
      R.id.user_role_tv, R.id.user_img, R.id.drop_img})
  public void onClick(View view) {
    boolean closeDrawer = true;
    hasData = baseInfoService.getAllProvinces().size() != 0;

    switch (view.getId()) {

      case R.id.user_name_tv:
      case R.id.header:
      case R.id.user_role_tv:
      case R.id.user_img:
      case R.id.drop_img:
        changeMode();
        closeDrawer = false;
        break;
      case R.id.features_list_lay:
        mainActivity.changeFragment(MainActivity.FEATURE_FRAGMENT_ID, true);
        break;
      case R.id.today_paths_lay:
        if (!hasData) {
          ToastUtil.toastError(mainActivity, R.string.error_message_no_data);
          break;
        }
        mainActivity.changeFragment(MainActivity.PATH_FRAGMENT_ID, true);
        break;
      case R.id.customers_lay:
        if (!hasData) {
          ToastUtil.toastError(mainActivity, R.string.error_message_no_data);
          break;
        }
        mainActivity.changeFragment(MainActivity.CUSTOMER_FRAGMENT, true);
        break;
      case R.id.goods_lay:
        if (!hasData) {
          ToastUtil.toastError(mainActivity, R.string.error_message_no_data);
          break;
        }
        Bundle args = new Bundle();
        args.putBoolean(Constants.READ_ONLY, true);
        mainActivity.changeFragment(MainActivity.GOODS_LIST_FRAGMENT_ID, args, true);
        break;
      case R.id.reports_lay:
        if (!hasData) {
          ToastUtil.toastError(mainActivity, R.string.error_message_no_data);
          break;
        }
        mainActivity.changeFragment(MainActivity.REPORT_FRAGMENT, true);
        break;
      case R.id.questionnaire_lay:
        if (!hasData) {
          ToastUtil.toastError(mainActivity, R.string.error_message_no_data);
          break;
        }
        mainActivity.changeFragment(MainActivity.ANONYMOUS_QUESTIONNAIRE_FRAGMENT_ID, true);
        break;
      case R.id.map_lay:
        mainActivity.changeFragment(MainActivity.USER_TRACKING_FRAGMENT_ID, true);
        break;
      case R.id.setting_lay:
        ToastUtil.toastMessage(mainActivity, R.string.error_message_there_is_no_settings);
        break;
      case R.id.about_us:
        mainActivity.changeFragment(MainActivity.ABOUT_US_FRAGMENT_ID, true);
        break;
      case R.id.body_log_out:
        settingService.clearAllSettings();
        startActivity(new Intent(mainActivity, LoginActivity.class));
        mainActivity.finish();
        break;
      case R.id.get_data_lay:
        Bundle args3 = new Bundle();
        args3.putString(Constants.DATA_TRANSFER_ACTION, Constants.DATA_TRANSFER_GET);
        mainActivity.changeFragment(MainActivity.DATA_TRANSFER_FRAGMENT_ID, args3, true);
        break;
      case R.id.send_data_lay:
        Bundle args2 = new Bundle();
        args2.putString(Constants.DATA_TRANSFER_ACTION, Constants.DATA_TRANSFER_SEND_DATA);
        mainActivity.changeFragment(MainActivity.DATA_TRANSFER_FRAGMENT_ID, args2, true);
        break;
    }
    if (closeDrawer) {
      mainActivity.closeDrawer();
    }
  }

  private void changeMode() {
    isLogOutMode = !isLogOutMode;
    if (isLogOutMode) {
      dropImg.setImageResource(R.drawable.ic_arrow_drop_up);
      dropImg.setColorFilter(ContextCompat.getColor(mainActivity, R.color.login_gray));
      body.setVisibility(View.GONE);
      bodyLogOut.setVisibility(View.VISIBLE);
      footer.setVisibility(View.GONE);
      footerLogOut.setVisibility(View.VISIBLE);
    } else {
      dropImg.setImageResource(R.drawable.ic_arrow_drop_down);
      dropImg.setColorFilter(ContextCompat.getColor(mainActivity, R.color.login_gray));
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

  @Override
  public int getFragmentId() {
    return MainActivity.NAVIGATION_DRAWER_FRAGMENT;
  }
}
