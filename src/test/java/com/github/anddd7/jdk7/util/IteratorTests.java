package com.github.anddd7.jdk7.util;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import com.github.anddd7.jdk7.util.MyIterable.MyIterator;
import java.util.Iterator;
import org.junit.Test;

/**
 * @author Anddd7
 * @see Iterator
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

  @Test
  public void iterator_ShouldContain4IntegersAfterSetParameters() {
    Iterator iterator = iterable.iterator();
    assertEquals(3, iterable.size());

    iterable.setParameters(new int[]{4, 3, 2, 1});
    assertEquals(4, iterable.size());

    assertTrue(iterator.hasNext());
    assertEquals(4, iterator.next());
    assertEquals(3, iterator.next());
    assertEquals(2, iterator.next());
    assertEquals(1, iterator.next());
    assertFalse(iterator.hasNext());
  }
}
