package github.eddy.java7core.util;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import github.eddy.java7core.util.mockModels.MyIterable;
import github.eddy.java7core.util.mockModels.MyIterable.MyListIterator;
import java.util.ListIterator;
import org.junit.Test;

/**
 * @author Anddd7
 * @see ListIterator
 * 扩展 Iterable 的功能 ,包括向前遍历 ,获取元素位置 index , 用于 List 组件
 */
@SuppressWarnings("unchecked")
public class ListIteratorTests extends IteratorTests {

  private final MyIterable iterable = new MyIterable(new int[]{1, 2, 3});

  @Test
  public void listIterator_ShouldReturnMyListIteratorAndContain3Integers() {
    ListIterator listIterator = iterable.listIterator();

    assertTrue(listIterator instanceof MyListIterator);

    assertTrue(listIterator.hasNext());
    assertFalse(listIterator.hasPrevious());

    assertEquals(1, listIterator.next());
    assertEquals(2, listIterator.next());
    assertEquals(3, listIterator.next());

    assertTrue(listIterator.hasPrevious());
    assertFalse(listIterator.hasNext());

    assertEquals(3, listIterator.previous());
    assertEquals(2, listIterator.previous());
    assertEquals(1, listIterator.previous());

    assertTrue(listIterator.hasNext());
    assertFalse(listIterator.hasPrevious());
  }

  @Test
  public void iterator_ShouldReturnMyIteratorAndCanAddRemove() {
    ListIterator listIterator = iterable.listIterator();

    assertTrue(listIterator instanceof MyListIterator);

    assertEquals(1, listIterator.next());
    listIterator.add(4);
    assertEquals(4, listIterator.next());
    assertEquals(2, listIterator.next());
    listIterator.remove();
    assertFalse(listIterator.hasNext());
  }
}
