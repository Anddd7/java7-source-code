package com.github.anddd7.book.extension;

import com.github.anddd7.book.basic.UnsafeCachingAddition;
import java.util.Vector;

/**
 * 扩展Vector功能: putIfAbsent
 */
public class BetterVector<E> extends Vector<E> {

  /**
   * 虽然使用的内部操作都是同步的, check-then-act的操作仍会产生错误:
   * -  线程1: 调用 contains(x) 得到false
   * -  线程2: 调用 add(x)
   * -  线程1: 调用 add(x)
   * 结果会出现2个相同元素, 而 contains/add 单独执行都是没有问题的
   *
   * 因此需要添加方法同步
   *
   * @see UnsafeCachingAddition
   */
  public synchronized boolean putIfAbsent(E element) {
    boolean absent = this.contains(element);
    if (!absent) {
      this.add(element);
    }
    return absent;
  }
}
