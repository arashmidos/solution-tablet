package com.parsroyal.solutiontablet.ui.adapter;

import android.content.Context;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.RecyclerView.Adapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioButton;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import com.parsroyal.solutiontablet.R;
import com.parsroyal.solutiontablet.data.model.LabelValue;
import com.parsroyal.solutiontablet.ui.adapter.AssortmentAdapter.ViewHolder;
import com.parsroyal.solutiontablet.ui.fragment.dialogFragment.GoodsFilterDialogFragment;
import java.util.List;

public class AssortmentAdapter extends Adapter<ViewHolder> {

  private Context context;
  private List<LabelValue> assortments;
  private LayoutInflater inflater;
  private LabelValue selectedItem;
  private GoodsFilterDialogFragment goodsFilterDialogFragment;

  public AssortmentAdapter(Context context,
      GoodsFilterDialogFragment goodsFilterDialogFragment,
      List<LabelValue> assortments) {
    this.context = context;
    this.assortments = assortments;
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
    return assortments.size();
  }

  public LabelValue getSelectedItem() {
    return selectedItem;
  }

  public void removeSelection() {
    selectedItem = null;
    notifyDataSetChanged();
  }

  public void setSelected(LabelValue selectedAssortmentLV) {
    this.selectedItem = selectedAssortmentLV;
  }

  public void update(List<LabelValue> list) {
    this.assortments = list;
    notifyDataSetChanged();
  }

  public class ViewHolder extends RecyclerView.ViewHolder {

    @BindView(R.id.item_radio)
    RadioButton itemRadio;

    private LabelValue assortment;

    public ViewHolder(View itemView) {
      super(itemView);
      ButterKnife.bind(this, itemView);
    }

    public void setData(int position) {
      assortment = assortments.get(position);
      itemRadio.setText(assortment.getLabel());
      if (selectedItem != null && selectedItem.equals(assortment)) {
        itemRadio.setChecked(true);
      } else {
        itemRadio.setChecked(false);
      }
    }

    @OnClick(R.id.item_radio)
    public void onViewClicked() {
//      goodsFilterDialogFragment.removeSelectionVisibility(false);
      selectedItem = assortment;
      notifyDataSetChanged();
    }
  }
}
