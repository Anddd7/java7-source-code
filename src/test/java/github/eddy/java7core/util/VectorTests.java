package github.eddy.java7core.util;

import static github.eddy.java7core.util.AbstractCollectionTests.MAX_ARRAY_SIZE;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Vector;

/**
 * @author Anddd7
 * @see Vector
 *
 * 使用 synchronized 关键字同步关键(都是 public 操作)方法 ,实现线程安全
 * @deprecated synchronized 粒度太大 ,而且遗留方法多而杂 ,建议使用 {@link Collections#synchronizedList(List)}
 */
@Deprecated
public class VectorTests {

  protected Object[] elementData;

  /**
   * {@link Vector#capacityIncrement}
   * - 扩容时递增量
   */
  protected int capacityIncrement;

  /**
   * {@link Vector#Vector()}
   * - 默认容量10
   *
   * {@link Vector#Vector(int)}
   * {@link Vector#Vector(int, int)}
   * - 指定 初始容量(/扩容递增量)
   *
   * {@link Vector#Vector(Collection)}
   * - 基于 c.toArray 创建 Vector
   *
   * synchronized {@link Vector#copyInto(Object[])}
   * - 复制 元素到指定 Array
   *
   * synchronized {@link Vector#setSize(int)}
   * - 扩展 Vector 到指定size(不是容量) : 变大,补 null; 变小,删除多余元素
   *
   * synchronized {@link Vector#capacity()}
   * - 获取 容量数
   *
   * {@link Vector#elements()}
   * - 返回枚举对象 ,内部方法使用 synchronized 同步
   *
   * synchronized {@link Vector#elementAt(int)}
   * synchronized {@link Vector#firstElement()}
   * synchronized {@link Vector#lastElement()}
   * - 获取指定位置元素
   *
   * synchronized {@link Vector#addElement(Object)}
   * synchronized {@link Vector#insertElementAt(Object, int)}
   * synchronized {@link Vector#removeElement(Object)}
   * synchronized {@link Vector#removeElementAt(int)}
   * synchronized {@link Vector#setElementAt(Object, int)}
   * - 增删改指定位置元素
   *
   * {@link Vector#clear()}
   * synchronized {@link Vector#removeAllElements()}
   *
   * -------------- 剩下方法同 ArrayList -------------------
   * 因为 Vector 的结构设计早于 Java 集合 ,因此遗留了很多方法
   * 结合了 原有方法 和 ArrayList ,通过同步关键字实现 list 接口
   * ------------------------------------------------------
   */
  public VectorTests() {
  }

  /**
   * 同 {@link AbstractCollectionTests#hugeCapacity(int)}
   */
  private static int hugeCapacity(int minCapacity) {
    if (minCapacity < 0) {
      throw new OutOfMemoryError();
    }
    return (minCapacity > MAX_ARRAY_SIZE) ? Integer.MAX_VALUE : MAX_ARRAY_SIZE;
  }

  /**
   * {@link Vector#grow(int)}
   * - 扩容算法
   */
  private void grow(int minCapacity) {
    int oldCapacity = elementData.length;
    // 如果指定了递增量 ,就递增一次 ; 否则扩大一倍
    int newCapacity = oldCapacity +
        ((capacityIncrement > 0) ? capacityIncrement : oldCapacity);
    // 扩容量不足 : 使用最小满足值
    if (newCapacity - minCapacity < 0) {
      newCapacity = minCapacity;
    }
    // 超界
    if (newCapacity - MAX_ARRAY_SIZE > 0) {
      newCapacity = hugeCapacity(minCapacity);
    }
    elementData = Arrays.copyOf(elementData, newCapacity);
  }
}
