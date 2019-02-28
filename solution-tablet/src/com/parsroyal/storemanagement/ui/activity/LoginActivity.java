package com.parsroyal.storemanagement.ui.activity;

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
import br.com.simplepass.loading_button_lib.customViews.CircularProgressButton;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import com.parsroyal.storemanagement.R;
import com.parsroyal.storemanagement.constants.SaleType;
import com.parsroyal.storemanagement.data.event.ErrorEvent;
import com.parsroyal.storemanagement.data.response.CompanyInfoResponse;
import com.parsroyal.storemanagement.data.response.SettingResponse;
import com.parsroyal.storemanagement.service.SettingService;
import com.parsroyal.storemanagement.service.impl.RestAuthenticateServiceImpl;
import com.parsroyal.storemanagement.service.impl.SettingServiceImpl;
import com.parsroyal.storemanagement.util.DialogUtil;
import com.parsroyal.storemanagement.util.Empty;
import com.parsroyal.storemanagement.util.MultiScreenUtility;
import com.parsroyal.storemanagement.util.ToastUtil;
import com.parsroyal.storemanagement.util.constants.ApplicationKeys;
import io.github.inflationx.viewpump.ViewPumpContextWrapper;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

public class LoginActivity extends AppCompatActivity implements TextWatcher {

  @BindView(R.id.company_code_edt)
  EditText companyCodeEdt;
  @BindView(R.id.user_name_edt)
  EditText userNameEdt;
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

  private SettingService settingService;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
//    if (MultiScreenUtility.isTablet(this)) {
//      setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
//    } else {
      setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
//    }
    setContentView(R.layout.activity_login);
    ButterKnife.bind(this);

    onEditTextFocus();

    settingService = new SettingServiceImpl();

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

  @OnClick({R.id.log_in_btn})
  public void onClick(View view) {
    switch (view.getId()) {

      case R.id.log_in_btn:
        if (validate()) {
          doLogin();
        }
    }
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
      settingService.saveSetting(companyInfo);
      RestAuthenticateServiceImpl.getCompanySetting(this, companyInfo.getBackendUri(),
          userNameEdt.getText().toString(), passwordEdt.getText().toString(),
          SaleType.STORE_MANAGEMENT);
    } else if (response instanceof SettingResponse) {

      logInBtn.doneLoadingAnimation(ContextCompat.getColor(this, R.color.log_in_enter_bg),
          BitmapFactory.decodeResource(getResources(), R.drawable.ic_check_white_18_dp));

      settingService.saveSetting((SettingResponse) response);

      settingService.saveSetting(ApplicationKeys.SETTING_SALE_TYPE,
          String.valueOf(SaleType.STORE_MANAGEMENT.getValue()));
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

      ErrorEvent errorEvent = (ErrorEvent) response;
      int message = R.string.error_connecting_server;
      switch (errorEvent.getStatusCode()) {
        case NETWORK_ERROR:
          message = R.string.error_no_network;
          break;
        case INVALID_DATA:
          message = R.string.error_invalid_login_info;
          break;
        case FORBIDDEN:
          message = errorEvent.getStatusCode().getMessage();
          break;
      }
      ToastUtil.toastError(this, message);
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
