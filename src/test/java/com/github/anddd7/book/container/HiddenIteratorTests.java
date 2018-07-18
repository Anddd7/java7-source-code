package com.github.anddd7.book.container;

import com.github.anddd7.ThreadHelper;
import com.github.anddd7.book.container.HiddenIterator;
import java.lang.Thread.UncaughtExceptionHandler;
import java.util.Collections;
import java.util.ConcurrentModificationException;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ThreadFactory;
import org.junit.Assert;
import org.junit.Test;

public class HiddenIteratorTests {

  private final Set<Throwable> throwables = Collections.synchronizedSet(new HashSet<Throwable>());

  @Test
  public void throwConcurrentModificationException() throws InterruptedException {

    final HiddenIterator hiddenIterator = new HiddenIterator();
    Runnable task = new Runnable() {
      @Override
      public void run() {
        hiddenIterator.addTenThings();
      }
    };
    ThreadFactory threadFactory = ThreadHelper
        .buildHandlerThreadFactory(new UncaughtExceptionHandler() {
          @Override
          public void uncaughtException(Thread t, Throwable e) {
            throwables.add(e);
          }
        });
    ThreadHelper.waitFor(
        ThreadHelper.executeTask(2, 10, task, threadFactory)
    );

    boolean haveConcurrentModificationException = false;
    for (Throwable throwable : throwables) {
      if (throwable instanceof ConcurrentModificationException) {
        haveConcurrentModificationException = true;
        break;
      }
    }
    Assert.assertTrue(haveConcurrentModificationException);
  }
}