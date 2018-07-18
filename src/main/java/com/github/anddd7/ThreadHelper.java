package com.github.anddd7;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;

public class ThreadHelper {

  private static final int WAIT_TIME = 1000;

  public static ThreadFactory buildHandlerThreadFactory(
      final Thread.UncaughtExceptionHandler exceptionHandler) {
    return new ThreadFactory() {
      @Override
      public Thread newThread(Runnable r) {
        Thread t = new Thread(r);
        t.setUncaughtExceptionHandler(exceptionHandler);
        return t;
      }
    };
  }

  public static ExecutorService executeTask(int threadNum, int redoTimes, Runnable task) {
    return executeTask(threadNum, redoTimes, task, null);
  }

  public static ExecutorService executeTask(int threadNum, int redoTimes, Runnable task,
      ThreadFactory threadFactory) {
    List<Runnable> tasks = new ArrayList<>(redoTimes);
    for (int i = 0; i < redoTimes; i++) {
      tasks.add(task);
    }
    return executeTasks(threadNum, tasks, threadFactory);
  }

  public static ExecutorService executeTasks(int threadNum, List<Runnable> tasks) {
    return executeTasks(threadNum, tasks, null);
  }

  public static ExecutorService executeTasks(
      int threadNum, List<Runnable> tasks, ThreadFactory threadFactory) {
    ExecutorService executorService = threadFactory == null ?
        Executors.newFixedThreadPool(threadNum) :
        Executors.newFixedThreadPool(threadNum, threadFactory);
    for (Runnable task : tasks) {
      executorService.execute(task);
    }
    return executorService;
  }

  public static void waitFor(ExecutorService executorService) throws InterruptedException {
    while (executorService.awaitTermination(WAIT_TIME, TimeUnit.MILLISECONDS)) {
    }
  }

}
