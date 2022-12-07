package com.winova.demo;

import com.winova.demo.HelloApp;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import static org.junit.Assert.assertTrue;

@RunWith(JUnit4.class)
public class HelloAppTest {

    @Test
    public void sum(){
        System.out.println("############################# ");
        System.out.println("### Java tests Running#### ");
        assertTrue(HelloApp.sum(1,2) == 3);
    }
}
