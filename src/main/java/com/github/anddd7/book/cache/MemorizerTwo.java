package com.github.anddd7.book.cache;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * - 使用ConcurrentHashMap缓存结果并保证读写的线程安全
 *
 * 缺点在于:
 * get/put操作是原子的, 但check-then-act的操作打破了原子性
 * 线程A在compute时, 线程B获取到的result仍为null, 可能会触发多次compute
 */
public class MemorizerTwo<A, V> implements Computable<A, V> {

  private final Map<A, V> cache = new ConcurrentHashMap<>();
  private final Computable<A, V> c;

  public MemorizerTwo(Computable<A, V> c) {
    this.c = c;
  }

  @Override
  public V compute(A arg) throws InterruptedException {
    V result = cache.get(arg);
    if (result == null) {
      result = c.compute(arg);
      cache.put(arg, result);
    }
    return result;
  }
}

