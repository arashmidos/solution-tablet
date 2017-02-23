package com.parsroyal.solutiontablet.ui.adapter;

import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.parsroyal.solutiontablet.R;
import com.parsroyal.solutiontablet.data.listmodel.VisitLineListModel;
import com.parsroyal.solutiontablet.exception.UnknownSystemException;
import com.parsroyal.solutiontablet.service.CustomerService;
import com.parsroyal.solutiontablet.service.impl.CustomerServiceImpl;
import com.parsroyal.solutiontablet.ui.MainActivity;
import com.parsroyal.solutiontablet.util.ToastUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Mahyar on 7/6/2015.
 */
public class VisitLinesAdapter extends BaseListAdapter<VisitLineListModel>
{

    private MainActivity context;
    private CustomerService customerService;

    public VisitLinesAdapter(MainActivity context, List<VisitLineListModel> dataModel)
    {
        super(context, dataModel);
        this.context = context;
        this.customerService = new CustomerServiceImpl(context);
    }

    @Override
    protected List<VisitLineListModel> getFilteredData(CharSequence constraint)
    {
        try
        {
            if (constraint.length() != 0 && !constraint.toString().equals(""))
            {
                return customerService.getAllFilteredVisitLinesListModel(constraint.toString());
            } else
            {
                return customerService.getAllVisitLinesListModel();
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
            VisitLineViewHolder holder;

            if (convertView == null)
            {
                convertView = mLayoutInflater.inflate(R.layout.row_layout_visit_line, null);
                holder = new VisitLineViewHolder();
                holder.rowLayout = (RelativeLayout) convertView.findViewById(R.id.rowLayout);
                holder.nameTxt = (TextView) convertView.findViewById(R.id.nameTxt);
                holder.codeTxt = (TextView) convertView.findViewById(R.id.codeTxt);
                holder.countTxt = (TextView) convertView.findViewById(R.id.countTxt);
                convertView.setTag(holder);
            } else
            {
                holder = (VisitLineViewHolder) convertView.getTag();
            }

            final VisitLineListModel model = dataModel.get(position);

            {
                holder.nameTxt.setText(model.getTitle());
                holder.codeTxt.setText(model.getCode());
                holder.countTxt.setText(String.valueOf(model.getCustomerCount()));
            }
            return convertView;
        } catch (Exception e)
        {
            Log.e(TAG, e.getMessage(), e);
            return null;
        }
    }

    private static class VisitLineViewHolder
    {
        public RelativeLayout rowLayout;
        public TextView nameTxt;
        public TextView codeTxt;
        public TextView countTxt;
    }
}
