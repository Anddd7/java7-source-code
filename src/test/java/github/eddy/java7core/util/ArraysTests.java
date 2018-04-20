package github.eddy.java7core.util;

import static org.junit.Assert.assertEquals;

import java.util.Arrays;
import java.util.Comparator;
import java.util.Objects;
import org.junit.Test;

/**
 * @author and777
 *
 * {@link Arrays}
 * 数组工具类
 */
public class ArraysTests {

  /**
   * {@link Arrays#asList(Object[])}}
   * - 数组转List
   *
   *
   * {@link Arrays#binarySearch(int[], int)}
   * - 二分查找
   *
   * {@link Arrays#fill(int[], int)}
   * {@link Arrays#fill(int[], int, int, int)}
   * - 用指定值填充数组(区段)
   *
   *
   * {@link Arrays#equals(Object[], Object[])}
   * {@link Arrays#hashCode(Object[])}
   * {@link Arrays#sort(Object[])}
   * - 遍历并比较数组元素
   *
   *
   * {@link Arrays#deepEquals(Object[], Object[])} 同 {@link Objects#deepEquals(Object, Object)}
   * {@link Arrays#deepHashCode(Object[])} 同 {@link Objects#hash(Object...)}
   * {@link Arrays#deepToString(Object[])}
   * - 对数组元素(Object) 递归式 的执行 equals/hashCode/toString 方法
   *
   *
   * {@link Arrays#sort(int[])}
   * - 基于快速排序 {@link java.util.DualPivotQuicksort}
   *
   * {@link Arrays#sort(Object[], Comparator)}
   */
  public ArraysTests() {
  }

  /**
   * {@link Arrays#copyOf(Object[], int)}
   * {@link Arrays#copyOfRange(Object[], int, int)}
   * 快速复制数组 ,这两个函数封装了: new dest数组; 复制元素到dest数组; 返回dest数组
   * - length不能超过原数组长度
   * - 对于非基本类型 :
   * |  Object-创建Object数组;
   * |  其他类型-通过 {@link java.lang.reflect.Array#newInstance(Class, int)}创建指定长度的对象数组
   */
  @Test
  public void copyOf_ShouldReturnCopiedArray() {
    int[] source = {1, 2, 3, 4, 5, 6, 7, 8, 9, 0};

    int[] dest1 = Arrays.copyOf(source, 5);
    assertEquals(5, dest1.length);
    assertEquals(1, dest1[0]);
    assertEquals(5, dest1[dest1.length - 1]);

    int[] dest2 = Arrays.copyOfRange(source, 5, 10);
    assertEquals(5, dest2.length);
    assertEquals(6, dest2[0]);
    assertEquals(0, dest2[dest2.length - 1]);
  }

}
