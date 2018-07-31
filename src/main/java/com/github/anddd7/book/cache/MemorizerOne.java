package com.github.anddd7.book.cache;

import java.util.HashMap;
import java.util.Map;

/**
 * - 使用HashMap缓存结果
 * - 使用synchronized保证线程安全
 *
 * 缺点在于:
 * c.compute可能非常耗时, synchronized method阻塞了所有缓存的获取
 * 真正需要同步的是对 "同一缓存项的get/put操作"
 */
public class MemorizerOne<A, V> implements Computable<A, V> {

  private final Map<A, V> cache = new HashMap<>();
  private final Computable<A, V> c;

  public MemorizerOne(Computable<A, V> c) {
    this.c = c;
  }

  @Override
  public synchronized V compute(A arg) throws InterruptedException {
    // 检查缓存
    V result = cache.get(arg);
    if (result == null) {
      // 重新计算结果并暂存在cache中
      result = c.compute(arg);
      cache.put(arg, result);
    }
    return result;
  }
}

