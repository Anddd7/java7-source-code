package com.github.anddd7.book.escape;

import java.util.ArrayList;
import java.util.List;

class EventSource<T> {

  private T eventListener;

  synchronized void registerListener(final T eventListener) {
    this.eventListener = eventListener;
    this.notifyAll();
  }

  synchronized List<T> retrieveListeners() throws InterruptedException {
    if (eventListener == null) {
      this.wait();
    }
    List<T> list = new ArrayList<>(1);
    list.add(eventListener);
    return list;
  }
}

