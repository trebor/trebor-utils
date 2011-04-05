package org.trebor.util.properties;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

public class PropertySetTest
{
  IProperty<?>[] testProperties =
  {
    new StringProperty("string", 's', "default-value", "a test string prop"),
    new BooleanProperty("bool", 'b', false, "a test bool prop"),
    new FlagProperty("flag", 'f', false, "a test flag prop"),
    new IntegerProperty("int", 'i', 11, "a test int prop"),
    new FloatProperty("float", 'F', 2.2f, "a test float prop"),
    new DoubleProperty("double", 'd', 3.3d, "a test double prop"),
    new LongProperty("long", 'l', 10000l, "a test long prop"),
  };

  @Test
    public void devTest()
  {
    PropertySet ps = new PropertySet();

    for (IProperty<?> p: testProperties)
      ps.add(p);

    assertEquals(ps.getString("string"), "default-value");
    assertEquals(ps.getBoolean("bool"), false);
    assertEquals(ps.getFlag("flag"), false);
    assertEquals(ps.getInteger("int"), 11);
    assertTrue(ps.getFloat("float") == 2.2f);
    assertTrue(ps.getDouble("double") == 3.3d);
    assertTrue(ps.getLong("long") == 10000l);

    System.out.println(ps);
    System.out.println(ps.toHelpString());

    String[] args = {
      "--double", "77.77",
      "--long", "1000000000000",
      "-F", "88.88", 
      "--int", "99", 
      "-f", 
      "--string", "string-value", 
      "--bool", "true"};

    ps.applyArgs(args);

    System.out.println(ps);
    System.out.println(ps.toHelpString());
    
    assertEquals(ps.getString("string"), "string-value");
    assertEquals(ps.getBoolean("bool"), true);
    assertEquals(ps.getFlag("flag"), true);
    assertEquals(ps.getInteger("int"), 99);
    assertTrue(ps.getFloat("float") == 88.88f);
    assertTrue(ps.getDouble("double") == 77.77d);
    assertTrue(ps.getLong("long") == 1000000000000l);
  }

  @Test
    public void conflictTest()
  {
    boolean pass1 = false;
    boolean pass2 = false;
    boolean pass3 = false;
    boolean pass4 = true;

    try
    {
      PropertySet ps = new PropertySet();
      ps.add(new StringProperty("str", 's', null, null));
      ps.add(new StringProperty("xxx", 's', null, null));
    }
    catch (Error e)
    {
      pass1 = true;
    }

    try
    {
      PropertySet ps = new PropertySet();
      ps.add(new StringProperty("str", 's', null, null));
      ps.add(new StringProperty("str", 'x', null, null));
    }
    catch (Error e)
    {
      pass2 = true;
    }

    try
    {
      PropertySet ps = new PropertySet();
      ps.add(new StringProperty("str", 's', null, null));
      ps.add(new StringProperty("str", 's', null, null));
    }
    catch (Error e)
    {
      pass3 = true;
    }

    try
    {
      PropertySet ps = new PropertySet();
      ps.add(new StringProperty("str", 's', null, null));
      ps.add(new StringProperty("xxx", 'x', null, null));
    }
    catch (Error e)
    {
      pass4 = false;
    }

    assertTrue(pass1);
    assertTrue(pass2);
    assertTrue(pass3);
    assertTrue(pass4);
  }
}
