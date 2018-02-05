package github.eddy.java7core.util.concurrent;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import org.junit.Test;

/**
 * @author and777
 * @date 2018/1/30
 */
public class TestSyncAndVolatile {


  private boolean runFlagA = true;
  private volatile boolean runFlagB = true;
  /**
   * volatile 标识的字段不会受缓存影响
   * A: 使用的普通的变量 ,当另一个线程修改了变量值后 ,当前工作环境的runFlagA变量仍旧使用的自己的缓存值(true) ,无法停止
   * B: 使用的volatile变量 ,当另一个线程修改了变量值后即时的刷新到了当前线程 ,循环随即停止
   */
  @Test
  public void volatileTest() throws InterruptedException {
    ExecutorService executor = Executors.newFixedThreadPool(4);

    executor.submit(new Runnable() {
      int count = 0;

      @Override
      public void run() {
        while (runFlagA) {
          count++;
        }
        //it won't stop
        System.out.println("runFlagA has terminated:" + count);
      }
    });

    executor.submit(new Runnable() {
      @Override
      public void run() {
        try {
          Thread.sleep(1000);
          runFlagA = false;
          System.out.println("Set runFlagA to false");
        } catch (InterruptedException e) {
          e.printStackTrace();
        }
      }
    });

    executor.submit(new Runnable() {
      int count = 0;

      @Override
      public void run() {
        while (runFlagB) {
          count++;
        }
        System.out.println("runFlagB has terminated:" + count);
      }
    });

    executor.submit(new Runnable() {
      @Override
      public void run() {
        try {
          Thread.sleep(1000);
          runFlagB = false;
          System.out.println("Set runFlagB to false");
        } catch (InterruptedException e) {
          e.printStackTrace();
        }
      }
    });

    executor.shutdown();
    while (!executor.awaitTermination(10, TimeUnit.MILLISECONDS)) {
    }
  }

}
