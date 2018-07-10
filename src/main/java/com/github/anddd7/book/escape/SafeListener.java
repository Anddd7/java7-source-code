package com.github.anddd7.book.escape;

/**
 * @see ThisEscape
 */
public class SafeListener {

  /**
   * 包含this指针的对象需要在初始化完成后才能发布出去
   */
  private final EscapeEventListener eventListener;

  private final int id;
  private final String name;

  private SafeListener() {
    this.eventListener = new EscapeEventListener() {
      @Override
      public String onEvent() {
        return SafeListener.this.toString();
      }
    };
    this.id = 1;
    this.name = "this escape";
  }

  /**
   * 使用工厂方法, 保证初始化完成后才会加入到EventSource触发下一步, 最后再返回this指针
   */
  public static SafeListener newInstance(final EventSource<EscapeEventListener> source) {
    SafeListener safeListener = new SafeListener();
    source.registerListener(safeListener.eventListener);
    return safeListener;
  }
}
