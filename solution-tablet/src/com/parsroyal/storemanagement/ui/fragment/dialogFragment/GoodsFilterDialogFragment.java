package com.parsroyal.storemanagement.ui.fragment.dialogFragment;


import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
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
import com.parsroyal.storemanagement.R;
import com.parsroyal.storemanagement.data.entity.Goods;
import com.parsroyal.storemanagement.data.event.GoodListEvent;
import com.parsroyal.storemanagement.data.model.LabelValue;
import com.parsroyal.storemanagement.data.searchobject.GoodsSo;
import com.parsroyal.storemanagement.service.impl.BaseInfoServiceImpl;
import com.parsroyal.storemanagement.service.impl.GoodsServiceImpl;
import com.parsroyal.storemanagement.ui.activity.MainActivity;
import com.parsroyal.storemanagement.ui.adapter.AssortmentAdapter;
import com.parsroyal.storemanagement.ui.adapter.SupplierAdapter;
import java.util.List;
import org.greenrobot.eventbus.EventBus;

public class GoodsFilterDialogFragment extends DialogFragment {

  public final String TAG = GoodsFilterDialogFragment.class.getSimpleName();

  @BindView(R.id.supplier_recyclerView)
  RecyclerView supplierRecyclerView;
  @BindView(R.id.assortment_recyclerView)
  RecyclerView assortmentRecyclerView;
  @BindView(R.id.remove_supplier_tv)
  TextView removeSupplierTv;
  @BindView(R.id.remove_assortment_tv)
  TextView removeAssortmentTv;

  private MainActivity mainActivity;
  private BaseInfoServiceImpl baseInfoService;
  private SupplierAdapter supplierAdapter;
  private AssortmentAdapter assortmentAdapter;

  public GoodsFilterDialogFragment() {
    // Required empty public constructor
  }

  public static GoodsFilterDialogFragment newInstance() {
    return new GoodsFilterDialogFragment();
  }

  @Override
  public void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    setStyle(DialogFragment.STYLE_NORMAL, R.style.myDialog);

    setRetainInstance(true);
  }

  protected String getTAG() {
    return TAG;
  }

  @Override
  public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
      Bundle savedInstanceState) {

    // Inflate the layout for this fragment
    View view = inflater.inflate(getLayout(), container, false);
    ButterKnife.bind(this, view);
    mainActivity = (MainActivity) getActivity();
    baseInfoService = new BaseInfoServiceImpl(mainActivity);
    setupSupplierRecyclerView();
    setupAssortmentRecyclerView();
    return view;
  }

  private void setupSupplierRecyclerView() {
    List<LabelValue> suppliers = baseInfoService.getAllSupplier();
    supplierAdapter = new SupplierAdapter(mainActivity, this, suppliers);
    supplierRecyclerView.setLayoutManager(new LinearLayoutManager(mainActivity));
    supplierRecyclerView.setAdapter(supplierAdapter);
  }

  private void setupAssortmentRecyclerView() {
    List<LabelValue> assortments = baseInfoService.getAllAssortment();
    assortmentAdapter = new AssortmentAdapter(mainActivity, this, assortments);
    assortmentRecyclerView.setLayoutManager(new LinearLayoutManager(mainActivity));
    assortmentRecyclerView.setAdapter(assortmentAdapter);
  }

  protected int getLayout() {
    if (!getTAG().contains("Sheet")) {
      setStyle(DialogFragment.STYLE_NORMAL, R.style.myDialog);
      return R.layout.fragment_dialog_goods_filter;
    }
    return R.layout.fragment_dialog_goods_filter_bottom_sheet;
  }

  @OnClick({R.id.close_iv, R.id.do_filter_tv, R.id.remove_assortment_tv, R.id.remove_supplier_tv})
  public void onViewClicked(View view) {
    switch (view.getId()) {
      case R.id.close_iv:
        dismiss();
        break;
      case R.id.do_filter_tv:
        doFilter();
        break;
      case R.id.remove_assortment_tv:
        removeAssortment();
        break;
      case R.id.remove_supplier_tv:
        removeSupplier();
        break;
    }
  }

  private void removeSupplier() {
    removeSupplierTv.setVisibility(View.GONE);
    supplierAdapter.removeSelection();
  }

  private void removeAssortment() {
    removeAssortmentTv.setVisibility(View.GONE);
    assortmentAdapter.removeSelection();
  }

  public void removeSelectionVisibility(boolean isSupplier) {
    if (isSupplier) {
      removeSupplierTv.setVisibility(View.VISIBLE);
    } else {
      removeAssortmentTv.setVisibility(View.VISIBLE);
    }
  }

  private void doFilter() {
    LabelValue selectedAssortmentLV = assortmentAdapter.getSelectedItem();
    LabelValue selectedSupplierLV = supplierAdapter.getSelectedItem();
    GoodsSo goodsSo = new GoodsSo();
    if (selectedAssortmentLV != null) {
      goodsSo.setAssortment(String.valueOf(selectedAssortmentLV.getValue()));
    }
    if (selectedSupplierLV != null) {
      goodsSo.setSupplier(String.valueOf(selectedSupplierLV.getValue()));
    }
    if (selectedAssortmentLV == null && selectedSupplierLV == null) {
      goodsSo.setConstraint("");
    }
    GoodsServiceImpl goodsService = new GoodsServiceImpl(mainActivity);
    List<Goods> goodsListModels = goodsService.searchForGoodsList(goodsSo);
    EventBus.getDefault().post(new GoodListEvent(goodsListModels));
    dismiss();
  }
}
