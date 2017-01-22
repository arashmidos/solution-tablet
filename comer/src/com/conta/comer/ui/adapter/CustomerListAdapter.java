package com.conta.comer.ui.adapter;

import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.conta.comer.R;
import com.conta.comer.data.listmodel.CustomerListModel;
import com.conta.comer.exception.UnknownSystemException;
import com.conta.comer.service.CustomerService;
import com.conta.comer.service.impl.CustomerServiceImpl;
import com.conta.comer.ui.MainActivity;
import com.conta.comer.util.CharacterFixUtil;
import com.conta.comer.util.Empty;
import com.conta.comer.util.ToastUtil;

import java.util.ArrayList;
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
                return customerService.getFilteredCustomerList(visitLineId, CharacterFixUtil.fixString(constraint.toString()));
            } else
            {
                return customerService.getAllCustomersListModelByVisitLineBackendId(visitLineId);
            }
        } catch (final Exception ex)
        {
            context.runOnUiThread(new Runnable()
            {
                @Override
                public void run()
                {
                    ToastUtil.toastError(context, new UnknownSystemException(ex));
                }
            });
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
                holder.locationImg.setImageResource(R.drawable.ic_location_on_light_green_700_24dp);
            } else
            {
                holder.locationImg.setImageResource(R.drawable.ic_location_off_grey_700_24dp);
            }
            //set visit icon
            if (model.isVisited())
            {
                holder.visitImg.setImageResource(R.drawable.ic_visibility_light_green_700_24dp);
            } else
            {
                holder.visitImg.setImageResource(R.drawable.ic_visibility_off_grey_700_24dp);
            }

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
    }
}
