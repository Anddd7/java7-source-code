package github.eddy.java7core.io;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.JarURLConnection;
import java.net.URL;
import java.util.Enumeration;
import java.util.jar.JarFile;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipOutputStream;
import org.junit.Assert;
import org.junit.Test;

/**
 * @author and777
 * @date 2018/2/7
 */
public class TestJarAndZip {

  //"D:\\IDEASpace\\java7-source-code\\";
  static final String projectPath = System.getProperty("user.dir") + File.separator;

  static final String srcPath = projectPath + "src";
  static final String zipPath = projectPath + "target" + File.separator + "src.zip";
  static final String jarPath = projectPath
      + "target" + File.separator + "algorithm-lab-java7-1.0-SNAPSHOT.jar";

  @Test
  public void zipTest() throws IOException {
    ZipOutputStream zipOutputStream = new ZipOutputStream(new FileOutputStream(zipPath));
    //压缩src文件夹
    zip("", new File(srcPath), zipOutputStream);
    //添加额外的一个描述文件
    zipOutputStream.putNextEntry(new ZipEntry("README.txt"));
    zipOutputStream.write("Hello ,ZIP~".getBytes());
    //一定要close ,否则会导致压缩文件 可以解压(里面的文件完整) 但 无法正常打开
    zipOutputStream.close();
  }

  private void zip(String path, File file, ZipOutputStream zipOut) throws IOException {
    if (!file.exists()) {
      return;
    }

    if (file.isFile()) {
      zipOut.putNextEntry(new ZipEntry(path + file.getName()));
      System.out.println(path + file.getName());
      try (InputStream input = new FileInputStream(file)) {
        int temp;
        while ((temp = input.read()) != -1) {
          zipOut.write(temp);
        }
      } catch (Exception e) {
        e.printStackTrace();
      }
      return;
    }

    for (File subFile : file.listFiles()) {
      zip(path + file.getName() + File.separator, subFile, zipOut);
    }
  }

  @Test
  public void unzipTest() throws IOException {
    ZipFile zipFile = new ZipFile(zipPath);
    File outputFile = new File(zipPath.substring(0, zipPath.lastIndexOf(File.separator)));

    Enumeration<ZipEntry> entryEnumeration = (Enumeration<ZipEntry>) zipFile.entries();
    while (entryEnumeration.hasMoreElements()) {
      ZipEntry zipEntry = entryEnumeration.nextElement();
      System.out.println(zipEntry.getName());
      File file = new File(outputFile, zipEntry.getName());
      if (zipEntry.getName().endsWith(File.separator)) {
        file.mkdir();
        continue;
      }

      //创建多级父文件夹
      File parentDir = file.getParentFile();
      if (!parentDir.exists()) {
        parentDir.mkdirs();
      }

      //写文件
      try (InputStream in = zipFile.getInputStream(zipEntry);
          OutputStream out = new FileOutputStream(file)) {
        int temp;
        while ((temp = in.read()) != -1) {
          out.write(temp);
        }
      } catch (Exception e) {
        e.printStackTrace();
      }

    }
    zipFile.close();
  }


  /**
   * 获取Jar
   * - 通过URL.openConnection -> JarURLConnection.getJarFile -> jarFile : 可以访问远程的Jar
   * - 通过 JarInputStream 直接读取文件流的方式
   */
  @Test
  public void getJar() throws IOException {
    URL url = new URL("jar:file:" + jarPath + "!/");
    JarURLConnection jarURLConnection = (JarURLConnection) url.openConnection();
    JarFile jarFile1 = jarURLConnection.getJarFile();

    JarFile jarFile2 = new JarFile(jarPath);

    Assert.assertEquals(jarFile1.getName(), jarFile2.getName());
  }
}
