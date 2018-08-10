package com.github.anddd7.book.deadlock;

/**
 * 分段锁
 */
public class StripedMap {

  private static final int N_LOCKS = 16;
  private final Node[] buckets;
  private final Object[] locks;

  private static class Node {

    private Node next;
    private Object key;
    private Object value;
  }

  public StripedMap(int numBuckets) {
    this.buckets = new Node[numBuckets];
    this.locks = new Object[N_LOCKS];
    for (int i = 0; i < N_LOCKS; i++) {
      this.locks[i] = new Object();
    }
  }

  private final int indexOf(Object key) {
    return Math.abs(key.hashCode() % buckets.length);
  }

  public Object get(Object key) {
    int index = indexOf(key);
    synchronized (locks[index % N_LOCKS]) {
      for (Node m = buckets[index]; m != null; m = m.next) {
        if (m.key.equals(key)) {
          return m.value;
        }
      }
    }
    return null;
  }

  public void clear() {
    for (int i = 0; i < buckets.length; i++) {
      Node m;
      synchronized (locks[i % N_LOCKS]) {
        m = buckets[i];
        buckets[i] = null;
      }
      while (m != null) {
        Node n = m.next;
        m.key = null;
        m.value = null;
        m.next = null;
        m = n;
      }
    }
  }
}
