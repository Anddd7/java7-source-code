package com.github.anddd7.jdk7.util;

import java.util.NavigableMap;

/**
 * @author Anddd7
 * @see NavigableMap
 *
 * Key 值按优先级进行排列的 map, 提供一些近似(约等于)的访问方式
 */
public class NavigableMapTests {

  /**
   * {@link NavigableMap#lowerKey(Object)}
   * {@link NavigableMap#lowerEntry(Object)}
   * 返回满足 target.key < key 条件的最大key值
   *
   * {@link NavigableMap#floorKey(Object)}
   * {@link NavigableMap#floorEntry(Object)}
   * - target.key <= key
   *
   * {@link NavigableMap#ceilingKey(Object)}
   * {@link NavigableMap#ceilingEntry(Object)}
   * - target.key >= key
   *
   * {@link NavigableMap#higherKey(Object)}
   * {@link NavigableMap#higherEntry(Object)}
   * - target.key > key
   *
   * {@link NavigableMap#firstKey()}
   * {@link NavigableMap#pollFirstEntry()}
   *
   *
   * {@link NavigableMap#descendingMap()}
   * - 获取反向 Map
   *
   * TODO
   */
  public NavigableMapTests() {
  }
}
