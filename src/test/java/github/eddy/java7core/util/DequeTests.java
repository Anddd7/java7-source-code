package github.eddy.java7core.util;

import java.util.Deque;
import java.util.Queue;

/**
 * @author Anddd7
 * {@link Deque} extends {@link Queue}
 * {@link DequeTests} extends {@link QueueTests}
 */
public class DequeTests {

  /**
   * ---- 增加对队首队尾的操作 ----
   *
   * {@link Deque#addFirst(Object)}
   * {@link Deque#addLast(Object)}
   * {@link Deque#removeFirst()}
   * {@link Deque#removeLast()}
   * {@link Deque#getFirst()}
   * {@link Deque#getLast()}
   * - 超界异常
   *
   * {@link Deque#offerFirst(Object)}
   * {@link Deque#offerLast(Object)}
   * {@link Deque#pollFirst()}
   * {@link Deque#pollLast()}
   * {@link Deque#peekFirst()}
   * {@link Deque#peekLast()}
   * - 超界返回 false/null
   *
   * {@link Deque#removeFirstOccurrence(Object)}
   * {@link Deque#removeLastOccurrence(Object)}
   * - 查询队列
   *
   * ---- 增加对 stack 的支持 ----
   *
   * {@link Deque#push(Object)}
   * - 压栈 ,栈满会抛出异常
   *
   * {@link Deque#pop()}
   * - 出栈 ,栈空会抛出异常
   *
   * ---- 强制重写 Collection 实现的一些方法(如 AbstractCollection ) ----
   * 基于队列的方法去实现 Collection 的方法 ,减少代码重复
   * ----------------------------------------------------------------
   */
  public DequeTests() {
  }
}