package com.conta.comer.ui.adapter;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.conta.comer.R;
import com.conta.comer.data.model.KPIDetail;
import com.conta.comer.service.KPIService;
import com.conta.comer.service.impl.KPIServiceImpl;
import com.conta.comer.ui.MainActivity;

import java.text.DecimalFormat;
import java.util.List;

/**
 * Created by Arash on 2016-09-233
 */
public class KPIListAdapter extends ArrayAdapter<KPIDetail>
{

    private static final String TAG = KPIListAdapter.class.getSimpleName();
    private final DecimalFormat mFormat;
    private MainActivity context;
    private KPIService kpiService;

    public KPIListAdapter(MainActivity context, List<KPIDetail> dataModel)
    {
        super(context, R.layout.row_layout_kpi, dataModel);
        this.context = context;
        this.kpiService = new KPIServiceImpl(context);
        mFormat = new DecimalFormat("###,###,###,##0.0");

    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent)
    {
        try
        {
            KPIViewHolder holder;

            if (convertView == null)
            {
                convertView = LayoutInflater.from(context).inflate(R.layout.row_layout_kpi, null);
                holder = new KPIViewHolder();
                holder.rowLayout = (LinearLayout) convertView.findViewById(R.id.kpi_row_layout);
                holder.code = (TextView) convertView.findViewById(R.id.codeTv);
                holder.description = (TextView) convertView.findViewById(R.id.descriptionTv);
                holder.value = (TextView) convertView.findViewById(R.id.valueTv);
                convertView.setTag(holder);
            } else
            {
                holder = (KPIViewHolder) convertView.getTag();
            }

            final KPIDetail model = getItem(position);

            holder.code.setText(model.getCode());
            holder.description.setText(model.getDescription());

            holder.value.setText(mFormat.format(model.getValue()));
            return convertView;

        } catch (Exception e)
        {
            Log.e(TAG, e.getMessage(), e);
            return null;
        }
    }

    private static class KPIViewHolder
    {
        public LinearLayout rowLayout;
        public TextView code;
        public TextView description;
        public TextView value;
    }
}
