package com.github.anddd7.book.webserver;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.net.InetSocketAddress;
import java.net.Socket;

public class SocketClient {

  public static void main(String[] args) throws IOException, InterruptedException {
    Socket socket = new Socket();
    socket.connect(new InetSocketAddress(9999));

    // 模拟网络延迟
    Thread.sleep(5 * 1000);
    BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
    writer.write("你好");
    writer.flush();
  }
}
