package com.github.anddd7.book.basic;

import com.github.anddd7.book.annotation.NotThreadSafe;

/**
 * 常见的 check-then-act 错误
 */
@NotThreadSafe
public class LazyInitRace {

  private Object instance;

  public Object getInstance() {
    if (instance == null) {
      instance = new Object();
    }
    return instance;
  }
}
