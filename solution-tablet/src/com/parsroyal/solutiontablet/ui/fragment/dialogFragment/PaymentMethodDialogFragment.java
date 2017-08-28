package com.parsroyal.solutiontablet.ui.fragment.dialogFragment;

import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.parsroyal.solutiontablet.R;
import com.parsroyal.solutiontablet.constants.BaseInfoTypes;
import com.parsroyal.solutiontablet.data.model.LabelValue;
import com.parsroyal.solutiontablet.service.impl.BaseInfoServiceImpl;
import com.parsroyal.solutiontablet.ui.MainActivity;
import com.parsroyal.solutiontablet.ui.adapter.PaymentMethodAdapter;
import com.parsroyal.solutiontablet.ui.fragment.NewOrderInfoFragment;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class PaymentMethodDialogFragment extends DialogFragment {


  @BindView(R.id.recycler_view) RecyclerView recyclerView;

  private LabelValue selectedItem;
  private PaymentMethodAdapter adapter;
  private MainActivity activity;
  private BaseInfoServiceImpl baseInfoService;
  private NewOrderInfoFragment newOrderInfoFragment;

  public PaymentMethodDialogFragment() {
    // Required empty public constructor
  }


  public static PaymentMethodDialogFragment newInstance(NewOrderInfoFragment newOrderInfoFragment, LabelValue selectedItem) {
    PaymentMethodDialogFragment fragment = new PaymentMethodDialogFragment();
    fragment.newOrderInfoFragment = newOrderInfoFragment;
    fragment.selectedItem = selectedItem;
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
    View view = inflater.inflate(R.layout.fragment_payment_method_dialog, container, false);
    ButterKnife.bind(this, view);
    activity = (MainActivity) getActivity();
    baseInfoService = new BaseInfoServiceImpl(activity);
    setUpRecyclerView();
    return view;
  }

  //set up recycler view
  private void setUpRecyclerView() {
    adapter = new PaymentMethodAdapter(activity, getPaymentMethodList(), selectedItem);
    LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
    recyclerView.setLayoutManager(linearLayoutManager);
    recyclerView.setAdapter(adapter);
  }

  private List<LabelValue> getPaymentMethodList() {
    return baseInfoService
        .getAllBaseInfosLabelValuesByTypeId(BaseInfoTypes.PAYMENT_TYPE.getId());
  }

  @OnClick({R.id.close, R.id.submit_btn}) public void onClick(View view) {
    switch (view.getId()) {
      case R.id.close:
        getDialog().dismiss();
        break;
      case R.id.submit_btn:
        newOrderInfoFragment.setPaymentMethod(adapter.getSelectedItem());
        getDialog().dismiss();
        break;
    }
  }
}
