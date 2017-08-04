package com.parsroyal.solutiontablet.ui;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.parsroyal.solutiontablet.R;
import com.parsroyal.solutiontablet.ui.fragment.LoginFragment;

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
}
