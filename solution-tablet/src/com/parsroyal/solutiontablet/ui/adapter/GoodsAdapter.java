package com.parsroyal.solutiontablet.ui.adapter;

import android.app.Dialog;
import android.content.Context;
import android.support.v4.app.FragmentTransaction;
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
import com.parsroyal.solutiontablet.ui.fragment.AddOrderDialogFragment;
import com.parsroyal.solutiontablet.util.Empty;
import com.parsroyal.solutiontablet.util.MediaUtil;
import com.parsroyal.solutiontablet.util.NumberUtil;
import java.util.List;

/**
 * Created by ShakibIsTheBest on 8/4/2017.
 */

public class GoodsAdapter extends RecyclerView.Adapter<GoodsAdapter.ViewHolder> {

  private final boolean readOnly;
  private LayoutInflater inflater;
  private Context context;
  private List<Goods> goodsList;

  public GoodsAdapter(Context context, List<Goods> goodsList, boolean readOnly) {
    this.context = context;
    this.goodsList = goodsList;
    this.readOnly = readOnly;
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
    holder.setData(position, good);
  }

  private void showOrderDialog() {
    FragmentTransaction ft = ((MainActivity) context).getSupportFragmentManager()
        .beginTransaction();
    AddOrderDialogFragment addOrderDialogFragment = AddOrderDialogFragment.newInstance();
    addOrderDialogFragment.show(ft, "order");
  }

  @Override
  public int getItemCount() {
    return goodsList.size();
  }

  public void update(List<Goods> goodsList) {
    this.goodsList = goodsList;
    notifyDataSetChanged();
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
          .error(R.drawable.goods_default)
          .into(goodImg);
      goodNameTv.setText(good.getTitle());
      goodCodeTv.setText(String.format(context.getString(R.string.good_code_x), good.getCode()));

      //set existings
      Double unit1Existing = Double.valueOf(good.getExisting()) / 1000D;
      goodNumberTv.setText(String.format("%s %s",
          NumberUtil.formatDoubleWith2DecimalPlaces(unit1Existing),
          (Empty.isNotEmpty(good.getUnit1Title()) ? good.getUnit1Title() : "--")));

      Long unit1Count = good.getUnit1Count();
      if (Empty.isNotEmpty(unit1Count) && !unit1Count.equals(0L)) {
        Double unit2Existing = Double.valueOf(good.getExisting()) / Double.valueOf(unit1Count);
        unit2Existing = unit2Existing / 1000D;

        goodBoxTv.setText(String
            .format("%s %s", NumberUtil.formatDoubleWith2DecimalPlaces(unit2Existing),
                (Empty.isNotEmpty(good.getUnit2Title()) ? good.getUnit2Title() : "--")));
      }
      //

      //set price
      Double goodsAmount = Double.valueOf(good.getPrice()) / 1000D;
      goodPriceTv.setText(NumberUtil.getCommaSeparated(goodsAmount) + " " +
          context.getString(R.string.common_irr_currency));

      //TODO: If its rejectGoods hide this
      recoveryDateTv.setText(
          String.format(context.getString(R.string.recovery_date_x), good.getRecoveryDate()));


//      mainLay.setOnClickListener(v -> showOrderDialog());
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
              .into((ImageView) view.findViewById(R.id.good_image));
          settingsDialog.setContentView(view);
          settingsDialog.show();
          break;
      }
    }
  }
}
