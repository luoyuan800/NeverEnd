package cn.luo.yuan.maze.service;

import cn.luo.yuan.maze.exception.MonsterToPetException;
import cn.luo.yuan.maze.model.Data;
import cn.luo.yuan.maze.model.Egg;
import cn.luo.yuan.maze.model.Element;
import cn.luo.yuan.maze.model.Hero;
import cn.luo.yuan.maze.model.Monster;
import cn.luo.yuan.maze.model.Pet;
import cn.luo.yuan.maze.model.Race;
import cn.luo.yuan.maze.model.names.FirstName;
import cn.luo.yuan.maze.model.names.SecondName;
import cn.luo.yuan.maze.utils.Random;
import cn.luo.yuan.maze.utils.StringUtils;
import dalvik.annotation.TestTargetClass;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Map;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;

/**
 * Created by gluo on 5/16/2017.
 */
public class TestPetMonsterHelper {

    @Test
    public void testUpgrade() {
        Pet major = new Pet();
        major.setLevel(0);
        major.setAtk(100);
        major.setDef(100);
        major.setMaxHp(100);
        Pet minor = new Pet();
        minor.setLevel(0);
        minor.setAtk(100);
        minor.setDef(100);
        minor.setMaxHp(100);
        long originLevel = major.getLevel();
        long originAtk = major.getAtk();
        long originDef = major.getDef();
        long originHp = major.getMaxHp();
        PetMonsterHelper helper = PetMonsterHelper.instance;
        helper.setRandom(new Random(System.currentTimeMillis()));
        float percent = 0;
        int count = 100000;
        for (int i = 0; i < count; i++) {
            if (helper.upgrade(major, minor)) {
                System.out.println("Upgrade success!");
                assertLarger(major.getMaxHp(), originHp);
                assertLarger(major.getAtk(), originAtk);
                assertLarger(major.getDef(), originDef);
                assertEquals(major.getLevel(), originLevel + 1);
                originLevel = major.getLevel();
                originAtk = major.getAtk();
                originDef = major.getDef();
                originHp = major.getMaxHp();
                percent ++;
            } else {
                System.out.println("upgrade failed!");
                assertEquals(major.getLevel(), originLevel);
            }
        }
        System.out.println("Percent: " + StringUtils.DecimalFormatRound(percent * 100 /count,2) + "%");
    }

    @Test
    public void testEgg() throws MonsterToPetException {
        PetMonsterHelper helper = PetMonsterHelper.instance;
        MockMonsterLoader loader = new MockMonsterLoader();
        helper.setMonsterLoader(loader);
        MockGameContext context = new MockGameContext();
        context.hero = new Hero();
        context.hero.setRace(Race.Elyosr.ordinal());
        helper.setRandom(context.getRandom());
        Monster m1 = new Pet();
        m1.setIndex(0);
        m1.setType("test");
        m1.setSex(0);
        m1.setRace(Race.Elyosr);
        m1.setElement(Element.FIRE.getReinforce());
        m1.setFirstName(context.getRandom().randomItem(FirstName.values()));
        m1.setSecondName(context.getRandom().randomItem(SecondName.values()));
        m1.setRank(1);
        Monster m2 = new Pet();
        m2.setIndex(1);
        m2.setType("test1");
        m2.setSex(1);
        m2.setRace(Race.Elyosr);
        m2.setElement(Element.FIRE);
        m1.setEggRate(200);
        m2.setEggRate(100);
        m2.setFirstName(context.getRandom().randomItem(FirstName.values()));
        m2.setSecondName(context.getRandom().randomItem(SecondName.values()));
        m2.setRank(5);
        loader.setMonsterList(new ArrayList<>(Arrays.<Monster>asList(m1, m2)));
        Pet p1 = helper.monsterToPet(m1, new Hero(), 100);
        p1.setId("1");
        Pet p2 = helper.monsterToPet(m2, new Hero(), 100);
        p2.setId("2");
        Egg egg = helper.buildEgg(p1, p2, context);
        if(egg!=null) {
            assertTrue(egg.step > 0);
            Assert.assertEquals(egg.getName(), "蛋");
        }
    }

    @Test
    public void testMonsterToPet() throws MonsterToPetException {
        Monster monster = new Monster();
        long level = 100;
        monster.setHp(100 + level * Data.MONSTER_HP_RISE_PRE_LEVEL);
        monster.setMaxHp(100 + level * Data.MONSTER_HP_RISE_PRE_LEVEL);
        monster.setColor(Data.DARKGOLD_COLOR);
        monster.setAtk(100 + level * Data.MONSTER_ATK_RISE_PRE_LEVEL);
        monster.setDef(100 + level * Data.MONSTER_DEF_RISE_PRE_LEVEL);
        monster.setFirstName(FirstName.angry);
        monster.setSecondName(SecondName.blue);
        monster.setType("测试");
        monster.setIndex(1);
        PetMonsterHelper helper = PetMonsterHelper.instance;
        helper.setRandom(new Random(System.currentTimeMillis()));
        Hero hero = new Hero();
        hero.setName("test");
        hero.setId("11111");
        Pet p = helper.monsterToPet(monster, hero, level);
        assertEquals(p.getType(), monster.getType());
        assertEquals(p.getFirstName(), monster.getFirstName());
        assertLarger(monster.getAtk(), p.getAtk());
        assertLarger(monster.getDef(), p.getDef());
        assertLarger(monster.getMaxHp(), p.getMaxHp());
    }

    public void assertLarger(Object o1, Object o2) {
        System.out.println(o1 + " should larger than " + o2);
    }

    public void assertEquals(Object o1, Object o2) {
        System.out.println(o1 + " should equals to " + o2);
    }

}
