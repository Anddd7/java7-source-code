package github.eddy.java7core.util;

import java.util.Deque;
import java.util.LinkedList;
import java.util.List;
import java.util.NoSuchElementException;
import org.junit.Assert;
import org.junit.Test;

/**
 * @author Anddd7
 * @see LinkedList
 * 基于链表实现的 List 和 Deque
 */
public class LinkedListTests<E> {

  protected transient int modCount = 0;
  transient Node<E> first;
  transient Node<E> last;
  transient int size = 0;

  /**
   * {@link LinkedList#node(int)}
   * - 根据 index 选择近的一端开始查询
   */
  public LinkedListTests() {
  }

  /**
   * {@link LinkedList#linkBefore(Object, LinkedList.Node)}
   * - 将 e 插入到目标节点前
   *
   * 因为 LinkedList 是双向链表 ,每一次 link 动作要对相关的2个节点进行操作 ,一个 prev 一个 next
   * ,a = b = c    |                     : 插入 d 到 b 之前
   * ,a = b = c    | a <- d -> b = c     : 1.创建节点 d
   * ,a <- b = c   | a <- d = b = c      : 2.修改 b.prev=d
   * ,             | a = d = b = c       : 3.2.修改 a.next=d
   */
  void linkBefore(E e, Node<E> succ) {
    // 取前置节点
    final Node<E> pred = succ.prev;
    // 1.创建节点并关联到 前置节点 和 目标节点
    final Node<E> newNode = new Node<>(pred, e, succ);
    // 2.修改后置节点的关联关系
    succ.prev = newNode;
    if (pred == null) {
      // 3.1 如果前置节点为空 ,表示新节点是最前面一个: first 节点
      first = newNode;
    } else {
      // 3.2 如果前置节点不为空 ,修改前置节点的关联关系
      pred.next = newNode;
    }
    size++;
    modCount++;
  }

  /**
   * {@link LinkedList#linkFirst(Object)}
   * - 相当于是 linkBefore(e,first)
   * - 因为 first.prev=null ,因此会少一次节点指针链接 ,多一次 first 指针链接
   *
   * {@link LinkedList#linkLast(Object)}
   * - linkFirst 倒过来
   */
  private void linkFirst(E e) {
    // 前置节点= null ,后置节点= first
    final Node<E> f = first;
    final Node<E> newNode = new Node<>(null, e, f);
    // 新节点成为链表头
    first = newNode;
    if (f == null) {
      // 没有后置节点 ,新节点是最后一个
      last = newNode;
    } else {
      // 有后置节点 ,修改后置节点的关联关系
      f.prev = newNode;
    }
    size++;
    modCount++;
  }

  /**
   * {@link LinkedList#unlink(LinkedList.Node)}
   * - 从链表中删除节点
   */
  E unlink(Node<E> x) {
    // 暂存数据
    final E element = x.item;
    final Node<E> next = x.next;
    final Node<E> prev = x.prev;

    if (prev == null) {
      // 前置节点为空 ,当前节点是链表头 ,重置链表头到后置节点上
      first = next;
    } else {
      // 前置节点不为空 ,修改前置节点的链接
      prev.next = next;
      x.prev = null;
    }

    // 同上
    if (next == null) {
      last = prev;
    } else {
      next.prev = prev;
      x.next = null;
    }

    x.item = null;
    size--;
    modCount++;
    return element;
  }

  /**
   * {@link LinkedList#unlinkFirst(LinkedList.Node)}
   * - 删除链表头
   *
   * {@link LinkedList#unlinkLast(LinkedList.Node)}
   * - 删除链表尾
   */
  private E unlinkFirst(Node<E> f) {
    // 暂存元素
    final E element = f.item;
    final Node<E> next = f.next;
    // 清理节点 f 的关系
    f.item = null;
    f.next = null;
    // reset first
    first = next;

    if (next == null) {
      // 如果后置节点为空 ,则已无可用节点 ,清理 last
      last = null;
    } else {
      // 如果后置节点不为空
      // 后置链已经是链头 ,后置链.prev =null
      next.prev = null;
    }

    size--;
    modCount++;
    return element;
  }

  /**
   * ---------- 实现 List 方法 ----------------
   * 基于 size 和 node 方法 ,遍历元素直到指定位置
   * 通过 link/unlink 操作增删改元素节点
   * 超界会抛出异常
   * -----------------------------------------
   */
  @Test
  public void useLinkedAsList_ShouldThrowExceptionWhenIndexOutOfBound() {
    List<String> linkedList = new LinkedList<>();
    linkedList.add("a");
    Assert.assertEquals(1, linkedList.size());

    linkedList.add(0, "b");
    Assert.assertEquals(2, linkedList.size());
    Assert.assertEquals("b", linkedList.get(0));

    try {
      linkedList.add(3, "c");
    } catch (Exception e) {
      Assert.assertTrue(e instanceof IndexOutOfBoundsException);
    }

    linkedList.remove("a");
    Assert.assertEquals(1, linkedList.size());

    linkedList.remove(0);
    Assert.assertEquals(0, linkedList.size());

    try {
      linkedList.remove(0);
    } catch (Exception e) {
      Assert.assertTrue(e instanceof IndexOutOfBoundsException);
    }
  }

  /**
   * ---------- 实现 Deque 方法 ----------
   * 通过 first/last 指针对队列头尾进行操作
   * 超界不会抛出异常
   * ------------------------------------
   *
   * ---------- 实现 Stack 方法 ----------
   * 同 Deque ,在无元素时抛出异常
   * ------------------------------------
   *
   * 队列大多数用于多线程环境 ,因此队列是可能为空和为满的 ,所以会避免可能的超界异常 -> 避免使用collection的一些方法
   */
  @Test
  public void useLinkedAsDeque_ShouldReturnNullWhenIndexOutOfBound() {
    Deque<String> linkedDeque = new LinkedList<>();

    //FIFO
    linkedDeque.offer("a");
    linkedDeque.offer("b");
    Assert.assertEquals("a", linkedDeque.poll());
    Assert.assertEquals("b", linkedDeque.poll());
    Assert.assertNull(linkedDeque.poll());

    //FILO
    linkedDeque.offer("a");
    linkedDeque.offer("b");
    Assert.assertEquals("b", linkedDeque.pollLast());
    Assert.assertEquals("a", linkedDeque.pollLast());
    Assert.assertNull(linkedDeque.pollLast());

    //FILO(Stack)
    linkedDeque.push("a");
    linkedDeque.push("b");
    Assert.assertEquals("b", linkedDeque.pop());
    Assert.assertEquals("a", linkedDeque.pop());
    try {
      linkedDeque.pop();// removeLast
    } catch (Exception e) {
      Assert.assertTrue(e instanceof NoSuchElementException);
    }
  }

  /**
   * 链表节点
   */
  private static class Node<E> {

    /**
     * 数据
     */
    E item;
    /**
     * 前置节点
     */
    Node<E> next;
    /**
     * 后置节点
     */
    Node<E> prev;

    Node(Node<E> prev, E element, Node<E> next) {
      this.item = element;
      this.next = next;
      this.prev = prev;
    }
  }
}
