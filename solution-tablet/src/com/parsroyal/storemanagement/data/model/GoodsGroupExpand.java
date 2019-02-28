package com.parsroyal.storemanagement.data.model;

import com.parsroyal.storemanagement.data.entity.GoodsGroup;
import com.thoughtbot.expandablerecyclerview.models.ExpandableGroup;
import java.util.List;

/**
 * Created by shkbhbb on 1/13/18.
 */

public class GoodsGroupExpand extends ExpandableGroup {

  public GoodsGroupExpand(String title,
      List<GoodsGroup> items) {
    super(title, items);
  }
}
