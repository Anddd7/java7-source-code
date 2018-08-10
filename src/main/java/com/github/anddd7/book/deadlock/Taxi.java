package com.github.anddd7.book.deadlock;

import java.util.HashSet;
import java.util.Set;

public class Taxi {

  private Object location, destination;
  private final Dispatcher dispatcher;

  public Taxi(Dispatcher dispatcher) {
    this.dispatcher = dispatcher;
  }

  public synchronized Object getLocation() {
    return location;
  }

  /**
   * 更新位置: 锁 set -> 锁 dispatcher
   */
  public synchronized void setLocation_0(Object location) {
    this.location = location;
    if (location.equals(destination)) {
      // notify后隐藏了getLocation操作, 需要同步
      dispatcher.notifyAvailable(this);
    }
  }

  /**
   * 更新位置: 锁 set 释放 -> 锁 dispatcher 释放
   * - 避免同时获得多个锁
   */
  public void setLocation(Object location) {
    boolean reachedDestination = false;
    synchronized (this) {
      this.location = location;
      reachedDestination = location.equals(destination);
    }
    if (reachedDestination) {
      dispatcher.notifyAvailable(this);
    }
  }


  private class Dispatcher {

    private final Set<Taxi> taxis;
    private final Set<Taxi> availableTaxis;

    public Dispatcher(Set<Taxi> taxis, Set<Taxi> availableTaxis) {
      this.taxis = new HashSet<>();
      this.availableTaxis = new HashSet<>();
    }

    public synchronized void notifyAvailable(Taxi taxi) {
      availableTaxis.add(taxi);
    }

    /**
     * 重绘地图: 锁 dispatcher -> 锁 set
     */
    public synchronized Object getImage_0() {
      Object image = new Object();
      for (Taxi taxi : taxis) {
        // 遍历, 需要同步
        taxi.getLocation();
        // do with taxi
      }
      return image;
    }

    /**
     * 重绘地图: 锁 dispatcher 释放 -> 锁 set 释放
     * - 避免多锁
     */
    public Object getImage() {
      Set<Taxi> copyTaxis;
      synchronized (this) {
        copyTaxis = new HashSet<>(taxis);
      }

      Object image = new Object();
      for (Taxi taxi : copyTaxis) {
        taxi.getLocation();
        // do with taxi
      }
      return image;
    }
  }
}
