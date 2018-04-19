package github.eddy;

/**
 * @author and777
 * @date 2018/1/18
 */
public class Tool {


  static long start;

  public static void start() {
    System.out.println("-----start------");
    start = System.currentTimeMillis();
  }

  public static void timer(String desc) {
    System.out.println("耗时:" + (System.currentTimeMillis() - start) + " | " + desc);
    start = System.currentTimeMillis();
  }
}
