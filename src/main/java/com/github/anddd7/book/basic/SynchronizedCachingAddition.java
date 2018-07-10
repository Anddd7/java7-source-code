package com.github.anddd7.book.basic;

import java.util.Map;

/**
 * 使用 synchronized method 构造的线程安全的 缓存计数器
 */
public class SynchronizedCachingAddition implements Servlet<Integer> {

  private final int max;

  SynchronizedCachingAddition(final int max) {
    this.max = max;
  }

  private Integer lastFirst = 0;
  private Integer lastSecond = 0;
  private Integer lastAnswer = 0;

  /**
   * @deprecated synchronized关键字保证了方法能够同步执行, 但是粒度太粗效率很低(不必要的地方也被阻塞了)
   */
  @Deprecated
  @Override
  public synchronized void service(final Map<String, Integer> request,
      final Map<String, Integer> response) {
    Integer first = request.get("first");
    Integer second = request.get("second");
    if (first.equals(lastFirst) && second.equals(lastSecond)) {
      response.put("answer", lastAnswer);
    } else {
      Integer answer = first + second;
      lastFirst = first;
      lastSecond = second;
      lastAnswer = answer;
      response.put("answer", answer);
    }
  }

  /**
   * 模拟service中使用synchronized
   */
  public synchronized void methodSync() {
    if (lastFirst <= max) {
      lastFirst++;
    }
  }

  void objectSync() {
    synchronized (this) {
      if (lastFirst <= max) {
        lastFirst++;
      }
    }
  }

  int getLastFirst() {
    return lastFirst;
  }

  /**
   * synchronized是可重入的, 当线程获取锁后可以随意访问加锁代码块
   */
  synchronized int reduce(int i) {
    if (i == 0) {
      return i;
    }
    return i + reduce(--i);
  }
}