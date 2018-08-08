package com.github.anddd7.book.deadlock;

import java.util.Random;
import javax.naming.InsufficientResourcesException;

@SuppressWarnings({"Duplicates"})
public class DemonstrateDeadLock {

  private static final int NUM_THREADs = 20;
  private static final int NUM_ACCOUNTS = 5;
  private static final int NUM_ITERATIONS = 1000000;
  private static final Random r = new Random();

  public static void main(String[] args) {
    final Account[] accounts = new Account[NUM_ACCOUNTS];
    for (int i = 0; i < accounts.length; i++) {
      accounts[i] = new Account(r.nextInt(1000));
    }

    class TransferThread extends Thread {

      @Override
      public void run() {
        for (int j = 0; j < NUM_ITERATIONS; j++) {
          int fromAccount = r.nextInt(NUM_ACCOUNTS);
          int toAccount = r.nextInt(NUM_ACCOUNTS);
          DollarAmount amount = new DollarAmount(r.nextInt(1000));
          try {
            transferMoney(accounts[fromAccount], accounts[toAccount], amount);
          } catch (InsufficientResourcesException e) {
          }
        }
      }
    }

    for (int m = 0; m < NUM_THREADs; m++) {
      new TransferThread().start();
    }
  }

  /**
   * 会发生死锁: from和to会可能交换位置, 出现顺序死锁
   */
  @Deprecated
  private static void transferMoney_0(Account from, Account to, DollarAmount amount)
      throws InsufficientResourcesException {
    synchronized (from) {
      synchronized (to) {
        transfer(from, to, amount);
      }
    }
  }

  /**
   * 使用其他手段保证锁顺序
   */
  private static final Object TIE_LOCK = new Object();

  private static void transferMoney(Account from, Account to, DollarAmount amount)
      throws InsufficientResourcesException {

    if (from.compareTo(to) > 0) {
      synchronized (from) {
        synchronized (to) {
          transfer(from, to, amount);
        }
      }
    } else if (from.compareTo(to) < 0) {
      synchronized (to) {
        synchronized (from) {
          transfer(from, to, amount);
        }
      }
    } else {
      synchronized (TIE_LOCK) {
        transfer(from, to, amount);
      }
    }
  }

  private static void transfer(Account from, Account to, DollarAmount amount)
      throws InsufficientResourcesException {
    if (from.getBalance().compareTo(amount) < 0) {
      throw new InsufficientResourcesException();
    } else {
      from.debit(amount);
      to.credit(amount);
    }
  }


  private static class Account implements Comparable<Account> {

    private DollarAmount balance;

    Account(int balance) {
      this.balance = new DollarAmount(balance);
    }

    DollarAmount getBalance() {
      return balance;
    }

    void debit(DollarAmount amount) {
      balance.value -= amount.value;
    }

    void credit(DollarAmount amount) {
      balance.value += amount.value;
    }

    @Override
    public int compareTo(Account o) {
      // not implement
      return 0;
    }
  }

  private static class DollarAmount implements Comparable<DollarAmount> {

    private int value;

    DollarAmount(int value) {
      this.value = value;
    }

    @Override
    public int compareTo(DollarAmount o) {
      return Integer.compare(value, o.value);
    }
  }
}
