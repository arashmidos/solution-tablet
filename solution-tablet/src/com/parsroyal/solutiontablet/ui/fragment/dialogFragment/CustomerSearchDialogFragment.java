package com.parsroyal.solutiontablet.ui.fragment.dialogFragment;

import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import com.parsroyal.solutiontablet.R;
import com.parsroyal.solutiontablet.constants.BaseInfoTypes;
import com.parsroyal.solutiontablet.data.model.LabelValue;
import com.parsroyal.solutiontablet.ui.MainActivity;
import com.parsroyal.solutiontablet.ui.adapter.CityAdapter;
import com.parsroyal.solutiontablet.ui.adapter.CustomerSearchAdapter;
import java.util.ArrayList;
import java.util.List;

public class CustomerSearchDialogFragment extends DialogFragment {

  @BindView(R.id.recycler_view)
  RecyclerView recyclerView;
  @BindView(R.id.customer_edt)
  EditText customerEdt;
  @BindView(R.id.search_img)
  ImageView searchImg;


  private MainActivity mainActivity;
  private CustomerSearchAdapter customerSearchAdapter;

  public CustomerSearchDialogFragment() {
    // Required empty public constructor
  }

  public static CustomerSearchDialogFragment newInstance() {
    CustomerSearchDialogFragment fragment = new CustomerSearchDialogFragment();
    return fragment;
  }

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setStyle(DialogFragment.STYLE_NORMAL, R.style.myDialog);
    setRetainInstance(true);
  }

  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container,
      Bundle savedInstanceState) {
    // Inflate the layout for this fragment
    View view = inflater.inflate(R.layout.fragment_dialog_customer_search, container, false);
    ButterKnife.bind(this, view);
    mainActivity = (MainActivity) getActivity();
    setUpRecyclerView();
    onSearch();
    return view;
  }

  private void onSearch() {
    customerEdt.addTextChangedListener(new TextWatcher() {
      @Override
      public void beforeTextChanged(CharSequence s, int start, int count, int after) {

      }

      @Override
      public void onTextChanged(CharSequence s, int start, int before, int count) {
        if (TextUtils.isEmpty(s.toString())) {
          searchImg.setVisibility(View.VISIBLE);
        } else {
          searchImg.setVisibility(View.GONE);
          customerSearchAdapter.update(customerSearchAdapter.getFilteredData(s));
        }
      }

      @Override
      public void afterTextChanged(Editable s) {

      }
    });

  }

  //set up recycler view
  private void setUpRecyclerView() {
    customerSearchAdapter = new CustomerSearchAdapter(getActivity(),this,new ArrayList<>(), 0);
    LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
    recyclerView.setLayoutManager(linearLayoutManager);
    recyclerView.setAdapter(customerSearchAdapter);
  }
  @OnClick({R.id.close_btn})
  public void onClick(View view) {
    switch (view.getId()) {
      case R.id.close_btn:
        getDialog().dismiss();
        break;
    }
  }

  @Override
  public void onDestroyView() {
    //workaround for this issue: https://code.google.com/p/android/issues/detail?id=17423 (unable to retain instance after configuration change)
    if (getDialog() != null && getRetainInstance()) {
      getDialog().setDismissMessage(null);
    }
    super.onDestroyView();
  }
}
