package com.parsroyal.solutiontablet.util;

import com.parsroyal.solutiontablet.exception.InvalidDateStringException;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.TimeZone;

/**
 * Created by h.arbaboon on 6/2/2014.
 */
public class DateUtil {

  //days of week
  public static final String SATURDAY = "شنبه";
  public static final String SUNDAY = "یکشنبه";
  public static final String MONDAY = "دوشنبه";
  public static final String TUESDAY = "سه شنبه";
  public static final String WEDNESDAY = "چهارشنبه";
  public static final String THURSDAY = "پنجشنبه";
  public static final String FRIDAY = "جمعه";

  public static final String DATE_DEFINER_TODAY = "today";
  public static final String DATE_DEFINER_YESTERDAY = "yesterday";
  public static final String DATE_DEFINER_TOMORROW = "tomorrow";
  public static final String DATE_DEFINER_START_WEEK = "startWeek";
  public static final String DATE_DEFINER_END_WEEK = "endWeek";
  public static final String DATE_DEFINER_START_MONTH = "startMonth";
  public static final String DATE_DEFINER_END_MONTH = "endMonth";
  public static final String DATE_DEFINER_START_YEAR = "startYear";
  public static final String DATE_DEFINER_END_YEAR = "endYear";

  public static final SimpleDateFormat FULL_FORMATTER = new SimpleDateFormat("yyyy/MM/dd");
  public static final SimpleDateFormat FULL_FORMATTER_WITH_TIME = new SimpleDateFormat(
      "yyyy/MM/dd - HH:mm:ss");
  public static final SimpleDateFormat GLOBAL_FORMATTER = new SimpleDateFormat("yy/MM/dd");
  public static final SimpleDateFormat FISCAL_YEAR_FORMAT = new SimpleDateFormat("yyyy");
  public static final SimpleDateFormat GLOBAL_FORMATTER_GREGORIAN = new SimpleDateFormat(
      "yy-MM-dd");
  public static final SimpleDateFormat FULL_FORMATTER_GREGORIAN = new SimpleDateFormat(
      "yyyy-MM-dd");
  public static final SimpleDateFormat FULL_FORMATTER_GREGORIAN_WITH_TIME = new SimpleDateFormat(
      "yyyy-MM-dd - HH:mm:ss");
  public static final SimpleDateFormat TIME_24 = new SimpleDateFormat("HH:mm:ss");
  static String[] monthNames = {"فروردین",
      "اردیبهشت",
      "خرداد",
      "تیر",
      "مرداد",
      "شهریور",
      "مهر",
      "آبان",
      "آذر",
      "دی",
      "بهمن",
      "اسفند"
  };

  static {
    FULL_FORMATTER.setCalendar(new HijriShamsiCalendar());
    GLOBAL_FORMATTER.setCalendar(new HijriShamsiCalendar());
    FISCAL_YEAR_FORMAT.setCalendar(new HijriShamsiCalendar());
    FULL_FORMATTER_WITH_TIME.setCalendar(new HijriShamsiCalendar());
    TIME_24.setCalendar(new HijriShamsiCalendar());
  }

  public static String getTodayDate(String locale) {
    return convertDate(new Date(), FULL_FORMATTER, locale);
  }

  public static Integer getCurrentFiscalYear() {
    return Integer.parseInt(FISCAL_YEAR_FORMAT.format(new Date()));
  }

  public static String convertDate(Date date, SimpleDateFormat formatter, String locale) {
    if (locale.equalsIgnoreCase("EN")) {
      GregorianCalendar gregorianCalendar = new GregorianCalendar();
      formatter.setCalendar(gregorianCalendar);
    }
    String englishDate = formatter.format(date);
    englishDate = NumberUtil.digitsToEnglish(englishDate);
    return englishDate;
  }

  public static String getCurrentYearStartDate() {
    String result = GLOBAL_FORMATTER.format(new Date());
    String[] split = result.split("[/]");
    return split[0] + "/" + "01" + "/" + "01";
  }

  public static String getCurrentYearEndDate() {
    String result = GLOBAL_FORMATTER.format(new Date());
    String[] split = result.split("[/]");
    if (HijriShamsiCalendar.isLeap(Integer.parseInt(split[0]))) {
      return split[0] + "/" + "12" + "/" + "30";
    }
    return split[0] + "/" + "12" + "/" + "29";
  }

  public static String getCurrentMonthStartDate() {
    String result = GLOBAL_FORMATTER.format(new Date());
    String[] split = result.split("[/]");
    return split[0] + "/" + split[1] + "/" + "01";
  }

  public static Date addDaysToDate(Date date, int days, boolean clearTime) {
    Calendar cal = Calendar.getInstance();
    cal.setTime(date);
    cal.set(Calendar.DATE, cal.get(Calendar.DATE) + days);
    if (clearTime) {
      cal.set(Calendar.HOUR_OF_DAY, 0);
      cal.set(Calendar.MINUTE, 0);
      cal.set(Calendar.SECOND, 0);
      cal.set(Calendar.MILLISECOND, 0);
    }
    return cal.getTime();
  }

  public static int getToday() {
    Calendar cal = Calendar.getInstance();
    return cal.get(Calendar.DAY_OF_WEEK);
  }

  public static String getValueForDateDefiner(String value) {
    if (value.equals(DATE_DEFINER_TODAY)) {
      return getTodayDate("FA");
    } else if (value.equals(DATE_DEFINER_TOMORROW)) {
      return convertDate(addDaysToDate(new Date(), 1, false), FULL_FORMATTER, "FA");
    } else if (value.equals(DATE_DEFINER_YESTERDAY)) {
      return convertDate(addDaysToDate(new Date(), -1, false), FULL_FORMATTER, "FA");
    } else if (value.equals(DATE_DEFINER_START_MONTH)) {
      return getCurrentMonthStartDate();
    } else if (value.equals(DATE_DEFINER_START_YEAR)) {
      return getCurrentYearStartDate();
    } else if (value.equals(DATE_DEFINER_END_YEAR)) {
      return getCurrentYearEndDate();
    } else if (value.equals(DATE_DEFINER_START_WEEK)) {
      return getCurrentWeekStartDate();
    } else if (value.equals(DATE_DEFINER_END_WEEK)) {
      return getCurrentWeekEndDate();
    } else if (value.equals(DATE_DEFINER_END_MONTH)) {
      return getCurrentMonthEndDate();
    }
    return value;
  }

  public static String getCurrentMonthEndDate() {
    final Date now = new Date();
    String result = GLOBAL_FORMATTER.format(now);
    String[] split = result.split("[/]");
    final String dayNum;
    final int monthNum = Integer.parseInt(split[1]);
    if (monthNum <= 6) {
      dayNum = "31";
    } else if (monthNum <= 11) {
      dayNum = "30";
    } else {
      if (HijriShamsiCalendar.isLeap(Integer.valueOf(split[0]))) {
        dayNum = "30";
      } else {
        dayNum = "29";
      }
    }
    return split[0] + "/" + split[1] + "/" + dayNum;
  }

  public static String getCurrentWeekEndDate() {
    final int today = getToday();
    return convertDate(addDaysToDate(new Date(), 6 - today, false), FULL_FORMATTER, "FA");
  }

  public static String getCurrentWeekStartDate() {
    final int today = getToday();
    return convertDate(addDaysToDate(new Date(), -1 * today, false), FULL_FORMATTER, "FA");
  }

  public static String getLastYearForDate(String date) {
    final String[] strings = date.split("[/]");
    return (Integer.parseInt(strings[0]) - 1) + "/" + strings[1] + "/" + strings[2];

  }

  public static String getLastMonthForDate(String date) {
    final String[] strings = date.split("[/]");
    int year = Integer.parseInt(strings[0]);
    int month = Integer.parseInt(strings[1]);
    int day = Integer.parseInt(strings[2]);
    month--;
    if (month == 0) {
      month = 12;
      year--;
    }
    if (month > 6 && day > 30) {
      //to do do some thing for leap years
      day = 30;
    }
    DecimalFormat format = new DecimalFormat("00");
    return format.format(year) + "/" + format.format(month) + "/" + format.format(day);
  }

  public static String getLastDayForDate(String date) {
    final String[] strings = date.split("[/]");
    int year = Integer.parseInt(strings[0]);
    int month = Integer.parseInt(strings[1]);
    int day = Integer.parseInt(strings[2]);
    day--;
    if (day == 0) {
      month--;
    }
    if (month == 0) {
      month = 12;
      year--;
    }
    if (day == 0) {
      if (month <= 6) {
        day = 31;
      } else {
        //to do do something for leap years
        day = 30;
      }
    }
    DecimalFormat format = new DecimalFormat("00");
    return format.format(year) + "/" + format.format(month) + "/" + format.format(day);
  }

  public static boolean datesAreInSameYear(String date1, String date2) {
    final String[] strings1 = date1.split("[/]");
    final String[] strings2 = date2.split("[/]");
    return strings1[0].equals(strings2[0]);
  }

  public static String getMonthStartForDate(String date) {
    final String[] strings = date.split("[/]");
    return strings[0] + "/" + strings[1] + "/01";
  }

  public static String getMonthEndForDate(String date) {
    final String[] strings = date.split("[/]");
    String endDay = "/31";
    if (Integer.parseInt(strings[1]) > 6) {
      endDay = "/30";
    }

    if (Integer.parseInt(strings[1]) == 12 && !HijriShamsiCalendar
        .isLeap(Integer.valueOf(strings[0]))) {
      endDay = "/29";
    }
    return strings[0] + "/" + strings[1] + endDay;
  }

  public static String getYearStartForDate(String date) {
    final String[] strings = date.split("[/]");
    return strings[0] + "/01/01";
  }

  public static String getYearStartForYearNumber(int yearNumber) {
    if (yearNumber > 100) {
      yearNumber = yearNumber % 100;
    }
    return yearNumber + "/01/01";
  }

  public static List<String> getAllDatesInTimePeriod(String startDate, String endDate,
      boolean includePeriodDates) {
    if (startDate.equals(endDate)) {
      if (includePeriodDates) {
        ArrayList<String> result = new ArrayList<String>();
        result.add(startDate);
        return result;
      } else {
        return null;
      }
    }
    List<Date> dates = new ArrayList<Date>();
    final Date start = convertStringToDate(startDate, FULL_FORMATTER, "FA");
    if (includePeriodDates) {
      dates.add(start);
    }
    final Date end = convertStringToDate(endDate, FULL_FORMATTER, "FA");
    Date newDate = addDaysToDate(start, 1, true);
    while (!newDate.equals(end)) {
      dates.add(newDate);
      newDate = addDaysToDate(newDate, 1, true);
    }

    if (includePeriodDates) {
      dates.add(end);
    }
    List<String> result = new ArrayList<String>();
    for (Date date : dates) {
      result.add(convertDate(date, FULL_FORMATTER, "FA"));
    }
    return result;
  }

  public static Date convertStringToDate(String date, SimpleDateFormat formatter, String locale) {
    if (date.length() == 8 && !date.startsWith("13")) {
      date = "13" + date;
    }
    if (locale.equalsIgnoreCase("EN")) {
      GregorianCalendar gregorianCalendar = new GregorianCalendar();
      gregorianCalendar.setTimeZone(TimeZone.getTimeZone("Asia/Tehran"));
      formatter.setCalendar(gregorianCalendar);
    }
    try {
      return formatter.parse(date);
    } catch (Exception e) {
      throw new InvalidDateStringException(date);
    }
  }

  public static int compareDates(String date1, String date2) {
    if (date1.equals(date2)) {
      return 0;
    }
    if (dateGreaterThanOtherDate(date1, date2)) {
      return 1;
    }
    return -1;
  }

  public static boolean dateGreaterThanOtherDate(String date1, String date2) {
    Date dateOne = convertStringToDate(date1, FULL_FORMATTER, "FA");
    Date dateTwo = convertStringToDate(date2, FULL_FORMATTER, "FA");
    return dateOne.getTime() > dateTwo.getTime();
  }

  public static boolean dateGreaterEqualOtherDate(String date1, String date2) {
    Date dateOne = convertStringToDate(date1, FULL_FORMATTER, "FA");
    Date dateTwo = convertStringToDate(date2, FULL_FORMATTER, "FA");
    return dateOne.getTime() >= dateTwo.getTime();
  }

  public static boolean dateLessThanOtherDate(String date1, String date2) {
    Date dateOne = convertStringToDate(date1, FULL_FORMATTER, "FA");
    Date dateTwo = convertStringToDate(date2, FULL_FORMATTER, "FA");
    return dateOne.getTime() < dateTwo.getTime();
  }

  public static boolean dateLessEqualOtherDate(String date1, String date2) {
    Date dateOne = convertStringToDate(date1, FULL_FORMATTER, "FA");
    Date dateTwo = convertStringToDate(date2, FULL_FORMATTER, "FA");
    return dateOne.getTime() <= dateTwo.getTime();
  }

  public static Integer getDifferenceDateDayCount(String date1, String date2) {
    Long difference =
        convertStringToDate(date2, FULL_FORMATTER, "FA").getTime() - convertStringToDate(date1,
            FULL_FORMATTER, "FA").getTime();
    return Integer.parseInt(String.valueOf((difference) / (1000 * 60 * 60 * 24)));
  }

  public static String getPersianDayOfWeek(int day) {
    switch (day) {
      case Calendar.SATURDAY:
        return SATURDAY;
      case Calendar.SUNDAY:
        return SUNDAY;
      case Calendar.MONDAY:
        return MONDAY;
      case Calendar.TUESDAY:
        return TUESDAY;
      case Calendar.WEDNESDAY:
        return WEDNESDAY;
      case Calendar.THURSDAY:
        return THURSDAY;
      case Calendar.FRIDAY:
        return FRIDAY;
    }
    return "";
  }

  public static String getFullPersianDate(Date date) {
    Calendar calendar = Calendar.getInstance();
    calendar.setTime(date);
    int dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK);
    String dateString = DateUtil.convertDate(date, DateUtil.GLOBAL_FORMATTER, "FA");
    String[] splitDate = dateString.split("/");
    String monthName = monthNames[Integer.parseInt(splitDate[1]) - 1];
    return String.format("%s %s %s %s", getPersianDayOfWeek(dayOfWeek), splitDate[2], monthName,
        splitDate[0], monthName);
  }

  public static String moveDate(String date1, Integer count) {
    Long todayTime = convertStringToDate(date1, FULL_FORMATTER, "FA").getTime();
    todayTime += (1000 * 60 * 60 * 24) * count;
    Date date = new Date(todayTime);

    return convertDate(date, FULL_FORMATTER, "FA");
  }

  public static String getCurrentGregorianFullWithTimeDate() {
    return convertDate(new Date(), DateUtil.FULL_FORMATTER_GREGORIAN_WITH_TIME, "EN");
  }

  public static String getCurrentGregorianFullWithDate() {
    return convertDate(new Date(), DateUtil.FULL_FORMATTER_GREGORIAN, "EN");
  }

  /**
   * @return End time of given date, e.g. 23:59
   */
  public static Date endOfDay(Date date) {
    Calendar cal = Calendar.getInstance();
    cal.setTime(date);
    return endOfDay(cal);
  }

  /**
   * @return Start time of given date, 5:00 A.M
   */
  public static Date startOfDay(Date date) {
    Calendar cal = Calendar.getInstance();
    cal.setTime(date);
    return startOfDay(cal);
  }

  /**
   * @return End time of given date, e.g. 23:59
   */
  public static Date endOfDay(Calendar cal) {
    cal.set(Calendar.HOUR_OF_DAY, 23);
    cal.set(Calendar.MINUTE, 59);
    return cal.getTime();
  }

  /**
   * @return Start time of given date, 5:00 A.M
   */
  public static Date startOfDay(Calendar cal) {
    cal.set(Calendar.HOUR_OF_DAY, 5);
    cal.set(Calendar.MINUTE, 0);
    return cal.getTime();
  }

  /**
   * @return Subtraction of c2 and c1 in days
   */
  public static long compareDatesInDays(Calendar c1, Calendar c2) {
    long diff = c2.getTimeInMillis() - c1.getTimeInMillis();
    return diff / (24 * 60 * 60 * 1000);
  }


}