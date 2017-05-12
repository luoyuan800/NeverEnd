package cn.luo.yuan.maze.utils;

/**
 * Copyright 2015 gluo.
 * ALL RIGHTS RESERVED.
 * Created by gluo on 9/17/2015.
 */
public class Random extends java.util.Random {
    private static final long serialVersionUID = 1L;
    public Random(long seed){
        super(seed);
    }
    public long nextLong(long num) {
        try {
            if (num <= 0) {
                return 0;
            }
            if (num < Integer.MAX_VALUE) {
                return nextInt((int) num);
            } else {
                int j;
                long k;
                j = Integer.MAX_VALUE - 1;
                k = (num - Integer.MAX_VALUE);
                if (k > Integer.MAX_VALUE) {
                    k = k - nextInt(Integer.MAX_VALUE - 1);
                }
                if (k > Integer.MAX_VALUE / 2 && nextBoolean()) {
                    k = nextInt(Integer.MAX_VALUE);
                }
                return (long) nextInt(j) + k / 2;
            }
        } catch (Exception e) {
            return 1;
        }
    }

    public int nextInt(int num){
        if(num <= 0){
            return 0;
        }
        return super.nextInt(num);
    }

    public void randomBinary(byte[] binary){
        for(int i = 0; i< binary.length; i++){
            binary[i] = (byte)nextInt(2);
        }
    }


    public long randomRange(long max, long min) {
        if(max < min){
            max = max ^ min;
            min = max ^ min;
            max = max ^ min;
        }
        return min + nextLong(max - min);
    }

    public float randomRange(float max, float min) {
         return randomRange((long)max,(long)min);
    }
}
