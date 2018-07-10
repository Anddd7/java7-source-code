package com.github.anddd7.book.basic;

import com.github.anddd7.book.annotation.NotThreadSafe;
import java.util.Map;

/**
 * write/read 造成的线程不安全
 */
@NotThreadSafe
public class UnsafeCountingAddition implements Servlet<Integer>,Counting {

  private int count = 0;

  @Override
  public int getCount() {
    return count;
  }

  /**
   * 竞态条件(Race Condition)
   * 计算结果的正确性依赖于操作执行的顺序: 读写;先检查再执行(Check-Then-Act)
   *
   * @see UnsafeSequence
   * @see LazyInitRace
   */
  @Override
  public void service(final Map<String, Integer> request, final Map<String, Integer> response) {
    ++count;
    Integer first = request.get("first");
    Integer second = request.get("second");
    response.put("answer", first + second);
    response.put("count", count);
  }
}
