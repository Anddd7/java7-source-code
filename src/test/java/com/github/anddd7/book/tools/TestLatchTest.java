package com.github.anddd7.book.tools;

import org.junit.Test;

public class TestLatchTest {

  @Test
  public void timeTasks() throws InterruptedException {
    TestLatch testHarness = new TestLatch(5,
        new Runnable() {
          @Override
          public void run() {
            try {
              Thread.sleep(1000);
            } catch (InterruptedException e) {
              e.printStackTrace();
            }
          }
        });
    Thread.sleep(1000);
    System.out.println(String.format("cost time:[%s]nano", testHarness.startTasks()));
  }
}