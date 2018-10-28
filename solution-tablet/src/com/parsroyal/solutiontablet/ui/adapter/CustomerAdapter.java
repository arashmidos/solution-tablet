package com.parsroyal.solutiontablet.ui.adapter;

import android.app.Activity;
import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.RecyclerView.Adapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.RelativeLayout.LayoutParams;
import android.widget.TextView;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import com.parsroyal.solutiontablet.R;
import com.parsroyal.solutiontablet.data.dao.CustomerDao;
import com.parsroyal.solutiontablet.data.dao.impl.CustomerDaoImpl;
import com.parsroyal.solutiontablet.data.entity.Customer;
import com.parsroyal.solutiontablet.service.impl.CustomerServiceImpl;
import com.parsroyal.solutiontablet.ui.MainActivity;
import com.parsroyal.solutiontablet.util.NumberUtil;
import java.util.List;

/**
 * Created by ShakibIsTheBest on 09/01/2017.
 */

public class CustomerAdapter extends Adapter<CustomerAdapter.ViewHolder> {

  private final CustomerServiceImpl customerService;
  private CustomerDao customerDao;
  private LayoutInflater inflater;
  private Context context;
  private List<Customer> customers;
  private Activity mainActivity;
  private boolean isClickable;

  public CustomerAdapter(Context context, List<Customer> customers,
      boolean isClickable) {
    this.context = context;
    this.isClickable = isClickable;
    this.customers = customers;
    this.mainActivity = (MainActivity) context;
    this.customerDao = new CustomerDaoImpl(context);
    this.customerService = new CustomerServiceImpl(context);
    this.inflater = LayoutInflater.from(context);
  }

  @Override
  public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
    View view = inflater.inflate(R.layout.item_system_customer, parent, false);
    return new ViewHolder(view);
  }

  @Override
  public void onBindViewHolder(ViewHolder holder, int position) {
    Customer customer = customers.get(position);
    holder.setData(customer, position);
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

  @Override
  public int getItemCount() {
    return customers.size();
  }

  public void update(List<Customer> customers) {
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
    @BindView(R.id.edit_lay)
    LinearLayout editLay;

    private Customer customer;
    private int position;

    public ViewHolder(View itemView) {
      super(itemView);
      ButterKnife.bind(this, itemView);
    }

    @OnClick(R.id.customer_lay)
    public void onViewClicked() {
      //TODO:insert customer
      customer.setVisitLineBackendId(0L);
      customerDao.create(customer);
      mainActivity.onBackPressed();
    }

    public void setData(Customer customer, int position) {
      this.customer = customer;
      this.position = position;
      editLay.setVisibility(View.GONE);
      setMargin(position == customers.size() - 1, customerLay);
      customerNameTv.setText(customer.getFullName());
      String customerCode = "کد : " + NumberUtil.digitsToPersian(customer.getCode());
      customerIdTv.setText(customerCode);
      customerShopNameTv.setText(NumberUtil.digitsToPersian(customer.getShopName()));
      //TODO:check condition
      if (customer.getxLocation() > 0 && customer.getxLocation() > 0) {
        hasLocationImg.setImageResource(R.drawable.ic_gps_fixed_black_18dp);
        hasLocationImg.setColorFilter(ContextCompat.getColor(context, R.color.primary));
      } else {
        hasLocationImg.setImageResource(R.drawable.ic_gps_off_black_18dp);
        hasLocationImg.setColorFilter(ContextCompat.getColor(context, R.color.login_gray));
      }
    }
  }
}
