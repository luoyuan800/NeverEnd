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
        buildAngel(dlcTable);
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
    public static void buildAngel(ObjectTable<MonsterDLC> table) throws IOException {
        MonsterDLC dlc = new MonsterDLC();
        dlc.setTitle("天降之物");
        dlc.setDebrisCost(10);
        dlc.setDesc("传说天使是圣洁的，是代表勇敢，代表智慧。但其实他们也有另一面，黑暗、堕落、淫乱。据说，天使是没有性别，那么，俊美的外表下到底是否有你想要的灵魂？此DLC包含全新的七种天使。");
        Monster monster = new Monster();
        monster.setIndex(68);
        monster.setHp(76);
        monster.setMaxHp(76);
        monster.setAtk(219);
        monster.setDef(123);
        monster.setEggRate(40);
        monster.setPetRate(15);
        monster.setSilent(16);
        monster.setHitRate(10);
       monster.setSex(-1);
        monster.setRank(2);
        monster.setNext(69);
        monster.setRace(Race.Elyosr);
        monster.setType("天使");
        monster.setDesc("代表圣洁、良善，正直，保护平民不被恶魔侵扰。");
        dlc.getMonsters().add(monster);
        dlc.getImage().add(readImage("D:\\NeverEnd\\assets\\monster\\angle_0.png"));
        //---------//
        monster = new Monster();
        monster.setIndex(71);
        monster.setHp(51);
        monster.setMaxHp(51);
        monster.setAtk(105);
        monster.setDef(255);
        monster.setEggRate(19);
        monster.setPetRate(22);
        monster.setSilent(37);
        monster.setHitRate(28);
        monster.setSex(-1);
        monster.setRank(3);
        monster.setRace(Race.Eviler);
        monster.setType("天使喵");
        monster.setDesc("它的眼神为何如此忧伤，或许只是因为你不再爱它。");
        dlc.getMonsters().add(monster);
        dlc.getImage().add(readImage("D:\\NeverEnd\\assets\\monster\\angle_miao.png"));
        //---------//
        monster = new Monster();
        monster.setIndex(92);
        monster.setHp(240);
        monster.setMaxHp(240);
        monster.setAtk(68);
        monster.setDef(237);
        monster.setEggRate(9);
        monster.setPetRate(22);
        monster.setSilent(37);
        monster.setHitRate(18);
        monster.setSex(1);
        monster.setRank(4);
        monster.setRace(Race.Elyosr);
        monster.setType("妮姆芙");
        monster.setDesc("β型号");
        dlc.getMonsters().add(monster);
        dlc.getImage().add(readImage("D:\\NeverEnd\\assets\\monster\\nimufu.png"));
        //---------//
        monster = new Monster();
        monster.setIndex(69);
        monster.setHp(86);
        monster.setMaxHp(86);
        monster.setAtk(175);
        monster.setDef(253);
        monster.setEggRate(21);
        monster.setPetRate(13);
        monster.setSilent(12);
        monster.setHitRate(10);
        monster.setSex(-1);
        monster.setRace(Race.Orger);
        monster.setRank(3);
        monster.setType("黑天使");
        monster.setDesc("被人性中最最邪恶的一面污染的天使就会进化为黑天使。");
        dlc.getMonsters().add(monster);
        dlc.getImage().add(readImage("D:\\NeverEnd\\assets\\monster\\angle_1.png"));
        //---------//
        monster = new Monster();
        monster.setIndex(70);
        monster.setHp(41);
        monster.setMaxHp(41);
        monster.setFirstName(FirstName.frailty);
        monster.setSecondName(SecondName.face);
        monster.setAtk(105);
        monster.setDef(210);
        monster.setEggRate(9);
        monster.setPetRate(12);
        monster.setSilent(27);
        monster.setHitRate(18);
        monster.setSex(-1);
        monster.setRank(4);
        monster.setRace(Race.Elyosr);
        monster.setType("辉天使");
        monster.setDesc("磐涅，重生，进化成更高级的生命。");
        dlc.getMonsters().add(monster);
        dlc.getImage().add(readImage("D:\\NeverEnd\\assets\\monster\\Angel.png"));
        //---------//
        monster = new Monster();
        monster.setIndex(93);
        monster.setHp(255);
        monster.setMaxHp(255);
        monster.setAtk(255);
        monster.setDef(37);
        monster.setEggRate(0);
        monster.setPetRate(22);
        monster.setSilent(37);
        monster.setHitRate(18);
        monster.setSex(1);
        monster.setRank(4);
        monster.setRace(Race.Elyosr);
        monster.setType("伊卡洛斯");
        monster.setDesc("α型号。攻击奇高，内心却很脆弱。");
        dlc.getMonsters().add(monster);
        dlc.getImage().add(readImage("D:\\NeverEnd\\assets\\monster\\yikaluos.png"));
        //---------//
        monster = new Monster();
        monster.setIndex(95);
        monster.setHp(125);
        monster.setMaxHp(125);
        monster.setAtk(255);
        monster.setDef(99);
        monster.setEggRate(0);
        monster.setPetRate(20);
        monster.setSilent(47);
        monster.setHitRate(38);
        monster.setSex(1);
        monster.setRank(5);
        monster.setRace(Race.Nonsr);
        monster.setType("CHISE");
        monster.setDesc("世界战争的\"最终兵器\"。");
        dlc.getMonsters().add(monster);
        dlc.getImage().add(readImage("D:\\NeverEnd\\assets\\monster\\chisi.png"));
        System.out.println(table.save(dlc));
    }
}
