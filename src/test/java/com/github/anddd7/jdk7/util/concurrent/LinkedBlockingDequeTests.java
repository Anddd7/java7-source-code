package com.github.anddd7.jdk7.util.concurrent;

import java.util.concurrent.LinkedBlockingDeque;

/**
 * @see LinkedBlockingDeque
 * 使用双向链表实现的双向队列
 * -  因为双向链表, 可能对同一个位置进行读写, 因此读写需要同步: 使用了1个lock 和 2个Condition
 */
public class LinkedBlockingDequeTests {

  /**
   * 类似其他2个阻塞队列
   *
   * @see LinkedBlockingQueueTests
   * @see ArrayBlockingQueueTests
   */
  public LinkedBlockingDequeTests() {
  }
}
