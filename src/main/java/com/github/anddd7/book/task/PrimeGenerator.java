package com.github.anddd7.book.task;

import static java.util.concurrent.TimeUnit.SECONDS;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

public class PrimeGenerator implements Runnable {

  private final List<BigInteger> primes = new ArrayList<>();
  /**
   * volatile 保证取消标志的可见性
   */
  private volatile boolean cancelled;

  @Override
  public void run() {
    BigInteger p = BigInteger.ONE;
    /**
     * 每次执行时check
     */
    while (!cancelled) {
      p = p.nextProbablePrime();
      synchronized (this) {
        primes.add(p);
      }
    }
  }

  public void cancel() {
    this.cancelled = true;
  }

  public synchronized List<BigInteger> getPrimes() {
    return new ArrayList<>(primes);
  }


  public static void main(String[] args) throws InterruptedException {
    PrimeGenerator primeGenerator = new PrimeGenerator();
    new Thread(primeGenerator).start();
    try {
      SECONDS.sleep(1);
    } finally {
      primeGenerator.cancel();
    }
    primeGenerator.getPrimes();
  }
}
