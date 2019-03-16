package com.parsroyal.storemanagement.ui.adapter;

import android.support.annotation.NonNull;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.RecyclerView.Adapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.RelativeLayout.LayoutParams;
import android.widget.TextView;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import com.parsroyal.storemanagement.R;
import com.parsroyal.storemanagement.data.model.StockGood;
import com.parsroyal.storemanagement.ui.activity.WarehouseHandling;
import com.parsroyal.storemanagement.ui.adapter.StockGoodsAdapter.ViewHolder;
import com.parsroyal.storemanagement.ui.fragment.dialogFragment.PackerAddGoodDialogFragment;
import com.parsroyal.storemanagement.util.Empty;
import com.parsroyal.storemanagement.util.NumberUtil;
import java.util.List;

/**
 * Created by Arash on 8/4/2017.
 */

public class StockGoodsAdapter extends Adapter<ViewHolder> {

  private List<StockGood> goods;

  private LayoutInflater inflater;
  private WarehouseHandling context;

  public StockGoodsAdapter(WarehouseHandling context, List<StockGood> goods) {
    this.context = context;
    this.goods = goods;
    inflater = LayoutInflater.from(context);
  }

  @NonNull
  @Override
  public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
    View view = inflater.inflate(R.layout.item_stock_good_item, parent, false);
    return new ViewHolder(view);
  }

  @Override
  public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
    StockGood good = goods.get(position);
    try {
      holder.setData(position, good);
    } catch (Exception ex) {
      ex.printStackTrace();
    }
  }


  @Override
  public int getItemCount() {
    return goods.size();
  }

  @OnClick(R.id.good_img)
  public void onViewClicked() {
  }

  private void setMargin(boolean isLastItem, RelativeLayout layout) {
    LayoutParams parameter = new LayoutParams(
        ViewGroup.LayoutParams.MATCH_PARENT,
        ViewGroup.LayoutParams.WRAP_CONTENT);
    float scale = context.getResources().getDisplayMetrics().density;
    int paddingInPx = (int) (8 * scale + 0.5f);
    if (isLastItem) {
      parameter.setMargins(paddingInPx, paddingInPx, paddingInPx, paddingInPx);
    } else {
      parameter.setMargins(paddingInPx, paddingInPx, paddingInPx, 0);
    }
    layout.setLayoutParams(parameter);
  }

  public void update(List<StockGood> goods) {
    this.goods = goods;
    notifyDataSetChanged();
  }

  public class ViewHolder extends RecyclerView.ViewHolder {

    @BindView(R.id.good_img)
    ImageView goodImg;
    @BindView(R.id.good_status)
    ImageView goodStatus;
    @BindView(R.id.good_name_tv)
    TextView goodNameTv;
    @BindView(R.id.good_code_value)
    TextView goodCodeValueTv;
    @BindView(R.id.total_value_tv)
    TextView totalValueTv;
    @BindView(R.id.packed_value_tv)
    TextView packedValueTv;
    @BindView(R.id.remained_value_tv)
    TextView remainedValueTv;
    @BindView(R.id.remained_tv)
    TextView remainedTv;
    @BindView(R.id.main_lay)
    RelativeLayout mainLay;
    private int position;
    private StockGood good;


    public ViewHolder(View itemView) {
      super(itemView);
      ButterKnife.bind(this, itemView);

    }

    public void setData(int position, StockGood good) {
      this.position = position;
      this.good = good;
      goodNameTv.setText(NumberUtil.digitsToPersian(good.getGoodNamGLS()));
      goodCodeValueTv.setText(NumberUtil.digitsToPersian(good.getGoodCdeGLS()));

      setMargin(position == goods.size() - 1, mainLay);
      if (Empty.isNullOrZero(good.getCounted())) {
        setGoodNormal();
      } else {
        setGoodCompleted();
      }
    }

    @OnClick({R.id.good_img, R.id.main_lay})
    public void onViewClicked(View view) {
      switch (view.getId()) {
        case R.id.good_img:
          break;
        case R.id.main_lay:
          showAddDialog();
          break;
      }
    }

    public void showAddDialog() {
      FragmentTransaction ft = context.getSupportFragmentManager().beginTransaction();
      PackerAddGoodDialogFragment goodsFilterDialogFragment;
//      if (MultiScreenUtility.isTablet(context)) {
//        goodsFilterDialogFragment = PackerAddGoodBottomSheet.newInstance(good);
//      } else {
//      goodsFilterDialogFragment = PackerAddGoodDialogFragment.newInstance(good);
//      }
//      goodsFilterDialogFragment.show(ft, "add_good");
    }


    private void setGoodCompleted() {
      mainLay.setBackgroundColor(ContextCompat.getColor(context, R.color.gift));
      remainedValueTv.setVisibility(View.INVISIBLE);
      remainedTv.setVisibility(View.INVISIBLE);
      goodStatus.setVisibility(View.VISIBLE);
    }


    private void setGoodNormal() {
      mainLay.setBackgroundColor(ContextCompat.getColor(context, R.color.white));
      remainedValueTv.setVisibility(View.VISIBLE);
      remainedTv.setVisibility(View.VISIBLE);
      goodStatus.setVisibility(View.INVISIBLE);
    }
  }
}
