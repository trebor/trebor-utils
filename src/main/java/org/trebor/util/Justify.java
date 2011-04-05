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

/** Ggeneralized justification system, with ability to specify left,
 * center, and right with an optional custom offsets.
 */

public class Justify
{
    /** custom offset */

    double offset;

    /** is the offset a percent of the frame */

    boolean percent;

    /** justification type */

    Type location;

    /** types of justification */

    public static enum Type
    {
      TOP, LEFT, CENTER, RIGHT, BOTTOM;
    };

    // stock justifications

    public static final Justify LEFT   = new Justify(Type.LEFT);
    public static final Justify CENTER = new Justify(Type.CENTER);
    public static final Justify RIGHT  = new Justify(Type.RIGHT);
    public static final Justify TOP    = new Justify(Type.TOP);
    public static final Justify BOTTOM = new Justify(Type.BOTTOM);

    /** Construct a justification with no custom offset.
     *
     * @param type type of offset - TOP, LEFT, CENTER, RIGHT, BOTTOM
     */

    public Justify(Type type)
    {
      this(type, 0, false);
    }

    /** Construct a justification with whit a custom offset.
     *
     * @param type type of offset - TOP, LEFT, CENTER, RIGHT, BOTTOM
     * @param offset offset from position specified by type
     */

    public Justify(Type type, double offset)
    {
      this(type, offset, false);
    }

    /** Construct a justification with whit a custom offset which
     * is a percentage of the frame.
     *
     * @param location type of offset - TOP, LEFT, CENTER, RIGHT, BOTTOM
     * @param offset offset from position specified by type
     * @param percent true if the offset is a percent of the frame
     * inside which the content is being displayed
     */

    public Justify(Type location, double offset, boolean percent)
    {
      this.location = location;
      this.offset = offset;
      this.percent = percent;
    }

    /** Compute offset which should be added to the content
     * position to produce the correct justification of the content
     * inside the frame.
     *
     * @param frame size of the frame
     * @param content size of the content
     * @return the justification for the conent in the frame.
     */

    public double compute(double frame, double content)
    {
      double result = 0;

      switch (location)
      {
        case TOP:
        case LEFT:
          break;
        case CENTER:
          result = (frame - content) / 2;
          break;
        case BOTTOM:
        case RIGHT:
          result = (frame - content);
          break;
      }
      // return the result adjusted by offset

      return result + (percent
      ? offset * frame
      : offset);
    }
}
