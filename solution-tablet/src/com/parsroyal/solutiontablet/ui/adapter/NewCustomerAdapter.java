package com.parsroyal.solutiontablet.ui.adapter;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import butterknife.BindView;
import butterknife.ButterKnife;
import com.parsroyal.solutiontablet.R;
import com.parsroyal.solutiontablet.data.listmodel.NCustomerListModel;
import java.util.List;

/**
 * Created by ShakibIsTheBest on 09/01/2017.
 */

public class NewCustomerAdapter extends RecyclerView.Adapter<NewCustomerAdapter.ViewHolder> {

  @BindView(R.id.delete_img)
  ImageView deleteImg;
  @BindView(R.id.edit_img)
  ImageView editImg;
  @BindView(R.id.edit_lay)
  LinearLayout editLay;
  private LayoutInflater inflater;
  private Context context;
  private boolean isSend;
  private List<NCustomerListModel> customers;

  public NewCustomerAdapter(Context context, boolean isSend, List<NCustomerListModel> customers) {
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
    if (isSend) {
      editLay.setVisibility(View.GONE);
    } else {
      editLay.setVisibility(View.VISIBLE);
    }

    setMargin(position == customers.size() - 1, holder.customerLay);
    holder.setData(customer, position);
    editImg.setOnClickListener(v -> Toast.makeText(context, "edit", Toast.LENGTH_SHORT).show());
    deleteImg.setOnClickListener(v -> Toast.makeText(context, "delete", Toast.LENGTH_SHORT).show());
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

  @Override
  public int getItemCount() {
    return customers.size();
  }

  public void update(List<NCustomerListModel> customers) {
    this.customers = customers;
    notifyDataSetChanged();
  }

  public class ViewHolder extends RecyclerView.ViewHolder implements OnClickListener {

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
    private NCustomerListModel customer;
    private int position;

    public ViewHolder(View itemView) {
      super(itemView);
      ButterKnife.bind(this, itemView);
      customerLay.setOnClickListener(this);
    }

    public void setData(NCustomerListModel customer, int position) {
      this.customer = customer;
      this.position = position;
      customerNameTv.setText(customer.getTitle());
      customerIdTv.setText(String.format(context.getString(R.string.code_x), customer.getCode()));
      customerShopNameTv.setText(customer.getShopName());
      if (customer.hasLocation()) {
        hasLocationImg.setImageResource(R.drawable.ic_gps_fixed_black_18dp);
        hasLocationImg.setColorFilter(ContextCompat.getColor(context, R.color.primary));
      } else {
        hasLocationImg.setImageResource(R.drawable.ic_gps_off_black_18dp);
        hasLocationImg.setColorFilter(ContextCompat.getColor(context, R.color.login_gray));
      }
    }

    @Override
    public void onClick(View v) {
      Toast.makeText(context, "khare gav", Toast.LENGTH_SHORT).show();
    }
  }
}
