package com.github.anddd7.book.models;

import com.github.anddd7.book.annotation.ThreadSafe;

@ThreadSafe
public class Sequence {

  private int value;

  public synchronized int next() {
    return value++;
  }
}
