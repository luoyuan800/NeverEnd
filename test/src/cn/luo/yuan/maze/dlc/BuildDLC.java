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
import cn.luo.yuan.maze.model.goods.types.EmptyAccessory;
import cn.luo.yuan.maze.model.names.FirstName;
import cn.luo.yuan.maze.model.names.SecondName;
import cn.luo.yuan.maze.model.skill.Skill;
import cn.luo.yuan.maze.model.skill.race.Alayer;
import cn.luo.yuan.maze.model.skill.race.Chaos;
import cn.luo.yuan.maze.model.skill.race.Decide;
import cn.luo.yuan.maze.model.skill.race.Exorcism;
import cn.luo.yuan.maze.model.skill.race.Masimm;
import cn.luo.yuan.maze.model.skill.race.Painkiller;
import cn.luo.yuan.maze.serialize.ObjectTable;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.UUID;

/**
 * Copyright @Luo
 * Created by Gavin Luo on 8/14/2017.
 */
public class BuildDLC {
    public static File root = new File("dlc");
    public static void main(String... args) throws IOException {
        ObjectTable<DLC> dlcTable = new ObjectTable<DLC>(DLC.class, new File("dlc"));
        /*buildAngel(dlcTable);
        buildJiuwei(dlcTable);
        buildEmprtyAccessory(dlcTable);
        buildSkillDlc(new Alayer(), dlcTable);
        buildSkillDlc(new Chaos(), dlcTable);
        buildSkillDlc(new Decide(), dlcTable);
        buildSkillDlc(new Exorcism(), dlcTable);
        buildSkillDlc(new Masimm(), dlcTable);
        buildSkillDlc(new Painkiller(), dlcTable);*/
        buildEveoo(dlcTable);
        //buildNPC(dlcTable);
    }

    public static void buildNPC(ObjectTable<DLC> dlcObjectTable) throws IOException{
        chuyin(dlcObjectTable);
        yuanshuxion(dlcObjectTable);
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
        npcdlc.setDebrisCost(1);
        npcdlc.setDesc("你想邂逅小姐姐吗？那就兑换这个攻击高达100W的小姐姐陪你玩哦！兑换后有一定机率在迷宫中遇见小姐姐。");
        npcdlc.setNpc(record);
        npcdlc.setTitle(hero.getName());
        dlcObjectTable.save(npcdlc);
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
        npcdlc.setDebrisCost(1);
        npcdlc.setDesc("这是迷失的朋友！我在这迷宫中晃荡多年，对各种怪异早已经见怪不怪了。或许你这个仿佛没有尽头的迷宫产生了绝望，但是外面的世界或许早已……<br>相信在这个世界里，你并不是孤独一人的。<br>继续往上爬吧，总有一天你会找到这个世界的真谛。<br>");
        npcdlc.setNpc(record);
        npcdlc.setTitle(hero.getName());
        dlcObjectTable.save(npcdlc);
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
        System.out.println(dlcTable.save(dlc));
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
        System.out.println(table.save(dlc));
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
        System.out.println(table.save(dlc));
    }

    public static void buildSkillDlc(Skill skill, ObjectTable<DLC> table) throws IOException {
        SkillDLC dlc = new SkillDLC();
        dlc.setSkill(skill);
        dlc.setDebrisCost(20);
        System.out.println(table.save(dlc));
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
        dlc.setDesc("解锁伊布12种进化形态，电、鬼、魂、毒、草、虫、月、绿、木、暗、冰、角。");
        dlc.setDebrisCost(200);
        System.out.println(table.save(dlc));
    }
}
