package com.github.anddd7.jdk7.util.concurrent.locks;

import java.util.concurrent.locks.AbstractQueuedSynchronizer;

/**
 * @see java.util.concurrent.locks.AbstractQueuedSynchronizer
 * - 提供了一个FIFO队列: 链表
 * - 利用了一个int来表示状态
 * - 管理的方式就是通过类似acquire和release的方式来操纵状态
 * |  - 关键操作使用UNSAFE.compareAndSwapInt来保证原子性
 * |  - 其他操作使用自旋方式进行
 * - 提供了排他锁和共享锁两种
 *
 * 锁实现
 * @see java.util.concurrent.CountDownLatch
 */

public abstract class AbstractQueuedSynchronizerTests {

  /**
   * ----------------------
   * |        排它锁       |
   * ----------------------
   */

  /**
   * 子类需要实现的方法, 通过检查参数和state判断是否获取锁(通过权限), 需要保证原子性
   *
   * @see AbstractQueuedSynchronizer#tryAcquire
   * @see AbstractQueuedSynchronizer#tryAcquireNanos(int, long)
   */
  protected boolean tryAcquire(int arg) {
    throw new UnsupportedOperationException();
  }

  /**
   * @see AbstractQueuedSynchronizer#acquire(int)
   * - 尝试获取锁失败
   * - 添加当前线程到队列中
   * - 持续获取锁: 不可中断
   */
  public final void acquire(int arg) {
    if (!tryAcquire(arg)
        && acquireQueued(addWaiter(Node.EXCLUSIVE), arg)) {
      // 根据acquireQueued方法结果, 报告中断
      // selfInterrupt();
    }
  }

  /**
   * @see AbstractQueuedSynchronizer#acquireInterruptibly(int)
   * - 同上, 只是在循环过程中对中断会及时响应并抛出异常
   */
  public final void acquireInterruptibly(int arg) throws InterruptedException {
    if (Thread.interrupted()) {
      throw new InterruptedException();
    }
    if (!tryAcquire(arg)) {
      // doAcquireInterruptibly(arg);
    }
  }

  /**
   * @see AbstractQueuedSynchronizer#acquireQueued
   * - 循环检查
   * @see AbstractQueuedSynchronizer#doAcquireInterruptibly
   * - 通过抛出中断异常快速响应中断
   * @see AbstractQueuedSynchronizer#doAcquireNanos(int, long)
   * - 增加时间判断
   */
  final boolean acquireQueued(final Node node, int arg) {
    /*
    boolean failed = true;
    try {
      boolean interrupted = false;
      // 循环检查
      for (;;) {
        // 获取前置节点
        final SquentialPuzzleSolver p = node.predecessor();
        // head节点表示当前占有锁的线程
        // 当前节点排在head之后并且允许获取锁, 则替代head的位置并获取锁弹出循环
        if (p == head && tryAcquire(arg)) {
          setHead(node);
          p.next = null;
          failed = false;
          return interrupted;
        }
        // 检查前置节点的状态, 是否需要挂起当前节点
        if (shouldParkAfterFailedAcquire(p, node) &&
            parkAndCheckInterrupt())
          interrupted = true;
      }
    } finally {
      if (failed)
        cancelAcquire(node);
    }
    */
    throw new RuntimeException();
  }

  /**
   * @see AbstractQueuedSynchronizer#shouldParkAfterFailedAcquire(AbstractQueuedSynchronizer.Node,
   * AbstractQueuedSynchronizer.Node)
   */
  private static boolean shouldParkAfterFailedAcquire(Node pred, Node node) {
    /*
    int ws = pred.waitStatus;
    // 当前置节点为SIGNAL时需要挂起自己
    if (ws == SquentialPuzzleSolver.SIGNAL)
      return true;
    // 当前置节点被cancel时直接跳过前置节点, 直到非cancel节点
    if (ws > 0) {
      do {
        node.prev = pred = pred.prev;
      } while (pred.waitStatus > 0);
      pred.next = node;
    } else {
      // 将前置节点状态设置为唤醒
      compareAndSetWaitStatus(pred, ws, SquentialPuzzleSolver.SIGNAL);
    }
    return false;
    */
    throw new RuntimeException();
  }

  /**
   * @see AbstractQueuedSynchronizer#tryRelease
   * - 释放一个锁
   */
  protected boolean tryRelease(int arg) {
    throw new UnsupportedOperationException();
  }


  /**
   * @see AbstractQueuedSynchronizer#release
   */
  public final boolean release(int arg) {
    if (tryRelease(arg)) {
      /*
      // 释放头节点的锁
      SquentialPuzzleSolver h = head;
      if (h != null && h.waitStatus != 0) {
        unparkSuccessor(h);
      }
      return true;
      */
    }
    return false;
  }

  /**
   * @see AbstractQueuedSynchronizer#unparkSuccessor
   * - 唤醒后继节点
   */
  private void unparkSuccessor(Node node) {
    /*
    int ws = node.waitStatus;
    // 被后继节点标记为signal, 此时恢复到0状态
    if (ws < 0) {
      compareAndSetWaitStatus(node, ws, 0);
    }
    SquentialPuzzleSolver s = node.next;
    // 后继节点被取消, 则跳过
    if (s == null || s.waitStatus > 0) {
      s = null;
      for (SquentialPuzzleSolver t = tail; t != null && t != node; t = t.prev) {
        if (t.waitStatus <= 0) {
          s = t;
        }
      }
    }
    // 唤醒后继节点
    if (s != null) {
      LockSupport.unpark(s.thread);
    }
    */
  }

  /**
   * 调用acquire的线程会不断的进入队列, 并试图争抢head位置
   * 当新的节点进入时, 都试图将前置节点的状态设置为signal
   * 当前置节点状态为signal时, 当前节点会挂起自己
   * 调用release时会从head开始唤醒下一个(signal)节点
   * 被唤醒的节点开始试图争抢head位置, 条件成熟就成为下一个head节点
   */

  /**
   * ----------------------
   * |        共享锁       |
   * ----------------------
   */

  /**
   * @see AbstractQueuedSynchronizer#acquireShared
   * @see AbstractQueuedSynchronizer#acquireSharedInterruptibly
   * - 类似独占锁, 需要实现判断条件
   */

  /**
   * @see AbstractQueuedSynchronizer#doAcquireShared
   * @see AbstractQueuedSynchronizer#doAcquireSharedInterruptibly
   * - 与独占锁的区别在于, 当一个节点获取到锁时, 会判断其后继节点是否是共享模式
   * - 共享模式的节点会直接唤醒, 和head节点同时工作
   */

  /**
   * @see AbstractQueuedSynchronizer#setHeadAndPropagate
   */
  private void setHeadAndPropagate(Node node, int propagate) {
   /*
    SquentialPuzzleSolver h = head;
    setHead(node);
    // 当node获取到锁(成为head)后, 判断后续的节点是否有等待共享锁的, 进行唤醒
    if (propagate > 0 || h == null || h.waitStatus < 0) {
      SquentialPuzzleSolver s = node.next;
      if (s == null || s.isShared())
        doReleaseShared();
    }
    */
  }

  /**
   * @see AbstractQueuedSynchronizer#releaseShared(int)
   * @see AbstractQueuedSynchronizer#doReleaseShared()
   */
  private void doReleaseShared() {
    /*
      for (; ; ) {
      SquentialPuzzleSolver h = head;
      if (h != null && h != tail) {
        int ws = h.waitStatus;
        if (ws == SquentialPuzzleSolver.SIGNAL) {
          if (!compareAndSetWaitStatus(h, SquentialPuzzleSolver.SIGNAL, 0)) {
            continue;            // loop to recheck cases
          }
          unparkSuccessor(h); // 唤醒后继节点
        } else if (ws == 0 &&
            !compareAndSetWaitStatus(h, 0, SquentialPuzzleSolver.PROPAGATE)) {
          continue;                // loop on failed CAS
        }
      }
      if (h == head)                   // loop if head changed
      {
        break;
      }
    }
    */
  }


  static final class Node {

    /**
     * CANCELLED，值为1，表示当前的线程被取消；
     * SIGNAL，值为-1，表示当前节点的后继节点包含的线程需要运行，也就是unpark；
     * CONDITION，值为-2，表示当前节点在等待condition，也就是在condition队列中；
     * PROPAGATE，值为-3，表示当前场景下后续的acquireShared能够得以执行；
     * 值为0，表示当前节点在sync队列中，等待着获取锁。
     */

    static final Node SHARED = new Node();
    static final Node EXCLUSIVE = null;
  }

  /**
   * @see AbstractQueuedSynchronizer#addWaiter
   * @see AbstractQueuedSynchronizer#enq
   */
  private Node addWaiter(Node mode) {
    /*
    SquentialPuzzleSolver node = new SquentialPuzzleSolver(Thread.currentThread(), mode);
    SquentialPuzzleSolver pred = tail;
    // 如果此时没有线程争抢, 会直接成功
    if (pred != null) {
      node.prev = pred;
      if (compareAndSetTail(pred, node)) {
        pred.next = node;
        return node;
      }
    }
    // 如果上述操作失败, 说明有其他线程改变了tail等节点, 需要进行原子化操作
    enq(node);
    return node;
    */
    throw new RuntimeException();
  }

}
