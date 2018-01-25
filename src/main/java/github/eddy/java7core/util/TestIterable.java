package github.eddy.java7core.util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import org.junit.Before;
import org.junit.Test;

/**
 * @author and777
 * @date 2018/1/12
 */
public class TestIterable {

  List<? extends List> lists = Arrays.asList(new ArrayList(), new LinkedList());
  int elmtCount = 1000;

  @Before
  public void before() {
    for (int i = 0; i < elmtCount; i++) {
      for (List list : lists) {
        list.add(i);
      }
    }
  }

  @Test
  public void foriTest() {
    for (List list : lists) {
      for (int i = 0; i < list.size(); i++) {
        System.out.print(list.get(i));
      }
    }
  }

  @Test
  public void forTest() {
    for (List list : lists) {
      for (Object o : list) {
        System.out.print(o);
      }
    }
  }

  @Test
  public void forIteratorTest() {
    for (List list : lists) {
      for (Iterator iterator = list.iterator(); iterator.hasNext(); ) {
        System.out.print(iterator.next());
      }
    }
  }

  @Test
  public void whileIteratorTest() {
    for (List list : lists) {
      Iterator iterator = list.iterator();
      while (iterator.hasNext()) {
        System.out.print(iterator.next());
      }
    }
  }
}
