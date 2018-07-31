package com.github.anddd7.book.cache;

import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.FutureTask;

/**
 * - 使用ConcurrentHashMap缓存结果并保证读写的线程安全
 * - 使用Future作为缓存值的存放器, 当值还未准备好时, 线程会进行等待
 *
 * 缺点在于:
 * compute方法中仍存在check-then-act, 可能出现多线程同时查询
 */
public class MemorizerThree<A, V> implements Computable<A, V> {

  private final Map<A, Future<V>> cache = new ConcurrentHashMap<>();
  private final Computable<A, V> c;

  public MemorizerThree(Computable<A, V> c) {
    this.c = c;
  }

  @Override
  public V compute(final A arg) throws InterruptedException {
    Future<V> f = cache.get(arg);
    if (f == null) {
      Callable<V> eval = new Callable<V>() {
        @Override
        public V call() throws Exception {
          return c.compute(arg);
        }
      };
      FutureTask ft = new FutureTask(eval);
      f = ft;
      cache.put(arg, ft);
      ft.run();
    }

    try {
      return f.get();
    } catch (ExecutionException e) {
      throw launderThrowable(e);
    }
  }

  @SuppressWarnings("Duplicates")
  private static RuntimeException launderThrowable(Throwable cause) {
    if (cause instanceof RuntimeException) {
      throw (RuntimeException) cause;
    } else if (cause instanceof Error) {
      throw (Error) cause;
    } else {
      return new IllegalStateException("Not unchecked:", cause);
    }
  }
}

