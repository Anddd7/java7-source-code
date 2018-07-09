package com.github.anddd7.book.models;

import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

public class UnsafeCachingAddition implements Servlet<Integer> {

  private int max;

  public UnsafeCachingAddition(int max) {
    this.max = max;
  }

  private AtomicInteger lastFirst = new AtomicInteger(0);
  private AtomicInteger lastSecond = new AtomicInteger(0);
  private AtomicInteger lastAnswer = new AtomicInteger(0);

  /**
   * 单个内部变量的操作是原子性的, 但是多个变量/多个操作之间形成了新的竞态条件
   * get/set被人为的分拆到不同的地方, 并且无法保证原子性
   */
  @Override
  public void service(Map<String, Integer> request, Map<String, Integer> response) {
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

  public int getLastFirst() {
    return lastFirst.get();
  }

  /**
   * 模拟service方法中将get/set拆开执行的方式
   */
  public void getThenSet() {
    int now = lastFirst.get();
    if (now < max) {
      lastFirst.set(now + 1);
    }
  }
}