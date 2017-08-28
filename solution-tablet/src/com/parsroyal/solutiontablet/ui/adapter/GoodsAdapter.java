package com.parsroyal.solutiontablet.ui.adapter;

import android.content.Context;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.parsroyal.solutiontablet.R;
import com.parsroyal.solutiontablet.data.entity.Goods;
import com.parsroyal.solutiontablet.ui.MainActivity;
import com.parsroyal.solutiontablet.ui.fragment.dialogFragment.AddOrderDialogFragment;
import com.parsroyal.solutiontablet.util.MediaUtil;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by ShakibIsTheBest on 8/4/2017.
 */

public class GoodsAdapter extends RecyclerView.Adapter<GoodsAdapter.ViewHolder> {

  private LayoutInflater inflater;
  private Context context;
  private List<Goods> goodsList;

  public GoodsAdapter(Context context, List<Goods> goodsList) {
    this.context = context;
    this.goodsList = goodsList;
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
    Glide.with(context)
        .load(MediaUtil.getGoodImage(good.getCode()))
        .error(R.drawable.goods_default)
        .into(holder.goodImg);
    holder.goodNameTv.setText(good.getTitle());
    String code = "محصول : " + good.getCode();
    String number = "عدد " + String.valueOf(good.getExisting());
    holder.goodCodeTv.setText(code);
    holder.goodNumberTv.setText(number);
    holder.goodBoxTv.setText("30 کارتن");
    holder.goodPriceTv.setText("12000 تومان");
    holder.goodPaymentTv.setText("راس چرداخت : 1");
    holder.mainLay.setOnClickListener(new View.OnClickListener() {
      @Override public void onClick(View v) {
        showOrderDialog();
      }
    });
  }

  private void showOrderDialog() {
    FragmentTransaction ft = ((MainActivity) context).getSupportFragmentManager().beginTransaction();
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

  public class ViewHolder extends RecyclerView.ViewHolder {

    @BindView(R.id.good_img) ImageView goodImg;
    @BindView(R.id.good_name_tv) TextView goodNameTv;
    @BindView(R.id.good_code_tv) TextView goodCodeTv;
    @BindView(R.id.good_number_tv) TextView goodNumberTv;
    @BindView(R.id.good_box_tv) TextView goodBoxTv;
    @BindView(R.id.good_price_tv) TextView goodPriceTv;
    @BindView(R.id.good_payment_tv) TextView goodPaymentTv;
    @BindView(R.id.main_lay) RelativeLayout mainLay;


    public ViewHolder(View itemView) {
      super(itemView);
      ButterKnife.bind(this, itemView);
    }
  }
}
