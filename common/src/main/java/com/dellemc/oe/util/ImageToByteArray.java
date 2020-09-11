/**
 * Copyright (c) Dell Inc., or its subsidiaries. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */

package com.dellemc.oe.util;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.InputStream;
import java.net.URL;
import javax.imageio.ImageIO;
public class ImageToByteArray {
   public static  byte [] readImage() throws Exception{

      //File file =  new ImageToByteArray().getFileFromResources("nautilus.jpg");
      ClassLoader classloader = Thread.currentThread().getContextClassLoader();
      InputStream inputStream = classloader.getResourceAsStream("nautilus.jpg");

      BufferedImage bImage = ImageIO.read(inputStream);
      ByteArrayOutputStream bos = new ByteArrayOutputStream();
      ImageIO.write(bImage, "jpg", bos );
      byte [] data = bos.toByteArray();
      return data;
   }

   public static void createImage(byte[] data) throws Exception {
      ByteArrayInputStream bis = new ByteArrayInputStream(data);
      BufferedImage bImage2 = ImageIO.read(bis);
      ImageIO.write(bImage2, "jpg", new File("c:/tmp/output.jpg") );
      System.out.println("image created");
   }

   // get file from classpath, resources folder
   private File getFileFromResources(String fileName) {

      ClassLoader classLoader = getClass().getClassLoader();

      URL resource = classLoader.getResource(fileName);
      if (resource == null) {
         throw new IllegalArgumentException("file is not found!");
      } else {
         return new File(resource.getFile());
      }

   }
}