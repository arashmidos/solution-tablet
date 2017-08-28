package com.parsroyal.solutiontablet.ui.fragment.dialogFragment;

import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.view.ViewPager;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;

import com.parsroyal.solutiontablet.R;
import com.parsroyal.solutiontablet.ui.adapter.GoodImagePagerAdapter;
import com.parsroyal.solutiontablet.util.DepthPageTransformer;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import me.relex.circleindicator.CircleIndicator;

public class AddOrderDialogFragment extends DialogFragment {


  @BindView(R.id.pager) ViewPager viewPager;
  @BindView(R.id.indicator) CircleIndicator indicator;
  @BindView(R.id.spinner) Spinner spinner;

  public AddOrderDialogFragment() {
    // Required empty public constructor
  }


  public static AddOrderDialogFragment newInstance() {
    AddOrderDialogFragment fragment = new AddOrderDialogFragment();
    return fragment;
  }

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setStyle(DialogFragment.STYLE_NORMAL, R.style.myDialog);
  }

  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container,
                           Bundle savedInstanceState) {
    // Inflate the layout for this fragment
    View view = inflater.inflate(R.layout.fragment_add_order_dialog, container, false);
    ButterKnife.bind(this, view);
    setUpPager();
    setUpSpinner();
    return view;
  }

  private void setUpSpinner() {
    List<String> list = new ArrayList<>();
    list.add(getString(R.string.number));
    list.add(getString(R.string.carton));
    ArrayAdapter<String> adapter = new ArrayAdapter<String>
        (getActivity(), android.R.layout.simple_spinner_item, list) {
      public View getView(int position, View convertView, ViewGroup parent) {
        View v = super.getView(position, convertView, parent);
        ((TextView) v).setGravity(Gravity.RIGHT);
        return v;
      }

      public View getDropDownView(int position, View convertView, ViewGroup parent) {
        View v = super.getDropDownView(position, convertView, parent);
        ((TextView) v).setGravity(Gravity.RIGHT);
        return v;
      }
    };
    spinner.setAdapter(adapter);
  }

  private void setUpPager() {
    List<Integer> ids = new ArrayList<>();
    ids.add(R.drawable.ic_aboutus_43dp);
    ids.add(R.drawable.ic_exit_43dp);
    ids.add(R.drawable.ic_version_43dp);
    ids.add(R.drawable.ic_transform_43dp);
    final GoodImagePagerAdapter adapter = new GoodImagePagerAdapter(getActivity().getSupportFragmentManager(), getActivity(), ids);
    viewPager.setAdapter(adapter);
    indicator.setViewPager(viewPager);
    viewPager.setPageTransformer(true, new DepthPageTransformer());
  }

  @OnClick(R.id.close) public void onClick() {
    getDialog().dismiss();
  }
}
