package com.github.anddd7.book.task;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

public class CancelTask {

  private static final ScheduledExecutorService cancelExec = Executors
      .newSingleThreadScheduledExecutor();

  public static void timedRun0(Runnable task, long timeout, TimeUnit unit) {
    final Thread taskThread = Thread.currentThread();
    // 指定时间过后对 执行timeRun方法的[当前]线程 进行中断
    cancelExec.schedule(new Runnable() {
      @Override
      public void run() {
        taskThread.interrupt();
      }
    }, timeout, unit);
    task.run();
  }

  public static void timedRun1(final Runnable task, long timeout, TimeUnit unit)
      throws InterruptedException {
    class RethrowableTask implements Runnable {

      private volatile Throwable t;

      @Override
      public void run() {
        try {
          task.run();
        } catch (Exception e) {
          this.t = e;
        }
      }

      void rethrow() {
        if (t != null) {
          throw (RuntimeException) t;
        }
      }
    }

    final RethrowableTask rethrowableTask = new RethrowableTask();
    // 使用已知的线程(这里是新的)进行中断, 因为你能够掌控中断后进行的操作
    final Thread taskThread = new Thread(rethrowableTask);
    taskThread.start();
    cancelExec.schedule(new Runnable() {
      @Override
      public void run() {
        taskThread.interrupt();
      }
    }, timeout, unit);
    // 如果中断发生, 这里会抛出中断异常
    taskThread.join(unit.toMillis(timeout));
    rethrowableTask.rethrow();
  }

  public static void timedRun(final Runnable task, long timeout, TimeUnit unit) {
    ExecutorService taskExec = Executors.newSingleThreadExecutor();
    Future future = taskExec.submit(task);
    try {
      future.get(timeout, unit);
    } catch (TimeoutException e) {
      // 任务超时
    } catch (ExecutionException e) {
      // 任务出现异常
    } catch (InterruptedException e) {
      // 任务被中断
    } finally {
      // 如果超时, 任务会被中断
      future.cancel(true);
    }
  }

}
