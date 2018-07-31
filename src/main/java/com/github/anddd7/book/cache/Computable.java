package com.github.anddd7.book.cache;

public interface Computable<A, V> {

  /**
   * 阻塞方法, 用于获取A对应的对象V
   */
  V compute(A arg) throws InterruptedException;
}
