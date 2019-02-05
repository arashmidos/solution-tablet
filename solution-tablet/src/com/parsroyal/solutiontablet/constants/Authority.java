package com.parsroyal.solutiontablet.constants;

public enum Authority {
  COLD(69001L),
  HOT(69002L),
  DISTRIBUTOR(69003L),
  ADD_NEW_CUSTOMER(69011L),
  ADD_SUB_QUESTIONNAIRE(69012L),
  ADD_ORDER(69013L),
  ADD_REJECT(69014L),
  ADD_PAYMENT(69015L),
  ADD_QUESTIONNAIRE(69016L),
  ADD_PICTURE(69017L),
  ADD_DELIVERY(69018L),
  ADD_LOCATION(69019L),
  ADD_PHONE_CUSTOMER(69020L),
  ADD_PHONE_ORDER(69021L),
  UNLIMITED_DISTRIBUTE(69022L),
  ADD_FREE_ORDER(69023L),
  EDIT_CUSTOMER(69024L);

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
