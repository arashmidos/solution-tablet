package com.parsroyal.solutiontablet.constants;

public enum Authority {
  COLD(69001L),
  HOT(69002L),
  DISTRIBUTOR(69003L),
  ADD_NEW_CUSTOMER(690011L),
  ADD_SUB_QUESTIONNAIRE(690012L),
  ADD_ORDER(690013L),
  ADD_REJECT(690014L),
  ADD_PAYMENT(690015L),
  ADD_QUESTIONNAIRE(690016L),
  ADD_PICTURE(690017L),
  ADD_DELIVERY(690018L),
  ADD_LOCATION(690019L),
  ADD_PHONE_CUSTOMER(690020L);

  private Long id;

  Authority(Long id) {
    this.id = id;
  }

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }
}
