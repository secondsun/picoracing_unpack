package com.company;

import java.awt.Color;
import java.awt.Graphics;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.WindowConstants;

public class ScaleMain {

  private static int zoom = 0x1;
  private static int zoomIncr = 0x1;
  private static int zoomedInIncr = ((32<<6)/zoom);


  private static int counter = 1;

  public static void main(String... args) throws IOException {
    final var image = ImageIO.read(new File("tree.png"));
    JFrame main = new JFrame("Main");
    main.setSize(320,320);
    main.setContentPane(new JPanel(){
      @Override
      public void paint(Graphics g) {
        super.paint(g);
        g.setColor(Color.BLACK);
        g.fillRect(0,0,getWidth(),getHeight());
        int nextX = 0;
        int nextY = 0;
        for (int x = 0; x < (64<<6); x+=zoomedInIncr) {
          nextY = 0;
          for (int y = 0; y < (64<<6); y+=zoomedInIncr) {
            g.setColor(new Color(image.getRGB(x>>6,y>>6)));
            g.fillRect(nextX,nextY,1,1);
            nextY++;

          }
          nextX++;
        }


        zoom++;
        zoomedInIncr = ((32<<6)/zoom);
        counter++;
        if (counter > 128) {
          counter = 1;
          zoom = 0x1;
          zoomedInIncr = ((32<<6)/zoom);
        }
        try {
          Thread.sleep(1/30);
        } catch (InterruptedException e) {
          e.printStackTrace();
        }
        repaint();
      }
    });
    main.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
    main.setVisible(true);
  }

}
