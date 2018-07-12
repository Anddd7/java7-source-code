package com.github.anddd7.jdk7.util.concurrent;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.ConcurrentModificationException;
import java.util.Iterator;
import java.util.List;
import org.junit.Test;

/**
 * 同步异常主要用来表示: [迭代器]在对某数据进行操作的同时被其他线程所修改
 *
 * 当容器出现 get/set/remove 混乱时, 通常是抛出 IndexOutOfBoundsException,NoSuchElementException
 * -  size和elementData不相符, 操作超界
 * 但是引入迭代器后, 外部可以通过迭代器和容器同时进行操作, 但内部变量其实是共享的
 * -  如何判断是 容器还是迭代器 操作产生的不同步
 * 因此, 使用了 modCount和ConcurrentModificationException 来特别的标识迭代器中的不同步情况
 *
 * @see SynchronizedContainersTests
 * @see ConcurrentModificationException
 */
public class ConcurrentModificationExceptionTests {

  private List<String> buildList() {
    return new ArrayList<>(Arrays.asList("aaa", "bbb", "ccc", "ddd"));
  }

  private void iterator(List<String> list) {
    for (String s : list) {
      System.out.println(s + "  ");
    }
  }

  /**
   * list的forEach是对iterator方法的语法糖
   *
   * @see this#listRemoveWhileIteratorLoop()
   */
  @Test(expected = ConcurrentModificationException.class)
  public void listRemoveWhileListForEach() {
    List<String> list = buildList();

    for (String s : list) {
      if ("aaa".equals(s)) {
        list.remove(s);
      }
    }
    iterator(list);
  }

  /**
   * 在迭代器执行过程中对list容器进行修改, 就可能触发同步异常
   */
  @Test(expected = ConcurrentModificationException.class)
  public void listRemoveWhileIteratorLoop() {

    List<String> list = buildList();
    Iterator<String> iterator = list.iterator();
    while (iterator.hasNext()) {
      String s = iterator.next();
      if ("aaa".equals(s)) {
        list.remove(s);
      }
    }
    iterator(list);
  }

  /**
   * 对迭代器进行remove操作会对modCount(判定同步异常的标签)进行同步修改
   * 但是在多线程环境中, 仍可能出现(未同步的进行remove等操作)
   */
  @Test
  public void iteratorRemoveWhileIteratorLoop() {
    List<String> list = buildList();
    Iterator<String> iterator = list.iterator();
    while (iterator.hasNext()) {
      String s = iterator.next();
      if ("aaa".equals(s)) {
        iterator.remove();
      }
    }
    iterator(list);
  }

  /**
   * fori循环是直接操作的list, 因此不会出现同步异常
   * 但是在多线程环境中, 仍可能出现(未同步的进行remove等操作)
   */
  @Test
  public void listRemoveWhileListFor() {
    List<String> list = buildList();
    for (int i = 0; i < list.size(); i++) {
      String s = list.get(i);
      if ("aaa".equals(s)) {
        list.remove(s);
      }
    }
    iterator(list);
  }
}
