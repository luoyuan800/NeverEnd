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
        for(int i =0; i< 2000; i++){
            encodeLong.setValue(i);
            assertEquals(i,encodeLong.getValue(), "They should equals but not!");
        }
        assertTrue(System.currentTimeMillis() - start < 500,"Refresh too long, need to refactor encode code!");
    }

    @Test
    public void test(){
        for(int i =0 ;i < 1000; i++) {
            char[] chars = Long.toBinaryString(i).toCharArray();
            StringBuilder builder = new StringBuilder();
            for (int j = 0; j < chars.length; j++) {
                builder.append(chars[j]);
            }
            System.out.println(builder);
        }
    }
}
