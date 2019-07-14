package com.parsroyal.solutiontablet.ui.fragment;

import android.os.Bundle;
import androidx.annotation.NonNull;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import com.parsroyal.solutiontablet.R;
import com.parsroyal.solutiontablet.constants.Constants;
import com.parsroyal.solutiontablet.constants.SaleOrderStatus;
import com.parsroyal.solutiontablet.data.event.ErrorEvent;
import com.parsroyal.solutiontablet.data.listmodel.SaleOrderListModel;
import com.parsroyal.solutiontablet.data.model.GoodsDtoList;
import com.parsroyal.solutiontablet.data.searchobject.SaleOrderSO;
import com.parsroyal.solutiontablet.service.impl.SaleOrderServiceImpl;
import com.parsroyal.solutiontablet.service.impl.SettingServiceImpl;
import com.parsroyal.solutiontablet.ui.activity.MainActivity;
import com.parsroyal.solutiontablet.ui.adapter.RejectAdapter;
import com.parsroyal.solutiontablet.util.DialogUtil;
import com.parsroyal.solutiontablet.util.Empty;
import com.parsroyal.solutiontablet.util.ToastUtil;
import com.parsroyal.solutiontablet.util.constants.ApplicationKeys;
import java.util.List;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

public class ReturnListFragment extends BaseFragment {

  @BindView(R.id.recycler_view)
  RecyclerView recyclerView;
  @BindView(R.id.fab_add_return)
  FloatingActionButton fabAddReturn;

  private VisitDetailFragment parent;
  private MainActivity mainActivity;
  private SaleOrderServiceImpl saleOrderService;
  private SettingServiceImpl settingService;
  private String saleType;
  private long visitId;
  private SaleOrderSO saleOrderSO = new SaleOrderSO();
  private RejectAdapter adapter;

  public ReturnListFragment() {
    // Required empty public constructor
  }

  public static ReturnListFragment newInstance(Bundle arguments,
      VisitDetailFragment visitDetailFragment) {
    ReturnListFragment returnListFragment = new ReturnListFragment();
    returnListFragment.parent = visitDetailFragment;
    returnListFragment.setArguments(arguments);
    return returnListFragment;
  }


  @Override
  public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
      Bundle savedInstanceState) {
    // Inflate the layout for this fragment
    View view = inflater.inflate(R.layout.fragment_return_list, container, false);
    mainActivity = (MainActivity) getActivity();
    ButterKnife.bind(this, view);
    Bundle args = getArguments();
    saleOrderService = new SaleOrderServiceImpl(mainActivity);
    settingService = new SettingServiceImpl();

    saleType = settingService.getSettingValue(ApplicationKeys.SETTING_SALE_TYPE);

    if (args != null) {
      visitId = args.getLong(Constants.VISIT_ID, -1);
    }
    if (parent == null) {
      fabAddReturn.hide();
    }
    setUpRecyclerView();
    return view;
  }

  //set up recycler view
  private void setUpRecyclerView() {
    adapter = new RejectAdapter(mainActivity, getReturnList(), parent == null, visitId);
    LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
    recyclerView.setLayoutManager(linearLayoutManager);
    recyclerView.setAdapter(adapter);
    if (!Empty.isEmpty(getArguments())) {
      recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
        @Override
        public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
          super.onScrolled(recyclerView, dx, dy);
          if (dy > 0) {
            fabAddReturn.hide();
          } else {
            fabAddReturn.show();
          }
        }
      });
    }
  }

  private List<SaleOrderListModel> getReturnList() {
    if (parent != null) {
      saleOrderSO.setCustomerBackendId(parent.getCustomer().getBackendId());
    }
    saleOrderSO.setIgnoreDraft(true);
    saleOrderSO.setStatusId(SaleOrderStatus.REJECTED.getId());
    return saleOrderService.findOrders(saleOrderSO);
  }

  @Override
  public int getFragmentId() {
    return 0;
  }


  @OnClick(R.id.fab_add_return)
  public void onViewClicked() {
    parent.openOrderDetailFragment(SaleOrderStatus.REJECTED_DRAFT.getId(), false);
  }

  @Override
  public void onResume() {
    super.onResume();
    EventBus.getDefault().register(this);
  }

  @Override
  public void onPause() {
    super.onPause();
    EventBus.getDefault().unregister(this);
  }

  @Subscribe
  public void getMessage(GoodsDtoList goodsDtoList) {
    adapter.setRejectGoods(goodsDtoList);
  }

  @Subscribe
  public void getMessage(ErrorEvent event) {
    if (Empty.isNotEmpty(event.getMessage()) && event.getMessage().equals("reject")) {

      DialogUtil.dismissProgressDialog();
      ToastUtil.toastError(mainActivity,
          mainActivity.getString(R.string.err_reject_order_not_possible));
    }
  }
}
