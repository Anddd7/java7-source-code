package github.eddy.java7core.util;

import java.util.Collection;
import java.util.List;

/**
 * @author Anddd7
 *
 * {@link List} extends {@link Collection}
 * {@link ListTests} extends {@link CollectionTests}
 * 元素可重复的,线性集合 ,可以根据元素序列 index 对集合进行操作
 */
public class ListTests {

  /**
   * {@link List#get(int)}
   * - 获取指定位置元素
   * |  - 原有集合没有获取元素的方法 ,只能通过迭代器访问元素
   *
   * {@link List#set(int, Object)}
   * - 替换指定位置元素
   *
   * {@link List#add(int, Object)}
   * {@link List#addAll(Collection)}
   * - 在指定位置添加元素
   *
   * {@link List#remove(int)}
   * - 删除指定位置元素
   *
   * {@link List#indexOf(Object)}
   * {@link List#lastIndexOf(Object)}
   * - 获取指定元素的位置
   *
   * {@link List#listIterator()}
   * {@link List#listIterator(int)}
   * - 从指定位置开始建立一个 ListIterator
   *
   * {@link List#subList(int, int)}
   * - 截取 list
   */
  public ListTests() {
  }
}
