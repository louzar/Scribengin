package com.neverwinterdp.scribengin.readers;

import static org.junit.Assert.assertEquals;

import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.util.Properties;

import kafka.javaapi.message.ByteBufferMessageSet;
import kafka.message.MessageAndOffset;
import kafka.producer.KeyedMessage;
import kafka.producer.ProducerConfig;
import kafka.javaapi.producer.Producer;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import com.neverwinterdp.queuengin.kafka.SimplePartitioner;
import com.neverwinterdp.scribengin.KafkaClusterBuilder.KafkaClusterBuilder;


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
  public void testRead() {
    
    //Write numOfMessages to Kafka
    int numOfMessages = 100 ;
    
    Properties producerProps = new Properties();
    producerProps.put("metadata.broker.list", "localhost:9092");
    producerProps.put("serializer.class", "kafka.serializer.StringEncoder");
    producerProps.put("partitioner.class", SimplePartitioner.class.getName());
    producerProps.put("request.required.acks", "1");
    
    Producer<String, String> producer = new Producer<String, String>(new ProducerConfig(producerProps));
    for(int i =0 ; i < numOfMessages; i++) {
      KeyedMessage<String, String> data = new KeyedMessage<String, String>(KafkaClusterBuilder.TOPIC,"Neverwinter"+Integer.toString(i));
      producer.send(data);
    }
    producer.close();
    
    
    //Read back those messages and ensure the payload is correct
    Properties p = new Properties();
    p.put("kafka.topic", KafkaClusterBuilder.TOPIC);
    p.put("kafka.partition", "0");
    p.put("kafka.port", "9092");
    p.put("kafka.broker.list", "localhost");
    p.put("kafka.buffer.size","1024");
    
    reader = new KafkaReader(p);
    ByteBufferMessageSet read = reader.read();
    
    int count =0;
    for (MessageAndOffset messageAndOffset : read) {
      ByteBuffer payload = messageAndOffset.message().payload();
      byte [] bytes = new byte[payload.limit()];
      payload.get(bytes);
      String x = new String(bytes, Charset.forName("UTF-8"));
      assertEquals("Neverwinter"+Integer.toString(count++),x);
    }
    assertEquals("Number of messages sent to Kafka did not match number of messages read in", numOfMessages,count);
  }


  //@Test
  //public void execute() throws Exception {

  //}
}
