package cn.luo.yuan.maze.utils;

import java.io.Serializable;
import java.util.Arrays;

/**
 * Created by gluo on 4/24/2017.
 */
public class EncodeLong implements Serializable {
    private final static long serialVersionUID = 1L;
    private byte[] value;
    private byte[] key;
    private Random random;
    private boolean negative;

    public EncodeLong(long def) {
        this.random = new Random(System.currentTimeMillis());
        setValue(def);
    }

    public long getValue() {
        byte[] value;
        byte[] key;
        boolean negative;
        synchronized (this){
            value = Arrays.copyOf(this.value, this.value.length);
            key = Arrays.copyOf(this.key, this.key.length);
            negative = this.negative;
        }
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < value.length; i++) {
            builder.append(value[i] ^ key[i]);
        }
        long number = Long.parseLong(builder.toString(), 2);
        return negative ? -number : number;
    }

    public synchronized void setValue(long value) {
        if (value < 0) {
            negative = true;
            value = -value;
        }else{
            negative = false;
        }
        char[] chars = Long.toBinaryString(value).toCharArray();
        key = new byte[chars.length];
        random.randomBinary(key);
        this.value = new byte[chars.length];
        for (int i = 0; i < chars.length; i++) {
            byte b = Byte.parseByte(chars[i] + "");
            this.value[i] = (byte) (b ^ key[i]);
        }

    }

    public static void main(String... args) {
        EncodeLong encode = new EncodeLong(1);
        for (int i = 0; i < 1000; i++) {
            encode.setValue(i);
            System.out.println("original: " + i + ", decode: " + encode.getValue());
        }
    }
}
