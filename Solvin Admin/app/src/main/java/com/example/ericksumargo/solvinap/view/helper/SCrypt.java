package com.example.ericksumargo.solvinap.view.helper;

import java.security.NoSuchAlgorithmException;

import javax.crypto.Cipher;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

import static android.R.attr.key;

/**
 * Created by Erick Sumargo on 3/22/2017.
 */

public class SCrypt {
    //    HELPER
    private IvParameterSpec ivSpec;
    private SecretKeySpec keySpec;
    private Cipher cipher;

    //    VARIABLE
    private int trim;
    private final int size = 16;
    private byte[] encrypted, decrypted, buffer, temp;

    private final static String iv = "9i7t64OpS9WE9y25", key = "PgK3RR2nAQ5wC5lD";
    private String hex;

    public SCrypt() {
        ivSpec = new IvParameterSpec(iv.getBytes());
        keySpec = new SecretKeySpec(key.getBytes(), "AES");

        try {
            cipher = Cipher.getInstance("AES/CBC/NoPadding");
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (NoSuchPaddingException e) {
            e.printStackTrace();
        }
    }

    public static SCrypt getInstance() {
        return new SCrypt();
    }

    public byte[] encrypt(String text) throws Exception {
        if (text == null || text.length() == 0) {
            throw new Exception("String null");
        } else {
            encrypted = null;
            try {
                cipher.init(Cipher.ENCRYPT_MODE, keySpec, ivSpec);
                encrypted = cipher.doFinal(padString(text).getBytes());
            } catch (Exception e) {
                throw new Exception(e.getMessage());
            }
            return encrypted;
        }
    }

    public byte[] decrypt(String code) throws Exception {
        if (code == null || code.length() == 0) {
            throw new Exception("String null");
        } else {
            decrypted = null;
            try {
                cipher.init(Cipher.DECRYPT_MODE, keySpec, ivSpec);
                decrypted = removePaddingZeros(cipher.doFinal(hexToBytes(code)));
            } catch (Exception e) {
                throw new Exception(e.getMessage());
            }
            return decrypted;
        }
    }

    public String bytesToHex(byte[] data) {
        if (data == null) {
            return null;
        } else {
            hex = "";
            for (int i = 0; i < data.length; i++) {
                if ((data[i] & 0xFF) < 16)
                    hex = hex + "0" + java.lang.Integer.toHexString(data[i] & 0xFF);
                else
                    hex = hex + java.lang.Integer.toHexString(data[i] & 0xFF);
            }
            return hex;
        }
    }

    private byte[] hexToBytes(String hex) {
        if (hex == null) {
            return null;
        } else if (hex.length() < 2) {
            return null;
        } else {
            buffer = new byte[hex.length() / 2];
            for (int i = 0; i < buffer.length; i++) {
                buffer[i] = (byte) Integer.parseInt(hex.substring(i * 2, i * 2 + 2), 16);
            }
            return buffer;
        }
    }

    private String padString(String source) {
        final int padLength = size - source.length() % size;
        for (int i = 0; i < padLength; i++) {
            source += ' ';
        }
        return source;
    }

    private byte[] removePaddingZeros(byte[] bytes) {
        if (bytes.length > 0) {
            trim = 0;
            for (int i = bytes.length - 1; i >= 0; i--) {
                if (bytes[i] == 0) {
                    trim++;
                }
            }
            if (trim > 0) {
                temp = new byte[bytes.length - trim];
                System.arraycopy(bytes, 0, temp, 0, bytes.length - trim);
                bytes = temp;
            }
        }
        return bytes;
    }
}