package com.hr.cloud;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Created by hurui on 2017/12/25.
 */
@SpringBootApplication
//@EnableAutoConfiguration(exclude = DataSourceAutoConfiguration.class)
//@MapperScan("com.hr.cloud.mapper")
public class CloudApplication {
  
  public static void main(String[] args) {
    SpringApplication.run(CloudApplication.class);
  }
  
}
