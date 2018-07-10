package com.github.anddd7.book.basic;

import com.github.anddd7.book.annotation.NotThreadSafe;

/**
 * write/read 造成的线程不安全
 */
@NotThreadSafe
public class UnsafeSequence {

  private int value;

  /**
   * 'value++' equals 'value=value+1'
   */
  public int next() {
    return value++;
  }
}
