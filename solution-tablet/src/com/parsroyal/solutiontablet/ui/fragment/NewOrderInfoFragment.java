package com.parsroyal.solutiontablet.ui.fragment;

import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.parsroyal.solutiontablet.R;
import com.parsroyal.solutiontablet.data.model.LabelValue;
import com.parsroyal.solutiontablet.ui.MainActivity;
import com.parsroyal.solutiontablet.ui.fragment.dialogFragment.PaymentMethodDialogFragment;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;


public class NewOrderInfoFragment extends BaseFragment {

  @BindView(R.id.customer_name_tv) TextView customerNameTv;
  @BindView(R.id.cost_tv) TextView costTv;
  @BindView(R.id.date_tv) TextView dateTv;
  @BindView(R.id.payment_method_tv) TextView paymentMethodTv;
  @BindView(R.id.order_code_tv) TextView orderCodeTv;

  private LabelValue selectedItem = null;
  private MainActivity activity;

  public NewOrderInfoFragment() {
    // Required empty public constructor
  }

  public static NewOrderInfoFragment newInstance() {
    return new NewOrderInfoFragment();
  }

  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container,
                           Bundle savedInstanceState) {
    // Inflate the layout for this fragment
    View view = inflater.inflate(R.layout.fragment_new_order_info, container, false);
    ButterKnife.bind(this, view);
    activity = (MainActivity) getActivity();
    setData();
    return view;
  }

  private void setData() {
    if (selectedItem != null)
      paymentMethodTv.setText(selectedItem.getLabel());
    activity.changeTitle(getString(R.string.payment_info));
    customerNameTv.setText("سید رضا سعیدی");
    costTv.setText("556.600 تومان");
    dateTv.setText("12/05/1395");
    orderCodeTv.setText("5125484");
  }

  public void setPaymentMethod(LabelValue paymentMethod) {
    selectedItem = paymentMethod;
    paymentMethodTv.setText(selectedItem.getLabel());
  }

  @OnClick({R.id.payment_method_tv, R.id.submit_order_btn}) public void onClick(View view) {
    switch (view.getId()) {
      case R.id.payment_method_tv:
        showPaymentMethodDialog();
        break;
      case R.id.submit_order_btn:
        Toast.makeText(activity, "submit", Toast.LENGTH_SHORT).show();
        break;
    }
  }

  @Override public int getFragmentId() {
    return activity.ORDER_INFO_FRAGMENT;
  }

  private void showPaymentMethodDialog() {
    FragmentTransaction ft = activity.getSupportFragmentManager().beginTransaction();
    PaymentMethodDialogFragment paymentMethodDialogFragment = PaymentMethodDialogFragment.newInstance(this, selectedItem);
    paymentMethodDialogFragment.show(ft, "payment method");
  }
}
