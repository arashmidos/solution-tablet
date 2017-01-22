package com.conta.comer.ui.adapter;

import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.conta.comer.R;
import com.conta.comer.data.model.GoodsListModel;
import com.conta.comer.data.model.SaleOrderDto;
import com.conta.comer.data.searchobject.GoodsSo;
import com.conta.comer.service.GoodsService;
import com.conta.comer.service.impl.GoodsServiceImpl;
import com.conta.comer.ui.MainActivity;
import com.conta.comer.util.CharacterFixUtil;
import com.conta.comer.util.Empty;
import com.conta.comer.util.NumberUtil;

import java.util.List;

/**
 * Created by Mahyar on 8/31/2015.
 */
public class GoodsListAdapter extends BaseAdapter implements Filterable
{

    private MainActivity context;
    private SaleOrderDto order;
    private boolean disabled;
    private GoodsService goodsService;
    private List<GoodsListModel> goodsListModelList;
    private GoodsSo goodsSo;
    private Filter filter;

    public GoodsListAdapter(MainActivity context, SaleOrderDto order, boolean disable)
    {
        this.context = context;
        this.order = order;
        this.disabled = disable;
        this.goodsService = new GoodsServiceImpl(context);
        goodsSo = new GoodsSo();
        this.goodsListModelList = findGoods();
    }

    public List<GoodsListModel> findGoods()
    {
        return goodsService.searchForGoods(goodsSo);
    }

    public List<GoodsListModel> getGoodsListModelList()
    {
        return goodsListModelList;
    }

    public void setGoodsListModelList(List<GoodsListModel> goodsListModelList)
    {
        this.goodsListModelList = goodsListModelList;
    }

    @Override
    public int getCount()
    {
        return goodsListModelList.size();
    }

    @Override
    public Object getItem(int position)
    {
        return goodsListModelList.get(position);
    }

    @Override
    public long getItemId(int position)
    {
        return goodsListModelList.get(position).getPrimaryKey();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent)
    {

        GoodsItemViewHolder holder;

        if (Empty.isEmpty(convertView))
        {
            convertView = context.getLayoutInflater().inflate(R.layout.row_layout_goods, null);
            holder = new GoodsItemViewHolder();
            holder.goodsRowLayout = (LinearLayout) convertView.findViewById(R.id.goodsRowLayout);
            holder.goodsNameTv = (TextView) convertView.findViewById(R.id.goodNameTv);
            holder.goodsCodeTv = (TextView) convertView.findViewById(R.id.goodCodeTv);
            holder.goodsAmountTv = (TextView) convertView.findViewById(R.id.goodAmountTv);
            holder.unit1ExistingTv = (TextView) convertView.findViewById(R.id.unit1ExistingTv);
            holder.recoveryDateTv = (TextView) convertView.findViewById(R.id.recoveryDateTv);
            convertView.setTag(holder);
        } else
        {
            holder = (GoodsItemViewHolder) convertView.getTag();
        }

        GoodsListModel goods = goodsListModelList.get(position);

        holder.goodsNameTv.setText(goods.getTitle());
        holder.goodsCodeTv.setText(goods.getCode());
        {
            Double goodsAmount = Double.valueOf(goods.getGoodsAmount()) / 1000D;
            holder.goodsAmountTv.setText(NumberUtil.getCommaSeparated(goodsAmount) + " " + context.getString(R.string.common_irr_currency));
        }
        StringBuilder existingSb = new StringBuilder();
        {
            Double unit1Existing = Double.valueOf(goods.getExisting()) / 1000D;
            existingSb.append(NumberUtil.formatDoubleWith2DecimalPlaces(unit1Existing)).append(" ").append(Empty.isNotEmpty(goods.getUnit1Title()) ? goods.getUnit1Title() : "--");

        }
        {
            Long unit1Count = goods.getUnit1Count();
            if (Empty.isNotEmpty(unit1Count) && !unit1Count.equals(0L))
            {
                Double unit2Existing = Double.valueOf(goods.getExisting()) / Double.valueOf(unit1Count);
                unit2Existing = unit2Existing / 1000D;
                existingSb.append("  ").append(NumberUtil.formatDoubleWith2DecimalPlaces(unit2Existing)).append(" ").append(Empty.isNotEmpty(goods.getUnit2Title()) ? goods.getUnit2Title() : "--");
            }
        }
        holder.unit1ExistingTv.setText(existingSb.toString());
        {
            if (Empty.isNotEmpty(goods.getRecoveryDate()))
            {
                holder.recoveryDateTv.setText(String.valueOf(goods.getRecoveryDate()));
            }
        }

        return convertView;
    }

    @Override
    public Filter getFilter()
    {
        if (Empty.isEmpty(filter))
        {
            filter = new Filter()
            {
                @Override
                protected FilterResults performFiltering(CharSequence constraint)
                {
                    FilterResults results = new FilterResults();
                    if (Empty.isEmpty(goodsSo))
                    {
                        goodsSo = new GoodsSo();
                    }
                    constraint = CharacterFixUtil.fixString(constraint.toString());
                    goodsSo.setConstraint("%" + constraint.toString() + "%");
                    List<GoodsListModel> filteredData = findGoods();
                    results.count = filteredData.size();
                    results.values = filteredData;

                    return results;
                }

                @Override
                protected void publishResults(CharSequence constraint, FilterResults results)
                {
                    if (Empty.isNotEmpty(results.values) && results.count > 0)
                    {
                        GoodsListAdapter.this.goodsListModelList = (List<GoodsListModel>) results.values;
                        notifyDataSetChanged();
                    } else
                    {
                        notifyDataSetInvalidated();
                    }

                }
            };
        }
        return filter;
    }

    private static class GoodsItemViewHolder
    {
        private LinearLayout goodsRowLayout;
        private TextView goodsNameTv;
        private TextView goodsCodeTv;
        private TextView goodsAmountTv;
        private TextView unit1ExistingTv;
        private TextView recoveryDateTv;
    }
}
