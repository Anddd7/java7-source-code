package com.github.anddd7.book.task.render;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

/**
 * 使用Future对图片进行异步下载
 * - "下载图片"能提前进行
 * - 但还是需要等到所有图片下载完成后才进行渲染
 */
public class FutureRenderer implements IRender {

  private final ExecutorService executor = Executors.newSingleThreadExecutor();

  @Override
  @SuppressWarnings("Duplicates")
  public void renderPage(CharSequence source) {
    final List<ImageInfo> imageInfos = scanForImageInfo(source);
    Callable<List<ImageData>> task = new Callable<List<ImageData>>() {
      @Override
      public List<ImageData> call() {
        List<ImageData> imageData = new ArrayList<>();
        for (ImageInfo imageInfo : imageInfos) {
          imageData.add(imageInfo.downloadImage());
        }
        return imageData;
      }
    };
    Future<List<ImageData>> future = executor.submit(task);

    renderText(source);

    try {
      // 当图片下载完成后进行渲染
      for (ImageData data : future.get()) {
        renderImage(data);
      }
    } catch (InterruptedException e) {
      // 告诉调用线程已中断
      Thread.currentThread().interrupt();
      future.cancel(true);
    } catch (ExecutionException e) {
      // 处理其他异常
    }
  }

  @Override
  public void renderText(CharSequence source) {

  }

  @Override
  public void renderImage(ImageData data) {

  }

  @Override
  public List<ImageInfo> scanForImageInfo(CharSequence source) {
    return null;
  }
}
