package com.github.anddd7.book.basic;

import static java.lang.String.format;

import com.github.anddd7.ThreadHelper;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
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
    ExecutorService executorService = ThreadHelper.executeTask(2, REDO_TIMER, new Runnable() {
      @Override
      public void run() {
        unsafeCountingAddition.service(request, response);
      }
    });
    ThreadHelper.waitFor(executorService);
    System.out.println(
        format("%s final %s", unsafeCountingAddition, unsafeCountingAddition.getCount()));
  }

  @Test
  public void countingAddition_service() throws InterruptedException {
    ExecutorService executorService = ThreadHelper.executeTask(2, REDO_TIMER, new Runnable() {
      @Override
      public void run() {
        atomicCountingAddition.service(request, response);
      }
    });
    ThreadHelper.waitFor(executorService);
    System.out.println(
        format("%s final %s", atomicCountingAddition, atomicCountingAddition.getCount()));
  }

  private UnsafeCachingAddition unsafeCachingAddition = new UnsafeCachingAddition(REDO_TIMER);
  private SynchronizedCachingAddition synchronizedCachingAddition =
      new SynchronizedCachingAddition(REDO_TIMER);

  @Test
  public void unsafeCachingAddition_getThenSet() throws InterruptedException {
    ExecutorService executorService = ThreadHelper.executeTask(2, REDO_TIMER, new Runnable() {
      @Override
      public void run() {
        unsafeCachingAddition.getThenSet();
      }
    });
    ThreadHelper.waitFor(executorService);
    System.out.println(
        format("%s final %s", unsafeCachingAddition, unsafeCachingAddition.getLastFirst()));
  }

  @Test
  public void synchronizedCachingAddition_methodSync() throws InterruptedException {
    ExecutorService executorService = ThreadHelper.executeTask(2, REDO_TIMER, new Runnable() {
      @Override
      public void run() {
        synchronizedCachingAddition.methodSync();
      }
    });
    ThreadHelper.waitFor(executorService);
    System.out.println(
        format("%s final %s", synchronizedCachingAddition,
            synchronizedCachingAddition.getLastFirst()));
  }

  @Test
  public void synchronizedCachingAddition_objectSync() throws InterruptedException {
    ExecutorService executorService = ThreadHelper.executeTask(2, REDO_TIMER, new Runnable() {
      @Override
      public void run() {
        synchronizedCachingAddition.objectSync();
      }
    });
    ThreadHelper.waitFor(executorService);
    System.out.println(format("%s final %s", synchronizedCachingAddition,
        synchronizedCachingAddition.getLastFirst()));
  }

  @Test
  public void synchronizedCachingAddition_reduce() {
    System.out.println(format("%s reduce will not stop by sync - %s", synchronizedCachingAddition,
        synchronizedCachingAddition.reduce(100)));
  }
}