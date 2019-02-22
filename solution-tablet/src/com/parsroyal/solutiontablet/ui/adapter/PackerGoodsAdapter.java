package com.parsroyal.solutiontablet.ui.adapter;

import android.support.annotation.NonNull;
import android.support.v4.app.FragmentTransaction;
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
import com.parsroyal.solutiontablet.R;
import com.parsroyal.solutiontablet.data.model.GoodDetail;
import com.parsroyal.solutiontablet.data.model.SaleOrderDto;
import com.parsroyal.solutiontablet.ui.activity.PackerActivity;
import com.parsroyal.solutiontablet.ui.adapter.PackerGoodsAdapter.ViewHolder;
import com.parsroyal.solutiontablet.ui.fragment.PackerDetailFragment;
import com.parsroyal.solutiontablet.ui.fragment.bottomsheet.PackerAddGoodBottomSheet;
import com.parsroyal.solutiontablet.ui.fragment.dialogFragment.PackerAddGoodDialogFragment;
import com.parsroyal.solutiontablet.util.MultiScreenUtility;
import com.parsroyal.solutiontablet.util.NumberUtil;
import java.util.List;

/**
 * Created by Arash on 8/4/2017.
 */

public class PackerGoodsAdapter extends Adapter<ViewHolder> {

  private final PackerDetailFragment parent;

  private List<GoodDetail> details;
  private SaleOrderDto order;
  private LayoutInflater inflater;
  private PackerActivity context;

  public PackerGoodsAdapter(PackerActivity context, PackerDetailFragment detailFragment,
      List<GoodDetail> details) {
    this.context = context;
    this.parent = detailFragment;
    this.details = details;
    inflater = LayoutInflater.from(context);
  }

  @NonNull
  @Override
  public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
    View view = inflater.inflate(R.layout.item_packer_good_item, parent, false);
    return new ViewHolder(view);
  }

  @Override
  public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
    GoodDetail good = details.get(position);
    try {
      holder.setData(position, good);
    } catch (Exception ex) {
      ex.printStackTrace();
    }
  }


  @Override
  public int getItemCount() {
    return details.size();
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

  public void update(List<GoodDetail> goodDetails) {
    this.details = goodDetails;
    notifyDataSetChanged();
  }

  public class ViewHolder extends RecyclerView.ViewHolder {

    @BindView(R.id.good_img)
    ImageView goodImg;
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
    @BindView(R.id.main_lay)
    RelativeLayout mainLay;
    private int position;
    private GoodDetail good;


    public ViewHolder(View itemView) {
      super(itemView);
      ButterKnife.bind(this, itemView);

    }

    public void setData(int position, GoodDetail good) {
      this.position = position;
      this.good = good;
      goodNameTv.setText(NumberUtil.digitsToPersian(good.getGoodNameSGL()));
      goodCodeValueTv.setText(NumberUtil.digitsToPersian(good.getGoodCodeSGL()));
      totalValueTv.setText(
          String.format("%s %s", NumberUtil.digitsToPersian(good.getQty() / 1000), good.getuName())
              .trim());
      remainedValueTv.setText(String
          .format("%s %s", NumberUtil.digitsToPersian(good.getRemain() / 1000), good.getuName())
          .trim());
      packedValueTv.setText(String
          .format("%s %s", NumberUtil.digitsToPersian(good.getPacked() / 1000), good.getuName())
          .trim());
      setMargin(position == details.size() - 1, mainLay);
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
      if (MultiScreenUtility.isTablet(context)) {
        goodsFilterDialogFragment = PackerAddGoodBottomSheet.newInstance(good);
      } else {
        goodsFilterDialogFragment = PackerAddGoodDialogFragment.newInstance(good);
      }
      goodsFilterDialogFragment.show(ft, "add_good");
    }
  }
}
