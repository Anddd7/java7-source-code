package com.github.anddd7.book.cache;

import java.util.concurrent.Semaphore;

public class BoundedBuffer<E> {

  private final Semaphore availableItems, availableSpaces;
  private final E[] items;
  private int putPosition = 0, takePosition = 0;

  BoundedBuffer(int capacity) {
    this.availableItems = new Semaphore(0);
    this.availableSpaces = new Semaphore(capacity);
    this.items = (E[]) new Object[capacity];
  }

  public void put(E x) throws InterruptedException {
    availableSpaces.acquire();
    doInsert(x);
    availableItems.release();
  }


  public E take() throws InterruptedException {
    availableItems.acquire();
    E item = doExtract();
    availableSpaces.release();
    return item;
  }


  private synchronized void doInsert(E x) {
    int i = putPosition;
    items[i] = x;
    putPosition = ++i % items.length;
  }

  private synchronized E doExtract() {
    int i = takePosition;
    E x = items[i];
    items[i] = null;
    takePosition = ++i % items.length;
    return x;
  }

  boolean isEmpty() {
    return availableItems.availablePermits() == 0;
  }

  boolean isFull() {
    return availableSpaces.availablePermits() == 0;
  }
}
