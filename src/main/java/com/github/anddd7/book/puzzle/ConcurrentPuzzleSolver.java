package com.github.anddd7.book.puzzle;

import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.ExecutorService;

public class ConcurrentPuzzleSolver<P, M> {

  private final Puzzle<P, M> puzzle;
  private final ConcurrentMap<P, Boolean> seen;
  private final ExecutorService exec;
  /**
   * 使用闭锁使发起线程阻塞, 等待工作线程完成工作
   */
  final ValueLatch<Node<P, M>> solution = new ValueLatch<>();

  public ConcurrentPuzzleSolver(Puzzle<P, M> puzzle,
      ConcurrentMap<P, Boolean> seen, ExecutorService exec) {
    this.puzzle = puzzle;
    this.seen = seen;
    this.exec = exec;
  }

  public List<M> solve() throws InterruptedException {
    P pos = puzzle.initialPosition();
    try {
      exec.execute(newTask(pos, null, null));
      // 阻塞发起线程, 直到得出答案
      Node<P, M> solvedNode = solution.getValue();
      return solvedNode == null ? null : solvedNode.asMoveList();
    } finally {
      exec.shutdown();
    }
  }

  private Runnable newTask(P pos, M m, Node<P, M> n) {
    return new SolverTask(pos, m, n);
  }

  static class Node<P, M> {

    final P pos;
    final M move;
    final Node<P, M> prev;

    public Node(P pos, M move, Node<P, M> prev) {
      this.pos = pos;
      this.move = move;
      this.prev = prev;
    }

    List<M> asMoveList() {
      List<M> solution = new LinkedList<>();
      for (Node<P, M> n = this; n.move != null; n = n.prev) {
        solution.add(0, n.move);
      }
      return solution;
    }
  }

  class SolverTask extends Node<P, M> implements Runnable {

    public SolverTask(P pos, M m, Node<P, M> n) {
      super(pos, m, n);
    }

    @Override
    public void run() {
      if (solution.isSet() || seen.putIfAbsent(pos, true) != null) {
        // 已有答案, 或当前节点已检查过
        return;
      }
      if (puzzle.isGoal(pos)) {
        // 当前节点是一个可行的通路
        solution.setValue(this);
      } else {
        // 以node出发, 搜索下一个节点是否能到达目标节点
        for (M move : puzzle.legalMoves(pos)) {
          exec.execute(newTask(puzzle.move(pos, move), move, this));
        }
      }
    }
  }
}
