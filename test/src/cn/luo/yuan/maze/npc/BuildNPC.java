package cn.luo.yuan.maze.npc;

import cn.luo.yuan.maze.model.Element;
import cn.luo.yuan.maze.model.Hero;
import cn.luo.yuan.maze.model.Race;
import cn.luo.yuan.maze.model.gift.Gift;
import cn.luo.yuan.serialize.FileObjectTable;
import cn.luo.yuan.serialize.ObjectTable;

import java.io.File;
import java.io.IOException;

/**
 * Copyright @Luo
 * Created by Gavin Luo on 9/19/2017.
 */
public class BuildNPC {
    public static void main(String...args){
        ObjectTable<Hero> heroObjectTable = new FileObjectTable<Hero>(Hero.class, new File("npc"));
        build(heroObjectTable);
    }

    public static void build(ObjectTable<Hero> heroObjectTable) {
        Hero hero = new Hero();
        hero.setName("*VIP1*倾羽");
        hero.setElement(Element.NONE);
        hero.setRace(Race.Elyosr.ordinal());
        hero.setAtkGrow(36);
        hero.setDefGrow(46);
        hero.setHpGrow(56);
        hero.setDef(1000);
        hero.setAtk(5000);
        hero.setMaxHp(50000);
        hero.setId("0b5bf93c00");
        hero.setGift(Gift.Element);
        try {
            heroObjectTable.save(hero);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
