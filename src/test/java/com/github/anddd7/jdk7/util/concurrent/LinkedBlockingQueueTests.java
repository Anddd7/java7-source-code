package com.github.anddd7.jdk7.util.concurrent;

import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

/**
 * @see LinkedBlockingQueue
 * 使用单向链表实现的单向队列
 * -  使用了2个lock分别对读/写控制, 可以并发的读/写
 * -  使用了2个Condition, 可以将不满足条件的线程挂起
 */
public class LinkedBlockingQueueTests<E> {

  private final ReentrantLock takeLock = new ReentrantLock();
  private final Condition notEmpty = takeLock.newCondition();
  private final ReentrantLock putLock = new ReentrantLock();
  private final Condition notFull = putLock.newCondition();

  /**
   * 因为读写操作可能并发, 对count的修改需要使用atomic来保证原子性
   */
  private final AtomicInteger count = new AtomicInteger(0);

  private transient Node<E> head;
  private transient Node<E> last;
  private int capacity; // 链表最大长度

  /**
   * @see LinkedBlockingQueue#put(Object)
   * -  这里主流程只对写进行加锁, 因此在插入新元素时, 读线程可以对之前的元素进行操作
   */
  public void put(E e) throws InterruptedException {
    if (e == null) {
      throw new NullPointerException();
    }
    int c = -1;
    Node<E> node = new Node<>(e);
    final ReentrantLock putLock = this.putLock;
    final AtomicInteger count = this.count;
    // 加 写锁
    putLock.lockInterruptibly();
    try {
      while (count.get() == capacity) {
        // 队列满挂起
        notFull.await();
      }
      enqueue(node);
      // 原子更新后, 读线程就可以并发的读这个插入的元素了
      c = count.getAndIncrement();
      /*
        如果有多个写线程在进行写操作:
        - A因为没拿到锁在等待
        - B因为notFull而挂起
        - C正在执行

        C在插入完元素后如果队列还未满, 可以直接唤醒B开始下一步插入
        如果此处不进行notFull.signal(), 那么B必须等到一个读线程进行消费过后对他进行唤醒, 即使当前有足够的空间

        并且, 读线程在完成消费后, 需要对写操作加锁后再唤醒写操作(避免当前有一个写操作正在执行, 会导致队列满)
        如果此时由A线程抢到了锁, 那么得等待A线程进行检查再放弃锁; 然后唤醒操作才进行, B才能够开始争抢
       */
      if (c + 1 < capacity) {
        notFull.signal();
      }
    } finally {
      putLock.unlock();
    }
    if (c == 0) {
      // 唤醒因"空队列"而等待的线程
      signalNotEmpty();
    }
  }

  /**
   * @see LinkedBlockingQueue#enqueue(LinkedBlockingQueue.Node)
   */
  private void enqueue(Node<E> node) {
    // 添加元素到队尾
    last = last.next = node;
  }

  /**
   * @see LinkedBlockingQueue#signalNotEmpty()
   * - 唤醒读操作
   */
  private void signalNotEmpty() {
    final ReentrantLock takeLock = this.takeLock;
    takeLock.lock();
    try {
      notEmpty.signal();
    } finally {
      takeLock.unlock();
    }
  }

  static class Node<E> {

    E item;
    Node<E> next;

    Node(E x) {
      item = x;
    }
  }
}
