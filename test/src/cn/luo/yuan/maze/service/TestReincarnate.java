package cn.luo.yuan.maze.service;

import cn.luo.yuan.maze.model.Data;
import cn.luo.yuan.maze.model.Hero;
import org.testng.annotations.Test;

/**
 * Copyright @Luo
 * Created by Gavin Luo on 8/4/2017.
 */
public class TestReincarnate {
    @Test
    public void testReincarnateMax(){
        Hero hero = new Hero();
        for(int i =0; i < 100; i++){
            hero.setReincarnate(hero.getReincarnate() + 1);
            hero.setAtkGrow(hero.getReincarnate() * Data.GROW_INCRESE  + hero.getAtkGrow());
            hero.setDefGrow(hero.getReincarnate() * Data.GROW_INCRESE  + hero.getDefGrow());
            hero.setHpGrow(hero.getReincarnate() * Data.GROW_INCRESE  + hero.getHpGrow());
            hero.setMaxHp(hero.getReincarnate() * 20);
            hero.setHp(hero.getMaxHp());
            hero.setDef(hero.getReincarnate() * 5);
            hero.setAtk(hero.getReincarnate() * 15);
            System.out.println("atkg:" + hero.getAtkGrow());
            System.out.println("-------" + hero.getReincarnate());
        }
    }
}
