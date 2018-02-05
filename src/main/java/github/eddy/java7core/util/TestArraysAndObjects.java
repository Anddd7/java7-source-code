package github.eddy.java7core.util;

import java.util.Arrays;
import java.util.Comparator;
import java.util.Objects;

/**
 * @author and777
 * @date 2018/2/5
 *
 * {@link Arrays} and {@link Objects}
 */
public class TestArraysAndObjects {

  /**
   {@link Arrays#asList(Object[])}}
   - 数组转List
   */

  /**
   {@link Arrays#binarySearch(int[], int)}
   - 二分查找
   */

  /**
   {@link Arrays#copyOf(int[], int)}
   {@link Arrays#copyOfRange(int[], int, int)}
   - 数组(区段)复制

   {@link Arrays#fill(int[], int)}
   {@link Arrays#fill(int[], int, int, int)}
   - 用指定值填充数组(区段)
   */

  /**
   {@link Arrays#equals(Object[], Object[])}
   {@link Arrays#hashCode(Object[])}
   {@link Arrays#sort(Object[])}
   - 遍历并比较数组元素

   {@link Arrays#deepEquals(Object[], Object[])} 同 {@link Objects#deepEquals(Object, Object)}
   {@link Arrays#deepHashCode(Object[])} 同 {@link Objects#hash(Object...)}
   {@link Arrays#deepToString(Object[])}
   - 对数组元素(Object) 递归式 的执行 equals/hashCode/toString 方法
   */

  /**
   {@link Arrays#sort(int[])}
   - 基于快速排序 {@link java.util.DualPivotQuicksort}

   {@link Arrays#sort(Object[], Comparator)}
   */

  /**
   {@link Objects#equals(Object, Object)}
   {@link Objects#hashCode(Object)}
   {@link Objects#toString(Object)}
   - 用来避免Object为空时 ,object.equals等的空指针异常


   {@link Objects#compare(Object, Object, Comparator)}
   - 对象比较

   {@link Objects#requireNonNull(Object, String)}
   - 空则抛出NullPointerException
   */


}
