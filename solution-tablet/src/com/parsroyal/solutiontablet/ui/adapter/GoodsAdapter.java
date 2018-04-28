package com.parsroyal.solutiontablet.ui.adapter;

import android.app.Dialog;
import android.content.Context;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import butterknife.BindView;
import butterknife.ButterKnife;
import com.bumptech.glide.Glide;
import com.parsroyal.solutiontablet.R;
import com.parsroyal.solutiontablet.data.entity.Goods;
import com.parsroyal.solutiontablet.ui.MainActivity;
import com.parsroyal.solutiontablet.ui.fragment.OrderFragment;
import com.parsroyal.solutiontablet.util.Empty;
import com.parsroyal.solutiontablet.util.Logger;
import com.parsroyal.solutiontablet.util.MediaUtil;
import com.parsroyal.solutiontablet.util.NumberUtil;
import java.util.List;
import java.util.Locale;

/**
 * Created by ShakibIsTheBest on 8/4/2017.
 */

public class GoodsAdapter extends RecyclerView.Adapter<GoodsAdapter.ViewHolder> {

  private final boolean readOnly;
  private final boolean isRejectedGoods;
  private final OrderFragment parent;
  private LayoutInflater inflater;
  private Context context;
  private List<Goods> goodsList;

  public GoodsAdapter(Context context, OrderFragment orderFragment, List<Goods> goodsList,
      boolean readOnly, boolean isRejectedGoods) {
    this.context = context;
    this.goodsList = goodsList;
    this.readOnly = readOnly;
    this.isRejectedGoods = isRejectedGoods;
    this.parent = orderFragment;
    inflater = LayoutInflater.from(context);
  }

  @Override
  public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
    View view = inflater.inflate(R.layout.item_goods_list, parent, false);
    return new ViewHolder(view);
  }

  @Override
  public void onBindViewHolder(ViewHolder holder, int position) {
    Goods good = goodsList.get(position);
    try {
      holder.setData(position, good);
    } catch (Exception ex) {
      Logger.sendError("Goods Adapter", ex.getMessage());
    }
  }


  @Override
  public int getItemCount() {
    return goodsList.size();
  }

  public void update(List<Goods> goodsList) {
    this.goodsList = goodsList;
    notifyDataSetChanged();
  }

  public Goods getItemByPosition(int pos) {
    if (pos < goodsList.size()) {
      return goodsList.get(pos);
    }
    return null;
  }

  public class ViewHolder extends RecyclerView.ViewHolder implements OnClickListener {

    @BindView(R.id.good_img)
    ImageView goodImg;
    @BindView(R.id.good_name_tv)
    TextView goodNameTv;
    @BindView(R.id.good_code_tv)
    TextView goodCodeTv;
    @BindView(R.id.good_number_tv)
    TextView goodNumberTv;
    @BindView(R.id.good_box_tv)
    TextView goodBoxTv;
    @BindView(R.id.good_price_tv)
    TextView goodPriceTv;
    @BindView(R.id.good_recovery_date_tv)
    TextView recoveryDateTv;
    @BindView(R.id.main_lay)
    RelativeLayout mainLay;
    @BindView(R.id.good_customer_price_tv)
    TextView goodsCustomerPrice;
    @BindView(R.id.good_customer_price_title)
    TextView goodCustomerPriceTitle;
    @Nullable
    @BindView(R.id.good_currency)
    TextView goodsCurrency;
    private int position;
    private Goods good;


    public ViewHolder(View itemView) {
      super(itemView);
      ButterKnife.bind(this, itemView);
      if (!readOnly) {
        mainLay.setOnClickListener(this);
      } else {
        goodImg.setOnClickListener(this);
      }
    }

    public void setData(int position, Goods good) {
      this.position = position;
      this.good = good;
      Glide.with(context)
          .load(MediaUtil.getGoodImage(good.getCode()))
          .error(Glide.with(context).load(R.drawable.goods_default))
          .into(goodImg);
      goodNameTv.setText(NumberUtil.digitsToPersian(good.getTitle()));
      goodCodeTv.setText(
          String.format(Locale.getDefault(), "%s %s", context.getString(R.string.code),
              NumberUtil.digitsToPersian(good.getCode())));

      //set existings
      Double unit1Existing = Double.valueOf(good.getExisting()) / 1000D;
      goodNumberTv.setText(NumberUtil.digitsToPersian(String.format(Locale.getDefault(), "%s %s",
          NumberUtil.formatDoubleWith2DecimalPlaces(unit1Existing),
          (Empty.isNotEmpty(good.getUnit1Title()) ? good.getUnit1Title() : "--"))));

      Long unit1Count = good.getUnit1Count();
      if (Empty.isNotEmpty(unit1Count) && !unit1Count.equals(0L)) {
        Double unit2Existing = Double.valueOf(good.getExisting()) / Double.valueOf(unit1Count);
        unit2Existing = unit2Existing / 1000D;

        goodBoxTv.setText(NumberUtil.digitsToPersian(String.format(Locale.getDefault(), "%s %s",
            NumberUtil.formatDoubleWith2DecimalPlaces(unit2Existing),
            (Empty.isNotEmpty(good.getUnit2Title()) ? good.getUnit2Title() : "--"))));
      }
      //

      //set price
      Double goodsAmount = Double.valueOf(good.getPrice()) / 1000D;
      goodPriceTv.setText(NumberUtil.digitsToPersian(
          String.format(Locale.getDefault(), "%s %s", NumberUtil.getCommaSeparated(goodsAmount),
              context.getString(R.string.common_irr_currency))));
      if (Empty.isNotEmpty(good.getCustomerPrice())) {

        Double goodsCustomerAmount = Double.valueOf(good.getCustomerPrice()) / 1000D;
        goodsCustomerPrice.setText(NumberUtil.digitsToPersian(String
            .format(Locale.getDefault(), "%s", NumberUtil.getCommaSeparated(goodsCustomerAmount))));
        if (Empty.isNotEmpty(goodsCurrency)) {
          goodsCurrency.setText(R.string.common_irr_currency);
        }
      }

      if (isRejectedGoods) {
        recoveryDateTv.setVisibility(View.GONE);
        goodsCustomerPrice.setVisibility(View.INVISIBLE);
        goodCustomerPriceTitle.setVisibility(View.INVISIBLE);
        if (Empty.isNotEmpty(goodsCurrency)) {
          goodsCurrency.setVisibility(View.INVISIBLE);
        }
      } else {
        recoveryDateTv.setText(String.format(NumberUtil.digitsToPersian(good.getRecoveryDate())));
      }
    }

    @Override
    public void onClick(View v) {
      switch (v.getId()) {
        case R.id.good_img:
          Dialog settingsDialog = new Dialog(context);
          settingsDialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
          View view = ((MainActivity) context).getLayoutInflater()
              .inflate(R.layout.image_fullscreen_layout, null);
          Glide.with(context)
              .load(MediaUtil.getGoodImage(good.getCode()))
              .error(Glide.with(context).load(R.drawable.goods_default))
              .into((ImageView) view.findViewById(R.id.good_image));
          settingsDialog.setContentView(view);
          settingsDialog.show();
          break;
        case R.id.main_lay:
          parent.showOrderDialog(good);
          break;
      }
    }
  }
}
