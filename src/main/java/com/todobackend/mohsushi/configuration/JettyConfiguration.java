package com.todobackend.mohsushi.configuration;

public class JettyConfiguration {
  static final int DEFAULT_PORT = 7000;

  private final int port;

  JettyConfiguration(int port) {
    this.port = port;
  }

  public JettyConfiguration(Integer port) {
    this(port == null ? DEFAULT_PORT : port);
  }

  public int port() {
    return port;
  }

}
