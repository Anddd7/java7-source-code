package github.eddy.java7core.util.source;

import java.util.AbstractCollection;
import java.util.Arrays;
import java.util.Iterator;

/**
 * @author and777
 * @date 2018/1/12
 */
public abstract class SourceAbstractCollection<E> extends AbstractCollection<E> {

  @Override
  public boolean contains(Object o) {
    Iterator<E> it = iterator();
    if (o == null) {
      while (it.hasNext()) {
        //o为空 ,则查询是否有为空的元素
        if (it.next() == null) {
          return true;
        }
      }
    } else {
      while (it.hasNext()) {
        //o不为空 ,调用o的equals方法进行比较 ,避免元素为空
        if (o.equals(it.next())) {
          return true;
        }
      }
    }
    return false;
  }

  /**
   * 返回元素数组
   * - 并发情况下size可能变化,元素数以返回的iterator为准
   *
   * 例如:
   * 一个数组toArray
   * 1.元素add/remove ,iterator的长度和数组r不一致
   */
  @Override
  public Object[] toArray() {
    Object[] r = new Object[size()];
    //-1 并发
    Iterator<E> it = iterator();
    for (int i = 0; i < r.length; i++) {
      if (!it.hasNext()) {
        //iterator小于当前size 提前退出
        return Arrays.copyOf(r, i);
      }
      //放入数组
      r[i] = it.next();
    }
    //iterator大于当前size 继续补充
    return it.hasNext() ? finishToArray(r, it) : r;
  }

  /**
   * 把元素放入指定的数组a中
   * - 如果a太大,则在最后一个元素之后放入null
   * - 如果a太小,则返回所有元素组成的数组
   * - 也需要考虑并发
   */
  @Override
  public <T> T[] toArray(T[] a) {
    int size = size();
    T[] r = a.length >= size ?
        a : (T[]) java.lang.reflect.Array
        .newInstance(a.getClass().getComponentType(), size); //反射初始化结果数组
    //-1 并发
    Iterator<E> it = iterator();
    for (int i = 0; i < r.length; i++) {
      //iterator小于预期长度(size) ,需要提前终止
      if (!it.hasNext()) {
        if (a == r) {
          //结果数组为a ,说明a太大 ,末尾放入null返回
          r[i] = null;
        } else if (a.length < i) {
          //a数组的容量小于内部数组r的有效段 ,返回有效段
          return Arrays.copyOf(r, i);
        } else {
          //a数组较小但又大于等于当前的有效段 ,把有效的部分copy到a中
          System.arraycopy(r, 0, a, 0, i);
          //如果a还有剩余空间 ,末尾放入null返回
          if (a.length > i) {
            a[i] = null;
          }
        }
        return a;
      }
      r[i] = (T) it.next();
    }
    // iterator大于预期数量 ,需要补充
    return it.hasNext() ? finishToArray(r, it) : r;
  }

  /**
   * 数组最大长度: 部分VM设计会在集合存放其他信息 ,因此数组长度需要预留一些空间
   */
  private static final int MAX_ARRAY_SIZE = Integer.MAX_VALUE - 8;

  /**
   * iterator还有剩余元素需要补充到Arry里
   */
  private static <T> T[] finishToArray(T[] r, Iterator<?> it) {
    //i 记录末尾元素位置
    int i = r.length;
    while (it.hasNext()) {
      //确定当前的数组空间
      int cap = r.length;
      //插入元素已满 ,需要扩容
      if (i == cap) {
        //每次扩容当前长度的1/2+1
        int newCap = cap + (cap >> 1) + 1;
        /**
         * 当 cap=1431655759 时 : newCap = MAX_ARRAY_SIZE
         * 如果扩容过后的结果超过最大界限 则需要判断空间是否超出数组界限(Interger.MAX_VALUE)
         *
         * PS: 为什么使用减法再比较是否大于0 ,而不直接 newCap > MAX_ARRAY_SIZE
         * 答: 直接比较时会先比较符号位 ,当newCap产生溢出时为负数 ,而负数的长度显然是不被允许的 .
         * 使用减法时 ,当 newCap>MaxArraySize 或者 newCap溢出 , newCap - MAX_ARRAY_SIZE > 0 都成立 ,此时newCap的值已经超界.
         * 因此使用newCap的值无效 ,使用hugeCapacity直接把数组扩容到最大值.
         *
         */
        if (newCap - MAX_ARRAY_SIZE > 0) {
          newCap = hugeCapacity(cap + 1);
        }
        //创建更大的数组 ,copy value
        r = Arrays.copyOf(r, newCap);
      }
      r[i++] = (T) it.next();
    }
    //裁掉扩容的多余空间
    return (i == r.length) ? r : Arrays.copyOf(r, i);
  }

  /**
   * 获取下一次扩容的大小 ,minCapacity = 当前容量+1
   */
  private static int hugeCapacity(int minCapacity) {
    //最小扩容大小已经溢出 ,则cap=Integer.MAX_VALUE ,已经无法扩容
    if (minCapacity < 0) {
      throw new OutOfMemoryError
          ("Required array size too large");
    }
    //如果当前容量已经到数组最大值 ,则使用Integer.MAX_VALUE作为数组长度
    return (minCapacity > MAX_ARRAY_SIZE) ? Integer.MAX_VALUE : MAX_ARRAY_SIZE;
  }
}
