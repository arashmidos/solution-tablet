package com.parsroyal.solutiontablet.ui.adapter;

import android.content.Context;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.RecyclerView.Adapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import butterknife.BindView;
import butterknife.ButterKnife;
import com.bumptech.glide.Glide;
import com.parsroyal.solutiontablet.R;
import com.parsroyal.solutiontablet.constants.Constants;
import com.parsroyal.solutiontablet.ui.adapter.NewCustomerPictureAdapter.ViewHolder;
import com.parsroyal.solutiontablet.ui.fragment.AddCustomerFragment;
import java.util.List;

/**
 * Created by Arash on 1/1/2018
 */

public class NewCustomerPictureAdapter extends Adapter<ViewHolder> {

  private final AddCustomerFragment parent;
  private List<String> customerPics;
  private Context context;
  private LayoutInflater inflater;
  private boolean maxReached;

  public NewCustomerPictureAdapter(Context context, List<String> customerPics,
      AddCustomerFragment addCustomerFragment) {
    this.parent = addCustomerFragment;
    this.customerPics = customerPics;
    this.context = context;
    inflater = LayoutInflater.from(context);
    maxReached = customerPics.size() == Constants.MAX_NEW_CUSTOMER_PHOTO;
  }

  public boolean isMaxReached() {
    return maxReached;
  }

  @Override
  public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
    View view;
    if (viewType == ItemType.BUTTON.value) {
      view = inflater.inflate(R.layout.item_new_customer_button, parent, false);
    } else {
      view = inflater.inflate(R.layout.item_new_customer_pic_list, parent, false);
    }
    return new ViewHolder(view);
  }

  @Override
  public int getItemViewType(int position) {
    return position == 0 && !maxReached ? ItemType.BUTTON.value : ItemType.IMAGE.value;
  }

  @Override
  public void onBindViewHolder(ViewHolder holder, int position) {
    if (position == 0 && !maxReached) {
      holder.createPhotoLayout.setOnClickListener(view -> {
        parent.takePhoto();
      });
    } else {
      holder.setData(maxReached ? position : position - 1);
    }
  }

  @Override
  public int getItemCount() {
    return maxReached ? customerPics.size() : customerPics.size() + 1;
  }

  public void updateList(List<String> customerPics) {
    this.customerPics = customerPics;
    notifyDataSetChanged();
  }

  public void add(String newImage) {
    customerPics.add(newImage);
    if (customerPics.size() == Constants.MAX_NEW_CUSTOMER_PHOTO) {
      maxReached = true;
    }
    notifyDataSetChanged();
  }

  public List<String> getCustomerPics() {
    return customerPics;
  }

  enum ItemType {
    BUTTON(0), IMAGE(1);

    private final int value;

    ItemType(int value) {
      this.value = value;
    }
  }

  public class ViewHolder extends RecyclerView.ViewHolder {

    @Nullable
    @BindView(R.id.pic_img)
    ImageView picImg;
    @Nullable
    @BindView(R.id.create_photo_layout)
    RelativeLayout createPhotoLayout;

    private String customerPic;

    public ViewHolder(View itemView) {
      super(itemView);
      ButterKnife.bind(this, itemView);
    }

    public void setData(int position) {
      this.customerPic = customerPics.get(position);
      Glide.with(context)
          .load(customerPic)
          .error(Glide.with(context).load(R.drawable.goods_default))
          .into(picImg);
    }
  }
}
