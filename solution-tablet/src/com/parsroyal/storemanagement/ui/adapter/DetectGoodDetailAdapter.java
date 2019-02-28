package com.parsroyal.storemanagement.ui.adapter;

import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.RecyclerView.Adapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import com.parsroyal.storemanagement.R;
import com.parsroyal.storemanagement.data.model.DetectGoodDetail;
import com.parsroyal.storemanagement.ui.adapter.DetectGoodDetailAdapter.ViewHolder;
import com.parsroyal.storemanagement.util.Empty;
import com.parsroyal.storemanagement.util.NumberUtil;
import java.util.List;
import org.jetbrains.annotations.NotNull;

public class DetectGoodDetailAdapter extends Adapter<ViewHolder> {

  private AppCompatActivity context;
  private List<DetectGoodDetail> list;
  private LayoutInflater layoutInflater;

  public DetectGoodDetailAdapter(AppCompatActivity context, List<DetectGoodDetail> list) {
    this.context = context;
    this.list = list;
    this.layoutInflater = LayoutInflater.from(context);
  }

  @NotNull
  @Override
  public ViewHolder onCreateViewHolder(@NotNull ViewGroup parent, int viewType) {
    View view = layoutInflater.inflate(R.layout.item_list_detect_good, parent, false);
    return new ViewHolder(view);
  }

  @Override
  public void onBindViewHolder(@NotNull ViewHolder holder, int position) {
    holder.setData(position);
  }

  @Override
  public int getItemCount() {
    return list.size();
  }

  public void updateList(List<DetectGoodDetail> list) {
    this.list = list;
    notifyDataSetChanged();
  }

  public class ViewHolder extends RecyclerView.ViewHolder {


    @BindView(R.id.detect_good_title)
    TextView titleTv;
    @BindView(R.id.detect_good_value)
    TextView valueTv;

    private DetectGoodDetail detail;

    public ViewHolder(View itemView) {
      super(itemView);
      ButterKnife.bind(this, itemView);
    }

    public void setData(int position) {
      this.detail = list.get(position);
      if (Empty.isNotEmpty(detail.getTitle())) {
        titleTv.setText(detail.getTitle().trim());
      }
      if (Empty.isNotEmpty(detail.getColumn1())) {
        valueTv.setText(NumberUtil.digitsToPersian(detail.getColumn1().trim()));
      }
    }

    @OnClick({R.id.root_layout})
    public void onViewClicked(View view) {
      switch (view.getId()) {
        case R.id.root_layout:

          break;
      }
    }
  }
}
