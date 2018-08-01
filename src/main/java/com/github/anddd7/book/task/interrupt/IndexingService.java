package com.github.anddd7.book.task.interrupt;

import java.io.File;
import java.io.FileFilter;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

/**
 * 毒丸(poison pill): 针对生产/消费者, 以某一个特殊对象作为停止服务的标识放在队列最后一个
 */
public class IndexingService {

  private static final File POISON = new File("");

  private final IndexThread consumer = new IndexThread();
  private final CrawlerThread producer = new CrawlerThread();
  private final BlockingQueue<File> queue;
  private final FileFilter fileFilter;
  private final File root;

  private final Map<String, File> fileMap = new HashMap<>();

  public IndexingService(FileFilter fileFilter, File root) {
    this.queue = new ArrayBlockingQueue<>(100);
    this.fileFilter = fileFilter;
    this.root = root;
  }

  private class IndexThread extends Thread {

    public IndexThread() {
      super("IndexThread");
    }

    @Override
    public void run() {
      try {
        while (true) {
          File file = queue.take();
          if (file == POISON) {
            print("收到停止信号, 已处理完当前所有文件");
            break;
          } else {
            indexFile(file);
          }
        }
      } catch (InterruptedException e) {
        print("异常中断");
      }
    }

    private void indexFile(File file) {
      fileMap.put(file.getPath(), file);
      print(String.format("已索引该文件:%s", file.getName()));
    }
  }

  private class CrawlerThread extends Thread {

    public CrawlerThread() {
      super("CrawlerThread");
    }

    @Override
    public void run() {
      try {
        crawl(root);
        print("文件抓取完成");
      } catch (InterruptedException e) {
        // 异常或被中断
        print("收到中断信号");
      } finally {
        while (true) {
          try {
            queue.put(POISON);
            break;
          } catch (InterruptedException e) {
            // 存放毒丸信号时失败, 需要重新尝试
            print("等待传递中断(毒丸)信号给消费者");
          }
        }
        print("抓取线程已停止");
      }
    }

    private void crawl(File root) throws InterruptedException {
      for (File file : root.listFiles()) {
        if (file.isDirectory()) {
          crawl(file);
        } else if (fileFilter.accept(file)) {
          queue.put(file);
          print(String.format("抓取文件:%s", file.getName()));
        }
      }
    }
  }

  public void start() {
    print("扫描文件开始");
    consumer.start();
    producer.start();
  }

  public void stop() {
    // 中断生产者, 由生产者发出停止讯号
    producer.interrupt();
    print("用户停止任务");
  }

  public void await() throws InterruptedException {
    // 等待消费者结束(消费者会在生产者结束并消费完所有内容后停止)
    consumer.join();
    print("程序退出");
  }

  public Map<String, File> getFileMap() {
    return Collections.unmodifiableMap(fileMap);
  }

  public static void main(String[] args) throws InterruptedException {
    FileFilter filter = new FileFilter() {
      @Override
      public boolean accept(File file) {
        return file.getName().endsWith(".java");
      }
    };
    IndexingService indexingService = new IndexingService(filter, new File("src"));
    indexingService.start();

//    Thread.sleep(10); // 用户取消
    Thread.sleep(5 * 1000); // 处理完成

    indexingService.stop();
    indexingService.await();
    System.out.println("索引量:" + indexingService.getFileMap().size());
  }

  private void print(String msg) {
    System.out.println(String.format("%s - %s", Thread.currentThread().getName(), msg));
  }
}
