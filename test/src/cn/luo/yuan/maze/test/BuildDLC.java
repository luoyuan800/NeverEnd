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
    public static File root = new File("dlc");
    public static void main(String... args) throws IOException {
        ObjectTable<MonsterDLC> dlcTable = new ObjectTable<MonsterDLC>(MonsterDLC.class, new File("dlc"));
        buildJiuwei(dlcTable);
    }

    private static byte[] readImage(String path) throws IOException {
        FileInputStream fis = new FileInputStream(path);
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
        return bytes;
    }

    public static void buildJiuwei(ObjectTable<MonsterDLC> table) throws IOException {
        MonsterDLC dlc = new MonsterDLC();
        dlc.setTitle("九尾的传奇");
        dlc.setDebrisCost(1);
        dlc.setDesc("九尾，中国古代汉族神话传说中的奇兽。<br>古典传说中，九尾狐乃四脚怪兽，通体上下长有火红色的绒毛。善变化，蛊惑。九尾狐出，世间大乱。<br>购买这个DLC，如果你捕获到了红眼的九尾，那么你就可以开启九尾狐的拟人进化之路了！");
        Monster monster = new Monster();
        monster.setIndex(96);
        monster.setHp(185);
        monster.setMaxHp(185);
        monster.setFirstName(FirstName.frailty);
        monster.setSecondName(SecondName.face);
        monster.setAtk(199);
        monster.setDef(125);
        monster.setEggRate(0);
        monster.setPetRate(5);
        monster.setSilent(90);
        monster.setHitRate(10);
        monster.setSex(1);
        monster.setRank(5);
        monster.setRace(Race.Eviler);
        monster.setType("玉藻前");
        monster.setDesc("传说身受重伤的九尾逃到了日本，用仅存的妖力幻化成了人。不想却深受有受虐倾向的大和民族的崇拜，不小心成为了日本神话传说中的传奇妖怪。");
        dlc.getMonsters().add(monster);
        dlc.getImage().add(readImage("D:\\NeverEnd\\assets\\monster\\jiuwei_yzq.png"));
        //---------//
        monster = new Monster();
        monster.setIndex(81);
        monster.setHp(89);
        monster.setMaxHp(89);
        monster.setFirstName(FirstName.frailty);
        monster.setSecondName(SecondName.face);
        monster.setAtk(199);
        monster.setDef(225);
        monster.setEggRate(1);
        monster.setPetRate(15);
        monster.setSilent(70);
        monster.setHitRate(10);
        monster.setNext(96);
        monster.setSex(1);
        monster.setRace(Race.Wizardsr);
        monster.setRank(3);
        monster.setType("狐仙");
        monster.setDesc("九尾狐，中华古代汉族神话传说中的生物--青丘之山，有兽焉，其状如狐而九尾。这种形态的九尾狐拥有极高的智商，已然脱离妖的范畴。");
        dlc.getMonsters().add(monster);
        dlc.getImage().add(readImage("D:\\NeverEnd\\assets\\monster\\jiuwei_kynei.png"));
        //---------//
        monster = new Monster();
        monster.setIndex(41);
        monster.setHp(100);
        monster.setMaxHp(100);
        monster.setFirstName(FirstName.frailty);
        monster.setSecondName(SecondName.face);
        monster.setAtk(130);
        monster.setDef(199);
        monster.setEggRate(19);
        monster.setPetRate(25);
        monster.setSilent(7);
        monster.setHitRate(10);
        monster.setNext(81);
        monster.setSex(1);
        monster.setRank(2);
        monster.setRace(Race.Wizardsr);
        monster.setType("九尾狐");
        monster.setDesc("九尾狐，中华古代汉族神话传说中的生物--青丘之山，有兽焉，其状如狐而九尾。在迷宫创造者消失后才出现的生物。");
        dlc.getMonsters().add(monster);
        dlc.getImage().add(readImage("D:\\NeverEnd\\assets\\monster\\jiuweihu_red.png"));
        System.out.println(table.save(dlc));
    }
}
