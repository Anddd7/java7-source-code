package github.eddy.java7core.util;

import java.util.Arrays;
import java.util.Comparator;
import java.util.PriorityQueue;

/**
 * @author Anddd7
 * @see PriorityQueue
 *
 * 基于数组的二叉小顶堆实现, 通过对树的调整, 保证元素能够按优先级进行取用
 * |    0
 * |  1   2
 * | 3 4 5 6
 */
public class PriorityQueueTests<E> {

  private static final int DEFAULT_INITIAL_CAPACITY = 11;
  private static final int MAX_ARRAY_SIZE = Integer.MAX_VALUE - 8;
  private final Comparator<? super E> comparator = null;
  private transient Object[] queue;
  private int size = 0;
  private transient int modCount = 0;

  /**
   * {@link PriorityQueue#PriorityQueue()}
   * - 默认容量11
   */
  public PriorityQueueTests() {
  }

  private static int hugeCapacity(int minCapacity) {
    if (minCapacity < 0) // overflow
    {
      throw new OutOfMemoryError();
    }
    return (minCapacity > MAX_ARRAY_SIZE) ?
        Integer.MAX_VALUE :
        MAX_ARRAY_SIZE;
  }

  /**
   * {@link PriorityQueue#grow(int)}
   */
  private void grow(int minCapacity) {
    int oldCapacity = queue.length;
    // Double size if small; else grow by 50%
    int newCapacity = oldCapacity + ((oldCapacity < 64) ?
        (oldCapacity + 2) :
        (oldCapacity >> 1));
    // overflow-conscious code
    if (newCapacity - MAX_ARRAY_SIZE > 0) {
      newCapacity = hugeCapacity(minCapacity);
    }
    queue = Arrays.copyOf(queue, newCapacity);
  }

  /**
   * {@link PriorityQueue#offer(Object)}
   */
  public boolean offer(E e) {
    if (e == null) {
      throw new NullPointerException();
    }
    modCount++;
    int i = size;
    if (i >= queue.length) {
      // 需要扩容
      grow(i + 1);
    }
    size = i + 1;
    if (i == 0) {
      // 第一个元素直接放在根节点
      queue[0] = e;
    } else {
      // 元素插入到树的末端, 并进行调整
      siftUp(i, e);
    }
    return true;
  }

  /**
   * {@link PriorityQueue#siftUp(int, Object)}
   * - 插入节点到 k 位置, 判断是否需要上升该节点
   */
  private void siftUp(int k, E x) {
    if (comparator != null) {
      siftUpUsingComparator(k, x);
    } else {
      siftUpComparable(k, x);
    }
  }

  /**
   * {@link PriorityQueue#siftUpComparable(int, Object)}
   */
  private void siftUpComparable(int k, E x) {
    Comparable<? super E> key = (Comparable<? super E>) x;
    // 直到根节点前
    while (k > 0) {
      // 标准二叉树, k-1 即父节点的 index
      int parent = (k - 1) >>> 1;
      Object e = queue[parent];
      // 父节点较小, 调整完成
      if (key.compareTo((E) e) >= 0) {
        break;
      }
      // 父节点较大, 元素上升到父节点位置, 继续检查
      queue[k] = e;
      k = parent;
    }
    //调整完成, k 位置放入 x 元素
    queue[k] = key;
  }

  /**
   * {@link PriorityQueue#siftUpComparable(int, Object)}
   * - 同上
   */
  private void siftUpUsingComparator(int k, E x) {
    while (k > 0) {
      int parent = (k - 1) >>> 1;
      Object e = queue[parent];
      if (comparator.compare(x, (E) e) >= 0) {
        break;
      }
      queue[k] = e;
      k = parent;
    }
    queue[k] = x;
  }


  /**
   * {@link PriorityQueue#removeAt(int)}
   * - 删除指定位置的元素
   * - 取队尾元素插入到指定位置
   */
  private E removeAt(int i) {
    assert i >= 0 && i < size;
    modCount++;
    int s = --size;
    if (s == i) {
      // 删除的元素是队尾元素
      queue[i] = null;
    } else {
      // 取队尾元素
      E moved = (E) queue[s];
      queue[s] = null;
      // 尝试将队尾元素插入到目标位置
      // 小顶堆不保证左右节点的大小, 因此需要先尝试下沉该元素
      siftDown(i, moved);
      if (queue[i] == moved) {
        // 下沉无效, 考虑上升
        siftUp(i, moved);
        if (queue[i] != moved) {
          return moved;
        }
      }
    }
    return null;
  }

  /**
   * {@link PriorityQueue#siftDown(int, Object)}
   */
  private void siftDown(int k, E x) {
    if (comparator != null) {
      siftDownUsingComparator(k, x);
    } else {
      siftDownComparable(k, x);
    }
  }

  /**
   * {@link PriorityQueue#siftDownComparable(int, Object)}
   */
  private void siftDownComparable(int k, E x) {
    Comparable<? super E> key = (Comparable<? super E>) x;
    int half = size >>> 1;
    while (k < half) {
      int child = (k << 1) + 1;
      // 左节点: 默认左边较小
      Object c = queue[child];
      int right = child + 1;
      // 先比较左右
      if (right < size &&
          ((Comparable<? super E>) c).compareTo((E) queue[right]) > 0) {
        // 右边较小, 替换待比较 child 到右边
        c = queue[child = right];
      }
      // 当前元素较小, 符合规则
      if (key.compareTo((E) c) <= 0) {
        break;
      }
      // 当前元素较大, 需要下沉, 和 child 互换位置
      queue[k] = c;
      k = child;
    }
    queue[k] = key;
  }

  /**
   * {@link PriorityQueue#siftDownUsingComparator(int, Object)}
   */
  private void siftDownUsingComparator(int k, E x) {
    int half = size >>> 1;
    while (k < half) {
      int child = (k << 1) + 1;
      Object c = queue[child];
      int right = child + 1;
      if (right < size &&
          comparator.compare((E) c, (E) queue[right]) > 0) {
        c = queue[child = right];
      }
      if (comparator.compare(x, (E) c) <= 0) {
        break;
      }
      queue[k] = c;
      k = child;
    }
    queue[k] = x;
  }
}
