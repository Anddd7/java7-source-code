package com.github.anddd7.jdk7.lang;

import static java.lang.Character.isHighSurrogate;
import static java.lang.Character.isLowSurrogate;
import static java.lang.Character.toCodePoint;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

/**
 * @author Anddd7
 *
 * 字符包装类
 * Java默认使用 Unicode字符集 + UTF-16编码, 即:
 * 1 char = 2 byte = 1 unicode
 */
public class CharacterTests {

  /**
   * TODO
   */
  public CharacterTests() {
  }

  /**
   * {@link Character#codePointAtImpl(char[], int, int)}
   * - 获取指定位置的unicode编码
   */
  static int codePointAtImpl(char[] a, int index, int limit) {
    char c1 = a[index++];
    // 当前编码可能是扩展编码: c1是高位
    if (isHighSurrogate(c1)) {
      if (index < limit) {
        char c2 = a[index];
        // 当前编码可能是扩展编码: c2是低位
        if (isLowSurrogate(c2)) {
          // 基于扩展编码进行输出
          return toCodePoint(c1, c2);
        }
      }
    }
    return c1;
  }

  @Test
  public void codePointAtImpl() {
    char[] c = {'a', 'b', '测', '试'};
    assertEquals(97, Character.codePointAt(c, 0));
    assertEquals(98, Character.codePointAt(c, 1));
    assertEquals(27979, Character.codePointAt(c, 2));
    assertEquals(35797, Character.codePointAt(c, 3));
    assertEquals(c[0], (char) 97);
    assertEquals(c[1], (char) 98);
    assertEquals(c[2], (char) 27979);
    assertEquals(c[3], (char) 35797);
  }
  static final int ERROR = 0xFFFFFFFF;

  /**
   * {@link Character#isHighSurrogate(char)}
   * {@link Character#isLowSurrogate(char)}
   * {@link Character#codePointCount(char[], int, int)}
   * - Surrogate 辅助平面字符, 即扩展的字符, 需要使用2个char来表示字符, 分高低位
   */
  @Test
  public void surrogate() {
    char[] simple = Character.toChars('a');
    assertEquals(1, simple.length);
    assertEquals(1, Character.codePointCount(simple, 0, simple.length));

    //特殊字符 𝌆
    char[] special = Character.toChars(0x1D306);
    assertEquals(2, special.length);
    assertTrue(isHighSurrogate(special[0]));
    assertTrue(isLowSurrogate(special[1]));
    // 但是只有一个unicode码
    assertEquals(1, Character.codePointCount(special, 0, special.length));
  }
}
