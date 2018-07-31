package com.github.anddd7.book.task;

import java.math.BigInteger;
import java.util.concurrent.BlockingQueue;

/**
 * 使用Interrupt作为取消线程的条件
 * - 当在while以外被interrupt时, 线程会在下一次while check时退出
 * - 当在put时被interrupt时, 会抛出异常并退出
 */
public class PrimeProducer extends Thread {

  private final BlockingQueue<BigInteger> queue;

  public PrimeProducer(BlockingQueue<BigInteger> queue) {
    this.queue = queue;
  }

  @Override
  public void run() {
    try {
      BigInteger p = BigInteger.ONE;
      while (!Thread.currentThread().isInterrupted()) {
        queue.put(p = p.nextProbablePrime());
      }
    } catch (InterruptedException e) {
      e.printStackTrace();
    }
  }

  public void cancel() {
    interrupt();
  }
}
