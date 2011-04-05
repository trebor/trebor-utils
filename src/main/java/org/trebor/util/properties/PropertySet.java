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

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Map;
import java.util.HashMap;
import java.util.Properties;
import java.util.Set;

public class PropertySet
{
  private Map<String, IProperty<?>> mPropertiesByName;
  private Map<Character, IProperty<?>> mPropertiesByFlag;

  public PropertySet()
  {
    mPropertiesByName = new HashMap<String, IProperty<?>>();
    mPropertiesByFlag = new HashMap<Character, IProperty<?>>();
  }

  public Set<String> getNames()
  {
    return mPropertiesByName.keySet();
  }
  
  public void add(IProperty<?> candidate)
  {
    for (IProperty<?> property: mPropertiesByName.values())
      if (property.conflictsWith(candidate))
        throw new Error("Properties conflict:" + property + " and " 
          + candidate + ".  Do they have the same name or flag?");

    mPropertiesByName.put(candidate.getName(), candidate);
    if (candidate.getFlag() != null)
      mPropertiesByFlag.put(candidate.getFlag(), candidate);
  }
  
  
  public void applyPropertyFile(File file)
  {
      try
      {
        Properties p = new Properties();
        p.load(new FileReader(file));
        applyProperties(p);
      }
      catch (FileNotFoundException e)
      {
        e.printStackTrace();
        throw new Error("unable to read properties file: " + file);
      }
      catch (IOException e)
      {
        e.printStackTrace();
        throw new Error("unable to read properties file: " + file);
      }
  }

  public void applyProperties(Properties properties)
  {
    for (String propertyName: properties.stringPropertyNames())
      parseNamed(propertyName, properties.getProperty(propertyName));
  }

  public void applyArgs(String[] args)
  {
    // loop through the arguments

    for (int i = 0; i < args.length; ++i)
    {
      // extract the value of the argument, which would follow the argument (may be null)

      String value = (i + 1) < args.length ? args[i + 1] : null;

      // if the argument starts with "--", expect a full name 

      if (args[i].startsWith("--"))
        i += parseNamed(args[i].substring(2), value);

      // if the argument starts with "-", expect a flag

      else if (args[i].startsWith("-"))
        i += parseFlagged(args[i].charAt(1), value);

      // otherwise it's an odd ball

      else
        throw new Error("unexpected argument <" + args[i] + ">");
    }
  }

  protected int parseNamed(String name, String value)
  {
    IProperty<?> p = mPropertiesByName.get(name);
    if (p == null)
      throw new Error("property with name <" + name + "> not found");
    return parseValue(p, value);
  }

  protected int parseFlagged(Character flag, String value)
  {
    IProperty<?> p = mPropertiesByFlag.get(flag);
    if (p == null)
      throw new Error("property with flag <" + flag + "> not found");
    return parseValue(p, value);
  }

  protected int parseValue(IProperty<?> p, String value)
  {
    boolean isFlag = (p instanceof FlagProperty);

    // if the value is null the property must be a flag

    if (value == null && !isFlag)
      throw new Error(p + " has no value");

    // if this is a flag, enable it

    if (isFlag)
      ((FlagProperty)p).setValue(true);

    // otherwise set the value from the string

    else
      p.setValueAsString(value);

    // indicate if the value was used

    return isFlag ? 0 : 1;
  }

  public String toString()
  {
    StringBuffer result = new StringBuffer();

    result.append("Properties\n{\n");
    for (IProperty<?> property: mPropertiesByName.values())
      result.append("  " + property + " = " + property.getValue() + "\n");
    result.append("}\n");

    return result.toString();
  }

  public String toHelpString()
  {
    StringBuffer result = new StringBuffer();

    result.append("Options: \n\n");
    for (IProperty<?> property: mPropertiesByName.values())
      result.append("  " + property.toHelpString() + "\n");
    result.append("\n");

    return result.toString();
  }

  public IProperty<?> findPropertyByName(String name)
  {
    IProperty<?> p = mPropertiesByName.get(name);
    if (p == null)
      throw new Error("Unknown property <" + name + ">");
    return p;
  }
  
  public IProperty<?> findPropertyByFlag(Character flag)
  {
    IProperty<?> p = mPropertiesByFlag.get(flag);
    if (p == null)
      throw new Error("Unknown property flag <" + flag + ">");
    return p;
  }

  public String getValueAsString(String propertyName)
  {
    return mPropertiesByName.get(propertyName).getValueAsString();
  }
  
  public String getString(String propertyName)
  {
    return ((StringProperty)findPropertyByName(propertyName)).getValue();
  }

  public Boolean getBoolean(String propertyName)
  {
    return ((BooleanProperty)findPropertyByName(propertyName)).getValue();
  }

  public Boolean getFlag(String propertyName)
  {
    return ((FlagProperty)findPropertyByName(propertyName)).getValue();
  }
  
  public int getInteger(String propertyName)
  {
    return ((IntegerProperty)findPropertyByName(propertyName)).getValue();
  }
  
  public long getLong(String propertyName)
  {
    return ((LongProperty)findPropertyByName(propertyName)).getValue();
  }
  
  public float getFloat(String propertyName)
  {
    return ((FloatProperty)findPropertyByName(propertyName)).getValue();
  }
  
  public double getDouble(String propertyName)
  {
    return ((DoubleProperty)findPropertyByName(propertyName)).getValue();
  }
}
