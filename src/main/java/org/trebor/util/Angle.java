/*
 * Copyright (C) 2008 Robert B. Harris (trebor@trebor.org). Permission is
 * hereby granted, free of charge, to any person obtaining a copy of this
 * software and associated documentation files (the "Software"), to deal in
 * the Software without restriction, including without limitation the
 * rights to use, copy, modify, merge, publish, distribute, sublicense,
 * and/or sell copies of the Software, and to permit persons to whom the
 * Software is furnished to do so, subject to the following conditions: The
 * above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software. THE SOFTWARE IS
 * PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED,
 * INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS
 * FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING
 * FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER
 * DEALINGS IN THE SOFTWARE.
 */

package org.trebor.util;

import java.awt.geom.Point2D;
import java.lang.Comparable;

import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlRootElement;

import static java.lang.Math.sin;
import static java.lang.Math.cos;
import static java.lang.Math.atan2;
import static java.lang.Math.toRadians;
import static java.lang.Math.toDegrees;
import static org.trebor.util.Angle.Type.*;

/**
 * Angle is an immutable handles math for angular values. It handles issues
 * of wrap-around and conversions between degrees, radians and heading. It
 * understands that a heading (0 degrees == north, values increasing
 * clockwise) is different from the unit circle (0 degrees = east, values
 * increasing counter clockwise).
 */

@XmlRootElement(name = "angle")
@XmlAccessorType(XmlAccessType.FIELD)
public class Angle implements Comparable<Angle>
{
  /**
   * Type is used to identify common representations of angles. If your
   * preferred representation is not present, feel free to add it.
   */

  public static enum Type
  {
    /**
     * Radians range from 0 to 2pi and increase in a counter clockwise
     * direction.
     */

    RADIANS
    {
      public double toInternal(double value)
      {
        return toDegrees(value) % 360;
      }

      public double fromInternal(double value)
      {
        return toRadians((360 + value) % 360);
      }
    },

    /**
     * Radians rate, range from -2p to 2pi and increase in a counter
     * clockwise direction.
     */

    RADIAN_RATE
    {
      public double toInternal(double value)
      {
        return toDegrees(value) % 360;
      }

      public double fromInternal(double value)
      {
        return toRadians(value % 360);
      }
    },

    /**
     * Degrees range from 0 to 360 and increase in a counter clockwise
     * direction.
     */

    DEGREES
    {
      public double toInternal(double value)
      {
        return value % 360;
      }

      public double fromInternal(double value)
      {
        return (360 + value) % 360;
      }
    },

    /**
     * Degrees rate range from -360 to 360 and increase in a counter
     * clockwise direction.
     */

    DEGREE_RATE
    {
      public double toInternal(double value)
      {
        return value % 360;
      }

      public double fromInternal(double value)
      {
        return value % 360;
      }
    },

    /**
     * Headings range from 0 to 360 and increase in a clockwise direction.
     */

    HEADING
    {
      public double toInternal(double value)
      {
        return (90 - value) % 360;
      }

      public double fromInternal(double value)
      {
        return (360 + (90 - value)) % 360;
      }
    },

    /**
     * Heading rates range from -360 to 360 and increase in a clockwise
     * direction.
     */

    HEADING_RATE
    {
      public double toInternal(double value)
      {
        return -value % 360;
      }

      public double fromInternal(double value)
      {
        return -value % 360;
      }
    };

    /** Convert a angular typed value to the internal angle representation. */

    abstract double toInternal(double value);

    /**
     * Convert a value in the internal angle representation to a value of
     * this angular type.
     */

    abstract double fromInternal(double value);
  };

  /** The internal representation of the angular value. */

  protected static final Type INTERNAL = DEGREE_RATE;

  /**
   * The internal angular value, stored in the representation as specified
   * by the INTERNAL value.
   */

  private double angle;

  /** Default Angle constructor. */

  public Angle()
  {
    setAngle(0, INTERNAL);
  }

  /**
   * Angle constructor.
   * 
   * @param value the angular value
   * @param type the type of angular value
   */

  public Angle(double value, Type type)
  {
    setAngle(value, type);
  }

  /**
   * Angle copy constructor which accepts another angle.
   * 
   * @param other angle to copy
   */

  public Angle(Angle other)
  {
    setAngle(other);
  }

  /**
   * Angle constructor which accepts the slope of a line.
   * 
   * @param deltaX the change in X value along a line segment
   * @param deltaY the change in Y value along a line segment
   */

  public Angle(double deltaX, double deltaY)
  {
    setAngle(deltaX, deltaY);
  }

  /**
   * Angle constructor which accepts two points.
   * 
   * @param p1 start point of line segment
   * @param p2 end point of line segment
   */

  public Angle(Point2D p1, Point2D p2)
  {
    setAngle(p2.getX() - p1.getX(), p2.getY() - p1.getY());
  }

  /**
   * Create a new angle from a given angle and amount to rotate it by.
   * 
   * @param a1 the base angel
   * @param a2 the amount to rotate a1 by
   */

  public Angle(Angle a1, Angle a2)
  {
    setAngle(a1.rotate(a2));
  }

  /**
   * Angle constructor used internally which accepts values in the internal
   * angular representation.
   * 
   * @param internal the angular value in the internal representation
   */

  protected Angle(double internal)
  {
    setAngle(internal, INTERNAL);
  }

  /**
   * Sets the value of this angle to that of another angle.
   * 
   * @param other the other angle
   */

  protected void setAngle(Angle other)
  {
    setAngle(other.angle, INTERNAL);
  }

  /**
   * Sets the value of this angle. This is the one true setAngle which all
   * other setAngle methods call.
   * 
   * @param value the angular value
   * @param type the type of angular value
   */

  protected void setAngle(double value, Type type)
  {
    angle = type.toInternal(value);
  }

  /**
   * Sets the value of this angle to the slope of a line.
   * 
   * @param deltaX the change in X value along a line segment
   * @param deltaY the change in Y value along a line segment
   */

  protected void setAngle(double deltaX, double deltaY)
  {
    setAngle(atan2(deltaY, deltaX), RADIANS);
  }

  /**
   * Rotate the angle by some amount. This is the one true rotate which is
   * called by any other rotate methods.
   * 
   * @param delta amount to change angle by
   * @param type the type of angular value
   * @return a new angle rotated by delta
   */

  public Angle rotate(double delta, Type type)
  {
    return new Angle(angle + type.toInternal(delta), INTERNAL);
  }

  /**
   * Rotate this angle by some delta angle.
   * 
   * @param delta the amount to rotate this angle by
   * @return a new angle rotated by delta
   */

  public Angle rotate(Angle delta)
  {
    return rotate(delta.angle, INTERNAL);
  }

  /**
   * Return the angular value in the specified type.
   * 
   * @param type the type of angular value
   * @return the angular value in the specified type.
   */

  public double as(Type type)
  {
    return type.fromInternal(angle);
  }

  /**
   * Convert polar coordinates to Cartesian (rectangular) coordinates.
   * 
   * @param radius radius of polar coordinate
   * @param deltaX x offset in Cartesian space
   * @param deltaY y offset in Cartesian space
   * @return point in Cartesian space
   */

  public Point2D cartesian(double radius, double deltaX, double deltaY)
  {
    return cartesian(angle, INTERNAL, radius, deltaX, deltaY);
  }

  /**
   * Convert polar coordinates to Cartesian (rectangular) coordinates.
   * 
   * @param radius radius of polar coordinate
   * @param delta offset in Cartesian space
   * @return point in Cartesian space
   */

  public Point2D cartesian(double radius, Point2D delta)
  {
    return cartesian(angle, INTERNAL, radius, delta.getX(), delta.getY());
  }

  /**
   * Convert polar coordinates to Cartesian (rectangular) coordinates.
   * 
   * @param angle angle of polar coordinate
   * @param type type of angle
   * @param radius radius of polar coordinate
   * @param deltaX x offset in Cartesian space
   * @param deltaY y offset in Cartesian space
   * @return point in Cartesian space
   */

  public static Point2D cartesian(double angle, Type type, double radius,
    double deltaX, double deltaY)
  {
    // convert to radians

    angle = RADIANS.fromInternal(type.toInternal(angle));

    // compute point

    return new Point2D.Double(deltaX + radius * cos(angle), deltaY + radius *
      sin(angle));
  }

  /**
   * Convert polar coordinates to Cartesian (rectangular) coordinates.
   * 
   * @param radius radius of polar coordinate
   * @return point in Cartesian space
   */

  public Point2D cartesian(double radius)
  {
    return cartesian(radius, 0, 0);
  }

  /**
   * Bisect this angle with another angle.
   * 
   * @param other other angle to bisect with this angle
   * @return the angle which bisects this and other angle
   */

  public Angle bisect(Angle other)
  {
    double delta = difference(as(INTERNAL), other.as(INTERNAL)) / 2;
    return new Angle(as(INTERNAL) + delta);
  }

  /**
   * Compare two angles.
   * 
   * @param other other angle to compare to
   * @return a negative integer, zero, or a positive integer as this object
   *         is less than, equal to, or greater than the specified object
   */

  public int compareTo(Angle other)
  {
    return (int)difference(other).as(HEADING_RATE);
  }

  /**
   * Compute difference between this and another angle.
   * 
   * @param angle the angular value
   * @param type the type of that value
   * @return difference between this angle and the other in the specified
   *         type.
   */

  public Angle difference(double angle, Type type)
  {
    return difference(this, new Angle(angle, type));
  }

  /**
   * Compute difference between this and another angle.
   * 
   * @param other other angle to compute difference to.
   * @return difference between this angle and the other.
   */

  public Angle difference(Angle other)
  {
    return difference(this, other);
  }

  /**
   * Compute difference between two angles which handles wrap around.
   * 
   * @param angle1 angle to compute difference from
   * @param angle2 angle to compute difference to
   * @return difference between angle1 and angle2
   */

  public static Angle difference(Angle angle1, Angle angle2)
  {
    return new Angle(difference(angle1.as(INTERNAL), angle2.as(INTERNAL)));
  }

  /**
   * Compute difference between two angular values.
   * 
   * @param value1 angle to compute difference from
   * @param value2 angle to compute difference from
   * @param type the type of both the values
   * @return difference between angle1 and angle2 in the internal
   *         representation.
   */

  public static double difference(double value1, double value2, Type type)
  {
    return type.fromInternal(difference(type.toInternal(value1),
      type.toInternal(value2)));
  }

  /**
   * Compute difference between two angles in the internal representation.
   * 
   * @param angle1 angle to compute difference from, in internal
   * @param angle2 angle to compute difference to, in internal
   * @return difference between angle1 and angle2 in the internal
   *         representation.
   */

  protected static double difference(double angle1, double angle2)
  {
    double delta = (angle2 - angle1) % 360;

    if (delta < -180)
      return 360 + delta;

    if (delta > 180)
      return delta - 360;

    return delta % 360;
  }

  /**
   * Convert angle to a string.
   * 
   * @return the value is the angle in degrees followed by the word
   *         "degrees".
   */

  public String toString()
  {
    return toStringAs(DEGREES);
  }

  /**
   * Convert angle to a string as a particular type.
   * 
   * @param type the type to format the printed angle in.
   * @return the value is the angle in specified type followed by the a
   *         label indicating said type.
   */

  public String toStringAs(Type type)
  {
    return "ANGLE[" + as(type) + " " + type + "]";
  }

  /**
   * Test if some other angle equals this one.
   * 
   * @param other the other angle to test
   * @return true if the two angels are equal
   */

  public boolean equals(Angle other)
  {
    return as(DEGREES) == other.as(DEGREES);
  }
}
