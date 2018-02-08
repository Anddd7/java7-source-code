package github.eddy.java7core.util.collection;

import github.eddy.java7core.io.TestCloneableAndSerializable;
import github.eddy.java7core.util.TestRandomAccess;
import github.eddy.java7core.util.collection.source.SourceAbstractCollection;
import github.eddy.java7core.util.concurrent.LabConcurrentCollection;
import java.util.AbstractCollection;
import java.util.AbstractList;
import java.util.AbstractQueue;
import java.util.AbstractSequentialList;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Deque;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;
import java.util.PriorityQueue;
import java.util.Queue;
import java.util.Stack;
import java.util.Vector;
import java.util.concurrent.BlockingDeque;
import java.util.concurrent.BlockingQueue;

public class LabCollection {
  /**
   - 相关接口
   {@link TestCloneableAndSerializable}
   {@link TestRandomAccess}
   */

  /**
   - 顶层接口

   {@link Iterable}
   - 返回一个迭代器 ,用于实现 for (T element : collection){}的语法糖 ,编译后实际是 for (Iterator iterator = list.iterator(); iterator.hasNext(); ) {}

   {@link Collection}
   - 集合基本操作定义

   {@link AbstractCollection}
   - 集合类型基类 :
   |  - 所有需要查询/遍历集合的行为 ,都是通过生成的iterator实现的
   |  - 实现contains/remove方法
   |  - 实现{@link SourceAbstractCollection#toArray()} ,并发情况下复制元素 ,扩容 n/2+1
   */

  /**
   {@link Iterator}
   - 迭代器 : 不同数据结构创建自己的迭代器 ,用来屏蔽底层数据结构的遍历方式(例如数组和链表)

   {@link ListIterator}
   - 双向迭代器 ,为list提供add/set/remove等方法
   */

  /**
   {@link List}
   - 有序列表

   {@link AbstractList}
   - List基类
   |  - 实现iterator和listIterator : 轻量级内部类
   |  - 实现subList : 通过 list引用/上下界 约束对list的部分访问
   |  - 实现indexOf/lastIndexOf : 基于listIterator ,从头/尾开始遍历

   {@link AbstractSequentialList}
   - 与RandomAccess相反 ,不能随机访问某一元素 ,因此add/set/get方法都是无效的
   |  - 但是又必须提供标准的collection的方法 ,所以基于listIterator实现add/set/get

   {@link ArrayList}
   - 基于数组实现的有序列表
   |  - 实现add/get/set方法 : 对数组取用
   |  - 实现扩容算法 : 初始为空数组 ,加入元素扩容到10 ,每次扩容为当前长度的1.5倍 {@link ArrayList#ensureCapacityInternal(int)}
   |  - 手动扩容(大数据量时 ,避免多次自动扩容的性能损失) : ensureCapacity{@link ArrayList#ensureCapacity(int)}
   |  - 线程不安全 : add/set方法不是原子操作 ,多线程情况下会冲突

   {@link Vector}
   - 结构和方法同ArrayList ,但是线程安全的
   |  - 扩容大小可手动设置 ,默认是当前元素的2倍
   |  - 线程安全 : 在add/get/set/ensureCapacity等方法上添加了synchronized关键字(所有对elementData数组进行操作取值的方法){@link Vector#ensureCapacity(int)}

   {@link Stack}
   - 基于Vector实现的栈 ,实现后入先出(LIFO) : 官方推荐使用Deque来实现栈的功能
   |  - push = add() ,pop = remove(lastIndex) ,search = 比较最后一个元素
   |  - 线程安全 : synchronized关键字

   {@link LinkedList}
   - 链表结构
   |  - 基于node(prev,element,next)存放和链接数据
   |  - 记录头/尾node ,进行相关操作 getFirst/getLast/add
   |  - 线程不安全 : add/get不是原子操作s
   */

  /**
   {@link Queue}
   - 队列 ,先进先出
   |  - 队列不允许随机访问元素 (窄化了list的访问方式)
   |  - collection#add/remove/element 和 queue#poll/offer/peek 的方法实现一致 ,只是collection超界会抛出异常 ,队列则不会
   |  - 队列大多数用于多线程环境 ,队列是可能为空和为满的 ,因此应该避免可能的超界异常 -> 避免使用collection的一些方法

   {@link Deque}
   - 双向队列 ,两头进两头出
   |  - 提供了对两头元素的访问方式 ,offer = offerLast ,poll = pollFirst
   |  - 提供了stack相关的方法 : push=offerFirst ,pop=pollFirst

   {@link LinkedList}
   - 基于链表的队列实现
   |  - 队列方法和集合方法的实现是一样的 ,只是角度不同

   {@link ArrayDeque}
   - 基于数组的队列实现
   |  - 默认大小16 ; 每次扩容2倍 ; 给定大小n ,会计算出最小的大于n的 2^k-1 长度 {@link TestQueue#allocateElements()}
   |  - 扩容时会复制元素并重排序 {@link ArrayDeque#doubleCapacity()} :  |------|tail/head|-------|  =>  |head|-------------|tail|....................|
   |  - FIFO : 使用 head/tail 指向当前 取值/放置 的坐标 @{@link TestQueue#arraDeque()}
   |  - 线程不安全

   {@link AbstractQueue}
   - 队列基本实现 : 基于 offer/poll/peek 实现 add/remove/element

   {@link PriorityQueue}
   - 优先级队列 ,基于数组实现了一个二叉树 : 0-root 1-left 2-right ... 依次类推
   |  - 因为是二叉树 : 默认大小11 ; 小于64时扩容 2n+2 ,大于64时扩大 1.5倍 ;
   |  - add 时插入元素到队尾 ,通过{@link PriorityQueue#siftUp(int, Object)}比较父节点(k-1)>>>1 ,上升直到比父节点小的位置
   |    (这个操作保证 根节点元素一定小于叶子节点 ,但叶子节点的大小不确定)
   |  - remove 时弹出第一个元素 ,并将队尾元素插到头部 ,通过{@link PriorityQueue#siftDown(int, Object)}比较子节点 ,和较小的节点交换位置
   |    (此时比较左右叶子 ,保证最小的元素交换到根部)
   |  - removeAt 将队列最后一个元素插入到指定位置 ,然后再调整顺序

   {@link BlockingQueue} {@link BlockingDeque}
   - blocking队列额外实现了put/take操作 ,在队列满/空的时候进行阻塞 {@link LabConcurrentCollection}
   */
}
