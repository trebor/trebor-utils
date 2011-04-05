/*
 * Copyright (C) 2010 Robert B. Harris (trebor@trebor.org).
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

package org.trebor.util.properties;

public abstract class AProperty<T> implements IProperty<T>
{
  private final String mName;
  private final Character mFlag;
  private final String mDescription;
  private final String mTypeName;
  private final T mDefaultValue;
  private T mValue;

  public AProperty(String name, Character flag, String typeName, T defaultValue, 
    String description)
  {
    if (name.length() < 2)
      throw new Error("property name <" + name + "> too short");

    mName = name;
    mFlag = flag;
    mTypeName = typeName;
    mDescription = description;
    mDefaultValue = defaultValue;
    setValue(defaultValue);
  }

  public String getName()
  {
    return mName;
  }

  public Character getFlag()
  {
    return mFlag;
  }

  public String getTypeName()
  {
    return mTypeName;
  }
  
  public T getDefaultValue()
  {
    return mDefaultValue;
  }

  public String getDescription()
  {
    return mDescription;
  }

  public String getValueAsString()
  {
    return mValue.toString();
  }

  public T getValue()
  {
    return mValue;
  }

  public void setValue(T value)
  {
    mValue = value;
  }

  public boolean conflictsWith(IProperty<?> other)
  {
    return (getName().equalsIgnoreCase(other.getName()) ||
      (getFlag() == other.getFlag() && getFlag() != null));
  }

  public String toString()
  {
    String flagStr = getFlag() == null 
      ? "" 
      : ", <" + getFlag() + ">";
      
    return "Property[" + getName() + flagStr + "]";
  }

  public String toHelpString()
  {
    StringBuffer result = new StringBuffer();
    String valueString = getTypeName() == null 
      ? "" 
      : " <" + getTypeName() + ">";

    result.append("--" + getName() + valueString);
    if (getFlag() != null)
      result.append(" -" + getFlag() + valueString);
    result.append(" DEFAULT: " + getDefaultValue());
    result.append(" DESCRIPTION: " + getDescription());
    
    return result.toString();
  }

}
