package com.github.anddd7.book.vehicle;

import java.util.Collections;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * 发布: 对外发布的都是线程安全的对象
 * -  不可变 且 线程安全 的容器 - unmodifiableMap
 * -  可变 但 线程安全(sync) 的元素 - SafePoint
 * 原子性: 由ConcurrentMap的操作所保证
 *
 * @see DelegatingVehicleTracker
 * @see MonitorVehicleTracker
 */
public class PublishingVehicleTracker {

  /**
   * 容器不可变, 但内部元素对象属性可变
   */
  private final ConcurrentMap<String, SafePoint> locations;
  private final Map<String, SafePoint> unmodifiableMap;

  public PublishingVehicleTracker(Map<String, SafePoint> points) {
    this.locations = new ConcurrentHashMap<>(points);
    this.unmodifiableMap = Collections.unmodifiableMap(locations);
  }

  public Map<String, SafePoint> getLatestLocations() {
    return unmodifiableMap;
  }

  public SafePoint getLocation(String id) {
    return locations.get(id);
  }

  /**
   * 和MonitorVehicleTracker中方法不同的是, getLocation/setLocation的同步由ConcurrentMap保证, 因此不需要sync
   *
   * @see MonitorVehicleTracker#setLocation(String, int, int)
   */
  public void setLocation(String id, int x, int y) {
    if (!locations.containsKey(id)) {
      throw new IllegalArgumentException("No such ID: " + id);
    }
    locations.get(id).set(x, y);
  }
}

/**
 * 因为SafePoint具有可变性, 且对外进行了发布(getLocation)
 * 因此会出现 set(called by 'setLocation')/get(called by 'outer method') 的混乱问题, 需要同步
 */
class SafePoint {

  private int x;
  private int y;

  SafePoint(int x, int y) {
    this.x = x;
    this.y = y;
  }


  public synchronized void set(int x, int y) {
    this.x = x;
    this.y = y;
  }

  public synchronized int[] get() {
    return new int[]{x, y};
  }
}