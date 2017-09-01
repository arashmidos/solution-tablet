package com.parsroyal.solutiontablet.ui.adapter;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.parsroyal.solutiontablet.R;
import com.parsroyal.solutiontablet.data.listmodel.NCustomerListModel;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by ShakibIsTheBest on 09/01/2017.
 */

public class CustomerSendAdapter extends RecyclerView.Adapter<CustomerSendAdapter.ViewHolder> {

  @BindView(R.id.delete_img) ImageView deleteImg;
  @BindView(R.id.edit_img) ImageView editImg;
  @BindView(R.id.edit_lay) LinearLayout editLay;
  private LayoutInflater inflater;
  private Context context;
  private boolean isSend;
  private List<NCustomerListModel> customers;

  public CustomerSendAdapter(Context context, boolean isSend, List<NCustomerListModel> customers) {
    this.context = context;
    this.isSend = isSend;
    this.customers = customers;
    inflater = LayoutInflater.from(context);
  }

  @Override
  public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
    View view = inflater.inflate(R.layout.item_system_customer, parent, false);
    return new ViewHolder(view);
  }

  @Override
  public void onBindViewHolder(ViewHolder holder, int position) {
    NCustomerListModel customer = customers.get(position);
    if (isSend)
      editLay.setVisibility(View.GONE);
    else
      editLay.setVisibility(View.VISIBLE);
    setMargin(position == customers.size() - 1, holder.customerLay);
    holder.customerNameTv.setText(customer.getTitle());
    String customerCode = "کد : " + customer.getCode();
    holder.customerIdTv.setText(customerCode);
    holder.customerShopNameTv.setText(customer.getShopName());
    if (customer.hasLocation()) {
      holder.hasLocationImg.setImageResource(R.drawable.ic_gps_fixed_black_18dp);
      holder.hasLocationImg.setColorFilter(ContextCompat.getColor(context, R.color.primary));
    } else {
      holder.hasLocationImg.setImageResource(R.drawable.ic_gps_off_black_18dp);
      holder.hasLocationImg.setColorFilter(ContextCompat.getColor(context, R.color.login_gray));
    }
    editImg.setOnClickListener(new View.OnClickListener() {
      @Override public void onClick(View v) {
        Toast.makeText(context, "edit", Toast.LENGTH_SHORT).show();
      }
    });
    deleteImg.setOnClickListener(new View.OnClickListener() {
      @Override public void onClick(View v) {
        Toast.makeText(context, "delete", Toast.LENGTH_SHORT).show();
      }
    });
  }

  private void setMargin(boolean isLastItem, RelativeLayout layout) {
    RelativeLayout.LayoutParams parameter = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
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

  @Override
  public int getItemCount() {
    return customers.size();
  }

  public void update(List<NCustomerListModel> customers) {
    this.customers = customers;
    notifyDataSetChanged();
  }

  public class ViewHolder extends RecyclerView.ViewHolder {

    @BindView(R.id.customer_name_tv) TextView customerNameTv;
    @BindView(R.id.customer_id_tv) TextView customerIdTv;
    @BindView(R.id.customer_lay) RelativeLayout customerLay;
    @BindView(R.id.customer_shop_name_tv) TextView customerShopNameTv;
    @BindView(R.id.has_location_img) ImageView hasLocationImg;

    public ViewHolder(View itemView) {
      super(itemView);
      ButterKnife.bind(this, itemView);
    }
  }
}
