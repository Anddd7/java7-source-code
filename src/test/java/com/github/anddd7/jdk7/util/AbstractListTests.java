package com.github.anddd7.jdk7.util;

import java.util.AbstractCollection;
import java.util.AbstractList;
import java.util.Collection;
import java.util.List;

/**
 * @author Anddd7
 * @see AbstractList
 * @see AbstractCollection
 * @see List
 */
public class AbstractListTests {

  /**
   * {@link AbstractList#add(Object)}
   * {@link AbstractList#addAll(int, Collection)}
   * - 基于 add(index,e)
   *
   * {@link AbstractList#indexOf(Object)}
   * {@link AbstractList#lastIndexOf(Object)}
   * - 遍历 listIterator() ,返回(正序/倒序)第一个目标元素(使用 equals)位置
   *
   *
   * {@link AbstractList#iterator()}
   * {@link AbstractList#listIterator()}
   * {@link AbstractList#listIterator(int)}
   * - 创建一个普通/ list 迭代器
   *
   * {@link AbstractList.Itr}
   * {@link AbstractList.ListItr}
   * - 迭代器实现
   * - cursor 指向当前迭代元素的位置
   * - add/set/remove 依赖 list 的方法实现
   *
   * {@link AbstractList#equals(Object)}
   * - 基于元素的deepEquals
   *
   * {@link AbstractList#hashCode()}
   * - 基于元素的deepHash
   *
   * {@link AbstractList#clear()}
   * {@link AbstractList#removeRange(int, int)}
   * - 移除全部/范围元素
   *
   * {@link AbstractList#subList(int, int)}
   *
   * {@link java.util.SubList}
   * {@link java.util.RandomAccessSubList}
   * - 包装模式 : list,fromIndex,toIndex 构成的 sublist
   * - subList.iterator 也是对 list.iterator 的包装
   */
  public AbstractListTests() {
  }
}
