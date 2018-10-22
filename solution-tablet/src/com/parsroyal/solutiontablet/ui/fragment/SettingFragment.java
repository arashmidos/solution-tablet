package com.parsroyal.solutiontablet.ui.fragment;


import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioButton;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import com.parsroyal.solutiontablet.R;
import com.parsroyal.solutiontablet.ui.MainActivity;
import com.parsroyal.solutiontablet.util.PreferenceHelper;

public class SettingFragment extends BaseFragment {


  @BindView(R.id.google_map)
  RadioButton googleMap;
  @BindView(R.id.waze)
  RadioButton waze;

  Unbinder unbinder;

  private MainActivity mainActivity;

  public SettingFragment() {
    // Required empty public constructor
  }

  public static SettingFragment newInstance() {
    SettingFragment fragment = new SettingFragment();
    return fragment;
  }

  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container,
      Bundle savedInstanceState) {
    // Inflate the layout for this fragment
    View view = inflater.inflate(R.layout.fragment_setting, container, false);
    unbinder = ButterKnife.bind(this, view);
    mainActivity = (MainActivity) getActivity();
    mainActivity.changeTitle(getString(R.string.setting));
    setDefaultNavigator();
    return view;
  }

  private void setDefaultNavigator() {
    String defNav = PreferenceHelper.getDefaultNavigator();
    if (defNav.equals("google")) {
      googleMap.setChecked(true);
    } else {
      waze.setChecked(true);
    }
  }

  @Override
  public int getFragmentId() {
    return MainActivity.SETTING_FRAGMENT;
  }

  @Override
  public void onDestroyView() {
    super.onDestroyView();
    unbinder.unbind();
  }

  @OnClick({R.id.google_map, R.id.waze})
  public void onViewClicked(View view) {
    switch (view.getId()) {
      case R.id.google_map:
        PreferenceHelper.setDefaultNavigator("google");
        break;
      case R.id.waze:
        PreferenceHelper.setDefaultNavigator("waze");
        break;
    }
  }
}
