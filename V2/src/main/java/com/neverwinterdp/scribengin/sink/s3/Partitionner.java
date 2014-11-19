/**
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.neverwinterdp.scribengin.sink.s3;

import org.apache.commons.lang.StringUtils;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * LogFilePath represents path of a log file. It contains convenience method for
 * building and decomposing paths.
 *
 * Log file path has the following form: prefix/topic/partition1/.../partitionN/
 * generation_kafkaParition_firstMessageOffset where: prefix is top-level
 * directory for log files. It can be a local path or an s3 dir, topic is a
 * kafka topic, partition1, ..., partitionN is the list of partition names
 * extracted from message content. E.g., the partition may describe the message
 * date such as dt=2014-01-01, generation is the consumer version. It allows up
 * to perform rolling upgrades of non-compatible Secor releases, kafkaPartition
 * is the kafka partition of the topic, firstMessageOffset is the offset of the
 * first message in a batch of files committed atomically.
 *
 * @author Pawel Garbacki (pawel@pinterest.com)
 */
public class Partitionner {
  private String mTopic;
  private int mKafkaPartition;
  private long mOffset;
  private String mBucketName;
  private String mExtension;

  // this one
  public Partitionner(String bucketName, String topic, int kafkaPartition, long offset, String extension) {
    this.mBucketName = bucketName;
    mTopic = topic;
    mKafkaPartition = kafkaPartition;
    mOffset = offset;
    mExtension = extension;

  }

  public String getLogFilePath() {

    ArrayList<String> pathElements = new ArrayList<String>();
    pathElements.add(mBucketName);
    pathElements.add(mTopic);

    return StringUtils.join(pathElements, "/");
  }

  public String getLogFileBasename() {
    ArrayList<String> basenameElements = new ArrayList<String>();
    basenameElements.add(Integer.toString(mKafkaPartition));
    basenameElements.add(String.format("%020d", mOffset));
    return StringUtils.join(basenameElements, "_") + mExtension;
  }

  public void incOffset() {
    mOffset++;
  }

}
