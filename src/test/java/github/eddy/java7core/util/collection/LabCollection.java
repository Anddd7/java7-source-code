package github.eddy.java7core.util.collection;

import github.eddy.java7core.util.concurrent.LabConcurrentCollection;
import java.util.AbstractQueue;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.LinkedList;
import java.util.PriorityQueue;
import java.util.Queue;
import java.util.Stack;
import java.util.concurrent.BlockingDeque;
import java.util.concurrent.BlockingQueue;

public class LabCollection {
  /**
   */

  /**
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
