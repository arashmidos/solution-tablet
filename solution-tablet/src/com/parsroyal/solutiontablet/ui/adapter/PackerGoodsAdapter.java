package com.parsroyal.solutiontablet.ui.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import butterknife.ButterKnife;
import com.parsroyal.solutiontablet.R;
import com.parsroyal.solutiontablet.data.entity.Goods;
import com.parsroyal.solutiontablet.data.model.Packer;
import com.parsroyal.solutiontablet.data.model.SaleOrderDto;
import com.parsroyal.solutiontablet.ui.fragment.PackerDetailFragment;
import java.util.List;

/**
 * Created by Arash on 8/4/2017.
 */

public class PackerGoodsAdapter extends RecyclerView.Adapter<PackerGoodsAdapter.ViewHolder> {

  private SaleOrderDto order;
  private LayoutInflater inflater;
  private Context context;
  private List<Goods> goodsList;

  public PackerGoodsAdapter(Context context, PackerDetailFragment detailFragment, Packer packer) {
    this.context = context;

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
    /*Goods good = goodsList.get(position);
    try {
      holder.setData(position, good);
    } catch (Exception ex) {
      Logger.sendError("Goods Adapter", ex.getMessage());
    }*/
  }


  @Override
  public int getItemCount() {
    return 2;
  }


  public class ViewHolder extends RecyclerView.ViewHolder implements OnClickListener {

    private int position;


    public ViewHolder(View itemView) {
      super(itemView);
      ButterKnife.bind(this, itemView);

    }

    public void setData(int position, Goods good) {
      this.position = position;

    }

    @Override
    public void onClick(View v) {
      switch (v.getId()) {
        case R.id.good_img:

          break;
      }
    }
  }
}
