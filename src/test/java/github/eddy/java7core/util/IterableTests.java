package github.eddy.java7core.util;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;
import org.junit.Before;
import org.junit.Test;

/**
 * @author and777
 * @date 2018/1/12
 *
 * {@link Iterable} 接口
 *
 * 标示这个对象可以使用for迭代器
 * for (Object o : it) {
 * }
 */
public class IterableTests {

  private final int count = 1000;
  private ArrayList<Integer> arrayList = mock(ArrayList.class);
  private LinkedList<Integer> linkedList = mock(LinkedList.class);
  private Iterator iterator = mock(Iterator.class);

  @Before
  public void before() {
    when(arrayList.get(any(Integer.class))).thenReturn(0);
    when(linkedList.get(any(Integer.class))).thenReturn(0);
    when(arrayList.iterator()).thenReturn(Collections.singletonList(0).iterator());
    when(linkedList.iterator()).thenReturn(Collections.singletonList(0).iterator());
  }

  @Test
  public void fori_ShouldNotCallIterator() {
    for (int i = 0; i < 10; i++) {
      arrayList.get(i);
      linkedList.get(i);
    }

    verify(arrayList, times(10)).get(any(Integer.class));
    verify(linkedList, times(10)).get(any(Integer.class));
    verify(arrayList, times(0)).iterator();
    verify(linkedList, times(0)).iterator();
  }

  @Test
  public void for_ShouldCallIterator() {
    int itCount = 0;
    for (Integer integer : arrayList) {
      itCount += integer;
    }
    for (Integer integer : linkedList) {
      itCount += integer;
    }

    verify(arrayList, times(1)).iterator();
    verify(linkedList, times(1)).iterator();
  }
}