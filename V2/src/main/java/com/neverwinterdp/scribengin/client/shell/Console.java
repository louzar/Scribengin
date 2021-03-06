package com.neverwinterdp.scribengin.client.shell;

import java.io.IOException;


public class Console {
  private Appendable out ;
  
  public Console() {
    this.out = System.out ;
  }
  
  public Console(Appendable out) {
    this.out = out ;
  }
  
  public void print(String line) throws IOException {
    out.append(line);
  }
  
  public void println(String line) throws IOException {
    out.append(line).append('\n');
  }
}
