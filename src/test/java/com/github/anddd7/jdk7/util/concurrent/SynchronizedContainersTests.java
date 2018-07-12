package com.github.anddd7.jdk7.util.concurrent;

import java.util.Random;
import java.util.Vector;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import org.junit.Test;

public class SynchronizedContainersTests {

  Vector<Integer> buildVector(int number) {
    Vector<Integer> vector = new Vector<>();
    vector.add(number);
    vector.add(number);
    vector.add(number);
    return vector;
  }

  @Test(expected = ArrayIndexOutOfBoundsException.class)
  public void deleteLast_throwArrayIndexOutOfBounds() {
    final ExecutorService executor = Executors.newSingleThreadExecutor();
    final Random random = new Random();
    while (true) {
      final Vector<Integer> vector = buildVector(random.nextInt());
      executor.submit(new Runnable() {
        @Override
        public void run() {
          SynchronizedContainers.deleteLastUnsafe(vector);
        }
      });
      SynchronizedContainers.getLastSafe(vector);
    }
  }
}
