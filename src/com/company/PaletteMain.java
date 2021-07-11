package com.company;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.HashMap;
import java.util.Map;
import javax.imageio.ImageIO;

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
    writeTree();

  }

  private static void writeTree() throws IOException {
    for (String fileName : files) {
      var pngImage = ImageIO.read(new File(fileName + ".png"));
      try (var sfxData = new FileOutputStream(fileName + ".bin")) {
System.out.println(String.format("Converting %s, %d x %d", fileName, pngImage.getWidth(), pngImage.getHeight()));
        sfxData.write(new byte[]{(byte)(0xFF&pngImage.getWidth()/2), (byte)(0xFF&pngImage.getHeight())});
        for (int y = 0; y < pngImage.getHeight(); y++) {
          for (int x = 0; x < pngImage.getWidth(); x++) {
            var pixelHigh = pngImage.getRGB(x, y) & 0x00FFFFFF;
            var pixelLow = pngImage.getRGB(++x, y) & 0x00FFFFFF;
            var indexHigh = colorToIndexMap.get(pixelHigh);
            var indexLow = colorToIndexMap.get(pixelLow);
            if (indexHigh == null || indexLow == null) {
              throw new IllegalStateException(
                  String.format("null index for color %x ", pixelLow) + String
                      .format("null index for color %x", pixelHigh) +String.format("For File %s", fileName));

            } else {
              byte b = (byte) ((indexHigh & 0b1111) << 4 | (indexLow & 0b1111));
              sfxData.write(new byte[]{b});
            }
          }
        }
      }
    }

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