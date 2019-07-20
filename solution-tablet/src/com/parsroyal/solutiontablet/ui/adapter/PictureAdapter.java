package com.parsroyal.solutiontablet.ui.adapter;

import android.content.Context;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.RecyclerView.Adapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import butterknife.BindView;
import butterknife.ButterKnife;
import com.bumptech.glide.Glide;
import com.parsroyal.solutiontablet.R;
import com.parsroyal.solutiontablet.data.entity.CustomerPic;
import com.parsroyal.solutiontablet.ui.adapter.PictureAdapter.ViewHolder;
import java.util.List;

/**
 * Created by shkbhbb on 9/21/17.
 */

public class PictureAdapter extends Adapter<ViewHolder> {

  private List<CustomerPic> customerPics;
  private Context context;
  private LayoutInflater inflater;

  public PictureAdapter(Context context, List<CustomerPic> customerPics) {
    this.customerPics = customerPics;
    this.context = context;
    inflater = LayoutInflater.from(context);
  }

  @Override
  public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
    View view = inflater.inflate(R.layout.item_pic_list, parent, false);
    return new ViewHolder(view);
  }

  @Override
  public void onBindViewHolder(ViewHolder holder, int position) {
    holder.setData(customerPics.get(position));
  }

  @Override
  public int getItemCount() {
    return customerPics.size();
  }

  public void updateList(List<CustomerPic> customerPics) {
    this.customerPics = customerPics;
    notifyDataSetChanged();
  }

  public class ViewHolder extends RecyclerView.ViewHolder {

    @BindView(R.id.pic_img)
    ImageView picImg;

    private CustomerPic customerPic;

    public ViewHolder(View itemView) {
      super(itemView);
      ButterKnife.bind(this, itemView);
    }

    public void setData(CustomerPic customerPic) {
      this.customerPic = customerPic;
      Glide.with(context)
          .load(customerPic.getTitle())
          .error(Glide.with(context).load(R.drawable.goods_default))
          .into(picImg);
    }
  }
}
