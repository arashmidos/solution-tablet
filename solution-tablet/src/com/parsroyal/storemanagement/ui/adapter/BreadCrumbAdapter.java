package com.parsroyal.storemanagement.ui.adapter;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.RecyclerView.Adapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import butterknife.BindView;
import butterknife.ButterKnife;
import com.parsroyal.storemanagement.R;
import com.parsroyal.storemanagement.data.entity.GoodsGroup;
import com.parsroyal.storemanagement.ui.adapter.BreadCrumbAdapter.ViewHolder;
import com.parsroyal.storemanagement.ui.fragment.OrderFragment;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by shkbhbb on 1/17/18.
 */

public class BreadCrumbAdapter extends Adapter<ViewHolder> {

  private List<GoodsGroup> goodsGroups = new ArrayList<>();
  private Context context;
  private LayoutInflater inflater;
  private OrderFragment orderFragment;

  public BreadCrumbAdapter(
      List<GoodsGroup> goodsGroups, Context context, OrderFragment orderFragment) {
    this.goodsGroups = goodsGroups;
    this.context = context;
    this.orderFragment = orderFragment;
    this.inflater = LayoutInflater.from(context);
  }

  @Override
  public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
    View view = inflater.inflate(R.layout.item_bread_crumb, parent, false);
    return new ViewHolder(view);
  }

  @Override
  public void onBindViewHolder(ViewHolder holder, int position) {
    holder.setData(position);
  }

  @Override
  public int getItemCount() {
    return goodsGroups.size();
  }

  public void update(List<GoodsGroup> breadCrumbList) {
    this.goodsGroups = breadCrumbList;
    notifyDataSetChanged();
  }

  public class ViewHolder extends RecyclerView.ViewHolder implements OnClickListener {

    @BindView(R.id.title_tv)
    TextView titleTv;
    @BindView(R.id.arrow_img)
    ImageView arrowImg;

    private GoodsGroup goodsGroup;
    private int position;

    public ViewHolder(View itemView) {
      super(itemView);
      ButterKnife.bind(this, itemView);
      titleTv.setOnClickListener(this);
    }

    public void setData(int position) {
      this.goodsGroup = goodsGroups.get(position);
      this.position = position;
      titleTv.setText(goodsGroup.getTitle());
      if (position == 0) {
        arrowImg.setVisibility(View.GONE);
      } else {
        arrowImg.setVisibility(View.VISIBLE);
      }
      if (position == goodsGroups.size() - 1) {
        titleTv.setBackgroundResource(R.drawable.rec_border);
      } else {
        titleTv.setBackgroundColor(ContextCompat.getColor(context, android.R.color.transparent));
      }
    }

    @Override
    public void onClick(View view) {
      switch (view.getId()) {
        case R.id.title_tv:
          if (position != goodsGroups.size() - 1) {
            orderFragment.onBreadCrumbClicked(goodsGroup);
          }
          break;
      }
    }
  }
}
