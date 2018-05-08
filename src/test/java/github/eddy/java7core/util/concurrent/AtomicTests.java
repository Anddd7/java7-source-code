package github.eddy.java7core.util.concurrent;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicStampedReference;
import org.junit.Test;

/**
 * @author and777
 * @see java.util.concurrent.atomic.AtomicBoolean
 * @see java.util.concurrent.atomic.AtomicInteger
 * @see java.util.concurrent.atomic.AtomicLong
 * @see java.util.concurrent.atomic.AtomicReference
 *
 * CAS 实现探究 和 Atomic 类
 *
 * TODO
 */
public class AtomicTests {


  private ExecutorService executor = Executors.newFixedThreadPool(5);
  private AtomicInteger atomicInteger = new AtomicInteger(0);
  private Integer integer = 0;
  private List<Integer> integers = new ArrayList<>(Collections.singleton(0));

  /**
   * Atomic 基本类型
   */
  @Test
  public void cas() throws InterruptedException {
    for (int i = 0; i < 5; i++) {
      executor.submit(new Runnable() {
        @Override
        public void run() {
          for (int i = 0; i < 100000; i++) {
            safeCount();
            unsafeCount();
          }
        }
      });
    }
    executor.shutdown();
    while (!executor.awaitTermination(10, TimeUnit.MILLISECONDS)) {
    }
    System.out.println(atomicInteger.get());
    System.out.println(integer);
    System.out.println(integers.get(0));
  }

  /**
   * CAS {@link AtomicInteger#compareAndSet(int, int)} ,通过JVM (CMPXCHG 指令) 保证原子性
   * 自旋CAS {@link AtomicInteger#addAndGet(int)} ,保证set操作的原子性
   *
   * 缺点 :
   * 1.ABA ,值反复变化时 ,CAS会认为没有变化 : 解决 {@link AtomicStampedReference}
   * 2.循环导致资源浪费
   * 3.只能保证一个共享变量的原子性
   */
  private void safeCount() {
    while (true) {
      int i = atomicInteger.get();
      if (atomicInteger.compareAndSet(i, ++i)) {
        break;
      }
    }
  }

  private void unsafeCount() {
    integer++;
  }

  /**
   * {@link java.util.concurrent.atomic.AtomicIntegerArray}
   * {@link java.util.concurrent.atomic.AtomicLongArray}
   *
   * {@link java.util.concurrent.atomic.AtomicIntegerFieldUpdater}
   *
   *
   * {@link java.util.concurrent.atomic.AtomicLongFieldUpdater}
   * {@link java.util.concurrent.atomic.AtomicMarkableReference}
   *
   * {@link java.util.concurrent.atomic.AtomicReferenceArray}
   * {@link java.util.concurrent.atomic.AtomicReferenceFieldUpdater}

   * {@link AtomicStampedReference}
   */
}
