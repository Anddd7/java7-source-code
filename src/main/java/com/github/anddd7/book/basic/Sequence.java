package com.github.anddd7.book.basic;

import com.github.anddd7.book.annotation.ThreadSafe;

/**
 * 使用 synchronized method 的线程安全类
 */
@ThreadSafe
public class Sequence {

  private int value;

  public synchronized int next() {
    return value++;
  }
}
