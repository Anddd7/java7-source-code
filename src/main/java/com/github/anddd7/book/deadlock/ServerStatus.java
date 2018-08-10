package com.github.anddd7.book.deadlock;

import java.util.Set;

public class ServerStatus {

  public final Set<String> users;
  public final Set<String> queries;

  public ServerStatus(Set<String> users, Set<String> queries) {
    this.users = users;
    this.queries = queries;
  }

  /**
   * 不会相互影响的方法也共用同一个锁, 造成无谓的争抢
   */
  public synchronized void addUser_0(String user) {
    users.add(user);
  }

  public synchronized void addQuery_0(String query) {
    queries.add(query);
  }

  public synchronized void removeUser_0(String user) {
    users.remove(user);
  }

  public synchronized void removeQuery_0(String query) {
    queries.remove(query);
  }

  /**
   * 分离锁
   */
  public void addUser(String user) {
    synchronized (users) {
      users.add(user);
    }
  }

  public void addQuery(String query) {
    synchronized (queries) {
      queries.add(query);
    }
  }

  public void removeUser(String user) {
    synchronized (users) {
      users.remove(user);
    }
  }

  public void removeQuery(String query) {
    synchronized (queries) {
      queries.remove(query);
    }
  }
}
