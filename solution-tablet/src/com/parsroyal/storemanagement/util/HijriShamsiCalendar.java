package com.parsroyal.storemanagement.util;

import java.util.Arrays;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by h.arbaboon on 6/2/2014.
 */
public class HijriShamsiCalendar extends GregorianCalendar {

  private static final List<Integer> LEAP_YEAR_MODES = Arrays.asList(1, 5, 9, 13, 17, 22, 26, 30);

  private static final Double GREGORIAN_EPOCH = 1721425.5;
  private static final Double HIJRI_SHAMSI_EPOCH = 1948320.5;

  public HijriShamsiCalendar() {
    super(Calendar.getInstance().getTimeZone());
  }

  private static Double gregorianToDouble(int year, int month, int day) {
    return (GREGORIAN_EPOCH - 1)
        + (365 * (year - 1))
        + Math.floor((year - 1) / 4)
        + (-Math.floor((year - 1) / 100))
        + Math.floor((year - 1) / 400)
        + Math.floor((((367 * month) - 362) / 12)
        + ((month <= 2) ? 0 : (leapGregorian(year) ? -1 : -2))
        + day);
  }

  private static Double hijriShamsiToDouble(int year, int month, int day) {
    int epbase, epyear;

    epbase = year - ((year >= 0) ? 474 : 473);
    epyear = 474 + mod(epbase, 2820).intValue();

    return day
        + ((month <= 7) ? ((month - 1) * 31) : (((month - 1) * 30) + 6))
        + Math.floor(((epyear * 682) - 110) / 2816) + (epyear - 1)
        * 365 + Math.floor(epbase / 2820) * 1029983
        + (HIJRI_SHAMSI_EPOCH - 1);
  }

  private static Map<String, Integer> toHijriShamsi(Double jd) {
    Double depoch, cycle, cyear, aux1, aux2, ycycle, yday, year, month, day;
    Map<String, Integer> hijriShamsiDate = new HashMap<String, Integer>();
    jd = Math.floor(jd) + 0.5;

    depoch = jd - hijriShamsiToDouble(475, 1, 1);
    cycle = Math.floor(depoch / 1029983);
    cyear = mod(depoch.intValue(), 1029983);
    if (cyear == 1029982) {
      ycycle = 2820.0;
    } else {
      aux1 = Math.floor(cyear / 366);
      aux2 = mod(cyear.intValue(), 366);
      ycycle = Math.floor(((2134 * aux1) + (2816 * aux2) + 2815) / 1028522)
          + aux1 + 1;
    }
    year = (ycycle + (2820 * cycle) + 474);
    if (year <= 0) {
      year--;
    }
    yday = (jd - hijriShamsiToDouble(year.intValue(), 1, 1)) + 1;
    month = (yday <= 186) ? Math.ceil(yday / 31) : Math.ceil((yday - 6) / 30);
    day = (jd - hijriShamsiToDouble(year.intValue(), month.intValue(), 1)) + 1;
    hijriShamsiDate.put("day", day.intValue());
    hijriShamsiDate.put("year", year.intValue());
    hijriShamsiDate.put("month", month.intValue());
    return hijriShamsiDate;
  }

  private static Map<String, Integer> toGregorian(Double jd) {
    Double wjd, depoch, quadricent, dqc, cent, dcent, quad, dquad, yindex, dyindex, year, yearday, month, day;
    Integer leapadj;
    Map<String, Integer> gregorianDate = new HashMap<String, Integer>();

    wjd = Math.floor(jd - 0.5) + 0.5;
    depoch = wjd - GREGORIAN_EPOCH;
    quadricent = Math.floor(depoch / 146097);
    dqc = mod(depoch.intValue(), 146097);
    cent = Math.floor(dqc / 36524);
    dcent = mod(dqc.intValue(), 36524);
    quad = Math.floor(dcent / 1461);
    dquad = mod(dcent.intValue(), 1461);
    yindex = Math.floor(dquad / 365);
    year = (quadricent * 400) + (cent * 100) + (quad * 4) + yindex;
    if (!((cent == 4) || (yindex == 4))) {
      year++;
    }
    yearday = wjd - gregorianToDouble(year.intValue(), 1, 1);
    leapadj = ((wjd < gregorianToDouble(year.intValue(), 3, 1)) ? 0
        : (leapGregorian(year.intValue()) ? 1 : 2));
    month = Math.floor((((yearday + leapadj) * 12) + 373) / 367);
    day = (wjd - gregorianToDouble(year.intValue(), month.intValue(), 1)) + 1;

    gregorianDate.put("day", day.intValue());
    gregorianDate.put("year", year.intValue());
    gregorianDate.put("month", month.intValue());

    return gregorianDate;
  }

  private static Boolean leapGregorian(int year) {
    return ((year % 4) == 0)
        && (!(((year % 100) == 0) && ((year % 400) != 0)));
  }

  private static Double mod(Integer a, Integer b) {
    return a - (b * Math.floor(a / b));
  }

  private static long mod2(double a, double b) {
    return (long) (a - b * Math.floor(a / b));
  }

  public static boolean isLeap(int year) {
    return LEAP_YEAR_MODES.contains(year % 33);
  }

  @Override
  protected void computeTime() {
    int year = fields[YEAR];
    int month = fields[MONTH] + 1;
    int day = fields[DAY_OF_MONTH];
    Double tm = hijriShamsiToDouble(year, month, day);
    Map<String, Integer> resultMap = toGregorian(tm);
    year = resultMap.get("year");
    month = resultMap.get("month") - 1;
    day = resultMap.get("day");
    fields[YEAR] = year;
    fields[MONTH] = month;
    fields[DAY_OF_MONTH] = day;
    super.computeTime();
    year = fields[YEAR];
    month = fields[MONTH] + 1;
    day = fields[DAY_OF_MONTH];
    tm = gregorianToDouble(year, month, day);
    resultMap = toHijriShamsi(tm);
    year = resultMap.get("year");
    month = resultMap.get("month") - 1;
    day = resultMap.get("day");
    fields[YEAR] = year;
    fields[MONTH] = month;
    fields[DAY_OF_MONTH] = day;
  }

  @Override
  protected void computeFields() {
    super.computeFields();
    int year = fields[YEAR];
    int month = fields[MONTH] + 1;
    int day = fields[DAY_OF_MONTH];
    Double tm = gregorianToDouble(year, month, day);
    Map<String, Integer> resultMap = toHijriShamsi(tm);
    year = resultMap.get("year");
    month = resultMap.get("month") - 1;
    day = resultMap.get("day");
    fields[YEAR] = year;
    fields[MONTH] = month;
    fields[DAY_OF_MONTH] = day;
  }

}
