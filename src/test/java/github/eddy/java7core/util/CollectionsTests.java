package github.eddy.java7core.util;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Deque;
import java.util.Enumeration;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.SortedMap;
import java.util.SortedSet;
import org.junit.Assert;
import org.junit.Test;

/**
 * @author and777
 *
 * {@link Collections}
 * 集合工具类
 */
public class CollectionsTests {

  /**
   * {@link Collections#addAll(Collection, Object[])}
   * - 循环添加元素
   *
   *
   * {@link Collections#asLifoQueue(Deque)}
   * - 用Deque构造一个后进先出(stack)的队列 : 委派类 ,offer -> offerFirst , poll -> pollFirst
   *
   *
   * {@link Collections#binarySearch(List, Object)}
   * {@link Collections#binarySearch(List, Object, Comparator)} (List, Object)}
   * - 二分查找
   * - <p>BINARYSEARCH_THRESHOLD = 5000</p> sequential access list 优化
   * |  - RandomAccess 或 size < threshold 基于 index 遍历进行查找
   * |  - 其他的使用iterator : {@link Collections#iteratorBinarySearch(List, Object)}
   * |    - 通过遍历先移动iterator.index到中间位置 ,再get(index)取元素
   * |    - 下一次二分时 ,如果位置小于当前index ,则前移previous ,否则后移next
   *
   *
   * {@link Collections#checkedCollection(Collection, Class)}
   * {@link Collections#checkedList(List, Class)}
   * {@link Collections#checkedMap(Map, Class, Class)}
   * {@link Collections#checkedSet(Set, Class)}
   * {@link Collections#checkedSortedMap(SortedMap, Class, Class)}
   * {@link Collections#checkedSortedSet(SortedSet, Class)}
   * - 委派者模式 {@link Collections.CheckedCollection},指定元素类型class ,在添加元素时进行检查
   *
   *
   * {@link Collections#copy(List, List)}
   * - 复制src的元素到dest数组中 : 按顺序替代已有元素 , 必须src.size<dest.size
   * - src.size < 10 或者两者都是 RandomAccess 可以 fori 遍历 ; 其他需使用 listIterator
   *
   *
   * {@link Collections#disjoint(Collection, Collection)}
   * - 判断2个集合有没有相同的元素 (是否有交集)
   * |  - set.contains 效率高于其他集合的contains
   * |  - 2个非set集合 ,元素少的作为迭代器-元素多的作为contains ,比较次数更少(contains平均时间为ceiling(n/2))
   *
   * {@link Collections#emptyEnumeration()}
   * {@link Collections#emptyList()}
   * {@link Collections#emptyIterator()}
   * {@link Collections#emptyListIterator()}
   * {@link Collections#emptyMap()}
   * {@link Collections#emptySet()}
   * - 返回高效的空 集合/枚举/MapTests
   * |  - static final : 全局共享 ,多次使用也不会耗费额外资源
   * |  - 重载方法 ,例如 contains = false : 节省不必要的计算
   *
   * {@link Collections#enumeration(Collection)}
   * {@link Collections#list(Enumeration)}
   * - 集合/枚举 互转
   *
   * {@link Collections#fill(List, Object)}
   * - 指定元素填充List
   *
   * {@link Collections#frequency(Collection, Object)}
   * - 计算元素出现的次数
   *
   * {@link Collections#indexOfSubList(List, List)}
   * {@link Collections#lastIndexOfSubList(List, List)}
   * - 查询subList在list中的位置
   *
   * {@link Collections#max(Collection)}
   * {@link Collections#min(Collection)}
   * - 找最大值
   *
   * {@link Collections#nCopies(int, Object)}
   * - 创建 包含n个指定元素的 不可变的 list (没有实现add方法)
   *
   * {@link Collections#newSetFromMap(Map)}
   * - 基于Map创建Set (keySet)
   *
   * {@link Collections#replaceAll(List, Object, Object)}
   * - 替换指定元素
   *
   * {@link Collections#reverse(List)}
   * - 倒序排列当前list
   *
   * {@link Collections#reverseOrder()}
   * {@link Collections#reverseOrder(Comparator)}
   * - 获取 自然序列/指定comparator 的 倒序comparator
   *
   * {@link Collections#shuffle(List)}
   * {@link Collections#shuffle(List, Random)}
   * - 打乱顺序
   *
   * {@link Collections#singleton(Object)}
   * {@link Collections#singletonList(Object)}
   * {@link Collections#singletonIterator(Object)}
   * {@link Collections#singletonMap(Object, Object)}
   * - 只包含一个元素的集合
   *
   * {@link Collections#sort(List)}
   * {@link Collections#sort(List, Comparator)}
   * - 基于Arrays.sort对元素排序
   *
   * {@link Collections#swap(List, int, int)}
   * - 交换指定位置的元素
   *
   * {@link Collections#synchronizedCollection(Collection)}
   * {@link Collections#synchronizedList(List)}
   * {@link Collections#synchronizedMap(Map)}
   * {@link Collections#synchronizedSet(Set)}
   * {@link Collections#synchronizedSortedSet(SortedSet)}
   * {@link Collections#synchronizedSortedMap(SortedMap)}
   * - 同步集合/map
   * |  - 委派模式 ,使用Synchronized对象包裹实际集合对象
   * |  - 内置一个mutex对象 ,需要同步的方法都包裹在 synchronized(mutex) 中
   *
   * {@link Collections#unmodifiableCollection(Collection)}
   * {@link Collections#unmodifiableList(List)}
   * {@link Collections#unmodifiableMap(Map)}
   * {@link Collections#unmodifiableSet(Set)}
   * {@link Collections#unmodifiableSortedSet(SortedSet)}
   * {@link Collections#unmodifiableSortedMap(SortedMap)}
   * - 不可变集合/map
   * |  - 委派模式 ,重载了并失效了add/remove
   */
  public CollectionsTests() {
  }

  /**
   * {@link Collections#rotate(List, int)}
   * - 指定list向右移位 (向左移动3 = 向右移动size-3)
   */
  @Test
  public void rotate_MoveElementsTowardsRight() {
    List<Integer> integers = Arrays.asList(1, 2, 3, 4, 5);
    Collections.rotate(integers, 3);
    Assert.assertEquals(3, integers.get(0).intValue());
    Collections.rotate(integers, -3);
    Assert.assertEquals(1, integers.get(0).intValue());
    Collections.rotate(integers, integers.size() - 3);
    Assert.assertEquals(4, integers.get(0).intValue());
  }
}
