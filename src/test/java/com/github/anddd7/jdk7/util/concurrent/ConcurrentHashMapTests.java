package com.github.anddd7.jdk7.util.concurrent;

import com.github.anddd7.jdk7.util.HashMapTests;
import com.github.anddd7.jdk7.util.HashMapTests.Entry;
import java.io.Serializable;
import java.util.Collections;
import java.util.HashMap;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.ReentrantLock;

/**
 * @see HashMap 非线程安全
 * @see HashMapTests#transfer(Entry[], boolean)
 * @see HashMapTests#concurrentIssue_WhenTransferInMultiThread()
 * - HashMap导致的死循环问题: 并发的rehash
 * @see Collections#synchronizedMap 对每个操作都加锁, 在极端情况下(退化链表)操作会非常耗时且线程独占
 * @see ConcurrentHashMap 使用分段锁对不同的桶进行锁控制
 */
public class ConcurrentHashMapTests<K, V> {

  static final int MAXIMUM_CAPACITY = 1 << 30;

  private transient final int hashSeed = 0;
  /**
   * 默认ssize=16, sshift=4
   * segmentShift = 32 - 4 = 28
   * segmentMask = 15 = 1111
   */
  private int segmentShift;
  private int segmentMask;

  /**
   * UNSAFE可以直接访问内存变量, 提供一些原子方法
   * SSHIFT,SBASE segment数组在堆的地址基数
   * TBASE,TSHIFT entry数组在堆的地址基数
   */
  private static int SSHIFT;
  private static long SBASE;
  private static long TBASE;
  private static int TSHIFT;
  private static sun.misc.Unsafe UNSAFE;

  /**
   * @see ConcurrentHashMap#get
   * @see ConcurrentHashMap#containsKey
   * - ConcurrentHashMap提供了"最终一致"的遍历方式
   * - Concurrent的get/contains方法并没有使用锁(segment), 而是由volatile和Unsafe类提供保证
   * |  - table通过volatile修饰, 且通过Unsafe直接进行内存取用
   * |  - 因此, 遍历时获取的Entry要么是旧的(存在过的正确值), 要么是新的
   */
  public boolean containsKey(Object key) {
    Segment<K, V> s;
    HashEntry<K, V>[] tab;
    int h = hash(key);
    long u = (((h >>> segmentShift) & segmentMask) << SSHIFT) + SBASE;
    // 获取此时内存中的segment
    if ((s = (Segment<K, V>) UNSAFE.getObjectVolatile(segments, u)) != null
        &&
        (tab = s.table) != null) {
      // 遍历此时内存中的segment.table
      for (HashEntry<K, V> e = (HashEntry<K, V>)
          UNSAFE.getObjectVolatile(
              tab,
              ((long) (((tab.length - 1) & h)) << TSHIFT) + TBASE
          );
          e != null;
          e = e.next) {
        K k;
        if ((k = e.key) == key || (e.hash == h && key.equals(k))) {
          return true;
        }
      }
    }
    return false;
  }

  /**
   * @see ConcurrentHashMap#contains
   * @see ConcurrentHashMap#containsValue
   * -  containsValue的方法比较特殊
   * -  会在无锁情况下尝试2次遍历, 如果遍历过程中segment进行了修改(write操作), 则会加锁进行遍历
   */
  static final int RETRIES_BEFORE_LOCK = 2;

  public boolean containsValue(Object value) {
    if (value == null) {
      throw new NullPointerException();
    }
    final Segment<K, V>[] segments = this.segments;
    boolean found = false;
    long last = 0;
    int retries = -1;
    try {
      outer:
      for (; ; ) {
        // 前2次在不获取segment的锁的情况下进行尝试
        if (retries++ == RETRIES_BEFORE_LOCK) {
          for (int j = 0; j < segments.length; ++j) {
            ensureSegment(j).lock();
          }
        }

        long hashSum = 0L;
        int sum = 0;
        for (int j = 0; j < segments.length; ++j) {
          HashEntry<K, V>[] tab;
          Segment<K, V> seg = segmentAt(segments, j);
          if (seg != null && (tab = seg.table) != null) {
            for (int i = 0; i < tab.length; i++) {
              HashEntry<K, V> e;
              for (e = entryAt(tab, i); e != null; e = e.next) {
                V v = e.value;
                if (v != null && value.equals(v)) {
                  found = true;
                  break outer;
                }
              }
            }
            sum += seg.modCount;
          }
        }

        // 比较上一次遍历过程中的modCount与这次是否相同: 相同则segment没有修改过
        if (retries > 0 && sum == last) {
          break;
        }
        // 记录本次的modCount总和
        last = sum;
      }
    } finally {
      // 如果进行了有锁访问则解锁
      if (retries > RETRIES_BEFORE_LOCK) {
        for (int j = 0; j < segments.length; ++j) {
          segmentAt(segments, j).unlock();
        }
      }
    }
    return found;
  }

  static final <K, V> Segment<K, V> segmentAt(Segment<K, V>[] ss, int j) {
    long u = (j << SSHIFT) + SBASE;
    return ss == null ? null :
        (Segment<K, V>) UNSAFE.getObjectVolatile(ss, u);
  }

  static final <K, V> HashEntry<K, V> entryAt(HashEntry<K, V>[] tab, int i) {
    return (tab == null) ? null :
        (HashEntry<K, V>) UNSAFE.getObjectVolatile(
            tab,
            ((long) i << TSHIFT) + TBASE
        );
  }

  /**
   * @see ConcurrentHashMap#remove
   * @see ConcurrentHashMap#replace
   * -  将删除/替换功能托管给了加锁的桶
   */
  public V remove(Object key) {
    int hash = hash(key);
    Segment<K, V> s = segmentForHash(hash);
    return s == null ? null : s.remove(key, hash, null);
  }

  /**
   * @see ConcurrentHashMap#put(Object, Object)
   * -  先检查和初始化segment
   * -  托管给segment进行put
   */
  public V put(K key, V value) {
    Segment<K, V> s;
    if (value == null) {
      throw new NullPointerException();
    }
    int hash = hash(key);
    // 计算segment位置
    int j = (hash >>> segmentShift) & segmentMask;
    // 检查此时内存中的segment
    if ((s = (Segment<K, V>) UNSAFE.getObject(segments, (j << SSHIFT) + SBASE)) == null) {
      // 初始化新的segment
      s = ensureSegment(j);
    }
    return s.put(key, hash, value, false);
  }

  /**
   * @see ConcurrentHashMap#ensureSegment(int)
   * -  初始化segment, 基于Unsafe提供的CAS操作
   */
  private Segment<K, V> ensureSegment(int k) {
    final Segment<K, V>[] ss = this.segments;
    // segment的内存地址
    long u = (k << SSHIFT) + SBASE;
    Segment<K, V> seg;
    // recheck
    if ((seg = (Segment<K, V>) UNSAFE.getObjectVolatile(ss, u)) == null) {
      // 复制默认配置
      Segment<K, V> proto = ss[0];
      int cap = proto.table.length;
      float lf = proto.loadFactor;
      int threshold = (int) (cap * lf);
      HashEntry<K, V>[] tab = (HashEntry<K, V>[]) new HashEntry[cap];

      // recheck
      if ((seg = (Segment<K, V>) UNSAFE.getObjectVolatile(ss, u)) == null) {
        Segment<K, V> s = new Segment<K, V>(lf, threshold, tab);
        // recheck
        while ((seg = (Segment<K, V>) UNSAFE.getObjectVolatile(ss, u)) == null) {
          // CAS原子操作: 用新的segment替换原有的null值, 只会成功一次
          if (UNSAFE.compareAndSwapObject(ss, u, null, seg = s)) {
            break;
          }
        }
      }
    }
    return seg;
  }

  /**
   * @see ConcurrentHashMap#segmentForHash(int)
   */
  private Segment<K, V> segmentForHash(int h) {
    // 计算元素所在的桶位置
    int index = (h >>> segmentShift) & segmentMask;
    // 计算其内存位置
    long u = (index << SSHIFT) + SBASE;
    // 硬件级别的原子操作
    return (Segment<K, V>) UNSAFE.getObjectVolatile(segments, u);
  }

  /**
   * @see ConcurrentHashMap#hash
   * - 使用了[Wang/Jenkins Hash]替代了HashMap中的hash算法
   */
  private int hash(Object k) {
    int h = hashSeed;
    if ((0 != h) && (k instanceof String)) {
      return sun.misc.Hashing.stringHash32((String) k);
    }
    h ^= k.hashCode();

    h += (h << 15) ^ 0xffffcd7d;
    h ^= (h >>> 10);
    h += (h << 3);
    h ^= (h >>> 6);
    h += (h << 2) + (h << 14);
    return h ^ (h >>> 16);
  }

  /**
   * table是volatile类型, 对table的操作都通过Unsafe执行
   */
  static final <K, V> void setEntryAt(HashEntry<K, V>[] tab, int i, HashEntry<K, V> e) {
    UNSAFE.putOrderedObject(tab, ((long) i << TSHIFT) + TBASE, e);
  }

  static final <K, V> HashEntry<K, V> entryForHash(Segment<K, V> seg, int h) {
    HashEntry<K, V>[] tab;
    return (seg == null || (tab = seg.table) == null) ? null :
        (HashEntry<K, V>) UNSAFE.getObjectVolatile(
            tab,
            ((long) (((tab.length - 1) & h)) << TSHIFT) + TBASE
        );
  }

  /**
   * @see ConcurrentHashMap.Segment
   * -  用分段锁管理桶
   * -  一个segment可以管理多个桶
   */
  private Segment<K, V>[] segments;

  static final class Segment<K, V> extends ReentrantLock implements Serializable {

    /**
     * 内部管理的entry数组
     */
    transient volatile HashEntry<K, V>[] table;
    transient int threshold;
    final float loadFactor;
    transient int modCount;
    transient int count;

    Segment(float lf, int threshold, HashEntry<K, V>[] tab) {
      this.loadFactor = lf;
      this.threshold = threshold;
      this.table = tab;
    }

    /**
     * @see ConcurrentHashMap.Segment#remove(Object, int, Object)
     */
    final V remove(Object key, int hash, Object value) {
      // 加锁
      if (!tryLock()) {
        scanAndLock(key, hash);
      }

      V oldValue = null;
      try {
        HashEntry<K, V>[] tab = table;
        int index = (tab.length - 1) & hash;
        HashEntry<K, V> e = entryAt(tab, index);
        HashEntry<K, V> pred = null;
        while (e != null) {
          K k;
          HashEntry<K, V> next = e.next;
          if ((k = e.key) == key ||
              (e.hash == hash && key.equals(k))) {
            V v = e.value;
            if (value == null || value == v || value.equals(v)) {
              // 摘除当前节点
              if (pred == null) {
                // 作为链表头: next作为新链表头
                setEntryAt(tab, index, next);
              } else {
                // 作为中间节点: 链接前后
                pred.setNext(next);
              }
              // 记录当前Segment的修改
              ++modCount;
              --count;
              oldValue = v;
            }
            break;
          }
          pred = e;
          e = next;
        }
      } finally {
        // 解锁
        unlock();
      }
      return oldValue;
    }

    /**
     * @see ConcurrentHashMap.Segment#put(Object, int, Object, boolean)
     */
    final V put(K key, int hash, V value, boolean onlyIfAbsent) {
      // 获取锁
      HashEntry<K, V> node = tryLock() ? null :
          scanAndLockForPut(key, hash, value);
      V oldValue;
      try {
        HashEntry<K, V>[] tab = table;
        // 获取元素所属桶(在当前这个segment里)的位置
        int index = (tab.length - 1) & hash;
        HashEntry<K, V> first = entryAt(tab, index);
        // 遍历链表
        for (HashEntry<K, V> e = first; ; ) {
          if (e != null) {
            K k;
            if ((k = e.key) == key || (e.hash == hash && key.equals(k))) {
              oldValue = e.value;
              // 已有相同元素
              if (!onlyIfAbsent) {
                // 替换值
                e.value = value;
                ++modCount;
              }
              break;
            }
            e = e.next;
          } else {
            // 已检查到链表尾: 将原链表链接到新元素之后: node->oldChain
            if (node != null) {
              node.setNext(first);
            } else {
              node = new HashEntry<K, V>(hash, key, value, first);
            }
            int c = count + 1;
            if (c > threshold && tab.length < MAXIMUM_CAPACITY) {
              // 需要扩容
              rehash(node);
            } else {
              // 新元素作为新的链表头
              setEntryAt(tab, index, node);
            }
            ++modCount;
            count = c;
            oldValue = null;
            break;
          }
        }
      } finally {
        // 解锁
        unlock();
      }
      return oldValue;
    }

    /**
     * @see ConcurrentHashMap.Segment#rehash(ConcurrentHashMap.HashEntry)
     * -  segment扩容算法
     */
    private void rehash(HashEntry<K, V> node) {
      HashEntry<K, V>[] oldTable = table;
      int oldCapacity = oldTable.length;

      int newCapacity = oldCapacity << 1;
      threshold = (int) (newCapacity * loadFactor);
      HashEntry<K, V>[] newTable = (HashEntry<K, V>[]) new HashEntry[newCapacity];
      int sizeMask = newCapacity - 1;

      // 遍历所有链表
      for (int i = 0; i < oldCapacity; i++) {
        HashEntry<K, V> e = oldTable[i];
        if (e != null) {
          HashEntry<K, V> next = e.next;
          // 计算新的位置
          int idx = e.hash & sizeMask;
          // 分拆当前链表
          if (next == null) {
            newTable[idx] = e;
          } else {
            HashEntry<K, V> lastRun = e;
            int lastIdx = idx;
            // 找到一个lastRun节点, 这个节点之后的节点仍映射到同一个链表上
            for (HashEntry<K, V> last = next;
                last != null;
                last = last.next) {
              int k = last.hash & sizeMask;
              // 同样的一个index
              if (k != lastIdx) {
                lastIdx = k;
                lastRun = last;
              }
            }
            // 将lastRun及后续链搬迁到新位置
            newTable[lastIdx] = lastRun;

            // 遍历当前链表, 创建新的Entry
            for (HashEntry<K, V> p = e; p != lastRun; p = p.next) {
              V v = p.value;
              int h = p.hash;
              int k = h & sizeMask;
              HashEntry<K, V> n = newTable[k];
              newTable[k] = new HashEntry<K, V>(h, p.key, v, n);
            }
          }
        }
      }
      // 将新元素放入table中
      int nodeIndex = node.hash & sizeMask; // add the new node
      node.setNext(newTable[nodeIndex]);
      newTable[nodeIndex] = node;
      // 替换新table
      table = newTable;
    }

    /**
     * @see ConcurrentHashMap.Segment#scanAndLockForPut(Object, int, Object)
     * @see ConcurrentHashMap.Segment#scanAndLock(Object, int)
     * -  当快速加锁tryLock失败后, 继续进行加锁
     * -  重试前遍历链表: 数据预热, 提高内存cache hit
     */
    static final int MAX_SCAN_RETRIES =
        Runtime.getRuntime().availableProcessors() > 1 ? 64 : 1;


    private HashEntry<K, V> scanAndLockForPut(K key, int hash, V value) {
      HashEntry<K, V> first = entryForHash(this, hash);
      // 链表头
      HashEntry<K, V> e = first;
      // 待加入的node
      HashEntry<K, V> node = null;

      int retries = -1;
      // 重试多次直到获取锁
      while (!tryLock()) {
        HashEntry<K, V> f;
        if (retries < 0) {
          // "检查"流程: 在重试的过程中顺便遍历链表, 确定是否实例化node
          if (e == null) {
            // 当前节点为空, node将要作为新节点插入: 实例化
            if (node == null) {
              node = new HashEntry<K, V>(hash, key, value, null);
            }
            retries = 0;
          } else if (key.equals(e.key)) {
            // 与链表头元素key值相同, 只能舍去/替换值: 不需要实例化node
            retries = 0;
          } else {
            // 检查下一个节点
            e = e.next;
          }
        } else if (++retries > MAX_SCAN_RETRIES) {
          // "检查"流程完成, 且重试次数超过64: 阻塞等待锁
          lock();
          break;
        } else if (
          // "检查"流程中链表头发生修改则重新检查
            (retries & 1) == 0 &&
                (f = entryForHash(this, hash)) != first) {
          e = first = f;
          retries = -1;
        }
      }
      return node;
    }

    private void scanAndLock(Object key, int hash) {
      HashEntry<K, V> first = entryForHash(this, hash);
      HashEntry<K, V> e = first;

      int retries = -1;
      while (!tryLock()) {
        HashEntry<K, V> f;
        if (retries < 0) {
          if (e == null || key.equals(e.key)) {
            retries = 0;
          } else {
            e = e.next;
          }
        } else if (++retries > MAX_SCAN_RETRIES) {
          lock();
          break;
        } else if ((retries & 1) == 0 &&
            (f = entryForHash(this, hash)) != first) {
          e = first = f;
          retries = -1;
        }
      }
    }
  }

  static final class HashEntry<K, V> {

    int hash;
    K key;
    volatile V value;
    volatile HashEntry<K, V> next;

    HashEntry(int hash, K key, V value, HashEntry<K, V> next) {
      this.hash = hash;
      this.key = key;
      this.value = value;
      this.next = next;
    }

    /**
     * next是volatile类型
     */
    final void setNext(HashEntry<K, V> n) {
      UNSAFE.putOrderedObject(this, nextOffset, n);
    }

    /**
     * nextOffset 记录next在内存中的偏移量
     */
    static long nextOffset;
    static sun.misc.Unsafe UNSAFE;
  }
}