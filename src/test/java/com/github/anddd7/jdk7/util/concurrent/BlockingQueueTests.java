package com.github.anddd7.jdk7.util.concurrent;

import java.util.Collection;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;

/**
 * @see BlockingQueue
 *
 * 阻塞队列增加了阻塞读写的功能
 */
public class BlockingQueueTests {

  /**
   * @see BlockingQueue#put(Object)
   * -  区别于Queue中的 add/offer
   * -  阻塞线程直到成功插入元素
   * @see BlockingQueue#offer(Object, long, TimeUnit)
   * -  插入元素, 如果队列满则等待; 过期则返回false
   * @see BlockingQueue#take()
   * -  阻塞线程直到成功获取元素
   * @see BlockingQueue#take()
   * -  弹出元素, 如果队列空则等待; 过期则返回false
   * @see BlockingQueue#remainingCapacity()
   * -  剩余容量
   * @see BlockingQueue#drainTo(Collection)
   * @see BlockingQueue#drainTo(Collection, int)
   * -  一次性抽出队列中的元素
   */
  public BlockingQueueTests() {
  }
}
