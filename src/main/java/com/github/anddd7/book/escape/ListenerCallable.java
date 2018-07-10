package com.github.anddd7.book.escape;

import java.util.concurrent.Callable;

public class ListenerCallable implements Callable<String> {

  private EventSource<EscapeEventListener> source;

    ListenerCallable(final EventSource<EscapeEventListener> source) {
    this.source = source;
  }

  @Override
  public String call() {
    try {
      for (EscapeEventListener listener : this.source.retrieveListeners()) {
        return listener.onEvent();
      }
    } catch (InterruptedException e) {
      e.printStackTrace();
    }
    return null;
  }
}

