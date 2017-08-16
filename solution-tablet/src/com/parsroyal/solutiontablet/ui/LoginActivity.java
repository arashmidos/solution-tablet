package com.parsroyal.solutiontablet.ui;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import com.parsroyal.solutiontablet.R;
import com.parsroyal.solutiontablet.ui.fragment.LoginFragment;
import com.parsroyal.solutiontablet.util.DialogUtil;

public class LoginActivity extends AppCompatActivity {

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_login);
    showLoginFragment();
  }

  private void showLoginFragment() {
    LoginFragment fragment = LoginFragment.newInstance();
    getSupportFragmentManager().beginTransaction()
        .replace(R.id.container, fragment, fragment.getFragmentTag())
        .addToBackStack(fragment.getFragmentTag()).commit();
  }

  @Override
  public void onBackPressed() {
    //TODO old code, update with new style
    DialogUtil.showConfirmDialog(this, getString(R.string.message_exit),
        getString(R.string.message_do_you_want_to_exit),
        (dialog, which) ->
        {
          dialog.dismiss();
          finish();
        });
  }
}
