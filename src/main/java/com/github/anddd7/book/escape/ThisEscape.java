package com.github.anddd7.book.escape;

/**
 * this指针逸出
 */
public class ThisEscape {

  private final int id;
  private final String name;

  ThisEscape(final EventSource<EscapeEventListener> source) {
    source.registerListener(new EscapeEventListener() {
      @Override
      public String onEvent() {
        return ThisEscape.this.toString();
      }
    });
    // 在初始化未完成时就对外发布了当前类的this指针: EscapeEventListener里面握有ThisEscape的指针
    // 当初始化完成前, EventListener就被其他线程所执行, 就会出现错误
    this.id = 1;
    this.name = "this escape";
  }

  @Override
  public String toString() {
    return id != 0 && name != null ? String.format("%s: %s", id, name) : null;
  }
}