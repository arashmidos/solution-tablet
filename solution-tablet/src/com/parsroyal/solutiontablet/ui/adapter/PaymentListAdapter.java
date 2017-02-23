package com.parsroyal.solutiontablet.ui.adapter;

import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.parsroyal.solutiontablet.R;
import com.parsroyal.solutiontablet.data.listmodel.PaymentListModel;
import com.parsroyal.solutiontablet.exception.UnknownSystemException;
import com.parsroyal.solutiontablet.service.PaymentService;
import com.parsroyal.solutiontablet.service.impl.PaymentServiceImpl;
import com.parsroyal.solutiontablet.ui.MainActivity;
import com.parsroyal.solutiontablet.util.DateUtil;
import com.parsroyal.solutiontablet.util.Empty;
import com.parsroyal.solutiontablet.util.NumberUtil;
import com.parsroyal.solutiontablet.util.ToastUtil;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by Arash on 08/19/2016.
 */
public class PaymentListAdapter extends BaseListAdapter<PaymentListModel>
{

    private final Long customerBackendId;
    private MainActivity context;
    private PaymentService paymentService;

    public PaymentListAdapter(MainActivity context, List<PaymentListModel> dataModel, Long customerBackendId)
    {
        super(context, dataModel);
        this.context = context;
        this.customerBackendId = customerBackendId;
        this.paymentService = new PaymentServiceImpl(context);
    }

    @Override
    protected List<PaymentListModel> getFilteredData(CharSequence constraint)
    {
        try
        {
            if (constraint.length() != 0 && !constraint.toString().equals(""))
            {
//                 paymentService.ge(constraint.toString()));
            } else
            {
//                return customerService.getAllCustomersListModelByVisitLineBackendId(visitLineId);
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
        return paymentService.getAllPaymentsListModelByCustomerBackendId(customerBackendId);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent)
    {
        try
        {
            PaymentViewHolder holder;

            if (convertView == null)
            {
                convertView = mLayoutInflater.inflate(R.layout.row_layout_payment, null);
                holder = new PaymentViewHolder();
                holder.rowLayout = (LinearLayout) convertView.findViewById(R.id.paymentRowLayout);
                holder.amountTxt = (TextView) convertView.findViewById(R.id.amountTv);
                holder.typeTxt = (TextView) convertView.findViewById(R.id.typeTv);
                holder.dateTxt = (TextView) convertView.findViewById(R.id.dateTv);
                holder.customerName = (TextView) convertView.findViewById(R.id.customerNameTv);
                holder.customerNameTv = (TextView) convertView.findViewById(R.id.textView47);
                convertView.setTag(holder);
            } else
            {
                holder = (PaymentViewHolder) convertView.getTag();
            }

            final PaymentListModel model = dataModel.get(position);

            {
                long amount = Long.parseLong(model.getAmount()) / 1000L;
                holder.amountTxt.setText(NumberUtil.getCommaSeparated(amount) + " " + context.getString(R.string.common_irr_currency));
                holder.typeTxt.setText(model.getType().equals("1") ? "وجه نقد" :
                        model.getType().equals("2") ? "پرداخت الکترونیکی" : "چک");
                if (Empty.isNotEmpty(model.getDate()))
                {

                    Date createDate = DateUtil.convertStringToDate(model.getDate(), DateUtil.FULL_FORMATTER_GREGORIAN_WITH_TIME, "FA");

                    String dateString = DateUtil.convertDate(createDate, DateUtil.GLOBAL_FORMATTER, "FA");
                    holder.dateTxt.setText(dateString);
                }

                if (customerBackendId == -1)
                {
                    holder.customerNameTv.setVisibility(View.VISIBLE);
                    holder.customerName.setVisibility(View.VISIBLE);
                    holder.customerName.setText(model.getCustomerFullName());
                } else
                {
                    holder.customerNameTv.setVisibility(View.GONE);
                    holder.customerName.setVisibility(View.GONE);
                }
            }
            return convertView;
        } catch (Exception e)
        {
            Log.e(TAG, e.getMessage(), e);
            return null;
        }
    }

    private static class PaymentViewHolder
    {
        public LinearLayout rowLayout;
        public TextView amountTxt;
        public TextView typeTxt;
        public TextView dateTxt;
        public TextView customerName;
        public TextView customerNameTv;
    }
}
