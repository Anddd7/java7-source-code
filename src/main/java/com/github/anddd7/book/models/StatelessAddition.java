package com.github.anddd7.book.models;

import com.github.anddd7.book.annotation.ThreadSafe;
import java.util.Map;

@ThreadSafe
public class StatelessAddition implements Servlet<Integer> {

  /**
   * 所有变量都是从外部读取或者暂存在方法内的临时变量
   * 对这个Servlet来说, 它是无状态的(无内部熟悉)
   * 因此一定是线程安全的(对此对象来说): 在任何时候任何线程内对相同输入输出都相同
   *
   * ps: 'Thread Safe'是针对类/对象来说的
   * 虽然service方法包含非原子操作, 但对于每一个请求(输入)的回复都是一定的(输出)
   * 不会因为多个请求同时访问servlet就会不同
   *
   * @see UnsafeCountingAddition
   * 而在有状态的servlet中, 对于请求的回复还依赖于内部变量
   * 而这个变量会被多线程访问和修改, 就可能出现非预期的结果
   */
  @Override
  public void service(Map<String, Integer> request, Map<String, Integer> response) {
    Integer first = request.get("first");
    Integer second = request.get("second");
    response.put("answer", first + second);
  }
}
