package cn.luo.yuan.maze.utils;

import org.testng.annotations.Test;

import static org.testng.AssertJUnit.assertEquals;


/**
 * Created by luoyuan on 2017/8/4.
 */
public class TestEncodeFloat {
    @Test
    public void testFloat(){
        EncodeFloat encodeFloat = new EncodeFloat(0.0f);
        Random random = new Random(System.currentTimeMillis());
        for(int i = 0; i< 100; i++){
            float v = random.nextFloat();
            encodeFloat.setValue(v);
            System.out.println("original: " + v + ",value: " + encodeFloat.getValue());
            assertEquals("Should not change!", v, encodeFloat.getValue());
        }
    }
}
