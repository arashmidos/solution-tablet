package com.conta.comer.ui.adapter;

import com.conta.comer.data.model.GoodsListModel;
import com.conta.comer.data.searchobject.GoodsSo;
import com.conta.comer.service.GoodsService;
import com.conta.comer.service.impl.GoodsServiceImpl;
import com.conta.comer.ui.MainActivity;

import java.util.List;

/**
 * Created by Mahyar on 7/29/2015.
 */
public class GoodsListForQuestionnairesAdapter extends BaseListAdapter<GoodsListModel>
{

    private List<GoodsListModel> dataModel;
    private MainActivity mainActivity;
    private GoodsService goodsService;
    private Long goodsGroupBackendId;

    public GoodsListForQuestionnairesAdapter(MainActivity context, List<GoodsListModel> dataModel, Long goodsGroupBackendId)
    {
        super(context, dataModel);
        this.dataModel = dataModel;
        this.mainActivity = context;
        this.goodsService = new GoodsServiceImpl(context);
        this.goodsGroupBackendId = goodsGroupBackendId;

    }

    @Override
    protected List<GoodsListModel> getFilteredData(CharSequence constraint)
    {
        GoodsSo goodsSo = new GoodsSo();
        goodsSo.setConstraint(constraint.toString());
        goodsSo.setGoodsGroupBackendId(goodsGroupBackendId);
        return goodsService.searchForGoods(goodsSo);
    }
}
