package cn.luo.yuan.maze.server;

import cn.luo.yuan.maze.model.Hero;
import cn.luo.yuan.maze.model.ServerRecord;
import cn.luo.yuan.maze.server.persistence.HeroTable;
import org.testng.annotations.Test;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertNotSame;

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
        MainProcess process = new MainProcess();
        HeroBattleService hbs = new HeroBattleService(cache, process.groups, process);
        hbs.run();
        for(Map.Entry<String, HeroTable> entry : cache.entrySet()){
            ServerRecord r = entry.getValue().getRecord(entry.getKey());
            if(r.getData()!=null){
                System.out.println(r.getMessages());
            }
        }
    }
}
