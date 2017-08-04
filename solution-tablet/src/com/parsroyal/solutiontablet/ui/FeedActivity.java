package com.parsroyal.solutiontablet.ui;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.parsroyal.solutiontablet.R;
import com.parsroyal.solutiontablet.ui.fragment.FeaturesFragment;

public class FeedActivity extends AppCompatActivity {

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_feed);
    showFeaturesFragment();
  }
  private void showFeaturesFragment() {
    FeaturesFragment fragment = FeaturesFragment.newInstance();
    getSupportFragmentManager().beginTransaction()
        .replace(R.id.container, fragment, fragment.getFragmentTag())
        .addToBackStack(fragment.getFragmentTag()).commit();
  }
}
