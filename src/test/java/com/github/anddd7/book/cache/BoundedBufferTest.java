package com.github.anddd7.book.cache;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.lang.management.ManagementFactory;
import java.lang.management.MemoryMXBean;
import org.junit.Test;

public class BoundedBufferTest {

  @Test
  public void mod() {
    assertEquals(10, 10 % 15);
    assertEquals(5, 20 % 15);

    int i = 10;
    assertEquals(10, i++ % 15);
    assertEquals(12, ++i % 15);
  }

  @Test
  public void testIsEmptyWhenConstructed() {
    BoundedBuffer<Integer> buffer = new BoundedBuffer<>(10);
    assertTrue(buffer.isEmpty());
    assertFalse(buffer.isFull());
  }

  @Test
  public void testIsFullAfterPuts() throws InterruptedException {
    BoundedBuffer<Integer> buffer = new BoundedBuffer<>(10);
    for (int i = 0; i < 10; i++) {
      buffer.put(i);
    }
    assertFalse(buffer.isEmpty());
    assertTrue(buffer.isFull());
  }

  @Test
  public void testTakeBlocksWhenEmpty() {
    final BoundedBuffer<Integer> buffer = new BoundedBuffer<>(10);
    Thread take = new Thread() {
      @Override
      public void run() {
        try {
          // 当前资源为0, 因此buffer.take会阻塞
          buffer.take();
          // 如果take成功则表示未阻塞, 测试失败
          fail();
        } catch (InterruptedException success) {
          // 响应中断
        }
      }
    };
    long LOCKUP_DETECT_TIMEOUT = 1000L;
    try {
      take.start();
      // 等待take执行
      Thread.sleep(LOCKUP_DETECT_TIMEOUT);
      // 中断take
      take.interrupt();
      // 等待take退出
      take.join(LOCKUP_DETECT_TIMEOUT);
      assertFalse(take.isAlive());
    } catch (Exception unexpected) {
      fail();
    }
  }

  class Big {
    private int[] i = new int[100000];
  }

  @Test
  public void testLeak() throws InterruptedException {
    final int CAPACITY = 5000;
    MemoryMXBean memoryMXBean = ManagementFactory.getMemoryMXBean();

    BoundedBuffer<Big> buffer = new BoundedBuffer<>(CAPACITY);

    System.gc();
    System.out.println(memoryMXBean.getHeapMemoryUsage());
    for (int i = 0; i < CAPACITY; i++) {
      // buffer.put(0);
      // buffer.put(new Object());
      // 大对象
      buffer.put(new Big());
    }
    System.gc();
    System.out.println(memoryMXBean.getHeapMemoryUsage());
    for (int i = 0; i < CAPACITY; i++) {
      buffer.take();
    }
    System.gc();
    System.out.println(memoryMXBean.getHeapMemoryUsage());
  }
}