package com.example.mydiary;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
/*
 * MD5 算法
 */
public class MD5 {

    public MD5() {
    }

    public static String getMD5Code(String str) {
        MessageDigest md;
        StringBuffer sb = new StringBuffer();
        try {
            md = MessageDigest.getInstance("MD5");
            md.update(str.getBytes());
            byte[] data = md.digest();
            int index;
            for (byte b : data) {
                index = b;
                if (index < 0) index += 256;
                if (index < 16) sb.append("0");
                sb.append(Integer.toHexString(index));
            }
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return sb.toString();
    }
}