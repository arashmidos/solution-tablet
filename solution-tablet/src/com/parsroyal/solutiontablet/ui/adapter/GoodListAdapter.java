package com.parsroyal.solutiontablet.ui.adapter;

import android.app.Dialog;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import butterknife.BindView;
import butterknife.ButterKnife;
import com.bumptech.glide.Glide;
import com.marshalchen.ultimaterecyclerview.UltimateViewAdapter;
import com.parsroyal.solutiontablet.R;
import com.parsroyal.solutiontablet.data.entity.Goods;
import com.parsroyal.solutiontablet.ui.fragment.GoodsListFragment;
import com.parsroyal.solutiontablet.util.Empty;
import com.parsroyal.solutiontablet.util.MediaUtil;
import com.parsroyal.solutiontablet.util.NumberUtil;
import java.util.List;

/**
 * Created by Arash on 03/06/2017
 */
public class GoodListAdapter extends UltimateViewAdapter<GoodListAdapter.MyViewHolder> {

  private final boolean readOnly;
  private final boolean isRejectedGoods;
  private LayoutInflater mInflater;
  private List<Goods> goods;
  private GoodsListFragment context;

  public GoodListAdapter(GoodsListFragment context, List<Goods> goods, boolean readOnly,
      boolean isRejectedGoods) {
    this.goods = goods;
    this.context = context;
    this.mInflater = LayoutInflater.from(context.getActivity());
    this.readOnly = readOnly;
    this.isRejectedGoods = isRejectedGoods;
  }

  public void filter(List<Goods> goods) {
    this.goods = goods;
    notifyDataSetChanged();
  }

  @Override
  public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
    View view = mInflater.inflate(R.layout.row_layout_goods, parent, false);
    return new MyViewHolder(view);
  }

  @Override
  public void onBindViewHolder(MyViewHolder holder, int position) {
    Goods good = goods.get(position);
    holder.setData(good, position);
    Glide.with(context)
        .load(MediaUtil.getGoodImage(good.getBackendId()))
        .error(R.drawable.goods_default)
        .into(holder.imageView);
  }

  @Override
  public MyViewHolder newFooterHolder(View view) {
    return null;
  }

  @Override
  public MyViewHolder newHeaderHolder(View view) {
    return null;
  }

  @Override
  public MyViewHolder onCreateViewHolder(ViewGroup parent) {
    return null;
  }

  @Override
  public RecyclerView.ViewHolder onCreateHeaderViewHolder(ViewGroup parent) {
    return null;
  }

  @Override
  public void onBindHeaderViewHolder(RecyclerView.ViewHolder holder, int position) {
  }

  @Override
  public int getItemCount() {
    return goods.size();
  }

  @Override
  public int getAdapterItemCount() {
    return goods.size();
  }

  @Override
  public long generateHeaderId(int position) {
    return 0L;
  }

  class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

    @BindView(R.id.addBtn)
    ImageButton addBtn;
    @BindView(R.id.operationsContainer)
    LinearLayout operationsContainer;
    @BindView(R.id.recoveryDateTv)
    TextView recoveryDateTv;
    @BindView(R.id.unit1ExistingTv)
    TextView unit1ExistingTv;
    @BindView(R.id.goodAmountTv)
    TextView goodAmountTv;
    @BindView(R.id.goodCodeTv)
    TextView goodCodeTv;
    @BindView(R.id.goodNameTv)
    TextView goodNameTv;
    @BindView(R.id.goodsRowLayout)
    LinearLayout goodsRowLayout;
    @BindView(R.id.good_image)
    ImageView imageView;
    long id;

    Goods current;
    private int pos;

    public MyViewHolder(View view) {
      super(view);
      ButterKnife.bind(this, view);
      if (!readOnly) {
        view.setOnClickListener(this);
      }
      imageView.setOnClickListener(this);
    }

    /**
     * Called when a view has been clicked.
     *
     * @param v The view that was clicked.
     */
    @Override
    public void onClick(View v) {
      switch (v.getId()) {
        case R.id.good_image:
          Dialog settingsDialog = new Dialog(context.getActivity());
          settingsDialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
          View view = context.getActivity().getLayoutInflater()
              .inflate(R.layout.image_fullscreen_layout, null);
          Glide.with(context)
              .load(MediaUtil.getGoodImage(current.getBackendId()))
              .into((ImageView) view.findViewById(R.id.good_image));
          settingsDialog.setContentView(view);
          settingsDialog.show();
          break;
        default:
          context.handleOnGoodsItemClickListener(current);
      }
    }

    public void setData(Goods good, int position) {
      this.id = good.getBackendId();
      this.current = good;
      this.pos = position;

      goodNameTv.setText(good.getTitle());
      goodCodeTv.setText(good.getCode());
      Double goodsAmount = Double.valueOf(good.getPrice()) / 1000D;
      goodAmountTv.setText(NumberUtil.getCommaSeparated(goodsAmount) + " " +
          context.getString(R.string.common_irr_currency));

      StringBuilder existingSb = new StringBuilder();

      Double unit1Existing = Double.valueOf(good.getExisting()) / 1000D;
      existingSb.append(NumberUtil.formatDoubleWith2DecimalPlaces(unit1Existing)).append(" ")
          .append(Empty.isNotEmpty(good.getUnit1Title()) ? good.getUnit1Title() : "--");

      Long unit1Count = good.getUnit1Count();
      if (Empty.isNotEmpty(unit1Count) && !unit1Count.equals(0L)) {
        Double unit2Existing = Double.valueOf(good.getExisting()) / Double.valueOf(unit1Count);
        unit2Existing = unit2Existing / 1000D;
        existingSb.append(" ").append(NumberUtil.formatDoubleWith2DecimalPlaces(unit2Existing))
            .append(" ")
            .append(Empty.isNotEmpty(good.getUnit2Title()) ? good.getUnit2Title() : "--");
      }
      unit1ExistingTv.setText(existingSb.toString());

      if (isRejectedGoods) {
        recoveryDateTv.setVisibility(View.GONE);
      } else {
        if (Empty.isNotEmpty(good.getRecoveryDate())) {
          recoveryDateTv.setText(good.getRecoveryDate());
        }
      }
    }
  }
}
