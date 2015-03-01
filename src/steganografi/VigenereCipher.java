/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package steganografi;

import java.nio.charset.Charset;
import java.util.Scanner;

/**
 *
 * @author Fahziar
 */
public class VigenereCipher {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        // TODO code application logic here
        Scanner input = new Scanner(System.in);
        String key, plainText, cipherText;
        
        System.out.println("Text:");
        plainText = input.nextLine();
        System.out.println("Key: ");
        key = input.next();
        
        
        cipherText = vigenereExtendedEncryptAuto(key, plainText);
        cipherText = vigenereExtendedDecryptAuto(key, cipherText);
        
        System.out.println(cipherText);
    }
    
    public static String vigenereStandardEncrypt(String key, String plainText)
    {
        String out = "";
        int i , j;
        j = 0;
        
        for (i=0; i < plainText.length(); i++)
        {
            int currentKey = key.charAt(j) - 'A';
            int currentText = plainText.charAt(i) - 'A';
            int currentCipher;
            
            if (currentKey + currentText > 25)
            {
                currentCipher = currentText + currentKey - 26;
            } else {
                currentCipher = currentText + currentKey;
            }
            
            out = out.concat(Character.toString((char) ((int)currentCipher + (int)'A')));
            
            j++;
            if (key.length() == j)
            {
                j = 0;
            }
        }
        
        return out;
    }
    
    public static String vigenereStandardDecrypt(String key, String cipherText)
    {
        String out = "";
        int i , j;
        j = 0;
        
        for (i=0; i < cipherText.length(); i++)
        {
            int currentKey = key.charAt(j) - 'A';
            int currentText = cipherText.charAt(i) - 'A';
            int currentCipher;
            
            if (currentText - currentKey < 0)
            {
                currentCipher = currentText - currentKey + 26;
            } else {
                currentCipher = currentText - currentKey;
            }
            
            out = out.concat(Character.toString((char) ((int)currentCipher + (int)'A')));
            
            j++;
            if (key.length() == j)
            {
                j = 0;
            }
        }
        
        return out;
    }
    
    public static String vigenereExtendedEncrypt(String key, String plainText)
    {
        String out = "";
        int i , j;
        j = 0;
        
        for (i=0; i < plainText.length(); i++)
        {
            int currentKey = key.charAt(j);
            int currentText = plainText.charAt(i);
            int currentCipher;
            
            if (currentKey + currentText > 255)
            {
                currentCipher = currentText + currentKey - 256;
            } else {
                currentCipher = currentText + currentKey;
            }
            
            out = out.concat(Character.toString((char)currentCipher));
            
            j++;
            if (key.length() == j)
            {
                j = 0;
            }
        }
        
        return out;
    }
    
    public static String vigenereExtendedDecrypt(String key, String cipherText)
    {
        String out = "";
        int i , j;
        j = 0;
        
        for (i=0; i < cipherText.length(); i++)
        {
            int currentKey = key.charAt(j);
            int currentText = cipherText.charAt(i);
            int currentCipher;
            
            if (currentText - currentKey < 0)
            {
                currentCipher = currentText - currentKey + 256;
            } else {
                currentCipher = currentText - currentKey;
            }
            
            out = out.concat(Character.toString((char) currentCipher ));
            
            
            j++;
            if (key.length() == j)
            {
                j = 0;
            }
        }
        
        return out;
    }
    
    public static String vigenereStandardEncryptAuto(String key, String plainText)
    {
        String out = "";
        key = key.concat(plainText);
        int i , j;
        j = 0;
        
        for (i=0; i < plainText.length(); i++)
        {
            int currentKey = key.charAt(j) - 'A';
            int currentText = plainText.charAt(i) - 'A';
            int currentCipher;
            
            if (currentKey + currentText > 25)
            {
                currentCipher = currentText + currentKey - 26;
            } else {
                currentCipher = currentText + currentKey;
            }
            
            out = out.concat(Character.toString((char) ((int)currentCipher + (int)'A')));
            
            
            j++;
            if (key.length() == j)
            {
                j = 0;
            }
        }
        
        return out;
    }
    
    public static String vigenereStandardDecryptAuto(String key, String cipherText)
    {
        String out = "";
        int i , j;
        j = 0;
        
        for (i=0; i < cipherText.length(); i++)
        {
            int currentKey = key.charAt(j) - 'A';
            int currentText = cipherText.charAt(i) - 'A';
            int currentCipher;
            
            if (currentText - currentKey < 0)
            {
                currentCipher = currentText - currentKey + 26;
            } else {
                currentCipher = currentText - currentKey;
            }
            
            out = out.concat(Character.toString((char) ((int)currentCipher + (int)'A')));
            key = key.concat(Character.toString((char) ((int)currentCipher + (int)'A')));
            
            j++;
            if (key.length() == j)
            {
                j = 0;
            }
        }
        
        return out;
    }
    
    public static String vigenereExtendedEncryptAuto(String key, String plainText)
    {
        String out = "";
        key = key.concat(plainText);
        int i , j;
        j = 0;
        
        for (i=0; i < plainText.length(); i++)
        {
            int currentKey = key.charAt(j);
            int currentText = plainText.charAt(i);
            int currentCipher;
            
            if (currentKey + currentText > 255)
            {
                currentCipher = currentText + currentKey - 256;
            } else {
                currentCipher = currentText + currentKey;
            }
            
            out = out.concat(Character.toString((char)currentCipher));
            key = key.concat(Character.toString((char)currentCipher));
            
            j++;
            if (key.length() == j)
            {
                j = 0;
            }
        }
        
        return out;
    }
    
    public static String vigenereExtendedDecryptAuto(String key, String cipherText)
    {
        String out = "";
        int i , j;
        j = 0;
        
        for (i=0; i < cipherText.length(); i++)
        {
            int currentKey = key.charAt(j);
            int currentText = cipherText.charAt(i);
            int currentCipher;
            
            if (currentText - currentKey < 0)
            {
                currentCipher = currentText - currentKey + 256;
            } else {
                currentCipher = currentText - currentKey;
            }
            
            out = out.concat(Character.toString((char) currentCipher ));
            key = key.concat(Character.toString((char) currentCipher ));
            
            j++;
            if (key.length() == j)
            {
                j = 0;
            }
        }
        
        return out;
    }
    
    public static byte[] vigenereExtendedEncryptBytes(String key, byte[] bytes)
    {
        byte out[] = new byte[bytes.length];
        int i , j;
        j = 0;
        
        for (i=0; i < bytes.length; i++)
        {
            int currentKey = key.charAt(j);
            int currentText = bytes[i];
            int currentCipher;
            
            if (currentKey + currentText > 127)
            {
                currentCipher = currentText + currentKey - 256;
            } else {
                currentCipher = currentText + currentKey;
            }
            
            out[i] = (byte) currentCipher;
            
            j++;
            if (key.length() == j)
            {
                j = 0;
            }
        }
        
        return out;
    }
    
    public static byte[] vigenereExtendedDecryptBytes(String key, byte[] bytes)
    {
        byte[] out = new byte[bytes.length];
        int i , j;
        j = 0;
        
        for (i=0; i < bytes.length; i++)
        {
            int currentKey = key.charAt(j);
            int currentText = bytes[i];
            int currentCipher;
            
            if (currentText - currentKey < -128)
            {
                currentCipher = currentText - currentKey + 256;
            } else {
                currentCipher = currentText - currentKey;
            }
            
            out[i] = (byte) currentCipher;
            
            
            j++;
            if (key.length() == j)
            {
                j = 0;
            }
        }
        
        return out;
    }
    
    public static byte[] vigenereExtendedEncryptAuto(String stringKey, byte[] bytes)
    {
        byte[] out = new byte[bytes.length];
        byte[] key = new byte[stringKey.getBytes().length + bytes.length];
        
        int i , j;
        for(i=0; i<stringKey.getBytes().length; i++)
        {
            key[i] = stringKey.getBytes()[i];
        }
        j = i + 1;
        
        for(i=0; i<bytes.length; i++)
        {
            key[j] = bytes[i];
            j++;
        }
        j = 0;
        
        for (i=0; i < bytes.length; i++)
        {
            int currentKey = key[j];
            int currentText = bytes[i];
            int currentCipher;
            
            if (currentKey + currentText > 127)
            {
                currentCipher = currentText + currentKey - 256;
            } else {
                currentCipher = currentText + currentKey;
            }
            
            out[i] = (byte) currentCipher;
            
            j++;
            if (key.length == j)
            {
                j = 0;
            }
        }
        
        return out;
    }
    
    public static byte[] vigenereExtendedDecryptAuto(String stringKey, byte[] bytes)
    {
        byte[] out = new byte[bytes.length];
        byte[] key = new byte[stringKey.getBytes().length + bytes.length];
        
        int i , j;
        for(i=0; i<stringKey.getBytes().length; i++)
        {
            key[i] = stringKey.getBytes()[i];
        }
        j = 0;
        int k = stringKey.getBytes().length;
        for (i=0; i < bytes.length; i++)
        {
            int currentKey = key[j];
            int currentText = bytes[i];
            int currentCipher;
            
            if (currentText - currentKey < -128)
            {
                currentCipher = currentText - currentKey + 256;
            } else {
                currentCipher = currentText - currentKey;
            }
            
            out[i] = (byte) currentCipher;
            byte temp[] = new byte[1];
            temp[0] = (byte) currentCipher;
            key[k] = (byte) currentCipher;
            
            j++;
            k++;
            if (key.length == j)
            {
                j = 0;
            }
        }
        
        return out;
    }
    
}
