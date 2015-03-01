/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package steganografi;

import static java.lang.Math.pow;

/**
 *
 * @author Afik
 */
public class BlockFour {
    
    /**
     * Attribute
     */
    
    private final int keyLow = 2;
    private final int keyHigh = 3;
    private final int threshold = 5;
    
    private String message;
    private int key;
    private int kelas;
    private int y[] = new int[4];
    private int y1[] = new int[4];
    private int y2[] = new int[4];
    private int yt[] = new int[4];
    
    
    /**
     * MAIN
     */
    public static void main(String args[]) {
        BlockFour fb = new BlockFour(); 
        int block[] = new int[4];
        block[0] = 107; block[1] = 106; block[2] = 109; block[3] = 109;
        fb.setY(block);
        fb.setMessage("10011001");
        
        System.out.println("Is error block ? " + fb.isErrorBlock(block));
        System.out.println("Min : " + fb.getMin(block) + " Max : " + fb.getMax(block));
        System.out.println("Average : " + fb.getAverageD(block));
        fb.setKey();
        System.out.println("Key : " + fb.getKey() + " Size : " + fb.getSizeAvailable());
        System.out.println("Message : " + fb.getMessage());
        fb.hideMessage();
        System.out.println("Hasil normal : " + fb.getY1()[0] + "," + fb.getY1()[1] + "," + fb.getY1()[2] + "," + fb.getY1()[3]);
        System.out.println("Hasil modified : " + fb.getY2()[0] + "," + fb.getY2()[1] + "," + fb.getY2()[2] + "," + fb.getY2()[3]);
        System.out.println("Hasil akhir : " + fb.getYt()[0] + "," + fb.getYt()[1] + "," + fb.getYt()[2] + "," + fb.getYt()[3]);
        
        fb.setY(fb.getYt());
        String pesan = fb.returnMessage();
        System.out.println("Hasil ekstraksi pesan : " + pesan);
        
    }
    
    public void hideMessage(){
        setKey();
        //System.out.println("Y awal : " + Integer.toBinaryString(getY()[0]) + "," + Integer.toBinaryString(getY()[1]) + "," + Integer.toBinaryString(getY()[2]) + "," + Integer.toBinaryString(getY()[3]));
        LSBNormal();
        //System.out.println("Hasil normal : " + Integer.toBinaryString(getY1()[0]) + "," + Integer.toBinaryString(getY1()[1]) + "," + Integer.toBinaryString(getY1()[2]) + "," + Integer.toBinaryString(getY1()[3]));
        LSBModified();
        //System.out.println("Hasil modified : " + getY2()[0] + "," + getY2()[1] + "," + getY2()[2] + "," + getY2()[3]);
        readjustment();
        //System.out.println("Hasil akhir : " + getYt()[0] + "," + getYt()[1] + "," + getYt()[2] + "," + getYt()[3]);
    }
    
    public String returnMessage(){
       String message="";
       String cur,temp; 
       setKey();
       for (int i=0; i<4; i++) {
            cur = Integer.toBinaryString(y[i]);
            if (cur.length()<8) {
                temp ="";
                for(int idx=0; idx < (8-cur.length());idx++) {
                    temp+="0";
                }
                cur = temp + cur;
            }
            message+=cur.substring(cur.length()-key,cur.length());
        }
        return message;
    }
    
    public void LSBNormal() {
        int curvalue;
        int[] partmsg = new int[4];
        partmsg[0] = Integer.parseInt(message.substring(0, message.length()/4),2);
        partmsg[1] = Integer.parseInt(message.substring(message.length()/4, (2*(message.length()/4))),2);
        partmsg[2] = Integer.parseInt(message.substring(2*(message.length()/4), (3*(message.length()/4))),2);
        partmsg[3] = Integer.parseInt(message.substring(3*(message.length()/4), message.length()),2);
        
        /*
        System.out.println(message.substring(0, message.length()/4) + " " + message.substring(message.length()/4, (2*(message.length()/4))) 
                            + " " + message.substring(2*(message.length()/4), (3*(message.length()/4)))
                            + " " + message.substring(3*(message.length()/4),message.length()));
        System.out.println(partmsg[0] + " " + partmsg[1] + " " + partmsg[2] + " " + partmsg[3] + " ");
        */
        
        for (int i = 0; i < 4; i++) {
            y1[i] = 0;
            curvalue = y[i]; //ambil nilai y
            curvalue -= curvalue%(pow(2,key)); //kosongkan k-LSB
            //System.out.println("nilai y : " +curvalue);
            switch(i) { //isi sesuai message.
                case 0 : curvalue += partmsg[0]; break;
                case 1 : curvalue += partmsg[1]; break;
                case 2 : curvalue += partmsg[2]; break;
                case 3 : curvalue += partmsg[3]; break;
            }
            y1[i] = curvalue;
        }
    }
    
    public void LSBModified () {
        int MSB,res1,res0,res1m,min;
        String sMSB,sMSB1,sMSB1m,sLSB,sval, tsval;
        for (int i = 0; i < 4; i++) {
            y2[i]=0;
            sval = Integer.toBinaryString(y1[i]);
            if (sval.length()<8) {
                tsval ="";
                for(int idx=0; idx < (8-sval.length());idx++) {
                    tsval+="0";
                }
                sval = tsval + sval;
            }
            sLSB = sval.substring(sval.length()-key,sval.length());
            MSB = y1[i]/(int)(pow(2,key));
            sMSB = Integer.toBinaryString(MSB);
            sMSB1 = Integer.toBinaryString(MSB+1);
            sMSB1m = Integer.toBinaryString(MSB-1);
            if (MSB==0){
                sMSB1m = Integer.toBinaryString(MSB);
            }
            //System.out.println("MSB 0 1 -1: " + sMSB + " " + sMSB1 + " " +sMSB1m);
            res0 = Integer.parseInt((sMSB + sLSB),2);
            res1 = Integer.parseInt((sMSB1 + sLSB),2);
            res1m = Integer.parseInt((sMSB1m + sLSB),2);
            //System.out.println("res 0 1 -1: " + res0 + " " + res1 + " " +res1m);
            min = Math.abs(res0-y[i]); 
            if (min > Math.abs(res1-y[i])){
                if (Math.abs(res1-y[i]) > Math.abs(res1m-y[i])) {
                    y2[i]=res1m;
                }
                else { 
                    y2[i]=res1;
                }
            }
            else if (min > Math.abs(res1m-y[i])) {
                if (Math.abs(res1m-y[i]) > Math.abs(res1-y[i])) {
                   y2[i]=res1;
                }
                else { 
                    y2[i]=res1m;
                }
            }
            else {
                y2[i]=res0;
                   
            }
        }
            
    }
    
    public void readjustment() {
        int curvalue, diff, tdiff, kelasku;
        int[] list1 = new int[4];
        int[] list2 = new int[4];
        int[] list3 = new int[4];
        int[] _y0 = new int[3];
        int[] _y1 = new int[3];
        int[] _y2 = new int[3];
        int[] _y3 = new int[3];
        int[] brute = new int[4];
        int[] result = new int[4];
        
        for(int i=0; i<4;i++) {
            curvalue = y2[i];
            list1[i] = (int) (curvalue + (0*(pow(2,key))));
            list2[i] = (int) (curvalue + (1*(pow(2,key))));
            list3[i] = (int) (curvalue + (-1*(pow(2,key))));

        }
        
        _y0[0] = list1[0]; _y0[1] = list2[0]; _y0[2] = list3[0];
        _y1[0] = list1[1]; _y1[1] = list2[1]; _y1[2] = list3[1];
        _y2[0] = list1[2]; _y2[1] = list2[2]; _y2[2] = list3[2];
        _y3[0] = list1[3]; _y3[1] = list2[3]; _y3[2] = list3[3];
        
        diff = 1000; tdiff = 1000;
        
        for (int i =0; i<3; i++){
            for (int j=0; j<3; j++) {
                for (int k =0; k<3; k++) {
                    for (int l=0; l<3;l++) {
                        brute[0] = _y0[i];
                        brute[1] = _y1[j];
                        brute[2] = _y2[k];
                        brute[3] = _y3[l];
        
                        if (getAverageD(brute)<=threshold) {
                            kelasku = 0;
                        }
                        else {
                            kelasku = 1;
                        }
                                
                        if (!isErrorBlock(brute)) {
                            if (kelasku == kelas) {
                                tdiff = getDiff(brute,y);
                            }
                            if (diff > tdiff) {
                                diff = tdiff;
                                result[0]=i;result[1]=j;result[2]=k;result[3]=l;
                            }
                        }
                    }                    
                }
            }
        }
        yt[0] = _y0[result[0]]; yt[1] = _y1[result[1]]; yt[2] = _y2[result[2]]; yt[3] = _y3[result[3]];
    }
    
    public int getDiff(int[] yf, int[] y0){
        int total = 0;
        for (int i=0; i<4; i++){
            total+=(yf[i]-y[i])*(yf[i]-y[i]);
        }
        return total;
    }
    
    public void setKey() {
        if (getAverageD(y)<=threshold){
            key = keyLow;
            kelas = 0;
        }
        else {
            key = keyHigh;
            kelas = 1;
        }
    }
        
    public int getSizeAvailable() {
        return 4*key;
    }
    
    public boolean isErrorBlock(int y[]) {
        boolean error = false;
        if (((getMax(y)-getMin(y))>(2*threshold+2)) &&
                ((getAverageD(y)<=threshold) || (int)getAverageD(y)==threshold)) 
            error = true;
        return error;
    }
    
    public float getAverageD(int y[]){
        float d = (float)((y[0]-getMin(y))+(y[1]-getMin(y))+(y[2]-getMin(y))+(y[3]-getMin(y)))/3;
        return d;
    }
    
    public int getMax(int y[]){
        int max = y[0];
        for (int i=1; i<y.length; i++) {
            if (max < y[i])
                max = y[i];
        }
        return max;
    }
    
    public int getMin(int y[]){
        int min = y[0];
        for (int i=1; i<y.length; i++) {
            if (min > y[i])
                min = y[i];
        }
        return min;
    }

    
    /**
     * Getter / Setter
     */

    public int getKeyLow() {
        return keyLow;
    }

    public int getKeyHigh() {
        return keyHigh;
    }

    public int[] getY() {
        return y;
    }

    public void setY(int[] y) {
        this.y = y;
        setKey();
    }
    
    public void setY(int idx, int value) {
       y[idx] = value;
    }

    public int[] getY1() {
        return y1;
    }
    
    public void setY1(int[] y) {
        this.y1 = y;
    }
    
    public void setY1(int idx, int value) {
        y1[idx] = value;
    }

    public int[] getY2() {
        return y2;
    }
    
    public void setY2(int[] y) {
        this.y2 = y;
    }
    
    public void setY2(int idx, int value) {
        y2[idx] = value;
    }

    public int[] getYt() {
        return yt;
    }

    public void setYt(int idx, int value) {
        yt[idx] = value;
    }

    public int getThreshold() {
        return threshold;
    }

    public int getKey() {
        return key;
    }
    
    public int getKelas() {
        return kelas;
    }
    
    public void setKelas(int kelas) {
        this.kelas = kelas;
    }
    
    public String getMessage() {
        return message;
    }
    
    public void setMessage(String message) {
        this.message = message;
    }
    
}
