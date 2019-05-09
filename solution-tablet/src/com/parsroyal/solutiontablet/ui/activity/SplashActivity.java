package com.parsroyal.solutiontablet.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import com.parsroyal.solutiontablet.R;
import com.parsroyal.solutiontablet.util.MultiScreenUtility;
import com.parsroyal.solutiontablet.util.NetworkUtil;

/**
 * Created by m.sefidi on 5/24/14.
 */
public class SplashActivity extends AppCompatActivity {

  private static final int SPLASH_SCREEN_TIME = 2000;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    if (!isTaskRoot()
        && getIntent().hasCategory(Intent.CATEGORY_LAUNCHER)
        && getIntent().getAction() != null
        && getIntent().getAction().equals(Intent.ACTION_MAIN)) {

      finish();
      return;
    }

    setContentView(R.layout.activity_splash);
    new Handler().postDelayed(() -> {

      if (!NetworkUtil.isTokenExpired()) {
        if (MultiScreenUtility.isTablet(this)) {
          startActivity(new Intent(this, TabletMainActivity.class));
        } else {
          startActivity(new Intent(this, MobileMainActivity.class));
        }
      } else {
        Intent iLoginActivity = new Intent(getApplicationContext(),
            LoginActivity.class);
        startActivity(iLoginActivity);
      }

      finish();
    }, SPLASH_SCREEN_TIME);
  }
}
