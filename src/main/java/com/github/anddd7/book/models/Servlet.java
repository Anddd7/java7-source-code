package com.github.anddd7.book.models;

import java.util.Map;

public interface Servlet<T> {

  void service(Map<String, T> request, Map<String, T> response);
}
