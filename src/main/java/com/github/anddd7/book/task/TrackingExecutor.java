package com.github.anddd7.book.task;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.AbstractExecutorService;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeUnit;

public class TrackingExecutor extends AbstractExecutorService {

  private final ExecutorService exec;
  private final Set<Runnable> tasksCancelledAtShutdown = Collections
      .synchronizedSet(new HashSet<Runnable>());

  public TrackingExecutor(ExecutorService exec) {
    this.exec = exec;
  }

  /**
   * 封装改写ExecutorService
   */
  @Override
  public void execute(final Runnable command) {
    exec.execute(new Runnable() {
      @Override
      public void run() {
        try {
          command.run();
        } finally {
          // 执行过程中被中断停止
          if (isShutdown() && Thread.currentThread().isInterrupted()) {
            tasksCancelledAtShutdown.add(command);
          }
        }
      }
    });
  }

  public List<Runnable> getCancelledTasks() {
    if (!isTerminated()) {
      throw new IllegalStateException();
    }
    return new ArrayList<>(tasksCancelledAtShutdown);
  }

  @Override
  public void shutdown() {
    exec.shutdown();
  }

  @Override
  public List<Runnable> shutdownNow() {
    return exec.shutdownNow();
  }

  @Override
  public boolean isShutdown() {
    return exec.isShutdown();
  }

  @Override
  public boolean isTerminated() {
    return exec.isTerminated();
  }

  @Override
  public boolean awaitTermination(long timeout, TimeUnit unit) throws InterruptedException {
    return exec.awaitTermination(timeout, unit);
  }
}
