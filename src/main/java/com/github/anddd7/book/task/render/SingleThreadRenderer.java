package com.github.anddd7.book.task.render;

import java.util.ArrayList;
import java.util.List;

/**
 * 串行的渲染页面
 * - 下载图片会耗费大量时间, 直到下载完成后才能进行渲染
 */
public class SingleThreadRenderer implements IRender {

  @Override
  public void renderPage(CharSequence source) {
    renderText(source);
    List<ImageData> imageData = new ArrayList<>();
    for (ImageInfo imageInfo : scanForImageInfo(source)) {
      imageData.add(imageInfo.downloadImage());
    }
    for (ImageData data : imageData) {
      renderImage(data);
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
