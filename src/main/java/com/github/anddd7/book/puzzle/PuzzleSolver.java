package com.github.anddd7.book.puzzle;

import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.atomic.AtomicInteger;

public class PuzzleSolver<P, M> extends ConcurrentPuzzleSolver<P, M> {

  private final AtomicInteger taskCount = new AtomicInteger(0);

  public PuzzleSolver(Puzzle<P, M> puzzle, ConcurrentMap<P, Boolean> seen, ExecutorService exec) {
    super(puzzle, seen, exec);
  }

  class CountingSolverTask extends SolverTask {

    public CountingSolverTask(P pos, M m, Node<P, M> n) {
      super(pos, m, n);
      taskCount.incrementAndGet();
    }

    @Override
    public void run() {
      try {
        super.run();
      } finally {
        // 所有待执行任务都执行完了还未找到答案, 直接设置value
        if (taskCount.decrementAndGet() == 0) {
          solution.setValue(null);
        }
      }
    }
  }
}
