package com.github.anddd7.book.basic;

import com.github.anddd7.book.annotation.ThreadSafe;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 使用 原子类 构造的线程安全的 计数servlet
 */
@ThreadSafe
public class AtomicCountingAddition implements Servlet<Integer>, Counting {

  private final AtomicInteger count = new AtomicInteger(0);

  @Override
  public int getCount() {
    return count.get();
  }

  /**
   * 使用原子类Atomic保存状态, '状态'线程安全=servlet线程安全
   */
  @Override
  public void service(final Map<String, Integer> request, final Map<String, Integer> response) {
    int actualCount = count.incrementAndGet();
    Integer first = request.get("first");
    Integer second = request.get("second");
    response.put("answer", first + second);
    response.put("count", actualCount);
  }
}
