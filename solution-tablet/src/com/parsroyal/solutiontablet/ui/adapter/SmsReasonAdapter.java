package com.parsroyal.solutiontablet.ui.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.RecyclerView.Adapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import com.parsroyal.solutiontablet.R;
import com.parsroyal.solutiontablet.data.model.LabelValue;
import com.parsroyal.solutiontablet.ui.adapter.SmsReasonAdapter.ViewHolder;
import java.util.List;

/**
 * Created by shkbhbb on 9/17/18.
 */

public class SmsReasonAdapter extends Adapter<ViewHolder> {

  private List<LabelValue> list;
  private Context context;
  private LayoutInflater inflater;
  private long selectedItem;
  private int selectedPosition;

  public SmsReasonAdapter(Context context, List<LabelValue> list) {
    this.context = context;
    this.list = list;
    selectedItem = 0;
    this.inflater = LayoutInflater.from(context);
  }

  @NonNull
  @Override
  public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
    View view = inflater.inflate(R.layout.item_sms_reason, parent, false);
    return new ViewHolder(view);
  }

  @Override
  public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
    holder.setData(position);
  }

  @Override
  public int getItemCount() {
    return list.size();
  }

  public long getSelectedItem() {
    return selectedItem;
  }

  public class ViewHolder extends RecyclerView.ViewHolder {

    @BindView(R.id.check_iv)
    ImageView checkIv;
    @BindView(R.id.reason_tv)
    TextView reasonTv;
    @BindView(R.id.main_lay)
    RelativeLayout mainLay;

    private int pos;
    private LabelValue item;

    public ViewHolder(View itemView) {
      super(itemView);
      ButterKnife.bind(this, itemView);
    }

    public void setData(int position) {
      this.pos = position;
      this.item = list.get(position);
      reasonTv.setText(item.getLabel());
      if (position == selectedPosition) {
        checkIv.setVisibility(View.VISIBLE);
        mainLay.setBackgroundColor(ContextCompat.getColor(context, R.color.white));
      } else {
        checkIv.setVisibility(View.INVISIBLE);
        mainLay.setBackgroundColor(ContextCompat.getColor(context, R.color.white_fa));
      }
    }

    @OnClick(R.id.main_lay)
    public void onViewClicked() {
      selectedPosition=pos;
      selectedItem = item.getValue();
      notifyDataSetChanged();
    }
  }
}
