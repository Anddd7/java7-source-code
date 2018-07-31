package com.github.anddd7.book.webserver;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * 单线程的服务器
 * - 每次接受一个socket连接, 然后进行操作(输入/输出)
 * - 执行完成后才能while接受下一个socket连接
 *
 * 执行过后会发现, 顺序执行时每个socket都需要等待I/O的准备, 如果时间过长会大大影响后续的链接
 */
public class SingleThreadWebServer {

  public static void main(String[] args) throws IOException {
    ServerSocket serverSocket = new ServerSocket(9999);
    while (true) {
      System.out.println("等待连接");
      Socket connection = serverSocket.accept();

      System.out.println(String.format("开启连接:%s", connection.getPort()));

      BufferedReader reader = new BufferedReader(
          new InputStreamReader(connection.getInputStream()));
      System.out.println(String.format("收到消息:%s", reader.readLine()));

      System.out.println(String.format("结束操作:%s", connection.getPort()));
    }
  }
}
