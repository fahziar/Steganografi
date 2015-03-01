/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package steganografi;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import static java.lang.Math.sqrt;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Scanner;
import javax.imageio.ImageIO;

/**
 *
 * @author Afik
 */
public class FourDiffStego {
    private BufferedImage coverImage;
    private BufferedImage stegoImage;
    private String message;
    private int msglen;
    private BlockFour bf = new BlockFour();
    
    
    public static void main (String args[]) throws IOException{
        Scanner n = new Scanner(System.in);
        
        int pilihan = n.nextInt();
        
        if (pilihan == 1) //hide pesan
        {
            FourDiffStego fd = new FourDiffStego();
            
            Path path = Paths.get("D:\\AFIK\\Project\\Steganografi\\lorem_3.txt");
            byte[] hiddenText = Files.readAllBytes(path);
            String msg="";
            for (int i =0; i<hiddenText.length; i++){
                msg += Integer.toBinaryString(hiddenText[i]); //ubah pesan jadi String 10001011110
            }
            //EOF
            msg += Integer.toBinaryString(65);
            msg += Integer.toBinaryString(70);
            msg += Integer.toBinaryString(73);
            msg += Integer.toBinaryString(75);
            String msgsize  = Integer.toBinaryString(hiddenText.length);
            fd.setMessage(msg);
            System.out.println("msg len : " + msg.length() + " bit");
            //System.out.println(msg);
            
            File input = new File("D:\\AFIK\\Project\\Steganografi\\baboon.bmp");
            BufferedImage cover = ImageIO.read(input);
            fd.setCoverImage(cover);
            fd.setStegoImage(cover);
            System.out.println(fd.coverImage.getWidth());
            int size = fd.getCapacity();
            System.out.println("Ukuran gambar : " + cover.getWidth() + " " + cover.getHeight());
            System.out.println("Size : " + size + " bit; " + size/8 + " byte");

            if (hiddenText.length > size/8){
                System.out.println("Ukuran text terlalu besar");
            }
            else {
              fd.hideMessage();
              ImageIO.write(fd.stegoImage, "bmp", new File("D:\\AFIK\\Project\\Steganografi\\baboon_out.bmp"));
              byte[] t = Files.readAllBytes(Paths.get("D:\\AFIK\\Project\\Steganografi\\baboon.bmp"));
              byte[] h = Files.readAllBytes(Paths.get("D:\\AFIK\\Project\\Steganografi\\baboon_out.bmp"));
              double psnr = fd.getPSNR(t, h);
              System.out.println("PSNR = " + psnr);
            }
        }
        else if (pilihan == 2) {
            FourDiffStego fd = new FourDiffStego();
            File input = new File("D:\\AFIK\\Project\\Steganografi\\lena_out.bmp");
            String msg="";
            BufferedImage cover = ImageIO.read(input);
            fd.setCoverImage(cover);
            fd.setStegoImage(cover);
            msg = fd.extractMessage();
            System.out.println(msg);
            
            String msgfinal;   
            msgfinal = fd.bitToText(msg);
            System.out.println(msgfinal);
            
        }
        
    }
    
    public void hideMessage(){
        String msg ;
        String exmsg="";
        int size,offset=0,pixel1,pixel2,pixel3,pixel4;
        int[] newR, newG, newB;
        int[] error= new int[4];
        error[0] = 0; error[1] = 0; error[2] = 0; error[3]=0;
        boolean stop = false;
        for (int i=0; i<coverImage.getHeight(); i+=2) {
            for (int j=0; j<coverImage.getWidth(); j+=2) {
                if((coverImage.getHeight() > i+1) && (coverImage.getWidth() > j+1) && !stop){
                    //System.out.println(offset + " " + i + " " + j);
                    newR = error; newB = error; newG = error;
                    if (!bf.isErrorBlock(returnRBlock(0,j,i))){
                        bf.setY(returnRBlock(1,j,i));
                        size = bf.getSizeAvailable();
                        if (offset+size< message.length()){
                            msg = message.substring(offset, offset+size);
                            offset += size;
                            msglen -= size;
                            bf.setMessage(msg);
                            bf.hideMessage();
                            newR = bf.getYt().clone();
                            bf.setY(error);
                        }
                        else {
                            System.out.println("Pesan habis");
                            stop = true;
                        }
                    }
                   
                    
                    if (!bf.isErrorBlock(returnGBlock(j,i))){
                        bf.setY(returnGBlock(j,i));
                        size = bf.getSizeAvailable();
                        if (offset+size< message.length()){
                            msg = message.substring(offset, offset+size);
                            offset += size;
                            msglen -= size;
                            bf.setMessage(msg);
                            bf.hideMessage();
                            newG = bf.getYt().clone();
                            bf.setY(error);
                        }
                        else {
                            System.out.println("Pesan habis");
                            stop = true;
                        }
                    }
                    
                    if (!bf.isErrorBlock(returnBBlock(j,i))){
                        bf.setY(returnBBlock(j,i));
                        size = bf.getSizeAvailable();
                        if (offset+size< message.length()){
                            msg = message.substring(offset, offset+size);
                            offset += size;
                            msglen -= size;
                            bf.setMessage(msg);
                            bf.hideMessage();
                            newB = bf.getYt().clone();
                        }
                        else {
                            stop = true;
                            System.out.println("Pesan habis");
                        }
                    }
        
                    setBlock(j,i,newR,newG,newB);
                   
                }
            }
        }
        
    }
    
    public String extractMessage(){
        String msg="";
        int lenprev=0, addlen=0;
        boolean EOF = false;
        for (int i=0; i<stegoImage.getHeight(); i+=2) {
            for (int j=0; j<stegoImage.getWidth(); j+=2) {
                if((stegoImage.getHeight() > i+1) && (stegoImage.getWidth() > j+1) && !EOF){
                    System.out.println(i + " " + j);
                    if (!bf.isErrorBlock(returnRBlockS(j,i))){
                        bf.setY(returnRBlockS(j,i));
                        msg+=bf.returnMessage();
                        addlen +=bf.returnMessage().length();
                    }
                   
                    
                    if (!bf.isErrorBlock(returnGBlockS(j,i))){
                        bf.setY(returnGBlockS(j,i));
                        msg+=bf.returnMessage();
                        addlen +=bf.returnMessage().length();
                    }
                    
                    if (!bf.isErrorBlock(returnBBlockS(j,i))){
                        bf.setY(returnBBlockS(j,i));
                        msg+=bf.returnMessage();
                        addlen +=bf.returnMessage().length();
                    }
                    
                    
                    lenprev += addlen;
                    addlen = 0;
                }
            }
        }
        return msg;
    }
    
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
    
    public String bitToText(String bit){
        String result = "";
        int ascii;
        String bitProcess;
        while(bit.length() % 8 != 0){
                bit = '0' + bit;
        }
        while(bit.length()>0){
                bitProcess = bit.substring(0,8);
                ascii = Integer.parseInt(bitProcess,2);
                result += (char)ascii;
                bit = bit.substring(8);
        }
        return result;
    }
    
    //return capacity in bit
    public int getCapacity(){
        int size=0;
        for (int i=0; i<coverImage.getHeight(); i+=2) {
            for (int j=0; j<coverImage.getWidth(); j+=2) {
                if((coverImage.getHeight() > i+1) && (coverImage.getWidth() > j+1)){
                    if (!bf.isErrorBlock(returnRBlock(0,j,i))){
                        bf.setY(returnRBlock(0,j,i));
                        bf.setKey();
                        size += bf.getSizeAvailable();
                    }
                    
                    if (!bf.isErrorBlock(returnGBlock(j,i))){
                        bf.setY(returnGBlock(j,i));
                        bf.setKey();
                        size += bf.getSizeAvailable();
                    }
                    
                    if (!bf.isErrorBlock(returnBBlock(j,i))){
                        bf.setY(returnBBlock(j,i));
                        bf.setKey();
                        size += bf.getSizeAvailable();
                    }
                }
            }
        }
        return size;
    }
    
    public boolean checkEOF(String msg, int lenprev){
        System.out.println(lenprev + " " + msg.length());
        boolean eof=false;
        char last,a,f,i;
        if (msg.length()-lenprev > 8) {
            last =  (char)Integer.parseInt(msg.substring(msg.length()-8, msg.length()-1));
            System.out.println("char : " + last);
            if (msg.length()>=32 && last=='K') {
                i = (char)Integer.parseInt(msg.substring(msg.length()-16, msg.length()-9));
                f = (char)Integer.parseInt(msg.substring(msg.length()-24, msg.length()-17));
                a = (char)Integer.parseInt(msg.substring(msg.length()-32, msg.length()-25));
                if (a=='a'&&f=='f'&&i=='i')
                    eof = true;
            }
        }
        return eof;
        // 0-7 8-15 16-23 24-31
    }
    
    public void setBlock(int x, int y, int[] R, int[] G, int[] B){
        int RGB;
        Color color1 = new Color(((int)R[0])%256,((int)G[0])%256,((int)B[0])%256);
        RGB = color1.getRGB();
        stegoImage.setRGB(x, y, RGB);
        Color color2 = new Color(((int)R[1])%256,((int)G[1])%256,((int)B[1])%256);
        RGB = color2.getRGB();
        stegoImage.setRGB(x+1, y, RGB);
        Color color3 = new Color(((int)R[2])%256,((int)G[2])%256,((int)B[2])%256);
        RGB = color3.getRGB();
        stegoImage.setRGB(x, y+1, RGB);
        Color color4 = new Color(((int)R[3])%256,((int)G[3])%256,((int)B[3])%256);
        RGB = color4.getRGB();
        stegoImage.setRGB(x+1, y+1, RGB);
    }
    
        //untuk hide
    public int[] returnRBlock(int tes, int offsetx,int offsety){
        int[] RBlock = new int[4];
        int pixel1, pixel2, pixel3, pixel4;
        
        pixel1 = coverImage.getRGB(offsetx, offsety);
        pixel2 = coverImage.getRGB(offsetx+1, offsety);
        pixel3 = coverImage.getRGB(offsetx, offsety+1);
        pixel4 = coverImage.getRGB(offsetx+1, offsety+1);
        
        /*
        if (tes == 1) {
            //print pixel awal
            System.out.println("get : " + pixel1 + " " + Integer.toBinaryString(pixel1));
            System.out.println("get : " + pixel2 + " " + Integer.toBinaryString(pixel2));
            System.out.println("get : " + pixel3 + " " + Integer.toBinaryString(pixel3));
            System.out.println("get : " + pixel4 + " " + Integer.toBinaryString(pixel4));
        }*/
        
        RBlock[0]=(pixel1 >> 16) & 0x000000FF;
        //System.out.println(" Red1 After shift : " + RBlock[0] + " " + Integer.toBinaryString(RBlock[0]));
        RBlock[1]=(pixel2 >> 16) & 0x000000FF;
        //System.out.println(" Red2 After shift : " + RBlock[1] + " " + Integer.toBinaryString(RBlock[0]));
        RBlock[2]=(pixel3 >> 16) & 0x000000FF;
        //System.out.println(" Red3 After shift : " + RBlock[2] + " " + Integer.toBinaryString(RBlock[0]));
        RBlock[3]=(pixel4 >> 16) & 0x000000FF;
        //System.out.println(" Red4 After shift : " + RBlock[3] + " " + Integer.toBinaryString(RBlock[0]));
        
        //System.out.println("RBlock " + RBlock[0] + " " + RBlock[1] + " " + RBlock[2] + " " + RBlock[3]);
        return RBlock;
    }
    
    public int[] returnGBlock(int offsetx, int offsety){
        int[] GBlock = new int[4];
        int pixel1, pixel2, pixel3, pixel4;
        
         pixel1 = coverImage.getRGB(offsetx, offsety);
        //System.out.println("pixel1 : " + pixel1 + " " + Integer.toBinaryString(pixel1));
        pixel2 = coverImage.getRGB(offsetx+1, offsety);
        pixel3 = coverImage.getRGB(offsetx, offsety+1);
        pixel4 = coverImage.getRGB(offsetx+1, offsety+1);
        
        GBlock[0]=(pixel1 >> 8) & 0x000000FF;
        //System.out.println(" Green After shift : " + GBlock[0] + " " + Integer.toBinaryString(GBlock[0]));
        GBlock[1]=(pixel2 >> 8) & 0x000000FF;
        GBlock[2]=(pixel3 >> 8) & 0x000000FF;
        GBlock[3]=(pixel4 >> 8) & 0x000000FF;

        //System.out.println("GBlock " + GBlock[0] + " " + GBlock[1] + " " + GBlock[2] + " " + GBlock[3]);
        
        return GBlock;
    }
    
    public int[] returnBBlock(int offsetx, int offsety){
        int[] BBlock = new int[4];
        int pixel1, pixel2, pixel3, pixel4;
        
        pixel1 = coverImage.getRGB(offsetx, offsety);
        //System.out.println("pixel1 : " + pixel1 + " " + Integer.toBinaryString(pixel1));
        pixel2 = coverImage.getRGB(offsetx+1, offsety);
        pixel3 = coverImage.getRGB(offsetx, offsety+1);
        pixel4 = coverImage.getRGB(offsetx+1, offsety+1);
        
        BBlock[0]=pixel1 & 0x000000FF;
        //System.out.println(" Blue After shift : " + BBlock[0] + " " + Integer.toBinaryString(BBlock[0]));
        BBlock[1]=pixel2 & 0x000000FF;
        BBlock[2]=pixel3 & 0x000000FF;
        BBlock[3]=pixel4 & 0x000000FF;

        return BBlock;
    }
    
    //untuk extract
    public int[] returnRBlockS(int offsetx,int offsety){
        int[] RBlock = new int[4];
        int pixel1, pixel2, pixel3, pixel4;
        
        pixel1 = stegoImage.getRGB(offsetx, offsety);
        pixel2 = stegoImage.getRGB(offsetx+1, offsety);
        pixel3 = stegoImage.getRGB(offsetx, offsety+1);
        pixel4 = stegoImage.getRGB(offsetx+1, offsety+1);
        //System.out.println("pixel4 : " + pixel4 + " " + Integer.toBinaryString(pixel1));
        
        RBlock[0]=(pixel1 >> 16) & 0x000000FF;
        //System.out.println(" Red1 After shift : " + RBlock[0] + " " + Integer.toBinaryString(RBlock[0]));
        RBlock[1]=(pixel2 >> 16) & 0x000000FF;
        //System.out.println(" Red2 After shift : " + RBlock[1] + " " + Integer.toBinaryString(RBlock[0]));
        RBlock[2]=(pixel3 >> 16) & 0x000000FF;
        //System.out.println(" Red3 After shift : " + RBlock[2] + " " + Integer.toBinaryString(RBlock[0]));
        RBlock[3]=(pixel4 >> 16) & 0x000000FF;
        //System.out.println(" Red4 After shift : " + RBlock[3] + " " + Integer.toBinaryString(RBlock[0]));
        
        //System.out.println("RBlock " + RBlock[0] + " " + RBlock[1] + " " + RBlock[2] + " " + RBlock[3]);
        return RBlock;
    }
    
    public int[] returnGBlockS(int offsetx, int offsety){
        int[] GBlock = new int[4];
        int pixel1, pixel2, pixel3, pixel4;
        
         pixel1 = stegoImage.getRGB(offsetx, offsety);
        //System.out.println("pixel1 : " + pixel1 + " " + Integer.toBinaryString(pixel1));
        pixel2 = stegoImage.getRGB(offsetx+1, offsety);
        pixel3 = stegoImage.getRGB(offsetx, offsety+1);
        pixel4 = stegoImage.getRGB(offsetx+1, offsety+1);
        
        GBlock[0]=(pixel1 >> 8) & 0x000000FF;
        //System.out.println(" Green After shift : " + GBlock[0] + " " + Integer.toBinaryString(GBlock[0]));
        GBlock[1]=(pixel2 >> 8) & 0x000000FF;
        GBlock[2]=(pixel3 >> 8) & 0x000000FF;
        GBlock[3]=(pixel4 >> 8) & 0x000000FF;

        //System.out.println("GBlock " + GBlock[0] + " " + GBlock[1] + " " + GBlock[2] + " " + GBlock[3]);
        
        return GBlock;
    }
    
    public int[] returnBBlockS(int offsetx, int offsety){
        int[] BBlock = new int[4];
        int pixel1, pixel2, pixel3, pixel4;
        
        pixel1 = stegoImage.getRGB(offsetx, offsety);
        //System.out.println("pixel1 : " + pixel1 + " " + Integer.toBinaryString(pixel1));
        pixel2 = stegoImage.getRGB(offsetx+1, offsety);
        pixel3 = stegoImage.getRGB(offsetx, offsety+1);
        pixel4 = stegoImage.getRGB(offsetx+1, offsety+1);
        
        BBlock[0]=pixel1 & 0x000000FF;
        //System.out.println(" Blue After shift : " + BBlock[0] + " " + Integer.toBinaryString(BBlock[0]));
        BBlock[1]=pixel2 & 0x000000FF;
        BBlock[2]=pixel3 & 0x000000FF;
        BBlock[3]=pixel4 & 0x000000FF;

        return BBlock;
    }
    
    public BufferedImage getCoverImage() {
        return coverImage;
    }

    public void setCoverImage(BufferedImage coverImage) {
        this.coverImage = coverImage;
    }

    public BufferedImage getStegoImage() {
        return stegoImage;
    }

    public void setStegoImage(BufferedImage stegoImage) {
        this.stegoImage = stegoImage;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
        this.msglen = message.length();
    }
}
