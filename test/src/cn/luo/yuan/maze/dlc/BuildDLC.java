package cn.luo.yuan.maze.dlc;

import cn.luo.yuan.maze.model.Element;
import cn.luo.yuan.maze.model.Hero;
import cn.luo.yuan.maze.model.Monster;
import cn.luo.yuan.maze.model.NPCLevelRecord;
import cn.luo.yuan.maze.model.Race;
import cn.luo.yuan.maze.model.dlc.DLC;
import cn.luo.yuan.maze.model.dlc.GoodsDLC;
import cn.luo.yuan.maze.model.dlc.MonsterDLC;
import cn.luo.yuan.maze.model.dlc.NPCDLC;
import cn.luo.yuan.maze.model.dlc.SkillDLC;
import cn.luo.yuan.maze.model.goods.Goods;
import cn.luo.yuan.maze.model.goods.types.EmptyAccessory;
import cn.luo.yuan.maze.model.goods.types.Invincible;
import cn.luo.yuan.maze.model.names.FirstName;
import cn.luo.yuan.maze.model.names.SecondName;
import cn.luo.yuan.maze.model.skill.Skill;
import cn.luo.yuan.maze.model.skill.pet.Mass;
import cn.luo.yuan.maze.model.skill.pet.Zoarium;
import cn.luo.yuan.serialize.FileObjectTable;
import cn.luo.yuan.serialize.ObjectTable;

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
        ObjectTable<DLC> dlcTable = new FileObjectTable<DLC>(DLC.class, new File("dlc"));
        /*buildAngel(dlcTable);
        buildJiuwei(dlcTable);
        buildEmprtyAccessory(dlcTable);
        buildSkillDlc(new Alayer(), dlcTable);
        buildSkillDlc(new Chaos(), dlcTable);
        buildSkillDlc(new Decide(), dlcTable);
        buildSkillDlc(new Exorcism(), dlcTable);
        buildSkillDlc(new Masimm(), dlcTable);
        buildSkillDlc(new Painkiller(), dlcTable);*/
        //buildEveoo(dlcTable);
        //buildNPC(dlcTable);
        buildSkillDlc(new Zoarium(), dlcTable);
//        buildGoodsDlc(new Invincible(), 50, dlcTable);
        //buildEveoo(dlcTable);
        //buildEveooII(dlcTable);
    }

    public static void buildNPC(ObjectTable<DLC> dlcObjectTable) throws IOException{
        /*chuyin(dlcObjectTable);
        yuanshuxion(dlcObjectTable);*/
        leierdan(dlcObjectTable);
        yuanxiaomei(dlcObjectTable);
    }

    public static void buildGoodsDlc(Goods goods, int cost, ObjectTable<DLC> dlcObjectTable) throws IOException {
        goods.setCount(1);
        GoodsDLC dlc = new GoodsDLC();
        dlc.setGoods(goods);
        dlc.setDebrisCost(cost);
        dlc.setTitle(goods.getName());
        dlc.setDesc(goods.getDesc());
        try {
            System.out.println(dlcObjectTable.save(dlc));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void yuanxiaomei(ObjectTable<DLC> dlcObjectTable) throws IOException {
        Hero hero = new Hero();
        hero.setMaxHp(500000);
        hero.setHp(hero.getMaxHp());
        hero.setAtk(700000);
        hero.setDef(4000000);
        hero.setRace(Race.Elyosr.ordinal());
        hero.setElement(Element.FIRE);
        hero.setName("袁小梅");
        hero.setId(hero.getName());
        NPCLevelRecord record = new NPCLevelRecord(hero);
        record.setSex(1);
        record.setLevel(500);
        record.setHead("Succubus.png");
        NPCDLC npcdlc = new NPCDLC();
        npcdlc.setDebrisCost(25);
        npcdlc.setDesc("不需要钥匙你也可以敲开我的门哦……");
        npcdlc.setNpc(record);
        npcdlc.setTitle(hero.getName());
        try {
            dlcObjectTable.save(npcdlc);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void leierdan(ObjectTable<DLC> dlcObjectTable) throws IOException {
        Hero hero = new Hero();
        hero.setMaxHp(7000000);
        hero.setHp(hero.getMaxHp());
        hero.setAtk(9000000);
        hero.setDef(7000000);
        hero.setRace(Race.Orger.ordinal());
        hero.setElement(Element.FIRE);
        hero.setName("雷二蛋");
        hero.setId(hero.getName());
        NPCLevelRecord record = new NPCLevelRecord(hero);
        record.setSex(0);
        record.setLevel(1000);
        record.setHead("General_m.png");
        NPCDLC npcdlc = new NPCDLC();
        npcdlc.setDebrisCost(5);
        npcdlc.setDesc("曾经，我很喜欢偷钥匙，但是你们这些混蛋在这个版本竟然都不再需要钥匙了，气死我了！！");
        npcdlc.setNpc(record);
        npcdlc.setTitle(hero.getName());
        try {
            dlcObjectTable.save(npcdlc);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public static void chuyin(ObjectTable<DLC> dlcObjectTable) throws IOException {
        Hero hero = new Hero();
        hero.setMaxHp(1200000);
        hero.setHp(hero.getMaxHp());
        hero.setAtk(1050000);
        hero.setDef(180000);
        hero.setRace(Race.Wizardsr.ordinal());
        hero.setElement(Element.FIRE);
        hero.setName("初音未来");
        hero.setId(hero.getName());
        NPCLevelRecord record = new NPCLevelRecord(hero);
        record.setSex(1);
        record.setLevel(300);
        record.setHead("chuyin");
        NPCDLC npcdlc = new NPCDLC();
        npcdlc.setDebrisCost(15);
        npcdlc.setDesc("你想邂逅小姐姐吗？那就兑换这个攻击高达100W的小姐姐陪你玩哦！兑换后有一定机率在迷宫中遇见小姐姐。");
        npcdlc.setNpc(record);
        npcdlc.setTitle(hero.getName());
        try {
            dlcObjectTable.save(npcdlc);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public static void yuanshuxion(ObjectTable<DLC> dlcObjectTable) throws IOException {
        Hero hero = new Hero();
        hero.setMaxHp(4000);
        hero.setHp(hero.getMaxHp());
        hero.setAtk(3000);
        hero.setDef(5000);
        hero.setRace(Race.Ghosr.ordinal());
        hero.setElement(Element.FIRE);
        hero.setName("龙剑森");
        hero.setId(hero.getName());
        NPCLevelRecord record = new NPCLevelRecord(hero);
        record.setSex(0);
        record.setLevel(100);
        record.setHead("Actor2_1.png");
        NPCDLC npcdlc = new NPCDLC();
        npcdlc.setDebrisCost(5);
        npcdlc.setDesc("这是迷失的朋友！我在这迷宫中晃荡多年，对各种怪异早已经见怪不怪了。或许你这个仿佛没有尽头的迷宫产生了绝望，但是外面的世界或许早已……<br>相信在这个世界里，你并不是孤独一人的。<br>继续往上爬吧，总有一天你会找到这个世界的真谛。<br>");
        npcdlc.setNpc(record);
        npcdlc.setTitle(hero.getName());
        try {
            dlcObjectTable.save(npcdlc);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void buildEmprtyAccessory(ObjectTable<DLC> dlcTable) throws IOException {
        GoodsDLC dlc = new GoodsDLC();
        EmptyAccessory ea = new EmptyAccessory();
        ea.setCount(1);
        dlc.setGoods(ea);
        dlc.setDebrisCost(500);
        dlc.setId(ea.getName());
        dlc.setDesc(ea.getDesc());
        dlc.setTitle(ea.getName());
        try {
            System.out.println(dlcTable.save(dlc));
        } catch (Exception e) {
            e.printStackTrace();
        }
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

    public static void buildJiuwei(ObjectTable<DLC> table) throws IOException {
        MonsterDLC dlc = new MonsterDLC();
        dlc.setTitle("九尾的传奇");
        dlc.setDebrisCost(50);
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
        dlc.getImage().add(readImage("E:\\NeverEnd\\assets\\monster\\jiuwei_yzq.png"));
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
        dlc.getImage().add(readImage("E:\\NeverEnd\\assets\\monster\\jiuwei_kynei.png"));
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
        dlc.getImage().add(readImage("E:\\NeverEnd\\assets\\monster\\jiuweihu_red.png"));
        try {
            System.out.println(table.save(dlc));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public static void buildAngel(ObjectTable<DLC> table) throws IOException {
        MonsterDLC dlc = new MonsterDLC();
        dlc.setTitle("天降之物");
        dlc.setDebrisCost(100);
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
        dlc.getImage().add(readImage("E:\\NeverEnd\\assets\\monster\\angle_0.png"));
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
        dlc.getImage().add(readImage("E:\\NeverEnd\\assets\\monster\\angle_miao.png"));
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
        dlc.getImage().add(readImage("E:\\NeverEnd\\assets\\monster\\nimufu.png"));
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
        dlc.getImage().add(readImage("E:\\NeverEnd\\assets\\monster\\angle_1.png"));
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
        dlc.getImage().add(readImage("E:\\NeverEnd\\assets\\monster\\Angel.png"));
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
        dlc.getImage().add(readImage("E:\\NeverEnd\\assets\\monster\\yikaluos.png"));
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
        dlc.getImage().add(readImage("E:\\NeverEnd\\assets\\monster\\chisi.png"));
        try {
            System.out.println(table.save(dlc));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void buildSkillDlc(Skill skill, ObjectTable<DLC> table) throws IOException {
        SkillDLC dlc = new SkillDLC();
        dlc.setSkill(skill);
        dlc.setDebrisCost(20);
        try {
            System.out.println(table.save(dlc));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void buildEveoo(ObjectTable<DLC> table) throws IOException {
        MonsterDLC dlc = new MonsterDLC();
        Monster monster = null;
        monster = new Monster();
        monster.setIndex(102);
        monster.setAtk(228);
        monster.setMaxHp(40);
        monster.setHp(monster.getMaxHp());
        monster.setDef(170);
        monster.setPetRate(0);
        monster.setEggRate(50);
        monster.setSilent(39);
        monster.setHitRate(18);
        monster.setSex(-1);
        monster.setRank(3);
        monster.setRace(Race.Ghosr);
        monster.setType("鬼伊布");
        monster.setDesc("伊布满亲密度的时候死亡，它会舍不得主人化身为鬼魂萦绕在主人的身边。");
        dlc.getMonsters().add(monster);
        dlc.getImage().add(readImage("pics/evee/evo_gui.png"));

        monster = new Monster();
        monster.setIndex(129);
        monster.setAtk(228);
        monster.setMaxHp(40);
        monster.setHp(monster.getMaxHp());
        monster.setDef(170);
        monster.setPetRate(0);
        monster.setEggRate(50);
        monster.setSilent(39);
        monster.setHitRate(90);
        monster.setSex(-1);
        monster.setRank(3);
        monster.setRace(Race.Ghosr);
        monster.setType("魂伊布");
        monster.setDesc("伊布满亲密度的时候死亡，它会舍不得主人化身为鬼魂萦绕在主人的身边。");
        dlc.getMonsters().add(monster);
        dlc.getImage().add(readImage("pics/evee/evo_gui_1.png"));

        monster = new Monster();
        monster.setIndex(119);
        monster.setAtk(208);
        monster.setMaxHp(140);
        monster.setHp(monster.getMaxHp());
        monster.setDef(90);
        monster.setPetRate(0);
        monster.setEggRate(50);
        monster.setSilent(29);
        monster.setHitRate(90);
        monster.setSex(-1);
        monster.setRank(3);
        monster.setRace(Race.Eviler);
        monster.setType("冰伊布");
        monster.setDesc("传说主人的五行属性为水的时候，伊布可以吸收水的力量进化成冰精灵。");
        dlc.getMonsters().add(monster);
        dlc.getImage().add(readImage("pics/evee/evo_bin.png"));

        monster = new Monster();
        monster.setIndex(106);
        monster.setAtk(128);
        monster.setMaxHp(140);
        monster.setHp(monster.getMaxHp());
        monster.setDef(170);
        monster.setPetRate(0);
        monster.setEggRate(50);
        monster.setSilent(29);
        monster.setHitRate(10);
        monster.setSex(-1);
        monster.setRank(3);
        monster.setRace(Race.Orger);
        monster.setType("暗伊布");
        monster.setDesc("如果一个亲密度低的伊布死亡，它会化身为黑暗寻找害死它的人进行报复。");
        dlc.getMonsters().add(monster);
        dlc.getImage().add(readImage("pics/evee/evo_an.png"));

        monster = new Monster();
        monster.setIndex(118);
        monster.setAtk(128);
        monster.setMaxHp(100);
        monster.setHp(monster.getMaxHp());
        monster.setDef(100);
        monster.setPetRate(0);
        monster.setEggRate(50);
        monster.setSilent(39);
        monster.setHitRate(10);
        monster.setSex(-1);
        monster.setRank(3);
        monster.setRace(Race.Orger);
        monster.setType("木伊布");
        monster.setDesc("传说主人的五行属性为木的时候，伊布可以吸收植物的精华的力量进化成木精灵。");
        dlc.getMonsters().add(monster);
        dlc.getImage().add(readImage("pics/evee/evo_cao.png"));

        monster = new Monster();
        monster.setIndex(125);
        monster.setAtk(208);
        monster.setMaxHp(140);
        monster.setHp(monster.getMaxHp());
        monster.setDef(50);
        monster.setPetRate(0);
        monster.setEggRate(50);
        monster.setSilent(49);
        monster.setHitRate(10);
        monster.setSex(-1);
        monster.setRank(3);
        monster.setRace(Race.Wizardsr);
        monster.setType("草伊布");
        monster.setDesc("周一专属伊布！");
        dlc.getMonsters().add(monster);
        dlc.getImage().add(readImage("pics/evee/evo_cao_1.png"));

        monster = new Monster();
        monster.setIndex(124);
        monster.setAtk(128);
        monster.setMaxHp(140);
        monster.setHp(monster.getMaxHp());
        monster.setDef(170);
        monster.setPetRate(0);
        monster.setEggRate(50);
        monster.setSilent(49);
        monster.setHitRate(10);
        monster.setSex(-1);
        monster.setRank(3);
        monster.setRace(Race.Elyosr);
        monster.setType("绿伊布");
        monster.setDesc("传说主人头顶绿油油，伊布可以吸收绿的精华的力量进化成绿精灵。");
        dlc.getMonsters().add(monster);
        dlc.getImage().add(readImage("pics/evee/evo_cao_2.png"));

        monster = new Monster();
        monster.setIndex(109);
        monster.setAtk(128);
        monster.setMaxHp(140);
        monster.setHp(monster.getMaxHp());
        monster.setDef(170);
        monster.setPetRate(0);
        monster.setEggRate(50);
        monster.setSilent(49);
        monster.setHitRate(10);
        monster.setSex(-1);
        monster.setRank(3);
        monster.setRace(Race.Wizardsr);
        monster.setType("月伊布");
        monster.setDesc("周三专属伊布!");
        dlc.getMonsters().add(monster);
        dlc.getImage().add(readImage("pics/evee/evo_chao.png"));

        monster = new Monster();
        monster.setIndex(110);
        monster.setAtk(28);
        monster.setMaxHp(40);
        monster.setHp(monster.getMaxHp());
        monster.setDef(250);
        monster.setPetRate(0);
        monster.setEggRate(50);
        monster.setSilent(49);
        monster.setHitRate(10);
        monster.setSex(-1);
        monster.setRank(3);
        monster.setRace(Race.Eviler);
        monster.setType("虫伊布");
        monster.setDesc("周四专属伊布。");
        dlc.getMonsters().add(monster);
        dlc.getImage().add(readImage("pics/evee/evo_cong.png"));

        monster = new Monster();
        monster.setIndex(111);
        monster.setAtk(28);
        monster.setMaxHp(40);
        monster.setHp(monster.getMaxHp());
        monster.setDef(250);
        monster.setPetRate(0);
        monster.setEggRate(50);
        monster.setSilent(49);
        monster.setHitRate(10);
        monster.setSex(-1);
        monster.setRank(3);
        monster.setRace(Race.Eviler);
        monster.setType("角伊布");
        monster.setDesc("过了周四当然就是充满希望的周五啦。");
        dlc.getMonsters().add(monster);
        dlc.getImage().add(readImage("pics/evee/evo_cong_1.png"));

        monster = new Monster();
        monster.setIndex(117);
        monster.setAtk(228);
        monster.setMaxHp(132);
        monster.setHp(monster.getMaxHp());
        monster.setDef(55);
        monster.setPetRate(0);
        monster.setEggRate(50);
        monster.setSilent(49);
        monster.setHitRate(10);
        monster.setSex(-1);
        monster.setRank(3);
        monster.setRace(Race.Elyosr);
        monster.setType("电伊布");
        monster.setDesc("传说主人的五行属性为金的时候，伊布可以吸收雷电的力量进化成电精灵。");
        dlc.getMonsters().add(monster);
        dlc.getImage().add(readImage("pics/evee/evo_dian.png"));
        dlc.setTitle("伊布伊布I");
        dlc.setDesc("解锁伊布12种进化形态，电、鬼、魂、草、虫、月、绿、木、暗、冰、角。");
        dlc.setDebrisCost(200);
        try {
            System.out.println(table.save(dlc));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void buildEveooII(ObjectTable<DLC> table) throws IOException {
        MonsterDLC dlc = new MonsterDLC();
        Monster monster = null;
        monster = new Monster();
        monster.setIndex(113);
        monster.setAtk(128);
        monster.setMaxHp(134);
        monster.setHp(monster.getMaxHp());
        monster.setDef(170);
        monster.setPetRate(0);
        monster.setEggRate(50);
        monster.setSilent(59);
        monster.setHitRate(18);
        monster.setSex(-1);
        monster.setRank(3);
        monster.setRace(Race.Orger);
        monster.setType("毒伊布");
        monster.setDesc("筋疲力尽的玩耍了一个周末，一想到明天就要上班了，伊布就会吸收主人的怨念进化成了饱含毒性毒伊布。");
        dlc.getMonsters().add(monster);
        dlc.getImage().add(readImage("pics/evee/evo_du.png"));

        monster = new Monster();
        monster.setIndex(131);
        monster.setAtk(218);
        monster.setMaxHp(79);
        monster.setHp(monster.getMaxHp());
        monster.setDef(165);
        monster.setPetRate(0);
        monster.setEggRate(50);
        monster.setSilent(19);
        monster.setHitRate(90);
        monster.setSex(-1);
        monster.setRank(3);
        monster.setRace(Race.Orger);
        monster.setType("黑暗伊布");
        monster.setDesc("如果一个亲密度低的伊布死亡，它会化身为黑暗寻找害死它的人进行报复。");
        dlc.getMonsters().add(monster);
        dlc.getImage().add(readImage("pics/evee/evo_er_1.png"));

        monster = new Monster();
        monster.setIndex(115);
        monster.setAtk(28);
        monster.setMaxHp(40);
        monster.setHp(monster.getMaxHp());
        monster.setDef(250);
        monster.setPetRate(0);
        monster.setEggRate(50);
        monster.setSilent(29);
        monster.setHitRate(9);
        monster.setSex(-1);
        monster.setRank(3);
        monster.setRace(Race.Eviler);
        monster.setType("飞伊布");
        monster.setDesc("亲密度也会影响伊布的体重，体重轻的时候就会进化了会飞的伊布了");
        dlc.getMonsters().add(monster);
        dlc.getImage().add(readImage("pics/evee/evo_fei.png"));

        monster = new Monster();
        monster.setIndex(116);
        monster.setAtk(128);
        monster.setMaxHp(40);
        monster.setHp(monster.getMaxHp());
        monster.setDef(170);
        monster.setPetRate(0);
        monster.setEggRate(50);
        monster.setSilent(29);
        monster.setHitRate(10);
        monster.setSex(-1);
        monster.setRank(3);
        monster.setRace(Race.Eviler);
        monster.setType("轻伊布");
        monster.setDesc("亲密度也会影响伊布的体重，体重轻的时候就会进化了会飞的伊布了。");
        dlc.getMonsters().add(monster);
        dlc.getImage().add(readImage("pics/evee/evo_fei_1.png"));

        monster = new Monster();
        monster.setIndex(112);
        monster.setAtk(128);
        monster.setMaxHp(40);
        monster.setHp(monster.getMaxHp());
        monster.setDef(170);
        monster.setPetRate(0);
        monster.setEggRate(50);
        monster.setSilent(39);
        monster.setHitRate(10);
        monster.setSex(-1);
        monster.setRank(3);
        monster.setRace(Race.Eviler);
        monster.setType("羽伊布");
        monster.setDesc("飞翔的周六，伊布也会和主人一起飞翔。");
        dlc.getMonsters().add(monster);
        dlc.getImage().add(readImage("pics/evee/evo_fei_2.png"));

        monster = new Monster();
        monster.setIndex(123);
        monster.setAtk(28);
        monster.setMaxHp(40);
        monster.setHp(monster.getMaxHp());
        monster.setDef(270);
        monster.setPetRate(0);
        monster.setEggRate(30);
        monster.setSilent(19);
        monster.setHitRate(40);
        monster.setSex(-1);
        monster.setRank(3);
        monster.setRace(Race.Wizardsr);
        monster.setType("钢伊布");
        monster.setDesc("传说主人的五行属性为金的时候，伊布可以吸收金的能量进化成坚不可摧的钢精灵。");
        dlc.getMonsters().add(monster);
        dlc.getImage().add(readImage("pics/evee/evo_gan.png"));

        monster = new Monster();
        monster.setIndex(105);
        monster.setAtk(228);
        monster.setMaxHp(140);
        monster.setHp(monster.getMaxHp());
        monster.setDef(70);
        monster.setPetRate(0);
        monster.setEggRate(50);
        monster.setSilent(49);
        monster.setHitRate(10);
        monster.setSex(-1);
        monster.setRank(3);
        monster.setRace(Race.Eviler);
        monster.setType("武伊布");
        monster.setDesc("为战斗而生的伊布进化型。");
        dlc.getMonsters().add(monster);
        dlc.getImage().add(readImage("pics/evee/evo_ge.png"));

        monster = new Monster();
        monster.setIndex(127);
        monster.setAtk(228);
        monster.setMaxHp(140);
        monster.setHp(monster.getMaxHp());
        monster.setDef(70);
        monster.setPetRate(0);
        monster.setEggRate(50);
        monster.setSilent(9);
        monster.setHitRate(40);
        monster.setSex(-1);
        monster.setRank(3);
        monster.setRace(Race.Elyosr);
        monster.setType("斗伊布");
        monster.setDesc("夜晚才能进化出来的伊布进化型。");
        dlc.getMonsters().add(monster);
        dlc.getImage().add(readImage("pics/evee/evo_ge_1.png"));

        monster = new Monster();
        monster.setIndex(120);
        monster.setAtk(228);
        monster.setMaxHp(140);
        monster.setHp(monster.getMaxHp());
        monster.setDef(70);
        monster.setPetRate(0);
        monster.setEggRate(50);
        monster.setSilent(9);
        monster.setHitRate(20);
        monster.setSex(-1);
        monster.setRank(3);
        monster.setRace(Race.Elyosr);
        monster.setType("火伊布");
        monster.setDesc("当然是需要主人的五行属性为火的时候才能看到这种进化型的。");
        dlc.getMonsters().add(monster);
        dlc.getImage().add(readImage("pics/evee/evo_huo.png"));

        monster = new Monster();
        monster.setIndex(126);
        monster.setAtk(228);
        monster.setMaxHp(140);
        monster.setHp(monster.getMaxHp());
        monster.setDef(70);
        monster.setPetRate(0);
        monster.setEggRate(30);
        monster.setSilent(9);
        monster.setHitRate(50);
        monster.setSex(-1);
        monster.setRank(3);
        monster.setRace(Race.Wizardsr);
        monster.setType("烈伊布");
        monster.setDesc("当然是需要主人的五行属性为火的时候才能看到这种进化型的。");
        dlc.getMonsters().add(monster);
        dlc.getImage().add(readImage("pics/evee/evo_huo_1.png"));

        monster = new Monster();
        monster.setIndex(130);
        monster.setAtk(228);
        monster.setMaxHp(140);
        monster.setHp(monster.getMaxHp());
        monster.setDef(170);
        monster.setPetRate(0);
        monster.setEggRate(10);
        monster.setSilent(9);
        monster.setHitRate(20);
        monster.setSex(-1);
        monster.setRank(3);
        monster.setRace(Race.Ghosr);
        monster.setType("灵伊布");
        monster.setDesc("伊布满亲密度的时候死亡，它会舍不得主人化身为鬼魂萦绕在主人的身边。");
        dlc.getMonsters().add(monster);
        dlc.getImage().add(readImage("pics/evee/evo_ling.png"));
        dlc.setTitle("伊布伊布II");
        dlc.setDesc("解锁伊布12种进化形态，毒、黑、飞、轻、羽、钢、武、斗、火、烈、灵。");
        dlc.setDebrisCost(200);
        try {
            System.out.println(table.save(dlc));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public static void buildLongerDLC(ObjectTable<DLC> table) throws IOException {
        MonsterDLC dlc = new MonsterDLC();
        dlc.setTitle("龙裔");
        dlc.setDebrisCost(50);
        dlc.setDesc("骚年，快来收集各种帅气的龙宠和我签订契约成为骚贱的龙骑士吧！");
        //---------//
        Monster monster = new Monster();
        monster.setIndex(30);
        monster.setAtk(50);
        monster.setDef(100);
        monster.setHp(200);
        monster.setMaxHp(200);
        monster.setEggRate(39);
        monster.setPetRate(19);
        monster.setSilent(7);
        monster.setHitRate(20);
        monster.setSex(1);
        monster.setRank(2);
        monster.setNext(98);
        monster.setRace(Race.Orger);
        monster.setType("蓝龙");
        monster.setDesc("西方神话传说中的一种生物，不知道为什么会出现在迷宫中。");
        dlc.getMonsters().add(monster);
        dlc.getImage().add(readImage("pics/long/long_pet.png"));

        monster = new Monster();
        monster.setIndex(98);
        monster.setAtk(108);
        monster.setDef(240);
        monster.setHp(237);
        monster.setMaxHp(237);
        monster.setEggRate(0);
        monster.setPetRate(0);
        monster.setSilent(7);
        monster.setHitRate(20);
        monster.setSex(-1);
        monster.setRank(3);
        monster.setRace(Race.Elyosr);
        monster.setType("翼神龙");
        monster.setDesc("太阳神的翼神龙。");
        dlc.getMonsters().add(monster);
        dlc.getImage().add(readImage("pics/long/ysl.png"));

        monster = new Monster();
        monster.setIndex(91);
        monster.setAtk(60);
        monster.setDef(177);
        monster.setHp(224);
        monster.setMaxHp(224);
        monster.setEggRate(1);
        monster.setPetRate(9);
        monster.setSilent(7);
        monster.setHitRate(2);
        monster.setNext(143);
        monster.setSex(-1);
        monster.setRank(2);
        monster.setRace(Race.Wizardsr);
        monster.setType("苍龙");
        monster.setDesc("苍龙又称青龙，是古代汉族神话传说中的灵兽。属于汉族传统文化中是四象之一，身似长蛇、麒麟首、鲤鱼尾、面有长须、犄角似鹿、有五爪、相貌威武。二十八宿中东方七宿,即角、亢、氐、房、心、尾、箕这七宿的形状又极似龙形，合称苍龙。");
        dlc.getMonsters().add(monster);
        dlc.getImage().add(readImage("pics/long/changlong.png"));
        try {
            System.out.println(table.save(dlc));
        } catch (Exception e) {
            e.printStackTrace();
        }

        monster = new Monster();
        monster.setIndex(31);
        monster.setAtk(50);
        monster.setDef(100);
        monster.setHp(200);
        monster.setMaxHp(200);
        monster.setEggRate(39);
        monster.setPetRate(19);
        monster.setSilent(7);
        monster.setHitRate(20);
        monster.setSex(-1);
        monster.setRank(2);
        monster.setNext(99);
        monster.setRace(Race.Orger);
        monster.setType("红龙");
        monster.setDesc("西方神话传说中的一种生物，不知道为什么会出现在迷宫中。");
        dlc.getMonsters().add(monster);
        dlc.getImage().add(readImage("pics/long/long_pet_red.png"));

        monster = new Monster();
        monster.setIndex(99);
        monster.setAtk(208);
        monster.setDef(240);
        monster.setHp(131);
        monster.setMaxHp(131);
        monster.setEggRate(0);
        monster.setPetRate(0);
        monster.setSilent(7);
        monster.setHitRate(25);
        monster.setSex(-1);
        monster.setRank(3);
        monster.setRace(Race.Orger);
        monster.setType("天空龙");
        monster.setDesc("欧西里斯的天空龙。");
        dlc.getMonsters().add(monster);
        dlc.getImage().add(readImage("pics/long/tkl.png"));

        monster = new Monster();
        monster.setIndex(406);
        monster.setAtk(108);
        monster.setDef(200);
        monster.setHp(101);
        monster.setMaxHp(101);
        monster.setEggRate(29);
        monster.setPetRate(19);
        monster.setSilent(1);
        monster.setHitRate(15);
        monster.setSex(-1);
        monster.setRank(2);
        monster.setRace(Race.Eviler);
        monster.setType("绿龙");
        monster.setDesc("传说中的原谅龙，性淫。");
        dlc.getMonsters().add(monster);
        dlc.getImage().add(readImage("pics/long/Dragon.png"));

        monster = new Monster();
        monster.setIndex(407);
        monster.setAtk(218);
        monster.setDef(140);
        monster.setHp(100);
        monster.setMaxHp(100);
        monster.setEggRate(9);
        monster.setPetRate(19);
        monster.setSilent(17);
        monster.setHitRate(5);
        monster.setSex(-1);
        monster.setRank(3);
        monster.setRace(Race.Orger);
        monster.setType("奇美拉");
        monster.setDesc("奇美拉（Chimera）希腊神话中狮头， 羊身， 蛇尾的吐火怪物。西方龙系的分支。");
        dlc.getMonsters().add(monster);
        dlc.getImage().add(readImage("pics/long/Chimera.png"));
    }
}
