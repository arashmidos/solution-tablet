package com.parsroyal.solutiontablet.ui.activity;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.design.widget.TextInputLayout;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import br.com.simplepass.loading_button_lib.customViews.CircularProgressButton;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import com.parsroyal.solutiontablet.R;
import com.parsroyal.solutiontablet.constants.SaleType;
import com.parsroyal.solutiontablet.constants.StatusCodes;
import com.parsroyal.solutiontablet.data.event.ErrorEvent;
import com.parsroyal.solutiontablet.data.response.CompanyInfoResponse;
import com.parsroyal.solutiontablet.data.response.SettingResponse;
import com.parsroyal.solutiontablet.data.response.UserInfoResponse;
import com.parsroyal.solutiontablet.service.SettingService;
import com.parsroyal.solutiontablet.service.impl.RestAuthenticateServiceImpl;
import com.parsroyal.solutiontablet.service.impl.SettingServiceImpl;
import com.parsroyal.solutiontablet.util.DialogUtil;
import com.parsroyal.solutiontablet.util.Empty;
import com.parsroyal.solutiontablet.util.MultiScreenUtility;
import com.parsroyal.solutiontablet.util.NetworkUtil;
import com.parsroyal.solutiontablet.util.ToastUtil;
import com.parsroyal.solutiontablet.util.constants.ApplicationKeys;
import io.github.inflationx.viewpump.ViewPumpContextWrapper;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

public class LoginActivity extends AppCompatActivity implements TextWatcher {

  @BindView(R.id.company_code_edt)
  EditText companyCodeEdt;
  @BindView(R.id.user_name_edt)
  EditText userNameEdt;
  @BindView(R.id.distributor_icon)
  ImageView distributorIcon;
  @BindView(R.id.distributor_tv)
  TextView distributorTv;
  @BindView(R.id.distributor_bottom_line)
  View distributorBottomLine;
  @BindView(R.id.distributor_lay)
  RelativeLayout distributorLay;
  @BindView(R.id.hot_icon)
  ImageView hotIcon;
  @BindView(R.id.hot_tv)
  TextView hotTv;
  @BindView(R.id.hot_bottom_line)
  View hotBottomLine;
  @BindView(R.id.hot_lay)
  RelativeLayout hotLay;
  @BindView(R.id.sales_man_icon)
  ImageView salesManIcon;
  @BindView(R.id.sales_man_tv)
  TextView salesManTv;
  @BindView(R.id.sales_man_bottom_line)
  View salesManBottomLine;
  @BindView(R.id.sales_man_lay)
  RelativeLayout salesManLay;
  @BindView(R.id.company_code_icon)
  ImageView companyCodeIcon;
  @BindView(R.id.user_name_icon)
  ImageView userNameIcon;
  @BindView(R.id.company_code_lay)
  TextInputLayout companyCodeLay;
  @BindView(R.id.user_name_lay)
  TextInputLayout userNameLay;
  @BindView(R.id.password_edt)
  EditText passwordEdt;
  @BindView(R.id.password_lay)
  TextInputLayout passwordLay;
  @BindView(R.id.log_in_btn)
  CircularProgressButton logInBtn;
  @BindView(R.id.agent_icon)
  ImageView agentIcon;
  @BindView(R.id.agent_tv)
  TextView agentTv;
  @BindView(R.id.agent_bottom_line)
  View agentBottomLine;
  @BindView(R.id.agent_lay)
  RelativeLayout agentLay;
  @BindView(R.id.merchandiser_icon)
  ImageView merchandiserIcon;
  @BindView(R.id.merchandiser_tv)
  TextView merchandiserTv;
  @BindView(R.id.merchandiser_bottom_line)
  View merchandiserBottomLine;
  @BindView(R.id.merchandiser_lay)
  RelativeLayout merchandiserLay;
  @BindView(R.id.collector_icon)
  ImageView collectorIcon;
  @BindView(R.id.collector_tv)
  TextView collectorTv;
  @BindView(R.id.collector_bottom_line)
  View collectorBottomLine;
  @BindView(R.id.collector_lay)
  RelativeLayout collectorLay;

  private SaleType selectedRole = SaleType.COLD;
  private SettingService settingService;
  private RelativeLayout[] roleLayouts;
  private ImageView[] roleIcons;
  private TextView[] roleTitles;
  private View[] roleLines;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    if (MultiScreenUtility.isTablet(this)) {
      setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
    } else {
      setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
    }
    setContentView(R.layout.activity_login);
    ButterKnife.bind(this);

    roleLayouts = new RelativeLayout[]{distributorLay, hotLay, salesManLay, agentLay,
        merchandiserLay, collectorLay};
    roleIcons = new ImageView[]{distributorIcon, hotIcon, salesManIcon, agentIcon,
        merchandiserIcon, collectorIcon};
    roleTitles = new TextView[]{distributorTv, hotTv, salesManTv, agentTv, merchandiserTv,
        collectorTv};
    roleLines = new View[]{distributorBottomLine, hotBottomLine, salesManBottomLine,
        agentBottomLine, merchandiserBottomLine, collectorBottomLine};
    onEditTextFocus();
    onSalesManTapped();

    settingService = new SettingServiceImpl(this);

    companyCodeEdt.addTextChangedListener(this);
    userNameEdt.addTextChangedListener(this);
    passwordEdt.addTextChangedListener(this);
  }

  @Override
  public void onBackPressed() {
    DialogUtil.showConfirmDialog(this, getString(R.string.message_exit),
        getString(R.string.message_do_you_want_to_exit),
        (dialog, which) ->
        {
          dialog.dismiss();
          finish();
        });
  }

  @OnClick({R.id.distributor_lay, R.id.hot_lay, R.id.sales_man_lay, R.id.log_in_btn, R.id.agent_lay,
      R.id.merchandiser_lay, R.id.collector_lay})
  public void onClick(View view) {
    deselectRole(selectedRole.getOrder());
    switch (view.getId()) {
      case R.id.distributor_lay:
        selectedRole = SaleType.DISTRIBUTOR;
        break;
      case R.id.hot_lay:
        selectedRole = SaleType.HOT;
        break;
      case R.id.sales_man_lay:
        selectedRole = SaleType.COLD;
        break;
      case R.id.agent_lay:
        selectedRole = SaleType.AGENT;
        break;
      case R.id.merchandiser_lay:
        selectedRole = SaleType.MERCHANDISER;
        break;
      case R.id.collector_lay:
        selectedRole = SaleType.COLLECTOR;
        break;
      case R.id.log_in_btn:
        if (validate()) {
          doLogin();
        }
    }
    selectRole(selectedRole.getOrder());
  }

  private boolean validate() {

    if (Empty.isEmpty(companyCodeEdt.getText().toString())) {
      companyCodeLay.setError(getString(R.string.error_company_code_is_required));
      companyCodeEdt.requestFocus();
      return false;
    }
    if (Empty.isEmpty(userNameEdt.getText().toString())) {
      userNameLay.setError(getString(R.string.error_username_is_required));
      userNameEdt.requestFocus();
      return false;
    }
    if (Empty.isEmpty(passwordEdt.getText().toString())) {
      passwordLay.setError(getString(R.string.error_password_is_required));
      passwordEdt.requestFocus();
      return false;
    }

    return true;
  }

  private void onSalesManTapped() {
    selectedRole = SaleType.COLD;
    selectRole(selectedRole.getOrder());
  }

  private void onEditTextFocus() {
    userNameEdt.setOnFocusChangeListener((v, hasFocus) -> {
      if (hasFocus) {
        userNameIcon.setColorFilter(ContextCompat.getColor(this, R.color.primary));
      } else {
        userNameIcon.setColorFilter(ContextCompat.getColor(this, R.color.login_gray));
      }
    });
    companyCodeEdt.setOnFocusChangeListener((v, hasFocus) -> {
      if (hasFocus) {
        companyCodeIcon.setColorFilter(ContextCompat.getColor(this, R.color.primary));
      } else {
        companyCodeIcon.setColorFilter(ContextCompat.getColor(this, R.color.login_gray));
      }
    });
  }

  private void selectRole(int order) {
    roleLayouts[order].setBackgroundResource(R.drawable.role_selected);
    roleIcons[order].setColorFilter(ContextCompat.getColor(this, R.color.primary));
    roleTitles[order].setTextColor(ContextCompat.getColor(this, R.color.primary));
    roleLines[order].setVisibility(View.VISIBLE);
  }

  private void deselectRole(int order) {
    roleLayouts[order].setBackgroundResource(R.drawable.role_default);
    roleIcons[order].setColorFilter(ContextCompat.getColor(this, R.color.login_gray));
    roleTitles[order].setTextColor(ContextCompat.getColor(this, R.color.login_gray));
    roleLines[order].setVisibility(View.GONE);
  }

  private void doLogin() {
    logInBtn.startAnimation();
    RestAuthenticateServiceImpl.getCompanyInfo(this, companyCodeEdt.getText().toString());
  }

  @Override
  public void beforeTextChanged(CharSequence s, int start, int count, int after) {
  }

  @Override
  public void onTextChanged(CharSequence s, int start, int before, int count) {
  }

  @Override
  public void afterTextChanged(Editable s) {
    companyCodeLay.setError(null);
    userNameLay.setError(null);
    passwordLay.setError(null);
  }

  @Subscribe
  public void getMessage(Object response) {

    if (response instanceof CompanyInfoResponse) {

      CompanyInfoResponse companyInfo = (CompanyInfoResponse) response;
      settingService.saveSetting(ApplicationKeys.USER_COMPANY_KEY, companyInfo.getCompanyKey());
      settingService.saveSetting(ApplicationKeys.USER_COMPANY_NAME, companyInfo.getCompanyName());
      settingService.saveSetting(ApplicationKeys.BACKEND_URI, companyInfo.getBackendUri());

      RestAuthenticateServiceImpl.getCompanySetting(this, companyInfo.getBackendUri(),
          userNameEdt.getText().toString(), passwordEdt.getText().toString(), selectedRole);
    } else if (response instanceof SettingResponse) {

      logInBtn.doneLoadingAnimation(ContextCompat.getColor(this, R.color.log_in_enter_bg),
          BitmapFactory.decodeResource(getResources(), R.drawable.ic_check_white_18_dp));

      String token = ((SettingResponse) response).getToken();

      UserInfoResponse userInfo = NetworkUtil.extractUserInfo(token);

      settingService.saveSetting((SettingResponse) response);
      settingService.saveUserInfo(userInfo);
      settingService
          .saveSetting(ApplicationKeys.SETTING_SALE_TYPE, String.valueOf(selectedRole.getValue()));
      settingService
          .saveSetting(ApplicationKeys.SETTING_USERNAME, userNameEdt.getText().toString());
      settingService
          .saveSetting(ApplicationKeys.SETTING_PASSWORD, passwordEdt.getText().toString());

      Intent intent;
      if (MultiScreenUtility.isTablet(this)) {
        intent = new Intent(this, TabletMainActivity.class);
      } else {
        intent = new Intent(this, MobileMainActivity.class);
      }
      startActivity(intent);
      finish();
    } else if (response instanceof ErrorEvent) {
      logInBtn.revertAnimation();
      if (((ErrorEvent) response).getStatusCode() == StatusCodes.NETWORK_ERROR) {
        ToastUtil.toastError(this, R.string.error_no_network);
      } else if (((ErrorEvent) response).getStatusCode() == StatusCodes.INVALID_DATA) {
        ToastUtil.toastError(this, R.string.error_invalid_login_info);
      } else {
        ToastUtil.toastError(this, R.string.error_connecting_server);

      }
    }
  }

  @Override
  public void onResume() {
    super.onResume();
    EventBus.getDefault().register(this);
  }

  @Override
  public void onPause() {
    super.onPause();
    EventBus.getDefault().unregister(this);
  }

  @Override
  protected void attachBaseContext(Context newBase) {
    super.attachBaseContext(ViewPumpContextWrapper.wrap(newBase));
  }

  @Override
  protected void onDestroy() {
    super.onDestroy();
    logInBtn.dispose();
  }
}