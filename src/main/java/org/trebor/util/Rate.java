package org.trebor.util;

import static java.lang.Math.min;
import static java.lang.Math.max;

import java.util.concurrent.atomic.AtomicReference;

/**
 * A {@link Rate} models the acceleration and of a moving object.
 * 
 * @author trebor
 */

public class Rate
{
  final private String mName;
  final private AtomicReference<Double> mVelocity;
  final private AtomicReference<Double> mTarget;
  final private double mMin;
  final private double mMax;
  final private double mAcceleration;

  public Rate(String name, double min, double max, double acceleration)
  {
    mName = name;
    mMin = min;
    mMax = max;
    mAcceleration = acceleration;
    mVelocity = new AtomicReference<Double>(new Double(0));
    mTarget = new AtomicReference<Double>(new Double(0));
  }

  public Rate(String name, double acceleration)
  {
    this(name, Double.MIN_VALUE, Double.MAX_VALUE, acceleration);
  }
  
  // clone this rate

  public Rate copy()
  {
    Rate other = new Rate(mName, mMin, mMax, mAcceleration);
    other.setVelocity(getVelocity());
    other.setTarget(getTarget());
    return other;
  }

  // get maximum rate

  public double getMax()
  {
    return mMax;
  }

  // get minimum rate

  public double getMin()
  {
    return mMin;
  }

  // stipulate the velocity

  public void setVelocity(double velocity)
  {
    mVelocity.set(max(mMin, min(velocity, mMax)));
  }

  // get current rate

  public double getVelocity()
  {
    return mVelocity.get();
  }

  // set target rate

  public void setTarget(double target)
  {
    mTarget.set(target);
  }

  // set target as normalized value from -1 to 1

  public void setNormalizedTarget(double target)
  {
    assert (target >= -1 && target <= 1);
    setTarget(mMin + (mMax - mMin) * ((target + 1) / 2));
  }

  // get target

  public double getTarget()
  {
    return mTarget.get();
  }

  // get target as a normalized value from -1 to 1

  public double getNormalizedTarget()
  {
    return ((mTarget.get() - mMin) / (mMax - mMin)) * 2 - 1;
  }

  // update the rate

  public double update(double time)
  {
    if (mTarget.get() > mVelocity.get())
      mVelocity.set(min(mVelocity.get() + mAcceleration * time, mTarget.get()));
    else if (mTarget.get() < mVelocity.get())
      mVelocity.set(max(mVelocity.get() - mAcceleration * time, mTarget.get()));

    return mVelocity.get();
  }

  public double timeIn(double distance)
  {
    return timeIn(distance, getVelocity());
  }
  
  public double timeIn(double distance, double velocity1)
  {
    // d = (v1 + a/2 * t) * t
    // d = v1t + a/2 * t^2
    // 0 = -d + v1t + a/2 * t^2
    // c = -d
    // b = v1
    // a = a/2
    // t = (-b + sqrt(b^2 - 4ac)) / 2a
    // t = (-v1 + sqrt(v1^2 - 4 * (a/2) * -d)) / 2 (a/2)

    return (-velocity1 + Math.sqrt(Math.pow(velocity1, 2) - 2 *
      mAcceleration * -distance)) /
      mAcceleration;
  }
  
  public double velocityIn(double distance)
  {
    return velocityIn(distance, getVelocity());
  }

  public double velocityIn(double distance, double velocity1)
  {
    // t = (v2 - v1) / a
    // t * a = v2 - v1
    // v2 = t * a + v1
    
    return timeIn(distance, velocity1) * mAcceleration + velocity1;
  }
  
  public double timeTo(double velocity)
  {
    return timeBetween(getVelocity(), velocity);
  }

  public double distanceTo(double velocity)
  {
    return distanceBetween(getVelocity(), velocity);
  }
  
  public double timeBetween(double velocity1, double velocity2)
  {
    return (velocity2 - velocity1) / mAcceleration;
  }
  
  public double distanceBetween(double velocity1, double velocity2)
  {
    double time = timeBetween(velocity1, velocity2);
    return (velocity1 + 0.5 * mAcceleration * time) * time;
  }

  public String toString()
  {
    return "Rate [mName=" + mName + ", mVelocity=" + mVelocity + ", mTarget=" +
      mTarget + ", mMin=" + mMin + ", mMax=" + mMax + ", mAcceleration=" +
      mAcceleration + "]";
  }
}
