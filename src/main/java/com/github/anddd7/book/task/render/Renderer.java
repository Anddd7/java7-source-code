package com.github.anddd7.book.task.render;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.CompletionService;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorCompletionService;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

/**
 * 使用Future+CompletionService对图片进行分步异步下载
 * - 每下载一张图片就渲染一张
 */
public class Renderer implements IRender {

  private final ExecutorService executor = Executors.newFixedThreadPool(100);

  @Override
  @SuppressWarnings("Duplicates")
  public void renderPage(CharSequence source) {
    final List<ImageInfo> imageInfos = scanForImageInfo(source);
    CompletionService<ImageData> completionService = new ExecutorCompletionService<>(executor);


    for (final ImageInfo imageInfo : imageInfos) {
      completionService.submit(new Callable<ImageData>() {
        @Override
        public ImageData call() throws Exception {
          return imageInfo.downloadImage();
        }
      });
    }

    renderText(source);

    try {
      // 从已完成的任务中取图片进行渲染
      for (int i = 0; i < imageInfos.size(); i++) {
        Future<ImageData> future = completionService.take();
        try {
          // 在指定时间内完成
          renderImage(future.get(1000, TimeUnit.NANOSECONDS));
          // renderImage(future.get());
        } catch (TimeoutException e) {
          // 超时
        }
      }
    } catch (InterruptedException e) {
      // 告诉调用线程已中断
      Thread.currentThread().interrupt();
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
