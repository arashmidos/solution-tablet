package com.parsroyal.solutiontablet.constants;

import com.parsroyal.solutiontablet.R;

/**
 * Created by mahyar on 3/5/17.
 */
public enum VisitInformationDetailType {
  CREATE_ORDER(10L, R.string.title_order, R.drawable.ic_cart_24_dp),
  DELIVER_ORDER(11L, R.string.title_order, R.drawable.ic_cart_24_dp),// Later, Need update image and title
  CREATE_REJECT(20L, R.string.title_reject, R.drawable.ic_return_24_dp),
  CREATE_INVOICE(30L, R.string.title_factor, R.drawable.ic_cart_24_dp),// Later,Need update image
  TAKE_PICTURE(40L, R.string.images, R.drawable.ic_camera_24_dp),
  FILL_QUESTIONNAIRE(50L, R.string.questionnaires_x, R.drawable.ic_assignment_blue_24dp),
  SAVE_LOCATION(60L, R.string.location, R.drawable.ic_location_on_24dp),
  CASH(70L, R.string.payments_x,R.drawable.ic_attach_money_green_24dp),
  NO_ORDER(98L, R.string.title_no_order, R.drawable.ic_non_action_24_dp),
  NONE(99L, R.string.not_visited, R.drawable.ic_non_action_24_dp);

  private final int title;
  private final int drawable;
  private long value;

  VisitInformationDetailType(long value, int title, int drawable) {
    this.value = value;
    this.title = title;
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
