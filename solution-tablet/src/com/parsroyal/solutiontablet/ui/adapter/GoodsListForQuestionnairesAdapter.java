package com.parsroyal.solutiontablet.ui.adapter;

import com.parsroyal.solutiontablet.data.listmodel.GoodsListModel;
import com.parsroyal.solutiontablet.data.searchobject.GoodsSo;
import com.parsroyal.solutiontablet.service.GoodsService;
import com.parsroyal.solutiontablet.service.impl.GoodsServiceImpl;
import com.parsroyal.solutiontablet.ui.activity.OldMainActivity;
import java.util.List;

/**
 * Created by Mahyar on 7/29/2015.
 */
public class GoodsListForQuestionnairesAdapter extends BaseListAdapter<GoodsListModel> {

  private List<GoodsListModel> dataModel;
  private OldMainActivity oldMainActivity;
  private GoodsService goodsService;
  private Long goodsGroupBackendId;

  public GoodsListForQuestionnairesAdapter(OldMainActivity context, List<GoodsListModel> dataModel,
      Long goodsGroupBackendId) {
    super(context, dataModel);
    this.dataModel = dataModel;
    this.oldMainActivity = context;
    this.goodsService = new GoodsServiceImpl(context);
    this.goodsGroupBackendId = goodsGroupBackendId;
  }

  @Override
  protected List<GoodsListModel> getFilteredData(CharSequence constraint) {
    GoodsSo goodsSo = new GoodsSo();
    goodsSo.setConstraint(constraint.toString());
    goodsSo.setGoodsGroupBackendId(goodsGroupBackendId);
    return goodsService.searchForGoods(goodsSo);
  }
}
