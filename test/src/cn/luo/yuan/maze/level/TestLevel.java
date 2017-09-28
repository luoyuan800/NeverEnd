package cn.luo.yuan.maze.level;

import cn.luo.yuan.maze.model.real.level.ElyosrRealLevel;
import cn.luo.yuan.maze.utils.Random;
import org.testng.annotations.Test;

/**
 * Copyright @Luo
 * Created by Gavin Luo on 9/11/2017.
 */
public class TestLevel {
    @Test
    public void testLevelCalculate(){
        long w = 5;
        long l = 2;
        System.out.println(ElyosrRealLevel.Companion.getLevel(w * 100));
        System.out.println(ElyosrRealLevel.Companion.getLevel(w * 200));
        long point = 0;
        Random random = new Random(System.currentTimeMillis());
        for(int i =1; i< 100; i++){
            if(random.nextBoolean()){
                point += w;
            }else{
                point -= l;
            }
        }
        System.out.println(point + ": " + ElyosrRealLevel.Companion.getLevel(point));
    }
}
