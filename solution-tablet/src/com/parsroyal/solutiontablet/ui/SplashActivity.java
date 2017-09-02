package com.parsroyal.solutiontablet.ui;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import com.parsroyal.solutiontablet.R;
import com.parsroyal.solutiontablet.util.NetworkUtil;

/**
 * Created by m.sefidi on 5/24/14.
 */
public class SplashActivity extends BaseActivity {

  private static final int SPLASH_SCREEN_TIME = 2000;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_splash);
    new Handler().postDelayed(() -> {

      if (!NetworkUtil.isTokenExpired()) {
        startActivity(new Intent(this, MainActivity.class));
      } else {
        Intent iLoginActivity = new Intent(getApplicationContext(),
            LoginActivity.class);
        startActivity(iLoginActivity);
      }

      finish();
    }, SPLASH_SCREEN_TIME);
  }
}
