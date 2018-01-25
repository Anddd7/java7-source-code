package github.eddy.java7core.io;

import github.eddy.java7core.io.model.SerializableLine;
import github.eddy.java7core.io.model.SerializablePoint;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

/**
 * @author and777
 * @date 2018/1/12
 *
 * {@link Serializable}
 * - 标识一个对象是否可以序列化 (二进制)
 *
 * {@link Cloneable}
 * - 标识一个对象是否可以使用{@link #clone()}方法
 */
public class TestCloneableAndSerializable {

  Point point = new Point();
  Line line = new Line();

  SerializablePoint serializablePoint = new SerializablePoint();
  SerializableLine serializableLine = new SerializableLine();

  @Before
  public void before() {
    point.id = 10;
    point.name = "name";

    line.id = 1001;
    line.valid = true;
    line.point = point;

    serializablePoint.id = 10;
    serializablePoint.name = "name";

    serializableLine.id = 1001;
    serializableLine.valid = true;
    serializableLine.point = serializablePoint;
  }


  class Point implements Cloneable {

    long id;
    String name;

    @Override
    protected Object clone() throws CloneNotSupportedException {
      return super.clone();
    }
  }

  @Test
  public void cloneTest() throws CloneNotSupportedException {
    Point point1 = (Point) point.clone();

    Assert.assertNotEquals(point, point1);//对象不同
    Assert.assertEquals(point1.id, point.id);//基本类型直接复制值
    Assert.assertEquals(point1.name, point.name);
  }

  class Line implements Cloneable {

    long id;
    Point point;
    Boolean valid;

    @Override
    protected Object clone() throws CloneNotSupportedException {
      return super.clone();
    }
  }

  /**
   * 浅拷贝shallow clone
   * 如{@link TestCloneableAndSerializable#cloneTest(), TestCloneableAndSerializable#shallowCloneTest()}所示
   * - 基本数据类型 : 因为基本类型是作为值直接存在栈中
   * - 引用类型 : 直接复制引用到新的对象 ,即内部引用还是同一个
   */
  @Test
  public void shallowCloneTest() throws CloneNotSupportedException {
    Line line1 = (Line) line.clone();
    Assert.assertNotEquals(line, line1);
    Assert.assertEquals(line.id, line1.id);
    Assert.assertEquals(line.point, line1.point);//引用类型相同
    Assert.assertEquals(line.valid, line1.valid);
  }


  /**
   * 深拷贝
   * * 重写clone方法 ,对内部所有引用类型进行clone操作
   * - 缺点 : 需要所有类型都实现cloneable
   * * 使用{@link java.io.Serializable}
   * - serializable的类 ,能够将对象序列化为二进制流 ,然后再反序列化生成对象
   */
  @Test
  public void deepCloneTest() throws IOException, ClassNotFoundException {
    ObjectOutputStream out = new ObjectOutputStream(
        new FileOutputStream("target/serializableLine.output"));
    out.writeObject(serializableLine);
    out.flush();
    out.close();

    ObjectInputStream in = new ObjectInputStream(
        new FileInputStream("target/serializableLine.output"));
    SerializableLine serializableLine1 = (SerializableLine) in.readObject();

    Assert.assertNotEquals(serializableLine, serializableLine1);
    Assert.assertEquals(serializableLine.id, serializableLine1.id);
    Assert.assertNotEquals(serializableLine.point, serializableLine1.point);//引用类型不同
    Assert.assertEquals(serializableLine.valid, serializableLine1.valid);
  }
}
