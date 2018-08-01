package com.github.anddd7.book.task.interrupt;

import java.io.PrintWriter;
import java.io.Writer;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * 日志消息: 多生产单消费, 由额外的线程打印日志
 * - 但是没有终止的功能
 */
public class LogService {

  private final BlockingQueue<String> queue;
  private final LoggerThread logger;
  private boolean isShutdown;
  private int reservations;

  public LogService(Writer writer) {
    this.queue = new LinkedBlockingQueue<>(10);
    this.logger = new LoggerThread(writer);
  }

  public void start() {
    logger.start();
  }

  public void stop() {
    // 原子操作, 设置flag
    synchronized (this) {
      isShutdown = true;
    }
    // 中断正在执行的线程
    logger.interrupt();
  }


  public void log(String msg) throws InterruptedException {
    // 生产时, 原子的检查flag
    synchronized (this) {
      if (isShutdown) {
        throw new IllegalStateException();
      }
      // 记录当前日志量
      ++reservations;
    }
    // queue的原子性由BlockingQueue保证
    queue.put(msg);
  }


  private class LoggerThread extends Thread {

    private final PrintWriter writer;

    public LoggerThread(Writer writer) {
      this.writer = new PrintWriter(writer);
    }

    @Override
    public void run() {
      try {
        while (true) {
          try {
            // 消费时, 原子的检查flag和剩余消息: 当线程标记关闭且剩余日志为0时退出
            synchronized (LogService.this) {
              if (isShutdown && reservations == 0) {
                break;
              }
            }
            String msg = queue.take();
            // 标记已消费当前的消息
            synchronized (LogService.this) {
              --reservations;
            }
            writer.println(msg);
          } catch (InterruptedException e) {
            // take过程中被中断, 重试
          }
        }
      } finally {
        writer.close();
      }
    }
  }
}

