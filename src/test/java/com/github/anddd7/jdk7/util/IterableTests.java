package com.github.anddd7.jdk7.util;

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
 * @see Iterable
 * 实现该接口的对象可以使用for迭代器
 * for (Object o : it) {
 * }
 *
 * 实质是被编译成：
 * for(Iterator it = list.iterator();it.hasNext();){
 * Object o = it.next();
 * }
 *
 * 这个接口主要是用来隐藏 ArrayList 和 LinkedList 遍历时候的不同实现 (配合 {@link Iterator} )
 */
@SuppressWarnings({"unused", "ResultOfMethodCallIgnored"})

public class IterableTests {

  private ArrayList arrayList = mock(ArrayList.class);
  private LinkedList linkedList = mock(LinkedList.class);

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
    for (Object o : arrayList) {
      itCount += (Integer) o;
    }
    for (Object o : linkedList) {
      itCount += (Integer) o;
    }

    verify(arrayList, times(1)).iterator();
    verify(linkedList, times(1)).iterator();
  }
}