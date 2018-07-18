package com.github.anddd7.jdk7.util.concurrent;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

/**
 * @see ArrayBlockingQueue
 *
 * 阻塞的单向队列
 * -  必须指定容量
 * -  使用1个lock, 多线程访问时, 只有一个线程能够读/写
 * -  使用2个Condition, 会将不满足条件的线程挂起
 */
public class ArrayBlockingQueueTests<E> {

  private static void checkNotNull(Object v) {
    if (v == null) {
      throw new NullPointerException();
    }
  }

  Object[] items;
  int count;
  int putIndex;

  ReentrantLock lock;
  private Condition notEmpty;
  private Condition notFull;

  /**
   * @see ArrayBlockingQueue#put(Object)
   */
  public void put(E e) throws InterruptedException {
    checkNotNull(e);
    // 加锁
    final ReentrantLock lock = this.lock;
    lock.lockInterruptibly();
    try {
      // 如果队列满则等待
      while (count == items.length) {
        notFull.await();// 当前线程挂起, 锁释放
      }
      insert(e);
    } finally {
      lock.unlock();
    }
  }

  /**
   * @see ArrayBlockingQueue#offer(Object, long, TimeUnit)
   */
  public boolean offer(E e, long timeout, TimeUnit unit) throws InterruptedException {
    checkNotNull(e);
    long nanos = unit.toNanos(timeout);
    final ReentrantLock lock = this.lock;
    lock.lockInterruptibly();
    try {
      while (count == items.length) {
        // 超时失败
        if (nanos <= 0) {
          return false;
        }
        // 等待指定时间
        nanos = notFull.awaitNanos(nanos);// 当前线程挂起, 锁释放
      }
      insert(e);
      return true;
    } finally {
      lock.unlock();
    }
  }

  /**
   * @see ArrayBlockingQueue#insert(Object)
   */
  private void insert(E x) {
    items[putIndex] = x;
    putIndex = inc(putIndex);
    ++count;
    // 提示所有因"队列为空"等待的线程
    notEmpty.signal();
  }

  /**
   * 计算新增元素存放位置
   */
  final int inc(int i) {
    return (++i == items.length) ? 0 : i;
  }
}
