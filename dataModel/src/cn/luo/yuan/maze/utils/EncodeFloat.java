package cn.luo.yuan.maze.utils;

import cn.luo.yuan.maze.model.effect.FloatValueEffect;

import java.io.Serializable;
import java.util.Arrays;

/**
 * Created by gluo on 4/24/2017.
 */
public class EncodeFloat implements EncodeNumber, Serializable {
    private final static long serialVersionUID = 1L;
    private int[] value;
    private byte[] key;
    private Random random;
    private boolean negative;

    public EncodeFloat(float def) {
        this.random = new Random(System.currentTimeMillis());
        setValue(def);
    }

    public String toString(){
        return StringUtils.formatNumber(getValue());
    }

    public Float getValue() {
        int[] value;
        byte[] key;
        boolean negative;
        synchronized (this){
            value = Arrays.copyOf(this.value, this.value.length);
            key = Arrays.copyOf(this.key, this.key.length);
            negative = this.negative;
        }
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < value.length; i++) {
            builder.append((char)(value[i] - key[i]));
        }
        float number = Float.parseFloat(builder.toString());
        return negative ? -number : number;
    }

    public synchronized void setValue(Number value) {
        float fv = value.floatValue();
        if (fv < 0) {
            negative = true;
            fv = -fv;
        }else{
            negative = false;
        }
        char[] chars = Float.toHexString(fv).toCharArray();
        key = new byte[chars.length];
        random.randomBinary(key);
        this.value = new int[chars.length];
        for (int i = 0; i < chars.length; i++) {
            int b = chars[i];
            this.value[i] = (byte) (b + key[i]);
        }

    }

    public static void main(String... args) {
        EncodeFloat encode = new EncodeFloat(1);
        for (int i = 0; i < 1000; i++) {
            encode.setValue((long)i);
            System.out.println("original: " + i + ", decode: " + encode.getValue());
        }
    }
}
