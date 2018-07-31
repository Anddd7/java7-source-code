package com.github.anddd7.book.task;

import static java.util.concurrent.TimeUnit.SECONDS;

import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.DelayQueue;
import java.util.concurrent.ScheduledExecutorService;

/**
 * Timer不具备处理异常的能力, TimerTask出现异常会导致其他任务的终止
 *
 * @see ScheduledExecutorService 可以处理异常
 * @see DelayQueue 用于实现调度服务
 */
public class OutOfTimer {

  public static void main(String[] args) throws InterruptedException {
    Timer timer = new Timer();
    timer.schedule(new ThrowTask(), 1);
    SECONDS.sleep(1);
    timer.schedule(new ThrowTask(), 1);
    SECONDS.sleep(5);
  }

  static class ThrowTask extends TimerTask {

    @Override
    public void run() {
      throw new RuntimeException();
    }
  }
}
