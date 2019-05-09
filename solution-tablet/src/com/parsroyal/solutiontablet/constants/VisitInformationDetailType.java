package com.parsroyal.solutiontablet.constants;

import com.parsroyal.solutiontablet.R;

/**
 * Created by mahyar on 3/5/17.
 */
public enum VisitInformationDetailType {
  CREATE_ORDER(10L, R.string.title_order, R.drawable.ic_cart_24_dp, R.string.title_order),
  // Later, Need update image and title
  DELIVER_ORDER(11L, R.string.title_deliver_sale_order, R.drawable.ic_truck_24dp,
      R.string.title_deliver_sale_order),
  DELIVER_FREE_ORDER(12L, R.string.free_order, R.drawable.ic_add_layer_24dp, R.string.free_order),

  CREATE_REJECT(20L, R.string.title_reject, R.drawable.ic_return_24_dp, R.string.title_reject),
  CREATE_INVOICE(30L, R.string.title_factor, R.drawable.ic_cart_24_dp,
      R.string.title_factor),// Later,Need update image
  TAKE_PICTURE(40L, R.string.images, R.drawable.ic_camera_24_dp, R.string.images),
  FILL_QUESTIONNAIRE(50L, R.string.questionnaires_x, R.drawable.ic_assignment_blue_24dp,
      R.string.questionnaire),
  SAVE_LOCATION(60L, R.string.location, R.drawable.ic_location_on_24dp, R.string.salesman_location),
  CASH(70L, R.string.payments_x, R.drawable.ic_attach_money_green_24dp, R.string.visit_payment),
  NO_ORDER(98L, R.string.title_no_order, R.drawable.ic_non_action_24_dp, R.string.title_no_order),
  NONE(99L, R.string.not_visited, R.drawable.ic_non_action_24_dp, R.string.not_visited);

  private final int title;
  private final int drawable;
  private final int visitTitle;
  private long value;

  VisitInformationDetailType(long value, int title, int drawable, int visitTitle) {
    this.value = value;
    this.title = title;
    this.visitTitle = visitTitle;
    this.drawable = drawable;
  }

  public static VisitInformationDetailType getByValue(long value) {
    VisitInformationDetailType found = null;
    for (VisitInformationDetailType visitType : VisitInformationDetailType.values()) {
      if (visitType.getValue() == value) {
        found = visitType;
      }
    }
    return found;
  }

  public int getVisitTitle() {
    return visitTitle;
  }

  public long getValue() {
    return value;
  }

  public void setValue(long value) {
    this.value = value;
  }

  public int getTitle() {
    return title;
  }

  public int getDrawable() {
    return drawable;
  }

}
