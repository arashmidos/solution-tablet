package com.parsroyal.solutiontablet.constants

/**
 * Created by Arash on 2018-01-26
 */
enum class SaleOrderStatus constructor(var id: Long?, var title: String?) {
  GIFT(10L, "GIFT"),
  INVOICE_GIFT(11L, "INVOICE_GIFT"),
  DRAFT(90001L, "DRAFT"),
  SENT(90002L, "SENT"),
  DELIVERABLE(90003L, "DELIVERABLE"),
  INVOICED(90004L, "INVOICED"),
  SENT_INVOICE(90005L, "SENT_INVOICE"),
  READY_TO_SEND(90006L, "READY_TO_SEND"),
  CANCELED(90007L, "CANCELED"),
  REJECTED_DRAFT(90008L, "REJECTED_DRAFT"),
  REJECTED(90009L, "REJECTED"),
  REJECTED_SENT(90010L, "REJECTED_SENT"),
  DELIVERED(90011L, "DELIVERED"),
  DELIVERABLE_SENT(90012L, "DELIVERABLE_SENT"),
  FREE_ORDER_DRAFT(90013L, "FREE_ORDER_DRAFT"),
  FREE_ORDER_DELIVERED(90014L, "FREE_ORDER_DELIVERED"),
  FREE_ORDER_SENT(90015L, "FREE_ORDER_SENT"),
  REQUEST_REJECTED_DRAFT(90016L, "REQUEST_REJECTED_DRAFT"),
  REQUEST_REJECTED(90017L, "REQUEST_REJECTED"),
  REQUEST_REJECTED_SENT(90018L, "REQUEST_REJECTED_SENT");

  val stringId: String
    get() = id.toString()

  companion object {

    fun findById(statusId: Long): SaleOrderStatus? {
      for (saleOrderStatus in SaleOrderStatus.values()) {
        if (saleOrderStatus.id == statusId) {
          return saleOrderStatus
        }
      }
      return null
    }
  }
}
