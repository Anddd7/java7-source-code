package github.eddy.java7core.util.concurrent;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import org.junit.Test;

/**
 * @author and777
 * @date 2018/1/31
 */
public class TestLock {

  private ExecutorService executor = Executors.newFixedThreadPool(5);
  private List<Integer> list = new ArrayList<>();
  private volatile boolean flag = true;

  private ReentrantLock lock = new ReentrantLock();
  private ReentrantReadWriteLock readWriteLock = new ReentrantReadWriteLock();

  private void submitTask(Runnable runnable) throws InterruptedException {
    for (int i = 0; i < 5; i++) {
      executor.submit(runnable);
    }
    shutdown();
  }

  private void shutdown() throws InterruptedException {
    executor.shutdown();
    while (!executor.awaitTermination(10, TimeUnit.MILLISECONDS)) {
    }
  }

  private void startPrint(boolean withLock) {
    System.out.println(
        Thread.currentThread()
            + (withLock ? "获得了锁" : "开始执行")
            + ",当前数量:" + list.size());
  }

  private void addList() {
    for (int j = 0; j < 100000; j++) {
      list.add(j);
    }
  }

  private void endPrint(boolean withLock) {
    System.out.println(
        Thread.currentThread()
            + (withLock ? "释放了锁" : "结束执行")
            + ",当前数量:" + list.size());
  }


  @Test
  public void testWithoutLock() throws InterruptedException {
    submitTask(new Runnable() {
      @Override
      public void run() {
        startPrint(false);
        addList();
        endPrint(false);
      }
    });
    System.out.println("结果:" + list.size());
  }

  @Test
  public void testLock4ReentrantLock() throws InterruptedException {
    submitTask(new Runnable() {
      @Override
      public void run() {
        lock.lock();
        try {
          startPrint(true);
          addList();
        } catch (Exception e) {
          e.printStackTrace();
        } finally {
          endPrint(true);
          lock.unlock();
        }
      }
    });
    System.out.println("结果:" + list.size());
  }


  /**
   * 三个线程争抢 ,只有一个能够拿到锁 ,没拿到就直接做其他事
   */
  @Test
  public void testTryLock() throws InterruptedException {
    for (int i = 0; i < 3; i++) {
      executor.submit(new Runnable() {
        @Override
        public void run() {
          try {
            if (lock.tryLock(10, TimeUnit.MILLISECONDS)) {
              try {
                System.out.println(Thread.currentThread() + "拿到锁了 ,开始执行任务");
                Thread.sleep(10);
              } catch (Exception e) {
                e.printStackTrace();
              } finally {
                lock.unlock();
              }
            } else {
              System.out.println(Thread.currentThread() + "直接做其他工作");
            }
          } catch (InterruptedException e) {
            e.printStackTrace();
          }
        }
      });
    }
    shutdown();
  }

  /**
   * 三个线程争抢 ,每个任务运行2s , 3秒后将取消未完成的任务
   */
  @Test
  public void testInterrupt() throws InterruptedException {
    Runnable runnable = new Runnable() {
      @Override
      public void run() {
        try {
          lock.lockInterruptibly();
          try {
            System.out.println(Thread.currentThread() + "拿到锁了 ,开始执行任务");
            Thread.sleep(2000);
          } catch (Exception e) {
            e.printStackTrace();
          } finally {
            lock.unlock();
          }
        } catch (InterruptedException e) {
          System.out.println(Thread.currentThread() + "被中断 ,直接做其他工作");
        }
      }
    };

    Thread t1 = new Thread(runnable, "t1");
    Thread t2 = new Thread(runnable, "t2");
    Thread t3 = new Thread(runnable, "t3");

    t1.start();
    t2.start();
    t3.start();

    t3.interrupt();

    t1.join();
    t2.join();
    t3.join();
  }

  /**
   * 3个读线程 ,1个写线程
   * - 读线程可以同时 "开始读"->"结束读"
   * - 写线程必须等待 "结束读"/"结束写" 后才能 "开始读"
   */
  @Test
  public void testReadWriteLock() throws InterruptedException {
    for (int i = 0; i < 3; i++) {
      executor.submit(new Runnable() {
        @Override
        public void run() {
          while (flag) {
            try {
              readWriteLock.readLock().lock();
              System.out.println(Thread.currentThread() + "正在读取数据");
              Thread.sleep(10);
              System.out.println(Thread.currentThread() + "读取完毕");
            } catch (Exception e) {
              e.printStackTrace();
            } finally {
              readWriteLock.readLock().unlock();
            }
          }
        }
      });
    }

    executor.submit(new Runnable() {
      int count = 0;

      @Override
      public void run() {
        while (count < 100) {
          try {
            readWriteLock.writeLock().lock();
            System.out.println(Thread.currentThread() + "正在写入数据");
            Thread.sleep(50);
            count++;
            System.out.println(Thread.currentThread() + "写入完毕");
          } catch (Exception e) {
            e.printStackTrace();
          } finally {
            readWriteLock.writeLock().unlock();
          }
          try {
            Thread.sleep(20);
          } catch (InterruptedException e) {
            e.printStackTrace();
          }
        }
        flag = false;
      }
    });
    shutdown();
  }
}
