package com.github.anddd7;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class ThreadHelper {

  private static final int WAIT_TIME = 1000;

  public static ExecutorService executeTask(int threadNum, int redoTimes, Runnable task) {
    ExecutorService executorService = Executors.newFixedThreadPool(threadNum);
    for (int i = 0; i < redoTimes; i++) {
      executorService.execute(task);
    }
    return executorService;
  }

  public static void waitFor(ExecutorService executorService) throws InterruptedException {
    while (executorService.awaitTermination(WAIT_TIME, TimeUnit.MILLISECONDS)) {
    }
  }
}
