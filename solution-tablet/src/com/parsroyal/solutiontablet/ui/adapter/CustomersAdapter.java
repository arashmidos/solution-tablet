package com.parsroyal.solutiontablet.ui.adapter;

import android.content.Context;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.parsroyal.solutiontablet.R;
import com.parsroyal.solutiontablet.data.listmodel.CustomerListModel;
import com.parsroyal.solutiontablet.ui.FeedActivity;

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
    //TODO:set shop name
    holder.customerShopNameTv.setText(model.getAddress());
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

    holder.customerLay.setOnClickListener(new View.OnClickListener() {
      @Override public void onClick(View v) {
        showCustomerDetailDialog(model);
      }
    });
  }

  private void setMargin(boolean isLastItem, CardView cardView) {
    CardView.LayoutParams parameter = new CardView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
    if (isLastItem)
      parameter.setMargins(8, 8, 8, 8);
    else
      parameter.setMargins(8, 8, 8, 0);
    cardView.setLayoutParams(parameter);

  }

  private void showCustomerDetailDialog(CustomerListModel model) {
    AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(context);
    LayoutInflater inflater = ((FeedActivity) context).getLayoutInflater();
    View dialogView = inflater.inflate(R.layout.dialog_customer_detail, null);
    dialogBuilder.setView(dialogView);

    TextView customerNameTv = (TextView) dialogView.findViewById(R.id.customer_name_tv);
    TextView customerShopNameTv = (TextView) dialogView.findViewById(R.id.customer_shop_name_tv);
    TextView customerIdTv = (TextView) dialogView.findViewById(R.id.customer_id_tv);
    TextView customerLocationTv = (TextView) dialogView.findViewById(R.id.location_tv);
    TextView customerMobileTv = (TextView) dialogView.findViewById(R.id.mobile_tv);
    TextView customerPhoneTv = (TextView) dialogView.findViewById(R.id.phone_tv);
    TextView cancelTv = (TextView) dialogView.findViewById(R.id.cancel_btn);
    Button enterBtn = (Button) dialogView.findViewById(R.id.enter_btn);
    Button noVisitBtn = (Button) dialogView.findViewById(R.id.no_visit_btn);
    ImageView hasLocationImg = (ImageView) dialogView.findViewById(R.id.has_location_img);
    ImageView visitTodayImg = (ImageView) dialogView.findViewById(R.id.visit_today_img);
    ImageView hasOrderImg = (ImageView) dialogView.findViewById(R.id.has_order_img);
    //TODO:set shop name
    customerShopNameTv.setText(model.getAddress());
    customerNameTv.setText(model.getTitle());
    String customerCode = "کد : " + model.getCode();
    customerIdTv.setText(customerCode);
    //set location icon
    if (model.hasLocation()) {
      hasLocationImg.setImageResource(R.drawable.ic_location_on_24dp);
    } else {
      hasLocationImg.setImageResource(R.drawable.ic_location_off_24dp);
    }
    //set visit icon
    if (model.isVisited()) {
      visitTodayImg.setImageResource(R.drawable.ic_visibility_24dp);
    } else {
      visitTodayImg.setImageResource(R.drawable.ic_visibility_off_24dp);
    }
    if (model.hasRejection()) {
      visitTodayImg.setImageResource(R.drawable.ic_visibility_off_none_24dp);
    }

    hasOrderImg.setVisibility(model.hasOrder() ? View.VISIBLE : View.GONE);

    customerLocationTv.setText(model.getAddress());
    customerMobileTv.setText(model.getCellPhone());
    customerPhoneTv.setText(model.getPhoneNumber());

    AlertDialog alertDialog = dialogBuilder.create();
    alertDialog.show();
    cancelTv.setOnClickListener(new View.OnClickListener() {
      @Override public void onClick(View v) {
        alertDialog.cancel();
      }
    });
    enterBtn.setOnClickListener(new View.OnClickListener() {
      @Override public void onClick(View v) {
        //TODO:add enter action
        Toast.makeText(context, "enter", Toast.LENGTH_SHORT).show();
        alertDialog.dismiss();
      }
    });
    noVisitBtn.setOnClickListener(new View.OnClickListener() {
      @Override public void onClick(View v) {
        //TODO:add no visit action
        Toast.makeText(context, "no visit", Toast.LENGTH_SHORT).show();
        alertDialog.cancel();
      }
    });
  }

  @Override public int getItemCount() {
    return customers.size();
  }

  public class ViewHolder extends RecyclerView.ViewHolder {
    @BindView(R.id.customer_name_tv) TextView customerNameTv;
    @BindView(R.id.customer_id_tv) TextView customerIdTv;
    @BindView(R.id.customer_shop_name_tv) TextView customerShopNameTv;
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
