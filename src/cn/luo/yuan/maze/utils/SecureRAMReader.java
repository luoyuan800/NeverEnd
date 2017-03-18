package cn.luo.yuan.maze.utils;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

/**
 * Created by luoyuan on 2017/3/18.
 */
public class SecureRAMReader {
    private Cipher encode;
    private Cipher decode;

    public SecureRAMReader(byte[] key) {
        try {
            SecretKey secretKey = new SecretKeySpec(key, "AES");
            encode = Cipher.getInstance("AES/ECB/PKCS5Padding");
            encode.init(Cipher.ENCRYPT_MODE, secretKey);
            decode = Cipher.getInstance("AES/ECB/PKCS5Padding");
            decode.init(Cipher.DECRYPT_MODE, secretKey);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public long decodeLong(byte[] data) {
        try {
            if (decode != null) {
                String value = new String(decode.doFinal(data), "UTF-8");
                return Long.parseLong(value);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0L;
    }

    public byte[] encodeLong(long data) {
        try {
            return encode.doFinal(String.valueOf(data).getBytes("UTF-8"));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return new byte[0];
    }
}
