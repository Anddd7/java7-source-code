package com.github.anddd7.book.vehicle;

import java.util.Collections;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * 发布: 对外发布的都是线程安全的对象
 * -  不可变 且 线程安全 的容器 - unmodifiableMap
 * -  不可变 的元素 - Point
 * 原子性: 由ConcurrentMap的操作所保证
 *
 * 因此, 这个类也是线程安全的, 同时性能更优越
 */
public class DelegatingVehicleTracker {

  private final ConcurrentMap<String, Point> locations;
  private final Map<String, Point> unmodifiableMap;

  /**
   * 塑造 不可变 且 线程安全 的容器
   */
  public DelegatingVehicleTracker(Map<String, Point> points) {
    this.locations = new ConcurrentHashMap<>(points);
    this.unmodifiableMap = Collections.unmodifiableMap(locations);
  }

  /**
   * unmodifiableMap 是线程安全的, 直接返回不用同步
   */
  public Map<String, Point> getLocations() {
    return unmodifiableMap;
  }

  /**
   * 由ConcurrentMap保证同步性
   */
  public Point getLocation(String id) {
    return locations.get(id);
  }

  /**
   * 由ConcurrentMap保证同步性
   */
  public void setLocation(String id, int x, int y) {
    if (locations.replace(id, new Point(x, y)) == null) {
      throw new IllegalArgumentException(String.format("invalid vehicle name: %s", id));
    }
  }
}

class Point {

  private final int x;
  private final int y;

  Point(int x, int y) {
    this.x = x;
    this.y = y;
  }
}