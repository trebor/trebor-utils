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

import java.io.*;

/** Debug is a class to assist with good-old-fashioned debugging with
 * print statements.
 */

public class Debug
{
    /** counts the number of times messages have been printed */

    static private int markCount = 0;

    /** Prints the default debugging message */

    static public void Mark()
    {
      Mark(null, false);
    }

    /** Prints a debugging message with whatever extra stuff you
     * want to see.
     *
     * @param extraStuff anything you want printed at the end of
     * the debugging message
     */

    static public void Mark(String extraStuff)
    {
      Mark(extraStuff, false);
    }

    /** Prints a debugging message and optionally resets the
     * message counter.
     *
     * @param reset true if you wish to reset the debug number counter
     */

    static public void Mark(boolean reset)
    {
      Mark(null, reset);
    }

    /** Prints a debugging message with whatever extra stuff you
     * want to see and optionally resets the message counter.
     *
     * @param extraStuff anything you want printed at the end of
     * the debugging message
     * @param reset true if you wish to reset the debug number counter
     */

    static public void Mark(String extraStuff, boolean reset)
    {
      Exception	exc = new Exception();
      StringWriter sWriter = new StringWriter();
      PrintWriter pWriter = new PrintWriter(sWriter);

      // update the stack trace in the exception
      // and convert stacktrace to string

      exc.fillInStackTrace();
      exc.printStackTrace(pWriter);
      String fullTrace = sWriter.toString();

      // find lost occerance of "Sherpa.Mark"

      int pos = fullTrace.lastIndexOf("org.trebor.util.Debug.Mark");

      // then find next occurrence of "at ", and then ")"

      int start = fullTrace.indexOf("at " , pos) + 3;
      int end = fullTrace.indexOf( ")" , start) + 1;

      // and now get the substring we're interested in

      String line = fullTrace.substring(start, end);

      // if reset, reset mark count

      if (reset) markCount = 0;

      // and print it to std err

      if (extraStuff != null)
        System.err.println(line + " - " + markCount++ + ": "
        + extraStuff);
      else
        System.err.println(line + " - " + markCount++);
    }
}
