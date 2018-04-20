package github.eddy.java7core.util;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.RandomAccess;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

/**
 * @author and777
 *
 * {@link RandomAccess}
 * 标识一个List是否是可随机访问的, 可随机访问的(ArrayList)使用for循环要比iterator迭代器更快
 */
@SuppressWarnings({"unused", "UnusedAssignment", "ForLoopReplaceableByForEach"})
public class RandomAccessTests {

  private List<Integer> arrayList = new ArrayList<>();
  private List<Integer> linkedList = new LinkedList<>();

  @Before
  public void before() {
    for (int i = 0; i < Integer.MAX_VALUE >>> 6; i++) {
      arrayList.add(i);
    }

    for (int i = 0; i < Integer.MAX_VALUE >>> 16; i++) {
      linkedList.add(i);
    }
  }

  @Test
  public void array_TimeCostWithForLoopShouldLessThanIterator() {
    int count = 0;
    long start = System.currentTimeMillis();

    for (int i = 0; i < arrayList.size(); i++) {
      count = arrayList.get(i);
    }
    long timeForLoop = System.currentTimeMillis();

    for (Integer integer : arrayList) {
      count = integer;
    }
    long timeIterator = System.currentTimeMillis();

    System.out.println(timeForLoop - start);
    System.out.println(timeIterator - timeForLoop);
    // iterator of ArrayList has bean optimized , result is not correct in some times (depends on your PC)
    //    Assert.assertTrue(timeForLoop - start < timeIterator - timeForLoop);
  }

  @Test
  public void linked_TimeCostWithForLoopShouldMoreThanIterator() {
    int count = 0;
    long start = System.currentTimeMillis();

    for (int i = 0; i < linkedList.size(); i++) {
      count = linkedList.get(i);
    }
    long timeForLoop = System.currentTimeMillis();

    for (Integer integer : linkedList) {
      count = integer;
    }
    long timeIterator = System.currentTimeMillis();

    System.out.println(timeForLoop - start);
    System.out.println(timeIterator - timeForLoop);

    Assert.assertTrue(timeForLoop - start > timeIterator - timeForLoop);
  }

}