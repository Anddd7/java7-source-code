package com.github.anddd7.jdk7.util;

import static java.lang.String.format;

import java.lang.reflect.Array;
import java.util.AbstractMap;
import java.util.HashMap;
import java.util.Map;
import org.junit.Assert;
import org.junit.Test;

/**
 * @author Anddd7
 * {@link HashMap} extends {@link AbstractMap} implements {@link Map ,Cloneable, Serializable}
 *
 * 基于 hash 算法实现的 Map: 数组(桶)+链表
 */
public class HashMapTests<K, V> {

  /**
   * 最大元素数
   */
  private static final int MAXIMUM_CAPACITY = 1 << 30;
  static final int ALTERNATIVE_HASHING_THRESHOLD_DEFAULT = Integer.MAX_VALUE;
  private static final Entry[] EMPTY_TABLE = {};
  private final float loadFactor = 0.75f;
  private int threshold;
  /**
   * 桶
   */
  private transient Entry<K, V>[] table = (Entry<K, V>[]) EMPTY_TABLE;
  private transient int hashSeed = 0;
  private transient int size;
  private transient int modCount;

  /**
   * {@link HashMap#HashMap()}
   * - 桶初始大小16,负载因子0.75 ; 当size >= capacity*loadFactor ,就会扩容
   * |  - 桶大小必须是2^n ,通过indexFor的位置才均匀
   * |  - 负载因子越大 ,桶越少/链表元素越多 ,内存占用少查询时间长
   *
   * {@link HashMap#containsValue(Object)}
   * - 全局搜索: 桶+链表
   *
   * {@link HashMap.HashIterator}
   * - 通过 桶+index 遍历数组, 通过 entry 遍历链表
   */
  public HashMapTests() {
  }

  /**
   * {@link HashMap#roundUpToPowerOf2(int)}
   * - 获取大于等于 number 的最小的2^k
   * | - {@link ArrayDequeTests#allocateElements(int)} 获取的是大于 number 的最小的2^k
   * - 不超过最大元素数
   *
   * 桶的数量为什么必须是2的倍数 ?
   * - 为了均匀分配元素 ,需要用 % 确定某元素在哪一个桶中
   * - 模运算消耗很大 ,用 & 来替代 % : 类似掩码 ,只取低位
   * - 掩码低位都是1 ,可能取到的值在 [0,2^k-1] , 长度就是 2^k
   *
   * 因为 indexFor 使用 & 计算桶的位置,
   * 当桶数量为 2^n 时, length-1 即为 0...111111 的二进制数,
   * 能保证最大的散度
   */
  private static int roundUpToPowerOf2(int number) {
    return number >= MAXIMUM_CAPACITY ? MAXIMUM_CAPACITY
        : (number > 1) ? Integer.highestOneBit((number - 1) << 1) : 1;
  }

  /**
   * {@link HashMap#indexFor(int, int)}
   * - 获取散列桶的位置
   */
  static int indexFor(int h, int length) {
    return h & (length - 1);
  }

  @Test
  public void roundUpToPowerOf2() {
    Assert.assertEquals(4, roundUpToPowerOf2(3));
    Assert.assertEquals(16, roundUpToPowerOf2(15));
    Assert.assertEquals(64, roundUpToPowerOf2(64));
    Assert.assertEquals(128, roundUpToPowerOf2(65));
  }

  /**
   * {@link HashMap#inflateTable(int)}
   */
  private void inflateTable(int toSize) {
    // 获取当前所需容量
    int capacity = roundUpToPowerOf2(toSize);
    // 计算出下一个扩容临界值
    threshold = (int) Math.min(capacity * loadFactor, MAXIMUM_CAPACITY + 1);
    // 创建 table
    table = new Entry[capacity];
    // 必要时能通过特殊程序生成 hashSeed, 减少 hash 冲突
    // initHashSeedAsNeeded(capacity);
  }

  /**
   * {@link HashMap#hash(Object)}
   * - 再散列算法, 防止过于脆弱的hashCode方法影响分桶效率
   */
  final int hash(Object k) {
    int h = hashSeed;
    if (0 != h && k instanceof String) {
      return sun.misc.Hashing.stringHash32((String) k);
    }
    h ^= k.hashCode();

    // 增加散列程度(hashCode()方法太脆弱), 将高位的code互相扰乱 (JDK8简化了这一步)
    h ^= (h >>> 20) ^ (h >>> 12);
    return h ^ (h >>> 7) ^ (h >>> 4);
  }

  @Test
  public void hash() {
    Object o = new Object();
    int hashSeed = 0;

    int hash = o.hashCode();
    System.out.println(format("hash code :\t%s", toBinaryString(hash)));

    hash ^= hashSeed;
    System.out.println(format("hash seed :\t%s", toBinaryString(hash)));

    int temp1 = hash >>> 20;
    int temp2 = hash >>> 12;
    int temp3 = temp1 ^ temp2;
    System.out.println(
        format("hash temp1:\t%s", toBinaryString(temp1)));
    System.out.println(
        format("hash temp2:\t%s", toBinaryString(temp2)));
    System.out.println(
        format("hash temp3:\t%s", toBinaryString(temp3)));

    hash ^= temp3;
    System.out.println(
        format("hash after:\t%s", toBinaryString(hash)));
    showDifference(o.hashCode(), hash);

    int temp4 = hash >>> 7;
    int temp5 = hash >>> 4;
    int temp6 = temp4 ^ temp5;
    System.out.println(
        format("hash temp4:\t%s", toBinaryString(temp4)));
    System.out.println(
        format("hash temp5:\t%s", toBinaryString(temp5)));
    System.out.println(
        format("hash temp6:\t%s", toBinaryString(temp6)));

    hash ^= temp6;
    System.out.println(
        format("hash after:\t%s", toBinaryString(hash)));
    showDifference(o.hashCode(), hash);
  }

  private String toBinaryString(int hash) {
    String hashString = Integer.toBinaryString(hash);
    int paddingNum = 30 - hashString.length();
    StringBuilder padding = new StringBuilder();
    while (paddingNum > 0) {
      padding.append(" ");
      paddingNum--;
    }
    return padding + hashString;
  }

  private void showDifference(int s, int t) {
    String source = toBinaryString(s);
    String target = toBinaryString(t);

    StringBuilder outSource = new StringBuilder();
    StringBuilder outTarget = new StringBuilder();

    int point = 0;
    while (point < source.length()) {
      char x = source.charAt(point);
      char y = target.charAt(point);
      if (x != y) {
        outSource.append("[").append(x).append("]");
        outTarget.append("[").append(y).append("]");
      } else {
        outSource.append(x);
        outTarget.append(y);
      }
      point++;
    }
    System.out.println(String.format("difference:\n\t%s\n\t%s", outSource, outTarget));
  }

  /**
   * {@link HashMap#getEntry(Object)}
   * - 查询元素的核心逻辑
   */
  final Entry<K, V> getEntry(Object key) {
    if (size == 0) {
      return null;
    }

    // 通过散列值获取桶中的第一个 entry, 遍历链表
    int hash = (key == null) ? 0 : hash(key);
    for (Entry<K, V> e = table[indexFor(hash, table.length)];
        e != null;
        e = e.next) {
      Object k;
      // 找到对应 key 的 entry
      if (e.hash == hash &&
          ((k = e.key) == key || (key != null && key.equals(k)))) {
        return e;
      }
    }
    return null;
  }

  /**
   * {@link HashMap#get(Object)}
   * - 获取 value
   */
  public V get(Object key) {
    if (key == null) {
      return getForNullKey();
    }
    Entry<K, V> entry = getEntry(key);
    return null == entry ? null : entry.getValue();
  }

  /**
   * {@link HashMap#getForNullKey()}
   * -  以 null 为 key 值的元素会默认放在第一个桶中, 避免 hash(null)
   */
  private V getForNullKey() {
    if (size == 0) {
      return null;
    }
    for (Entry<K, V> e = table[0]; e != null; e = e.next) {
      if (e.key == null) {
        return e.value;
      }
    }
    return null;
  }

  /**
   * {@link HashMap#put(Object, Object)}
   * - 存放元素
   */
  public V put(K key, V value) {
    // 第一次加元素, 初始化扩容
    if (table == EMPTY_TABLE) {
      inflateTable(threshold);
    }
    // null 元素不能 hash(null), 单独处理
    if (key == null) {
      // return putForNullKey(value);
    }
    // 计算桶位置
    int hash = hash(key);
    int i = indexFor(hash, table.length);
    // 循环链表是否已有 key:entry
    for (Entry<K, V> e = table[i]; e != null; e = e.next) {
      Object k;
      // 替换已有 value
      if (e.hash == hash && ((k = e.key) == key || key.equals(k))) {
        V oldValue = e.value;
        e.value = value;
        e.recordAccess(this);
        return oldValue;
      }
    }

    // 链表中没有 entry, 新加一个
    modCount++;
    addEntry(hash, key, value, i);
    return null;
  }

  /**
   * {@link HashMap#addEntry(int, Object, Object, int)}
   * - 在链表头加 entry
   */
  void addEntry(int hash, K key, V value, int bucketIndex) {
    if ((size >= threshold) && (null != table[bucketIndex])) {
      // 需要扩容
      resize(2 * table.length);
      // 重新计算 hash
      hash = (null != key) ? hash(key) : 0;
      // 重新计算桶位置
      bucketIndex = indexFor(hash, table.length);
    }
    // 添加 entry
    createEntry(hash, key, value, bucketIndex);
  }

  /**
   * {@link HashMap#createEntry(int, Object, Object, int)}
   * - 创建一个 entry 在链表头
   */
  void createEntry(int hash, K key, V value, int bucketIndex) {
    Entry<K, V> e = table[bucketIndex];
    // table[bucketIndex] = new Entry<>(hash, key, value, e);
    size++;
  }

  /**
   * {@link HashMap#resize(int)}
   * - 扩容
   */
  void resize(int newCapacity) {
    Entry[] oldTable = table;
    int oldCapacity = oldTable.length;
    // 已达上限
    if (oldCapacity == MAXIMUM_CAPACITY) {
      threshold = Integer.MAX_VALUE;
      return;
    }
    Entry[] newTable = new Entry[newCapacity];

    // 扩容后, 元素需要重排 rehash, 循环所有桶的所有元素
    // transfer(newTable, initHashSeedAsNeeded(newCapacity));
    table = newTable;
    threshold = (int) Math.min(newCapacity * loadFactor, MAXIMUM_CAPACITY + 1);
  }

  /**
   * {@link HashMap#removeEntryForKey(Object)}
   */
  final Entry<K, V> removeEntryForKey(Object key) {
    if (size == 0) {
      return null;
    }
    // 搜索元素所在的桶
    int hash = (key == null) ? 0 : hash(key);
    int i = indexFor(hash, table.length);
    Entry<K, V> prev = table[i];
    Entry<K, V> e = prev;

    while (e != null) {
      Entry<K, V> next = e.next;
      Object k;
      if (e.hash == hash &&
          ((k = e.key) == key || (key != null && key.equals(k)))) {
        // 找到目标元素, 删除(unlink)
        modCount++;
        size--;
        if (prev == e) {
          table[i] = next;
        } else {
          prev.next = next;
        }
        e.recordRemoval(this);
        return e;
      }
      // 迭代下一个
      prev = e;
      e = next;
    }
    return e;
  }

  /**
   * @see HashMap#transfer(HashMap.Entry[], boolean)
   * - rehash时重新映射元素
   * - 引起死循环的地方
   */
  void transfer(Entry[] newTable, boolean rehash) {
    int newCapacity = newTable.length;
    for (Entry<K, V> e : table) {
      // 循环桶中的链表
      while (null != e) {
        // e: 当前元素, next: 下一个循环的元素
        Entry<K, V> next = e.next;
        if (rehash) {
          e.hash = null == e.key ? 0 : hash(e.key);
        }
        // 计算新位置
        int i = indexFor(e.hash, newCapacity);
        // 将e插入到table[i]的链表头
        /*
         并发环境下: e = x, next = y
         线程A执行过后, 交换了x/y的位置: y->x
         线程B执行: table[i]=x

         线程B进入下一个循环: e = y, next = x(由线程A修改)
         线程B执行: table[i]=y->x

         线程B进入下一个循环: e = x, next = null
         线程B执行: table[i]=x->y->x (!!!环形链表出现)

         现在当一个get方法在遍历table[i]时, 就会出现死循环: CPU 100%
         */
        e.next = newTable[i];
        newTable[i] = e;
        // 继续下一个循环
        e = next;
      }
    }
  }


  @Test
  public void concurrentIssue_WhenTransferInMultiThread() {
    // 0.默认只有一条链
    Node[] newTable = (Node[]) Array.newInstance(Node.class, 1);

    // 0.待处理的链: x->y
    Node x = new Node("x", new Node("y", null));
    Node y = x.next;

    // 1.线程A执行后, newTable: y->x->null
    y.next = x;
    x.next = null;
    newTable[0] = y;

    // 2.线程B仍以x->y的顺序遍历
    Node e = x;
    Node next = y;

    e.next = newTable[0];// x->y->x
    newTable[0] = e;//newTable[0]: x->y->x

    // 3.进入下一个循环
    e = next;// y
    next = y.next;// x

    System.out.println(x);
    System.out.println(y);
  }

  private class Node {

    private String key;
    private Node next;

    Node(String key, Node next) {
      this.key = key;
      this.next = next;
    }

    @Override
    public String toString() {
      return format("key:[%s],next:[%s]", key, next.key);
    }
  }

  public static abstract class Entry<K, V> implements Map.Entry<K, V> {

    K key;
    V value;
    Entry<K, V> next;
    int hash;

    /**
     * 可以用来记录元素的操作
     */
    void recordAccess(HashMapTests<K, V> m) {
    }

    void recordRemoval(HashMapTests<K, V> m) {
    }
  }
}
