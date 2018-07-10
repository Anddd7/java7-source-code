package com.github.anddd7.book.basic;

import com.github.anddd7.book.annotation.NotThreadSafe;
import com.github.anddd7.book.annotation.ThreadSafe;
import java.util.Random;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;

/**
 * 可见性问题 -> volatile原理和用法
 */
@NotThreadSafe
public class NoVisibility {

  private int number;
  private boolean ready;

  private final ExecutorService executor;
  private final Random random;

  NoVisibility(final ExecutorService executor, final Random random) {
    this.executor = executor;
    this.random = random;
  }

  private class Reader implements Callable<Integer> {

    @Override
    public Integer call() {
      while (!ready) {
        Thread.yield();
      }
      return number;
    }
  }

  int go() throws ExecutionException, InterruptedException {
    // 在另一个线程中读取属性值
    Future<Integer> submission = executor.submit(new Reader());
    // 在当前线程中设置属性值
    int randomNumber = random.nextInt();
    /* 因为 指令重排序/处理器缓存 可能使下面的语句执行顺序变化, 使ready=true时, number还未赋值 */
    this.number = randomNumber;
    this.ready = true;
    // 在当前线程中比较差异值: 结果不为0则说明Reader线程中读取的ready=true而number不同(为0)
    return randomNumber - submission.get();
  }

  /**
   * volatile能确保变量的可见性: 编译器不会对其重排序;处理器不会缓存
   * 因此对visibleReady的赋值顺序不会被重排, 但不能保证原子操作(例如 i++)
   */
  private volatile boolean visibleReady;

  private class VisibleReader implements Callable<Integer> {

    @Override
    public Integer call() {
      while (!visibleReady) {
        Thread.yield();
      }
      return number;
    }
  }


  @NotThreadSafe
  public class MutableInteger {

    private int value;

    public int getValue() {
      return value;
    }

    public void setValue(int value) {
      this.value = value;
    }
  }

  @ThreadSafe
  public class SynchronizedInteger {

    private int value;

    public synchronized int getValue() {
      return value;
    }

    public synchronized void setValue(int value) {
      this.value = value;
    }
  }
}
