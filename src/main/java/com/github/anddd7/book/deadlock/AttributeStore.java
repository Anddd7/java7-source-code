package com.github.anddd7.book.deadlock;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

public class AttributeStore {

  private final Map<String, String> attributes = new HashMap<>();

  /**
   * 锁粒度太大
   */
  public synchronized boolean userLocationMatches_0(String name, String regexp) {
    String key = "users." + name + ".location";
    String location = attributes.get(key);
    if (location == null) {
      return false;
    } else {
      return Pattern.matches(regexp, location);
    }
  }

  /**
   * 关键在于对attributes的get需要同步
   * - get过后就只有本地变量的操作, 不再需要同步
   */
  public boolean userLocationMatches(String name, String regexp) {
    String key = "users." + name + ".location";
    String location;
    synchronized (this) {
      location = attributes.get(key);
    }
    if (location == null) {
      return false;
    } else {
      return Pattern.matches(regexp, location);
    }
  }
}
