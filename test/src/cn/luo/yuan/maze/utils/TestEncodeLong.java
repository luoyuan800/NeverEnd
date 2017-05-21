package cn.luo.yuan.maze.utils;


import org.testng.annotations.Test;

import static org.testng.Assert.assertEquals;

/**
 * Created by luoyuan on 2017/5/21.
 */
public class TestEncodeLong {
    @Test
    public void testEncode(){
        EncodeLong encodeLong = new EncodeLong(0);
        for(int i =0; i< 2000; i++){
            encodeLong.setValue(i);
            assertEquals(i,encodeLong.getValue(), "They should equals but not!");
        }
    }

    @Test
    public void test(){

    }
}
