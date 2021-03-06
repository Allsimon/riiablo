package com.riiablo.logger.message;

public interface Message {
  String format();
  String pattern();
  Object[] parameters();
  Throwable throwable();
}
