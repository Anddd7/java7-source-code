package com.github.anddd7.book.extension;

import com.github.anddd7.book.annotation.NotThreadSafe;
import com.github.anddd7.book.annotation.ThreadSafe;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Collections.synchronizedList包裹的对象, 我们很难去扩展方法
 * -  需要同时扩展 装饰器和对象 以支持新操作
 * -  大量内部方法和参数未开放
 */
public class ListHelper<E> {

  public List<E> list = Collections.synchronizedList(new ArrayList<E>());

  /**
   * 虽然使用了synchronized, 但此方法拿到的锁是 synchronized(ListHelper), synchronizedList 的锁则是内部的lock
   * 除非对list的其他方法都用 synchronized(ListHelper) 装饰一遍, 否则 putIfAbsent与add/contains等方法并不是同步的
   */
  @NotThreadSafe
  public synchronized boolean putIfAbsentUnsafe(E element) {
    boolean absent = list.contains(element);
    if (!absent) {
      list.add(element);
    }
    return absent;
  }

  /**
   * 通过使用同样的内部锁(synchronizedList.mutex 默认为 this)
   *
   * @deprecated 但局限性很大, 如果内部锁未知/不开放 就很难用这种方式实现扩展; 且加锁操作被分离, 很难管理
   *
   * 更好的方式是扩展一个装饰器对象: 参考{@link Collections#synchronizedList(List)}, 实现一个新的SynchronizedList类
   */
  @ThreadSafe
  @Deprecated
  public boolean putIfAbsentSafe(E element) {
    synchronized (list) {
      boolean absent = list.contains(element);
      if (!absent) {
        list.add(element);
      }
      return absent;
    }
  }
}
