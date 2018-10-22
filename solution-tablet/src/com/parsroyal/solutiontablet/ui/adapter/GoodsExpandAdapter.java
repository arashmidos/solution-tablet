package com.parsroyal.solutiontablet.ui.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import com.parsroyal.solutiontablet.R;
import com.parsroyal.solutiontablet.data.entity.GoodsGroup;
import com.parsroyal.solutiontablet.service.GoodsService;
import com.parsroyal.solutiontablet.service.impl.GoodsServiceImpl;
import com.parsroyal.solutiontablet.ui.MainActivity;
import com.parsroyal.solutiontablet.ui.adapter.GoodsExpandAdapter.ChildViewHolder;
import com.parsroyal.solutiontablet.ui.adapter.GoodsExpandAdapter.ParentViewHolder;
import com.parsroyal.solutiontablet.ui.fragment.OrderFragment;
import com.thoughtbot.expandablerecyclerview.ExpandableRecyclerViewAdapter;
import com.thoughtbot.expandablerecyclerview.models.ExpandableGroup;
import com.thoughtbot.expandablerecyclerview.viewholders.GroupViewHolder;
import java.util.HashMap;
import java.util.List;

/**
 * Created by shkbhbb on 1/13/18.
 */

public class GoodsExpandAdapter extends
    ExpandableRecyclerViewAdapter<ParentViewHolder, ChildViewHolder> {

  private final HashMap<String, Long> titleIdes;
  private Context context;
  private LayoutInflater inflater;
  private OrderFragment orderFragment;
  private GoodsService goodsService;

  public GoodsExpandAdapter(Context context, List<? extends ExpandableGroup> groups,
      HashMap<String, Long> titleIdes, OrderFragment orderFragment) {
    super(groups);
    this.context = context;
    this.inflater = LayoutInflater.from(context);
    this.orderFragment = orderFragment;
    this.titleIdes=titleIdes;
    this.goodsService = new GoodsServiceImpl(context);
  }

  @Override
  public ParentViewHolder onCreateGroupViewHolder(ViewGroup parent, int viewType) {
    View view = inflater.inflate(R.layout.item_goods_expand_parent, parent, false);
    return new ParentViewHolder(view);
  }

  @Override
  public ChildViewHolder onCreateChildViewHolder(ViewGroup parent, int viewType) {
    View view = inflater.inflate(R.layout.item_goods_expand_child, parent, false);
    return new ChildViewHolder(view);
  }

  @Override
  public void onBindChildViewHolder(ChildViewHolder holder, int flatPosition, ExpandableGroup group,
      int childIndex) {
    holder.setData(group, childIndex);

  }

  @Override
  public void onBindGroupViewHolder(ParentViewHolder holder, int flatPosition,
      ExpandableGroup group) {
    holder.setData(group, flatPosition);
  }

  public class ChildViewHolder extends
      com.thoughtbot.expandablerecyclerview.viewholders.ChildViewHolder implements OnClickListener {

    @BindView(R.id.title_tv)
    TextView titleTv;

    private ExpandableGroup expandableGroup;
    private int position;
    private GoodsGroup goodsGroup;

    public ChildViewHolder(View itemView) {
      super(itemView);
      ButterKnife.bind(this, itemView);
      titleTv.setOnClickListener(this);
    }

    public void setData(ExpandableGroup group, int childIndex) {
      this.expandableGroup = group;
      this.position = childIndex;
      this.goodsGroup = (GoodsGroup) group.getItems().get(childIndex);
      titleTv.setText(goodsGroup.getTitle());
    }

    @Override
    public void onClick(View view) {
      switch (view.getId()) {
        case R.id.title_tv:
          if (goodsService.getChilds(goodsGroup.getBackendId()).size() > 0) {
            orderFragment
                .setUpCategoryRecyclerView(goodsService.getChilds(goodsGroup.getBackendId()));
          } else {
            orderFragment.setUpGoodsRecyclerView(goodsGroup.getBackendId());
          }
          break;
      }
    }
  }

  public class ParentViewHolder extends GroupViewHolder {

    @BindView(R.id.title_tv)
    TextView titleTv;
    @BindView(R.id.parent_lay)
    LinearLayout parentLay;
    @BindView(R.id.category_tv)
    TextView categoryTv;
    @BindView(R.id.icon_img)
    ImageView iconImg;

    private int position;
    private ExpandableGroup expandableGroup;

    public ParentViewHolder(View itemView) {
      super(itemView);
      ButterKnife.bind(this, itemView);

    }

    @Override
    public void onClick(View v) {
      if (position == 0) {
        orderFragment.setUpGoodsRecyclerView(-1L);
      }else if (expandableGroup.getItemCount() == 0) {
        orderFragment.setUpGoodsRecyclerView(titleIdes.get(expandableGroup.getTitle()));
      }
        super.onClick(v);
    }

    @Override
    public void expand() {
      if (expandableGroup.getItems() != null && expandableGroup.getItems().size() != 0) {
        iconImg.setImageResource(R.drawable.ic_arrow_expanded);
      }
      super.expand();
    }

    @Override
    public void collapse() {
      if (expandableGroup.getItems() != null && expandableGroup.getItems().size() != 0) {
        iconImg.setImageResource(R.drawable.ic_arrow_down_24_dp);
      }
      super.collapse();
    }

    public void setData(ExpandableGroup group, int flatPosition) {
      this.position = flatPosition;
      this.expandableGroup = group;
      if (position == 1) {
        categoryTv.setVisibility(View.VISIBLE);
      } else {
        categoryTv.setVisibility(View.GONE);
      }
      if (group.getItems() == null || group.getItems().size() == 0) {
        iconImg.setImageResource(R.drawable.ic_keyboard_arrow_left);
      }
      titleTv.setText(group.getTitle());
    }
  }
}
