package com.github.anddd7.jdk7.util;

import java.util.AbstractSequentialList;
import java.util.Collection;

/**
 * @author Anddd7
 * @see AbstractSequentialList
 *
 * 因为 SequentialList 只能顺序访问 ,因此get(index)/set(index)/remove(index)随机访问需要由迭代器获取
 */
public class AbstractSequentialListTests {

  /**
   * {@link AbstractSequentialList#get(int)}
   * {@link AbstractSequentialList#set(int, Object)}
   * {@link AbstractSequentialList#add(int, Object)}
   * {@link AbstractSequentialList#remove(int)}
   * {@link AbstractSequentialList#addAll(int, Collection)}
   * - 对 index 的访问全都依赖于 listIterator
   *
   * {@link AbstractSequentialList#iterator()}
   * {@link AbstractSequentialList#listIterator(int)}
   * - 需要重写 listIterator 用来支持线性存取的 list
   */
  public AbstractSequentialListTests() {
  }
}
