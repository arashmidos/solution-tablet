package com.parsroyal.solutiontablet.ui.adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.crashlytics.android.answers.Answers;
import com.crashlytics.android.answers.SearchEvent;
import com.parsroyal.solutiontablet.R;
import com.parsroyal.solutiontablet.data.listmodel.BaseListModel;
import com.parsroyal.solutiontablet.ui.MainActivity;
import com.parsroyal.solutiontablet.util.Empty;

import java.util.List;

/**
 * Created by Mahyar on 7/6/2015.
 */
public abstract class BaseListAdapter<T extends BaseListModel> extends BaseAdapter implements
        Filterable
{

    public static final String TAG = VisitLinesAdapter.class.getSimpleName();

    protected MainActivity context;
    protected List<T> dataModel;
    protected LayoutInflater mLayoutInflater;
    protected Filter filter;

    public BaseListAdapter(MainActivity context, List<T> dataModel)
    {
        this.context = context;
        this.dataModel = dataModel;
        this.mLayoutInflater = (LayoutInflater) context.getSystemService(
                Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount()
    {
        return dataModel == null ? 0 : dataModel.size();
    }

    @Override
    public Object getItem(int position)
    {
        return dataModel.get(position);
    }

    @Override
    public long getItemId(int position)
    {
        return dataModel.get(position).getPrimaryKey();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent)
    {

        try
        {
            VisitLineViewHolder holder;

            if (convertView == null)
            {
                convertView = mLayoutInflater.inflate(R.layout.row_layout_base, null);
                holder = new VisitLineViewHolder();
                holder.rowLayout = (RelativeLayout) convertView.findViewById(R.id.rowLayout);
                holder.nameTxt = (TextView) convertView.findViewById(R.id.nameTxt);
                holder.codeTxt = (TextView) convertView.findViewById(R.id.codeTxt);
                convertView.setTag(holder);
            } else
            {
                holder = (VisitLineViewHolder) convertView.getTag();
            }

            final BaseListModel model = dataModel.get(position);

            {
                holder.nameTxt.setText(model.getTitle());
                holder.codeTxt.setText(model.getCode());
            }
            return convertView;
        } catch (Exception e)
        {
            Log.e(TAG, e.getMessage(), e);
            return null;
        }
    }

    @Override
    public Filter getFilter()
    {
        if (Empty.isEmpty(filter))
        {
            filter = new Filter()
            {

                @SuppressWarnings("unchecked")
                @Override
                protected void publishResults(CharSequence constraint, final FilterResults results)
                {
                    BaseListAdapter.this.dataModel = (List<T>) results.values;
                    if (results.count > 0)
                    {
                        notifyDataSetChanged();
                    } else
                    {
                        notifyDataSetInvalidated();
                    }
                }

                @Override
                protected FilterResults performFiltering(CharSequence constraint)
                {
                    Answers.getInstance().logSearch(new SearchEvent()
                            .putQuery(constraint.toString()));
                    FilterResults results = new FilterResults();
                    List<T> filteredData = getFilteredData(constraint);
                    results.count = filteredData.size();
                    results.values = filteredData;
                    return results;
                }
            };
        }
        return filter;

    }

    protected abstract List<T> getFilteredData(CharSequence constraint);

    public List<T> getDataModel()
    {
        return dataModel;
    }

    public void setDataModel(List<T> dataModel)
    {
        this.dataModel = dataModel;
    }

    @Override
    public boolean isEmpty()
    {
        return false;
    }

    private static class VisitLineViewHolder
    {

        public RelativeLayout rowLayout;
        public TextView nameTxt;
        public TextView codeTxt;
    }
}
