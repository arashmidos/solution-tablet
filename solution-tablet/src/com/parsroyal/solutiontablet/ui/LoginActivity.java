package com.parsroyal.solutiontablet.ui;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import com.parsroyal.solutiontablet.R;
import com.parsroyal.solutiontablet.ui.fragment.LoginFragment;
import com.parsroyal.solutiontablet.util.DialogUtil;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

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
    //TODO shakib update with new style
    DialogUtil.showConfirmDialog(this, getString(R.string.message_exit),
        getString(R.string.message_do_you_want_to_exit),
        (dialog, which) ->
        {
          dialog.dismiss();
          finish();
        });
  }

  @Override
  protected void attachBaseContext(Context newBase) {
    super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
  }
}
