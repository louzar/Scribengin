package com.neverwinterdp.scribengin.stream;

import java.util.Arrays;

import com.neverwinterdp.scribengin.commitlog.CommitLog;
import com.neverwinterdp.scribengin.commitlog.CommitLogEntry;
import com.neverwinterdp.scribengin.commitlog.InMemoryCommitLog;
import com.neverwinterdp.scribengin.sink.SinkStream;
import com.neverwinterdp.scribengin.source.SourceStream;
import com.neverwinterdp.scribengin.task.Task;
import com.neverwinterdp.scribengin.tuple.Tuple;

public class StreamImpl implements Stream{

  private SourceStream source;
  private SinkStream sink;
  private SinkStream invalidSink;
  private Task task;
  private CommitLog commitLog;
  
  public StreamImpl(SourceStream y, SinkStream z, SinkStream invalidSink, Task t){
    this.source = y;
    this.sink = z;
    this.invalidSink = invalidSink;
    task = t;
    commitLog = new InMemoryCommitLog();
  }

  @Override
  public boolean initStreams() {
    boolean retVal = true;
    if( !this.source.openStream() ){
      retVal = false;
    }
    if( !this.sink.openStream() ){
      retVal = false;
    }
    if( !this.invalidSink.openStream() ){
      retVal = false;
    }
    return retVal;
  }


  @Override
  public boolean closeStreams() {
    boolean retVal = true;
    if( !this.source.closeStream()){
      retVal = false;
    }
    if( !this.sink.closeStream() ){
      retVal = false;
    }
    if( !this.invalidSink.closeStream() ){
      retVal = false;
    }
    return retVal;
  }

  
  @Override
  public boolean processNext() {
    try{
      if(this.source.hasNext()){
        Tuple t = task.execute(this.source.readNext());
        if(t.isInvalidData()){
          t.setInvalidData(true);
          this.invalidSink.writeTuple(t);
        }
        else{
          this.sink.writeTuple(t);
        }
        commitLog.addNextEntry(t.getCommitLogEntry());
      }
      
      
      return true;
    } catch(Exception e){
      e.printStackTrace();
      return false;
    }
  }

  @Override
  public Task getTask(){
    return this.task;
  }

  @Override
  public SinkStream getSinkStream() {
    return this.sink;
  }

  @Override
  public SinkStream getInvalidSink() {
    return this.invalidSink;
  }


  @Override
  public SourceStream getSourceStream() {
    return this.source;
  }
  
  @Override
  public void setInvalidSink(SinkStream s) {
    this.invalidSink = s;
  }
  
  @Override
  public void setSourceStream(SourceStream s) {
    this.source = s;
  }

  @Override
  public void setSink(SinkStream s) {
    this.sink = s;
  }

  @Override
  public void setTask(Task t) {
    this.task = t;
  }

  @Override
  public boolean verifyDataInSink() {
    CommitLogEntry[] commitLogs = this.commitLog.getCommitLogs();
    
    
    boolean isDataValid = true;
    
    for(int i =0; i < commitLogs.length; i++){
      if(!commitLogs[i].isInvalidData()){
        if(!Arrays.equals(
                    this.source.readFromOffset(commitLogs[i].getStartOffset(), commitLogs[i].getEndOffset()), 
                    this.sink.readFromOffset(commitLogs[i].getStartOffset(), commitLogs[i].getEndOffset()))
                  ) {
          isDataValid = false;
          break;
        }
      }
      else{
        if(!Arrays.equals(
                    this.source.readFromOffset(commitLogs[i].getStartOffset(), commitLogs[i].getEndOffset()), 
                    this.invalidSink.readFromOffset(commitLogs[i].getStartOffset(), commitLogs[i].getEndOffset()))
                  ) {
          //isDataValid = false;
          break;
        }
      }
    }
    
    return isDataValid;
  }




}
