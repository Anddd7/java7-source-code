package com.github.anddd7.jdk7.util.concurrent;

import java.io.File;
import java.io.FileFilter;
import java.util.concurrent.BlockingQueue;

public class FileCrawler implements Runnable {

  private final BlockingQueue<File> fileQueue;
  private final FileFilter fileFilter;
  private final File root;

  public FileCrawler(BlockingQueue<File> fileQueue, FileFilter fileFilter, File root) {
    this.fileQueue = fileQueue;
    this.fileFilter = fileFilter;
    this.root = root;
  }

  @Override
  public void run() {
    try {
      crawl(root);
    } catch (InterruptedException e) {
      e.printStackTrace();
    }
  }

  private void crawl(File root) throws InterruptedException {
    File[] entries = root.listFiles(fileFilter);
    if (entries == null) {
      return;
    }
    for (File entry : entries) {
      if (entry.isDirectory()) {
        crawl(entry);
      } else {
        fileQueue.put(entry);
        System.out.println(
            String.format("%s:抓取到文件-%s", Thread.currentThread().getName(), entry.getName()));
      }
    }
  }
}
