package dk.sdu.mmmi.rd1.edgedetect;

import static dk.sdu.mmmi.rd1.edgedetect.Luminance.lum;
import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import javax.imageio.ImageIO;

/**
 *
 * @author ancla
 */
public class EdgeDetector {
     public static void main(String[] args) throws InterruptedException, IOException
    {   
        EdgeDetector ed = new EdgeDetector("D:\\Calimero1.jpg");
        int[][] ex = ed.getMagnitudeArray();
        
        FileWriter f = new FileWriter("D:\\CalimeroToBinary1.txt");
        
        for (int i = 0; i < ex.length; i++)
        {
            for (int j = 0; j < ex[i].length; j++)
            {
                if (ex[i][j] == 255)
                {
                    System.out.print(1 + " ");
                    String s = "1";
                    f.write(s + ", ");
                }
                else
                {
                    System.out.print(0 + " ");
                    String s = "0";
                    f.write(s + ", ");
                    
                }
                
                //System.out.print(ex[i][j] + " ");
            }
            System.out.println(" ");
            f.write(System.lineSeparator());
            
        }
        f.close();
         File file = new File("D:\\Calimero1.jpg");
        Image img;
         img = ImageIO.read(file);
         //BufferedImage scaledImage = Scalr.resize(img, 200);
         
        //BufferedImage bfm = createResizedCopy(img, 800, 600, false);
        
        
        //  image = ImageIO.read(new File("D:\\Calimero1.jpg"));
    }

    private final String imagePath;

    /**
     * 
     * @param imagePath Path of the image to perform edgedetection on.
     */
    public EdgeDetector(String imagePath) {
        this.imagePath = imagePath;
    }

    private int truncate(int a) {
        if (a < 127) {
            return 0;
        } else if (a >= 255) {
            return 255;
        } else {
            return a;
        }
    }

    private int binaryTruncate(int a) {
        if (a < 127) {
            return 0;
        } else {
            return 255;
        }
    }

    /**
     * This method performs edge-detection of the image on the path provided to the constructor, and returns a BufferedImage representation of the result.
     * @return A BufferedImage-object representation of the image provided.
     */
    public BufferedImage getBufferedImage() {

        int[][] filter1 = {
            {-1, 0, 1},
            {-2, 0, 2},
            {-1, 0, 1}
        };

        int[][] filter2 = {
            {1, 2, 1},
            {0, 0, 0},
            {-1, -2, -1}
        };

        Picture picture0 = new Picture(imagePath);
        int width = picture0.width()-2;
        int height = picture0.height()-2;
        Picture picture1 = new Picture(width, height);

        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {

                // get 3-by-3 array of colors in neighborhood
                int[][] gray = new int[3][3];
                for (int i = 0; i < 3; i++) {
                    for (int j = 0; j < 3; j++) {
                        gray[i][j] = (int) Luminance.intensity(picture0.get(x + i, y + j));
                    }
                }

                // apply filter
                int gray1 = 0, gray2 = 0;
                for (int i = 0; i < 3; i++) {
                    for (int j = 0; j < 3; j++) {
                        gray1 += gray[i][j] * filter1[i][j];
                        gray2 += gray[i][j] * filter2[i][j];
                    }
                }
                // int magnitude = 255 - truncate(Math.abs(gray1) + Math.abs(gray2));
                int magnitude = 255 - truncate((int) Math.sqrt(gray1 * gray1 + gray2 * gray2));
                Color grayscale = new Color(magnitude, magnitude, magnitude);
                picture1.set(x, y, grayscale);

            }
        }

        return picture1.getImage();
    }

    /**
     * This method performs edge-detection of the image on the path provided to the constructor, and returns a two-dimensional int-array representation of the result.
     * @return A two dimensional array showing the magnitude (intensity) in each pixel of the picture provided.
     */
    public int[][] getMagnitudeArray() {
        int[][] filter1 = {
            {-1, 0, 1},
            {-2, 0, 2},
            {-1, 0, 1}
        };

        int[][] filter2 = {
            {1, 2, 1},
            {0, 0, 0},
            {-1, -2, -1}
        };

        Picture picture0 = new Picture(imagePath);
        int width = picture0.width()-2;
        int height = picture0.height()-2;
        int[][] arrayRepresentation = new int[width][height];

        for (int y = 0; y < height ; y++) {
            for (int x = 0; x < width ; x++) {

                // get 3-by-3 array of colors in neighborhood
                int[][] gray = new int[3][3];
                for (int i = 0; i < 3; i++) {
                    for (int j = 0; j < 3; j++) {
                        gray[i][j] = (int) Luminance.intensity(picture0.get(x + i, y + j));
                    }
                }

                // apply filter
                int gray1 = 0, gray2 = 0;
                for (int i = 0; i < 3; i++) {
                    for (int j = 0; j < 3; j++) {
                        gray1 += gray[i][j] * filter1[i][j];
                        gray2 += gray[i][j] * filter2[i][j];
                    }
                }
                // int magnitude = 255 - truncate(Math.abs(gray1) + Math.abs(gray2));
                int magnitude = 255 - truncate((int) Math.sqrt(gray1 * gray1 + gray2 * gray2));
                arrayRepresentation[x][y] = magnitude;
            }
        }

        return arrayRepresentation;
    }

    /**
     * This method performs edge-detection of the image on the path provided to the constructor, and returns a two-dimensional Color-array representation of the result.
     * @return A two-dimensional array with Color objects, representing a greyscale interpretation of the image.
     */
    public Color[][] getGreyscaleArray() {
        int[][] filter1 = {
            {-1, 0, 1},
            {-2, 0, 2},
            {-1, 0, 1}
        };

        int[][] filter2 = {
            {1, 2, 1},
            {0, 0, 0},
            {-1, -2, -1}
        };

        Picture picture0 = new Picture(imagePath);
        //Find width, removing outer border due to filter
        int width = picture0.width()-2;
        int height = picture0.height()-2;
        Color[][] arrayRepresentation = new Color[width][height];

        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {

                // get 3-by-3 array of colors in neighborhood
                int[][] gray = new int[3][3];
                for (int i = 0; i < 3; i++) {
                    for (int j = 0; j < 3; j++) {
                        gray[i][j] = (int) Luminance.intensity(picture0.get(x + i, y + j));
                    }
                }

                // apply filter
                int gray1 = 0, gray2 = 0;
                for (int i = 0; i < 3; i++) {
                    for (int j = 0; j < 3; j++) {
                        gray1 += gray[i][j] * filter1[i][j];
                        gray2 += gray[i][j] * filter2[i][j];
                    }
                }
                // int magnitude = 255 - truncate(Math.abs(gray1) + Math.abs(gray2));
                int magnitude = 255 - truncate((int) Math.sqrt(gray1 * gray1 + gray2 * gray2));
                Color grayscale = new Color(magnitude, magnitude, magnitude);
                arrayRepresentation[x][y] = grayscale;
            }
        }

        return arrayRepresentation;
    }
   public BufferedImage createResizedCopy(Image originalImage, 
            int scaledWidth, int scaledHeight, 
            boolean preserveAlpha)
    {
        System.out.println("resizing...");
        int imageType = preserveAlpha ? BufferedImage.TYPE_INT_RGB : BufferedImage.TYPE_INT_ARGB;
        BufferedImage scaledBI = new BufferedImage(scaledWidth, scaledHeight, imageType);
        Graphics2D g = scaledBI.createGraphics();
        if (preserveAlpha) {
            g.setComposite(AlphaComposite.Src);
        }
        g.drawImage(originalImage, 0, 0, scaledWidth, scaledHeight, null); 
        g.dispose();
        return scaledBI;
    }
    
    
   
}
