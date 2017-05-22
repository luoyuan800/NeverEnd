package cn.luo.yuan.maze.utils;

import org.testng.annotations.Test;

import static org.testng.Assert.assertTrue;

/**
 * Created by gluo on 5/17/2017.
 */
public class TestRandom {
    @Test
    public void testRandomFloat(){
        Random r = new Random(System.currentTimeMillis());
        float min = 1.2f;
        float max = 1.6f;
        for(int i =0; i<10;i++){
            assertTrue(r.nextFloat(max) < max, "next result should lesser then max");
            float v = r.randomRange(min, max);
            assertTrue(v < max, "next result should lesser then max");
            assertTrue(v >= min, "next result should larger or equals to min");
        }
    }
    @Test
    public void testRandomLong(){
        Random r = new Random(System.currentTimeMillis());
        Long min = Long.MIN_VALUE/10;
        Long max = Long.MAX_VALUE -2;
        for(int i =0; i<10;i++){
            assertTrue(r.nextLong(max) < max, "next result should lesser then max");
            Long v = r.randomRange(min, max);
            assertTrue(v < max, "next result should lesser then max");
            assertTrue(v >= min, "next result should larger or equals to min");
            v = r.randomRange(max, min);
            assertTrue(v < max, "next result should lesser then max");
            assertTrue(v >= min, "next result should larger or equals to min");
        }
    }
}
