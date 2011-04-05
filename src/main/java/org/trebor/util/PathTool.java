package org.trebor.util;

import java.awt.Shape;
import java.awt.geom.FlatteningPathIterator;
import java.awt.geom.Line2D;
import java.awt.geom.PathIterator;
import java.awt.geom.Point2D;
import java.util.Map;
import java.util.NavigableMap;
import java.util.TreeMap;

public class PathTool
{
  private final NavigableMap<Double, Segment> mLines;
  private final double mLength;

  public class PathPoint extends Point2D.Double
  {
    private static final long serialVersionUID = -6575106768229167797L;
    private final Angle mAngle;
    
    PathPoint(double x, double y, Angle angle)
    {
      super(x, y);
      mAngle = angle;
    }

    public Angle getAngle()
    {
      return mAngle;
    }
  }
  
  private class Segment
  {
    final Line2D mLine;
    final double mLength;
    
    public Segment(Point2D p1, Point2D p2)
    {
      mLength = p1.distance(p2);
      mLine = new Line2D.Double(p1, p2);
    }
  }

  public PathTool(Shape shape, double flatness)
  {
    this((FlatteningPathIterator)shape.getPathIterator(null, flatness));
  }
  
  public PathTool(FlatteningPathIterator pi)
  {
    mLines = new TreeMap<Double, Segment>();
    
    // variables used during the iteration along the path

    double length = 0;
    Point2D previouse = null;
    Point2D p = null;
    Point2D open = null;
    double[] choords = new double[6];

    // iterate along the path to compute length and collect line segments

    while (!pi.isDone())
    {
      switch (pi.currentSegment(choords))
      {
      case PathIterator.SEG_MOVETO:
        previouse = new Point2D.Double(choords[0], choords[1]);
        open = previouse;
        break;
      case PathIterator.SEG_LINETO:
        p = new Point2D.Double(choords[0], choords[1]);
        mLines.put(length, new Segment(previouse, p));
        length += previouse.distance(p);
        previouse = p;
        break;
      case PathIterator.SEG_CLOSE:
        mLines.put(length, new Segment(previouse, open));
        length += previouse.distance(open);
        previouse = open;
        break;
      default:
        throw new Error("Unexpected segment type.");
      }
      pi.next();
    }
    
    // fix the total path length
    
    this.mLength = length;
  }

  /**
   * Return the point at the start of the path.
   * 
   * @return the point at start of the path.
   */
  
  public PathPoint getStartPoint()
  {
    return getPathPoint(0);
  }
  
  /**
   * Return the point at the end of the path.
   * 
   * @return point at the end of the path.
   */
  
  public PathPoint getEndPoint()
  {
    return getPathPoint(getLength());
  }

  /**
   * Return the length of the path.
   * 
   * @return the total length of the path.
   */
  
  public double getLength()
  {
    return mLength;
  }

  /**
   * Compute the position and angle of a point on given path.
   * 
   * @param extent the extent down the path
   * @return the path point at the specified distance down the path, or NULL
   *         if the extent is beyond that of the path.
   */
  
  public PathPoint getPathPoint(double extent)
  {
    // if negative or beyond the length of the path, return null

    if (extent < 0 || extent > mLength)
      return null;

    Map.Entry<Double, Segment> segment = mLines.floorEntry(extent);

    // compute the percentage down the segment to travel

    double segmentPercent =
      (extent - segment.getKey()) / segment.getValue().mLength;
    
    // establish the start end end points of the path
    
    Point2D p1 = segment.getValue().mLine.getP1();
    Point2D p2 = segment.getValue().mLine.getP2();

    // return path point at provided extent
    
    return new PathPoint(p1.getX() + (p2.getX() - p1.getX()) * segmentPercent,
      p1.getY() + (p2.getY() - p1.getY()) * segmentPercent, new Angle(p1, p2));
  }
}