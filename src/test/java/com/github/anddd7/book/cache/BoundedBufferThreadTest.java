package com.github.anddd7.book.cache;

import static org.junit.Assert.assertEquals;

import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;
import org.junit.Test;

public class BoundedBufferThreadTest {

  private static final ExecutorService pool = Executors.newCachedThreadPool();
  private final AtomicInteger putSum = new AtomicInteger(0);
  private final AtomicInteger takeSum = new AtomicInteger(0);
  private final BoundedBuffer<Integer> buffer;
  private final int nTrials, nPairs;

  private final BarrierTimer timer = new BarrierTimer();
  private final CyclicBarrier barrier;

  public BoundedBufferThreadTest() {
    this.buffer = new BoundedBuffer<>(10);
    this.nTrials = 100000;
    this.nPairs = 10;
    this.barrier = new CyclicBarrier(nPairs * 2 + 1, timer);
  }

  @Test
  public void test() {
    try {
      for (int i = 0; i < nPairs; i++) {
        pool.execute(new Producer());
        pool.execute(new Consumer());
      }
      // 等待所有任务就绪(10+10), 并开始执行
      barrier.await();
      // 等待所有任务执行
      barrier.await();
      System.out.println(
          String.format("Cost time: [%d] ns/item",
              timer.getTime() / (nPairs * nTrials))
      );
      assertEquals(putSum.get(), takeSum.get());
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }

  private int random(Object o) {
    return (o.hashCode() ^ (int) System.nanoTime());
  }

  private int xorShift(int y) {
    y ^= y << 6;
    y ^= y >>> 21;
    y ^= y << 7;
    return y;
  }

  class Producer implements Runnable {

    @Override
    public void run() {
      try {
        int seed = random(this);
        int sum = 0;
        barrier.await();
        for (int trials = nTrials; trials > 0; trials--) {
          buffer.put(seed);
          sum += seed;
          seed = xorShift(seed);
        }
        putSum.getAndAdd(sum);
        barrier.await();
      } catch (Exception e) {
        throw new RuntimeException(e);
      }
    }
  }

  class Consumer implements Runnable {

    @Override
    public void run() {
      try {
        barrier.await();
        int sum = 0;
        for (int trials = nTrials; trials > 0; trials--) {
          sum += buffer.take();
        }
        takeSum.getAndAdd(sum);
        barrier.await();
      } catch (Exception e) {
        throw new RuntimeException(e);
      }
    }
  }

  class BarrierTimer implements Runnable {

    private boolean started;
    private long startTime, endTime;

    @Override
    public synchronized void run() {
      long t = System.nanoTime();
      if (!started) {
        started = true;
        startTime = t;
      } else {
        endTime = t;
      }
    }

    public synchronized void clear() {
      started = false;
    }

    public synchronized long getTime() {
      return endTime - startTime;
    }
  }
}