package com.parsroyal.solutiontablet.ui.adapter;

import android.content.Context;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.parsroyal.solutiontablet.R;
import com.parsroyal.solutiontablet.data.listmodel.CustomerListModel;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class CustomersAdapter extends RecyclerView.Adapter<CustomersAdapter.ViewHolder> {

  private LayoutInflater inflater;
  private Context context;
  private List<CustomerListModel> customers;

  public CustomersAdapter(Context context, List<CustomerListModel> customers) {
    this.context = context;
    this.customers = customers;
    inflater = LayoutInflater.from(context);
  }

  @Override public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
    View view = inflater.inflate(R.layout.item_customers_list, parent, false);
    return new ViewHolder(view);
  }

  @Override public void onBindViewHolder(ViewHolder holder, int position) {
    CustomerListModel model = customers.get(position);
    setMargin(position == customers.size() - 1, holder.customerLay);
    holder.customerAddressTv.setText(model.getAddress());
    holder.customerNameTv.setText(model.getTitle());
    String customerCode = "کد : " + model.getCode();
    holder.customerIdTv.setText(customerCode);
    //set location icon
    if (model.hasLocation()) {
      holder.hasLocationImg.setImageResource(R.drawable.ic_location_on_24dp);
    } else {
      holder.hasLocationImg.setImageResource(R.drawable.ic_location_off_24dp);
    }
    //set visit icon
    if (model.isVisited()) {
      holder.visitTodayImg.setImageResource(R.drawable.ic_visibility_24dp);
    } else {
      holder.visitTodayImg.setImageResource(R.drawable.ic_visibility_off_24dp);
    }
    if (model.hasRejection()) {
      holder.visitTodayImg.setImageResource(R.drawable.ic_visibility_off_none_24dp);
    }

    holder.hasOrderImg.setVisibility(model.hasOrder() ? View.VISIBLE : View.GONE);
  }

  private void setMargin(boolean isLastItem, CardView cardView) {
    CardView.LayoutParams parameter = new CardView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
    if (isLastItem)
      parameter.setMargins(8, 8, 8, 8);
    else
      parameter.setMargins(8, 8, 8, 0);
    cardView.setLayoutParams(parameter);

  }

  @Override public int getItemCount() {
    return customers.size();
  }

  public class ViewHolder extends RecyclerView.ViewHolder {
    @BindView(R.id.customer_name_tv) TextView customerNameTv;
    @BindView(R.id.customer_id_tv) TextView customerIdTv;
    @BindView(R.id.customer_address_tv) TextView customerAddressTv;
    @BindView(R.id.has_location_img) ImageView hasLocationImg;
    @BindView(R.id.visit_today_img) ImageView visitTodayImg;
    @BindView(R.id.has_order_img) ImageView hasOrderImg;
    @BindView(R.id.customer_lay) CardView customerLay;

    public ViewHolder(View itemView) {
      super(itemView);
      ButterKnife.bind(this, itemView);
    }
  }
}
