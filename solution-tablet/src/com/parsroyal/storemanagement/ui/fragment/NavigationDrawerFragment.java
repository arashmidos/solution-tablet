package com.parsroyal.storemanagement.ui.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentTransaction;
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
import com.parsroyal.storemanagement.BuildConfig;
import com.parsroyal.storemanagement.R;
import com.parsroyal.storemanagement.SolutionTabletApplication;
import com.parsroyal.storemanagement.constants.Constants;
import com.parsroyal.storemanagement.constants.SaleType;
import com.parsroyal.storemanagement.service.impl.BaseInfoServiceImpl;
import com.parsroyal.storemanagement.service.impl.DataTransferServiceImpl;
import com.parsroyal.storemanagement.service.impl.SettingServiceImpl;
import com.parsroyal.storemanagement.ui.activity.LoginActivity;
import com.parsroyal.storemanagement.ui.activity.MainActivity;
import com.parsroyal.storemanagement.ui.fragment.dialogFragment.DataTransferDialogFragment;
import com.parsroyal.storemanagement.util.DialogUtil;
import com.parsroyal.storemanagement.util.Empty;
import com.parsroyal.storemanagement.util.ToastUtil;
import com.parsroyal.storemanagement.util.constants.ApplicationKeys;

public class NavigationDrawerFragment extends BaseFragment {

  @BindView(R.id.body)
  LinearLayout body;
  @BindView(R.id.body_log_out)
  RelativeLayout bodyLogOut;
  @BindView(R.id.body_log_out_force)
  RelativeLayout bodyLogOutForce;
  @BindView(R.id.footer)
  LinearLayout footer;
  @BindView(R.id.footer_log_out)
  TextView footerLogOut;
  @BindView(R.id.drop_img)
  ImageView dropImg;
  @BindView(R.id.user_name_tv)
  TextView userNameTv;
  @BindView(R.id.user_role_tv)
  TextView userRole;
  @BindView(R.id.today_lines_tv)
  TextView todayLinesTv;
  @BindView(R.id.refresh_icon)
  ImageView refreshIv;

  private boolean isLogOutMode = false;
  private MainActivity mainActivity;
  private SettingServiceImpl settingService;
  private BaseInfoServiceImpl baseInfoService;
  private boolean hasData = true;
  private DataTransferServiceImpl dataTransferService;
  private int clickCount = 0;

  public NavigationDrawerFragment() {
    // Required empty public constructor
  }

  public static NavigationDrawerFragment newInstance() {
    return new NavigationDrawerFragment();
  }

  @Override
  public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
      Bundle savedInstanceState) {
    // Inflate the layout for this fragment
    View view = inflater.inflate(R.layout.fragment_navigation_drawer, container, false);
    ButterKnife.bind(this, view);
    mainActivity = (MainActivity) getActivity();
    settingService = new SettingServiceImpl();
    dataTransferService = new DataTransferServiceImpl(mainActivity);

    baseInfoService = new BaseInfoServiceImpl(mainActivity);

    setData();
    return view;
  }

  private void setData() {
    footerLogOut.setText(String.format(getString(R.string.name_version), BuildConfig.VERSION_NAME));
    String fullName = settingService.getSettingValue(ApplicationKeys.USER_FULL_NAME);
    userNameTv.setText(Empty.isNotEmpty(fullName) ? fullName : "");
    String role = settingService.getSettingValue(ApplicationKeys.SETTING_SALE_TYPE);
    if (Empty.isNotEmpty(role)) {
      userRole.setText(SaleType.getByValue(Long.parseLong(role)).getRole());
    }
    if (ApplicationKeys.SALE_DISTRIBUTER.equals(role)) {
      todayLinesTv.setText(R.string.today_request_line);
      refreshIv.setVisibility(View.GONE);
    }
  }

  @OnClick({R.id.features_list_lay, R.id.today_paths_lay, R.id.reports_lay,
      R.id.customers_lay, R.id.about_us, R.id.body_log_out, R.id.body_log_out_force,
      R.id.get_data_lay, R.id.send_data_lay, R.id.goods_lay, R.id.questionnaire_lay, R.id.header,
      R.id.refresh_icon, R.id.setting_lay, R.id.footer_log_out})
  public void onClick(View view) {
    boolean closeDrawer = true;
    hasData = baseInfoService.getAllProvinces().size() != 0;

    switch (view.getId()) {

      case R.id.header:
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
        mainActivity.changeFragment(MainActivity.VISITLINE_FRAGMENT_ID, true);
        break;
      case R.id.customers_lay:
        if (!hasData) {
          ToastUtil.toastError(mainActivity, R.string.error_message_no_data);
          break;
        }
        mainActivity.changeFragment(MainActivity.CUSTOMER_FRAGMENT, true);
        break;
      case R.id.goods_lay:
 /*       if (!hasData) {
          ToastUtil.toastError(mainActivity, R.string.error_message_no_data);
          break;
        }
        Bundle args = new Bundle();
        args.putBoolean(Constants.READ_ONLY, true);
        mainActivity.changeFragment(MainActivity.GOODS_LIST_FRAGMENT_ID, args, true);*/
        break;
      case R.id.reports_lay:
        /*if (!hasData) {
          ToastUtil.toastError(mainActivity, R.string.error_message_no_data);
          break;
        }
        mainActivity.changeFragment(MainActivity.REPORT_FRAGMENT, true);*/
        break;
      case R.id.questionnaire_lay:
        if (!hasData) {
          ToastUtil.toastError(mainActivity, R.string.error_message_no_data);
          break;
        }
        mainActivity.changeFragment(MainActivity.ANONYMOUS_QUESTIONNAIRE_FRAGMENT_ID, true);
        break;
      case R.id.refresh_icon:
        refreshData();
        break;
      case R.id.about_us:
        mainActivity.changeFragment(MainActivity.ABOUT_US_FRAGMENT_ID, true);
        break;
      case R.id.setting_lay:
//        mainActivity.changeFragment(MainActivity.SETTING_FRAGMENT, true);
        break;
      case R.id.body_log_out:
        doLogout(false);
        break;
      case R.id.body_log_out_force:
        doLogout(true);
        break;
      case R.id.get_data_lay:
        checkForUnsentData();
        break;
      case R.id.send_data_lay:
        openDataTransferDialog(Constants.DATA_TRANSFER_SEND_DATA);
        break;
      case R.id.footer_log_out:
        closeDrawer = false;
        clickCount++;
        if (clickCount >= 7) {
          clickCount = 0;
          openAdminLogin();
          mainActivity.closeDrawer();
        }
    }
    if (closeDrawer) {
      mainActivity.closeDrawer();
    }
  }

  private void openAdminLogin() {
    DialogUtil.showLoginDialog(mainActivity,
        (username, password) -> {
          if (Constants.debugUsername.equals(username) && Constants.debugPassword
              .equals(password)) {
            mainActivity.changeFragment(MainActivity.ADMIN_FRAGMENT_ID, true);
          } else {
            Toast.makeText(mainActivity, "Login Failed", Toast.LENGTH_SHORT).show();
          }
        });
  }

  private void doLogout(boolean forceLogout) {
    if (!forceLogout && dataTransferService.hasUnsentData()) {
      DialogUtil.showCustomDialog(mainActivity, getString(R.string.warning),
          getString(R.string.message_have_unsent_data),
          getString(R.string.yes),
          (dialog, which) -> openDataTransferDialog(Constants.DATA_TRANSFER_SEND_DATA),
          getString(R.string.no), (dialog, which) -> dialog.dismiss(), Constants.ICON_WARNING);
    } else {
      settingService.clearAllSettings();
      dataTransferService.clearData();
      SolutionTabletApplication.getInstance().clearAuthorities();
      startActivity(new Intent(mainActivity, LoginActivity.class));
//      Intent intent = new Intent(mainActivity, LocationUpdatesService.class);
//      mainActivity.stopService(intent);
      mainActivity.finish();
    }
  }

  private void refreshData() {
    if (dataTransferService.hasUnsentData()) {
      DialogUtil.showCustomDialog(mainActivity, getString(R.string.warning),
          "شما اطلاعات ارسال نشده دارید که قبل از بروز رسانی موجودی می بایست ارسال شوند. آیا میخواهید آنها را ارسال کنید؟",
          getString(R.string.yes),
          (dialog, which) -> openDataTransferDialog(Constants.DATA_TRANSFER_SEND_DATA),
          getString(R.string.no),
          (dialog, which) -> dialog.dismiss(), Constants.ICON_WARNING);
    } else {
      openDataTransferDialog(Constants.DATA_TRANSFER_GET_GOODS);
    }
  }

  private void checkForUnsentData() {

    if (dataTransferService.hasUnsentData()) {
      DialogUtil.showCustomDialog(mainActivity, getString(R.string.warning),
          "شما اطلاعات ارسال نشده دارید که قبل از دریافت اطلاعات جدید می بایست ارسال شوند. آیا میخواهید آنها را ارسال کنید؟",
          getString(R.string.yes),
          (dialog, which) -> openDataTransferDialog(Constants.DATA_TRANSFER_SEND_DATA),
          getString(R.string.no),
          (dialog, which) -> {
            dialog.dismiss();/* openDataTransferDialog(Constants.DATA_TRANSFER_GET)*/
          },
          Constants.ICON_WARNING);
    } else {
      openDataTransferDialog(Constants.DATA_TRANSFER_GET);
    }
  }

  private void openDataTransferDialog(String action) {
    FragmentTransaction ft = mainActivity.getSupportFragmentManager().beginTransaction();
    Bundle args = new Bundle();
    args.putString(Constants.DATA_TRANSFER_ACTION, action);
    DataTransferDialogFragment dialogFragment = DataTransferDialogFragment.newInstance(args);

    dialogFragment.show(ft, "data_transfer");
  }

  private void changeMode() {
    isLogOutMode = !isLogOutMode;
    if (isLogOutMode) {
      dropImg.setImageResource(R.drawable.ic_arrow_drop_up);
      dropImg.setColorFilter(ContextCompat.getColor(mainActivity, R.color.login_gray));
      body.setVisibility(View.GONE);
      bodyLogOut.setVisibility(View.VISIBLE);
      if (BuildConfig.DEBUG) {
        bodyLogOutForce.setVisibility(View.VISIBLE);
      }
      footer.setVisibility(View.GONE);
      footerLogOut.setVisibility(View.VISIBLE);
    } else {
      dropImg.setImageResource(R.drawable.ic_arrow_drop_down);
      dropImg.setColorFilter(ContextCompat.getColor(mainActivity, R.color.login_gray));
      body.setVisibility(View.VISIBLE);
      bodyLogOut.setVisibility(View.GONE);
      bodyLogOutForce.setVisibility(View.GONE);
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

  public interface OnLoginListener {

    void onLogin(String username, String password);
  }
}
