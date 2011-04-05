/*
 * Copyright (C) 2008 Robert B. Harris (trebor@trebor.org).
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */

package org.trebor.util;

import java.io.File;

import java.awt.Robot;
import java.awt.Shape;
import java.awt.Polygon;
import java.awt.Rectangle;
import java.awt.geom.Area;
import java.awt.geom.AffineTransform;
import java.awt.geom.Arc2D;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Rectangle2D;
import java.awt.geom.GeneralPath;
import java.awt.image.BufferedImage;
import java.awt.Component;

import javax.swing.SwingUtilities;
import javax.imageio.ImageIO;

public class ShapeTools
{
    // shapes

    public static final Shape TRIANGLE = createRegularPoly(3);
    public static final Shape SQUARE   = normalize(new Rectangle2D.Float(0, 0, 1, 1));
    public static final Shape PENTAGON = createRegularPoly(5);
    public static final Shape HEXAGON  = createRegularPoly(6);
    public static final Shape CIRCLE   = normalize(new Ellipse2D.Float(0, 0, 1, 1));
    public static final Shape HEART    = createHeartShape();
    public static final Shape STAR     = createStar(5);
    public static final Shape CAT      = createCatShape();
    public static final Shape DOG      = createDogShape();
    public static final Shape FISH     = createFishShape();

    /** create heart shape */

    public static Shape createHeartShape()
    {
      GeneralPath gp = new GeneralPath();
      gp.append(translate(CIRCLE, 0.5, 0), false);
      gp.append(translate(CIRCLE, 0, 0.5), false);
      gp.append(SQUARE, false);
      return normalize(rotate(gp, 225));
    }
    /** create cat shape */

    public static Shape createCatShape()
    {
      Area cat = new Area(CIRCLE);
      Area wisker = new Area(new Rectangle2D.Double(0, -.01, .3, .02));

      // create left wiskes

      Area leftWiskers = new Area();
      leftWiskers.add(rotate(wisker, -20));
      leftWiskers.add(rotate(wisker,  20));
      leftWiskers.add(rotate(wisker,  20));

      // create right wiskers

      Area rightWiskers = new Area();
      rightWiskers.add(rotate(wisker, 180));
      rightWiskers.add(rotate(wisker, -20));
      rightWiskers.add(rotate(wisker, -20));

      // add the ears

      Area ear = new Area(translate(scale(TRIANGLE, .5, .5), 0.0, -0.6));
      translate(ear, .07, 0);
      cat.add(ear);
      rotate(cat, 60);
      translate(ear, -.14, 0);
      cat.add(ear);
      rotate(cat, -30);

      // add the eyes

      Area eye = new Area(scale(CIRCLE, 0.18, 0.18));
      eye.subtract(new Area(scale(CIRCLE, .06, .12)));
      translate(eye, -.15, -.1);
      cat.subtract(eye);
      translate(eye, .3, 0);
      cat.subtract(eye);

      // add the wiskers

      cat.subtract(translate(leftWiskers,   .08, .14));
      cat.subtract(translate(rightWiskers, -.08, .14));

      // add nose

      Area nose = new Area(createRegularPoly(3));
      rotate(nose, 180);
      scale(nose, .15, .15);
      translate(nose, 0, .1);
      cat.subtract(nose);

      // flatten the cat

      scale(cat, 1.0, 0.85);

      // return normalized shape

      return normalize(cat);
    }
    /** create dog shape */

    public static Shape createDogShape()
    {
      Area dog = new Area(CIRCLE);

      // add the ears

      Area ear = new Area(scale(CIRCLE, .4, .7));
      rotate(ear, 20);
      translate(ear, -.5, -.2);
      dog.subtract(ear);
      scale(ear, -1, 1);
      dog.subtract(ear);
      scale(ear, -1, 1);
      translate(ear, -.05, 0);
      dog.add(ear);
      scale(ear, -1, 1);
      dog.add(ear);
      scale(ear, -1, 1);

      // add the eyes

      Area eye = new Area(scale(CIRCLE, 0.18, 0.18));
      eye.subtract(new Area(scale(CIRCLE, .12, .12)));
      translate(eye, -.15, -.1);
      dog.subtract(eye);
      translate(eye, .3, 0);
      dog.subtract(eye);

      // add nose

      Area snout = new Area(CIRCLE);
      scale(snout, .30, .30);
      translate(snout, 0, .2);
      dog.subtract(snout);

      // add nose

      Area nose = new Area(createRegularPoly(3));
      rotate(nose, 180);
      scale(nose, .20, .20);
      translate(nose, 0, .2);
      dog.add(nose);

      // stretch the dog

      scale(dog, 0.90, 1.0);

      // return normalized shape

      return normalize(dog);
    }
    /** create dog shape */

    public static Shape createFishShape()
    {
      Area fish = new Area();
      Area body = new Area(new Arc2D.Double(0.0, 0, 1.0, 1.0, 30, 120, Arc2D.CHORD));
      Rectangle2D bounds = body.getBounds2D();
      translate(body,
      -(bounds.getX() + bounds.getWidth()  / 2),
      -bounds.getHeight());
      fish.add(body);
      scale(body, 1, -1);
      fish.add(body);

      // add the eye

      Area eye = new Area(scale(CIRCLE, .13, .13));
      eye.subtract(new Area(scale(CIRCLE, .08, .08)));
      translate(eye, -.15, -.08);
      fish.subtract(eye);

      // add tail

      Area tail = new Area(normalize(rotate(TRIANGLE, 30)));
      scale(tail, .50, .50);
      translate(tail, .4, 0);
      fish.add(tail);

      // return normalized shape

      return normalize(fish);
    }
    /** create regular polygon */

    public static Shape createRegularPoly(int edges)
    {
      double radius = 1000;
      double theta = 0.75 * (2 * Math.PI);
      double dTheta = (2 * Math.PI) / edges;
      Polygon p = new Polygon();

      // add a point for each edge

      for (int edge = 0; edge < edges; ++edge)
      {
        p.addPoint(
          (int)(Math.cos(theta) * radius),
          (int)(Math.sin(theta) * radius));
        theta += dTheta;
      }
      // return the normalized poly

      return normalize(p);
    }
    /** create star */

    public static Shape createStar(int points)
    {
      double radius = 1000;
      double theta = 0.75 * (2 * Math.PI);
      double dTheta = (4 * Math.PI) / points;
      Polygon p = new Polygon();

      // add a point for each edge

      for (int point = 0; point < points; ++point)
      {
        p.addPoint(
          (int)(Math.cos(theta) * radius),
          (int)(Math.sin(theta) * radius));
        theta += dTheta;
      }
      // convert to a general path to fill the shape

      GeneralPath gp = new GeneralPath(GeneralPath.WIND_NON_ZERO);
      gp.append(p, true);

      // return the normalized star

      return normalize(gp);
    }
    /** normalize shape (centered at origin, length & with <= 1.0) */

    public static Shape normalize(Shape shape)
    {
      // center the shape on the origin

      Rectangle2D bounds = shape.getBounds2D();
      shape = translate(shape,
      -(bounds.getX() + bounds.getWidth() / 2),
      -(bounds.getY() + bounds.getHeight() / 2));

      // normalize size

      bounds = shape.getBounds2D();
      double scale = bounds.getWidth() > bounds.getHeight()
        ? 1.0 / bounds.getWidth()
        : 1.0 / bounds.getHeight();
      return scale(shape, scale, scale);
    }
    /** rotate a shape */

    public static Shape rotate(Shape shape, double degrees)
    {
      return AffineTransform.getRotateInstance(degrees / 180 * Math.PI)
        .createTransformedShape(shape);
    }
    /** rotate a shape about the center of the shape */

    public static Shape rotateAboutCenter(Shape shape, double degrees)
    {
      Rectangle2D bounds = shape.getBounds2D();
      return AffineTransform.getRotateInstance(degrees / 180 * Math.PI,
      bounds.getX() + bounds.getWidth() / 2,
      bounds.getY() + bounds.getHeight() / 2)
        .createTransformedShape(shape);
    }
    /** translate a shape */

    public static Shape translate(Shape shape, double x, double y)
    {
      return AffineTransform.getTranslateInstance(x, y).createTransformedShape(shape);
    }
    /** scale a shape */

    public static Shape scale(Shape shape, double x, double y)
    {
      return AffineTransform.getScaleInstance(x, y).createTransformedShape(shape);
    }
    /** rotate an area */

    public static Area rotate(Area area, double degrees)
    {
      area.transform(AffineTransform.getRotateInstance(degrees / 180 * Math.PI));
      return new Area(area);
    }
    /** rotate an area about the center of the area */

    public static Area rotateAboutCenter(Area area, double degrees)
    {
      Rectangle2D bounds = area.getBounds2D();
      area.transform(AffineTransform.getRotateInstance(degrees / 180 * Math.PI,
      bounds.getX() + bounds.getWidth() / 2,
      bounds.getY() + bounds.getHeight() / 2));
      return new Area(area);
    }
    /** translate an area */

    public static Area translate(Area area, double x, double y)
    {
      area.transform(AffineTransform.getTranslateInstance(x, y));
      return new Area(area);
    }
    /** scale an area */

    public static Area scale(Area area, double x, double y)
    {
      area.transform(AffineTransform.getScaleInstance(x, y));
      return new Area(area);
    }

    /** Capture the area from a given component.
     *
     * @param component the component to collect the image from
     *
     * @return a buffered image of the component passed in.
     */

    public static BufferedImage captureImage(Robot robot, Component component)
    {
      Rectangle bounds = component.getBounds();
      java.awt.Point point = new java.awt.Point(bounds.x, bounds.y);
      SwingUtilities.convertPointToScreen(point, component);
      bounds.setBounds(point.x, point.y, bounds.width, bounds.height);
      return robot.createScreenCapture(bounds);
    }

    /** Capture the area from a given component and write it out to the
     * home director of the user.
     *
     * @param component the component to collect the image from
     */

    public static void captureAndStoreImage(Component component, File file)
    {
      try
      {
        Robot robot = new Robot();
        ImageIO.write(captureImage(robot, component), "png", file);
      }
      catch (Exception ex)
      {
        ex.printStackTrace();
      }
    }
}
