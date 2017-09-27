package cn.luo.yuan.maze.server;

import cn.luo.yuan.maze.model.Element;
import cn.luo.yuan.maze.model.Hero;
import cn.luo.yuan.object.serializable.ObjectTable;
import org.testng.annotations.Test;

import java.io.File;
import java.io.IOException;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertNull;

/**
 * Created by gluo on 5/26/2017.
 */
public class TestObjectDB {
    private ObjectTable<Hero> heroObjectDB = new ObjectTable<>(Hero.class, new File("data"));

    @Test
    public void testSave() throws IOException, ClassNotFoundException {
        Hero hero = new Hero();
        hero.setMaxHp(2000);
        hero.setHp(1000);
        hero.setAtk(10);
        hero.setDef(5);
        hero.setElement(Element.EARTH);
        hero.setName("p1");
        heroObjectDB.save(hero);
        Hero hero1 = heroObjectDB.loadObject(hero.getId());
        assertNotNull(hero1.getName().equals(hero.getName()));
        assertNotNull(hero1.getMaxHp() == hero.getMaxHp());
    }

    @Test
    public void testLoadAll() throws IOException, ClassNotFoundException {
        Hero hero = new Hero();
        hero.setMaxHp(2000);
        hero.setHp(1000);
        hero.setAtk(10);
        hero.setDef(5);
        hero.setElement(Element.EARTH);
        hero.setName("p1");
        heroObjectDB.save(hero);
        hero.setName("p2");
        heroObjectDB.save(hero);
        hero.setName("p3");
        heroObjectDB.save(hero);
        assertEquals(heroObjectDB.loadAll().size(), 3);
    }
    @Test
    public void testDelete() throws IOException, ClassNotFoundException {
        Hero hero = new Hero();
        hero.setMaxHp(2000);
        hero.setHp(1000);
        hero.setAtk(10);
        hero.setDef(5);
        hero.setElement(Element.EARTH);
        hero.setName("p1");
        heroObjectDB.save(hero);
        assertNotNull(heroObjectDB.loadObject(hero.getId()));
        heroObjectDB.delete(hero.getId());
        assertNull(heroObjectDB.loadObject(hero.getId()));
    }
}
