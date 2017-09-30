package cn.luo.yuan.maze.level;

import cn.luo.yuan.maze.model.Element;
import cn.luo.yuan.maze.model.Hero;
import cn.luo.yuan.maze.model.NPCLevelRecord;
import cn.luo.yuan.maze.model.Pet;
import cn.luo.yuan.maze.model.Race;
import cn.luo.yuan.maze.model.dlc.DLC;
import cn.luo.yuan.maze.model.dlc.NPCDLC;
import cn.luo.yuan.maze.model.names.FirstName;
import cn.luo.yuan.maze.model.names.SecondName;
import cn.luo.yuan.maze.serialize.ObjectTable;

import java.io.File;
import java.io.IOException;

/**
 * Copyright @Luo
 * Created by Gavin Luo on 9/30/2017.
 */
public class BuildLeverRecord {

    public static void main(String...args) throws IOException {
        ObjectTable<NPCLevelRecord> table = new ObjectTable<NPCLevelRecord>(NPCLevelRecord.class, new File("npc_record"));
        leierdan(table);
    }
    public static void leierdan(ObjectTable<NPCLevelRecord> dlcObjectTable) throws IOException {
        Hero hero = new Hero();
        hero.setMaxHp(7000000);
        hero.setHp(hero.getMaxHp());
        hero.setAtk(9000000);
        hero.setDef(7000000);
        hero.setRace(Race.Orger.ordinal());
        hero.setElement(Element.FIRE);
        hero.setName("某鸟真帅!");
        hero.setId(hero.getName());
        NPCLevelRecord record = new NPCLevelRecord(hero);
        record.setSex(0);
        record.setLevel(1000);
        record.setHead("Actor1_7.png");
        Pet pet = new Pet();
        pet.setIntimacy(100);
        pet.setElement(Element.WOOD);
        pet.setType("幽魂");
        pet.setIndex(429);
        pet.setMaxHp(10000);
        pet.setHp(pet.getMaxHp());
        pet.setDef(1000);
        pet.setAtk(200000);
        pet.setRace(Race.Ghosr);
        pet.setFirstName(FirstName.frailty);
        pet.setSecondName(SecondName.brave);
        record.getPets().add(pet);
        dlcObjectTable.save(record);
    }
}
