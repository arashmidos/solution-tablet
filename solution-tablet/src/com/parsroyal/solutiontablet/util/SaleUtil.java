package com.parsroyal.solutiontablet.util;

import com.parsroyal.solutiontablet.constants.SaleOrderStatus;

public class SaleUtil {

  public static boolean isDelivery(Long orderStatus) {
    return (SaleOrderStatus.DELIVERABLE.getId().equals(orderStatus)
        || SaleOrderStatus.DELIVERED.getId().equals(orderStatus) || SaleOrderStatus.DELIVERABLE_SENT
        .getId().equals(orderStatus));
  }
}
