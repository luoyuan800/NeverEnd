package cn.luo.yuan.maze.utils;


import org.testng.annotations.Test;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;

/**
 * Created by luoyuan on 2017/5/21.
 */
public class TestEncodeLong {
    @Test
    public void testEncode(){
        long start = System.currentTimeMillis();
        EncodeLong encodeLong = new EncodeLong(0);
        for(int i =-100; i< 200; i++){
            encodeLong.setValue((long)i);
            assertEquals(Long.valueOf(i),encodeLong.getValue(), "They should equals but not!");
        }
        assertTrue(System.currentTimeMillis() - start < 500,"Refresh too long, need to refactor encode code!");
    }

}
