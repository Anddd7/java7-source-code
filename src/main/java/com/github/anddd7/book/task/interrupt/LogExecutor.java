package com.github.anddd7.book.task.interrupt;

import java.io.PrintWriter;
import java.io.Writer;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.TimeUnit;

/**
 * 日志消息: 多生产单消费, 由额外的线程打印日志
 * - 但是没有终止的功能
 */
public class LogExecutor {

  private final BlockingQueue<String> queue;
  private final ExecutorService exec;
  private final PrintWriter writer;

  public LogExecutor(Writer writer) {
    this.writer = new PrintWriter(writer);
    this.queue = new LinkedBlockingQueue<>(10);
    this.exec = Executors.newSingleThreadExecutor();
  }

  public void start() {
  }

  /**
   * 任务队列由Executor管理, shutdown进行软关闭(阻止新的任务提交)
   */
  public void stop() throws InterruptedException {
    try {
      exec.shutdown();
      // 阻塞线程直到所有任务完成
      exec.awaitTermination(10, TimeUnit.SECONDS);
    } finally {
      writer.close();
    }
  }

  public void log(final String msg) {
    try {
      exec.execute(new Runnable() {
        @Override
        public void run() {
          writer.write(msg);
        }
      });
    } catch (RejectedExecutionException e) {
      // 不能再提交新任务
    }
  }
}

