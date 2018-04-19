package github.eddy.java7core.util.collection;

import static github.eddy.Tool.timer;

import java.util.ArrayDeque;
import java.util.LinkedList;
import java.util.PriorityQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import org.junit.Test;

/**
 * @author and777
 * @date 2018/1/18
 */
public class TestQueue {

  /**
   * 获取长度2^k ,大于initialCapacity的最小值
   *
   * 假设当前值转化为二进制后为n位 ,那么满足条件的长度值就是 2^(n+1) ,例如 : 9 = 1001 ,长度应为 16 = 10000 ,则最大序列是1111
   * 因此我们只要获得当前最高位情况下的最大序列 ,+1 则是长度值
   *
   * 因为int最大32位 = 2^5 ,经过5次移位再或操作就能够获得当前位的最大值
   */
  @Test
  public void allocateElements() {
    //最高位是27
    int initialCapacity = Integer.valueOf("100000000000000000000000000", 2);
    System.out.println(printBinaryString(initialCapacity));

    //27 移位 26
    initialCapacity |= (initialCapacity >>> 1);
    System.out.println(printBinaryString(initialCapacity));
    //27-26 移位 25-24
    initialCapacity |= (initialCapacity >>> 2);
    System.out.println(printBinaryString(initialCapacity));
    //27-24 移位 23-20
    initialCapacity |= (initialCapacity >>> 4);
    System.out.println(printBinaryString(initialCapacity));
    //27-20 移位 19-12
    initialCapacity |= (initialCapacity >>> 8);
    System.out.println(printBinaryString(initialCapacity));
    //27-12 移位 11-0
    initialCapacity |= (initialCapacity >>> 16);
    System.out.println(printBinaryString(initialCapacity));

    //+1获取长度
    initialCapacity++;
    System.out.println(printBinaryString(initialCapacity));
  }

  /**
   * ArrayDeque 由数组和2个位置指针组成
   *
   * *- - - - - - - - - - - *
   * *    |             |   *
   * *   head          tail *
   */
  @Test
  public void arraDeque() {

    /**
     * FIFO : 使用offer/poll (offerLast/pollFirst) , 两个指针向右移动
     *
     * offer 将元素放到ele[tail] ,并移动尾部指针tail++ ;如果tail==head ,则表示队列已满
     * poll 将ele[head]的元素取出 ,并移动头部指针head++ ;如元素为null ,则表示队列为空
     *
     * 指针超界 : 如果队列大小为16 ,tail=15 head=14; 再加入元素时 ,tail=16 ,超过队列大小范围 ,但此时队列不满
     * -  tail = (tail+1) & (length-1) : 将tail的坐标重设到0 ,形成循环
     * -  tail = 10000 & 01111 = 00000 = 0
     */
    ArrayDeque fifo = new ArrayDeque();
    fifo.offer(1);
    fifo.offer(2);
    System.out.println(fifo.poll());
    fifo.offer(3);
    System.out.println(fifo.poll());
    System.out.println(fifo.poll());
    /**
     * FILO (stack):
     * 使用offerLast/pollLast : offer ele[tail] ; poll ele[tail]
     * 使用offerFirst/pollFirst : offer ele[head] ; poll ele[head]
     */
    ArrayDeque stack = new ArrayDeque();
    stack.offerLast(1);
    stack.offerLast(2);
    System.out.println(stack.pollLast());
    stack.offerLast(3);
    System.out.println(stack.pollLast());
    System.out.println(stack.pollLast());
  }


  @Test
  public void treeWithArray() {
    //int[] tree = new int[0];
    //000
    //001 010
    //011 100 101 110

    PriorityQueue priorityQueue = new PriorityQueue();
    priorityQueue.add(1);
    priorityQueue.add(2);
    priorityQueue.add(3);
    priorityQueue.add(4);
    priorityQueue.add(5);

    priorityQueue.remove(1);
    priorityQueue.remove(2);
  }

  private String printBinaryString(int i) {
    String s = Integer.toBinaryString(i);
    if (s.length() < 32) {
      String offset = "";
      for (int j = 0; j < 32 - s.length(); j++) {
        offset += "0";
      }
      return offset + s;
    }
    return s;
  }

  /**
   * 耗时:3865 | offer array
   * 耗时:5974 | offer linked
   * 耗时:13 | poll array
   * 耗时:106 | poll linked
   *
   * LinkedList使用经典的链表数据结构 ,同时实现了list/queue ,但效率上是低于ArrayList/Queue的
   * - Node结构耗费了更多的内存和操作
   * - add/get/remove 较多 ,考虑Array
   * - addAt/removeAt 较多 ,考虑Linked : 不用重排序列
   * - 需要List+Queue的时候 ,可以选择LinkedList
   */
  @Test
  public void arrayVSLinked() {
    final int maxLength = 10000000;

    ArrayDeque array = new ArrayDeque();
    LinkedList linked = new LinkedList();

    timer("start");
    //offer
    for (int i = 0; i < maxLength; i++) {
      array.offer(i);
    }
    timer("offer array");
    for (int i = 0; i < maxLength; i++) {
      linked.offer(i);
    }
    timer("offer linked");

    //poll
    for (int i = 0; i < maxLength; i++) {
      array.poll();
    }
    timer("poll array");
    for (int i = 0; i < maxLength; i++) {
      linked.poll();
    }
    timer("poll linked");
  }

  /**
   * 队列常用场景 : 生产者/消费者
   *
   * 多线程下会出现线程安全问题
   */
  @Test
  public void producerConsumer() throws InterruptedException {
    final ArrayDeque product = new ArrayDeque();//无限长度的队列

    final ExecutorService producers = Executors.newFixedThreadPool(5);
    ExecutorService consumer = Executors.newFixedThreadPool(5);

    for (int i = 0; i < 5; i++) {
      //每个生产者提供100,000个产品
      producers.submit(new Runnable() {
        @Override
        public void run() {
          for (int j = 0; j < 100000; j++) {
            product.offer(Thread.currentThread().getName() + "-" + j);
          }
        }
      });
    }
    producers.shutdown();
    while (!producers.awaitTermination(10, TimeUnit.MILLISECONDS)) {
    }
    System.out.println("共生产:" + product.size() + "个产品");

    //每个消费者消费50,000
    consumer.submit(new Runnable() {
      int innerNum = 0;

      @Override
      public void run() {
        while (innerNum < 50000) {
          Object obj = product.poll();
          if (obj != null) {
            innerNum++;
            System.out.println(Thread.currentThread().getName() + "消费了第" + innerNum + "个:" + obj);
          }
        }
      }
    });
    consumer.shutdown();

    int repeatTime = 5;
    while (!consumer.awaitTermination(1, TimeUnit.SECONDS) && repeatTime > 0) {
      repeatTime--;
    }
    System.out.println("还剩余:" + product.size() + "个产品");
  }
}
