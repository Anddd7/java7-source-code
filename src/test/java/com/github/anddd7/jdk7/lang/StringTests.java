package com.github.anddd7.jdk7.lang;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertSame;

import java.util.Locale;
import org.junit.Test;

/**
 * @author Anddd7
 * @see String
 *
 * 字符串, 内部维护一个字符数组
 * 字符串的难点主要在于 unicode编码/增补码 的问题[Java字符类型](https://blog.csdn.net/mazhimazh/article/details/17708001)
 */
public class StringTests {

  private char value[];

  /**
   * {@link String#String(String)}
   * - 通常很少使用 new String(String s) 的构造函数, 用字符串创建字符串很蠢
   *
   * {@link String#String(byte[])}
   * {@link String#String(char[])}
   * - 其他构造函数大多适用于 Byte[] 到 String 的转换
   *
   * {@link String#codePointAt(int)}
   * {@link String#codePointBefore(int)}
   * {@link String#codePointCount(int, int)}
   * - 读取指定位置的unicode编码
   * - codePointCount 和 String.length 不一定相同
   *
   * 因为String内部的字符数组是final的, 因此String运算获得的都是新的String
   */
  public StringTests() {
  }

  /**
   * {@link String#indexOf(char[], int, int, char[], int, int, int)}
   * - 在字符数组中匹配指定字符数组
   */
  static int indexOf(
      // 字符数组 偏移量 字符数组长度
      char[] source, int sourceOffset, int sourceCount,
      char[] target, int targetOffset, int targetCount,
      //搜索起点
      int fromIndex) {
    // 起点大于等于最大长度
    if (fromIndex >= sourceCount) {
      // 目标为空, 匹配数组末尾位置; 非空则匹配失败, 返回-1
      return (targetCount == 0 ? sourceCount : -1);
    }
    // reset起点
    if (fromIndex < 0) {
      fromIndex = 0;
    }
    // 目标为空, 即匹配当前位置
    if (targetCount == 0) {
      return fromIndex;
    }

    // 待匹配的第一个字符
    char first = target[targetOffset];
    // 可能匹配成功的最大坐标
    int max = sourceOffset + (sourceCount - targetCount);

    //在可能成功的范围内, 从fromIndex起点开始匹配(叠加偏移量)
    for (int i = sourceOffset + fromIndex; i <= max; i++) {
      //匹配第一个字符直至成功
      if (source[i] != first) {
        while (++i <= max && source[i] != first) {
          ;
        }
      }

      // 在max范围内检查剩下的字符
      if (i <= max) {
        // source的下一个比对字符
        int j = i + 1;
        // 理想情况下匹配结束的字符位置
        int end = j + targetCount - 1;

        // 匹配source和target的字符, 直到结束或匹配失败
        for (int k = targetOffset + 1;
            j < end && source[j] == target[k];
            j++, k++) {
          ;
        }

        // 检查是否匹配完成
        if (j == end) {
          // 相对于偏移量的index
          return i - sourceOffset;
        }
      }
    }
    return -1;
  }

  /**
   * {@link String#substring(int, int)}
   * - subString 会用 new 创建新字符串, 需要用equals进行比较
   */
  @Test
  public void substring() {
    String s = "i am String";

    assertEquals("i", s.substring(0, 1));
    assertNotSame("i", s.substring(0, 1));

    assertEquals(s.substring(3), s.substring(3));
    assertNotSame(s.substring(3), s.substring(3));
  }

  /**
   * {@link String#toLowerCase()}
   */
  public String toLowerCase(Locale locale) {
    if (locale == null) {
      throw new NullPointerException();
    }

    // 第一个大写字符的坐标
    int firstUpper;
    final int len = value.length;

    scan:
    {
      for (firstUpper = 0; firstUpper < len; ) {
        char c = value[firstUpper];
        // 属于surrogate特殊字符
        if ((c >= Character.MIN_HIGH_SURROGATE) && (c <= Character.MAX_HIGH_SURROGATE)) {
          int supplChar = codePointAt(firstUpper);
          // 转小写成功, 说明已经找到至少一个大写字符
          if (supplChar != Character.toLowerCase(supplChar)) {
            break scan;
          }
          // 当前字符是小写, 跳过当前字符继续检测
          firstUpper += Character.charCount(supplChar);
        } else {
          // 转小写成功
          if (c != Character.toLowerCase(c)) {
            break scan;
          }
          // 跳过
          firstUpper++;
        }
      }
      // 没有找到大写字符 直接退出
      return ""; // return this;
    }

    char[] result = new char[len];
    int resultOffset = 0;
    // copy非大写字符
    System.arraycopy(value, 0, result, 0, firstUpper);

    String lang = locale.getLanguage();
    boolean localeDependent = (lang == "tr" || lang == "az" || lang == "lt");

    char[] lowerCharArray;
    int lowerChar;
    int srcChar;
    int srcCount;
    for (int i = firstUpper; i < len; i += srcCount) {
      srcChar = (int) value[i];

      // 特殊字符处理
      if ((char) srcChar >= Character.MIN_HIGH_SURROGATE
          && (char) srcChar <= Character.MAX_HIGH_SURROGATE) {
        srcChar = codePointAt(i);
        srcCount = Character.charCount(srcChar);
      } else {
        srcCount = 1;
      }

      // 转小写
      if (localeDependent ||
          srcChar == '\u03A3' || // GREEK CAPITAL LETTER SIGMA
          srcChar == '\u0130') { // LATIN CAPITAL LETTER I WITH DOT ABOVE
        // 特殊语言扩展
        lowerChar = -1;// ConditionalSpecialCasing.toLowerCaseEx(this, i, locale);
      } else {
        lowerChar = Character.toLowerCase(srcChar);
      }

      // 转换失败或属于增补字符
      if (lowerChar == CharacterTests.ERROR
          || lowerChar >= Character.MIN_SUPPLEMENTARY_CODE_POINT) {
        if (lowerChar == CharacterTests.ERROR) {
          // 特殊语言扩展
          lowerCharArray = new char[0];// ConditionalSpecialCasing.toLowerCaseCharArray(this, i, locale);
        } else if (srcCount == 2) {
          // 当前特殊字符占2个char
          resultOffset += Character.toChars(lowerChar, result, i + resultOffset) - srcCount;
          continue;
        } else {
          // 正常处理
          lowerCharArray = Character.toChars(lowerChar);
        }

        // 转换前的字符长度小于转换后的, 即string需要扩容
        int mapLen = lowerCharArray.length;
        if (mapLen > srcCount) {
          // 扩容result
          char[] result2 = new char[result.length + mapLen - srcCount];
          System.arraycopy(result, 0, result2, 0, i + resultOffset);
          result = result2;
        }
        // 复制
        for (int x = 0; x < mapLen; ++x) {
          result[i + resultOffset + x] = lowerCharArray[x];
        }
        // 跳过
        resultOffset += (mapLen - srcCount);
      } else {
        // 正常编码, 直接赋值
        result[i + resultOffset] = (char) lowerChar;
      }
    }
    return new String(result, 0, len + resultOffset);
  }

  /**
   * simulation
   */
  public int codePointAt(int index) {
    return 0;
  }

  /**
   * {@link String#equals(Object)}
   * - "string" 创建的字符串是存放在常量池, 使用ldc指令进行读取
   * - new String("string") 则是创建了一个对象
   * - 因此字符串的equals通过重载, 会依次比较内部字符数组是否相同
   */
  @Test
  public void equals() {
    String s1 = "str";
    String s2 = new String("str");
    String s3 = "str";

    // s1在栈中常量池, s2在堆中: 地址不同
    assertNotSame(s1, s2);
    // 同常量池, 同字符常量, 同地址
    assertSame(s1, s3);
    // 重载方法, 比较内部字符数组
    assertEquals(s1, s2);
    assertEquals(s1, s3);
  }
}
