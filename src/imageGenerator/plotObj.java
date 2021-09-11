package imageGenerator;

import java.awt.Color;

import java.awt.Graphics;
import java.util.Random;
import javax.swing.JFrame;
import java.awt.image.BufferedImage;
import java.io.File;
import java.nio.file.Paths;
import java.awt.Graphics2D;
import javax.imageio.ImageIO;
import java.awt.GradientPaint;

public class plotObj extends JFrame{
    /**
     *
     */
	// global values
	int width = 416;
	int height = 416;
	int numOfImages = 50;
    
    private static final long serialVersionUID = 1L;

    public plotObj() throws Exception
    {
    	String rootFolder = System.getProperty("user.dir");
    	String filePath = rootFolder + "/samples/sampleImage";

        setSize(width,height);

        //pack();
        setVisible(true);
        BufferedImage img = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        Graphics gr = img.createGraphics();
        //dispose();
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        String filePathi = filePath;
		for (int i = 0; i < numOfImages; i++) {
			paint(gr);
			int fileNum = i;
			String fileNameEnd = String.valueOf(fileNum);
			filePathi = filePath + fileNameEnd + ".png";
			File file = new File(filePathi);
			ImageIO.write(img, "png", file);
		}

		return;
    }

    public void Create_Background (Graphics2D g)
    {
        for(int i = 0; i < width; i++) {
            for (int j = 0; j < height; j++) {
                Color backgroundColor = Dark_RGB_Gen();
                g.setColor(backgroundColor);
                g.fillRect(i, j, 1, 1);
            }
        }
    }

    public void paint (Graphics g)
    {
        Graphics2D g2d = (Graphics2D) g;
        Create_Background(g2d); // create noisy background

        // plot objects
        int objNum = (int)(Get_Controlled_Gaussian(0,200));
        objNum = Math.abs(objNum);
        System.out.printf("# of Objects: %d \n", objNum);
        int oMeanx = 0;
        int oStdx = 0;
        int oMeany = 0;
        int oStdy = 0;
        for (int i = 0; i < objNum; i++)
        {
            // generate location
            oMeany = height/2;
            oStdy = height*2;
            oMeanx = width/2;
            oStdx = width*2;
            int x = Math.abs( (int)(Get_Controlled_Gaussian(oMeanx,oStdx)));
            int y = Math.abs(  (int)(Get_Controlled_Gaussian(oMeany,oStdy)));
            // draw object
            if ((x < width) && (y < height)) {
                Draw_Object(g2d,x,y);
            }
        }


        // implement noise
        int scatNum = (int)(Math.abs(Get_Controlled_Gaussian(320,12280)));
        System.out.printf("# of Scatter: %d \n", objNum);
        for (int i = 0; i < scatNum; i++)
        {
            // generate location
            oMeany = height/2;
            oStdy = height*2;
            oMeanx = width/2;
            oStdx = width*2;
            int x = (int)(Get_Controlled_Gaussian(oMeanx,oStdx));
            int y = (int)(Get_Controlled_Gaussian(oMeany,oStdy));
            // draw object
            Plot_Noise(g2d,x,y);
        }

    }

    public void Draw_Object (Graphics2D g, int x, int y)
    {
        Color objectColor = Virus_RGB();
        Color bgColor = Dark_RGB_Gen();
        int objDiam1 = 0;
        int objDiam2 = 0;
        int maxD1 = objDiam1;
        int maxD2 = objDiam2;
        int oMean = randGen(0,6);
        int oStd = randGen(0, 5);
        int i = 0;
        int drawNum = Math.abs( (int)(Get_Controlled_Gaussian(oMean,oStd)));
        System.out.printf("Location: %d,%d \n", x, y);
        //System.out.printf("Plots per spot: %d\n", drawNum);
        for (i = 0; i < drawNum; i++)
        {
            objectColor = Virus_RGB();
            bgColor = Dark_RGB_Gen();
            oMean = randGen(3,7);
            oStd = randGen(0, 3);
            objDiam1 = (int)(Get_Controlled_Gaussian(oMean,oStd));
            objDiam2 = (int)(Get_Controlled_Gaussian(oMean,oStd));
            maxD1 = Math.max(maxD1, objDiam1);
            maxD2 = Math.max(maxD2, objDiam2);
            if ( (x > 0) && (x < width) && (y > 0) && (y < height)) {
                int fadeconstant = 8;
                for(int j = 0; j < 100; j++){
                    Color fadecolor = new Color(objectColor.getRed(), objectColor.getGreen(), objectColor.getBlue(), Math.min(255, (int)(2 * ((double)j))));
                    g.setColor(fadecolor);
                    g.fillOval(x + (objDiam1) / 2 - (objDiam1 / ((j + fadeconstant) / fadeconstant)) / 2 + (int)(Get_Controlled_Gaussian(0,3)), y + (objDiam2) / 2 - (objDiam2 / ((j + fadeconstant) / fadeconstant))/ 2 + (int)(Get_Controlled_Gaussian(0,3)), objDiam1 / ((j + fadeconstant) / fadeconstant), objDiam2 / ((j + fadeconstant) / fadeconstant));
                }
            }
        }
    }

    public void Plot_Noise (Graphics2D g, int x, int y)
    {

        int oMean = randGen(0,6);
        int oStd = randGen(0, 5);
        int drawNum = (int)(Get_Controlled_Gaussian(oMean,oStd));
        for (int i = 0; i < drawNum; i++)
        {
            Color noiseColor = GrayScale_Gen();
            g.setColor(noiseColor);
            int objDiam1 = (int)(Math.abs(Get_Controlled_Gaussian(0,2)));
            int objDiam2 = (int)(Math.abs(Get_Controlled_Gaussian(0,2)));
            g.fillOval(x, y, objDiam1, objDiam2);
            //System.out.printf("R%d,G%d, B%d \n", R,G,B);
        }
    }

    public void Fade_Object(int x, int y, int d1, int d2, Color col, Graphics2D g)
    {
        // get current RGB values
        int R = col.getRed();
        int G = col.getGreen();
        int B = col.getBlue();
        // get starting diameter
        int div = Math.abs( ((int)(Get_Controlled_Gaussian(0, d1))));
        if (div == 0) {
            div = 1;
        }
        int currD1 = d1 / div;
        while(currD1 > d1) {
            div = Math.abs( ((int)(Get_Controlled_Gaussian(0, d1))));
            if (div == 0) {
                div = 1;
            }
            currD1 = d1 / div;
        }
        if (currD1 < 1) {
            currD1 = 1;
        }
        div = Math.abs( ((int)(Get_Controlled_Gaussian(0, d2))));
        if (div == 0) {
            div = 1;
        }
        int currD2 = d2 / div;
        while(currD2 > d2) {
            div = Math.abs( ((int)(Get_Controlled_Gaussian(0, d2))));
            if (div == 0) {
                div = 1;
            }
            currD2 = d2 / div;
        }
        if (currD2 < 1) {
            currD2 = 1;
        }
        // plot
        g.setColor(col);
        g.fillOval(x,y,currD1, currD2);
        // plot bigger objects around
        while (currD1 <= d1) {
            if (currD2 >= d2) {
                break;
            }
            currD1 += Math.abs((int)(Get_Controlled_Gaussian(0,3)));
            currD2 += Math.abs((int)(Get_Controlled_Gaussian(0,3)));
            R = Subtract_RGB(R);
            G = Subtract_RGB(G);
            B = Subtract_RGB(B);
            Color fadeTone = new Color(R,G,B);
            Color darkTone = Dark_RGB_Gen();
            //g.setColor(fadeTone);
            GradientPaint gradient = new GradientPaint(x, y, fadeTone, x+d2, y-d1, darkTone);
            g.setPaint(gradient);
            g.fillOval(x,y,currD1, currD2);
        }
    }

    public void Fade_Object2(int x, int y, int d1, int d2, Color col, Graphics2D g)
    {
        int R = col.getRed();
        int G = col.getGreen();
        int B = col.getBlue();
        int currX = x;
        int currY = y;
        //System.out.printf("Begin fading: R%d,G%d, B%d \n", R,G,B);

        // fade x-axis
        // to the right
        if (x < (width-d1)) {
            for (int i = 0; i < d1; i++) {
                //System.out.printf("Begin fading right: R%d,G%d, B%d \n", R,G,B);
                currX = x+i;
                R = Subtract_RGB(R);
                G = Subtract_RGB(G);
                B = Subtract_RGB(B);
                Color fadeTone = new Color(R,G,B);
                //g.drawOval(currX,y,1,1);
                GradientPaint gradient = new GradientPaint(currX,y, col, x+d1, y, fadeTone);
                g.setPaint(gradient);
                g.fillOval((currX + (x + d1)) / 2, y, Math.abs(currX - (x + d1)) / 2, 1);
                //System.out.printf("change pixel at x: %d; y: %d\n", x+i, y);
                //System.out.printf("R%d,G%d, B%d \n", R,G,B);
            }
        }

        // to the left
        if (x > d1) {
            R = col.getRed();
            G = col.getGreen();
            B = col.getBlue();
            for (int i = x; i > x-d1; i--) {
                //System.out.printf("Begin fading left: R%d,G%d, B%d \n", R,G,B);
                currX = x-i;
                R = Subtract_RGB(R);
                G = Subtract_RGB(G);
                B = Subtract_RGB(B);
                Color fadeTone = new Color(R,G,B);
                GradientPaint gradient = new GradientPaint(currX,y, col, x-d1, y, fadeTone);
                g.setPaint(gradient);
                g.fillOval((currX + (x - d1)) / 2, y, Math.abs(currX - (x - d1)) / 2, 1);
                //System.out.printf("change pixel at x: %d; y: %d\n", currX, y);
                //System.out.printf("R%d,G%d, B%d \n", R,G,B);
            }
        }

        // up
        if (y < (height-d2)) {
            R = col.getRed();
            G = col.getGreen();
            B = col.getBlue();
            for (int i = 0; i < d2; i++) {
                System.out.printf("Begin fading up: R%d,G%d, B%d \n", R,G,B);
                currY = y+i;
                R = Subtract_RGB(R);
                G = Subtract_RGB(G);
                B = Subtract_RGB(B);
                Color fadeTone = new Color(R,G,B);
                GradientPaint gradient = new GradientPaint(x,currY, col, x, y+d2, fadeTone);
                g.setPaint(gradient);
                g.fillOval(x, (currY + (y + d2)) / 2, 1, Math.abs(currY - (y + d2)) / 2);
                //System.out.printf("change pixel at x: %d; y: %d\n", x, currY);
                //System.out.printf("R%d,G%d, B%d \n", R,G,B);
            }
        }

        // down

        if (y > d2) {
            R = col.getRed();
            G = col.getGreen();
            B = col.getBlue();
            for (int i = y; i > y-d2; i--) {
                System.out.printf("Begin fading down: R%d,G%d, B%d \n", R,G,B);
                currY = y-i;
                R = Subtract_RGB(R);
                G = Subtract_RGB(G);
                B = Subtract_RGB(B);
                Color fadeTone = new Color(R,G,B);
                GradientPaint gradient = new GradientPaint(x,currY, col, x, y-d2, fadeTone);
                g.setPaint(gradient);
                g.fillOval(x, (currY + (y - d2)) / 2, 1, Math.abs(currY - (y - d2)) / 2);
                //System.out.printf("change pixel at x: %d; y: %d\n", x+i, y);
                //System.out.printf("R%d,G%d, B%d \n", R,G,B);
            }
        }
    }

    public int Subtract_RGB (int RGB)
    {
        int subRGB = (int)(Get_Controlled_Gaussian(30,10));
        subRGB = Math.abs(subRGB);
        int returnVal = RGB - subRGB;
        if (returnVal < 0) {
            returnVal = 0;
        }
        return returnVal;
    }

    public Color GrayScale_Gen()
    {
        int RGBval = (int)(Get_Controlled_Gaussian(0,510));
        RGBval = Math.abs(RGBval);
        while (RGBval > 255) {
            RGBval = (int)(Get_Controlled_Gaussian(0,510));
            RGBval = Math.abs(RGBval);
        }
        //System.out.printf("RGB: %d \n", RGBval);
        Color newColor = new Color (RGBval, RGBval, RGBval);
        return newColor;
    }

    public Color Dark_RGB_Gen()
    {
        int arrRGB[] = new int[3]; // R=0, G=1, B=2
        for (int i = 0; i < 3; i++) {
            int RGBval = (int)(Get_Controlled_Gaussian(0,13));
            RGBval = Math.abs(RGBval);
            arrRGB[i] = RGBval;
        }
        Color newColor = new Color (arrRGB[0], arrRGB[1], arrRGB[2]);
        return newColor;

    }

    public Color Blue_RGB_Gen()
    {
        int arrRGB[] = new int[3]; // R=0, G=1, B=2
        arrRGB[2] = 255;
        for (int i = 0; i < 2; i++) {
            int RGBval = (int)(Get_Controlled_Gaussian(0,36));
            RGBval = Math.abs(RGBval);
            arrRGB[i] = RGBval;
        }
        Color blueColor = new Color (arrRGB[0], arrRGB[1], arrRGB[2]);
        return blueColor;
    }

    public Color Virus_RGB()
    {
        int arrRGB[] = new int[3]; // R=0, G=1, B=2
        // Green
        arrRGB[1] = (int)(Get_Controlled_Gaussian(255,50));
        while (arrRGB[1] > 255) {
            arrRGB[1] = (int)(Get_Controlled_Gaussian(255,50));
        }
        // Red
        arrRGB[0] = (int)(Get_Controlled_Gaussian(0,50));
        arrRGB[0] = Math.abs(arrRGB[0]);
        //Blue
        arrRGB[2] = (int)(Get_Controlled_Gaussian(0,50));
        arrRGB[2] = Math.abs(arrRGB[2]);
        // create color
        Color virusColor = new Color (arrRGB[0], arrRGB[1], arrRGB[2]);
        return virusColor;
    }

    public double Get_Gaussian()
    {
        // mean = 0
        // std = 1;
        Random randomObj = new Random();
        double gaussVal = randomObj.nextGaussian();
        double returnVal = Math.abs(gaussVal);

        return returnVal;
    }

    public double Get_Controlled_Gaussian(double gMean, double gStd)
    {
        Random randomObj = new Random();
        double gaussVal = randomObj.nextGaussian(); // mean = 0, std = 1
        gaussVal *= gStd;
        gaussVal = gaussVal + gMean;

        return gaussVal;
    }

    public int randGen(int min, int max)
    {
        int random_int = (int)Math.floor(Math.random()*(max-min+1)+min);
        System.out.println(random_int);
        return random_int;
    }


    public static void main(String[] args) throws Exception {
        plotObj o = new plotObj();

        //System.exit(0);

    }

}
