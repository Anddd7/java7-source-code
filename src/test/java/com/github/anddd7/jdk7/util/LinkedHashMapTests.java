package com.github.anddd7.jdk7.util;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * @author Anddd7
 * @see LinkedHashMap
 * @see HashMap
 * @see Map
 *
 * 在 HashMap 基础上增加了一个双向链表, 用来遍历所有元素
 */
public class LinkedHashMapTests<K, V> extends HashMapTests {

  /**
   * @see LinkedHashMap#accessOrder
   * - 开启访问顺序排序模式, 默认关闭
   * - 开启过后, 访问过的元素会移到队尾, 在多次调用 contains 不同元素时, 性能会有提升
   */
  private boolean accessOrder = false;
  /**
   * @see LinkedHashMap#header
   * - 串联所有元素的双向循环链表, header.after....after = header.before
   * - header 其实是队尾, 因为每次遍历都是从 header.after 开始
   */
  private transient Entry<K, V> header;

  /**
   * @see LinkedHashMap#createEntry(int, Object, Object, int)
   * - 重载, 创建节点后自动放到 header 之前 (插入到队尾)
   */
  public LinkedHashMapTests() {
  }

  /**
   * @see LinkedHashMap#transfer(HashMap.Entry[], boolean)
   * - 直接遍历 header 链表, 而不用 桶+链表 去遍历了
   * - 所有涉及到遍历的操作都变成链表遍历 contains/iterator
   */
  void transfer(HashMapTests.Entry[] newTable, boolean rehash) {
    int newCapacity = newTable.length;
    for (Entry<K, V> e = header.after; e != header; e = e.after) {
      if (rehash) {
        e.hash = (e.key == null) ? 0 : hash(e.key);
      }
      int index = indexFor(e.hash, newCapacity);
      e.next = newTable[index];
      newTable[index] = e;
    }
  }

  /**
   * @see LinkedHashMap.Entry
   */
  private static abstract class Entry<K, V> extends HashMapTests.Entry<K, V> {

    /**
     * 增加了 before/after, 表示 header 这一链表上的链接关系
     */
    Entry<K, V> before, after;

    private void remove() {
      // 删除当前节点关系
      before.after = after;
      after.before = before;
    }

    /**
     * 将当前节点插入到已有节点之前
     */
    private void addBefore(Entry<K, V> existingEntry) {
      after = existingEntry;
      before = existingEntry.before;
      before.after = this;
      after.before = this;
    }

    /**
     * 当前节点被访问, 即 Entry 中的值发生修改或被 get时
     */
    void recordAccess(HashMapTests<K, V> m) {
      LinkedHashMapTests<K, V> lm = (LinkedHashMapTests<K, V>) m;
      // 如果开启访问排序模式, 访问过的节点会插入到 header 链表的最前面
      if (lm.accessOrder) {
        lm.modCount++;
        remove();
        addBefore(lm.header);
      }
    }

    /**
     * 当前节点被删除
     */
    void recordRemoval(HashMap<K, V> m) {
      remove();
    }
  }
}
