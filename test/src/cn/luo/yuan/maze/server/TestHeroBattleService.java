package cn.luo.yuan.maze.server;

import cn.luo.yuan.maze.model.Hero;
import cn.luo.yuan.maze.server.persistence.HeroTable;
import org.testng.annotations.Test;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import static org.testng.Assert.assertNotNull;

/**
 * Created by gluo on 6/27/2017.
 */
public class TestHeroBattleService {
    @Test
    public void testBattle(){
        Map<String, HeroTable> cache = new HashMap<>();
        for(String name : new File("data/hero").list()){
            try {
                HeroTable table = new HeroTable(new File(new File(new File("data"), "hero"),name));
                cache.put(name, table);
                assertNotNull(table.getHero(name,0));
            } catch (IOException e) {
                e.printStackTrace();
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
        }
        HeroBattleService hbs = new HeroBattleService(cache);
        hbs.run();
    }
}
