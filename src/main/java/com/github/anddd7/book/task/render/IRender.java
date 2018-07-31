package com.github.anddd7.book.task.render;

import java.util.List;

public interface IRender {

  void renderPage(CharSequence source);

  void renderText(CharSequence source);

  void renderImage(ImageData data);

  List<ImageInfo> scanForImageInfo(CharSequence source);
}
