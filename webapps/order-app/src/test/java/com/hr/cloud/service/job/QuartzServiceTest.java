package com.hr.cloud.service.job;

import com.hr.cloud.OrderApp;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.quartz.Scheduler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.scheduling.quartz.SchedulerFactoryBean;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

/**
 * Created by hurui on 2018/1/27.
 */
@SpringBootTest
@RunWith(SpringJUnit4ClassRunner.class)
@Import(OrderApp.class)
public class QuartzServiceTest {
  
  @Autowired
  private QuartzService quartzService;
  
  @Autowired
  private Scheduler scheduler;
  
  @Autowired
  private SchedulerFactoryBean schedulerFactoryBean;
  
  @Test
  public void init() throws Exception {
    System.err.println(scheduler.getSchedulerName());
    System.err.println(schedulerFactoryBean.getScheduler().getSchedulerName());
    System.err.println(scheduler == schedulerFactoryBean.getScheduler());
  }
  
}