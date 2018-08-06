package com.github.anddd7.book.puzzle;

import java.util.concurrent.CountDownLatch;

public class ValueLatch<T> {

  private T value = null;
  private final CountDownLatch done = new CountDownLatch(1);

  public T getValue() throws InterruptedException {
    done.await();
    // 这里的synchronized主要保证可见性 = volatile
    // await通过后, value已经被set并且不会再改变, 因此只要保证多线程对value的查看是相同的即可
    synchronized (this) {
      return value;
    }
  }

  public boolean isSet() {
    return done.getCount() == 0;
  }

  public synchronized void setValue(T newValue) {
    // check-then-act, 需要同步
    // 保证只会set成功一次
    if (!isSet()) {
      value = newValue;
      done.countDown();
    }
  }

}
