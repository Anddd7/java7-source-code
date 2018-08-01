package com.github.anddd7.book.task;

import java.net.URL;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * 任务停止时暂存未执行的任务, 以便下次执行
 */
public abstract class WebCrawler {

  private volatile TrackingExecutor exec;
  private final Set<URL> urlsToCrawl = new HashSet<>();

  public synchronized void start() {
    exec = new TrackingExecutor(Executors.newCachedThreadPool());
    for (URL url : urlsToCrawl) {
      submitCrawlTask(url);
    }
    urlsToCrawl.clear();
  }

  public synchronized void stop() throws InterruptedException {
    try {
      saveUncrawled(exec.shutdownNow());
      if (exec.awaitTermination(10, TimeUnit.SECONDS)) {
        saveUncrawled(exec.getCancelledTasks());
      }
    } finally {
      exec = null;
    }
  }

  private void saveUncrawled(List<Runnable> tasks) {
    for (Runnable task : tasks) {
      urlsToCrawl.add(((CrawlTask) task).getUrl());
    }
  }

  protected abstract void submitCrawlTask(URL url);

  private class CrawlTask implements Runnable {

    private final URL url;

    public CrawlTask(URL url) {
      this.url = url;
    }

    @Override
    public void run() {
      for (URL link : processPage(url)) {
        if (Thread.currentThread().isInterrupted()) {
          return;
        }
        submitCrawlTask(link);
      }
    }

    public URL getUrl() {
      return url;
    }
  }

  protected abstract List<URL> processPage(URL url);
}
