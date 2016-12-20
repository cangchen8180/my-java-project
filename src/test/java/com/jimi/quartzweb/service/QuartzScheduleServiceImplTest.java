package com.jimi.quartzweb.service;

import org.junit.Test; 
import org.junit.Before; 
import org.junit.After; 
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

/** 
* QuartzScheduleServiceImpl Tester. 
* 
* @author <Authors name> 
* @since <pre>四月 8, 2016</pre> 
* @version 1.0 
*/
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {
        "classpath:spring-engine.xml"})
public class QuartzScheduleServiceImplTest { 

    @Autowired
    QuartzScheduleService QuartzScheduleServiceImpl;

    @Before
    public void before() throws Exception { 
    } 
    
    @After
    public void after() throws Exception { 
    } 
    
        /** 
    * 
    * Method: scheduleJob() 
    * 
    */ 
    @Test
    public void testScheduleJob() throws Exception {
        QuartzScheduleServiceImpl.scheduleJob();
    } 
    
        
    } 
