import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;

public class PopulationDistributionProducer {

    public Integer[][] getMap() {
        return map;
    }

    private Integer[][] map;
    //private Color[] colors;

    PopulationDistributionProducer(String state, int size) throws IOException {
        String inputImage = "states/" + state + ".png";
        String outputImage = "small.png";

        BufferedImage resized = resize(inputImage, outputImage, size, size);
//        BufferedImage resizedColors = resize("ranges.png", "newSmall.png", 100, 100);
//        colors = getColors(ImageIO.read(new File("newSmall.png")));
        //System.out.println(Arrays.toString(colors));
        map = readAndBuildArray(size, resized);
    }

    private int getNum(Color color){
        double weight = .8;
        if(color.getRed() > color.getGreen())
            return (int) ((250 - color.getGreen()) * .8);
        else
            return (int) ((250 - color.getRed()) * .1);
        //return ((color.getRed() - color.getGreen()) * color.getBlue()/1000) + 13;
    }

    public static Color[] getColors(BufferedImage image){
        Color[] temp = new Color[12];
        temp[0] = new Color(26,152,80);
        temp[1] = new Color(36,151,83);
        temp[2] = new Color(83, 176, 97);
        temp[3] = new Color(129, 199, 106);
        temp[4] = new Color(172, 217, 115);
        temp[5] = new Color(207, 233, 137);
        temp[6] = new Color(235, 230, 144);
        temp[7] = new Color(252, 213, 136);
        temp[8] = new Color(251, 178, 108);
        temp[9] = new Color(245, 135, 86);
        temp[10] = new Color(233, 91, 65);
        temp[11] = new Color(252, 13, 27);
        return temp;
    }

    private boolean isInRange(Color color){
        if(
                (color.getBlue() > 0 && color.getBlue() < 110 || color.getRed() > 200)
                && !(color.getRed() == 112 && color.getGreen() == 112 && color.getBlue() == 112)
                && !(color.getRed() == 96 && color.getGreen() == 96 && color.getBlue() == 96))
            return true;
        return false;
    }

    private Integer[][] readAndBuildArray(int size, BufferedImage resizedImg) {
        Integer[][] temp = new Integer[size][size];

//        Color first = new Color(96, 96, 96);
//        Color second = new Color(139, 176, 242);
        //Color third = new Color(170, 156, 128);

        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                Color color = new Color(resizedImg.getRGB(j, i));
                //int num = getNum(color);
                if(isInRange(color))
                    temp[j][i] = getNum(color);
                else
                    temp[j][i] = 0;
            }
        }

        for (int i = 0; i < temp.length; i++) {
            for (int i1 = 0; i1 < temp[i].length; i1++)
                System.out.print(temp[i1][i] + " ");
            System.out.println();
        }

        return temp;
    }

    /**
     * Resizes an image to a absolute width and height (the image may not be
     * proportional)
     *
     * @param inputImagePath  Path of the original image
     * @param outputImagePath Path to save the resized image
     * @param scaledWidth     absolute width in pixels
     * @param scaledHeight    absolute height in pixels
     * @throws IOException
     */

    public static BufferedImage resize(String inputImagePath,
                                       String outputImagePath, int scaledWidth, int scaledHeight)
            throws IOException, IOException {
        // reads input image
        File inputFile = new File(inputImagePath);
        BufferedImage inputImage = ImageIO.read(inputFile);

        // creates output image
        BufferedImage outputImage = new BufferedImage(scaledWidth,
                scaledHeight, inputImage.getType());

        // scales the input image to the output image
        Graphics2D g2d = outputImage.createGraphics();
        g2d.drawImage(inputImage, 0, 0, scaledWidth, scaledHeight, null);
        g2d.dispose();

        // extracts extension of output file
        String formatName = outputImagePath.substring(outputImagePath
                .lastIndexOf(".") + 1);

        // writes to output file
        ImageIO.write(outputImage, formatName, new File(outputImagePath));

        return outputImage;
    }
}

