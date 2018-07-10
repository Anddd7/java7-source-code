package com.github.anddd7.book.vehicle;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * 发布: 通过复制数据保证对外发布的对象不会影响其本身
 * 原子性: 对入口操作进行同步
 *
 * 因此, 即使内部状态(Map 和 MutablePoint)是非线程安全的对象, 也能保证线程安全
 */
public class MonitorVehicleTracker {

  /**
   * 保证Map对象的不可变: 引用/元素引用
   * 但元素对象内部的属性可变
   */
  private final Map<String, MutablePoint> locations;

  private static Map<String, MutablePoint> deepCopy(Map<String, MutablePoint> m) {
    Map<String, MutablePoint> result = new HashMap<>();
    for (String id : m.keySet()) {
      result.put(id, new MutablePoint(m.get(id)));
    }
    return Collections.unmodifiableMap(result);
  }

  /**
   * 保证Map对象不会受外部状态变化而变化
   */
  public MonitorVehicleTracker(Map<String, MutablePoint> locations) {
    this.locations = deepCopy(locations);
  }

  /**
   * 不对外发布内部变量
   */
  public synchronized Map<String, MutablePoint> getLocations() {
    return deepCopy(locations);
  }

  /**
   * 不对外发布内部变量(容器内的)
   */
  public synchronized MutablePoint getLocation(String id) {
    MutablePoint loc = locations.get(id);
    return loc == null ? null : new MutablePoint(loc);
  }

  /**
   * 保证对内部变量的原子修改
   */
  public synchronized void setLocation(String id, int x, int y) {
    MutablePoint loc = locations.get(id);
    if (loc == null) {
      throw new IllegalArgumentException("No such ID: " + id);
    }
    loc.set(x, y);
  }
}

class MutablePoint {

  private int x, y;

  MutablePoint(MutablePoint point) {
    this.x = point.x;
    this.y = point.y;
  }

  public void set(int x, int y) {
    this.x = x;
    this.y = y;
  }
}