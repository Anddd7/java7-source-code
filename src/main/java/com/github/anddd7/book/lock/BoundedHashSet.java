package com.github.anddd7.book.lock;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.Semaphore;

/**
 * 使用信号量Semaphore控制访问某一资源的操作数量
 * -  每加入一个元素就获取一个信号量, 失败时归还
 * -  删除时归还一个信号量
 * -  则容器中最大只能同时存在n个元素(有界), 超过时线程会阻塞等待
 */
public class BoundedHashSet<T> {

  private final Set<T> set;
  private final Semaphore sem;

  public BoundedHashSet(int bound) {
    this.set = Collections.synchronizedSet(new HashSet<T>());
    this.sem = new Semaphore(bound);
  }

  public boolean add(T o) throws InterruptedException {
    sem.acquire();
    boolean wasAdded = false;
    try {
      wasAdded = set.add(o);
      return wasAdded;
    } finally {
      if (!wasAdded) {
        sem.release();
      }
    }
  }

  public boolean remove(T o) throws InterruptedException {
    boolean wasRemoved = set.remove(o);
    if (wasRemoved) {
      sem.release();
    }
    return wasRemoved;
  }
}
