package com.github.anddd7.book.container;

import java.util.HashSet;
import java.util.Random;
import java.util.Set;

public class HiddenIterator {

  private final Set<Integer> set = new HashSet<>();

  public synchronized void add(Integer i) {
    set.add(i);
  }

  public synchronized void remove(Integer i) {
    set.remove(i);
  }

  /**
   * 方法没有同步, 且:
   * Set.toString 方法中隐含了遍历操作
   */
    void addTenThings() {
    Random r = new Random();
    for (int i = 0; i < 10; i++) {
      add(r.nextInt());
    }
    System.out.println("add ten elements to " + set);
  }
}
