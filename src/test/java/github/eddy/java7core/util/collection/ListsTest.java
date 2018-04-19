package github.eddy.java7core.util.collection;

import static github.eddy.Tool.timer;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Random;
import java.util.Vector;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import org.junit.Before;
import org.junit.Test;

/**
 * @author and777
 * @date 2018/1/18
 */
public class ListsTest {


  Random r;

  @Before
  public void before() {
    r = new Random(System.currentTimeMillis());
  }

  /**
   * ArrayList 和 LinkedList 的时间性能比较
   *
   * 耗时:33 | add array
   * 耗时:121 | add linked
   * 耗时:35 | add vector
   *
   * 耗时:0 | get array
   * 耗时:156 | get linked
   * 耗时:0 | get vector
   *
   * 耗时:206 | contains array
   * 耗时:353 | contains linked
   * 耗时:115 | contains vector
   *
   * 耗时:91 | removeFrist array
   * 耗时:0 | removeFrist linked
   * 耗时:83 | removeFrist vector
   *
   * 耗时:54 | remove array
   * 耗时:183 | remove linked
   * 耗时:56 | remove vector
   *
   * - ArrayList 因为基于数组 ,在涉及随机访问的场景下(遍历/指定访问index) ,速度都优于LinkedList ,唯一缺点就在于remove某一元素时 ,需要重排后续元素 :
   * 因此在队列/栈的实现上 ,LinkedList更适合
   * - LinkedList 头尾访问飞速 ,适合直接获取头/尾元素的情况 (但ArrayQueue优于LinkedList {@link TestQueue#arrayVSLinked()})
   * - Vector 在单线程下和ArrayList没有区别 ,多线程时函数执行时间会变成 ,但是不会出现线程干扰问题
   */
  @Test
  public void arrayVSLinked() {
    int maxLength = 1000000;
    int loopCount = 100;

    ArrayList array = new ArrayList();
    LinkedList linked = new LinkedList();
    Vector vector = new Vector();

    timer("start");
    //add
    for (int i = 0; i < maxLength; i++) {
      array.add(i);
    }
    timer("add array");
    for (int i = 0; i < maxLength; i++) {
      linked.add(i);
    }
    timer("add linked");
    for (int i = 0; i < maxLength; i++) {
      vector.add(i);
    }
    timer("add vector");

    //get
    for (int i = 0; i < loopCount; i++) {
      array.get(Math.abs(r.nextInt()) % maxLength);
    }
    timer("get array");
    for (int i = 0; i < loopCount; i++) {
      linked.get(Math.abs(r.nextInt()) % maxLength);
    }
    timer("get linked");
    for (int i = 0; i < loopCount; i++) {
      vector.get(i);
    }
    timer("get vector");

    //contains
    for (int i = 0; i < loopCount; i++) {
      array.contains(Math.abs(r.nextInt()) % maxLength);
    }
    timer("contains array");
    for (int i = 0; i < loopCount; i++) {
      linked.contains(Math.abs(r.nextInt()) % maxLength);
    }
    timer("contains linked");
    for (int i = 0; i < loopCount; i++) {
      vector.contains(Math.abs(r.nextInt()) % maxLength);
    }
    timer("contains vector");

    //remove first
    for (int i = 0; i < loopCount; i++) {
      array.remove(0);
    }
    timer("removeFrist array");
    for (int i = 0; i < loopCount; i++) {
      linked.remove();
    }
    timer("removeFrist linked");
    for (int i = 0; i < loopCount; i++) {
      vector.remove(0);
    }
    timer("removeFrist vector");

    //remove
    for (int i = 0; i < loopCount; i++) {
      array.remove(Math.abs(r.nextInt()) % maxLength);
    }
    timer("remove array");
    for (int i = 0; i < loopCount; i++) {
      linked.remove(Math.abs(r.nextInt()) % maxLength);
    }
    timer("remove linked");
    for (int i = 0; i < loopCount; i++) {
      vector.remove(Math.abs(r.nextInt()) % maxLength);
    }
    timer("remove vector");
  }


  /**
   * 线程安全问题
   *
   * 插入1000000条记录 ,各list的执行结果
   * array:9586876
   * linked:9063838
   * vector:10000000
   * 耗时:10756 | 执行结束
   *
   * linked 过程最复杂 ,受影响也最大
   */
  @Test
  public void asyncTest() throws InterruptedException {
    final int maxLength = 10000000;

    final ArrayList array = new ArrayList();
    final LinkedList linked = new LinkedList();
    final Vector vector = new Vector();

    timer("start");

    ExecutorService executorService = Executors.newFixedThreadPool(5);
    //5个线程向array中插入元素
    for (int i = 0; i < 5; i++) {
      executorService.submit(new Runnable() {
        @Override
        public void run() {
          for (int j = 0; j < maxLength / 5; j++) {
            array.add(j);
          }
        }
      });
      executorService.submit(new Runnable() {
        @Override
        public void run() {
          for (int j = 0; j < maxLength / 5; j++) {
            linked.add(j);
          }
        }
      });
      executorService.submit(new Runnable() {
        @Override
        public void run() {
          for (int j = 0; j < maxLength / 5; j++) {
            vector.add(j);
          }
        }
      });
    }
    executorService.shutdown();
    while (!executorService.awaitTermination(10, TimeUnit.MILLISECONDS)) {
    }

    System.out.println("array:" + array.size());
    System.out.println("linked:" + linked.size());
    System.out.println("vector:" + vector.size());
    timer("执行结束");
  }

  /**
   * array:10000000
   * linked:10000000
   * vector:10000000
   * 耗时:11258 | 执行结束
   */
  @Test
  public void syncTest() throws InterruptedException {
    final int maxLength = 10000000;

    final ArrayList array = new ArrayList();
    final LinkedList linked = new LinkedList();
    final Vector vector = new Vector();

    timer("start");

    ExecutorService executorService = Executors.newFixedThreadPool(5);
    //5个线程向array中插入元素
    for (int i = 0; i < 5; i++) {
      executorService.submit(new Runnable() {
        @Override
        public void run() {
          for (int j = 0; j < maxLength / 5; j++) {
            synchronized (array) {
              array.add(j);
            }
          }
        }
      });
      executorService.submit(new Runnable() {
        @Override
        public void run() {
          for (int j = 0; j < maxLength / 5; j++) {
            synchronized (linked) {
              linked.add(j);
            }
          }
        }
      });
      executorService.submit(new Runnable() {
        @Override
        public void run() {
          for (int j = 0; j < maxLength / 5; j++) {
            vector.add(j);
          }
        }
      });
    }
    executorService.shutdown();

    while (!executorService.awaitTermination(10, TimeUnit.MILLISECONDS)) {
    }

    System.out.println("array:" + array.size());
    System.out.println("linked:" + linked.size());
    System.out.println("vector:" + vector.size());
    timer("执行结束");
  }
}
