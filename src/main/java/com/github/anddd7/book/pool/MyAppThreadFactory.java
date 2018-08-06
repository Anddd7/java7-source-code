package com.github.anddd7.book.pool;


import java.util.concurrent.ThreadFactory;

/**
 * 自定义线程工厂类, 对线程池中的线程进行配置
 */
public class MyAppThreadFactory implements ThreadFactory {

  private final String poolName;

  public MyAppThreadFactory(String poolName) {
    this.poolName = poolName;
  }

  @Override
  public Thread newThread(Runnable r) {
    // 自定义的Thread类可以包含更多的数据和信息
    return new MyAppThread(r, poolName);
  }
}
