package com.github.anddd7.jdk7.util.concurrent;

import com.github.anddd7.book.annotation.NotThreadSafe;
import com.github.anddd7.book.annotation.ThreadSafe;
import java.util.Collections;
import java.util.ConcurrentModificationException;
import java.util.Random;
import java.util.Vector;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import org.junit.Test;

/**
 * 同步容器类: 将内部状态封装起来(容器对象), 对公共方法用synchronized锁进行同步
 *
 * @see Vector , Hashtable 早期JDK的结构, 不推荐使用
 * @see Collections#synchronizedMap,Collections#synchronizedCollection 基于装饰器模式扩展的同步容器
 */
public class SynchronizedContainersTests {

  /**
   * 同步容器本身是线程安全的, 但是客户端程序在使用时也需要注意是否打破了线程安全的规则
   * 下面2个方法:
   * -  getLast单独执行没问题: size/get方法不会修改内部状态(除非修改对象)
   * -  deleteLast出现问题: 执行remove后, 内部状态变化且违反了限制条件(数组边界)
   * 即: size()-remove()-get() 穿插执行导致问题
   */
  @NotThreadSafe
  public static Object getLastUnsafe(Vector vector) {
    int lastIndex = vector.size() - 1;
    return vector.get(lastIndex);
  }

  @NotThreadSafe
  public static Object deleteLastUnsafe(Vector vector) {
    int lastIndex = vector.size() - 1;
    // remove会影响size()的结果
    return vector.remove(lastIndex);
  }

  interface Function {

    void accept(Object o);

  }

  /**
   * 同上, 在迭代途中vector状态变化, 导致get操作失败
   *
   * @see ConcurrentModificationException 同步异常
   */
  @NotThreadSafe
  public static void forEachUnsafe(Vector vector, Function function) {
    for (int i = 0; i < vector.size(); i++) {
      function.accept(vector.get(i));
    }
  }

  /**
   * 对vector对象进行加锁, 保证 size()-get() 的有序性 (中间不会插入影响size的操作)
   */
  @ThreadSafe
  public static Object getLastSafe(Vector vector) {
    synchronized (vector) {
      int lastIndex = vector.size() - 1;
      return vector.get(lastIndex);
    }
  }

  @ThreadSafe
  public static Object deleteLastSafe(Vector vector) {
    synchronized (vector) {
      int lastIndex = vector.size() - 1;
      // remove会影响size()的结果
      return vector.remove(lastIndex);
    }
  }

  @ThreadSafe
  public static void forEachSafe(Vector vector, Function function) {
    synchronized (vector) {
      for (int i = 0; i < vector.size(); i++) {
        function.accept(vector.get(i));
      }
    }
  }

  Vector<Integer> buildVector(int number) {
    Vector<Integer> vector = new Vector<>();
    vector.add(number);
    vector.add(number);
    vector.add(number);
    return vector;
  }

  @Test(expected = ArrayIndexOutOfBoundsException.class)
  public void deleteLast_throwArrayIndexOutOfBounds() {
    final ExecutorService executor = Executors.newSingleThreadExecutor();
    final Random random = new Random();
    while (true) {
      final Vector<Integer> vector = buildVector(random.nextInt());
      executor.submit(new Runnable() {
        @Override
        public void run() {
          deleteLastUnsafe(vector);
        }
      });
      getLastSafe(vector);
    }
  }
}
