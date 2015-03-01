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
            
            Path path = Paths.get("D:\\AFIK\\Project\\Steganografi\\lorem_2.txt");
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
            System.out.println(msg);
            
            File input = new File("D:\\AFIK\\Project\\Steganografi\\lena.bmp");
            BufferedImage cover = ImageIO.read(input);
            fd.setCoverImage(cover);
            fd.setStegoImage(cover);
            int size = fd.getCapacity();
            System.out.println("Ukuran gambar : " + cover.getWidth() + " " + cover.getHeight());
            System.out.println("Size : " + size + " bit; " + size/8 + " byte");

            if (hiddenText.length > size/8){
                System.out.println("Ukuran text terlalu besar");
            }
            else {
              fd.hideMessage();
              ImageIO.write(fd.getStegoImage(), "bmp", new File("D:\\AFIK\\Project\\Steganografi\\lena_out.bmp"));
            }
        }
        else if (pilihan == 2) {
            FourDiffStego fd = new FourDiffStego();
            File input = new File("D:\\AFIK\\Project\\Steganografi\\lena_out.bmp");
            String msg="100110011011111110010110";
            BufferedImage cover = ImageIO.read(input);
            fd.setCoverImage(cover);
            fd.setStegoImage(cover);
            //msg = fd.extractMessage();
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
                    System.out.println(offset + " " + i + " " + j);
                    newR = error; newB = error; newG = error;
                    if (!bf.isErrorBlock(returnRBlock(0,j,i))){
                        bf.setY(returnRBlock(1,j,i));
//                        System.out.println("oldR = " + bf.getY()[0] + " " + bf.getY()[1] + " " + bf.getY()[2]+ " " + bf.getY()[3]);
                        size = bf.getSizeAvailable();
                        if (offset+size< message.length()){
                            msg = message.substring(offset, offset+size);
                            offset += size;
                            msglen -= size;
//                            System.out.println("msgR: "+ size + " " +msg + " " + msglen);
                            bf.setMessage(msg);
                            bf.hideMessage();
                            newR = bf.getYt().clone();
                            bf.setY(error);
//                            System.out.println("newR : " + newR[0] + " " + newR[1]+ " " + newR[2]+ " " + newR[3]);
                        }
                        else {
                            System.out.println("Pesan habis");
                            stop = true;
                        }
                    }
                   
                    
                    if (!bf.isErrorBlock(returnGBlock(j,i))){
                        bf.setY(returnGBlock(j,i));
//                        System.out.println("oldG = " + bf.getY()[0] + " " + bf.getY()[1] + " " + bf.getY()[2]+ " " + bf.getY()[3]);
                        size = bf.getSizeAvailable();
                        if (offset+size< message.length()){
                            msg = message.substring(offset, offset+size);
                            offset += size;
                            msglen -= size;
//                            System.out.println("msgG " + size + " " +msg + " " + msglen);
                            bf.setMessage(msg);
                            bf.hideMessage();
                            newG = bf.getYt().clone();
                            bf.setY(error);
//                            System.out.println("newG : " + newG[0] + " " + newG[1]+ " " + newG[2]+ " " + newG[3]);
                        }
                        else {
                            System.out.println("Pesan habis");
                            stop = true;
                        }
                    }
                    
                    if (!bf.isErrorBlock(returnBBlock(j,i))){
                        bf.setY(returnBBlock(j,i));
//                        System.out.println("oldB = " + bf.getY()[0] + " " + bf.getY()[1] + " " + bf.getY()[2]+ " " + bf.getY()[3]);
                        size = bf.getSizeAvailable();
                        if (offset+size< message.length()){
                            msg = message.substring(offset, offset+size);
                            offset += size;
                            msglen -= size;
//                            System.out.println("msgB : " +size + " " +msg + " " + msglen);
                            bf.setMessage(msg);
                            bf.hideMessage();
                            newB = bf.getYt().clone();
//                            System.out.println("newB : " + newB[0] + " " + newB[1]+ " " + newB[2]+ " " + newB[3]);
                        }
                        else {
                            stop = true;
                            System.out.println("Pesan habis");
                        }
                    }
        
                    setBlock(j,i,newR,newG,newB);
                    /*
                    bf.setY(returnRBlockS(j,i));
                    exmsg+=bf.returnMessage();
                    
                    bf.setY(returnGBlockS(j,i));
                    exmsg+=bf.returnMessage();
                    
                    bf.setY(returnBBlockS(j,i));
                    exmsg+=bf.returnMessage();
                    
                    System.out.println(exmsg);*/
                }
//                if (j == 2) 
                //break;
            }
//            if (i == 2) 
            //break;
        }
        
    }
    
    public String extractMessage(){
        String msg="";
        int lenprev=0, addlen=0;
        boolean EOF = false;
        for (int i=0; i<getStegoImage().getHeight(); i+=2) {
            for (int j=0; j<getStegoImage().getWidth(); j+=2) {
                if((getStegoImage().getHeight() > i+1) && (getStegoImage().getWidth() > j+1) && !EOF){
                    System.out.println(i + " " + j);
                    if (!bf.isErrorBlock(returnRBlockS(j,i))){
                        bf.setY(returnRBlockS(j,i));
//                        System.out.println("Pesan R: " + bf.returnMessage() + " " + Integer.parseInt(bf.returnMessage()));
                        msg+=bf.returnMessage();
                        addlen +=bf.returnMessage().length();
                    }
                   
                    
                    if (!bf.isErrorBlock(returnGBlockS(j,i))){
                        bf.setY(returnGBlockS(j,i));
//                        System.out.println("Pesan G : " +bf.returnMessage() +  " " + Integer.parseInt(bf.returnMessage()));
                        msg+=bf.returnMessage();
                        addlen +=bf.returnMessage().length();
                    }
                    
                    if (!bf.isErrorBlock(returnBBlockS(j,i))){
                        bf.setY(returnBBlockS(j,i));
//                        System.out.println("Pesan B : " +bf.returnMessage() + " " + Integer.parseInt(bf.returnMessage()));
                        msg+=bf.returnMessage();
                        addlen +=bf.returnMessage().length();
                    }
                    
                    
                    lenprev += addlen;
                    addlen = 0;
                }
                //break;
            }
            //break;
        }
        return msg;
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
                //System.out.println(bitProcess);
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
        Color color1 = new Color(R[0],G[0],B[0]);
        RGB = color1.getRGB();
//        System.out.println(" set : " + Integer.toBinaryString(RGB));
        getStegoImage().setRGB(x, y, RGB);
        Color color2 = new Color(R[1],G[1],B[1]);
        RGB = color2.getRGB();
//        System.out.println(" set : " + Integer.toBinaryString(RGB));
        getStegoImage().setRGB(x+1, y, RGB);
        Color color3 = new Color(R[2],G[2],B[2]);
        RGB = color3.getRGB();
//        System.out.println(" set : " + Integer.toBinaryString(RGB));
        getStegoImage().setRGB(x, y+1, RGB);
        Color color4 = new Color(R[3],G[3],B[3]);
        RGB = color4.getRGB();
//        System.out.println(" set : " + Integer.toBinaryString(RGB));
        getStegoImage().setRGB(x+1, y+1, RGB);
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
        
        pixel1 = getStegoImage().getRGB(offsetx, offsety);
        pixel2 = getStegoImage().getRGB(offsetx+1, offsety);
        pixel3 = getStegoImage().getRGB(offsetx, offsety+1);
        pixel4 = getStegoImage().getRGB(offsetx+1, offsety+1);
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
        
         pixel1 = getStegoImage().getRGB(offsetx, offsety);
        //System.out.println("pixel1 : " + pixel1 + " " + Integer.toBinaryString(pixel1));
        pixel2 = getStegoImage().getRGB(offsetx+1, offsety);
        pixel3 = getStegoImage().getRGB(offsetx, offsety+1);
        pixel4 = getStegoImage().getRGB(offsetx+1, offsety+1);
        
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
        
        pixel1 = getStegoImage().getRGB(offsetx, offsety);
        //System.out.println("pixel1 : " + pixel1 + " " + Integer.toBinaryString(pixel1));
        pixel2 = getStegoImage().getRGB(offsetx+1, offsety);
        pixel3 = getStegoImage().getRGB(offsetx, offsety+1);
        pixel4 = getStegoImage().getRGB(offsetx+1, offsety+1);
        
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
