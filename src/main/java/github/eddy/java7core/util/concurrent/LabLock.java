package github.eddy.java7core.util.concurrent;

/**
 * @author and777
 * @date 2018/1/31
 *
 *
 * 可重入锁Reentrant : 基于线程的分配 ,线程拿到锁过后执行加锁的方法不需要重复拿锁
 * - synchronized methodA 中调用了 synchronized methodB
 * - 当一个线程有权执行methodA时 ,就能直接执行methodB
 * - 实现 : synchronized/Lock
 *
 * 可中断锁 : 未拿到锁而等待的线程可以相应中断
 * - 实现 : Lock
 *
 * 公平锁 : 按请求顺序获取锁
 * - 实现 : ReentrantLock/ReentrantReadWriteLock
 *
 * 读写锁 : 读写/写写 需要同步 ,读读不需要
 * - 实现 : ReadWriteLock
 */
public class LabLock {
  /**
   {@link java.util.concurrent.locks.Lock}
   - 定义锁的操作 :
   | - lock 获取锁 : 获取不到会等待
   | - unlock 释放锁 : 放在try-catch-finally中保证锁的释放
   | - tryLock 尝试获取锁 : 成功与否都会立即返回(非阻塞)
   | - lockInterruptibly : 获取锁的过程中可以被 interrupt 中断

   {@link java.util.concurrent.locks.ReentrantLock}
   - 可重入/可中断/公平 锁实现
   |  -



   {@link java.util.concurrent.locks.ReadWriteLock}
   - 定义读写锁
   | - 获取读/写锁

   {@link java.util.concurrent.locks.ReentrantReadWriteLock}
   - 可重入读写锁
   |  - 只有 读写/写写 需要同步

   */
}
