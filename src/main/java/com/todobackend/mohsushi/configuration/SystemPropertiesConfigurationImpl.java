package com.todobackend.mohsushi.configuration;

import java.util.Objects;
import java.util.Properties;

public class SystemPropertiesConfigurationImpl implements Configuration {

  private final Properties props;

  public SystemPropertiesConfigurationImpl(Properties props) {
    this.props = Objects.requireNonNull(props);
  }

  @Override
  public JettyConfiguration jettyConfiguration() {
    Integer port = null;
    if (props.containsKey("server.port")) {
      try {
        port = Integer.parseInt(props.getProperty("server.port"));
      } catch (NumberFormatException nfe) {
        // no-op
      }
    }
    return new JettyConfiguration(port);
  }
}
