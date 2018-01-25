package github.eddy.java7core.util.collection;

import java.util.Arrays;
import org.junit.Test;

/**
 * @author and777
 * @date 2018/1/13
 *
 * 数组相关的操作
 */
public class TestArray {

  int[] source = {1, 2, 3, 4, 5, 6, 7, 8, 9, 0};

  /**
   * {@link System#arraycopy(Object, int, Object, int, int)}
   * 把a数组的aPos~aPos+length-1的元素拷贝到b数组的bPos~bPos+length-1的位置
   * - 两个数组都不能为空
   * - 位置不能超界 : 类似循环赋值 ,两个数组的起始位置[pos,pos+length]都应该在0~size之间
   * - 数组的存放类型一致
   */
  @Test
  public void systemCopy() {
    int[] b = new int[10];
    int[] c = new int[5];
    int[] d = new int[20];

    System.arraycopy(source, 0, b, 0, 10);
    print(b);
    System.arraycopy(source, 0, c, 0, 5);
    print(c);
    System.arraycopy(source, 0, d, 0, 10);
    print(d);
    System.arraycopy(d, 10, c, 0, 5);
    print(c);
  }

  /**
   * {@link Arrays#copyOf(Object[], int),Arrays#copyOfRange(Object[], int, int)}
   * 这两个函数封装了: new dest数组;复制元素到dest数组;返回dest数组
   * 用于快速复制一个数组
   * - length不能超过原数组长度
   * - 对于非基本类型 :
   * Object-创建Object数组;
   * 其他类型-通过 {@link java.lang.reflect.Array#newInstance(Class, int)}创建指定长度的对象数组
   */
  @Test
  public void arraysCopyOf() {
    print(Arrays.copyOf(source, 5));
    print(Arrays.copyOf(source, 10));
  }

  public void print(int[] target) {
    for (int i : target) {
      System.out.print(i + "\t");
    }
    System.out.println("");
  }
}
