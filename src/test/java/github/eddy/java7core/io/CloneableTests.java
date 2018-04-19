package github.eddy.java7core.io;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

import org.junit.Before;
import org.junit.Test;

/**
 * {@link Cloneable}
 * - 标识一个对象是否可以使用{@link #clone()}方法
 *
 * 一个深度Cloneable方法 ：序列化实验 {@link SerializableTests}
 * * {@link Cloneable}
 */
public class CloneableTests {

  private Point point = new Point();
  private Line line = new Line();

  @Before
  public void before() {
    point.id = 10;
    point.name = "name";

    line.id = 1001;
    line.valid = true;
    line.point = point;
  }

  /**
   * 浅拷贝shallow clone
   * - 基本数据类型 : 因为基本类型是作为值直接存在栈中
   * - 引用类型 : 直接复制引用到新的对象 ,即内部引用还是同一个
   */
  @Test
  public void shallowClone_ShouldReturnDifferentBasicAndSameReferenceFields()
      throws CloneNotSupportedException {
    Line cloneLine = (Line) line.clone();
    line.point.id = 11;

    assertNotEquals(line, cloneLine);
    assertEquals(line.id, cloneLine.id);
    assertEquals(line.point, cloneLine.point);
    assertEquals(line.valid, cloneLine.valid);
    assertEquals(line.point.id, cloneLine.point.id);
  }

  @Test
  public void deepClone_ShouldReturnDifferentFields() throws CloneNotSupportedException {
    Line cloneLine = line.deepClone();
    line.point.id = 11;

    assertNotEquals(line, cloneLine);
    assertEquals(line.id, cloneLine.id);
    assertNotEquals(line.point, cloneLine.point);
    assertEquals(line.valid, cloneLine.valid);
    assertNotEquals(line.point.id, cloneLine.point.id);
  }

  class Point implements Cloneable {

    long id;
    String name;

    @Override
    protected Object clone() throws CloneNotSupportedException {
      return super.clone();
    }
  }

  class Line implements Cloneable {

    long id;
    Point point;
    Boolean valid;

    @Override
    protected Object clone() throws CloneNotSupportedException {
      return super.clone();
    }

    Line deepClone() throws CloneNotSupportedException {
      Line cloneObject = (Line) super.clone();
      cloneObject.point = (Point) point.clone();
      return cloneObject;
    }
  }
}
