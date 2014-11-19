package com.neverwinterdp.scribengin.sink.s3;


import org.junit.Assert;
import org.junit.Test;

import com.neverwinterdp.scribengin.Record;
import com.neverwinterdp.scribengin.sink.Sink;
import com.neverwinterdp.scribengin.sink.SinkStream;
import com.neverwinterdp.scribengin.sink.SinkStreamWriter;
import com.neverwinterdp.scribengin.sink.s3.Partitionner;
import com.neverwinterdp.scribengin.sink.s3.S3SinkImpl;

public class S3SinkUnitTest {
  Partitionner partitionner;
  @Test
  public void testSink() throws Exception {
    String bucketName = "my-first-s3-bucket-cfa5e5d0-433f-4980-b47e-2ef99c57aa8a";
    String topic ="topicTest";
    int partition =  2;
    int offset = 1;
    partitionner = new Partitionner(bucketName,topic,partition, offset,".txt") ;
    Sink sink = new S3SinkImpl(partitionner) ;
    test(sink);
  }
  
  private void test(Sink sink) throws Exception {
    
    for(int i = 0; i < 2; i++) {
      SinkStream stream = sink.newSinkStream() ;
      SinkStreamWriter streamWriter = stream.getWriter() ;
      for(int j = 0; j  < 10; j++) {
        streamWriter.append(createRecord("key-" + i + "-" + j, 32));
      }
     
      streamWriter.close();
      partitionner.incOffset();
    }
    SinkStream[] streams = sink.getSinkStreams() ;
    Assert.assertEquals(2, streams.length);
    
    sink.close();
  }
  
  private Record createRecord(String key, int size) {
    byte[] data = new byte[size];
    Record record = new Record(key, data) ;
    return record;
  }
}
