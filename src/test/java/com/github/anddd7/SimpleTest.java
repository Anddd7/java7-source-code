package com.github.anddd7;

import java.util.Arrays;
import java.util.Comparator;
import org.junit.Test;

public class SimpleTest {

  @Test
  public void test() {
    String[] input = new String[]{
        "100", "100-A", "100-B", "100-C",
        "100-1", "100-11", "100-2",
        "100-21", "99-A", "99-10",
        "171A", "171B", "171C", "171ABCD"
    };

    String[] sorted = Arrays.copyOf(input, input.length);
    Arrays.sort(sorted);
    System.out.println(Arrays.toString(sorted));

    String[] customerSorted = Arrays.copyOf(input, input.length);
    Arrays.sort(customerSorted, new Comparator<String>() {
      @Override
      public int compare(String o1, String o2) {
        String[] parts1 = o1.split("-");
        String[] parts2 = o2.split("-");
        for (int i = 0; i < Math.min(parts1.length, parts2.length); i++) {
          int lengthCompare = Integer.compare(parts1[i].length(), parts2[i].length());
          if (lengthCompare != 0) {
            return lengthCompare;
          }
          int stringCompare = parts1[i].compareTo(parts2[i]);
          if (stringCompare != 0) {
            return stringCompare;
          }
        }
        return 0;
      }
    });
    System.out.println(Arrays.toString(customerSorted));
  }
}
