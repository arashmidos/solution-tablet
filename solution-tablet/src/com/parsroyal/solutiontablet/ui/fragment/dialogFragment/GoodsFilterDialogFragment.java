package com.parsroyal.solutiontablet.ui.fragment.dialogFragment;


import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
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
import android.widget.LinearLayout;
import android.widget.TextView;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import com.parsroyal.solutiontablet.R;
import com.parsroyal.solutiontablet.constants.BaseInfoTypes;
import com.parsroyal.solutiontablet.data.model.LabelValue;
import com.parsroyal.solutiontablet.service.impl.BaseInfoServiceImpl;
import com.parsroyal.solutiontablet.ui.activity.MainActivity;
import com.parsroyal.solutiontablet.ui.adapter.AssortmentAdapter;
import com.parsroyal.solutiontablet.ui.adapter.SupplierAdapter;
import com.parsroyal.solutiontablet.util.Empty;
import java.util.List;

public class GoodsFilterDialogFragment extends DialogFragment {

  public final String TAG = GoodsFilterDialogFragment.class.getSimpleName();
  @BindView(R.id.supplier_recyclerView)
  RecyclerView supplierRecyclerView;
  @BindView(R.id.assortment_recyclerView)
  RecyclerView assortmentRecyclerView;
  @BindView(R.id.filters)
  LinearLayout filtersLayout;
  @BindView(R.id.search_layout)
  LinearLayout searchLayout;
  @BindView(R.id.title_tv)
  TextView title;
  @BindView(R.id.back_iv)
  ImageView backIv;
  @BindView(R.id.close_iv)
  ImageView closeIv;
  @BindView(R.id.search_edt)
  EditText searchEdt;
  @BindView(R.id.search_img)
  ImageView searchImg;
  private MainActivity mainActivity;
  private BaseInfoServiceImpl baseInfoService;
  private SupplierAdapter supplierAdapter;
  private AssortmentAdapter assortmentAdapter;
  private OnFilterSelected parent;
  private LabelValue selectedAssortmentLV;
  private LabelValue selectedSupplierLV;
  private String constraint;

  public GoodsFilterDialogFragment() {
    // Required empty public constructor
  }

  public static GoodsFilterDialogFragment newInstance(
      LabelValue assortment, LabelValue supplier) {
    GoodsFilterDialogFragment goodsFilterDialogFragment = new GoodsFilterDialogFragment();
    goodsFilterDialogFragment.selectedAssortmentLV = assortment;
    goodsFilterDialogFragment.selectedSupplierLV = supplier;
    return goodsFilterDialogFragment;
  }

  @Override
  public void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    setStyle(DialogFragment.STYLE_NORMAL, R.style.myDialog);

    try {
      parent = (OnFilterSelected) getTargetFragment();
    } catch (ClassCastException e) {
      throw new ClassCastException("Calling Fragment must implement OnFilterSelected");
    }
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
    setSelection();
    setListeners();
    return view;
  }

  private void setListeners() {
    searchEdt.addTextChangedListener(new TextWatcher() {
      @Override
      public void beforeTextChanged(CharSequence s, int start, int count, int after) {
      }

      @Override
      public void onTextChanged(CharSequence s, int start, int before, int count) {
        if (TextUtils.isEmpty(s.toString())) {
          searchImg.setImageResource(R.drawable.ic_search);
        } else {
          searchImg.setImageResource(R.drawable.ic_close_24dp);
        }
      }

      @Override
      public void afterTextChanged(Editable s) {
        constraint = s.toString();

        if (Empty.isNotEmpty(constraint)) {
          if (assortmentRecyclerView.getVisibility() == View.VISIBLE) {
            List<LabelValue> list = baseInfoService
                .search(BaseInfoTypes.ASSORTMENT.getId(), constraint);
            assortmentAdapter.update(list);
          } else {
            //Its suuplier
            List<LabelValue> list = baseInfoService
                .search(BaseInfoTypes.SUPPLIER.getId(), constraint);
            supplierAdapter.update(list);
          }
        } else {
          if (assortmentRecyclerView.getVisibility() == View.VISIBLE) {
            assortmentAdapter.update(baseInfoService.getAllAssortment());
          } else {
            supplierAdapter.update(baseInfoService.getAllSupplier());
          }
        }
      }
    });
  }

  private void setSelection() {
    supplierAdapter.setSelected(selectedSupplierLV);
    assortmentAdapter.setSelected(selectedAssortmentLV);
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

  @OnClick({R.id.close_iv, R.id.do_filter_btn, R.id.remove_filters, R.id.supplier_lay,
      R.id.assortment_lay, R.id.back_iv, R.id.search_img})
  public void onViewClicked(View view) {
    switch (view.getId()) {
      case R.id.close_iv:
        dismiss();
        break;
      case R.id.do_filter_btn:
        doFilter();
        break;
      case R.id.remove_filters:
        removeFilters();
        doFilter();
        break;
      case R.id.assortment_lay:
        assortmentRecyclerView.setVisibility(View.VISIBLE);
        gotoDetails(R.string.assortment);

        break;
      case R.id.supplier_lay:
        supplierRecyclerView.setVisibility(View.VISIBLE);
        gotoDetails(R.string.supplier);
        break;
      case R.id.back_iv:
        supplierRecyclerView.setVisibility(View.GONE);
        assortmentRecyclerView.setVisibility(View.GONE);
        filtersLayout.setVisibility(View.VISIBLE);
        closeIv.setVisibility(View.VISIBLE);
        backIv.setVisibility(View.GONE);
        title.setText(R.string.filters);
        searchLayout.setVisibility(View.GONE);
        break;
      case R.id.search_img:
        searchEdt.setText("");
    }
  }

  private void gotoDetails(int titleId) {
    filtersLayout.setVisibility(View.GONE);
    searchLayout.setVisibility(View.VISIBLE);
    closeIv.setVisibility(View.GONE);
    backIv.setVisibility(View.VISIBLE);
    title.setText(titleId);
  }

  private void removeFilters() {
    supplierAdapter.removeSelection();
    assortmentAdapter.removeSelection();
  }

  private void doFilter() {
    selectedAssortmentLV = assortmentAdapter.getSelectedItem();
    selectedSupplierLV = supplierAdapter.getSelectedItem();

    parent.filterSelected(selectedAssortmentLV, selectedSupplierLV);

    dismiss();
  }

  public interface OnFilterSelected {

    void filterSelected(LabelValue assortment, LabelValue supplier);
  }
}
