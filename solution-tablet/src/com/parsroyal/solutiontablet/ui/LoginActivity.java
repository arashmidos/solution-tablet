package com.parsroyal.solutiontablet.ui;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
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
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

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
  @BindView(R.id.merchandiser_icon)
  ImageView merchandiserIcon;
  @BindView(R.id.merchandiser_tv)
  TextView merchandiserTv;
  @BindView(R.id.merchandiser_bottom_line)
  View merchandiserBottomLine;
  @BindView(R.id.merchandiser_lay)
  RelativeLayout merchandiserLay;
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

  private SaleType selectedRole;
  private SettingService settingService;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    if (MultiScreenUtility.isTablet(this)) {
      setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
//      setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_NOSENSOR);

    } else {
      setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
//      setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_NOSENSOR);
    }
    setContentView(R.layout.activity_login);
    ButterKnife.bind(this);

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

  @OnClick({R.id.distributor_lay, R.id.merchandiser_lay, R.id.sales_man_lay, R.id.log_in_btn})
  public void onClick(View view) {
    switch (view.getId()) {
      case R.id.distributor_lay:
        onDistributorTapped();
        break;
      case R.id.merchandiser_lay:
        onMerchandiserTapped();
        break;
      case R.id.sales_man_lay:
        onSalesManTapped();
        break;
      case R.id.log_in_btn:
        if (validate()) {
          doLogin();
        }
        break;
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

  private void onDistributorTapped() {
    selectedRole = SaleType.DISTRIBUTOR;
    onRoleSelected(distributorLay, distributorIcon, distributorTv, distributorBottomLine);
    onRoleDeSelected(salesManLay, salesManIcon, salesManTv, salesManBottomLine);
    onRoleDeSelected(merchandiserLay, merchandiserIcon, merchandiserTv, merchandiserBottomLine);
  }

  private void onSalesManTapped() {
    selectedRole = SaleType.COLD;
    onRoleDeSelected(distributorLay, distributorIcon, distributorTv, distributorBottomLine);
    onRoleSelected(salesManLay, salesManIcon, salesManTv, salesManBottomLine);
    onRoleDeSelected(merchandiserLay, merchandiserIcon, merchandiserTv, merchandiserBottomLine);
  }

  private void onMerchandiserTapped() {
    selectedRole = SaleType.HOT;
    onRoleDeSelected(distributorLay, distributorIcon, distributorTv, distributorBottomLine);
    onRoleDeSelected(salesManLay, salesManIcon, salesManTv, salesManBottomLine);
    onRoleSelected(merchandiserLay, merchandiserIcon, merchandiserTv, merchandiserBottomLine);
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

  private void onRoleSelected(RelativeLayout selectedLay, ImageView selectedIcon,
      TextView selectedTextView, View selectedView) {
    selectedLay.setBackgroundResource(R.drawable.role_selected);
    selectedIcon.setColorFilter(ContextCompat.getColor(this, R.color.primary));
    selectedTextView.setTextColor(ContextCompat.getColor(this, R.color.primary));
    selectedView.setVisibility(View.VISIBLE);
  }

  private void onRoleDeSelected(RelativeLayout selectedLay, ImageView selectedIcon,
      TextView selectedTextView, View selectedView) {
    selectedLay.setBackgroundResource(R.drawable.role_default);
    selectedIcon.setColorFilter(ContextCompat.getColor(this, R.color.login_gray));
    selectedTextView.setTextColor(ContextCompat.getColor(this, R.color.login_gray));
    selectedView.setVisibility(View.GONE);
  }

  private void doLogin() {
    DialogUtil.showProgressDialog(this, getString(R.string.authenticate_user));
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
    DialogUtil.dismissProgressDialog();
    if (response instanceof CompanyInfoResponse) {

      CompanyInfoResponse companyInfoResponse = (CompanyInfoResponse) response;
      settingService
          .saveSetting(ApplicationKeys.USER_COMPANY_KEY, companyInfoResponse.getCompanyKey());
      settingService
          .saveSetting(ApplicationKeys.USER_COMPANY_NAME, companyInfoResponse.getCompanyName());
      settingService.saveSetting(ApplicationKeys.BACKEND_URI, companyInfoResponse.getBackendUri());
      DialogUtil.showProgressDialog(this, getString(R.string.updating_user_info));
      RestAuthenticateServiceImpl
          .getCompanySetting(this, companyInfoResponse.getBackendUri(),
              userNameEdt.getText().toString(), passwordEdt.getText().toString(), selectedRole);
    } else if (response instanceof SettingResponse) {
      String token = ((SettingResponse) response).getToken();

      UserInfoResponse userInfo = NetworkUtil.extractUserInfo(token);

      settingService.saveSetting((SettingResponse) response);
      settingService.saveUserInfo(userInfo);
      settingService.saveSetting(ApplicationKeys.SETTING_SALE_TYPE,
          String.valueOf(selectedRole.getValue()));
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
      if (((ErrorEvent) response).getStatusCode() == StatusCodes.NETWORK_ERROR) {
        ToastUtil.toastError(this, R.string.error_no_network);
      } else {
        ToastUtil.toastError(this, R.string.error_invalid_login_info);
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
    super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
  }

}
