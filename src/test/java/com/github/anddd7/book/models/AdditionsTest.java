package com.github.anddd7.book.models;

import static java.lang.String.format;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import org.junit.Before;
import org.junit.Test;

public class AdditionsTest {

  private UnsafeCountingAddition unsafeCountingAddition = new UnsafeCountingAddition();
  private AtomicCountingAddition atomicCountingAddition = new AtomicCountingAddition();

  private final int REDO_TIMER = 100000;

  private Map<String, Integer> request = new HashMap<>();

  private Map<String, Integer> response = new HashMap<>();

  @Before
  public void setup() {
    request.put("first", 1);
    request.put("second", 1);
  }

  @Test
  public void unsafeCountingAddition_service() throws InterruptedException {
    ExecutorService executorService = Executors.newFixedThreadPool(2);
    for (int i = 0; i < REDO_TIMER; i++) {
      executorService.submit(new Runnable() {
        @Override
        public void run() {
          unsafeCountingAddition.service(request, response);
        }
      });
    }
    while (executorService.awaitTermination(1000, TimeUnit.MILLISECONDS)) {
    }
    System.out.println(
        format("%s final %s", unsafeCountingAddition, unsafeCountingAddition.getCount()));
  }

  @Test
  public void countingAddition_service() throws InterruptedException {
    ExecutorService executorService = Executors.newFixedThreadPool(2);
    for (int i = 0; i < REDO_TIMER; i++) {
      executorService.submit(new Runnable() {
        @Override
        public void run() {
          atomicCountingAddition.service(request, response);
        }
      });
    }
    while (executorService.awaitTermination(1000, TimeUnit.MILLISECONDS)) {
    }
    System.out.println(
        format("%s final %s", atomicCountingAddition, atomicCountingAddition.getCount()));
  }

  private UnsafeCachingAddition unsafeCachingAddition = new UnsafeCachingAddition(REDO_TIMER);
  private SynchronizedCachingAddition synchronizedCachingAddition =
      new SynchronizedCachingAddition(REDO_TIMER);

  @Test
  public void unsafeCachingAddition_getThenSet() throws InterruptedException {
    ExecutorService executorService = Executors.newFixedThreadPool(2);
    for (int i = 0; i < REDO_TIMER; i++) {
      executorService.submit(new Runnable() {
        @Override
        public void run() {
          unsafeCachingAddition.getThenSet();
        }
      });
    }
    while (executorService.awaitTermination(1000, TimeUnit.MILLISECONDS)) {
    }
    System.out.println(
        format("%s final %s", unsafeCachingAddition, unsafeCachingAddition.getLastFirst()));
  }

  @Test
  public void synchronizedCachingAddition_methodSync() throws InterruptedException {
    ExecutorService executorService = Executors.newFixedThreadPool(2);
    for (int i = 0; i < REDO_TIMER; i++) {
      executorService.submit(new Runnable() {
        @Override
        public void run() {
          synchronizedCachingAddition.methodSync();
        }
      });
    }
    while (executorService.awaitTermination(1000, TimeUnit.MILLISECONDS)) {
    }
    System.out.println(
        format("%s final %s", synchronizedCachingAddition,
            synchronizedCachingAddition.getLastFirst()));
  }

  @Test
  public void synchronizedCachingAddition_objectSync() throws InterruptedException {
    ExecutorService executorService = Executors.newFixedThreadPool(2);
    for (int i = 0; i < REDO_TIMER; i++) {
      executorService.submit(new Runnable() {
        @Override
        public void run() {
          synchronizedCachingAddition.objectSync();
        }
      });
    }
    while (executorService.awaitTermination(1000, TimeUnit.MILLISECONDS)) {
    }
    System.out.println(format("%s final %s", synchronizedCachingAddition,
        synchronizedCachingAddition.getLastFirst()));
  }

  @Test
  public void synchronizedCachingAddition_reduce() {
    System.out.println(format("%s reduce will not stop by sync - %s", synchronizedCachingAddition,
        synchronizedCachingAddition.reduce(100)));
  }
}