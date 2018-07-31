package com.github.anddd7.book.lock;

import java.util.Random;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.FutureTask;

public class Preloader {

  /**
   * 用来表示一个耗时的异步操作
   */
  private final FutureTask<String> futureTask = new FutureTask<>(new Callable<String>() {
    @Override
    public String call() throws RuntimeException, InterruptedException {
      Thread.sleep(5 * 1000);
      if (new Random().nextBoolean()) {
        throw new RuntimeException();
      }
      return "Completed";
    }
  });

  public final Thread thread = new Thread(futureTask);

  /**
   * 使用新的线程执行任务
   */
  public void start() {
    thread.start();
  }

  /**
   * 使用Future获取执行结果, 如果未完成则会阻塞当前线程
   */
  public String get() throws InterruptedException {
    try {
      return futureTask.get();
    } catch (ExecutionException e) {
      throw launderThrowable(e.getCause());
    }
  }

  private static RuntimeException launderThrowable(Throwable cause) {
    if (cause instanceof RuntimeException) {
      throw (RuntimeException) cause;
    } else if (cause instanceof Error) {
      throw (Error) cause;
    } else {
      return new IllegalStateException("Not unchecked:", cause);
    }
  }
}
