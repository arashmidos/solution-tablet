package com.parsroyal.storemanagement.ui.adapter;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import butterknife.BindView;
import butterknife.ButterKnife;
import com.parsroyal.storemanagement.R;
import com.parsroyal.storemanagement.data.model.LabelValue;
import com.parsroyal.storemanagement.ui.fragment.dialogFragment.SearchListDialogFragment;
import com.parsroyal.storemanagement.util.MultiScreenUtility;
import com.parsroyal.storemanagement.util.NumberUtil;
import java.util.ArrayList;
import java.util.List;
import org.jetbrains.annotations.NotNull;

/**
 * Created by Arash
 */

public class SelectableSearchListAdapter extends RecyclerView.Adapter<SelectableSearchListAdapter.ViewHolder> {

  private LayoutInflater inflater;
  private LabelValue selectedItem;
  private Context context;
  private List<LabelValue> model;
  private SearchListDialogFragment dialogFragment;
  private boolean isEditable;

  public SelectableSearchListAdapter(Context context, List<LabelValue> model,
      LabelValue selectedItem, SearchListDialogFragment dialogFragment, boolean isEditable) {
    this.context = context;
    this.isEditable = isEditable;
    this.selectedItem = selectedItem;
    this.model = model;
    this.dialogFragment = dialogFragment;
    inflater = LayoutInflater.from(context);
  }

  @NotNull
  @Override
  public ViewHolder onCreateViewHolder(@NotNull ViewGroup parent, int viewType) {
    View view = inflater.inflate(R.layout.item_selectable_search_list, parent, false);
    return new ViewHolder(view);
  }

  @Override
  public void onBindViewHolder(@NotNull ViewHolder holder, int position) {
    LabelValue paymentMethod = model.get(position);
    holder.setData(paymentMethod, position);

    if (!MultiScreenUtility.isTablet(context)) {
      lastItem(position == model.size() - 1, holder);
    }
  }

  private void lastItem(boolean isLastItem, ViewHolder holder) {
    LinearLayout.LayoutParams parameter = new LinearLayout.LayoutParams(
        ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
    if (isLastItem) {
      parameter.setMargins(0, 0, 0, 160);
    } else {
      parameter.setMargins(0, 0, 0, 0);
    }
    holder.bottomLine.setVisibility(isLastItem ? View.GONE : View.VISIBLE);
    holder.mainLay.setLayoutParams(parameter);
  }

  public LabelValue getSelectedItem() {
    return selectedItem;
  }

  @Override
  public int getItemCount() {
    return model.size();
  }

  public void updateList(List<LabelValue> model) {
    this.model = model;
    notifyDataSetChanged();
  }

  public class ViewHolder extends RecyclerView.ViewHolder {

    @BindView(R.id.title_tv)
    TextView titleTv;
    @BindView(R.id.main_lay)
    RelativeLayout mainLay;
    @BindView(R.id.bottom_line)
    View bottomLine;
    private LabelValue model;
    private int position;

    public ViewHolder(View itemView) {
      super(itemView);
      ButterKnife.bind(this, itemView);
    }

    public void setData(LabelValue model, int position) {
      this.model = model;
      this.position = position;
      titleTv.setText(NumberUtil.digitsToPersian(model.getLabel()));
      if (selectedItem != null && model.getValue().equals(selectedItem.getValue())) {
        mainLay.setBackgroundColor(ContextCompat.getColor(context, R.color.primary_dark));
        titleTv.setTextColor(ContextCompat.getColor(context, android.R.color.white));
      } else {
        mainLay.setBackgroundColor(ContextCompat.getColor(context, android.R.color.white));
        titleTv.setTextColor(ContextCompat.getColor(context, R.color.black_85));
      }

      mainLay.setOnClickListener(v -> {
        if (isEditable) {
          selectedItem = model;
          notifyDataSetChanged();
          if (dialogFragment != null) {
            dialogFragment.setSelectedItem(model.getValue());
          }
        }
      });
    }
  }
}
