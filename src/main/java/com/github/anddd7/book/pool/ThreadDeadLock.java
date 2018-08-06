package com.github.anddd7.book.pool;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class ThreadDeadLock {

  private final ExecutorService exec = Executors.newSingleThreadExecutor();

  class RenderPageTask implements Callable<String> {

    @Override
    public String call() throws Exception {
      Future<String> header, footer;
      header = exec.submit(new LoadFileTask("header.html"));
      footer = exec.submit(new LoadFileTask("footer.html"));
      String page = renderBody();
      // 此任务需要等待header和footer任务的执行, 但单线程池已被当前任务占用, 无法执行header和footer, 产生死锁
      return header.get() + page + footer.get();
    }

    private String renderBody() {
      return null;
    }

    private class LoadFileTask implements Callable<String> {

      private final String name;

      public LoadFileTask(String name) {
        this.name = name;
      }

      @Override
      public String call() {
        return name;
      }
    }
  }
}
