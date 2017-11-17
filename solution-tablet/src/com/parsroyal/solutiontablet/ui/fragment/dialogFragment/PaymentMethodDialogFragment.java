package com.parsroyal.solutiontablet.ui.fragment.dialogFragment;

import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import com.parsroyal.solutiontablet.R;
import com.parsroyal.solutiontablet.constants.BaseInfoTypes;
import com.parsroyal.solutiontablet.data.model.LabelValue;
import com.parsroyal.solutiontablet.service.impl.BaseInfoServiceImpl;
import com.parsroyal.solutiontablet.ui.MainActivity;
import com.parsroyal.solutiontablet.ui.adapter.PaymentMethodAdapter;
import com.parsroyal.solutiontablet.ui.fragment.OrderInfoFragment;
import java.util.List;

public class PaymentMethodDialogFragment extends DialogFragment {

  @BindView(R.id.recycler_view)
  RecyclerView recyclerView;
  @BindView(R.id.toolbar_title)
  TextView toolbarTitle;
  @BindView(R.id.submit_btn)
  Button submitButton;


  private LabelValue selectedItem;
  private PaymentMethodAdapter adapter;
  private MainActivity mainActivity;
  private BaseInfoServiceImpl baseInfoService;
  private OrderInfoFragment orderInfoFragment;
  private boolean isReject;

  public PaymentMethodDialogFragment() {
    // Required empty public constructor
  }

  public static PaymentMethodDialogFragment newInstance(OrderInfoFragment orderInfoFragment,
      LabelValue selectedItem, boolean isReject) {
    PaymentMethodDialogFragment fragment = new PaymentMethodDialogFragment();
    fragment.orderInfoFragment = orderInfoFragment;
    fragment.selectedItem = selectedItem;
    fragment.isReject = isReject;
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
    View view = inflater.inflate(R.layout.fragment_payment_method_dialog, container, false);
    ButterKnife.bind(this, view);
    mainActivity = (MainActivity) getActivity();
    baseInfoService = new BaseInfoServiceImpl(mainActivity);
    setUpRecyclerView();
    if (isReject) {
      toolbarTitle.setText(R.string.select_reason_to_return);
      submitButton.setBackgroundColor(ContextCompat.getColor(mainActivity,R.color.register_return));
    }
    return view;
  }

  //set up recycler view
  private void setUpRecyclerView() {
    adapter = new PaymentMethodAdapter(mainActivity, getModel(), selectedItem, null,
        true);
    LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
    recyclerView.setLayoutManager(linearLayoutManager);
    recyclerView.setAdapter(adapter);
  }

  private List<LabelValue> getModel() {
    return baseInfoService
        .getAllBaseInfosLabelValuesByTypeId(
            isReject ? BaseInfoTypes.REJECT_TYPE.getId() : BaseInfoTypes.PAYMENT_TYPE.getId());
  }

  @OnClick({R.id.close, R.id.submit_btn})
  public void onClick(View view) {
    switch (view.getId()) {
      case R.id.close:
        getDialog().dismiss();
        break;
      case R.id.submit_btn:
        orderInfoFragment.setPaymentMethod(adapter.getSelectedItem());
        getDialog().dismiss();
        break;
    }
  }

  @Override
  public void onDestroyView() {
    //workaround for this issue: https://code.google.com/p/android/issues/detail?id=17423 (unable to retain instance after configuration change)
    if (getDialog() != null && getRetainInstance())
      getDialog().setDismissMessage(null);
    super.onDestroyView();
  }
}
