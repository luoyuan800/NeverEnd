package cn.luo.yuan.maze.service;

import cn.luo.yuan.maze.model.Pet;
import cn.luo.yuan.maze.utils.StringUtils;

/**
 * Created by gluo on 5/16/2017.
 */
public class TestPetMonsterHelper {
    public static void main(String... args) {
        new TestPetMonsterHelper().testUpgrade();
    }

    public void testUpgrade() {
        GameContext control = new MockInfoControl();
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

    public void assertLarger(Object o1, Object o2) {
        System.out.println(o1 + " should larger than " + o2);
    }

    public void assertEquals(Object o1, Object o2) {
        System.out.println(o1 + " should equals to " + o2);
    }
}
