package com.parsroyal.solutiontablet.util;

import com.parsroyal.solutiontablet.constants.Constants;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import timber.log.Timber;

public class BarcodeUtil {

  public static Map<String, String> extractCheckQR(String result) {
    if (Empty.isEmpty(result)) {
      return null;
    }
    String[] split = result.split("\n");

    if (split.length != 4) {
      if (split.length == 5) {
        split = Arrays.copyOfRange(split, 1, 5);
      } else {
        return null;
      }
    }
    Map<String, String> data = new HashMap<>();
    data.put(Constants.NATIONAL_CODE, split[0]);
    Timber.i("Check validation of national code %s", ValidationUtil.isValidNationalCode(split[0]));

    data.put(Constants.SHABA, split[1]);
    data.put(Constants.CHECK_SERIAL, split[3]);

    String[] bankDetails = split[2].split("_");//055_173
    data.put(Constants.BANK_CODE, bankDetails[0]);
    data.put(Constants.BANK_BRANCH_CODE, bankDetails[1]);

    return data;
  }
}
