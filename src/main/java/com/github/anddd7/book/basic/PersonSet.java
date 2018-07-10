package com.github.anddd7.book.basic;

import com.github.anddd7.book.annotation.ThreadSafe;
import java.util.HashSet;
import java.util.Set;


/**
 * 虽然内部状态HashSet是线程不安全的
 * 但是对于PersonSet来说, 能够访问内部状态的method都由锁把守, 因此对外表现是线程安全的
 *
 * "装饰器模式"
 *
 * @see java.util.Vector
 * @see java.util.Hashtable
 */
@ThreadSafe
public class PersonSet {

  private final Set<Person> mySet = new HashSet<>();

  synchronized void addPerson(Person person) {
    mySet.add(person);
  }

  synchronized boolean containsPerson(Person person) {
    return mySet.contains(person);
  }

  interface Person {

  }
}
