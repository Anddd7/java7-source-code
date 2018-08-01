package com.github.anddd7.book.task.interrupt;

import java.io.IOException;
import java.net.Socket;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Callable;
import java.util.concurrent.FutureTask;
import java.util.concurrent.RunnableFuture;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class SocketUsingTask<T> implements CancellableTask<T> {

  private Socket socket;

  @Override
  public void cancel() {
    try {
      if (socket != null) {
        socket.close();
      }
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  @Override
  public RunnableFuture<T> newTask() {
    // 定制future, 使之使用我们的cancel方法
    return new FutureTask<T>(this) {
      @Override
      public boolean cancel(boolean mayInterruptIfRunning) {
        try {
          SocketUsingTask.this.cancel();
        } finally {
          return super.cancel(mayInterruptIfRunning);
        }
      }
    };
  }

  @Override
  public T call() throws Exception {
    return null;
  }
}

interface CancellableTask<T> extends Callable<T> {

  void cancel();

  RunnableFuture<T> newTask();
}

class CancellableExecutor extends ThreadPoolExecutor {

  public CancellableExecutor(int corePoolSize, int maximumPoolSize, long keepAliveTime,
      TimeUnit unit, BlockingQueue<Runnable> workQueue) {
    super(corePoolSize, maximumPoolSize, keepAliveTime, unit, workQueue);
  }

  @Override
  protected <T> RunnableFuture<T> newTaskFor(Callable<T> callable) {
    if (callable instanceof CancellableTask) {
      // 使用定制的工厂方法生成我们需要的Future
      return ((CancellableTask) callable).newTask();
    } else {
      return super.newTaskFor(callable);
    }
  }
}

