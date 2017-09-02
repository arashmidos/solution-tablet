package com.parsroyal.solutiontablet.ui.adapter;

import android.app.Activity;
import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import butterknife.BindView;
import butterknife.ButterKnife;
import com.crashlytics.android.Crashlytics;
import com.parsroyal.solutiontablet.R;
import com.parsroyal.solutiontablet.data.listmodel.CustomerListModel;
import com.parsroyal.solutiontablet.exception.UnknownSystemException;
import com.parsroyal.solutiontablet.service.impl.CustomerServiceImpl;
import com.parsroyal.solutiontablet.ui.MainActivity;
import com.parsroyal.solutiontablet.util.CharacterFixUtil;
import com.parsroyal.solutiontablet.util.ToastUtil;
import java.util.Collections;
import java.util.List;

/**
 * Created by ShakibIsTheBest on 09/01/2017.
 */

public class SystemCustomerAdapter extends RecyclerView.Adapter<SystemCustomerAdapter.ViewHolder> {

  private final CustomerServiceImpl customerService;
  private LayoutInflater inflater;
  private Context context;
  private List<CustomerListModel> customers;
  private Activity mainActivity;

  public SystemCustomerAdapter(Context context, List<CustomerListModel> customers) {
    this.context = context;
    this.customers = customers;
    this.mainActivity = (MainActivity) context;
    this.customerService = new CustomerServiceImpl(context);
    inflater = LayoutInflater.from(context);
  }

  @Override
  public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
    View view = inflater.inflate(R.layout.item_system_customer, parent, false);
    return new ViewHolder(view);
  }

  @Override
  public void onBindViewHolder(ViewHolder holder, int position) {
    CustomerListModel customer = customers.get(position);
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
  }

  private void setMargin(boolean isLastItem, RelativeLayout layout) {
    RelativeLayout.LayoutParams parameter = new RelativeLayout.LayoutParams(
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

  public List<CustomerListModel> getFilteredData(CharSequence constraint) {
    try {
      String keyword = "";
      if (constraint.length() != 0 && !constraint.toString().equals("")) {
        keyword = CharacterFixUtil.fixString(constraint.toString());
      }

      return customerService.getFilteredCustomerList(null, keyword);

    } catch (final Exception ex) {
      Crashlytics
          .log(Log.ERROR, "Filter data", "Error in filtering customer list" + ex.getMessage());
      mainActivity
          .runOnUiThread(() -> ToastUtil.toastError(mainActivity, new UnknownSystemException(ex)));
      return Collections.emptyList();
    }
  }

  @Override
  public int getItemCount() {
    return customers.size();
  }

  public void update(List<CustomerListModel> customers) {
    this.customers = customers;
    notifyDataSetChanged();
  }

  public class ViewHolder extends RecyclerView.ViewHolder {

    @BindView(R.id.customer_name_tv)
    TextView customerNameTv;
    @BindView(R.id.customer_id_tv)
    TextView customerIdTv;
    @BindView(R.id.customer_lay)
    RelativeLayout customerLay;
    @BindView(R.id.customer_shop_name_tv)
    TextView customerShopNameTv;
    @BindView(R.id.has_location_img)
    ImageView hasLocationImg;

    public ViewHolder(View itemView) {
      super(itemView);
      ButterKnife.bind(this, itemView);
    }
  }
}
