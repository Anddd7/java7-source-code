package com.github.anddd7.book.cache;

import java.util.concurrent.Callable;
import java.util.concurrent.CancellationException;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.FutureTask;

/**
 * - 使用ConcurrentHashMap缓存结果并保证读写的线程安全
 * - 使用Future作为缓存值的存放器, 当值还未准备好时, 线程会进行等待
 * - 对get/put进行同步
 */
public class Memorizer<A, V> implements Computable<A, V> {

  private final ConcurrentMap<A, Future<V>> cache = new ConcurrentHashMap<>();
  private final Computable<A, V> c;

  public Memorizer(Computable<A, V> c) {
    this.c = c;
  }

  @Override
  public V compute(final A arg) throws InterruptedException {
    while (true) {
      Future<V> f = cache.get(arg);
      if (f == null) {
        Callable<V> eval = new Callable<V>() {
          @Override
          public V call() throws Exception {
            return c.compute(arg);
          }
        };
        FutureTask ft = new FutureTask(eval);
        // 原子操作: 检查是否已存在值, 不存在则set, 存在则get
        f = cache.putIfAbsent(arg, ft);
        // 获取的值为null, 即之前不存在, 当前FutureTask是唯一的
        if (f == null) {
          f = ft;
          ft.run();
        }
      }

      try {
        return f.get();
      } catch (CancellationException e) {
        /*
        因为cache中存放的是future而不是实际的值, 当计算失败时cache中仍旧会存在无效的future对象
        当计算失败时我们希望重启这个计算流程, 因此我们在计算失败时删除task并重启(利用while)
        */
        cache.remove(arg, f);
      } catch (ExecutionException e) {
        throw launderThrowable(e);
      }
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

