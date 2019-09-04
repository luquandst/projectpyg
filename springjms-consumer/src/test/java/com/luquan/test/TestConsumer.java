package com.luquan.test;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.io.IOException;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("classpath:spring/applicationContext-*.xml")
public class TestConsumer {

   @Test
    public void testMessage(){
       try {
           System.in.read();
       } catch (IOException e) {
           e.printStackTrace();
       }
   }
}
