package com.github.anddd7.book.basic;

import java.util.Map;

/**
 * 使用 synchronized object 构造的线程安全的 缓存servlet
 */
public class CachingAddition implements Servlet<Integer> {

  private final int max;

  public CachingAddition(final int max) {
    this.max = max;
  }

  private Integer lastFirst = 0;
  private Integer lastSecond = 0;
  private Integer lastAnswer = 0;
  private int hits;
  private int cacheHits;

  /**
   * 细粒度的使用synchronized, 可以提高方法的执行效率, 在必要的时候才进行同步
   * 即: 只在状态变量发生读写时考虑是否同步
   *
   * @see SynchronizedCachingAddition
   */
  @Override
  public void service(final Map<String, Integer> request, final Map<String, Integer> response) {
    Integer first = request.get("first");
    Integer second = request.get("second");
    Integer answer = null;
    synchronized (this) {
      ++hits;
      if (first.equals(lastFirst) && second.equals(lastSecond)) {
        answer = lastAnswer;
        ++cacheHits;
      }
    }
    if (answer == null) {
      // answer是临时变量, 执行时单一线程的first/second都是确定的, 因此answer也是确定的
      answer = first + second;
      synchronized (this) {
        lastFirst = first;
        lastSecond = second;
        lastAnswer = answer;
      }
    }
    response.put("answer", answer);
  }
}