package com.github.anddd7.book.basic;

import com.github.anddd7.book.annotation.NotThreadSafe;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 滥用/错误使用 原子类 构造的线程不安全缓存servlet
 */
@NotThreadSafe
public class UnsafeCachingAddition implements Servlet<Integer> {

  private final int max;

  UnsafeCachingAddition(int max) {
    this.max = max;
  }

  private final AtomicInteger lastFirst = new AtomicInteger(0);
  private final AtomicInteger lastSecond = new AtomicInteger(0);
  private final AtomicInteger lastAnswer = new AtomicInteger(0);

  /**
   * 单个内部变量的操作是原子性的, 但是多个变量/多个操作之间形成了新的竞态条件
   * get/set被人为的分拆到不同的地方, 并且无法保证原子性
   */
  @Override
  public void service(final Map<String, Integer> request, final Map<String, Integer> response) {
    Integer first = request.get("first");
    Integer second = request.get("second");
    if (first.equals(lastFirst.get()) && second.equals(lastSecond.get())) {
      response.put("answer", lastAnswer.get());
    } else {
      Integer answer = first + second;
      lastFirst.set(first);
      lastSecond.set(second);
      lastAnswer.set(answer);
      response.put("answer", answer);
    }
  }

  int getLastFirst() {
    return lastFirst.get();
  }

  /**
   * 模拟service方法中将get/set拆开执行的方式
   */
  void getThenSet() {
    int now = lastFirst.get();
    if (now < max) {
      lastFirst.set(now + 1);
    }
  }
}