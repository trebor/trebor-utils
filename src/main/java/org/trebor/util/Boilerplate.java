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

import java.util.Vector;
import java.util.Iterator;
import java.util.Collection;


import java.io.File;
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.InputStreamReader;

import static java.lang.System.out;
import static java.lang.System.exit;

public class Boilerplate
{
  public static void main(String[] args)
  {
    if (args.length < 2)
    {
      printHelp("ERROR: no input files");
      exit(0);
    }

    File input = new File(args[0]);
    if (!input.exists())
    {
      printHelp("ERROR: input file does not exits: " + input);
      exit(0);
    }

    File boilerplate = new File(args[1]);
    if (!input.exists())
    {
      printHelp("ERROR: boilerplate file does not exits: " + boilerplate);
      exit(0);
    }

    Collection<IMatcher> matchers = new Vector<IMatcher>();
    matchers.add(new JavaMatcher(true));

    new Boilerplate(input, boilerplate, matchers);
  }

  /** Print help with an optional message */

  public static void printHelp(String message)
  {
    out.println("To perform boiler plate replacement:");
    out.println("");
    out.println("   BoilerPlate <input-file> <boilerplate-file>");
    out.println("");
    if (message != null && message.length() > 0)
    {
      out.println(message);
      out.println("");
    }
  }

  public Boilerplate(File inputFile, File boilerplateFile,
    Collection<IMatcher> matchers)
  {
    try
    {
      // open & read the input

      FileInputStream inputStream = new FileInputStream(inputFile);
      BufferedReader inputReader = new BufferedReader(
        new InputStreamReader(inputStream));
      Collection<String> input = new Vector<String>();
      for (String line = inputReader.readLine(); line != null;
           line = inputReader.readLine())
      {
        input.add(line);
      }
      inputStream.close();

      // open & read  boilerplate

      BufferedReader boilerplateReader = new BufferedReader(
        new InputStreamReader(new FileInputStream(boilerplateFile)));
      Collection<String> boilerplate = new Vector<String>();
      for (String line = boilerplateReader.readLine(); line != null;
           line = boilerplateReader.readLine())
      {
        boilerplate.add(line);
      }

      // process the lines

      Collection<String> output = process(input, boilerplate, matchers);

      // print results

      if (output != null)
      {
        out.println("fixed : " + inputFile);
        FileWriter writer = new FileWriter(inputFile);
        for (String line: output)
        {
          writer.write(line + "\n");
        }
        writer.flush();
        writer.close();
      }
      else
        out.println("FAILED: " + inputFile);

    }
    catch (Exception e)
    {
      e.printStackTrace();
    }
  }

  /** process lines against matchers */

  protected Collection<String> process(Collection<String> input, 
    Collection<String> boilerplate, Collection<IMatcher> matchers)
  {
    for (IMatcher matcher: matchers)
    {
      Collection<String> output = matcher.test(input, boilerplate);
      if (output != null)
        return output;
    }

    return null;
  }

  /** Matcher interface. */
  
  static interface IMatcher
  {
    public Collection<String> test(Collection<String> input, 
      Collection<String> boilerplate);
  }
  
  /** Abstract matcher. */

  static abstract class AMatcher implements IMatcher
  {
    private final String mName;
    private String[]     mHeader;
    private String[]     mFooter;
    private String       mPrefix;
    private String       mPostfix;
    protected boolean    mInstertIfNotFound;

    public AMatcher(String name, String[] header, String[] footer, 
      String prefix, String postfix, boolean instertIfNotFound)
    {
      mName = name;
      mHeader = header;
      mFooter = footer;
      mPrefix = prefix;
      mPostfix = postfix;
      mInstertIfNotFound = instertIfNotFound;
    }

    // insert at start of file

    protected Collection<String> insert(Collection<String> input,
      Collection<String> insertion)
    {
      Collection<String> output = new Vector<String>();

      // insert insertion text
      
      for (String line: mHeader)
        output.add(line);
      for (String line: insertion)
        output.add(mPrefix + line + mPostfix);
      for (String line: mFooter)
        output.add(line);
      
      // append the input 

      for (String line: input)
        output.add(line);
      
      // return output
      
      return output;
    }

    // replace a range of lines, do not include the 'start' and 'end' lines
    
    protected Collection<String> replace(Collection<String> input,
      String start, String end, Collection<String> replacement)
    {
      Collection<String> output = new Vector<String>();
      Iterator<String> i = input.iterator();
      
      // insert all lines before start
      
      while (i.hasNext())
      {
        String line = i.next();
        if (line == start)
          break;
        else
          output.add(line);
      }
      
      // exclude all lines upto end
      
      while (i.hasNext())
      {
        String line = i.next();
        if (line == end)
          break;
      }
      
      // write out the replacement
      
      for (String line: mHeader)
        output.add(line);
      for (String line: replacement)
        output.add(mPrefix + line + mPostfix);
      for (String line: mFooter)
        output.add(line);
      
      // write out rest of the file
      
      while (i.hasNext())
        output.add(i.next());
      
      // return output
      
      return output;
    }

    public abstract Collection<String> test(Collection<String> input, 
      Collection<String> boilerplate);

    public boolean isBlank(String line)
    {
      return (line.trim().length() == 0);
    }

    public String toString()
    {
      return mName;
    }
  }

  /** Java matcher. */

  static class JavaMatcher extends AMatcher
  {
    enum State {PRE_BLANK, START, COMMENT, END, POST_BLANK, REPLACE, INSERT};
    
    private State mState = State.PRE_BLANK;

    public JavaMatcher(boolean instertIfNotFound)
    {
      super("java", 
        new String[]{"/*"}, 
        new String[]{" */", ""}, 
        " * ",
        "", 
        instertIfNotFound);
    }

    public Collection<String> test(Collection<String> input, 
      Collection<String> boilerplate)
    {
      Iterator<String> i = input.iterator();
      String start = null;
      String end = null;

      for (String line = i.next(); i.hasNext(); /* in loop */)
      {
        switch (mState)
        {
        case PRE_BLANK:
          if (isBlank(line))
          {
            if (null == start)
              start = line;
            line = i.next();
          }
          else
          {
            if (null == start)
              start = line;
            mState = State.START;
          }
          break;

        case START:
          if (line.matches("^\\s*/\\*.*$"))
          {
            line = i.next();
            mState = State.COMMENT;
          }
          else 
            mState = State.INSERT;
          break;

        case COMMENT:
          if (isBlank(line) || 
            (line.matches("^\\s*\\*.*$") && !line.matches("^\\s*\\*/.*$")))
          {
            line = i.next();
          }
          else
            mState = State.END;
          break;

        case END:
          if (line.matches("^\\s*\\*/.*$"))
          {
            end = line;
            line = i.next();
            mState = State.POST_BLANK;
          }
          else 
            return null;
          break;

        case POST_BLANK:
          if (isBlank(line))
          {
            end = line;
            line = i.next();
          }
          else
            mState = State.REPLACE;
          break;

        case REPLACE:
          return replace(input, start, end, boilerplate);

        case INSERT:
          return insert(input, boilerplate);
        }
      }

      return null;
    }
  }
}
