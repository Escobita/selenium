package org.openqa.selenium.ie;

public enum ErrorCode {
  SUCCESS(0),
  INDEX_OUT_OF_BOUNDS(-1),
  NO_COLLECTION(-2),
  NO_STRING(-3),
  NO_STRING_LENGTH(-4),
  NO_STRING_WRAPPER(-5),
  NO_SUCH_DRIVER(-6),
  NO_SUCH_ELEMENT(-7),
  NO_SUCH_FRAME(-8),
  NOT_IMPLEMENTED(-9),
  UNKNOWN_ERROR(-100);

	public static ErrorCode fromCode(int code) {
    for (ErrorCode error : values()) {
      if (error.getCode() == code) {
        return error;
      }
    }

    return UNKNOWN_ERROR;
  }

  public int getCode() {
    return code;
  }

  private ErrorCode(int code) {
    this.code = code;
  }

  private int code;
}
