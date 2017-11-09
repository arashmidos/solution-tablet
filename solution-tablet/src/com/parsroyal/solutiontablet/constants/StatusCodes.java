package com.parsroyal.solutiontablet.constants;

import android.content.Context;
import com.parsroyal.solutiontablet.R;
import com.parsroyal.solutiontablet.SolutionTabletApplication;
import com.parsroyal.solutiontablet.util.Empty;

/**
 * @author Arash
 */
public enum StatusCodes {
  SUCCESS(200, R.string.data_transfered_successfully),
  NO_NETWORK(1001, R.string.error_no_network),
  INVALID_DATA(1002, R.string.message_exception_in_data_transfer),
  NETWORK_ERROR(1003,
      R.string.com_parsroyal_solutiontablet_exception_BackendIsNotReachableException),
  DATA_STORE_ERROR(1004, R.string.message_exception_in_data_store),
  SERVER_ERROR(1100, R.string.com_parsroyal_solutiontablet_exception_InternalServerError),
  PERMISSION_DENIED(2001, R.string.permission_denied_explanation),
  NEW_GPS_LOCATION(2002, R.string.message_new_gps_location),
  ACTION_ADD_ORDER(901, R.string.register_order),
  ACTION_ADD_PAYMENT(902, R.string.register_payment),
  ACTION_EXIT_VISIT(903, R.string.exit_end_visit),
  ACTION_START_CAMERA(904, R.string.add_picture),
  ;

  private int statusCode;
  private int message;

  StatusCodes(int statusCode, int message) {
    this.statusCode = statusCode;
    this.message = message;
  }

  public static String getDisplayTitle(Context context, int status) {
    StatusCodes foundStatus = findByStatusCode(status);
    if (Empty.isNotEmpty(foundStatus)) {
      return context.getString(foundStatus.getMessage());
    } else {
      return "";
    }
  }

  private static StatusCodes findByStatusCode(int statusCode) {
    for (StatusCodes status : StatusCodes.values()) {
      if (status.getId() == statusCode) {
        return status;
      }
    }
    return null;
  }

  public int getId() {
    return statusCode;
  }

  public void setId(int statusCode) {
    this.statusCode = statusCode;
  }

  public int getMessage() {
    return message;
  }

  public void setMessage(int message) {
    this.message = message;
  }

  @Override
  public String toString() {
    return SolutionTabletApplication.getInstance().getString(message);
  }
}
