package github.eddy.java7core.util.collection;

import java.util.AbstractMap;
import java.util.AbstractSet;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.IdentityHashMap;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.WeakHashMap;
import java.util.concurrent.ConcurrentHashMap;

public class LabMap {
  /**
   {@link IdentityHashMap}
   - 与HashMap最大的区别是能够存放'相同key'的对象
   |  - 使用=比较key值 ,如果是 "a" 和 new String("a") 就可以存放2个值


   {@link WeakHashMap}
   - 使用弱引用管理entry ,可能被GC清理掉 ,适用于缓存


   {@link Hashtable}
   - 类似HashMap : 属于Java遗留的结构 ,并不适用于现在使用 (使用  替代)
   |  - 不允许null值
   |  - 线程安全 ,但建议使用 {@link ConcurrentHashMap} > {@link Collections#synchronizedMap(Map)}


   {@link Properties}
   - 继承自HashTable
   |  - 提供从File/InputStream到HashTable的方法


   {@link LinkedHashMap}
   - 继承自HashMap , 额外增加链表 , 使元素可以按插入顺序(accessOrder = 访问顺序)进行遍历
   |  - 在put元素后 ,同时把元素 add 到链表的头部(header.before)
   |      - {@link LinkedHashMap#createEntry(int, Object, Object, int)}
   |      - {@link LinkedHashMap.Entry#addBefore(Entry)}
   |  - iterator 是基于这个链表 ,从 header.after 向后遍历 , 先入先出
   |      - {@link LinkedHashMap#newEntryIterator()}
   |      - {@link java.util.LinkedHashMap.LinkedHashIterator#nextEntry()}
   |  - 如果设置了accessOrder=true , get的时候会触发entry.recordAccess , 把元素插入到header.before
   |      - {@link LinkedHashMap.Entry#recordAccess(HashMap)}


   {@link TreeMap}
   - 基于红黑树的Map ,通过树的搜索/插入/删除来实现Map功能
   |  - 有序结构 ,查询时间 O(log n)
   */

  /**
   {@link Set}
   {@link AbstractSet}
   - 不允许重复的顺序集合

   {@link HashSet}
   - 基于HashMap的方式实现的set

   {@link LinkedHashSet}
   - 基于LinkedHashMap的方式实现的set

   {@link TreeSet}
   - 基于TreeMap的方式实现的set ,是有序的
   */
}
