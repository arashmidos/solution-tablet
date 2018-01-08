package com.parsroyal.solutiontablet.ui.fragment.dialog;

import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import com.parsroyal.solutiontablet.R;
import com.parsroyal.solutiontablet.service.SettingService;
import com.parsroyal.solutiontablet.service.impl.SettingServiceImpl;
import com.parsroyal.solutiontablet.ui.OldMainActivity;
import com.parsroyal.solutiontablet.util.Analytics;
import com.parsroyal.solutiontablet.util.Empty;
import com.parsroyal.solutiontablet.util.constants.ApplicationKeys;

/**
 * Created by mahyar on 12/14/15.
 */
public class LoginDialogFragment extends DialogFragment implements TextWatcher {

  public static final String TAG = LoginDialogFragment.class.getSimpleName();
  @BindView(R.id.usernameTxt)
  EditText usernameTxt;
  @BindView(R.id.passwordTxt)
  EditText passwordTxt;
  @BindView(R.id.error_msg)
  TextView errorMsg;

  private SettingService settingService;

  public static LoginDialogFragment newInstance() {
    return new LoginDialogFragment();
  }

  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container,
      Bundle savedInstanceState) {

    settingService = new SettingServiceImpl(getActivity());

    View view = inflater.inflate(R.layout.dialog_setting_login, null);
    ButterKnife.bind(this, view);

    usernameTxt.addTextChangedListener(this);
    passwordTxt.addTextChangedListener(this);
    getDialog().setTitle(R.string.login_to_setting);

    return view;
  }

  @OnClick({R.id.loginBtn, R.id.cancelBtn})
  public void onViewClicked(View view) {
    switch (view.getId()) {
      case R.id.loginBtn:
        doLogin();
        break;
      case R.id.cancelBtn:
        LoginDialogFragment.this.dismiss();
        break;
    }
  }

  private void doLogin() {
    String usernameValue = usernameTxt.getText().toString();
    String passwordValue = passwordTxt.getText().toString();

    if (Empty.isEmpty(usernameValue)) {
      errorMsg.setText(R.string.error_username_is_empty);
      errorMsg.setVisibility(View.VISIBLE);
      usernameTxt.requestFocus();
      return;
    }

    if (Empty.isEmpty(passwordValue)) {
      errorMsg.setText(R.string.error_password_is_empty);
      errorMsg.setVisibility(View.VISIBLE);
      passwordTxt.requestFocus();
      return;
    }

    String username = settingService.getSettingValue(ApplicationKeys.SETTING_USERNAME);
    String password = settingService.getSettingValue(ApplicationKeys.SETTING_PASSWORD);

    if (username.equals(usernameValue) && password.equals(passwordValue)) {
      Analytics.logLogin(true);
      ((OldMainActivity) getActivity()).changeFragment(OldMainActivity.SETTING_FRAGMENT_ID, false);
      LoginDialogFragment.this.dismiss();
    } else {
      errorMsg.setText(R.string.error_invalid_login);
      errorMsg.setVisibility(View.VISIBLE);
      Analytics.logLogin(false);
    }
  }

  @Override
  public void beforeTextChanged(CharSequence s, int start, int count, int after) {

  }

  @Override
  public void onTextChanged(CharSequence s, int start, int before, int count) {

  }

  @Override
  public void afterTextChanged(Editable s) {
    errorMsg.setVisibility(View.GONE);
  }
}
