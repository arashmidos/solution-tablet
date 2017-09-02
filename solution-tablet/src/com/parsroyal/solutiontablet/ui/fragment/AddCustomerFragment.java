package com.parsroyal.solutiontablet.ui.fragment;

import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import butterknife.BindView;
import butterknife.ButterKnife;
import com.parsroyal.solutiontablet.R;
import com.parsroyal.solutiontablet.ui.MainActivity;
import java.util.ArrayList;
import java.util.List;

public class AddCustomerFragment extends BaseFragment {

  @BindView(R.id.city_spinner)
  Spinner citySpinner;
  @BindView(R.id.state_spinner)
  Spinner stateSpinner;
  @BindView(R.id.ownership_spinner)
  Spinner ownershipSpinner;
  @BindView(R.id.activity_spinner)
  Spinner activitySpinner;
  @BindView(R.id.customer_class_spinner)
  Spinner customerClassSpinner;
  private MainActivity activity;

  public AddCustomerFragment() {
    // Required empty public constructor
  }

  public static AddCustomerFragment newInstance() {
    AddCustomerFragment fragment = new AddCustomerFragment();
    return fragment;
  }

  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container,
      Bundle savedInstanceState) {
    // Inflate the layout for this fragment
    View view = inflater.inflate(R.layout.fragment_add_customer, container, false);
    activity = (MainActivity) getActivity();
    activity.changeTitle(getString(R.string.add_customer));
    ButterKnife.bind(this, view);
    setUpSpinners();
    return view;
  }

  private void setUpSpinners() {
    List<String> list = new ArrayList<>();
    list.add("گزینه 1");
    list.add("گزینه 2");
    list.add("گزینه 3");
    list.add("گزینه 4");
    initSpinner(citySpinner, list, getString(R.string.city));
    initSpinner(stateSpinner, list, getString(R.string.state));
    initSpinner(ownershipSpinner, list, getString(R.string.ownership_type));
    initSpinner(activitySpinner, list, getString(R.string.activity_type));
    initSpinner(customerClassSpinner, list, getString(R.string.customer_class));
  }

  private void initSpinner(Spinner spinner, List<String> items, String hint) {
    ArrayAdapter<String> adapter = new ArrayAdapter<String>(activity,
        android.R.layout.simple_spinner_dropdown_item) {

      @Override
      public View getView(int position, View convertView, ViewGroup parent) {

        View v = super.getView(position, convertView, parent);
        if (position == getCount()) {
          ((TextView) v.findViewById(android.R.id.text1)).setText("");
          ((TextView) v.findViewById(android.R.id.text1))
              .setHintTextColor(ContextCompat.getColor(activity, android.R.color.black));
          ((TextView) v.findViewById(android.R.id.text1)).setHint(getItem(getCount()));
        }
        return v;
      }

      @Override
      public int getCount() {
        return super.getCount() - 1;
      }
    };
    adapter.addAll(items);
    //TODO:add this on the end of the list. its spinner hint
    adapter.add(hint);
    spinner.setAdapter(adapter);
    spinner.setSelection(adapter.getCount());
  }

  @Override
  public int getFragmentId() {
    return MainActivity.NEW_CUSTOMER_DETAIL_FRAGMENT_ID;
  }
}
