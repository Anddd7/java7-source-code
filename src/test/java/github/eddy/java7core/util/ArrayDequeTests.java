package github.eddy.java7core.util;

import static org.junit.Assert.assertEquals;

import java.util.ArrayDeque;
import java.util.ConcurrentModificationException;
import org.junit.Test;


/**
 * @author Anddd7
 * @see ArrayDeque
 *
 * 基于数组实现的无限容量(自动扩容)队列
 */
public class ArrayDequeTests<E> {

  /**
   * 队列允许的最小容量
   */
  private static final int MIN_INITIAL_CAPACITY = 8;
  private transient E[] elements;
  /**
   * 队首指针, elements[head]是队首元素
   */
  private transient int head;
  /**
   * 队尾指针, elements[tail-1]是队尾元素
   */
  private transient int tail;

  /**
   * {@link ArrayDeque#ArrayDeque()}
   * {@link ArrayDeque#ArrayDeque(int)}
   * - 初始/最小容量为16
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
   * - (容量-1) 会得到 0...111...111 的二进制数字
   * - tail-head>0 : 得到正常的元素数
   * - tail-head<0 : tail 指针已经跨界(循环数组), 经过与操作(取补)得到实际元素数
   * | - 计算机是先将数字转为二进制补码, 正数不变, 负数符号位取反+1
   * | - (1-4)=-3 ; -3 & (8-1) = 1....011 & 0.....111 =补码= 1111...101 & 0...111 = 0...101 = 5
   */
  public int size() {
    return (tail - head) & (elements.length - 1);
  }

  @Test
  public void and() {
    assertEquals(3, 3 & 7);
    assertEquals(3, 3 & 15);
    assertEquals(5, -3 & 7);
    assertEquals(13, -3 & 15);
  }

  /**
   * {@link ArrayDeque#addFirst(Object)}
   * - 同 size(), 利用位操作简化计算
   * - head=0, new head = elements.length-1, 即 head 指针从右向左移动 : 从数组最后一位向前存放
   * - head>0, new head = head-1, 向右移动一格
   */
  public void addFirst(E e) {
    if (e == null) {
      throw new NullPointerException();
    }
    elements[head = (head - 1) & (elements.length - 1)] = e;
    if (head == tail) {
      doubleCapacity();
    }
  }

  /**
   * {@link ArrayDeque#addLast(Object)}
   * - 添加到队尾即 tail 指针向右移动(与 head 反向), 通过与运算(等效取模 %)保证 tail 值的有效性
   */
  public void addLast(E e) {
    if (e == null) {
      throw new NullPointerException();
    }
    elements[tail] = e;
    if ((tail = (tail + 1) & (elements.length - 1)) == head) {
      doubleCapacity();
    }
  }

  /**
   * {@link ArrayDeque#pollFirst()}
   * - 弹出队首元素
   */
  public E pollFirst() {
    int h = head;
    // 获取当前元素
    E result = elements[h];
    // 当前元素为 null : 队列为空
    if (result == null) {
      return null;
    }
    // 弹出元素
    elements[h] = null;
    // head 向右移动
    head = (h + 1) & (elements.length - 1);
    return result;
  }

  /**
   * {@link ArrayDeque#removeFirstOccurrence(Object)}
   */
  public boolean removeFirstOccurrence(Object o) {
    if (o == null) {
      return false;
    }
    int mask = elements.length - 1;
    int i = head;
    E x;
    // 因为 ArrayDeque 是自动扩容的队列, 因此一定留有 null 的空位, 循环就能停止
    while ((x = elements[i]) != null) {
      if (o.equals(x)) {
        delete(i);
        return true;
      }
      i = (i + 1) & mask;
    }
    return false;
  }

  /**
   * {@link ArrayDeque#checkInvariants()}
   */
  private void checkInvariants() {
    // 队尾指针所指元素为空
    assert elements[tail] == null;
    // 队列为空: 队首一定为空; 队列不为空, 队首队尾都不为空
    assert head == tail ? elements[head] == null :
        (elements[head] != null &&
            elements[(tail - 1) & (elements.length - 1)] != null);
    // 队首的下一个位置为空
    assert elements[(head - 1) & (elements.length - 1)] == null;
  }

  /**
   * {@link ArrayDeque#delete(int)}
   * - 删除指定位置的元素
   */
  private boolean delete(int i) {
    // 确定队列不为空
    checkInvariants();
    final E[] elements = this.elements;
    final int mask = elements.length - 1;
    final int h = head;
    final int t = tail;
    // 待删元素到队首的距离
    final int front = (i - h) & mask;
    // 待删元素到队尾的距离
    final int back = (t - i) & mask;

    // 待删元素到队首的距离 >= 总元素数 = 元素冲突错误
    if (front >= ((t - h) & mask)) {
      throw new ConcurrentModificationException();
    }

    if (front < back) {
      // 即将 head~i 的元素移动到 head+1~i, 覆盖掉 i 的元素
      if (h <= i) {
        // 即 front>0, 表示 待删元素到队首距离更短, 需要移动的元素更少
        // 将 head~i 的元素全部后移一位
        System.arraycopy(elements, h, elements, h + 1, front);
      } else {
        // 即 front<0, 队首已经过界
        // 将 0~i 的元素全部后移一位
        System.arraycopy(elements, 0, elements, 1, i);
        // 将 mask 元素移到位置0
        elements[0] = elements[mask];
        // 将 h~mask 元素全部后移一位
        System.arraycopy(elements, h, elements, h + 1, mask - h);
      }
      // 队首置空并后移一位
      elements[h] = null;
      head = (h + 1) & mask;
      return false;
    } else {
      // 同上,  i 到队尾元素更少, copy 资源耗费更少
      if (i < t) {
        System.arraycopy(elements, i + 1, elements, i, back);
        tail = t - 1;
      } else {
        System.arraycopy(elements, i + 1, elements, i, mask - i);
        elements[mask] = elements[0];
        System.arraycopy(elements, 1, elements, 0, t);
        tail = (t - 1) & mask;
      }
      return true;
    }
  }
}
