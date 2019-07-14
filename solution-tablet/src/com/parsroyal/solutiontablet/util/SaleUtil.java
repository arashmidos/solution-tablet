package com.parsroyal.solutiontablet.util;

import androidx.annotation.NonNull;
import com.parsroyal.solutiontablet.constants.SaleOrderStatus;

public class SaleUtil {

  public static boolean isDelivery(Long orderStatus) {
    return (SaleOrderStatus.DELIVERABLE.getId().equals(orderStatus)
        || SaleOrderStatus.DELIVERED.getId().equals(orderStatus) || SaleOrderStatus.DELIVERABLE_SENT
        .getId().equals(orderStatus));
  }


  public static boolean isRequestReject(long orderStatus) {
    return (SaleOrderStatus.REQUEST_REJECTED_DRAFT.getId().equals(orderStatus)
        || SaleOrderStatus.REQUEST_REJECTED.getId().equals(orderStatus) || SaleOrderStatus.REQUEST_REJECTED_SENT
        .getId().equals(orderStatus));
  }


  /*
  @return true if it's one of the REJECTED states
 */
  public static boolean isRejected(@NonNull Long orderStatus) {
    return (orderStatus.equals(SaleOrderStatus.REJECTED_DRAFT.getId()) ||
        orderStatus.equals(SaleOrderStatus.REJECTED.getId()) ||
        orderStatus.equals(SaleOrderStatus.REJECTED_SENT.getId()));
  }
}
