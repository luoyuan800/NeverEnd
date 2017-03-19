package cn.luo.yuan.maze.utils;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

/**
 * Created by luoyuan on 2017/3/18.
 * 我们需要在创建新的 存档的时候生成一个AES的key然后存起来，在每次加载存档之前我们需要先构建这个RAMReader
 */
public class SecureRAMReader {
    private Cipher encode;
    private Cipher decode;

    public static byte[] generateKey(){
        try {
            KeyGenerator keyGenerator = KeyGenerator.getInstance("AES");//获取一个AES相关的KeyGenerator
            keyGenerator.init(128);//指定生成密钥的长度为128
            return keyGenerator.generateKey().getEncoded();//生成一个密钥
        }catch (Exception e){
            e.printStackTrace();
        }
        //Default is admin
        return new byte[]{-104, -127, 105, -71, -103, 59, -43, 12, -116, 69, -115, 14, 28, 74, -26, -107};
    }

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
