package com.parsroyal.solutiontablet.ui.fragment.dialogFragment;

import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import com.parsroyal.solutiontablet.R;
import com.parsroyal.solutiontablet.constants.Constants;
import com.parsroyal.solutiontablet.data.event.UpdateEvent;
import com.parsroyal.solutiontablet.data.event.UpdateListEvent;
import com.parsroyal.solutiontablet.data.model.GoodsDtoList;
import com.parsroyal.solutiontablet.data.model.SaleOrderDto;
import com.parsroyal.solutiontablet.service.SaleOrderService;
import com.parsroyal.solutiontablet.service.impl.SaleOrderServiceImpl;
import com.parsroyal.solutiontablet.ui.MainActivity;
import com.parsroyal.solutiontablet.ui.adapter.OrderFinalizeAdapter;
import com.parsroyal.solutiontablet.ui.fragment.OrderFragment;
import com.parsroyal.solutiontablet.util.NumberUtil;
import org.greenrobot.eventbus.EventBus;

public class FinalizeOrderDialogFragment extends DialogFragment {

  @BindView(R.id.recycler_view)
  RecyclerView recyclerView;
  @BindView(R.id.total_amount_title)
  TextView totalAmountTitle;
  @BindView(R.id.total_amount_tv)
  TextView totalAmountTv;

  private OrderFinalizeAdapter adapter;
  private MainActivity activity;
  private SaleOrderService saleOrderService;
  private SaleOrderDto order;
  private OrderFragment orderFragment;
  private long orderId;
  private long orderStatus;
  private GoodsDtoList rejectedGoodsList;

  public FinalizeOrderDialogFragment() {
    // Required empty public constructor
  }

  public static FinalizeOrderDialogFragment newInstance(OrderFragment orderFragment) {
    FinalizeOrderDialogFragment fragment = new FinalizeOrderDialogFragment();
    fragment.orderFragment = orderFragment;
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
    View view = inflater.inflate(R.layout.fragment_finalize_order_dialog, container, false);
    ButterKnife.bind(this, view);
    activity = (MainActivity) getActivity();
    saleOrderService = new SaleOrderServiceImpl(activity);
    Bundle arguments = getArguments();
    orderId = arguments.getLong(Constants.ORDER_ID);
    orderStatus = arguments.getLong(Constants.ORDER_STATUS);
    order = saleOrderService.findOrderDtoById(orderId);
    rejectedGoodsList = (GoodsDtoList) arguments.getSerializable(Constants.REJECTED_LIST);

    setData();
    setUpRecyclerView();
    return view;
  }

  private void setData() {
    totalAmountTv.setText(NumberUtil.getCommaSeparated(order.getAmount() / 1000) + " " +
        getString(R.string.common_irr_currency));
  }

  public void updateList() {
    order = saleOrderService.findOrderDtoById(orderId);
    setData();
    EventBus.getDefault().post(new UpdateListEvent());
  }

  //set up recycler view
  private void setUpRecyclerView() {
    adapter = new OrderFinalizeAdapter(this, activity, order, rejectedGoodsList);
    LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
    recyclerView.setLayoutManager(linearLayoutManager);
    recyclerView.setAdapter(adapter);
  }

  @OnClick({R.id.close, R.id.submit_btn})
  public void onClick(View view) {
    switch (view.getId()) {
      case R.id.close:
        getDialog().dismiss();
        break;
      case R.id.submit_btn:
        orderFragment.goToOrderInfoFragment();
        getDialog().dismiss();
        break;
    }
  }
}
