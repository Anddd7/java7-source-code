package github.eddy.java7core.util;

import java.util.Comparator;
import java.util.Objects;

/**
 * @author Anddd7
 *
 * {@link Objects}
 * 主要用来预防空指针
 */
public class ObjectsTests {

  /**
   * {@link Objects#equals(Object, Object)}
   * {@link Objects#hashCode(Object)}
   * {@link Objects#toString(Object)}
   * - 用来避免Object为空时 ,object.equals等的空指针异常
   *
   * {@link Objects#compare(Object, Object, Comparator)}
   * - 使用比较器进行对象比较
   *
   * {@link Objects#requireNonNull(Object, String)}
   * - null则抛出NullPointerException
   */
  public ObjectsTests() {
  }
}
