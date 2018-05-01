package github.eddy.java7core.io;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

import github.eddy.java7core.io.model.SerializableLine;
import github.eddy.java7core.io.model.SerializablePoint;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import org.junit.Before;
import org.junit.Test;

/**
 * {@link Serializable}
 * - 标识一个对象是否可以序列化 (二进制)
 */
public class SerializableTests {

  private final static String OUTPUT_FILE = "target/outLine.output";

  private SerializablePoint outPoint = new SerializablePoint();
  private SerializableLine outLine = new SerializableLine();

  @Before
  public void before() {
    outPoint.id = 10;
    outPoint.name = "name";

    outLine.id = 1001;
    outLine.valid = true;
    outLine.point = outPoint;
  }

  /**
   * 深拷贝的一个解决方式(不推荐 效率低)
   * * 使用{@link java.io.Serializable}
   * - serializable的类 ,能够将对象序列化为二进制流 ,然后再反序列化生成对象
   */
  @Test
  public void serialize_DeepCloneSolution() throws IOException, ClassNotFoundException {
    ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(OUTPUT_FILE));
    out.writeObject(outLine);
    out.flush();
    out.close();

    ObjectInputStream in = new ObjectInputStream(new FileInputStream(OUTPUT_FILE));
    SerializableLine inLine = (SerializableLine) in.readObject();

    assertNotEquals(outLine, inLine);
    assertEquals(outLine.id, inLine.id);
    assertEquals(outLine.valid, inLine.valid);
    assertNotEquals(outLine.point, inLine.point);

    assertEquals(outLine.point.id, inLine.point.id);
    assertEquals(outLine.point.name, inLine.point.name);
  }
}
