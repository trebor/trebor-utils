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

import java.io.InputStream;
import java.io.OutputStream;
import java.io.FileOutputStream;
import java.io.File;
import java.net.URL;
import java.lang.ClassLoader;

/** This class provides some standard tools for loading and executing
 * items from inside a jar file. */

public class JarTools
{
      static ClassLoader classLoader = ClassLoader.getSystemClassLoader();

      /** Load a system library from the jar into memory.
       *
       * @param path Path to the library in the jar file (or not for that matter).
       * @param name Base library name. OS specific text will be added
       *        around the name as needed.
       */

      public static void loadLibrary(String path, String name)
         throws Exception
      {
         // the file separator should NOT be File.separator, as we are
         // reading this out of the jar file, rather then from a volume
         // on the system.

         String separator = "/";

         // establish the correct path to the library based on os

         String lib = (path.endsWith(separator)
                       ? path
                       : path + separator) +
            System.mapLibraryName(name);
         
         // copy file to temporary location
         
         File tmp = File.createTempFile(name + ".", ".lib");
         tmp.deleteOnExit();
         copyResource(lib, tmp);
         
         // load lib file now in tmp
         
         System.load(tmp.getPath());
      }

      /** Copy a file in the jar to some location outside the jar.
       *
       * @param source relative path to file in jar
       * @param destination path to copy
       */

      public static void copyResource(String source, String destination)
         throws Exception 
      {
         copyResource(source, new File(destination));
      }

      /** Copy a file in the jar to some location outside the jar.
       *
       * @param source relative path to file in jar
       * @param destination path to copy
       */

      public static void copyResource(String source, File destination)
         throws Exception 
      {
         System.out.println("source: " + source);
         InputStream in = getResourceAsStream(source);
         OutputStream out = new FileOutputStream(destination);
         byte[] buffer = new byte [32*1024];
         int len;
         while ((len = in.read(buffer)) != -1)
            out.write(buffer, 0, len);
         out.close();
      }

      /** Return a stream to a file in the jar.  This is a covience
       * function witch just calls a function by the same name in the
       * class loader.
       *
       * @param file relative path to file in jar
       * @return Stream to specified file.
       */

      public static InputStream getResourceAsStream(String file)
      {
         return ClassLoader.getSystemClassLoader().getResourceAsStream(file);
      }
      
      /** Return a URL to a file in the jar.  This is a covience
       * function witch just calls a function by the same name in the
       * class loader.
       *
       * @param file relative path to file in jar
       * @return URL to specified file.
       */

      public static URL getResource(String file)
      {
         return ClassLoader.getSystemClassLoader().getResource(file);
      }

      /** For testing this program */

      public static void main(String[] args)
      {
         try
         {
            copyResource("resources/readme.txt", 
                         new File("/tmp/readme.txt"));
            loadLibrary("lib", "testLib");
         }
         catch (Exception e)
         {
            e.printStackTrace();
         }
      }
}
