package com.github.anddd7.jdk7.util.concurrent;

import java.util.ArrayDeque;
import java.util.LinkedList;
import java.util.PriorityQueue;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.PriorityBlockingQueue;
import java.util.concurrent.SynchronousQueue;

/**
 * 并发容器类: 使用细粒度的锁对操作进行同步(读写锁/互斥锁......)
 * -  {@link SynchronizedContainers}同步容器是使用Synchronized同步入口操作, 但是会使操作串行化, 降低了并发
 */
public class ConcurrentContainers {

  /**
   * 常用容器线程安全版
   */
  private CopyOnWriteArrayList list;
  private CopyOnWriteArraySet set;
  private ConcurrentHashMap map;

  /**
   * @see ArrayDeque
   * @see LinkedList
   * FIFO队列
   */
  private ArrayBlockingQueue arrayBlockingQueue;
  private LinkedBlockingQueue linkedBlockingQueue;
  /**
   * @see PriorityQueue
   * 优先级队列
   */
  private PriorityBlockingQueue priorityBlockingQueue;
  /**
   * @see LinkedList
   * 双向队列
   */
  private LinkedBlockingDeque linkedBlockingDeque;

  /**
   *
   */
  private SynchronousQueue synchronousQueue;
}
