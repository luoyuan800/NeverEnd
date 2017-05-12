package cn.luo.yuan.maze.utils;

import java.io.Serializable;

/**
 * Created by gluo on 4/24/2017.
 */
public class EncodeLong implements Serializable {
    private final static long serialVersionUID = 1L;
    private byte[] value;
    private byte[] key;
    private Random random;
    public EncodeLong(long def){
        this.random= new Random(System.currentTimeMillis());
        setValue(def);
    }

    public void setValue(long value){
        char[] chars = Long.toBinaryString(value).toCharArray();
        key = new byte[chars.length];
        random.randomBinary(key);
        this.value = new byte[chars.length];
        for(int i=0; i< chars.length; i++){
            byte b = Byte.parseByte(chars[i] + "");
            this.value[i] = (byte)(b ^ key[i]);
        }

    }

    public long getValue(){
            StringBuilder builder = new StringBuilder();
            for (int i = 0; i < value.length; i++) {
                builder.append(value[i] ^ key[i]);
            }
            return Long.parseLong(builder.toString(), 2);
    }

    public static void main(String...args){
        EncodeLong encode = new EncodeLong(1);
        for(int i = 0; i< 1000; i++){
            encode.setValue(i);
            System.out.println("original: " + i + ", decode: " + encode.getValue());
        }
    }
}
