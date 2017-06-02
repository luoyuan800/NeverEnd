package cn.luo.yuan.maze.service;

import cn.luo.yuan.maze.exception.MonsterToPetException;
import cn.luo.yuan.maze.model.Data;
import cn.luo.yuan.maze.model.Hero;
import cn.luo.yuan.maze.model.Monster;
import cn.luo.yuan.maze.model.Pet;
import cn.luo.yuan.maze.model.names.FirstName;
import cn.luo.yuan.maze.model.names.SecondName;
import cn.luo.yuan.maze.utils.Random;
import cn.luo.yuan.maze.utils.StringUtils;
import org.testng.annotations.Test;

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
    public void testMonsterToPet() throws MonsterToPetException {
        Monster monster = new Monster();
        monster.setHp(100);
        monster.setMaxHp(100);
        monster.setColor(Data.DARKGOLD_COLOR);
        monster.setAtk(100);
        monster.setDef(100);
        monster.setFirstName(FirstName.angry);
        monster.setSecondName(SecondName.blue);
        monster.setType("测试");
        monster.setIndex(1);
        PetMonsterHelper helper = PetMonsterHelper.instance;
        Hero hero = new Hero();
        hero.setName("test");
        hero.setId("11111");
        Pet p = helper.monsterToPet(monster, hero);
        assertEquals(p.getType(), monster.getType());
        assertEquals(p.getFirstName(), monster.getFirstName());
    }

    public void assertLarger(Object o1, Object o2) {
        System.out.println(o1 + " should larger than " + o2);
    }

    public void assertEquals(Object o1, Object o2) {
        System.out.println(o1 + " should equals to " + o2);
    }
}
