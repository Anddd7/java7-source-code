package github.eddy.java7core.util;

import github.eddy.java7core.io.CloneableTests;
import github.eddy.java7core.io.SerializableTests;
import java.io.Serializable;
import java.util.AbstractSequentialList;
import java.util.Deque;
import java.util.LinkedList;
import java.util.List;

/**
 * @author Anddd7
 *
 * {@link LinkedList} extends {@link AbstractSequentialList}
 * implements {@link List,Deque,Cloneable,Serializable}
 *
 * {@link LinkedListTests} extends {@link AbstractSequentialListTests}
 * implements {@link ListTests,DequeTests,CloneableTests,SerializableTests}
 *
 * 基于链表实现的 List 和 Deque
 */
public class LinkedListTests<E> {

  /**
   * {@link LinkedList#modCount}
   * - modify 计数
   */
  protected transient int modCount = 0;
  /**
   * {@link LinkedList#first}
   * 链表头指针
   */
  transient Node<E> first;
  /**
   * {@link LinkedList#last}
   * 链表尾指针
   */
  transient Node<E> last;
  /**
   * {@link LinkedList#size}
   * - 元素数
   */
  transient int size = 0;


  /**
   *
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
      // 3.2 如果前置链不为空 ,修改前置节点的关联关系
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
   *
   * TODO
   */
  private void linkFirst(E e) {
    // 前置节点= null ,后置节点= first
    final Node<E> f = first;
    final Node<E> newNode = new Node<>(null, e, f);
    first = newNode;
    if (f == null) {
      last = newNode;
    } else {
      f.prev = newNode;
    }
    size++;
    modCount++;
  }


  /**
   * {@link LinkedList#unlink(LinkedList.Node)}
   * - 从链表中删除节点
   * TODO
   */
  E unlink(Node<E> x) {
    final E element = x.item;
    final Node<E> next = x.next;
    final Node<E> prev = x.prev;

    if (prev == null) {
      first = next;
    } else {
      prev.next = next;
      x.prev = null;
    }

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
   * TODO
   */
  private E unlinkFirst(Node<E> f) {
    // 暂存元素
    final E element = f.item;
    // 后置链
    final Node<E> next = f.next;
    // 清理节点 f 的关系
    f.item = null;
    f.next = null;
    // reset first
    first = next;

    if (next == null) {
      // 如果后置链为空 ,则已无可用节点 ,清理 last
      last = null;
    } else {
      // 如果后置链不为空
      // 后置链已经是链头 ,后置链.prev =null
      next.prev = null;
    }

    size--;
    modCount++;
    return element;
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
