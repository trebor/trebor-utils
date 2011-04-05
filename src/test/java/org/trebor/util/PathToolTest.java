package org.trebor.util;

import static org.junit.Assert.assertEquals;

import java.awt.Shape;
import java.awt.geom.Arc2D;

import org.junit.Test;
import org.trebor.util.PathTool.PathPoint;

public class PathToolTest
{
  @Test
  public void basicTest()
  {
    final Shape arc = new Arc2D.Double(0d, 0d, 10, 10, 0, 180, Arc2D.OPEN);
    PathTool pt = new PathTool(arc, 0);
    
    PathPoint start = pt.getStartPoint();
    PathPoint mid = pt.getPathPoint(pt.getLength() / 2);
    PathPoint end = pt.getEndPoint();
    
    assertEquals(10, start.getX(), 0.001);
    assertEquals( 5, start.getY(), 0.001);
    assertEquals( 5, mid  .getX(), 0.001);
    assertEquals( 0, mid  .getY(), 0.001);
    assertEquals( 0, end  .getX(), 0.001);
    assertEquals( 5, end  .getY(), 0.001);
    
    assertEquals(180, start.getAngle().as(Angle.Type.HEADING), 0.1);
    assertEquals(270, mid  .getAngle().as(Angle.Type.HEADING), 0.1);
    assertEquals(360, end  .getAngle().as(Angle.Type.HEADING), 0.1);
    
    assertEquals(Math.PI * 5, pt.getLength(), 0.01);
  }
}
