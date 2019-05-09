package com.parsroyal.solutiontablet.ui.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.RecyclerView.Adapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.TextView;
import butterknife.BindView;
import butterknife.ButterKnife;
import com.parsroyal.solutiontablet.R;
import com.parsroyal.solutiontablet.data.entity.GoodsGroup;
import com.parsroyal.solutiontablet.service.GoodsService;
import com.parsroyal.solutiontablet.service.impl.GoodsServiceImpl;
import com.parsroyal.solutiontablet.ui.adapter.GoodsCategoryAdapter.ViewHolder;
import com.parsroyal.solutiontablet.ui.fragment.OrderFragment;
import java.util.List;

/**
 * Created by shkbhbb on 1/13/18.
 */

public class GoodsCategoryAdapter extends Adapter<ViewHolder> {

  private List<GoodsGroup> goodsGroups;
  private Context context;
  private LayoutInflater inflater;
  private OrderFragment orderFragment;
  private GoodsService goodsService;

  public GoodsCategoryAdapter(
      List<GoodsGroup> goodsGroups, Context context, OrderFragment orderFragment) {
    this.goodsGroups = goodsGroups;
    this.context = context;
    this.orderFragment = orderFragment;
    this.goodsService = new GoodsServiceImpl(context);
    this.inflater = LayoutInflater.from(context);
  }

  @Override
  public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
    View view = inflater.inflate(R.layout.list_item_goods_category, parent, false);
    return new ViewHolder(view);
  }

  @Override
  public void onBindViewHolder(ViewHolder holder, int position) {
    holder.setData(position);
  }

  public GoodsGroup getItemByPosition(int pos) {
    if (pos < goodsGroups.size()) {
      return goodsGroups.get(pos);
    }
    return null;
  }

  @Override
  public int getItemCount() {
    return goodsGroups.size();
  }

  public void updateAll(List<GoodsGroup> goodsGroups) {
    this.goodsGroups = goodsGroups;
    notifyDataSetChanged();
  }

  public class ViewHolder extends RecyclerView.ViewHolder implements OnClickListener {

    @BindView(R.id.title_tv)
    TextView titleTv;

    private GoodsGroup goodsGroup;

    public ViewHolder(View itemView) {
      super(itemView);
      ButterKnife.bind(this, itemView);
      titleTv.setOnClickListener(this);
    }

    public void setData(int position) {
      goodsGroup = goodsGroups.get(position);
      titleTv.setText(goodsGroup.getTitle());
    }

    @Override
    public void onClick(View view) {
      switch (view.getId()) {
        case R.id.title_tv:
          if (goodsService.getChilds(goodsGroup.getBackendId()) == null
              || goodsService.getChilds(goodsGroup.getBackendId()).size() == 0) {
            orderFragment.setUpGoodsRecyclerView(goodsGroup.getBackendId());
          } else {
            orderFragment
                .setUpCategoryRecyclerView(goodsService.getChilds(goodsGroup.getBackendId()));
          }
          break;

      }
    }
  }
}
