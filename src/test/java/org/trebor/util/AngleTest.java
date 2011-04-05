package org.trebor.util;

import static java.lang.Math.PI;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.trebor.util.Angle.Type.DEGREES;
import static org.trebor.util.Angle.Type.DEGREE_RATE;
import static org.trebor.util.Angle.Type.HEADING;
import static org.trebor.util.Angle.Type.HEADING_RATE;
import static org.trebor.util.Angle.Type.RADIANS;
import static org.trebor.util.Angle.INTERNAL;

import java.awt.geom.Point2D;

import org.junit.Test;
import org.trebor.util.Angle.Type;

public class AngleTest
{
  @Test
  public void basicTest()
  {
    double[] headings = {            315,      0,     45, 90,         180};
    double[] degrees =  {            135,     90,     45,  0,         270};
    double[] radians =  {PI / 2 + PI / 4, PI / 2, PI / 4,  0, PI + PI / 2};

    // run through all the above data

    for (int i = 0; i < headings.length; ++i)
    {
      double h = headings[i];
      double d = degrees[i];
      double r = radians[i];
      Angle ah = new Angle(h, HEADING);
      Angle ad = new Angle(d, DEGREES);
      Angle ar = new Angle(r, RADIANS);
      Point2D ph = ah.cartesian(3);
      Point2D pd = ad.cartesian(3);
      Point2D pr = ar.cartesian(3);
      
      // compare inputs v.s. outputs

      assertEquals(ah.as(HEADING), h, 0d);
      assertEquals(ah.as(DEGREES), d, 0d);
      assertEquals(ah.as(RADIANS), r, 0d);
      
      // cross check angles against each other
      
      assertTrue(ah.equals(ad));
      assertTrue(ah.equals(ar));
      assertTrue(ar.equals(ad));
      
      // cross check Cartesian results

      assertTrue(ph.equals(pd));
      assertTrue(ph.equals(pr));
      assertTrue(pd.equals(pr));
    }

    // test bisection

    double[] bisects = {112.5, 67.5, 22.5, 315};
    for (int i = 0; i < bisects.length; ++i)
    {
      Angle b = new Angle(bisects[i], DEGREES);

      Angle d1 = new Angle(degrees[i], DEGREES);
      Angle d2 = new Angle(degrees[i + 1], DEGREES);

      assertTrue(d1.bisect(d2).equals(b));

      Angle r1 = new Angle(radians[i], RADIANS);
      Angle r2 = new Angle(radians[i + 1], RADIANS);

      assertTrue(r1.bisect(r2).equals(b));

      Angle h1 = new Angle(headings[i], HEADING);
      Angle h2 = new Angle(headings[i + 1], HEADING);

      assertTrue(h1.bisect(h2).equals(b));
    }

    // test differencing

    Angle[] angles =
      {
        new Angle(10, DEGREES),
        new Angle(80, DEGREES),
        new Angle(270, DEGREES),
        new Angle(350, DEGREES),
        new Angle(-10, DEGREES),
        new Angle(-80, DEGREES),
        new Angle(-270, DEGREES),
        new Angle(-350, DEGREES),
      };
    double[] diffs =
      {
        0.0, 70.0, -100.0, -20.0, -20.0, -90.0, 80.0, -0.0,
        -70.0, 0.0, -170.0, -90.0, -90.0, -160.0, 10.0, -70.0,
        100.0, 170.0, 0.0, 80.0, 80.0, 10.0, -180.0, 100.0,
        20.0, 90.0, -80.0, 0.0, -0.0, -70.0, 100.0, 20.0,
        20.0, 90.0, -80.0, 0.0, 0.0, -70.0, 100.0, 20.0,
        90.0, 160.0, -10.0, 70.0, 70.0, 0.0, 170.0, 90.0,
        -80.0, -10.0, 180.0, -100.0, -100.0, -170.0, 0.0, -80.0,
        0.0, 70.0, -100.0, -20.0, -20.0, -90.0, 80.0, 0.0,
      };

    int idx = 0;
    for (Angle a1: angles)
      for (Angle a2: angles)
        assertEquals(a1.difference(a2).as(DEGREE_RATE), diffs[idx++], 0d);

    // test point based angle generation

    Point2D.Double center = new Point2D.Double(0, 0);
    
    Point2D.Double[] points = 
    {
      new Point2D.Double( 0,  1), // 0
      new Point2D.Double( 1,  1), // 45
      new Point2D.Double( 1,  0), // 90
      new Point2D.Double( 1, -1), // 135
      new Point2D.Double( 0, -1), // 180
      new Point2D.Double(-1, -1), // -135
      new Point2D.Double(-1,  0), // -90
      new Point2D.Double(-1,  1), // -45
    };

    double headingAngles[] = {0, 45, 90, 135, 180, -135, -90, -45};
    
    for (int i = 0; i < headingAngles.length; ++i)
    {
      Angle a = new Angle(center, points[i]);
      Angle test = new Angle(headingAngles[i], HEADING);
      
      assertTrue(a.equals(test));
    }

    // test compare

    Angle a1 = new Angle(10);
    Angle a2 = new Angle(20);
    Angle a3 = new Angle(10);
    
    assertTrue(a1.compareTo(a2) < 0);
    assertTrue(a2.compareTo(a1) > 0);
    assertTrue(a1.compareTo(a3) == 0);
  }

  @Test
  public void rotationTest()
  {
    Type type = HEADING_RATE;

    Angle ap = new Angle( 10, HEADING_RATE);
    Angle an = new Angle(-10, HEADING_RATE);
    System.out.println("- rot by -");
    show(ap, type);
    show(an, type);
    
    Angle a1 = new Angle(0, HEADING);
    Angle a2 = new Angle(0, HEADING);
    System.out.println("- no rot -");
    show(a1, type);
    show(a2, type);
    
    a1.rotate(ap);
    a2.rotate(an);
    System.out.println("- rot 1 -");
    show(a1, type);
    show(a2, type);
    
    a1.rotate(ap);
    a2.rotate(an);
    System.out.println("- rot 2 -");
    show(a1, type);
    show(a2, type);
    
    a1.rotate(ap);
    a2.rotate(an);
    System.out.println("- rot 3 -");
    show(a1, type);
    show(a2, type);
    
    a1.rotate(ap);
    a2.rotate(an);
    System.out.println("- rot 4 -");
    show(a1, type);
    show(a2, type);
  }
  

  public static void show(Angle a, Type type)
  {
    System.out.println(
      a.toStringAs(type) + " = " + a + " = " + a.toStringAs(INTERNAL)  + " = " + a.toStringAs(HEADING));
  }
  
}
