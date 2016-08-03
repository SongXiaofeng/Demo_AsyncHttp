package com.example.leon.demo_asynchttp;

/**
 * Created by Administrator on 2015/3/17.
 */

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/*
 * MD5 算法
*/
public class MD5 {

    // 全局数组
    private final static String[] strDigits = {"0", "1", "2", "3", "4", "5",
            "6", "7", "8", "9", "a", "b", "c", "d", "e", "f"};

    // 返回形式为数字跟字符串
    private static String byteToArrayString(byte bByte) {
        int iRet = bByte;
        // System.out.println("iRet="+iRet);
        if (iRet < 0) {
            iRet += 256;
        }
        int iD1 = iRet / 16;
        int iD2 = iRet % 16;
        return strDigits[iD1] + strDigits[iD2];
    }

    // 返回形式只为数字
//    private static String byteToNum(byte bByte) {
//        int iRet = bByte;
//        System.out.println("iRet1=" + iRet);
//        if (iRet < 0) {
//            iRet += 256;
//        }
//        return String.valueOf(iRet);
//    }

    // 转换字节数组为16进制字串
    private static String byteToString(byte[] bByte) {
        StringBuffer sBuffer = new StringBuffer();
        for (int i = 0; i < bByte.length; i++) {
            sBuffer.append(byteToArrayString(bByte[i]));
        }
        return sBuffer.toString();
    }

    private static String GetMD5Code(String strObj) {
        String resultString = null;
        try {
            resultString = new String(strObj);
            MessageDigest md = MessageDigest.getInstance("MD5");
            // md.digest() 该函数返回值为存放哈希值结果的byte数组
            resultString = byteToString(md.digest(strObj.getBytes()));
        } catch (NoSuchAlgorithmException ex) {
            ex.printStackTrace();
        }
        return resultString;
    }

    public static String getMD5Code(String strObj) {
        String va = GetMD5Code(strObj);
        int length = va.length();
        String first = va.substring(0, 2);
        String center = va.substring(2, length - 2);
        String lost = va.substring(length - 2, length);
        return GetMD5Code(lost + center + first);
    }

//    public static void main(String[] args) {
//        System.out.print(getMD5Code("123456"));
//    }
}