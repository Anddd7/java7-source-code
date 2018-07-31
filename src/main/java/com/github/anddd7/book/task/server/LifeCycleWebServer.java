package com.github.anddd7.book.task.server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.RejectedExecutionException;

public class LifeCycleWebServer {

  private final ExecutorService exec;
  private final ServerSocket socket;

  public LifeCycleWebServer(int port) throws IOException {
    this.exec = Executors.newFixedThreadPool(100);
    this.socket = new ServerSocket(port);
  }

  public void start() {
    while (!exec.isShutdown()) {
      try {
        final Socket connection = socket.accept();
        exec.execute(new Runnable() {
          @Override
          public void run() {
            handleRequest(connection);
          }
        });
      } catch (RejectedExecutionException e) {
        if (!exec.isShutdown()) {
          System.out.println("task submission rejected: server is stopped");
        }
      } catch (IOException e) {
        System.out.println("connect failed");
      }
    }
  }

  private void handleRequest(Socket connection) {
    // dispatchRequest
  }
  public void stop() {
    exec.shutdown();
  }

}
