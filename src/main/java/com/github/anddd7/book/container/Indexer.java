package com.github.anddd7.book.container;

import java.io.File;
import java.util.UUID;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentMap;

public class Indexer implements Runnable {

  private final ConcurrentMap<String, String> table;
  private final BlockingQueue<File> fileQueue;

  public Indexer(ConcurrentMap<String, String> table, BlockingQueue<File> fileQueue) {
    this.table = table;
    this.fileQueue = fileQueue;
  }

  @Override
  public void run() {
    while (true) {
      try {
        File file = fileQueue.take();
        table.putIfAbsent(file.getName(), UUID.randomUUID().toString());
        System.out.println(
            String.format("%s:处理了文件-%s", Thread.currentThread().getName(), file.getName()));
      } catch (InterruptedException e) {
        e.printStackTrace();
      }
    }
  }
}
