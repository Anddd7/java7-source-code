package github.eddy.java7core.util;

import github.eddy.java7core.io.CloneableTests;
import github.eddy.java7core.io.SerializableTests;
import java.io.Serializable;
import java.util.AbstractCollection;
import java.util.ArrayDeque;
import java.util.Collection;
import java.util.Deque;


/**
 * @author Anddd7
 *
 * {@link ArrayDeque} extends {@link AbstractCollection}
 * implements {@link Deque,Cloneable,Serializable}
 * {@link ArrayDequeTests} extends {@link AbstractCollectionTests}
 * implements {@link DequeTests,CloneableTests,SerializableTests}
 *
 * 基于数组实现的无限容量(自动扩容)队列
 */
public class ArrayDequeTests<E> {

  /**
   * 队列最小容量
   */
  private static final int MIN_INITIAL_CAPACITY = 8;
  /**
   * 元素数组
   */
  private transient E[] elements;
  /**
   * 队列头
   */
  private transient int head;
  /**
   * 队列尾
   */
  private transient int tail;

  /**
   * {@link ArrayDeque#ArrayDeque()}
   * {@link ArrayDeque#ArrayDeque(int)}
   * {@link ArrayDeque#ArrayDeque(Collection)}
   * - 初始/最小容量为16
   *
   * TODO -
   */
  public ArrayDequeTests() {
  }

  /**
   * {@link ArrayDeque#allocateElements(int)}
   * - 申请足够大小的空数组
   * - 新数组长度是满足指定容量时 ,最小的 2^k
   *
   * 假设需要的容量 n 转化为二进制后最高位为 k ,那么满足条件的长度值就是 2^(n+1)
   * 例如 : 9 = 1001 ,长度应为 16 = 10000 ,则最大序列是1111
   * 因此我们只要使二进制下 ,k 到 0 位都是1 ,即是最大值
   */
  private void allocateElements(int numElements) {
    int initialCapacity = MIN_INITIAL_CAPACITY;
    // 指定容量>8
    if (numElements >= initialCapacity) {
      // 假设二进制化后数字为: ... 1 ...
      initialCapacity = numElements;
      // 无符号右移: ... 11 ...
      initialCapacity |= (initialCapacity >>> 1);
      // ... 11 11 ...
      initialCapacity |= (initialCapacity >>> 2);
      // ... 1111 1111 ...
      initialCapacity |= (initialCapacity >>> 4);
      // ... 11111111 11111111 ...
      initialCapacity |= (initialCapacity >>> 8);
      // ... 11111111 11111111 11111111 11111111 ...
      initialCapacity |= (initialCapacity >>> 16);
      // 因为int共 32位 = 2^5 ,经过5次 右移+或 操作已经能覆盖所有位
      // 能保证最高位 k 往后所有位都是1
      // 此时再+1 ,容量即为满足条件的最小的 2 的倍数
      initialCapacity++;

      // 溢出 : initialCapacity = 2^31
      if (initialCapacity < 0) {
        // initialCapacity = 2^30
        initialCapacity >>>= 1;
      }
    }
    elements = (E[]) new Object[initialCapacity];
  }

  /**
   * {@link ArrayDeque#doubleCapacity()}
   * - 扩容算法 ,扩容后将原数组拼接到新数组
   * - ArrayDeque 是使用 head/tail 指针实现的循环队列(默认向右) ,因此 head->n + 0->tail 是链接起来的
   * - 在扩容过后可以重整顺序 head -> r(n-head) -> tail(n)
   */
  private void doubleCapacity() {
    //{ 1 2 3 4 [5] 6 7 8 9
    //{          |
    //{      head/tail

    // 队列头尾重合: 当前队列已满
    assert head == tail;

    // 从 0 ~ tail 的元素数 : 1 2 3 4
    int p = head;
    int n = elements.length;
    // 从 head ~ n 的元素数 : 5 6 7 8 9
    int r = n - p;
    // 扩容一倍
    int newCapacity = n << 1;
    if (newCapacity < 0) {
      throw new IllegalStateException("Sorry, deque too big");
    }
    Object[] a = new Object[newCapacity];

    // 将 head ~ n 放在新数组前段
    // 5 6 7 8 9 ---------------
    System.arraycopy(elements, p, a, 0, r);
    // 将 0 ~ tail 放在新数组后段
    // 5 6 7 8 9 1 2 3 4 -------
    System.arraycopy(elements, 0, a, r, p);

    elements = (E[]) a;

    // 重置队列指针
    head = 0;
    tail = n;
  }

  /**
   * {@link ArrayDeque#copyElements(Object[])}
   * - 将元素导出到指定数组中
   * - 需要处理循环队列 跨边界 问题
   */
  private <T> T[] copyElements(T[] a) {
    if (head < tail) {
      // head 在 tail 左边: 没有跨边界 ,直接复制
      System.arraycopy(elements, head, a, 0, size());
    } else if (head > tail) {
      // head 在 tail 右边: 跨边界 ,需要重整链接两部分
      int headPortionLen = elements.length - head;
      System.arraycopy(elements, head, a, 0, headPortionLen);
      System.arraycopy(elements, 0, a, headPortionLen, tail);
    }
    return a;
  }

  /**
   * {@link ArrayDeque#size()}
   * TODO -
   */
  public int size() {
    return (tail - head) & (elements.length - 1);
  }
}
