package com.github.anddd7.book.lock;

import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CyclicBarrier;

public class CellularAutomate {

  private final Board mainBoard;
  private final CyclicBarrier barrier;
  private final Worker[] workers;

  public CellularAutomate(final Board mainBoard) {
    this.mainBoard = mainBoard;
    int count = Runtime.getRuntime().availableProcessors();
    this.barrier = new CyclicBarrier(count, new Runnable() {
      @Override
      public void run() {
        // 栅栏完成后会执行这个
        mainBoard.commitNewValues();
      }
    });
    this.workers = new Worker[count];
    for (int i = 0; i < count; i++) {
      workers[i] = new Worker(mainBoard.getSubBoard(count, i));
    }
  }

  private class Worker implements Runnable {

    private final Board board;

    public Worker(Board board) {
      this.board = board;
    }

    @Override
    public void run() {
      while (!board.hasConverged()) {
        for (int i = 0; i < board.getMaxX(); i++) {
          for (int j = 0; j < board.getMaxY(); j++) {
            board.setNewValue(i, j, computeValue(i, j));
          }
        }
        try {
          // 栅栏等待多个线程
          barrier.await();
        } catch (InterruptedException | BrokenBarrierException e) {
          return;
        }
      }
    }

    private int computeValue(int i, int j) {
      // TODO
      return -1;
    }
  }

  public void start() {
    for (int i = 0; i < workers.length; i++) {
      new Thread(workers[i]).start();
    }
    mainBoard.waitForConvergence();
  }

  // TODO
  public class Board {

    int count = 0;

    private void commitNewValues() {
      // TODO
      System.out.println("commitNewValues");
      count++;
    }

    private boolean hasConverged() {
      // TODO
      return count < 10;
    }

    private int getMaxX() {
      // TODO
      return 10;
    }

    private int getMaxY() {
      // TODO
      return 10;
    }

    private Board getSubBoard(int count, int i) {
      // TODO
      return new Board();
    }

    private void setNewValue(int i, int j, int value) {
      // TODO
      System.out.println("setNewValue");
    }

    private void waitForConvergence() {
      // TODO
      System.out.println("waitForConvergence");
    }
  }
}
