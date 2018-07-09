package com.github.anddd7.jdk7.lang;

import static org.junit.Assert.assertEquals;

import com.github.anddd7.jdk7.util.ArraysTests;
import org.junit.Test;

/**
 * @author and777
 * @see System
 * 系统工具类
 */
public class SystemTests {

  /**
   * {@link System#arraycopy(Object, int, Object, int, int)}
   * 把a数组的aPos~aPos+length-1的元素拷贝到b数组的bPos~bPos+length-1的位置
   * - 两个数组都不能为空
   * - 位置不能超界 : 类似循环赋值 ,两个数组的起始位置[pos,pos+length]都应该在0~size之间
   * - 数组的存放类型一致
   *
   * 便捷工具 {@link ArraysTests}
   */
  @Test
  public void arrayCopy() {
    int[] source = {1, 2, 3, 4, 5, 6, 7, 8, 9, 0};
    int[] b = new int[10];
    System.arraycopy(source, 0, b, 0, 10);
    assertEquals(source[4], b[4]);
    assertEquals(source[6], b[6]);

    int[] c = new int[5];
    System.arraycopy(source, 0, c, 0, 5);
    assertEquals(source[4], c[4]);

    int[] d = new int[20];
    System.arraycopy(source, 0, d, 0, 10);
    assertEquals(source[4], d[4]);
    assertEquals(source[6], d[6]);
    assertEquals(0, d[13]);

    System.arraycopy(c, 0, d, 10, 5);
    assertEquals(c[3], d[13]);
    assertEquals(source[3], d[13]);
  }
}
