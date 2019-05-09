package com.parsroyal.solutiontablet.ui.fragment.dialogFragment;

import android.os.Bundle;
import android.support.annotation.NonNull;
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
import com.parsroyal.solutiontablet.ui.activity.MainActivity;
import com.parsroyal.solutiontablet.ui.adapter.PathDetailAdapter;
import java.util.ArrayList;

public class CustomerSearchDialogFragment extends DialogFragment {

  @BindView(R.id.recycler_view)
  RecyclerView recyclerView;
  @BindView(R.id.customer_edt)
  EditText customerEdt;
  @BindView(R.id.search_img)
  ImageView searchImg;


  private MainActivity mainActivity;
  private PathDetailAdapter pathDetailAdapter;

  public CustomerSearchDialogFragment() {
    // Required empty public constructor
  }

  public static CustomerSearchDialogFragment newInstance() {
    return new CustomerSearchDialogFragment();
  }

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setStyle(DialogFragment.STYLE_NORMAL, R.style.myDialog);
    setRetainInstance(true);
  }

  @Override
  public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
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
          pathDetailAdapter.update(pathDetailAdapter.getFilteredData(s));
        }
      }

      @Override
      public void afterTextChanged(Editable s) {

      }
    });

  }

  //set up recycler view
  private void setUpRecyclerView() {
    pathDetailAdapter = new PathDetailAdapter(mainActivity, new ArrayList<>(), null, false);
    pathDetailAdapter.setSearchCallback(this);
    LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
    recyclerView.setLayoutManager(linearLayoutManager);
    recyclerView.setAdapter(pathDetailAdapter);
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
