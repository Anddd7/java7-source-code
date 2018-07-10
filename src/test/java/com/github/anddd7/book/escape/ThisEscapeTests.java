package com.github.anddd7.book.escape;

import com.github.anddd7.book.escape.EscapeEventListener;
import com.github.anddd7.book.escape.EventSource;
import com.github.anddd7.book.escape.ListenerCallable;
import com.github.anddd7.book.escape.ThisEscape;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import org.junit.Test;

public class ThisEscapeTests {

  @Test
  public void escape() throws ExecutionException, InterruptedException {
    final ExecutorService executor = Executors.newSingleThreadExecutor();
    EventSource<EscapeEventListener> source = new EventSource<>();

    while (true) {
      // 启动监听线程
      Callable callable = new ListenerCallable(source);
      Future future = executor.submit(callable);
      // 初始化问题类
      new ThisEscape(source);

      // 当listener中获取的ThisEscaped对象不完整(id/name未加载完)就会弹出
      if (future.get() == null) {
        System.out.println("'this' is escaped");
        break;
      }
    }
  }
}