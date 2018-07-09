package com.github.anddd7.jdk7.util;

import java.util.AbstractCollection;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;

/**
 * @author Anddd7
 * @see AbstractCollection
 * @see Collection
 */
public class AbstractCollectionTests {

  /**
   * 数组的最大长度在不同的 JVM 可能不同 ,通常是小于 Integer.MAX_VALUE 的
   * MAX_ARRAY_SIZE 是一个在大多数 JVM 上比较保险的值
   */
  static final int MAX_ARRAY_SIZE = Integer.MAX_VALUE - 8;


  /**
   * {@link AbstractCollection#isEmpty()}
   * - 检查 size()
   *
   * {@link AbstractCollection#remove(Object)}
   * {@link AbstractCollection#contains(Object)}
   * - 遍历 iterator() 迭代器并进行比较(后删除)
   *
   * {@link AbstractCollection#addAll(Collection)}
   * - for 循环 add()
   *
   * {@link AbstractCollection#containsAll(Collection)}
   * - 遍历入参集合元素 ,调用contains(e)
   *
   * {@link AbstractCollection#removeAll(Collection)}
   * {@link AbstractCollection#retainAll(Collection)}
   * - 遍历 iterator() 迭代器 ,调用 c.contains(it.next()) 进行比较 ,并 it.remove()
   * |  为什么不能使用 c.iterator() 遍历 ,循环调用 remove() ?
   * |  - 因为 remove 方法在设计时已经是遍历+remove ,这样会耗费更多资源
   * |  - 这里需要的是 contain 找到元素然后直接 remove ,而实际的 remove 功能是由迭代器提供的
   *
   * {@link AbstractCollection#clear()}
   * - 遍历 iterator() 迭代器并删除
   *
   * {@link AbstractCollection#toArray()}
   * - 遍历并复制 iterator() 迭代器中的元素
   * - 并发问题 : 如果复制过程中元素 size() 变化
   * |  size() > 迭代器元素数 , 创建的数组太大 : 以迭代器当前元素数为准 ,it.hasNext
   * |  size() < 迭代器元素数 , 创建的数组太小 : 继续添加剩余元素 {@link this#finishToArray}
   *
   * {@link AbstractCollection#toArray(Object[])}
   * - 遍历并复制 iterator() 迭代器中的元素到指定数组 a 中
   * - 如果指定数组小于 size() 会新建数组 r
   * - 并发问题 : 如果复制过程中元素 size() 变化
   * |  size() > 迭代器元素数 : 以迭代器当前元素数为准 ,it.hasNext
   * |  - if (a == r)
   * |    - 指定数组 a 足够大且还未填完 : 末尾填 null
   * |  - else if (a.length < i)
   * |    - 指定数组 a 太小 , 返回新创建的数组 r
   * |  - else
   * |    - 指定数组 a 一开始被认为太小( < size) ,但后面又满足迭代器使用
   * |    - 把新建数组 r  所有元素复制回 a
   * |    - 如果 a 还没填满 ,末尾填 null
   * |  size() < 迭代器元素数 : 继续添加剩余元素 {@link this#finishToArray}
   *
   * {@link AbstractCollection#toString()}
   * - 重写后可以展示内部元素
   */
  public AbstractCollectionTests() {
  }

  /**
   * {@link AbstractCollection#finishToArray}
   * 继续添加剩余元素时 ,因为 iterator 无法得知元素数量 ,因此需要动态扩容
   */
  @SuppressWarnings("unchecked")
  private static <T> T[] finishToArray(T[] r, Iterator<?> it) {
    // 记录当前数组的大小 , i 也是下一个元素的位置
    int i = r.length;
    // 循环添加
    while (it.hasNext()) {
      int cap = r.length;
      // 是否需要扩容 : 新添加元素的位置==数组长度
      if (i == cap) {
        // 1.5*cap+1
        int newCap = cap + (cap >> 1) + 1;
        // 检查新长度是否已经溢出(超界)
        // 为什么不是 newCap > MAX_ARRAY_SIZE ?
        // - 因为 int newCap 会溢出 ,溢出时我们认为是 1.5cap+1 ,而计算机会把它当成一个负数 ,if 会失败
        // - 因此使用减法 ,就能得出 newCap 实际超界多少值
        if (newCap - MAX_ARRAY_SIZE > 0) {
          // 新长度超界 ,则表示当前 cap 已经足够大了 ,直接使用当前最大能接受的数组长度
          newCap = hugeCapacity(cap + 1);
        }
        // 复制-替换 当前结果数组
        r = Arrays.copyOf(r, newCap);
      }
      // 继续在 i 位置添加元素
      r[i++] = (T) it.next();
    }
    // 剪裁掉没有使用的部分 : i 最后一个元素位置
    return (i == r.length) ? r : Arrays.copyOf(r, i);
  }

  /**
   * {@link AbstractCollection#finishToArray}
   * 获取数组最大长度
   */
  static int hugeCapacity(int minCapacity) {
    // minCapacity = cap + 1
    if (minCapacity < 0) {
      // 已经是最大值 ,无法再扩
      throw new OutOfMemoryError("Required array size too large");
    }
    // 先扩大到 MAX_ARRAY_SIZE (稳定安全) ; 如果还要就扩大到 Integer.MAX_VALUE (可能出错)
    return (minCapacity > MAX_ARRAY_SIZE) ? Integer.MAX_VALUE : MAX_ARRAY_SIZE;
  }
}
