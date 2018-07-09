package com.github.anddd7.book.models;

import com.github.anddd7.book.annotation.NotThreadSafe;

@NotThreadSafe
public class LazyInitRace {

  private Object instance = null;

  public Object getInstance() {
    if (instance == null) {
      instance = new Object();
    }
    return instance;
  }
}
