package com.neverwinterdp.scribengin.source;

import com.neverwinterdp.scribengin.tuple.Tuple;

public interface SourceStream {
  Tuple readNext();
  
  byte[] readFromOffset(long startOffset, long endOffset);
  
  boolean openStream();
  boolean closeStream();
  boolean hasNext();
  String getName();
}
