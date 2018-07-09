package com.github.anddd7.jdk7.util;

import java.util.Iterator;
import java.util.ListIterator;

/**
 * @author Anddd7
 */
public class MyIterable implements Iterable<Integer> {

  private int[] parameters;

  public MyIterable(int[] parameters) {
    this.parameters = parameters;
  }

  public boolean contains(int i) {
    for (int parameter : parameters) {
      if (parameter == i) {
        return true;
      }
    }
    return false;
  }

  public void setParameters(int[] parameters) {
    this.parameters = parameters;
  }

  public int size(){
    return parameters.length;
  }

  @Override
  public Iterator<Integer> iterator() {
    return new MyIterator();
  }

  public ListIterator<Integer> listIterator() {
    return new MyListIterator();
  }

  public class MyIterator implements Iterator<Integer> {

    int pos = 0;

    @Override
    public boolean hasNext() {
      return pos < parameters.length;
    }

    @Override
    public Integer next() {
      return parameters[pos++];
    }

    @Override
    public void remove() {
      int[] replace = new int[parameters.length - 1];
      if (pos > 0) {
        System.arraycopy(parameters, 0, replace, 0, pos);
      }
      if (pos < parameters.length) {
        System.arraycopy(parameters, pos + 1, replace, pos, parameters.length - pos - 1);
      }
      parameters = replace;
    }
  }

  public class MyListIterator extends MyIterator implements ListIterator<Integer> {

    @Override
    public boolean hasPrevious() {
      return pos > 0;
    }

    @Override
    public Integer previous() {
      return parameters[--pos];
    }

    @Override
    public int nextIndex() {
      return pos + 1;
    }

    @Override
    public int previousIndex() {
      return pos - 1;
    }

    @Override
    public void set(Integer integer) {
      parameters[pos] = integer;
    }

    @Override
    public void add(Integer integer) {
      int[] replace = new int[parameters.length + 1];
      if (pos > 0) {
        System.arraycopy(parameters, 0, replace, 0, pos);
      }
      replace[pos] = integer;
      if (pos < parameters.length) {
        System.arraycopy(parameters, pos, replace, pos + 1, parameters.length - pos);
      }
      parameters = replace;
    }
  }
}
