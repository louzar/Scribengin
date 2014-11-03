package com.neverwinterdp.scribengin.dataflow;

import com.neverwinterdp.scribengin.Record;

public interface DataFlowReader {
  public Record next() throws Exception ;
  public void  close() throws Exception ;
}