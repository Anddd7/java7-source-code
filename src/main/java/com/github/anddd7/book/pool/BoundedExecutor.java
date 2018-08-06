package com.github.anddd7.book.pool;

import java.util.concurrent.Executor;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.Semaphore;

/**
 * 用信号量进行计数, 并阻塞多余任务的提交
 */
public class BoundedExecutor {

  private final Executor exec;
  private final Semaphore semaphore;

  public BoundedExecutor(Executor exec, int bound) {
    this.exec = exec;
    this.semaphore = new Semaphore(bound);
  }

  public void submitTask(final Runnable command) throws InterruptedException {
    semaphore.acquire();
    try {
      exec.execute(new Runnable() {
        @Override
        public void run() {
          try {
            command.run();
          } finally {
            // 提交成功并执行完毕
            semaphore.release();
          }
        }
      });
    } catch (RejectedExecutionException e) {
      // 提交时被拒绝, 释放资源让给下一个提交
      semaphore.release();
    }
  }
}
