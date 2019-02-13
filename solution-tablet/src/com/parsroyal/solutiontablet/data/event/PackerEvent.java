package com.parsroyal.solutiontablet.data.event;

import com.parsroyal.solutiontablet.data.model.Packer;

public class PackerEvent extends Event {

  private Packer detailList;

  public PackerEvent(Packer detailList) {
    this.detailList = detailList;
  }

  public Packer getDetailList() {
    return detailList;
  }

  public void setDetailList(Packer detailList) {
    this.detailList = detailList;
  }
}
