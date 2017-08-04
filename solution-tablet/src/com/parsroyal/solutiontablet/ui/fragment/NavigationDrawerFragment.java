package com.parsroyal.solutiontablet.ui.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.parsroyal.solutiontablet.R;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class NavigationDrawerFragment extends Fragment {

  @BindView(R.id.body) LinearLayout body;
  @BindView(R.id.body_log_out) RelativeLayout bodyLogOut;
  @BindView(R.id.footer) LinearLayout footer;
  @BindView(R.id.footer_log_out) TextView footerLogOut;
  @BindView(R.id.drop_img) ImageView dropImg;

  private boolean isLogOutMode = false;

  public NavigationDrawerFragment() {
    // Required empty public constructor
  }


  public static NavigationDrawerFragment newInstance() {
    NavigationDrawerFragment fragment = new NavigationDrawerFragment();
    return fragment;
  }

  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container,
                           Bundle savedInstanceState) {
    // Inflate the layout for this fragment
    View view = inflater.inflate(R.layout.fragment_navigation_drawer, container, false);
    ButterKnife.bind(this, view);
    return view;
  }

  @OnClick({R.id.user_name_tv, R.id.features_list_lay, R.id.today_paths_lay, R.id.reports_lay, R.id.customers_lay, R.id.map_lay, R.id.setting_lay, R.id.about_us, R.id.body_log_out, R.id.get_data_lay, R.id.send_data_lay})
  public void onClick(View view) {
    switch (view.getId()) {
      case R.id.user_name_tv:
        changeMode();
        break;
      case R.id.features_list_lay:
        Toast.makeText(getActivity(), "feature", Toast.LENGTH_SHORT).show();
        break;
      case R.id.customers_lay:
        Toast.makeText(getActivity(), "customers", Toast.LENGTH_SHORT).show();
        break;
      case R.id.today_paths_lay:
        Toast.makeText(getActivity(), "path", Toast.LENGTH_SHORT).show();
        break;
      case R.id.reports_lay:
        Toast.makeText(getActivity(), "report", Toast.LENGTH_SHORT).show();
        break;
      case R.id.map_lay:
        Toast.makeText(getActivity(), "map", Toast.LENGTH_SHORT).show();
        break;
      case R.id.setting_lay:
        Toast.makeText(getActivity(), "setting", Toast.LENGTH_SHORT).show();
        break;
      case R.id.about_us:
        Toast.makeText(getActivity(), "about us", Toast.LENGTH_SHORT).show();
        break;
      case R.id.body_log_out:
        Toast.makeText(getActivity(), "log out", Toast.LENGTH_SHORT).show();
        break;
      case R.id.get_data_lay:
        Toast.makeText(getActivity(), "get data", Toast.LENGTH_SHORT).show();
        break;
      case R.id.send_data_lay:
        Toast.makeText(getActivity(), "send data", Toast.LENGTH_SHORT).show();
        break;
    }
  }

  private void changeMode() {
    isLogOutMode = !isLogOutMode;
    if (isLogOutMode) {
      dropImg.setImageResource(R.drawable.ic_arrow_drop_up);
      dropImg.setColorFilter(ContextCompat.getColor(getActivity(),R.color.login_gray));
      body.setVisibility(View.GONE);
      bodyLogOut.setVisibility(View.VISIBLE);
      footer.setVisibility(View.GONE);
      footerLogOut.setVisibility(View.VISIBLE);
    } else {
      dropImg.setImageResource(R.drawable.ic_arrow_drop_down);
      dropImg.setColorFilter(ContextCompat.getColor(getActivity(),R.color.login_gray));
      body.setVisibility(View.VISIBLE);
      bodyLogOut.setVisibility(View.GONE);
      footer.setVisibility(View.VISIBLE);
      footerLogOut.setVisibility(View.GONE);
    }
  }
}
