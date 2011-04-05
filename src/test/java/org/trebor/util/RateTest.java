package org.trebor.util;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class RateTest
{
  @Test
  public void basicTest()
  {
    Rate r = new Rate("test", 0, 50, 2);
    r.setTarget(10);

    double aproximatedDistance = 0;
    double aproximatedTime = 0;
    double timeStep = 0.0001;
    while (r.getVelocity() < r.getTarget())
    {
      r.update(timeStep);
      aproximatedTime += timeStep;
      aproximatedDistance += r.getVelocity() * timeStep;
    }
    
    System.out.println("rate: " + r);
    System.out.println("aproximated     time: " + aproximatedTime);
    System.out.println("aproximated distance: " + aproximatedDistance);
    
    double computedTime1 = r.timeBetween(0, 10);
    double computedTime2 = r.timeBetween(10, 0);
    double computedDistance1 = r.distanceBetween(0, 10);
    double computedDistance2 = r.distanceBetween(10, 0);
    
    System.out.println("computed      time 1: " + computedTime1);
    System.out.println("computed      time 2: " + computedTime2);
    System.out.println("computed  distance 1: " + computedDistance1);
    System.out.println("computed  distance 2: " + computedDistance2);
    
    double computedTimeInDistance = r.timeIn(aproximatedDistance, 0);
    double computedVelocityInDistance = r.velocityIn(aproximatedDistance, 0);
    System.out.println("computed time  in distance: " + computedTimeInDistance);
    System.out.println("computed velcy in distance: " + computedVelocityInDistance);
    
    assertEquals(aproximatedTime, computedTime1, 0.001);
    assertEquals(-aproximatedTime, computedTime2, 0.001);
    assertEquals(aproximatedDistance, r.distanceBetween(0, 10), 0.001);
    assertEquals(-aproximatedDistance, r.distanceBetween(10, 0), 0.001);
    assertEquals(aproximatedTime, computedTimeInDistance, 0.001);
    assertEquals(r.getVelocity(), computedVelocityInDistance, 0.001);
  }
}
