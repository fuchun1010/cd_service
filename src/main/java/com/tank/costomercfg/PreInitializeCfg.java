package com.tank.costomercfg;

import com.tank.client.Client;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author tank198435163.com
 */
@Slf4j
@Configuration
public class PreInitializeCfg {

  @Bean
  public CommandLineRunner init() {
    return args -> {
      this.client.sayHello(this.appName);
      new Thread(client, String.format("%s-client-thread", this.appName)).start();
    };
  }

  @Value("${spring.name}")
  private String appName;

  @Autowired
  private Client client;

}
