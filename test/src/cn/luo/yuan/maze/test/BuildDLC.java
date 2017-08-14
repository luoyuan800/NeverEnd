package cn.luo.yuan.maze.test;

import cn.luo.yuan.maze.model.Element;
import cn.luo.yuan.maze.model.Monster;
import cn.luo.yuan.maze.model.Race;
import cn.luo.yuan.maze.model.dlc.MonsterDLC;
import cn.luo.yuan.maze.model.names.FirstName;
import cn.luo.yuan.maze.model.names.SecondName;
import cn.luo.yuan.maze.serialize.ObjectTable;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;

/**
 * Copyright @Luo
 * Created by Gavin Luo on 8/14/2017.
 */
public class BuildDLC {
    public static void main(String... args) throws IOException {
        ObjectTable<MonsterDLC> dlcTable = new ObjectTable<MonsterDLC>(MonsterDLC.class, new File("dlc"));
        MonsterDLC dlc = new MonsterDLC();
        dlc.setTitle("Pets");
        dlc.setDesc("Pet dlc");
        dlc.setDebrisCost(1);
        Monster monster = new Monster();
        monster.setHp(200);
        monster.setMaxHp(200);
        monster.setFirstName(FirstName.frailty);
        monster.setSecondName(SecondName.face);
        monster.setAtk(6);
        monster.setDef(1);
        monster.setElement(Element.FIRE);
        monster.setRace(Race.Elyosr);
        monster.setType("pet");
        monster.setIndex(1000);
        dlc.getMonsters().add(monster);
        FileInputStream fis = new FileInputStream("E:\\NeverEnd\\res\\drawable\\hsq.png");
        ArrayList<Byte> buffer = new ArrayList<>();
        Integer i = fis.read();
        while (i >= 0) {
            buffer.add(i.byteValue());
            i = fis.read();
        }
        byte[] bytes = new byte[buffer.size()];
        for (int j = 0; j < buffer.size(); j++) {
            bytes[j] = buffer.get(j);
        }
        dlc.getImage().add(bytes);
        System.out.println(dlcTable.save(dlc));
    }
}
