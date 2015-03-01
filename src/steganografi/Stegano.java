/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package stegano;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileOutputStream;
import static java.lang.Math.abs;
import static java.lang.Math.sqrt;
import java.nio.ByteBuffer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Random;
import javax.imageio.ImageIO;

/**
 *
 * @author Fahziar
 */
public class Stegano {
    private BufferedImage image;
    private BufferedImage image2;
    
    public void setImage(BufferedImage image) {
        this.image = image;
    }
    
    public void setImage2(BufferedImage image2) {
        this.image2 = image2;
    }
    
    public BufferedImage getImage() {
        return image;
    }
    
    //Mendapatkan kapasistas data yang dapat disimpan pada gambar grayscale (dalam bit)
    private int getCapacityGray(){
        return image.getHeight()*image.getWidth()-80;
    }
    
    //Mendapatkan kapasistas data yang dapat disimpan pada gambar truecolor (dalam bit)
    private int getCapacityTrue(){
        return image.getHeight()*image.getWidth()*3-80;
    }
    
    private  int getBit(int input, int position){
        return (input >> position) & 1;
    }
    
    private  int setBit(int input, int pos, int bit){
        int output = input;
        if (bit == 1){
            output = output | (1 << pos);
        } else if (bit == 0) {
            output = output & ~(1 << pos);
        }
        return output;
    }
    
    //Menyembunyikan data ke dalam gambar truecolor secara sekuensial
    public void insertData(String key, byte[] data){
        int row=0, col=0;
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
            
            image.setRGB(row, col, rgb);
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
    
    //Mengekstraksi data dari gambar truecolor secara sekuensial
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
    
    //Menyembunyikan data ke dalam gambar grayscale secara sekuensial
    public void insertDataGray(String key, byte[] data){
        int row=0, col=0;
        int processed=0, countBit=0, idx=0, currentBit;
        int size = data.length*8;
        System.out.println("Inserting data...");
        do{ 
            if(countBit == 8){countBit=0; idx++;}
            int rgb = image.getRGB(row, col);
            currentBit = getBit(data[idx], countBit);
            rgb = setBit(rgb, 16, currentBit);
            rgb = setBit(rgb, 8, currentBit); 
            rgb = setBit(rgb, 0, currentBit);
            image.setRGB(row, col, rgb);
            processed++; countBit++;
            col++;
            if(col == image.getWidth()){
                col = 0;
                row++;
            }
        } while (processed<size);
    }
    
    //Mengekstraksi data dari gambar grayscale secara sekuensial
    public  byte[] extractDataGray(String key, int size){
        byte out[] = new byte[(size / 8)];
        int row = 0;
        int col = 0;
                
        int processed = 0;
        int currData = 0;
        int curByte = 0;
        System.out.println();
        System.out.println("Extracting data...");
        do{
            int rgb = image2.getRGB(row, col);
            int currBit = getBit(rgb, 8);
            currData = (currBit << (processed % 8)) + currData;  
            processed++;
            if ((processed != 0) && (processed % 8 == 0)){
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
    
    //Menghitung PSNR
    public double getPSNR(byte[] image1, byte[] image2){
        double rms=0;
        int m=image1.length;
        for (int i=0; i<m; i++){
                int temp = (int) image1[i] - (int) image2[i]; 
                rms += Math.pow(temp, 2);
        }
        System.out.println(rms);
        System.out.println(m);
        rms = sqrt(rms/(double)(m));
        System.out.println(rms);
        return 20 * Math.log10((double) 256/rms);
    }
    
    public static void main(String[] args) throws Exception{
    //Menyembunyikan data
        File input = new File("F://lena512.bmp");
        Path path = Paths.get("F://input.txt");
        byte[] hiddenText = Files.readAllBytes(path);
        BufferedImage image = ImageIO.read(input);
        BufferedImage biCopy = new BufferedImage(image.getWidth(), image.getHeight(), BufferedImage.TYPE_INT_RGB);
        for (int x = 0; x < image.getWidth(); x++) {
            for (int y = 0; y < image.getHeight(); y++) {
                biCopy.setRGB(x, y, image.getRGB(x, y));
            }
        }
        Stegano ss = new Stegano();
        
        ss.setImage(biCopy);
        System.out.println("Capacity: " + ss.getCapacityGray());

        ByteBuffer bf = ByteBuffer.allocate(10 + hiddenText.length);
        bf.putChar('t');
        bf.putChar('x');
        bf.putChar('t');
        bf.putInt(hiddenText.length);
        System.out.println(hiddenText.length);
        bf.put(hiddenText);
        bf.flip();
        ss.insertDataGray("hello", bf.array());

        ImageIO.write(ss.getImage(), "bmp", new File("F://lena5122.bmp"));
    
    //Mengekstraksi data
        File output = new File("F://lena5122.bmp");
        BufferedImage image2 = ImageIO.read(output);
        BufferedImage biCopy2 = new BufferedImage(image2.getWidth(), image2.getHeight(), BufferedImage.TYPE_INT_RGB);
        for (int x = 0; x < image2.getWidth(); x++) {
            for (int y = 0; y < image2.getHeight(); y++) {
                biCopy2.setRGB(x, y, image2.getRGB(x, y));
            }
        }
        ss.setImage2(biCopy2);

        byte[] metadata = ss.extractDataGray("hello", 80);
        ByteBuffer bf3 = ByteBuffer.allocate(11);
        bf3.put(metadata);
        bf3.flip();
        char a, b, c;
        int size;
        a = bf3.getChar();
        b = bf3.getChar();
        c = bf3.getChar();
        size = bf3.getInt();
        System.out.println("size= "+ size);
        byte[] notStegoed = ss.extractDataGray("hello", (size + 10) * 8);
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
    
    //Menghitung PSNR
        byte[] t = Files.readAllBytes(Paths.get("F://lena512.bmp"));
        byte[] h = Files.readAllBytes(Paths.get("F://lena5122.bmp"));
        double psnr = ss.getPSNR(t, h);
        System.out.println("PSNR = " + psnr);
    }
}
