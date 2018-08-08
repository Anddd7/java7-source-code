package com.github.anddd7.book.task;

import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class ReactiveTaskPool {

  private ExecutorService reactivePool = Executors.newFixedThreadPool(10);


  /**
   * 正常情况下, 同步方法按顺序执行
   */
  public void save(Map<String, Object> object) {
    if (!isExisted(object)) {
      object = create(object);
    }
    updateService1(object);
    updateService2(object);
  }


  /**
   * 响应式编程则是由scheduler进行调度
   */
  public void reactiveSave(final Map<String, Object> object) {
    final Future<Boolean> isExisted = reactivePool.submit(new Callable<Boolean>() {
      @Override
      public Boolean call() throws Exception {
        return isExisted(object);
      }
    });
    final Future<Map<String, Object>> created = reactivePool.submit(
        new Callable<Map<String, Object>>() {
          @Override
          public Map<String, Object> call() throws Exception {
            return isExisted.get() ? object : create(object);
          }
        }
    );
    reactivePool.submit(new Runnable() {
      @Override
      public void run() {
        try {
          updateService1(created.get());
        } catch (InterruptedException | ExecutionException e) {
          e.printStackTrace();
        }
      }
    });
    reactivePool.submit(new Runnable() {
      @Override
      public void run() {
        try {
          updateService2(created.get());
        } catch (InterruptedException | ExecutionException e) {
          e.printStackTrace();
        }
      }
    });
  }

  private boolean isExisted(Map<String, Object> object) {
    return false;
  }

  private Map<String, Object> create(Map<String, Object> object) {
    return object;
  }

  private void updateService1(Map<String, Object> object) {
  }

  private void updateService2(Map<String, Object> object) {
  }
}
