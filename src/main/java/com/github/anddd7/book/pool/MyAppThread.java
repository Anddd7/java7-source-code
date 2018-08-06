package com.github.anddd7.book.pool;

import java.util.concurrent.atomic.AtomicInteger;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MyAppThread extends Thread {

  public static final String DEFAULT_NAME = "MyAppThreadFactory";
  private static volatile boolean debugLifeStyle = false;
  private static final AtomicInteger CREATED = new AtomicInteger();
  private static final AtomicInteger ALIVE = new AtomicInteger();
  private static final Logger log = LoggerFactory.getLogger(MyAppThread.class);

  public MyAppThread(Runnable r) {
    this(r, DEFAULT_NAME);
  }

  public MyAppThread(Runnable r, String poolName) {
    super(r, poolName + "-" + CREATED.incrementAndGet());
    setUncaughtExceptionHandler(new UncaughtExceptionHandler() {

      @Override
      public void uncaughtException(Thread t, Throwable e) {
        log.error("Uncaught Exception: {}", e.getMessage(), e);
      }
    });
  }

  @Override
  public void run() {
    boolean debug = debugLifeStyle;
    if (debug) {
      log.debug("Created: {}", getName());
    }
    try {
      ALIVE.incrementAndGet();
      super.run();
    } finally {
      ALIVE.decrementAndGet();
      if (debug) {
        log.debug("Exited: {}", getName());
      }
    }
  }

  public static int getThreadsCreated() {
    return CREATED.get();
  }

  public static int getThreadsAlive() {
    return ALIVE.get();
  }

  public static boolean getDebug() {
    return debugLifeStyle;
  }

  public static void setDebug(boolean b) {
    debugLifeStyle = b;
  }
}
