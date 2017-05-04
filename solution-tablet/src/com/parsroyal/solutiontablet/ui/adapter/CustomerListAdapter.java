package com.parsroyal.solutiontablet.ui.adapter;

import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.parsroyal.solutiontablet.R;
import com.parsroyal.solutiontablet.data.listmodel.CustomerListModel;
import com.parsroyal.solutiontablet.exception.UnknownSystemException;
import com.parsroyal.solutiontablet.service.CustomerService;
import com.parsroyal.solutiontablet.service.impl.CustomerServiceImpl;
import com.parsroyal.solutiontablet.ui.MainActivity;
import com.parsroyal.solutiontablet.util.CharacterFixUtil;
import com.parsroyal.solutiontablet.util.Empty;
import com.parsroyal.solutiontablet.util.ToastUtil;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Created by Mahyar on 7/6/2015.
 */
public class CustomerListAdapter extends BaseListAdapter<CustomerListModel>
{
    private MainActivity context;
    private CustomerService customerService;
    private Long visitLineId;

    public CustomerListAdapter(MainActivity context, List<CustomerListModel> dataModel, Long visitLineId)
    {
        super(context, dataModel);
        this.context = context;
        this.customerService = new CustomerServiceImpl(context);
        this.visitLineId = visitLineId;
    }

    @Override
    protected List<CustomerListModel> getFilteredData(CharSequence constraint)
    {
        try
        {
            if (constraint.length() != 0 && !constraint.toString().equals(""))
            {
                String keyword = CharacterFixUtil.fixString(constraint.toString());

                for (Iterator<CustomerListModel> it = dataModel.iterator(); it.hasNext(); )
                {
                    CustomerListModel item = it.next();
                    if (!item.getTitle().contains(keyword) && !item.getCode().contains(keyword)
                            && !item.getAddress().contains(keyword) && !item.getPhoneNumber().contains(keyword)
                            && !item.getCellPhone().contains(keyword))
                    {
                        it.remove();
                    }
                }
                return dataModel;
            } else
            {
                return customerService.getAllCustomersListModelByVisitLineBackendId(visitLineId);
            }
        } catch (final Exception ex)
        {
            context.runOnUiThread(() -> ToastUtil.toastError(context, new UnknownSystemException(ex)));
            return new ArrayList<>();
        }
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent)
    {
        try
        {
            CustomerViewHolder holder;

            if (convertView == null)
            {
                convertView = mLayoutInflater.inflate(R.layout.row_layout_customer, null);
                holder = new CustomerViewHolder();
                holder.rowLayout = (RelativeLayout) convertView.findViewById(R.id.rowLayout);
                holder.nameTxt = (TextView) convertView.findViewById(R.id.nameTxt);
                holder.codeTxt = (TextView) convertView.findViewById(R.id.codeTxt);
                holder.phoneTxt = (TextView) convertView.findViewById(R.id.phoneTxt);
                holder.cellPhoneTxt = (TextView) convertView.findViewById(R.id.cellPhoneTxt);
                holder.addressTxt = (TextView) convertView.findViewById(R.id.addressTxt);
                holder.locationImg = (ImageView) convertView.findViewById(R.id.has_location_img);
                holder.visitImg = (ImageView) convertView.findViewById(R.id.visit_today_img);
                holder.orderImg = (ImageView) convertView.findViewById(R.id.has_order_img);
                convertView.setTag(holder);
            } else
            {
                holder = (CustomerViewHolder) convertView.getTag();
            }

            final CustomerListModel model = dataModel.get(position);

            holder.nameTxt.setText(model.getTitle());
            holder.codeTxt.setText(model.getCode());
            holder.phoneTxt.setText(String.valueOf(model.getPhoneNumber()));
            holder.cellPhoneTxt.setText(Empty.isNotEmpty(model.getCellPhone()) ? model.getCellPhone() : "--");
            holder.addressTxt.setText(Empty.isNotEmpty(model.getAddress()) ? model.getAddress() : "--");

            //set location icon
            if (model.hasLocation())
            {
                holder.locationImg.setImageResource(R.drawable.ic_location_on_24dp);
            } else
            {
                holder.locationImg.setImageResource(R.drawable.ic_location_off_24dp);
            }
            //set visit icon
            if (model.isVisited())
            {
                holder.visitImg.setImageResource(R.drawable.ic_visibility_24dp);
            } else
            {
                holder.visitImg.setImageResource(R.drawable.ic_visibility_off_24dp);
            }
            if (model.hasRejection())
            {
                holder.visitImg.setImageResource(R.drawable.ic_visibility_off_none_24dp);
            }

            holder.orderImg.setVisibility(model.hasOrder() ? View.VISIBLE : View.GONE);

            return convertView;
        } catch (Exception e)
        {
            Log.e(TAG, e.getMessage(), e);
            return null;
        }
    }

    private static class CustomerViewHolder
    {
        public RelativeLayout rowLayout;
        public TextView nameTxt;
        public TextView codeTxt;
        public TextView phoneTxt;
        public TextView cellPhoneTxt;
        public TextView addressTxt;
        public ImageView locationImg;
        public ImageView visitImg;
        public ImageView orderImg;
    }
}
