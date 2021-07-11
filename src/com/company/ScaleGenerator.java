package com.company;


import java.io.ByteArrayOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

/**
 * Generates a binary file that is 32 frames of scale.
 * Array of 16 bit word pairs in the format
 *  scale, scale_r
 * in little endian
 */
public class ScaleGenerator {



  public static void main(String... args) throws IOException {
    ByteBuffer buffer = ByteBuffer.allocate(32* 4);
    buffer.order(ByteOrder.LITTLE_ENDIAN);
    byte[] bytes = buffer.array();
    for (int i = 32; i > 0; i--) {
      var degrees = (double)i * (90d/32d);
      var scale = Math.sin(Math.toRadians(degrees));
      var scale_r = 1d/scale;
      System.out.println(String.format("%04X, %04X", (int)(scale * 256), (int)(scale_r * 256)));
      short fixPointScale =  (short)(scale * 256);
      short fixPointScaleR =  (short)(scale_r * 256);

      buffer.putShort((short) (fixPointScale & 0xFFFF));
      buffer.putShort((short) (fixPointScaleR & 0xFFFF));

    }

    FileOutputStream fos = new FileOutputStream("scale.bin");
    fos.write(buffer.array());
    fos.close();

  }

}
