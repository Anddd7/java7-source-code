package com.github.anddd7.jdk7.util.concurrent;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.locks.AbstractQueuedSynchronizer;

/**
 * @see CountDownLatch
 * - 基于{@link AbstractQueuedSynchronizer}实现的同步器
 * - 基于这个同步器对定量的资源进行获取和释放
 */
public class CountDownLatchTests {


  /**
   * [基于共享锁方式实现的闭锁]
   *
   * 假设count = 3
   *
   * 当线程await时, 同步器开始通过tryAcquireShared检查状态
   * 当state不满足条件时, 所有执行await的线程都会在队列上等待: head <- 1 <- 2 <- 3
   *
   * 此时另外的线程通过countDown进行状态修改减1, 释放head节点上的锁(默认的)并唤醒后续的[节点1]
   * 但因为tryAcquireShared中state==0, 所以会继续等待
   *
   *
   * 直到state=0, [节点1]被唤醒且抢占了head位置获取了锁
   * 并且因为共享锁的关系, [节点1]抢占成功后会唤醒[节点2], 接着[节点2]会进行抢占 ...
   * 最后所有节点都成功获取了锁, 并执行完毕
   */
  private Sync sync;

  public void await() throws InterruptedException {
    sync.acquireSharedInterruptibly(1);
  }

  public void countDown() {
    sync.releaseShared(1);
  }

  private static final class Sync extends AbstractQueuedSynchronizer {

    private static final long serialVersionUID = 4982264981922014374L;

    Sync(int count) {
      setState(count);
    }

    int getCount() {
      return getState();
    }

    /**
     * 当state状态不为0时挂起当前线程
     */
    protected int tryAcquireShared(int acquires) {
      return (getState() == 0) ? 1 : -1;
    }

    /**
     * 原子的进行资源修改
     */
    protected boolean tryReleaseShared(int releases) {
      for (; ; ) {
        int c = getState();
        if (c == 0) {
          return false;
        }
        int nextc = c - 1;
        if (compareAndSetState(c, nextc)) {
          return nextc == 0;
        }
      }
    }
  }
}
