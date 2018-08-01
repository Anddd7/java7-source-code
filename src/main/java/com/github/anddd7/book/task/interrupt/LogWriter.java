package com.github.anddd7.book.task.interrupt;

import java.io.PrintWriter;
import java.io.Writer;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * 日志消息: 多生产单消费, 由额外的线程打印日志
 * - 但是没有终止的功能
 */
public class LogWriter {

  private final BlockingQueue<String> queue;
  private final LoggerThread logger;

  public LogWriter(Writer writer) {
    this.queue = new LinkedBlockingQueue<>(10);
    this.logger = new LoggerThread(writer);
  }

  public void start() {
    logger.start();
  }

  /**
   * 当日志(消费者)线程关闭时, 生产者线程因为队列满而阻塞在这里
   */
  public void log0(String msg) throws InterruptedException {
    queue.put(msg);
  }

  /**
   * 设置一个flag来标识消费者是否已关闭
   * - check-then-act 可能出现check通过而block在put上
   */
  private boolean shutdownRequested;

  public void log1(String msg) throws InterruptedException {
    if (!shutdownRequested) {
      queue.put(msg);
    } else {
      throw new IllegalStateException();
    }
  }


  public class LoggerThread extends Thread {

    private final PrintWriter writer;

    public LoggerThread(Writer writer) {
      this.writer = new PrintWriter(writer);
    }

    @Override
    public void run() {
      try {
        while (true) {
          writer.println(queue.take());
        }
      } catch (InterruptedException e) {
        // ignore
      } finally {
        writer.close();
      }
    }
  }
}

