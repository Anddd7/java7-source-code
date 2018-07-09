package com.github.anddd7.book.models;

import com.github.anddd7.book.annotation.ThreadSafe;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

@ThreadSafe
public class AtomicCountingAddition implements Servlet<Integer> {

  private AtomicInteger count = new AtomicInteger(0);

  public int getCount() {
    return count.get();
  }

  /**

   * 使用原子类Atomic保存状态, '状态'线程安全=servlet线程安全
   */
  @Override
  public void service(Map<String, Integer> request, Map<String, Integer> response) {
    int actualCount = count.incrementAndGet();
    Integer first = request.get("first");
    Integer second = request.get("second");
    response.put("answer", first + second);
    response.put("count", actualCount);
  }
}
