package com.github.anddd7.jdk7.util.concurrent;

import java.util.HashSet;
import java.util.Set;

public class HiddenIterator {

  private final Set<Integer> set = new HashSet<>();

  public synchronized void add(Integer i) {
    set.add(i);
  }

  public synchronized void remove(Integer i) {
    set.remove(i);
  }

}
