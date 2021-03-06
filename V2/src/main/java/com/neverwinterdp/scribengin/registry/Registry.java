package com.neverwinterdp.scribengin.registry;

import java.util.List;

public interface Registry {
  public Registry connect() throws RegistryException ;
  public void disconnect() throws RegistryException ;
  
  public String getSessionId()  ;
  
  public Node create(String path, NodeCreateMode mode) throws RegistryException ;
  public Node create(String path, byte[] data, NodeCreateMode mode) throws RegistryException ;
  public <T> Node create(String path, T data, NodeCreateMode mode) throws RegistryException ;
  public Node createIfNotExist(String path) throws RegistryException ;

  public Node get(String path) throws RegistryException ;
  public byte[] getData(String path) throws RegistryException ;
  public void setData(String path, byte[] data) throws RegistryException ;
  public <T> void setData(String path, T data) throws RegistryException ;
  
  public List<String> getChildren(String dir) throws RegistryException ;
  
  public boolean exists(String path) throws RegistryException ;
  
  public void watch(String path, NodeWatcher watcher) throws RegistryException ;
  
  public void delete(String path) throws RegistryException ;
}
