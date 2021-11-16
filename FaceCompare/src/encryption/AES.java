/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package encryption;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;

/**
 *
 * @author DELL
 */
public class AES {
     public static final String algorithm = "AES";
     public static SecretKey generateKey() {
        try {
            KeyGenerator keyGenerator = KeyGenerator.getInstance(algorithm);
            keyGenerator.init(128);
            return keyGenerator.generateKey();
        } catch (Exception ex) {
            System.out.println(ex);
        }
        return null;
    }

    public static byte[] encrypt(SecretKey secretKey, byte[] data) {
        try {
            Cipher cipher = Cipher.getInstance(algorithm);
            cipher.init(Cipher.ENCRYPT_MODE, secretKey);
            return cipher.doFinal(data);
        } catch (Exception ex) {
            System.out.println(ex);
        }
        return null;
    }

    public static byte[] decrypt(SecretKey secretKey, byte[] encrypted) {
        try {
            Cipher cipher = Cipher.getInstance(algorithm);
            cipher.init(Cipher.DECRYPT_MODE, secretKey);
            return cipher.doFinal(encrypted);
        } catch (Exception ex) {

        }
        return null;
    }

   
}
