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

import java.util.*;
import java.awt.*;
import java.awt.geom.*;
import java.awt.font.*;
import static java.lang.Math.*;
import static java.awt.geom.AffineTransform.*;

public class GlyphsOnPath
{
         // map the glyphs in a GlyphVector along the path
         // specified by a PathIterator 
      
      public static Vector<Shape> glyphsAlongPath(
         GlyphVector gv, PathIterator pi)
      {
         return glyphsAlongPath(gv, pi, Justify.CENTER);
      }
      public static Vector<Shape> glyphsAlongPath(
         GlyphVector gv, PathIterator pi, Justify justification)
      {
         Vector<Shape>  shapes = new Vector<Shape>();
         Vector<Line2D> lines  = new Vector<Line2D>();

            // establish how far down to move the
            // text to vertically center on the path

         gv.getFont()
            .getLineMetrics("", gv.getFontRenderContext());
         double drop = gv.getOutline().getBounds().getHeight() * 0.4;
         //- lm.getDescent(); 

            // variables used during the iteration of the path

         double[] choords = new double[6];
         double   pathLength = 0;
         Point2D  previouse = null;
         Point2D  p = null;
         Point2D  open = null;

         while (!pi.isDone())
         {
            int type = pi.currentSegment(choords);
            switch (type)
            {
               case PathIterator.SEG_MOVETO:
                  previouse = new Point2D.Double(choords[0], choords[1]);
                  open = previouse;
                  break;
               case PathIterator.SEG_LINETO:
                  p = new Point2D.Double(choords[0], choords[1]);
                  lines.add(new Line2D.Double(previouse, p));
                  pathLength += previouse.distance(p);
                  previouse = p;
                  break;
               case PathIterator.SEG_CLOSE:
                  lines.add(new Line2D.Double(previouse, open));
                  pathLength += previouse.distance(open);
                  previouse = open;
                  break;
            }
            pi.next();
         }
            // compute the justification

         double justify = justification.compute(
            pathLength, gv.getVisualBounds().getWidth());

            // track the distance along the path
            // and which line we are on

         double distance = 0;
         double deltaDist = 0;
         Iterator<Line2D> lineItr = lines.iterator();
         Line2D line = null;
         Point2D p1 = null;
         Point2D p2 = null;
         
            // go through the glyphs
         
         for (int i = 0; i < gv.getNumGlyphs(); ++i)
         {
               // get the shape and bounds of the glyph

            Shape shape = gv.getGlyphOutline(i);
            Rectangle bounds = shape.getBounds();

               // no need to concern ourselfs with the small stuff

            if (bounds.getWidth() == 0 || bounds.getHeight() == 0)
               continue;

               // compute the glyph position along the path
            
            double glyphX = 
               bounds.getX() +
               bounds.getWidth() / 2 +
               justify;
            
               // if the glyph is out of bounds don't consider it

            if (glyphX < 0 || glyphX > pathLength)
               continue;

               // while we are not far enough along path
               // move along to the next line in the path

            while (distance < glyphX && lineItr.hasNext())
            {
               line = lineItr.next();
               p1 = line.getP1();
               p2 = line.getP2();
               deltaDist = p1.distance(p2);
               distance += deltaDist;
            }
               // if we are far enough along the path
            
            if (distance >= glyphX)
            {
                  // compute the difference between the start of the line
                  // and the position the glyph should be at

               double diff = glyphX - (distance - deltaDist);

                  // compute glyph rotation

               double rotation = atan(slope(line)) +
                  (line.getX1() > line.getX2() ? PI : 0);

                  // translate the glyph
               
               Point2D translation = new Point2D.Double(
                  p1.getX() - (bounds.getX() 
                               + bounds.getWidth() / 2 - diff),
                  p1.getY() + drop);
               shape = translate(shape, translation);

                  // rotate glyph

               shape = rotate(shape, rotation, p1);
               
                  // add the glyph shape vector for return

               shapes.add(shape);
            }
         }

         return shapes;
      }
         // compute slope of line
      
      public static double slope(Line2D line)
      {
         return
            (line.getY2() - line.getY1()) /
            (line.getX2() - line.getX1());
      }
         // translate a shape

      public static Shape translate(Shape shape, Point2D point)
      {
         return getTranslateInstance(point.getX(), point.getY()).
            createTransformedShape(shape);
      }
         // translate a shape

      public static Shape rotate(Shape shape, double radians, 
                                 Point2D point)
      { 
         return getRotateInstance(radians, point.getX(), point.getY())
            .createTransformedShape(shape);
      }
}
