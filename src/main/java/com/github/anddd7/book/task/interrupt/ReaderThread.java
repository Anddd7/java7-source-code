package com.github.anddd7.book.task.interrupt;

import java.io.IOException;
import java.io.InputStream;
import java.net.Socket;

/**
 * 改写interrupt方法实现取消
 */
public class ReaderThread extends Thread {

  private final Socket socket;
  private final InputStream in;

  public ReaderThread(Socket socket) throws IOException {
    this.socket = socket;
    this.in = socket.getInputStream();
  }

  @Override
  public void interrupt() {
    try {
      // 被中断时直接关闭socket
      socket.close();
    } catch (IOException e) {
      e.printStackTrace();
    } finally {
      // 设置interrupt标志
      super.interrupt();
    }
  }

  @Override
  public void run() {
    try {
      byte[] buf = new byte[1024];
      while (true) {
        int count = in.read(buf);
        if (count < 0) {
          break;
        } else if (count > 0) {
          // do something
        }
      }
    } catch (IOException e) {
      e.printStackTrace();
    }
  }
}
