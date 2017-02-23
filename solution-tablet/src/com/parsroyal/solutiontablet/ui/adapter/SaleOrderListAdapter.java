package com.parsroyal.solutiontablet.ui.adapter;

import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.parsroyal.solutiontablet.R;
import com.parsroyal.solutiontablet.constants.SaleOrderStatus;
import com.parsroyal.solutiontablet.data.model.SaleOrderListModel;
import com.parsroyal.solutiontablet.data.searchobject.SaleOrderSO;
import com.parsroyal.solutiontablet.exception.UnknownSystemException;
import com.parsroyal.solutiontablet.service.order.SaleOrderService;
import com.parsroyal.solutiontablet.service.order.impl.SaleOrderServiceImpl;
import com.parsroyal.solutiontablet.ui.MainActivity;
import com.parsroyal.solutiontablet.util.Empty;
import com.parsroyal.solutiontablet.util.NumberUtil;
import com.parsroyal.solutiontablet.util.ToastUtil;

import java.util.List;
import java.util.Locale;

/**
 * Created by Mahyar on 8/25/2015.
 */
public class SaleOrderListAdapter extends BaseListAdapter<SaleOrderListModel>
{

    public static final String TAG = SaleOrderListAdapter.class.getSimpleName();

    private SaleOrderSO saleOrderSO;
    private SaleOrderService saleOrderService;

    public SaleOrderListAdapter(MainActivity context, List<SaleOrderListModel> dataModel, SaleOrderSO saleOrderSO)
    {
        super(context, dataModel);
        this.saleOrderSO = saleOrderSO;
        this.saleOrderService = new SaleOrderServiceImpl(context);
    }

    @Override
    protected List<SaleOrderListModel> getFilteredData(CharSequence constraint)
    {
        try
        {
            if (Empty.isNotEmpty(constraint) && "".equals(constraint.toString()))
            {
                saleOrderSO.setConstraint(constraint.toString());
            }
            return saleOrderService.findOrders(saleOrderSO);
        } catch (final Exception e)
        {
            Log.e(TAG, e.getMessage(), e);
            context.runOnUiThread(new Runnable()
            {
                @Override
                public void run()
                {
                    ToastUtil.toastError(context, new UnknownSystemException(e));
                }
            });
            return null;
        }
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent)
    {

        try
        {
            SaleOrderViewHolder holder;

            if (Empty.isEmpty(convertView))
            {
                convertView = mLayoutInflater.inflate(R.layout.row_layout_order, null);
                holder = new SaleOrderViewHolder();
                holder.saleOrderNumberTv = (TextView) convertView.findViewById(R.id.orderNumberTv);
                holder.saleOrderNumberLabel = (TextView) convertView.findViewById(R.id.orderNumberLabel);
                holder.dateTv = (TextView) convertView.findViewById(R.id.dateTv);
                holder.orderDateLabel = (TextView) convertView.findViewById(R.id.orderDateLabel);
                holder.amountTv = (TextView) convertView.findViewById(R.id.amountTv);
                holder.amountLabel = (TextView) convertView.findViewById(R.id.amountLabel);
                holder.paymentTypeTitleTv = (TextView) convertView.findViewById(R.id.paymentTypeTitleTv);
                holder.rejectOrPaymentLabel = (TextView) convertView.findViewById(R.id.rejectOrPaymentLabel);
                holder.statusTv = (TextView) convertView.findViewById(R.id.statusTv);
                holder.customerNameTv = (TextView) convertView.findViewById(R.id.customerNameTv);
                convertView.setTag(holder);
            } else
            {
                holder = (SaleOrderViewHolder) convertView.getTag();
            }

            {
                SaleOrderListModel orderListModel = dataModel.get(position);

                if (Empty.isNotEmpty(orderListModel))
                {
                    String title = getProperTitle(orderListModel.getStatus());
                    holder.saleOrderNumberLabel.setText(String.format(Locale.US,context.getString(R.string.number_x), title));
                    holder.saleOrderNumberTv.setText(orderListModel.getSaleOrderNumber());
                    holder.dateTv.setText(orderListModel.getDate());
                    {
                        Double displayAmount = Double.valueOf(orderListModel.getAmount()) / 1000D;
                        holder.amountTv.setText(NumberUtil.getCommaSeparated(displayAmount) + context.getString(R.string.common_irr_currency));
                    }
                    holder.orderDateLabel.setText(String.format(Locale.US,context.getString(R.string.date_x), title));
                    holder.amountLabel.setText(String.format(Locale.US,context.getString(R.string.amount_x), title));
                    if (isRejected(orderListModel.getStatus()))
                    {
                        holder.rejectOrPaymentLabel.setText(context.getString(R.string.reject_reason_title));
                    }else
                    {
                        holder.rejectOrPaymentLabel.setText(context.getString(R.string.payment_type));
                    }
                    //update reason
                    holder.paymentTypeTitleTv.setText(orderListModel.getPaymentTypeTitle());
                    holder.statusTv.setText(SaleOrderStatus.getDisplayTitle(context, orderListModel.getStatus()));
                    holder.customerNameTv.setText(orderListModel.getCustomerName());
                }
            }
            return convertView;
        } catch (final Exception e)
        {
            Log.e(TAG, e.getMessage(), e);
            context.runOnUiThread(new Runnable()
            {
                @Override
                public void run()
                {
                    ToastUtil.toastError(context, new UnknownSystemException(e));
                }
            });
            return null;
        }
    }

    /*
    @return Proper title for which could be "Rejected", "Order" or "Invoice"
    */
    private String getProperTitle(Long orderStatus)
    {

        if (orderStatus.equals(SaleOrderStatus.REJECTED_DRAFT.getId()) ||
                orderStatus.equals(SaleOrderStatus.REJECTED.getId()) ||
                orderStatus.equals(SaleOrderStatus.REJECTED_SENT.getId()))
        {
            return context.getString(R.string.title_reject);
        } else if (orderStatus.equals(SaleOrderStatus.INVOICED.getId()) ||
                orderStatus.equals(SaleOrderStatus.SENT_INVOICE.getId()))
        {
            return context.getString(R.string.title_factor);
        } else
            return context.getString(R.string.title_order);
    }

    private boolean isRejected(Long orderStatus)
    {
        return (orderStatus.equals(SaleOrderStatus.REJECTED_DRAFT.getId()) ||
                orderStatus.equals(SaleOrderStatus.REJECTED.getId()) ||
                orderStatus.equals(SaleOrderStatus.REJECTED_SENT.getId()));
    }

    private static class SaleOrderViewHolder
    {
        public LinearLayout rowLayout;
        public TextView saleOrderNumberTv;
        public TextView dateTv;
        public TextView orderDateLabel;
        public TextView amountTv;
        public TextView amountLabel;
        public TextView paymentTypeTitleTv;
        public TextView customerNameTv;
        public TextView statusTv;
        public TextView saleOrderNumberLabel;
        public TextView rejectOrPaymentLabel;
    }

}
