package com.hk.activity;

import java.util.Arrays;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.Semaphore;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.locks.ReentrantLock;

public class World {
  public static void main(String[] arg) {
    var es = (ThreadPoolExecutor)Executors.newFixedThreadPool(2);
    es.execute(new Runnable() {
      @Override
      public void run() {
        System.out.println("hello world");
      }
    });
    System.out.println("-------------> " + es.getClass().getSimpleName());
    //while (!es.) {
      try {
        Thread.sleep(2000);
      } catch (Exception e) {
        e.printStackTrace();
      }
    //}
    es.shutdown();
  }
}
