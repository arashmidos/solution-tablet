package com.parsroyal.storemanagement.util;

public class ValidationUtil {

  public static boolean isValidNationalCode(String nationalCode) {
    if (Empty.isEmpty(nationalCode) || nationalCode.length() != 10) {
      return false;
    } else {
      int sum = 0;
      int lenght = 10;
      for (int i = 0; i < lenght - 1; i++) {
        sum += Integer.parseInt(String.valueOf(nationalCode.charAt(i))) * (lenght - i);
      }

      int r = Integer.parseInt(String.valueOf(nationalCode.charAt(9)));

      int c = sum % 11;

      return (((c < 2) && (r == c)) || ((c >= 2) && ((11 - c) == r)));
    }
  }
}
