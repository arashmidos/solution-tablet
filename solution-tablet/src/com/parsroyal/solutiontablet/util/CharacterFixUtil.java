package com.parsroyal.solutiontablet.util;

/**
 * Created by IntelliJ IDEA.
 * User: h.arbaboon
 * Date: 10/15/11
 * Time: 3:55 PM
 * To change this template use File | Settings | File Templates.
 */
public class CharacterFixUtil {

  public static final char[] goodChars = {1740, 1705};
  public static final char[] badChars = {1610, 1603};

  public static String fixString(String value) {
    if (value == null) {
      return "";
    }

    String result = value;
    for (int i = 0; i < badChars.length; i++) {
      result = result.replace(badChars[i], goodChars[i]);
    }
    return result;
  }
}
