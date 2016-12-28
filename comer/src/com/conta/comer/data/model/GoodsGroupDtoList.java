package com.conta.comer.data.model;

import com.conta.comer.data.entity.GoodsGroup;

import java.util.List;

/**
 * Created by Mahyar on 7/23/2015.
 */
public class GoodsGroupDtoList extends BaseModel
{

    private List<GoodsGroup> goodsGroups;

    public List<GoodsGroup> getGoodsGroups()
    {
        return goodsGroups;
    }

    public void setGoodsGroups(List<GoodsGroup> goodsGroups)
    {
        this.goodsGroups = goodsGroups;
    }
}
