package com.dellemc.oe.model;

import java.io.Serializable;

public class ImageData implements   Serializable{
    public byte[] message;

   public ImageData(byte[] message)
   {
       this.message = message;
   }
    @Override
    public String toString() {
        return "ImageData{" +
                "id='" + message + '\'' +
                '}';
    }
}
