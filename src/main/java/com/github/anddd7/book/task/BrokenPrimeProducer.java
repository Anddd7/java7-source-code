package com.github.anddd7.book.task;

import java.math.BigInteger;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

public class BrokenPrimeProducer implements Runnable {

  private final BlockingQueue<BigInteger> queue;
  private volatile boolean cancelled;

  public BrokenPrimeProducer(BlockingQueue<BigInteger> queue) {
    this.queue = queue;
  }

  @Override
  public void run() {
    try {
      BigInteger p = BigInteger.ONE;
      while (!cancelled) {
        // 阻塞在这里的时候cancel程序会永远阻塞
        queue.put(p = p.nextProbablePrime());
      }
    } catch (InterruptedException e) {
      e.printStackTrace();
    }
  }

  public void cancel() {
    this.cancelled = true;
  }

  public static void main(String[] args) throws InterruptedException {
    BlockingQueue<BigInteger> primes = new ArrayBlockingQueue<>(10);
    BrokenPrimeProducer producer = new BrokenPrimeProducer(primes);
    new Thread(producer).start();
    try {
      while (needMore()) {
        primes.take();
      }
    } finally {
      // 执行这个之前producer线程又put了新的元素, 并阻塞在put
      producer.cancel();
    }
  }

  private static boolean needMore() {
    return false;
  }
}
