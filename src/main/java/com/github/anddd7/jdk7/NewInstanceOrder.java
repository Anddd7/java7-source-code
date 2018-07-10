package com.github.anddd7.jdk7;

public class NewInstanceOrder {

  /**
   * 0.加载class后便执行
   */
  private static InnerClass static_field = new InnerClass("static_field");

  /**
   * 2.属性优先执行
   */
  private InnerClass filed = new InnerClass("filed");
  /**
   * 3.在执行construct时才初始化
   */
  private InnerClass construct_field;

  public NewInstanceOrder(InnerClass filed) {
    this.construct_field = new InnerClass("construct_field");
  }

  public static class InnerClass {

    InnerClass(String name) {
      System.out.println(String.format("InnerClass-%s", name));
    }
  }

  public static void main(String[] args) {
    new NewInstanceOrder(new InnerClass("outer"));
  }
}
