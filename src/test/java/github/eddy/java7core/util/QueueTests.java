package github.eddy.java7core.util;

import java.util.Queue;

/**
 * @author Anddd7
 * @see Queue
 *
 * 单向队列 ,只能访问队头的元素 ,先入先出
 */
public class QueueTests {

  /**
   * --- 增队尾/取队首/查队首 ---
   *
   * {@link Queue#add(Object)}
   * {@link Queue#remove(Object)}
   * {@link Queue#element()}
   * - 超界时会抛出异常
   *
   * {@link Queue#offer(Object)}
   * {@link Queue#poll()}
   * {@link Queue#peek()}
   * - 超界时返回 false/null
   */
  public QueueTests() {
  }
}
