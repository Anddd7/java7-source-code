package com.github.anddd7.jdk7.util.concurrent;

import java.util.Arrays;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.locks.ReentrantLock;

/**
 * @see CopyOnWriteArrayList
 * @see java.util.concurrent.CopyOnWriteArraySet
 *
 * "Copy On Write"写入时复制的线程安全: (延时懒惰策略)
 * -  发布一个事实不可变对象: 不可变的array
 * -  修改时创建并引用新的容器副本: new Array = copy(old array)
 * -  使用volatile保证容器副本的替代具有可见性
 *
 * 优点: 线程安全; 读并发; 简单实用
 * 缺点: 数据量大的时候, add/remove元素触发复制耗时
 */
public class CopyOnWriteArrayListTests<E> {

  transient final ReentrantLock lock = new ReentrantLock();
  /**
   * 用volatile保证get/contains方法的同步: array的set能即使反馈到变量上
   * getArray要么拿到的是write之前的, 要么是之后的: set操作是原子的
   */
  private volatile transient Object[] array;

  final Object[] getArray() {
    return array;
  }

  final void setArray(Object[] a) {
    array = a;
  }

  /**
   * @see CopyOnWriteArrayList#add(Object)
   * @see CopyOnWriteArrayList#remove(Object)
   * - 通过锁保证write操作的同步, 且在setArray之前, 数据的变化都在临时变量中
   */
  public boolean add(E e) {
    final ReentrantLock lock = this.lock;
    // 加锁
    lock.lock();
    try {
      // 复制array, 插入元素, 替换
      Object[] elements = getArray();
      int len = elements.length;
      Object[] newElements = Arrays.copyOf(elements, len + 1);
      newElements[len] = e;
      setArray(newElements);
      return true;
    } finally {
      lock.unlock();
    }
  }
}
