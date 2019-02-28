package com.parsroyal.storemanagement.ui.fragment.dialogFragment;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
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
import com.parsroyal.storemanagement.R;
import com.parsroyal.storemanagement.constants.BaseInfoTypes;
import com.parsroyal.storemanagement.data.model.LabelValue;
import com.parsroyal.storemanagement.service.impl.BaseInfoServiceImpl;
import com.parsroyal.storemanagement.ui.activity.MainActivity;
import com.parsroyal.storemanagement.ui.adapter.DeliveryReturnAdapter;
import java.util.List;

public class DeliveryRejectDialogFragment extends DialogFragment {

  @BindView(R.id.recycler_view)
  RecyclerView recyclerView;
  @BindView(R.id.toolbar_title)
  TextView toolbarTitle;
  @BindView(R.id.submit_btn)
  Button submitButton;

  private LabelValue selectedItem;
  private DeliveryReturnAdapter adapter;
  private MainActivity mainActivity;
  private List<LabelValue> info;
  private BaseInfoServiceImpl baseInfoService;
  private MainActivity context;
  private FinalizeOrderDialogFragment parent;
  private boolean isCanceled;

  public DeliveryRejectDialogFragment() {
    // Required empty public constructor
  }

  public static DeliveryRejectDialogFragment newInstance(
      MainActivity mainActivity, FinalizeOrderDialogFragment finalizeOrderDialogFragment,
      boolean isCanceled) {
    DeliveryRejectDialogFragment fragment = new DeliveryRejectDialogFragment();
    fragment.context = mainActivity;
    fragment.parent = finalizeOrderDialogFragment;
    fragment.isCanceled = isCanceled;
    return fragment;
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
    View view = inflater.inflate(R.layout.fragment_gift_result_dialog, container, false);
    ButterKnife.bind(this, view);
    mainActivity = (MainActivity) getActivity();
    baseInfoService = new BaseInfoServiceImpl(mainActivity);

    toolbarTitle.setText(R.string.reject_reason_title);
    setUpRecyclerView();

    return view;
  }

  //set up recycler view
  private void setUpRecyclerView() {
    adapter = new DeliveryReturnAdapter(mainActivity, getModel(), this);
    LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
    recyclerView.setLayoutManager(linearLayoutManager);
    recyclerView.setAdapter(adapter);
  }

  private List<LabelValue> getModel() {
    return baseInfoService
        .getAllBaseInfosLabelValuesByTypeId(BaseInfoTypes.DELIVERY_RETURN_TYPE.getId());
  }

  @OnClick({R.id.close, R.id.submit_btn})
  public void onClick(View view) {
    switch (view.getId()) {
      case R.id.close:
        getDialog().dismiss();
        break;
      case R.id.submit_btn:
        if( selectedItem!=null) {
          if( isCanceled) {
            parent.saveOrder(selectedItem.getValue());
          }else{
            parent.setRejectType(selectedItem.getValue());
          }
          getDialog().dismiss();
        }
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

  public void setRejectType(LabelValue selectedItem) {
    this.selectedItem = selectedItem;
  }
}
