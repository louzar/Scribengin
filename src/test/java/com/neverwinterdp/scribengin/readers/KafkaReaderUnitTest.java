package com.neverwinterdp.scribengin.readers;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import kafka.javaapi.message.ByteBufferMessageSet;

import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import com.neverwinterdp.message.Message;
import com.neverwinterdp.queuengin.MetricsConsumerHandler;
import com.neverwinterdp.queuengin.kafka.KafkaMessageConsumerConnector;
import com.neverwinterdp.queuengin.kafka.KafkaMessageProducer;
import com.neverwinterdp.scribengin.KafkaClusterBuilder.KafkaClusterBuilder;
import com.neverwinterdp.scribengin.api.Source;
import com.neverwinterdp.util.monitor.ApplicationMonitor;
import com.neverwinterdp.util.monitor.ComponentMonitor;
import com.neverwinterdp.util.monitor.snapshot.MetricFormater;


public class KafkaReaderUnitTest {
  static KafkaClusterBuilder clusterBuilder ;
  static KafkaReader reader;
  
  @BeforeClass
  static public void setup() throws Exception {
    clusterBuilder = new KafkaClusterBuilder() ;
    clusterBuilder.install();
  }

  @AfterClass
  static public void teardown() throws Exception {
    clusterBuilder.destroy();
  }

  @Test
  public void testRead() throws Exception {
    ApplicationMonitor appMonitor = new ApplicationMonitor("Test", "localhost") ;
    
    int numOfMessages = 100 ;
    ComponentMonitor producerMonitor = appMonitor.createComponentMonitor("KafkaMessageProducer") ;
    Map<String, String> kafkaProducerProps = new HashMap<String, String>() ;
    kafkaProducerProps.put("request.required.acks", "1");
    KafkaMessageProducer producer = new KafkaMessageProducer(kafkaProducerProps, producerMonitor, "127.0.0.1:9092") ;
    for(int i = 0 ; i < numOfMessages; i++) {
      //SampleEvent event = new SampleEvent("event-" + i, "event " + i) ;
      Message message = new Message("m" + i, new byte[1024], false) ;
      producer.send(KafkaClusterBuilder.TOPIC,  message) ;
    }
   
    Thread.sleep(2000) ;
    
    Properties p = new Properties();
    p.put("kafka.topic", KafkaClusterBuilder.TOPIC);
    p.put("kafka.partition", "0");
    p.put("kafka.port", "9092");
    p.put("kafka.broker.list", "localhost");
    p.put("kafka.buffer.size","1048576");
    //
    reader = new KafkaReader(p);
    ByteBufferMessageSet read = reader.read();
    System.err.println("!!!!!!!!!!!!!!!!!!!!!");
    System.err.println(read.toString());
    System.err.println("!!!!!!!!!!!!!!!!!!!!!");
    read = reader.read();
    System.err.println("!!!!!!!!!!!!!!!!!!!!!");
    System.err.println(read.toString());
    System.err.println("!!!!!!!!!!!!!!!!!!!!!");
    read = reader.read();
    System.err.println("!!!!!!!!!!!!!!!!!!!!!");
    System.err.println(read.toString());
    System.err.println("!!!!!!!!!!!!!!!!!!!!!");
    
    producer.close();
  }


  //@Test
  //public void execute() throws Exception {

  //}
}
