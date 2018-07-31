package com.github.anddd7.book.task.server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * 多线程的服务器
 * - 主线程只阻塞等待新的连接
 * - 每一个连接分配一个新的线程处理余下的任务
 *
 * 原本串行时需要:
 * 等A连接 - 等A的I/O - 执行A任务 - 结束循环 - 等B连接 - 等B的I/O - 执行B任务 - 结束循环
 *
 * 现在并行:
 * 等A连接 - 开启线程A - 结束循环 - 等B连接 - 开启线程B -结束循环
 * 等A的I/O - 执行A任务
 * 等B的I/O - 执行B任务
 *
 * - 主线程更快的接受新的连接, 提高响应
 * - I/O时当前线程阻塞, 但其他线程可以继续进行; 多处理器并行执行
 * !!! 但是无限制的创建线程会占用大量内存, 提高上下文切换的代价
 */
public class ThreadPerTaskWebServer {

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
      new Thread(task).start();
      System.out.println(String.format("结束操作:%s", connection.getPort()));
    }
  }
}
