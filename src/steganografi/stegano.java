/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package stegano;
import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import static java.lang.Math.abs;
import static java.lang.Math.sqrt;
import java.nio.ByteBuffer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Random;
import java.util.Scanner;
import javax.imageio.ImageIO;


/**
 *
 * @author Fahziar
 */
public class Stegano {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) throws Exception{
        File input = new File("F://lena.bmp");
        Path path = Paths.get("F://input.txt");
        byte[] hiddenText = Files.readAllBytes(path);
        System.out.println(Integer.toBinaryString(hiddenText[0]));
        BufferedImage image = ImageIO.read(input);
        Stegano ss = new Stegano();

        ss.setImage(image);
        System.out.println("Capacity: " + Integer.toString(image.getHeight()*image.getWidth()*3));

        ByteBuffer bf = ByteBuffer.allocate(10 + hiddenText.length);
        bf.putChar('t');
        bf.putChar('x');
        bf.putChar('t');
        bf.putInt(hiddenText.length);
        System.out.println(hiddenText.length);
        bf.put(hiddenText);
        bf.flip();
        ss.insertData("hello", bf.array());

        ImageIO.write(ss.getImage(), "bmp", new File("F://lena2.bmp"));

        File output = new File("F://lena2.bmp");
        BufferedImage image2 = ImageIO.read(output);
        ss.setImage2(image2);

        byte[] metadata = ss.extractData("hello", 80);
        ByteBuffer bf3 = ByteBuffer.allocate(11);
        bf3.put(metadata);
        bf3.flip();
        char a, b, c;
        int size;
        a = bf3.getChar();
        b = bf3.getChar();
        c = bf3.getChar();
        size = bf3.getInt();
        byte[] notStegoed = ss.extractData("hello", (size + 10) * 8);
        bf.clear();
        ByteBuffer bf2 = ByteBuffer.allocate(notStegoed.length);
        bf2.put(notStegoed);
        bf2.flip();
        FileOutputStream fos = new FileOutputStream("F://hello2.txt");

        byte[] out = new byte[size];

        int i;
        for(i=0; i<3;i++)
        {
            bf2.getChar();
        }
        bf2.getInt();
        bf2.get(out);
        fos.write(out);
        fos.close();
        image = ImageIO.read(input);
        ss.setImage(image);
        byte[] t = Files.readAllBytes(Paths.get("F://lena.bmp"));
        byte[] h = Files.readAllBytes(Paths.get("F://lena2.bmp"));
        double psnr = ss.getPSNR(t, h);
        System.out.println("PSNR = " + psnr);
    }
    
    private BufferedImage image;
    private BufferedImage image2;
    private int currentBlockX;
    private int currentBlockY;
    
    public void setImage2(BufferedImage image2) {
        this.image2 = image2;
    }
    
    public  byte[] extractData(String key, int size){
        byte out[] = new byte[(size / 8)];
        int row = 0;
        int col = 0;
                
        int processed = 0;
        int currData = 0;
        int curByte = 0;
        System.out.println();
        System.out.println("Extracting data...");
        do{
            if (processed == size){break;}
            int rgb = image2.getRGB(row, col);
            int currBit = getBit(rgb, 16);
            currData = (currBit << (processed % 8)) + currData;  
            processed++;
            if ((processed != 0) && (processed % 8 == 0)){
                out[curByte] = (byte) currData;
                currData = 0;
                curByte++;
            }
            
            if (processed == size){break;}
            currBit = getBit(rgb, 8);
            currData = (currBit << (processed % 8)) + currData;      
            processed++;
            if (processed % 8 == 0) {
                out[curByte] = (byte) currData;
                currData = 0;
                curByte++;
            }
            if (processed == size){break;}
            currBit = getBit(rgb, 0);
            currData = (currBit << (processed % 8)) + currData;      
            processed++;
            if (processed % 8 == 0) {
                out[curByte] = (byte) currData;
                currData = 0;
                curByte++;
            }
            col++;
            if(col == image2.getWidth()){
                col = 0;
                row++;
            }
        } while (processed < size);
        return out;
    }
    
    public void insertData(String key, byte[] data){
        int row=0, col=0, savedRow=row, savedCol=col;
        int processed=0, countBit=0, idx=0, currentBit;
        int size = data.length*8;
        System.out.println("Inserting data...");
        do{ 
            if (processed == size){break;}
            if(countBit == 8){countBit=0; idx++;}
            int rgb = image.getRGB(row, col);
            currentBit = getBit(data[idx], countBit);
            rgb = setBit(rgb, 16, currentBit);
            processed++; countBit++;
            image.setRGB(row, col, rgb);
            
            if (processed == size){break;}
            if(countBit == 8){countBit=0; idx++;}
            currentBit = getBit(data[idx], countBit); 
            rgb = setBit(rgb, 8, currentBit);
            processed++; countBit++;
            
            if (processed == size){break;}
            if(countBit == 8){countBit=0; idx++;}
            currentBit = getBit(data[idx], countBit); 
            rgb = setBit(rgb, 0, currentBit);
            processed++; countBit++;
            
            image.setRGB(row, col, rgb);
            col++;
            if(col == image.getWidth()){
                col = 0;
                row++;
            }
        } while (processed<size);
    }
    
    public void insertDataRand(String key, byte[] data){
        int seed = 0;
        for (char c: key.toCharArray()){
            seed += (int) c;
        }
        Random rand = new Random(seed);
        int h = image.getHeight(); int w = image.getWidth();
        int i = abs(rand.nextInt()) % (h*w);
        int row=0, col=0, savedRow=row, savedCol=col;
        int processed=0, countBit=0, idx=0, currentBit;
        int size = data.length*8;
        System.out.println("Inserting data...");
        do{ 
            col = i%w; row = i/w; 
            System.out.println("" + col + " " + row);
            if (processed == size){break;}
            if(countBit == 8){countBit=0; idx++;}
            int rgb = image.getRGB(row, col);
            currentBit = getBit(data[idx], countBit);
            System.out.print(currentBit);
            rgb = setBit(rgb, 16, currentBit);
            processed++; countBit++;
            
            if (processed == size){break;}
            if(countBit == 8){countBit=0; idx++;}
            currentBit = getBit(data[idx], countBit); 
            System.out.print(currentBit);
            rgb = setBit(rgb, 8, currentBit);
            processed++; countBit++;
            
            if (processed == size){break;}
            if(countBit == 8){countBit=0; idx++;}
            currentBit = getBit(data[idx], countBit); 
            System.out.print(currentBit);
            rgb = setBit(rgb, 0, currentBit);
            processed++; countBit++;
            
            image.setRGB(row, col, rgb);
            i = abs(rand.nextInt()) % (h*w);
        } while (processed<size);
    }
    
    public  byte[] extractDataRand(String key, int size){
        int seed = 0;
        for (char c: key.toCharArray()){
            seed += (int) c;
        }
        Random rand = new Random(seed);
        int h = image.getHeight(); int w = image.getWidth();
        int i = abs(rand.nextInt()) % (h*w);
        byte out[] = new byte[(size / 8)];
        int row = 0;
        int col = 0;
                
        int processed = 0;
        int currData = 0;
        int curByte = 0;
        System.out.println();
        System.out.println("Extracting data...");
        do{
            col = i%w; row = i/w;
            System.out.println("" + col + " " + row);
            if (processed == size){break;}
            int rgb = image.getRGB(row, col);
            int currBit = getBit(rgb, 16);
            System.out.print(currBit);
            currData = (currBit << (processed % 8)) + currData;  
            processed++;
            if ((processed != 0) && (processed % 8 == 0)){
                out[curByte] = (byte) currData;
                currData = 0;
                curByte++;
            }
            
            if (processed == size){break;}
            currBit = getBit(rgb, 8);
            currData = (currBit << (processed % 8)) + currData;      
            processed++;
            if (processed % 8 == 0) {
                out[curByte] = (byte) currData;
                currData = 0;
                curByte++;
            }
            
            if (processed == size){break;}
            currBit = getBit(rgb, 0);
            System.out.print(currBit);
            currData = (currBit << (processed % 8)) + currData;      
            processed++;
            if (processed % 8 == 0) {
                out[curByte] = (byte) currData;
                currData = 0;
                curByte++;
            }
            i = abs(rand.nextInt()) % (h*w);
        } while (processed < size);
        return out;
    }
    
    public double getPSNR(byte[] image1, byte[] image2){
        double rms=0;
        int m=image1.length;
        for (int i=0; i<m; i++){
                int temp = (int) image1[i] - (int) image2[i]; 
                rms += Math.pow(temp, 2);
                System.out.println(Math.pow(temp, 2));
        }
        System.out.println(rms);
        System.out.println(m);
        rms = sqrt(rms/(double)(m));
        System.out.println(rms);
        return 20 * Math.log10((double) 256/rms);
    }
    
    private  int getBit(int input, int position)
    {
        return (input >> position) & 1;
    }
    
    private  int setBit(int input, int pos, int bit)
    {
        int output = input;
        if (bit == 1){
            output = output | (1 << pos);
        } else if (bit == 0) {
            output = output & ~(1 << pos);
        }
        
        return output;
    }
    public BufferedImage getImage() {
        return image;
    }

    /**
     * @param image the image to set
     */
    public void setImage(BufferedImage image) {
        this.image = image;
    }
}
