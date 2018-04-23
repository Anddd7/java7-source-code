package github.eddy.java7core.util;

import github.eddy.java7core.io.CloneableTests;
import github.eddy.java7core.io.SerializableTests;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.AbstractList;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.RandomAccess;
import org.junit.Test;

/**
 * @author Anddd7
 *
 * {@link ArrayList} extends {@link AbstractList}
 * implements {@link List,RandomAccess,Cloneable,Serializable}
 *
 * {@link ArrayListTests} extends {@link AbstractList}
 * implements {@link ListTests,RandomAccessTests,CloneableTests,SerializableTests}
 *
 * 数组实现的 List
 */
public class ArrayListTests {

  /**
   * 同 {@link AbstractCollectionTests#MAX_ARRAY_SIZE}
   */
  private static final int MAX_ARRAY_SIZE = Integer.MAX_VALUE - 8;
  /**
   * {@link ArrayList#elementData}
   * - 存放元素
   * - 没有用泛型 ,省略泛型强转的声明 ,只在必要时申明 -> get
   */
  private transient Object[] elementData;

  /**
   * {@link ArrayList#EMPTY_ELEMENTDATA
   * - 创建时 ,默认空数组
   *
   * {@link ArrayList#DEFAULT_CAPACITY}
   * - 非空数组默认大小10
   *
   * {@link ArrayList#ArrayList()}
   * {@link ArrayList#ArrayList(int)}
   * {@link ArrayList#ArrayList(Collection)}
   * - 创建 List 包含: 空数组/指定长度的数组/c.toArray()
   *
   * {@link ArrayList#trimToSize()}
   * - 剪裁数组 length=size
   *
   * {@link ArrayList#ensureCapacity(int)}
   * {@link ArrayList#ensureCapacityInternal(int)}
   * - private 指定扩容大小
   * |  - 当前列表为空: 至少扩到10
   * |  - 当前列表已有元素 : 直接扩容到指定大小
   *
   * {@link ArrayList#ensureExplicitCapacity(int)}
   * - 检查指定是否溢出
   *
   *
   * {@link ArrayList#size()}
   * {@link ArrayList#isEmpty()}
   * - 基于 列表长度({@link ArrayList#size}有效元素数)
   *
   * {@link ArrayList#contains(Object)}
   * {@link ArrayList#indexOf(Object)}
   * {@link ArrayList#lastIndexOf(Object)}
   * - 重载了 AbstractList 的方法 ,直接对元素数组 顺序/逆序 遍历
   *
   * {@link ArrayList#clone()}
   * - 深拷贝 : 拷贝 list 对象并替换元素数组对象
   *
   * {@link ArrayList#toArray()}
   * {@link ArrayList#toArray(Object[])}
   * - 重载 AbstractCollection 的方法 ,直接复制数组
   *
   * {@link ArrayList#elementData(int)}
   * {@link ArrayList#get(int)}
   * {@link ArrayList#set(int, Object)}
   * - 指定位置获取/设置元素
   *
   * {@link ArrayList#add(int, Object)}
   * - 指定位置插入元素
   *
   * {@link ArrayList#remove(int)}
   * {@link ArrayList#removeRange(int, int)}
   * - 指定位置 index 删除(多个)元素
   * |  1  2  3  [4]  5  6  : 删除 index = 3 的元素
   * |  1  2  3   5   6  6  : 复制 index 以后的元素向前一位
   * |  1  2  3   5   6  -  : 末尾元素置空 ,size--
   *
   * {@link ArrayList#remove(Object)}
   * - 遍历后删除指定对象
   *
   * {@link ArrayList#fastRemove(int)}
   * - 没有 rangeCheck 和返回值
   *
   * {@link ArrayList#clear()}
   * - 清除数组元素的引用 ,重置 size ( 但数组容量不变 )
   *
   * {@link ArrayList#addAll(Collection)}
   * {@link ArrayList#addAll(int, Collection)}
   * - arrayCopy c.toArray
   *
   * {@link ArrayList#rangeCheck(int)}
   * {@link ArrayList#rangeCheckForAdd(int)}
   * - 检查是否超界
   *
   * {@link ArrayList#removeAll(Collection)}
   * {@link ArrayList#retainAll(Collection)}
   * {@link ArrayList#batchRemove(Collection, boolean)}
   * TODO
   *
   * {@link ArrayList#writeObject(ObjectOutputStream)}
   * TODO
   *
   * {@link ArrayList#readObject(ObjectInputStream)}
   * TODO
   *
   * {@link ArrayList#iterator()}
   * {@link ArrayList#listIterator()}
   * {@link ArrayList#listIterator(int)}
   * - 重载 返回重写的迭代器
   *
   * {@link ArrayList.Itr}
   * TODO
   *
   * {@link ArrayList.ListItr}
   * TODO
   *
   * {@link ArrayList#subList(int, int)}
   * {@link ArrayList#subListRangeCheck(int, int, int)}
   * - 重载 返回重写的迭代器
   *
   * {@link ArrayList.SubList}
   * TODO
   */

  public ArrayListTests() {
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
   * {@link ArrayList#grow(int)}
   * - 扩容算法 : 增加数组长度 capacity, 满足指定容量 minCapacity
   */
  private void grow(int minCapacity) {
    // 当前容量
    int oldCapacity = elementData.length;
    // 扩容后容量
    int newCapacity = oldCapacity + (oldCapacity >> 1);
    // 扩容容量太小 ,使用指定扩容容量
    if (newCapacity - minCapacity < 0) {
      newCapacity = minCapacity;
    }
    // 扩容容量太大 ,超界
    if (newCapacity - MAX_ARRAY_SIZE > 0) {
      // 重置 扩容容量 = 满足 指定扩容容量 的最大容量
      newCapacity = hugeCapacity(minCapacity);
    }
    // 扩容
    elementData = Arrays.copyOf(elementData, newCapacity);
  }

  @Test
  public void arrayList_IsNotThreadSafety() {
    // TODO 非线程安全测试
  }
}
