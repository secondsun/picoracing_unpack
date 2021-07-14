package com.company;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.HashMap;
import java.util.Map;
import javax.imageio.ImageIO;

/**
 * This class generates palette and image binaries for the outrun clone.
 *
 * Images are using short words
 * The first two words are width/2 and height
 * Each word after that is two pixels. The colors are mapped from the palette.
 */
public class PaletteMain {

  public static final String TREE_PNG = "tree";
  public static final String START_1_PNG = "art_banner";
  public static final String START_2_PNG = "st_banner";
  public static final String PILLAR_PNG = "pillar";

  public static String[] files = {TREE_PNG, START_1_PNG, START_2_PNG, PILLAR_PNG};


  private static final int RGB_RED_SHIFT = 16;
  private static final int RGB_GREEN_SHIFT = 8;
  private static final int RGB_BLUE_SHIFT = 0;
  private static final int RED_MASK = 0x00FF0000;
  private static final int GREEN_MASK = 0x0000FF00;
  private static final int BLUE_MASK = 0x000000FF;
  static int[] colors = {0, 0x1D2B53, 0x7E2553, 0x008751, 0xAB5236, 0x5F574F, 0xC2C3C7, 0xFFF1E8,
      0xFF004D, 0xFFA300, 0xFFEC27, 0x00E436, 0x29ADFF, 0x83769C, 0xFF77A8, 0xFFCCAA};
  static Map<Integer, Integer> colorToIndexMap = new HashMap<>();


  public static void main(String... args) throws IOException {
    writePalette();
    writeImages();

  }

  private static void writeImages() throws IOException {
    for (String fileName : files) {
      var pngImage = ImageIO.read(new File(fileName + ".png"));
      try (var sfxData = new FileOutputStream(fileName + ".bin")) {
        sfxData.write(getImageBytes(pngImage));
      } catch (Exception e) {
        throw new RuntimeException("Error Processing " + fileName, e);
      }
    }

  }

  /**
   * two bytes, width/2, height
   * 4 bytes per height [skipLow][skipHigh][pad][length]
   * Image data. each row found by adding skip to the byte position of skipLow
   *
   * @return byteArray of the processed data for the image
   */
  private static byte[] getImageBytes(BufferedImage pngImage ) {

    ByteBuffer sfxData = ByteBuffer.allocate(2 + 4 * pngImage.getHeight() + 512*512*8);

    sfxData.order(ByteOrder.LITTLE_ENDIAN);

    var width = (byte)(0xFF&pngImage.getWidth()/2);
    var height = (byte)(0xFF&pngImage.getHeight());
    var bitmapStart = height * 4 - 1;
    //header
    sfxData.put(new byte[]{width, height});

    //table
    for (int y = 0; y < pngImage.getHeight(); y++) {
      sfxData.putShort((short)bitmapStart);
      var pad = getPadForLine(pngImage, y);
      var length = getLengthForLine(pngImage, y);
      bitmapStart += (length - 4);
      sfxData.put(pad);
      sfxData.put((byte) length);
    }
    //image data
    for (int y = 0; y < pngImage.getHeight(); y++) {
      sfxData.put(imageLineBytes(pngImage, y));
    }

    var outBuffer = ByteBuffer.allocate(sfxData.position());
    sfxData.flip();
    outBuffer.put(sfxData);

    return  outBuffer.array();
  }

  private  static byte[] imageLineBytes(BufferedImage pngImage, int y) {

    var pad = getPadForLine(pngImage, y);
    var length = getLengthForLine(pngImage, y);
    var sfxData = new ByteArrayOutputStream();
    for (int x = pad; x < (pad + length); x++) {
      try {
        var pixelHigh = pngImage.getRGB(x, y) & 0x00FFFFFF;
        var pixelLow = pngImage.getRGB(++x, y) & 0x00FFFFFF;

        var indexHigh = colorToIndexMap.get(pixelHigh);
        var indexLow = colorToIndexMap.get(pixelLow);
        if (indexHigh == null || indexLow == null) {
          throw new IllegalStateException(
              String.format("null index for color %x ", pixelLow) + String
                  .format("null index for color %x", pixelHigh));

        } else {
          byte b = (byte) ((indexHigh & 0b1111) << 4 | (indexLow & 0b1111));
          sfxData.write(b);
        }
      } catch (Exception e) {
        throw new RuntimeException(e);
      }
    }
    return sfxData.toByteArray();
  }

  /**
   * Length is how many pixels are bytween the leading and trailing blackness
   * @param pngImage imageData
   * @param y line
   * @return length of pixels to draw
   */
  private static byte getLengthForLine(BufferedImage pngImage, int y) {
    byte startImageData = getPadForLine(pngImage, y);
    byte endImageData = (byte) pngImage.getWidth();
    for (byte x = (byte) (pngImage.getWidth() - 1); x >= 0; x--) {
      var pixel = pngImage.getRGB(x, y) & 0x00FFFFFF;
      if (pixel == 0) {
        endImageData = x;
      } else {
        break;
      }
    }
    return (byte) Math.max(endImageData - startImageData, 0);
  }

  /**
   * Pad is how many leading black pixels are in the image
   * @param pngImage imageData
   * @param y line
   * @return number of leading black pixels
   */
  private  static byte getPadForLine(BufferedImage pngImage, int y) {
    byte pad = 0;
    for (int x = 0; x < pngImage.getWidth(); x++) {
      var pixel = pngImage.getRGB(x, y) & 0x00FFFFFF;
      if (pixel == 0) {
        pad++;
      } else {
        break;
      }
    }
    return (byte)(((pad>>1)<<1) & 0xFF);
  }

  private static void writePalette() throws IOException {
    BufferedImage imageOut = new BufferedImage(16 * 64, 64, BufferedImage.TYPE_INT_RGB);
    ByteBuffer buffer = ByteBuffer.allocate(32);
    buffer.order(ByteOrder.LITTLE_ENDIAN);
    int outint = 0;

    for (int i = 0; i < colors.length; i++) {
      int color = colors[i];

      var gfx = imageOut.getGraphics();
      var rgb15 = to5bitRGB(color);
      var bgr15 = to5bitBGR(color);
      var expanded5bitrgb = to8bitRGB(rgb15);
      gfx.setColor(new Color(expanded5bitrgb));
      gfx.fillRect(i * 64, 0, 64, 64);
      gfx.setColor(Color.WHITE);
      gfx.drawString(i + "", (i * 64) + 16, 16);
      buffer.putShort((short) bgr15);
      colorToIndexMap.put(expanded5bitrgb, i);
      System.out.println(String.format("%x becomes %x", colors[i], expanded5bitrgb));
    }
    ImageIO.write(imageOut, "PNG", new File("test.png"));
    var fos = new FileOutputStream("palette.bin");
    fos.write(buffer.array());
    fos.close();

  }

  private static int to8bitRGB(int rgb15) {
    int r = ((rgb15 & 0b111110000000000) >> 10);
    int g = ((rgb15 & 0b000001111100000) >> 5);
    int b = ((rgb15 & 0b000000000011111));
    return r << 19 | g << 11 | b << 3;
  }

  static int to5bitRGB(int rgb8) {
    int r = ((rgb8 & RED_MASK) >> RGB_RED_SHIFT + 3);
    int g = ((rgb8 & GREEN_MASK) >> RGB_GREEN_SHIFT + 3);
    int b = ((rgb8 & BLUE_MASK) >> RGB_BLUE_SHIFT + 3);
    return r << 10 | g << 5 | b;
  }

  static int to5bitBGR(int rgb8) {
    int r = ((rgb8 & RED_MASK) >> RGB_RED_SHIFT + 3);
    int g = ((rgb8 & GREEN_MASK) >> RGB_GREEN_SHIFT + 3);
    int b = ((rgb8 & BLUE_MASK) >> RGB_BLUE_SHIFT + 3);
    return b << 10 | g << 5 | r;
  }

}