package com.parsroyal.storemanagement.data.model;

import com.parsroyal.storemanagement.data.entity.BaseInfo;
import java.util.List;

/**
 * Created by Mahyar on 6/20/2015.
 */
public class BaseInfoList extends BaseModel {

  private List<BaseInfo> baseInfoList;

  public List<BaseInfo> getBaseInfoList() {
    return baseInfoList;
  }

  public void setBaseInfoList(List<BaseInfo> baseInfoList) {
    this.baseInfoList = baseInfoList;
  }
}
