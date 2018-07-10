package com.github.anddd7.book.basic;

import com.github.anddd7.book.annotation.ThreadSafe;
import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * '装饰器'+ '监视器' 的工厂方法
 *
 * @see java.util.Collections#synchronizedCollection(Collection)
 * @see java.util.Collections#synchronizedList(List)
 * @see java.util.Collections#synchronizedMap(Map)
 */
@ThreadSafe
public class PrivateLock {

  private final Object lock = new Object();
  private int count;

  void add() {
    synchronized (lock) {
      count++;
    }
  }
}
