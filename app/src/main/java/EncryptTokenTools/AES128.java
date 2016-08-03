package EncryptTokenTools;


import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

import EncryptTokenTools.BASE64_src.sun.misc.BASE64Decoder;
import EncryptTokenTools.BASE64_src.sun.misc.BASE64Encoder;


public class AES128 {

    private String sKey = "0123456789abcdef";
    private String ivParameter = "0123456789abcdef";

    private static AES128 instance = null;

    private AES128() {

    }

    public static AES128 getInstance() {
        if (instance == null)
            instance = new AES128();
        return instance;
    }

    public String encrypt(String sSrc) throws Exception {
        Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");

        SecretKeySpec skeySpec = new SecretKeySpec(sKey.getBytes(), "AES");

        IvParameterSpec iv = new IvParameterSpec(ivParameter.getBytes());
        cipher.init(Cipher.ENCRYPT_MODE, skeySpec, iv);

        byte[] encrypted = cipher.doFinal(sSrc.getBytes("utf-8"));

        return new BASE64Encoder().encode(encrypted);
    }

    public String decrypt(String sSrc) throws Exception {
        try {
            byte[] raw = sKey.getBytes("ASCII");
            SecretKeySpec skeySpec = new SecretKeySpec(raw, "AES");
            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
            IvParameterSpec iv = new IvParameterSpec(ivParameter.getBytes());
            cipher.init(Cipher.DECRYPT_MODE, skeySpec, iv);

            byte[] encrypted1 = new BASE64Decoder().decodeBuffer(sSrc);

            byte[] original = cipher.doFinal(encrypted1);

            String originalString = new String(original, "utf-8");
            return originalString;
        } catch (Exception ex) {
            return null;
        }
    }

    public static String toHexString1(byte[] b) {
        StringBuffer buffer = new StringBuffer();
        for (int i = 0; i < b.length; ++i) {
            buffer.append(toHexString1(b[i]));
        }
        return buffer.toString();
    }

    public static String toHexString1(byte b) {
        String s = Integer.toHexString(b & 0xFF);
        if (s.length() == 1) {
            return "0" + s;
        } else {
            return s;
        }
    }


    public static void main_test(String[] args) throws Exception {

        String cSrc = "我来自www.wenhq.com";
        System.out.println(cSrc);

        long lStart = System.currentTimeMillis();
        String enString = AES128.getInstance().encrypt(cSrc);
        System.out.println("加密后的字串是：" + enString);

        long lUseTime = System.currentTimeMillis() - lStart;
        System.out.println("加密耗时：" + lUseTime + "毫秒");

        lStart = System.currentTimeMillis();
        String DeString = AES128.getInstance().decrypt(enString);
        System.out.println("解密后的字串是：" + DeString);
        lUseTime = System.currentTimeMillis() - lStart;
        System.out.println("解密耗时：" + lUseTime + "毫秒");
    }
}
