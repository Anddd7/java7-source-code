package com.github.anddd7.book.lock;

import com.github.anddd7.ThreadHelper;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;

/**
 * @see CountDownLatch 闭锁
 * 延迟线程的进度直到锁达到终极状态, 达到开锁条件后会一直开启
 */
public class TestLatch {

  private final CountDownLatch startGate;
  private final CountDownLatch endGate;
  private final ExecutorService executorService;

  public TestLatch(int nThreads, final Runnable task) {
    System.out.println("项目初始化...");

    this.startGate = new CountDownLatch(1);
    this.endGate = new CountDownLatch(nThreads);
    this.executorService = ThreadHelper.executeTask(nThreads, nThreads, initTask(task));
  }

  private Runnable initTask(final Runnable task) {
    return new Runnable() {
      @Override
      public void run() {
        try {
          System.out.println(String.format("%s:等待启动...", Thread.currentThread().getName()));
          startGate.await();
          try {
            System.out.println(String.format("%s:任务执行中", Thread.currentThread().getName()));
            task.run();
          } finally {
            System.out.println(String.format("%s:任务执行完成", Thread.currentThread().getName()));
            endGate.countDown();
          }
        } catch (InterruptedException e) {
        }
      }
    };
  }

  public long startTasks() throws InterruptedException {
    System.out.println("项目启动...");
    long start = System.nanoTime();
    startGate.countDown();

    endGate.await();
    System.out.println("项目结束...");

    ThreadHelper.waitFor(executorService);
    return System.nanoTime() - start;
  }
}
