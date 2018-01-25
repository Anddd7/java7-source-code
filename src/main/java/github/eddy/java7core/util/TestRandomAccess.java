package github.eddy.java7core.util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.RandomAccess;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author and777
 * @date 2018/1/12
 *
 * {@link RandomAccess}
 * - 标识一个List是否是可随机访问的, 可随机访问的(ArrayList)使用for循环要比iterator迭代器更快
 */
public class TestRandomAccess {

  private static final Logger log = LoggerFactory.getLogger(TestRandomAccess.class);

  List<? extends List> lists = Arrays.asList(new ArrayList(), new LinkedList());
  int elmtCount = 100;
  int loopCount = 1000;

  @Before
  public void before() {
    for (int i = 0; i < elmtCount; i++) {
      for (List list : lists) {
        list.add(i);
      }
    }
  }

  @Test
  public void arrayWithForTest() {
    List list = lists.get(0);

    for (int i = 0; i < loopCount; i++) {
      for (int i1 = 0; i1 < list.size(); i1++) {
        System.out.println(list.get(i1));
      }
    }
  }

  @Test
  public void arrayWithIteratorTest() {
    List list = lists.get(0);
    for (int i = 0; i < loopCount; i++) {
      Iterator iterator = list.iterator();
      while (iterator.hasNext()) {
        System.out.println(iterator.next());
      }
    }
  }

  @Test
  public void linkedWithForTest() {
    List list = lists.get(1);
    for (int i = 0; i < loopCount; i++) {
      for (int i1 = 0; i1 < list.size(); i1++) {
        System.out.println(list.get(i1));
      }
    }
  }

  @Test
  public void linkedWithIteratorTest() {
    List list = lists.get(1);
    for (int i = 0; i < loopCount; i++) {
      Iterator iterator = list.iterator();
      while (iterator.hasNext()) {
        System.out.println(iterator.next());
      }
    }
  }

  @Test
  public void arrayWithRandomAccessTest() {
    List list = lists.get(0);
    if (list instanceof RandomAccess) {
      for (int i = 0; i < loopCount; i++) {
        for (int i1 = 0; i1 < list.size(); i1++) {
          System.out.println(list.get(i1));
        }
      }
    } else {
      for (int i = 0; i < loopCount; i++) {
        Iterator iterator = list.iterator();
        while (iterator.hasNext()) {
          System.out.println(iterator.next());
        }
      }
    }
  }

  @Test
  public void linkedWithRandomAccessTest() {
    List list = lists.get(1);
    if (list instanceof RandomAccess) {
      for (int i = 0; i < loopCount; i++) {
        for (int i1 = 0; i1 < list.size(); i1++) {
          System.out.println(list.get(i1));
        }
      }
    } else {
      for (int i = 0; i < loopCount; i++) {
        Iterator iterator = list.iterator();
        while (iterator.hasNext()) {
          System.out.println(iterator.next());
        }
      }
    }
  }
}