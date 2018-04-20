package github.eddy.java7core.util;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import github.eddy.java7core.util.mockModels.MyIterable;
import github.eddy.java7core.util.mockModels.MyIterable.MyIterator;
import java.util.Iterator;
import org.junit.Test;

/**
 * @author Anddd7
 *
 * {@link Iterator}
 * 结合 Iterable ,实现迭代器用于for语法糖
 */
public class IteratorTests {

  private final MyIterable iterable = new MyIterable(new int[]{1, 2, 3});

  @Test
  public void iterator_ShouldReturnMyIteratorAndContain3Integers() {
    Iterator iterator = iterable.iterator();

    assertTrue(iterator instanceof MyIterator);
    assertTrue(iterator.hasNext());
    assertEquals(1, iterator.next());
    assertEquals(2, iterator.next());
    assertEquals(3, iterator.next());
    assertFalse(iterator.hasNext());
  }

  @Test
  public void iterator_ShouldReturnMyIteratorAndContain2IntegersAfterRemove() {
    Iterator iterator = iterable.iterator();

    assertTrue(iterator instanceof MyIterator);
    assertTrue(iterator.hasNext());
    assertEquals(1, iterator.next());
    iterator.remove();
    assertEquals(3, iterator.next());
    assertFalse(iterator.hasNext());
  }
}
