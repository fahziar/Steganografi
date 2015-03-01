/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package steganografi;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileOutputStream;
import java.nio.ByteBuffer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Scanner;
import javax.imageio.ImageIO;


/**
 *
 * @author Fahziar
 */
public class NineDiffStego {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) throws Exception{
        // TODO code application logic here
        
        Scanner n = new Scanner(System.in);
        
        int pilihan = n.nextInt();
        
        if (pilihan == 1)
        {
            File input = new File("D://lena512.bmp");
            Path path = Paths.get("D://mpi_hostfile");
            
            byte[] hiddenText = Files.readAllBytes(path);
            
            BufferedImage image = ImageIO.read(input);
            BufferedImage image2 = ImageIO.read(input);
            
            if (image.getType() == BufferedImage.TYPE_3BYTE_BGR)
            {
                System.out.println("Berwarna");
            } else if (image.getType() == BufferedImage.TYPE_BYTE_GRAY)
            {
                System.out.println("Hitam putih");
            }
            NineDiffStego ss = new NineDiffStego();
            
            System.out.println("Width:" + image.getWidth() + " Height" + image.getHeight());
            ss.setImage(image);
            ss.setImage2(image2);
            System.out.println("NbTileX:" + ss.getNbTileX());
            System.out.println("Capacity: " + Integer.toString(ss.getCapacityGrayscale() /8 / 1024));
            
            ByteBuffer bf = ByteBuffer.allocate(10 + hiddenText.length);
            bf.putChar('t');
            bf.putChar('x');
            bf.putChar('t');
            bf.putInt(hiddenText.length);
            bf.put(hiddenText);
            bf.flip();
            byte output[] = bf.array();
            if (output.length * 8 > ss.getCapacity())
            {
                System.out.println("File kegedean");
            } else {
                ss.stegoGrayscale("hello", output);
            }
            
            ImageIO.write(ss.getImage(), "bmp", new File("D://lena2.bmp"));
        } else {
            File input = new File("D://lena2.bmp");
        
        
            BufferedImage image = ImageIO.read(input);
            BufferedImage image2 = ImageIO.read(input);
            NineDiffStego ss = new NineDiffStego();
                        
            ss.setImage(image);
            ss.setImage2(image2);
            byte[] metadata = ss.unStegoGrayscale("hello", 80);
            
            ByteBuffer bf = ByteBuffer.allocate(11);
            bf.put(metadata);
            bf.flip();
            char a, b, c;
            int size;
            //bf.flip();
            a = bf.getChar();
            b = bf.getChar();
            c = bf.getChar();
            
            size = bf.getInt();
            byte[] notStegoed = ss.unStegoGrayscale("hello", (size + 10) * 8);
            bf.clear();
            ByteBuffer bf2 = ByteBuffer.allocate(notStegoed.length);
            bf2.put(notStegoed);
            bf2.flip();
            FileOutputStream fos = new FileOutputStream("D://halo2.txt");
            
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
        }
        
        
        
        //ss.stego("hello", hiddenText);
        
        
        //System.out.println(Integer.toBinaryString(notStegoed[0]));
        
        
        
        //ImageIO.write(image, "bmp", new File("D://lena2.bmp"));
        //stego(image, "D://lena512color.bmp", "D://lena512color2.bmp", "Hello");
    }
    
    private BufferedImage image;
    private BufferedImage image2;
    private BufferedImage stegoImage;
    private int currentBlockX;
    private int currentBlockY;

    /**
     * @return the image2
     */
    public BufferedImage getImage2() {
        return image2;
    }

    /**
     * @param image2 the image2 to set
     */
    public void setImage2(BufferedImage image2) {
        this.image2 = image2;
    }
    
    public enum pixelCategory {lowPixel, midPixel, hiPixel, higherPixel}
    
    public int getNbTileX()
    {
        return ((image.getWidth() - 1) / 3) - 2;
    }
    
    public int getNbTileY()
    {
        return ((image.getHeight() - 1)/ 3) - 2;
    }
    
    private int getBit(int input, int position)
    {
        return (input >> position) & 1;
    }
    
    private int setBit(int input, int pos, int bit)
    {
        int output = input;
        if (bit == 1)
        {
            output = output | (1 << pos);
        } else if (bit == 0) {
            output = output & ~(1 << pos);
        }
        
        return output;
    }
    private int getAveragePixel(int x, int y)
    {
        int pixel = image.getRGB(x, y);
        int r = (pixel >> 16) & 0x000000FF;
        int g = (pixel >> 8) & 0x000000FF;
        int b = pixel & 0x000000FF;
        
        return (r + g + b) / 3;
    }
    
    public pixelCategory categorizeBlock(int x, int y)
    {
        if (x >= getNbTileX())
        {
            throw new RuntimeException("Invalid X");
        }
        
        if (y >= getNbTileY())
        {
            throw new RuntimeException("Invalid Y");
        }
        
        int min = getAveragePixel(x * 3, y * 3);
        int i, j;
        
        //Cari minimum
        for(i=y*3; i<(y*3)+3; i++)
        {
            for(j=x*3; j<(x*3)+3; j++)
            {
                if(min > getAveragePixel(j, i))
                {
                    min = getAveragePixel(j, i);
                }
            }
        }
        
        //Hitung d
        int d = 0;
        for(i=y*3; i<y*3+3; i++)
        {
            for(j=x*3; j<x*3+3; j++)
            {
                d = d + getAveragePixel(j, i) - min;
            }
        }
        
        d = d / 8;
        
        pixelCategory out;
        if (d <= 7)
        {
            out = pixelCategory.lowPixel;
        } else if ((8 <= d) && (d <= 15))
        {
            out = pixelCategory.midPixel;
        } else if ((16 <= d) && (d <= 31))
        {
            out = pixelCategory.hiPixel;
        } else {
            out = pixelCategory.higherPixel;
        }
        
        return out;
    }
    
    //Mengembalikan kapasistas block dalam pixel
    public int getBlockCapacity(int x, int y)
    {
        if (x >= getNbTileX())
        {
            throw new RuntimeException("Invalid X");
        }
        
        if (y >= getNbTileY())
        {
            throw new RuntimeException("Invalid Y");
        }
        
        int capacity;
        switch(categorizeBlock(x, y))
        {
            case lowPixel:
                capacity = 16 * 3;
                break;
            case midPixel:
                capacity = 24 * 3;
                break;
            case hiPixel:
                capacity = 32 * 3;
                break;
            case higherPixel:
                capacity = 40 * 3;
                break;
            default:
                capacity = 0;
                break;
        }
        
        return capacity;
    }
    
    public int getBlockCapacityGrayscale(int x, int y)
    {
        if (x >= getNbTileX())
        {
            throw new RuntimeException("Invalid X");
        }
        
        if (y >= getNbTileY())
        {
            throw new RuntimeException("Invalid Y");
        }
        
        int capacity;
        switch(categorizeBlock(x, y))
        {
            case lowPixel:
                capacity = 16;
                break;
            case midPixel:
                capacity = 24;
                break;
            case hiPixel:
                capacity = 32;
                break;
            case higherPixel:
                capacity = 40;
                break;
            default:
                capacity = 0;
                break;
        }
        
        return capacity;
    }
    
    //Kapasitas file, dalam bit
    public int getCapacity()
    {
        int capacity = 0;
        int i, j;
        
        for (i=0; i<getNbTileX(); i++)
        {
            for (j=0; j<getNbTileY(); j++)
            {
                int nb = getNbTileY();
                capacity = capacity + getBlockCapacity(i, j);
            }
        }
        return capacity;
    }
    
    public int getCapacityGrayscale()
    {
        int capacity = 0;
        int i, j;
        
        for (i=0; i<getNbTileX(); i++)
        {
            for (j=0; j<getNbTileY(); j++)
            {
                int nb = getNbTileY();
                capacity = capacity + getBlockCapacityGrayscale(i, j);
            }
        }
        return capacity;
    }
    
    int getPixelCapacity(int x, int y)
    {
        
        int currRgb = image.getRGB((x * 3) + 2, (y * 3) + 2);
        int bit1, bit2;
        
        bit2 = getBit(currRgb, 1);
        bit1 = getBit(currRgb, 0);
        
        int out;
        if (bit2 == 0)
        {
            if (bit1 == 0)
            {
                out = 2;
            } else {
                out = 3;
            }
        }  else {
            if (bit1 == 0)
            {
                out = 4;
            } else {
                out = 5;
            }
        }
        
        return out;
    }
    // x : posisi horizontal pixel
    // y : posisi vertikal pixel
    // i : piksel ke i
    // pos : posisi bit
    private void sisipBit(int bit, int x, int y, int i, int pos)
    {
        int posX = (x * 3) + (i % 3);
        int posY = (y * 3) + (i / 3);
        
        if ((bit == 1) && (posX == 2) && (posY == 2) && (pos == 0))
        {
            System.out.println("Warning");
        }
        int rgb = image.getRGB(posX, posY);
        
        rgb = setBit(rgb, pos, bit);
        image.setRGB(posX, posY, rgb );
    }
    
    private void sisipBitGrayscale(int bit, int x, int y, int i, int pos)
    {
        int posX = (x * 3) + (i % 3);
        int posY = (y * 3) + (i / 3);
        
        int rgb = image.getRGB(posX, posY);
        
        rgb = setBit(rgb, pos, bit);
        rgb = setBit(rgb, pos + 8, bit);
        rgb = setBit(rgb, pos + 16, bit);
        image.setRGB(posX, posY, rgb );
    }
    
    private int ambilBit(int x, int y, int i, int pos)
    {
        int posX = (x * 3) + (i % 3);
        int posY = (y * 3) + (i / 3);
        
        int rgb = image.getRGB(posX, posY);
        
        int out = (rgb >> pos) & 1;
        
        return out;
    }
    
    private void adjust(int x, int y, int i, int n, int color)
    {
        int posX = (x * 3) + (i % 3);
        int posY = (y * 3) + (i / 3);
        
        int rgb = image.getRGB(posX, posY);
        int rgb2 = image2.getRGB(posX, posY);
        
        int element = (rgb >> (8 * color)) & 0x000000FF;
        int element2 = (rgb2 >> (8 * color)) & 0x000000FF;
        
        if (element >= element2 + (1 << (n - 1)) + 1)
        {
            element = element - (1 << (n));
            
        } else if (element <= element2 - (1 << (n - 1)) + 1)
        {
            element = element + (1 << (n));
        }
        
        if (element < 0)
        {
            element = element + (1 << n);
        } else if (element > 255)
        {
            element = element - (1 << n);
        }
        
        int warna;
        
        int red = (rgb >> 16) & 0x000000FF;
        int green = (rgb >>8 ) & 0x000000FF;
        int blue = (rgb) & 0x000000FF;
        if (color == 0)
        {
            warna = (red << 16) | (green << 8) | element;
        } else if (color == 1)
        {
            warna = (red << 16) | (element << 8) | blue;
        } else
        {
            warna = (element << 16) | (green << 8) | blue;
        }
                   
        image.setRGB(posX, posY, warna);
        
    }
    
    public void stego(String key, byte[] data) throws Exception
    {
        int x = 0;
        int y = 0;
                
        int processed = 0;
        int blockUsedSpace = 0;
        int pixelUsedSpace = 0;
        int pixelCapacity = 0;
        int blockPos = 0;
        int currColor = 0;
        boolean gridSwitched = true;
        
        do
        {
            if (gridSwitched)
            {
                switch(categorizeBlock(x, y))
                {
                    case lowPixel:
                        pixelCapacity = 2;
                        sisipBit(0, x, y, 8, 0);
                        sisipBit(0, x, y, 8, 1);
                        break;
                    case midPixel:
                        pixelCapacity = 3;
                        sisipBit(1, x, y, 8, 0);
                        sisipBit(0, x, y, 8, 1);
                        break;
                    case hiPixel:
                        pixelCapacity = 4;
                        sisipBit(0, x, y, 8, 0);
                        sisipBit(1, x, y, 8, 1);
                        break;
                    case higherPixel:
                        pixelCapacity = 5;
                        sisipBit(1, x, y, 8, 0);
                        sisipBit(1, x, y, 8, 1);
                        break;
                    default:
                        break;
                }
                
                gridSwitched = false;
            }
            
                                  
            int currData = data[processed / 8];
            int currBit = getBit(currData, processed % 8);
            
            int temp = image.getRGB(x*3, y*3);
            sisipBit(currBit, x, y, blockPos, (8 * currColor) + pixelUsedSpace);
            
            temp = image.getRGB(x*3, y*3);
            processed++;
            pixelUsedSpace++;
                                
            if (pixelUsedSpace == pixelCapacity)
            {
                adjust(x, y, blockPos, pixelCapacity, currColor);
                currColor++;
                pixelUsedSpace = 0 ;
                if(currColor == 3)
                {
                    blockPos++;
                    currColor = 0;
                    if (blockPos == 8)
                    {
                        y++;
                        blockPos = 0;
                        gridSwitched = true;
                        if(y == getNbTileY())
                        {
                            x++;
                            y = 0;
                        }
                    }
                }
            }
        } while (processed < data.length * 8);
       
    }
    
    public void stegoGrayscale(String key, byte[] data) throws Exception
    {
        int x = 0;
        int y = 0;
                
        int processed = 0;
        int blockUsedSpace = 0;
        int pixelUsedSpace = 0;
        int pixelCapacity = 0;
        int blockPos = 0;
        int currColor = 0;
        boolean gridSwitched = true;
        
        do
        {
            if (gridSwitched)
            {
                switch(categorizeBlock(x, y))
                {
                    case lowPixel:
                        pixelCapacity = 2;
                        //Blue
                        sisipBitGrayscale(0, x, y, 8, 0);
                        sisipBitGrayscale(0, x, y, 8, 1);
                                                                       
                        break;
                    case midPixel:
                        pixelCapacity = 3;
                        sisipBitGrayscale(1, x, y, 8, 0);
                        sisipBitGrayscale(0, x, y, 8, 1);
                        break;
                    case hiPixel:
                        pixelCapacity = 4;
                        sisipBitGrayscale(0, x, y, 8, 0);
                        sisipBitGrayscale(1, x, y, 8, 1);
                        break;
                    case higherPixel:
                        pixelCapacity = 5;
                        sisipBitGrayscale(1, x, y, 8, 0);
                        sisipBitGrayscale(1, x, y, 8, 1);
                        break;
                    default:
                        break;
                }
                
                gridSwitched = false;
            }
            
                                  
            int currData = data[processed / 8];
            int currBit = getBit(currData, processed % 8);
            
            int temp = image.getRGB(x*3, y*3);
            sisipBitGrayscale(currBit, x, y, blockPos, pixelUsedSpace);
            
            temp = image.getRGB(x*3, y*3);
            processed++;
            pixelUsedSpace++;
            
            if (pixelUsedSpace == pixelCapacity)
            {
                //adjust(x, y, blockPos, pixelCapacity, currColor);
                pixelUsedSpace = 0;
                blockPos++;
                if (blockPos == 8)
                {
                    y++;
                    blockPos = 0;
                    gridSwitched = true;
                    if(y == getNbTileY())
                    {
                        x++;
                        y = 0;
                    }
               }
            }
            
        } while (processed < data.length * 8);
       
    }
    
    public byte[] unStego(String key, int size)
    {
        byte out[] = new byte[(size / 8) + 1];
        int x = 0;
        int y = 0;
                
        int processed = 0;
        int blockUsedSpace = 0;
        int pixelUsedSpace = 0;
        int pixelCapacity = 0;
        int blockPos = 0;
        int currColor = 0;
        int currData = 0;
        int curByte = 0;
        
        do
        {
            int bitPerBlock = 0;
            pixelCapacity = getPixelCapacity(x, y);
                        
            int currBit = ambilBit(x, y, blockPos, (8 * currColor) + pixelUsedSpace);
            currData = (currBit << (processed % 8)) + currData;
                        
            processed++;
            if (processed % 8 == 0)
            {
                out[curByte] = (byte) currData;
                currData = 0;
                curByte++;
            }
            pixelUsedSpace++;
            
            if (pixelUsedSpace == pixelCapacity)
            {
                //adjust(x, y, blockPos, pixelCapacity, currColor);
                currColor++;
                pixelUsedSpace = 0;
                
                //transformasi
                
                if(currColor == 3)
                {
                    //lakukan transformasi
                    blockPos++;
                    currColor = 0;
                    if (blockPos == 8)
                    {
                        y++;
                        blockPos = 0;
                        if(y == getNbTileY())
                        {
                            x++;
                            y = 0;
                        }
                    }
                }
            }
        } while (processed < size);
        
        return out;
    }
    
    public byte[] unStegoGrayscale(String key, int size)
    {
        byte out[] = new byte[(size / 8) + 1];
        int x = 0;
        int y = 0;
                
        int processed = 0;
        int blockUsedSpace = 0;
        int pixelUsedSpace = 0;
        int pixelCapacity = 0;
        int blockPos = 0;
        int currColor = 0;
        int currData = 0;
        int curByte = 0;
        
        do
        {
            int bitPerBlock = 0;
            pixelCapacity = getPixelCapacity(x, y);
                        
            int currBit = ambilBit(x, y, blockPos, pixelUsedSpace);
            currData = (currBit << (processed % 8)) + currData;
                        
            processed++;
            if (processed % 8 == 0)
            {
                out[curByte] = (byte) currData;
                currData = 0;
                curByte++;
            }
            pixelUsedSpace++;
            
            if (pixelUsedSpace == pixelCapacity)
            {
                //adjust(x, y, blockPos, pixelCapacity, currColor);
                pixelUsedSpace = 0;
                
                //transformasi
                
                blockPos++;
                currColor = 0;
                if (blockPos == 8)
                {
                    y++;
                    blockPos = 0;
                    if(y == getNbTileY())
                    {
                        x++;
                        y = 0;
                    }
                }
            }

        } while (processed < size);
        
        return out;
    }

    /**
     * @return the image
     */
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

