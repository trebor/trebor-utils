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

import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.FontMetrics;

import java.awt.geom.Rectangle2D;

import java.util.Vector;

import static java.lang.Math.*;

/** Scale a font to fit text into a given rectangular box, and
 * optionally draw that text into the box. */

public class ScaleText
{
    /** Paint provided message onto the provided box.
     *
     * @param g graphic on which to draw message
     * @param lines the message to draw
     * @param box box to paint message into
     * @param horizontal horizontal justification
     * @param vertical vertical justification
     */

    public static void drawMessage(
      Graphics2D g, String[] lines, Rectangle2D box, 
      Justify horizontal, Justify vertical)
    {
      // scale the font to fit the message

      scaleFont(g, lines, box);

      // establish the bounding box of the entire message  and for each
      // line

      Vector<Rectangle2D> lineBounds = new Vector<Rectangle2D>();
      Rectangle2D messageBounds = computeMessageBounds(g, lines, lineBounds);

      // establish the vertical offset

      double vOff = vertical.compute(
        box.getHeight(), messageBounds.getHeight());

      // draw message into frame

      for (int i = 0; i < lines.length; ++i)
      {
        // get the line and the line bounds

        String line = lines[i];
        Rectangle2D lineBound = lineBounds.get(i);

        // establish the horizontal offset

        double hOff = horizontal.compute(
          box.getWidth(), lineBound.getWidth());

        g.drawString(
          line,
          (int)(box.getX()                    + hOff),
          (int)(box.getY() + lineBound.getY() + vOff));
      }
    }

    /** compute the bounding box of the given message.
     *
     * @param g the graphics context from witch font and font size are
     * taken.
     * @param lines the lines that make up the message.
     *
     * @return the rectangle whitch enclises the message.
     */

    public static Rectangle2D computeMessageBounds(Graphics g, String[] lines)
    {
      return computeMessageBounds(g, lines, null);
    }

    /** compute the bounding box of the given message.
     *
     * @param g the graphics context from witch font and font size are
     * taken.
     * @param lines the lines that make up the message.
     * @param lineBounds an optional vector in which the enclosing
     * rectangle for each line is stored.
     *
     * @return the rectangle whitch enclises the message.
     */

    // compute the bounding box of the given message 

    public static Rectangle2D computeMessageBounds(
      Graphics g, String[] lines, Vector<Rectangle2D> lineBounds)
    {
      // establish the font metrics 

      FontMetrics fm = g.getFontMetrics();

      // make a place to put the message bounding box

      Rectangle2D messageBounds = null;

      // drop store the vertical position which moves down with each line

      double drop = 0;
      
      // work through the message lines

      for (String line: lines)
      {
        // compute the line bound

        Rectangle2D lineBound = fm.getStringBounds(line, g);

        // move the line bounds donw the correct amount

        lineBound.setRect(
          lineBound.getX(), lineBound.getHeight() + drop,
          lineBound.getWidth(), lineBound.getHeight());
        drop = lineBound.getMinY();

        // append the line bound the message bound

        if (messageBounds == null)
          messageBounds = lineBound.getBounds();
        else
          messageBounds.add(lineBound);

        // if the line bounds store exist add this line bound

        if (lineBounds != null)
          lineBounds.add(lineBound);
      }

      // return the message bounds

      return messageBounds;
    }

    /** Scale the font to that which allows the provided message to fit
     * into the provided box.
     *
     * @param g the graphics whos font will be scaled
     * @param lines the message to draw
     * @param box box to paint message into
     */

    public static void scaleFont(Graphics g, String[] lines, Rectangle2D box)
    {
      g.getFontMetrics();

      // establish the bounding box of the entire message

      Rectangle2D msgBounds = computeMessageBounds(g, lines);

      // compute how much to increase font by to make it fit
      // nicely on the screen

      double increase = min(
        (box.getWidth() / msgBounds.getWidth()) * 0.9d,
        (box.getHeight() / msgBounds.getHeight()) * 0.9d);

      // set font size

      Font font = g.getFont();
      g.setFont(font.deriveFont(font.getSize() * (float)increase));
    }
}
