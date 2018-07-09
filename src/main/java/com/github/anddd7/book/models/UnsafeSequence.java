package com.github.anddd7.book.models;

import com.github.anddd7.book.annotation.NotThreadSafe;

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
