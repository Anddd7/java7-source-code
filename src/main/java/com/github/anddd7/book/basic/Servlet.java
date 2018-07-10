package com.github.anddd7.book.basic;

import java.util.Map;

public interface Servlet<T> {

  void service(final Map<String, T> request, final Map<String, T> response);
}
