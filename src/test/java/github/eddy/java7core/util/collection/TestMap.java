package github.eddy.java7core.util.collection;

import github.eddy.Tool;
import java.util.Random;
import org.junit.Before;
import org.junit.Test;

/**
 * @author and777
 * @date 2018/1/23
 */
public class TestMap {

  static int hash(Object k) {
    int h = 0;
    h ^= k.hashCode();
    // This function ensures that hashCodes that differ only by
    // constant multiples at each bit position have a bounded
    // number of collisions (approximately 8 at default load factor).
    h ^= (h >>> 20) ^ (h >>> 12);
    return h ^ (h >>> 7) ^ (h >>> 4);
  }

  /**
   * Returns index for hash code h.
   */
  static int indexFor(int h, int length) {
    // assert Integer.bitCount(length) == 1 : "length must be a non-zero power of 2";
    return h & (length - 1);
  }

  Random r;

  @Before
  public void before() {
    r = new Random(System.currentTimeMillis());
  }

  /**
   * 为什么长度要是2的倍数
   * - 为了均匀分配元素 ,需要用 % 确定某元素在哪一个桶中
   * - 模运算消耗很大 ,用 & 来替代 % : 类似掩码 ,只取低位
   * - 掩码低位都是1 ,可能取到的值在 [0,2^k-1] , 长度就是 2^k
   */
  @Test
  public void whyLengthEqual2k() {
    int maxNum = 100000;
    int[] boxes1 = new int[15];
    int[] boxes2 = new int[15];
    int[] boxes3 = new int[16];

    //假设存储0-1000个数字
    Tool.timer("start");
    for (Integer i = 0; i < maxNum; i++) {
      boxes1[i.hashCode() % boxes1.length]++;
    }
    Tool.timer("% 计算");

    for (Integer i = 0; i < maxNum; i++) {
      boxes2[i.hashCode() & (boxes2.length - 1)]++;
    }
    Tool.timer("& 计算");

    for (Integer i = 0; i < maxNum; i++) {
      boxes3[i.hashCode() & (boxes3.length - 1)]++;
    }
    Tool.timer("& 计算 16桶");

    for (int i : boxes1) {
      System.out.print(i + "\t");
    }
    System.out.println("");

    for (int i : boxes2) {
      System.out.print(i + "\t");
    }
    System.out.println("");

    for (int i : boxes3) {
      System.out.print(i + "\t");
    }
    System.out.println("");
  }

  /**
   * 为什么要进行 hash 混淆函数
   * - 默认的hashCode函数并不是很好
   * - 通过高低位混淆 ,提高散列度
   */
  @Test
  public void whyNeedHash() {
    int maxNum = 100000;
    int size = 16;

    //非混淆
    printHashIndex(maxNum, size, new Function() {
      @Override
      public int getIndex(String s, int length) {
        return s.hashCode() & (length - 1);
      }
    });

    //混淆
    printHashIndex(maxNum, size, new Function() {
      @Override
      public int getIndex(String s, int length) {
        return hash(s) & (length - 1);
      }
    });
  }

  interface Function {

    int getIndex(String s, int length);
  }

  void printHashIndex(int maxNum, int size, Function f) {
    int[] boxes = new int[size];
    int max = 0;
    int average = maxNum / size;

    for (Integer i = 0; i < maxNum; i++) {
      String s = String.valueOf(i) + String.valueOf(r.nextInt()) + String.valueOf(r.nextFloat());
      int index = f.getIndex(s, boxes.length);
      boxes[index]++;
      if (boxes[index] > max) {
        max = boxes[index];
      }
    }

    for (int i : boxes) {
      int offset = i - average;
      System.out.print(i + "(" + offset + ")\t");
    }
    System.out.println("\n[" + max + "] [" + average + "]");
  }
}
