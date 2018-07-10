package com.github.anddd7.book.basic;

public class UnsafeStates {

  private String[] states = new String[]{"A", "B", "C"};

  /**
   * 此方法将内部可变状态发布出去, 且可以通过引用直接修改内部变量: 逸出 作用域
   */
  public String[] getStates() {
    return states;
  }
}
