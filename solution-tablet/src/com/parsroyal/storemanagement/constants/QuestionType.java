package com.parsroyal.storemanagement.constants;

/**
 * Created by Mahyar Sefidi <mailto:mahyarsefidi1@gmail.com>
 * on 2/12/17.
 */
public enum QuestionType {
  SIMPLE(100),
  SIMPLE_NUMERIC(101),
  CHOICE_SINGLE(102),
  CHOICE_MULTIPLE(103),
  DATE(104),;

  private int value;

  QuestionType(int value) {
    this.value = value;
  }

  public static QuestionType getByValue(int value) {
    QuestionType found = null;
    for (QuestionType questionType : QuestionType.values()) {
      if (questionType.getValue() == value) {
        found = questionType;
      }
    }
    return found;
  }

  public int getValue() {
    return value;
  }

  public void setValue(int value) {
    this.value = value;
  }
}
