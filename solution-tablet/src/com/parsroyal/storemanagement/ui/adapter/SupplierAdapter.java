package com.parsroyal.storemanagement.ui.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.RecyclerView.Adapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioButton;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import com.parsroyal.storemanagement.R;
import com.parsroyal.storemanagement.data.model.LabelValue;
import com.parsroyal.storemanagement.ui.adapter.SupplierAdapter.ViewHolder;
import com.parsroyal.storemanagement.ui.fragment.dialogFragment.GoodsFilterDialogFragment;
import java.util.List;

public class SupplierAdapter extends Adapter<ViewHolder> {

  private Context context;
  private List<LabelValue> suppliers;
  private LayoutInflater inflater;
  private LabelValue selectedItem;
  private GoodsFilterDialogFragment goodsFilterDialogFragment;


  public SupplierAdapter(Context context, GoodsFilterDialogFragment goodsFilterDialogFragment,
      List<LabelValue> supplier) {
    this.context = context;
    this.suppliers = supplier;
    this.selectedItem = null;
    this.goodsFilterDialogFragment = goodsFilterDialogFragment;
    this.inflater = LayoutInflater.from(context);
  }

  @NonNull
  @Override
  public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
    View view = inflater.inflate(R.layout.item_supplier_assortment, parent, false);
    return new ViewHolder(view);
  }

  @Override
  public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
    holder.setData(position);
  }

  @Override
  public int getItemCount() {
    return suppliers.size();
  }

  public LabelValue getSelectedItem() {
    return selectedItem;
  }

  public void removeSelection() {
    selectedItem = null;
    notifyDataSetChanged();
  }

  public class ViewHolder extends RecyclerView.ViewHolder {

    @BindView(R.id.item_radio)
    RadioButton itemRadio;

    private LabelValue supplier;

    public ViewHolder(View itemView) {
      super(itemView);
      ButterKnife.bind(this, itemView);
    }

    public void setData(int position) {
      supplier = suppliers.get(position);
      itemRadio.setText(supplier.getLabel());
      if (selectedItem != null && selectedItem.equals(supplier)) {
        itemRadio.setChecked(true);
      } else {
        itemRadio.setChecked(false);
      }
    }

    @OnClick(R.id.item_radio)
    public void onViewClicked() {
      goodsFilterDialogFragment.removeSelectionVisibility(true);
      selectedItem = supplier;
      notifyDataSetChanged();
    }
  }
}
