package com.github.anddd7.book.pool;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TimingThreadPool extends ThreadPoolExecutor {

  private static final Logger log = LoggerFactory.getLogger(TimingThreadPool.class);

  private final ThreadLocal<Long> startTime = new ThreadLocal<>();
  private final AtomicInteger numTasks = new AtomicInteger();
  private final AtomicLong totalTime = new AtomicLong();

  public TimingThreadPool(
      int corePoolSize, int maximumPoolSize, long keepAliveTime,
      TimeUnit unit, BlockingQueue<Runnable> workQueue) {
    super(corePoolSize, maximumPoolSize, keepAliveTime, unit, workQueue);
  }

  @Override
  protected void beforeExecute(Thread t, Runnable r) {
    super.beforeExecute(t, r);
    log.info("Thread {} : start {}", t, r);
    startTime.set(System.nanoTime());
  }

  @Override
  protected void afterExecute(Runnable r, Throwable t) {
    try {
      long endTime = System.nanoTime();
      long taskTime = endTime - startTime.get();
      numTasks.incrementAndGet();
      totalTime.addAndGet(taskTime);
      log.info("Thread {} : end {}, time {}ns", t, r, taskTime);
    } finally {
      super.afterExecute(r, t);
    }
  }

  @Override
  protected void terminated() {
    try {
      log.info("Terminated : avg time={}ns", totalTime.get() / numTasks.get());
    } finally {
      super.terminated();
    }
  }
}
