package com.parsroyal.solutiontablet.ui.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.RecyclerView.Adapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import butterknife.BindView;
import butterknife.ButterKnife;
import com.bumptech.glide.Glide;
import com.parsroyal.solutiontablet.R;
import com.parsroyal.solutiontablet.data.model.SaleOrderItemDto;
import com.parsroyal.solutiontablet.ui.adapter.OrderFinalizeAdapter.ViewHolder;
import com.parsroyal.solutiontablet.util.MediaUtil;
import com.parsroyal.solutiontablet.util.NumberUtil;
import java.util.List;

/**
 * Created by ShakibIsTheBest on 8/4/2017.
 */

public class OrderFinalizeAdapter extends Adapter<ViewHolder> {

  private LayoutInflater inflater;
  private Context context;
  private List<SaleOrderItemDto> orderItems;

  public OrderFinalizeAdapter(Context context, List<SaleOrderItemDto> orderItems) {
    this.context = context;
    this.orderItems = orderItems;
    inflater = LayoutInflater.from(context);
  }

  @Override
  public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
    View view = inflater.inflate(R.layout.item_order_list_finilize, parent, false);
    return new ViewHolder(view);
  }

  @Override
  public void onBindViewHolder(ViewHolder holder, int position) {
    SaleOrderItemDto item = orderItems.get(position);
    holder.setData(item);
    holder.deleteImg.setOnClickListener(//TODO:
        v -> Toast.makeText(context, "delete", Toast.LENGTH_SHORT).show());
    holder.editImg.setOnClickListener(
        v -> Toast.makeText(context, "edit", Toast.LENGTH_SHORT).show());
  }


  @Override
  public int getItemCount() {
    return orderItems.size();
  }

  public void update(List<SaleOrderItemDto> orderItems) {
    this.orderItems = orderItems;
    notifyDataSetChanged();
  }

  public class ViewHolder extends RecyclerView.ViewHolder {

    @BindView(R.id.good_img)
    ImageView goodImg;
    @BindView(R.id.good_title_tv)
    TextView goodTitleTv;
    @BindView(R.id.amount_tv)
    TextView amountTv;
    @BindView(R.id.total_amount_tv)
    TextView totalAmountTv;
    @BindView(R.id.delete_img)
    ImageView deleteImg;
    @BindView(R.id.edit_img)
    ImageView editImg;
    @BindView(R.id.count_tv)
    TextView countTv;
    @BindView(R.id.unit2_count_tv)
    TextView unit2CountTv;
    @BindView(R.id.unit1_title_tv)
    TextView unit1TitleTv;
    @BindView(R.id.unit2_title_tv)
    TextView unit2TitleTv;

    public ViewHolder(View itemView) {
      super(itemView);
      ButterKnife.bind(this, itemView);
    }

    public void setData(SaleOrderItemDto item) {
      Glide.with(context)
          .load(MediaUtil.getGoodImage(item.getGoods().getCode()))
          .error(R.drawable.goods_default)
          .into(goodImg);
      goodTitleTv.setText(item.getGoods().getTitle());
      Double goodsAmount = Double.valueOf(item.getGoods().getPrice()) / 1000D;
      amountTv.setText(NumberUtil.getCommaSeparated(goodsAmount) + " " +
          context.getString(R.string.common_irr_currency));
      Double totalAmount = Double.valueOf(item.getAmount()) / 1000D;
      totalAmountTv.setText(NumberUtil.getCommaSeparated(totalAmount) + " " +
          context.getString(R.string.common_irr_currency));
      countTv.setText(String.valueOf(item.getGoodsCount() / 1000));
      unit2CountTv.setText(String.valueOf(item.getGoodsUnit2Count() / 1000));
      unit1TitleTv.setText(item.getGoods().getUnit1Title());
      unit2TitleTv.setText(item.getGoods().getUnit2Title());
    }
  }
}
