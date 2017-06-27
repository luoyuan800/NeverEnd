package cn.luo.yuan.maze.utils;

import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Copyright 2015 gluo.
 * ALL RIGHTS RESERVED.
 * Created by gluo on 9/17/2015.
 */
public class Random extends java.util.Random {
    private static final long serialVersionUID = 1L;
    public Random(long seed){
        super(seedUniquifier() ^ seed);
    }

    private static long seedUniquifier() {
        // L'Ecuyer, "Tables of Linear Congruential Generators of
        // Different Sizes and Good Lattice Structure", 1999
        for (;;) {
            long current = seedUniquifier.get();
            long next = current * 181783497276652981L;
            if (seedUniquifier.compareAndSet(current, next))
                return next;
        }
    }
    private static final AtomicLong seedUniquifier
            = new AtomicLong(8682522807148012L);


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

    public final <T> T randomItem(T[] ts){
        if(ts == null || ts.length == 0){
            return null;
        }
        return ts[nextInt(ts.length)];
    }

    public void randomBinary(byte[] binary){
        for(int i = 0; i< binary.length; i++){
            binary[i] = (byte)nextInt(2);
        }
    }


    public long randomRange(long min, long max) {
        if(max < min){
            max = max ^ min;
            min = max ^ min;
            max = max ^ min;
        }
        return min + nextLong(max - min);
    }

    public float randomRange(float min, float max) {
        if(max < min){
            return 0;
        }
        return min + nextFloat(max - min);
    }

    public float nextFloat(float max){
        String value = String.valueOf(max);
        int dotIndex = value.indexOf(".");
        int multiple = 1;
        if(dotIndex >= 0){
            int length  = value.length() - dotIndex - 1;
            while (length-- > 0){
                multiple *= 10;
            }
        }
        return nextLong((long)(max * multiple))/multiple;
    }

    public long reduceToSpecialDigit(Long number, int digit){
        long maxValue = (long)Math.pow(10, digit);
        while(number >= maxValue){
            String numStr = String.valueOf(number);
            number = 0L;
            for(int i=0 ; i< numStr.length(); i++){
                number += nextLong(Integer.parseInt(numStr.charAt(i) + ""));
            }
        }
        return number;
    }

    public <T> T randomItem(ArrayList<T> ts) {
        if(ts.size() <= 0){
            return null;
        }
        return ts.get(nextInt(ts.size()));
    }
}
