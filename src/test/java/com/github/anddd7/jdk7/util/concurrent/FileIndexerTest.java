package com.github.anddd7.jdk7.util.concurrent;

import com.github.anddd7.ThreadHelper;
import java.io.File;
import java.io.FileFilter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ThreadFactory;
import org.junit.Test;

public class FileIndexerTest {

  @Test
  public void indexFiles() throws InterruptedException {
    ConcurrentMap<String, String> table = new ConcurrentHashMap<>();
    BlockingQueue<File> queue = new ArrayBlockingQueue<>(10);
    FileFilter filter = new FileFilter() {
      @Override
      public boolean accept(File pathname) {
        return true;
      }
    };
    List<File> files = Collections.singletonList(new File("src"));
    List<Runnable> crawlers = new ArrayList<>(files.size());
    for (File root : files) {
      crawlers.add(new FileCrawler(queue, filter, root));
    }

    ExecutorService crawlerService = ThreadHelper.executeTasks(files.size(), crawlers);
    ExecutorService indexerService = ThreadHelper.executeTask(
        10,
        10,
        new Indexer(table, queue)
    );

    ThreadHelper.waitFor(crawlerService);
    ThreadHelper.waitFor(indexerService);
  }
}