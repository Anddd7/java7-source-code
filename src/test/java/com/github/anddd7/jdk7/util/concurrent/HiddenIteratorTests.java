package com.github.anddd7.jdk7.util.concurrent;

import com.github.anddd7.ThreadHelper;
import java.lang.Thread.UncaughtExceptionHandler;
import java.util.Collections;
import java.util.ConcurrentModificationException;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import org.junit.Assert;
import org.junit.Test;

public class HiddenIteratorTests {

  private final Set<Throwable> throwables = Collections.synchronizedSet(new HashSet<Throwable>());

  @Test
  public void throwConcurrentModificationException() throws InterruptedException {

    final HiddenIterator hiddenIterator = new HiddenIterator();
    ExecutorService executorService = ThreadHelper.executeTask(2, 10,
        new Runnable() {
          @Override
          public void run() {
            hiddenIterator.addTenThings();
          }
        }, ThreadHelper.buildHandlerThreadFactory(new UncaughtExceptionHandler() {
          @Override
          public void uncaughtException(Thread t, Throwable e) {
            throwables.add(e);
          }
        }));
    ThreadHelper.waitFor(executorService);

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