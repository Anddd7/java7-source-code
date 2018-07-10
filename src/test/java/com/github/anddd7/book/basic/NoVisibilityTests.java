package com.github.anddd7.book.basic;


import java.util.Random;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import org.junit.Test;

public class NoVisibilityTests {

  /**
   * 等待一会儿就可以看到错误信息, reader读取的number值为0(但ready=true), 出现了可见性问题
   */
  @Test
  public void go() throws ExecutionException, InterruptedException {
    final ExecutorService executor = Executors.newSingleThreadExecutor();
    final Random random = new Random();
    while (true) {
      int readerResult = new NoVisibility(executor, random).go();
      if (readerResult != 0) {
        System.out
            .println(String.format("reader number is not same with writer: %s", readerResult));
        break;
      }
    }
  }
}