package com.github.anddd7.book.deadlock;

public class LeftRightDeadLock {

  private final Object left = new Object();
  private final Object right = new Object();

  public void leftRight() throws InterruptedException {
    synchronized (left) {
      System.out.println(Thread.currentThread() + "拿到了left锁");
      Thread.sleep(1000);
      synchronized (right) {
        System.out.println(Thread.currentThread() + "拿到了right锁");

      }
    }
  }

  public void rightLeft() throws InterruptedException {
    synchronized (right) {
      System.out.println(Thread.currentThread() + "拿到了right锁");
      Thread.sleep(1000);
      synchronized (left) {
        System.out.println(Thread.currentThread() + "拿到了left锁");
      }
    }
  }

  public static void main(String[] args) {
    final LeftRightDeadLock deadLock = new LeftRightDeadLock();
    new Thread(new Runnable() {
      @Override
      public void run() {
        try {
          deadLock.leftRight();
        } catch (InterruptedException e) {
          e.printStackTrace();
        }
      }
    }).start();
    new Thread(new Runnable() {
      @Override
      public void run() {
        try {
          deadLock.rightLeft();
        } catch (InterruptedException e) {
          e.printStackTrace();
        }
      }
    }).start();
  }
}
