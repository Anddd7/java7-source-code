package com.github.anddd7.book.webserver;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

/**
 * Executor实现的使用线程池的服务器
 * - 可以重复利用线程
 * - Executor易于修改和扩展
 */
public class TaskExecutionServer {

  private static final int NTHREADS = 100;
  private static final Executor exec = Executors.newFixedThreadPool(NTHREADS);

  @SuppressWarnings("Duplicates")
  public static void main(String[] args) throws IOException {
    ServerSocket serverSocket = new ServerSocket(9999);
    while (true) {
      System.out.println("等待连接");
      final Socket connection = serverSocket.accept();

      System.out.println(String.format("开启连接:%s", connection.getPort()));
      Runnable task = new Runnable() {
        @Override
        public void run() {
          try {
            BufferedReader reader = new BufferedReader(
                new InputStreamReader(connection.getInputStream()));
            System.out.println(String.format("收到消息:%s", reader.readLine()));
          } catch (IOException e) {
            e.printStackTrace();
          }
        }
      };
      exec.execute(task);
      System.out.println(String.format("结束操作:%s", connection.getPort()));
    }
  }

  /**
   * 利用Executor接口快速扩展功能
   */
  public class ThreadPerTaskExecutor implements Executor {

    @Override
    public void execute(Runnable command) {
      new Thread(command).start();
    }
  }

  public class WithinThreadExecutor implements Executor {

    @Override
    public void execute(Runnable command) {
      command.run();
    }
  }
}
