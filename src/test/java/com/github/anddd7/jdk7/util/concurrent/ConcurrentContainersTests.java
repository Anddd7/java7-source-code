package com.github.anddd7.jdk7.util.concurrent;

import static java.util.Collections.synchronizedList;
import static java.util.Collections.synchronizedMap;

import com.github.anddd7.ThreadHelper;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;


public class ConcurrentContainersTests {

  private static final Integer ELEMENT_COUNT = 1000000;
  private static final Integer THREAD_COUNT = 2;

  private static final boolean TOGGLE_CONTAIN = true;

  private final ExecutorService executorService = Executors.newFixedThreadPool(THREAD_COUNT);
  private final Random r = new Random();

  private void addElement(Map<Integer, Object> map) {
    for (int i = 0; i < ELEMENT_COUNT; i++) {
      map.put(i, i);
    }
  }

  private void addElement(List<Integer> list) {
    for (int i = 0; i < ELEMENT_COUNT; i++) {
      list.add(i);
    }
  }

  @Before
  public void setUp() {
    syncMap = synchronizedMap(new HashMap<Integer, Object>());
    concurrentMap = new ConcurrentHashMap<>();
    syncList = synchronizedList(new ArrayList<Integer>());
    concurrentList = new CopyOnWriteArrayList<>();
  }

  @After
  public void tearDown() throws Exception {
    ThreadHelper.waitFor(executorService);
  }

  /**
   * 在多线程争抢的时候, 细粒度的锁(concurrent)能获得更高的执行效率
   * -  单线程: 无锁 HashMap 效率最高
   * -  多线程: Concurrent细节化控制, 效率更高
   * |  - 当且仅当需要对Map对象进行加锁时, 才放弃concurrent (一些特别的扩展需求)
   *
   * @see ConcurrentHashMapTests
   */
  private Map<Integer, Object> syncMap;
  private Map<Integer, Object> concurrentMap;

  @Test
  public void syncMap() throws InterruptedException {
    Runnable add = new Runnable() {
      @Override
      public void run() {
        addElement(syncMap);
      }
    };
    Runnable contains = new Runnable() {
      @Override
      public void run() {
        syncMap.containsKey(r.nextInt() % ELEMENT_COUNT);
      }
    };
    for (int i = 0; i < THREAD_COUNT; i++) {
      executorService.submit(add);
      if (TOGGLE_CONTAIN) {
        executorService.submit(contains);
      }
    }
  }

  @Test
  public void concurrentMap() throws InterruptedException {
    Runnable add = new Runnable() {
      @Override
      public void run() {
        addElement(concurrentMap);
      }
    };
    Runnable contains = new Runnable() {
      @Override
      public void run() {
        concurrentMap.containsKey(r.nextInt() % ELEMENT_COUNT);
      }
    };
    for (int i = 0; i < THREAD_COUNT; i++) {
      executorService.submit(add);
      if (TOGGLE_CONTAIN) {
        executorService.submit(contains);
      }
    }
  }

  private List<Integer> syncList;
  private List<Integer> concurrentList;

  @Test
  public void syncList() throws InterruptedException {
    Runnable add = new Runnable() {
      @Override
      public void run() {
        addElement(syncList);
      }
    };
    Runnable contains = new Runnable() {
      @Override
      public void run() {
        syncList.contains(r.nextInt() % ELEMENT_COUNT);
      }
    };
    for (int i = 0; i < THREAD_COUNT; i++) {
      executorService.submit(add);
      if (TOGGLE_CONTAIN) {
        executorService.submit(contains);
      }
    }
  }

  @Test
  public void concurrentList() throws InterruptedException {
    Runnable add = new Runnable() {
      @Override
      public void run() {
        addElement(concurrentList);
      }
    };
    Runnable contains = new Runnable() {
      @Override
      public void run() {
        concurrentList.contains(r.nextInt() % ELEMENT_COUNT);
      }
    };
    for (int i = 0; i < THREAD_COUNT; i++) {
      executorService.submit(add);
      if (TOGGLE_CONTAIN) {
        executorService.submit(contains);
      }
    }
  }


}