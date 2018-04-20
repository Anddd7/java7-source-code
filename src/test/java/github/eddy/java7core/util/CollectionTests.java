package github.eddy.java7core.util;

import java.util.Collection;

/**
 * @author Anddd7
 *
 * {@link Collection}
 */
public class CollectionTests {

  /**
   * {@link Collection#iterator()}
   * - 继承 {@link Iterable} ,所有集合类都支持迭代器
   *
   * {@link Collection#size()}
   * - 数据量
   *
   * {@link Collection#isEmpty()}
   * - 是否为空
   *
   * {@link Collection#contains(Object)}
   * {@link Collection#containsAll(Collection)}
   * - 是否包含 元素/集合
   *
   * {@link Collection#toArray()}
   * {@link Collection#toArray(Object[])}
   * - 退化为数组
   *
   * {@link Collection#add(Object)}
   * {@link Collection#addAll(Collection)}
   * - 添加元素
   *
   * {@link Collection#remove(Object)}
   * {@link Collection#removeAll(Collection)}
   * - 移除元素
   *
   * {@link Collection#retainAll(Collection)}
   * - 取交集:只保留入参集合中有的元素 ,其他删除
   *
   * {@link Collection#clear()}
   * - 删除所有元素
   *
   * {@link Collection#equals(Object)}
   * {@link Collection#hashCode()}
   * - 集合的状态依赖于内部元素 ,因此必须重写 equals 和 hashCode 方法
   */
  public CollectionTests() {
  }
}
