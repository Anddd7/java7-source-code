package com.github.anddd7.jdk7.util;

import java.util.Deque;
import java.util.Stack;
import java.util.Vector;

/**
 * @author Anddd7
 *
 * @see Stack
 *
 * 基于 Vector 实现的后入先出 LIFO 栈
 * @deprecated vector 结构陈旧 ,推荐使用 {@link Deque}
 */
@Deprecated
public class StackTests {

  /**
   * {@link Stack#push(Object)}
   * - 压栈
   *
   * synchronized {@link Stack#pop()}
   * - 出栈
   *
   * synchronized {@link Stack#peek()}
   * - 获取栈顶
   *
   * synchronized {@link Stack#search(Object)}
   * - 从栈顶开始搜索元素
   */
  public StackTests() {
  }
}
