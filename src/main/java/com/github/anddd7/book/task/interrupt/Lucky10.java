package com.github.anddd7.book.task.interrupt;

import java.io.IOException;

/**
 * 简单的中断模拟
 * - 一个线程每毫秒打印一次时间, 另一个线程监听用户输入
 * - 用户输入任意字符时对计时线程进行中断, 计时线程停止计时
 */
public class Lucky10 {

  static class Time extends Thread {

    @Override
    public void run() {
      long startTime = System.currentTimeMillis();
      while (!Thread.currentThread().isInterrupted()) {
        try {
          System.out.println("当前:" + (System.currentTimeMillis() - startTime));
          Thread.sleep(1);
        } catch (InterruptedException e) {
          // sleep的时候被中断了
          // 底层已经恢复了中断状态 interrupted = false, 只抛出了异常
          // 因此要想isInterrupted生效, 我们必须手动设置中断状态
          Thread.currentThread().interrupt();
        }
      }
      System.out.println("用户终止:" + (System.currentTimeMillis() - startTime));
    }
  }

  public static void main(String[] args) throws IOException {
    Time time = new Time();

    System.out.println("开始计时, 回车停止");
    time.start();
    System.in.read();
    time.interrupt();
  }

}
