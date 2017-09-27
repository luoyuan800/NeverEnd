package cn.luo.yuan.maze.service;

import cn.luo.yuan.maze.client.service.RandomEventService;
import cn.luo.yuan.maze.model.Element;
import cn.luo.yuan.maze.model.Hero;
import cn.luo.yuan.maze.model.Maze;
import cn.luo.yuan.maze.model.gift.Gift;
import cn.luo.yuan.utils.Random;
import org.testng.annotations.Test;

import java.util.concurrent.ScheduledExecutorService;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;

/**
 * Created by gluo on 5/25/2017.
 */
public class TestRandomEventService {
    @Test
    public void testRandom(){
        Hero hero = new Hero();
        hero.setMaxHp(2000);
        hero.setHp(1000);
        hero.setAtk(10);
        hero.setDef(5);
        hero.setElement(Element.EARTH);
        hero.setAgi(100000);
        hero.setStr(100000);
        hero.setGift(Gift.HeroHeart);
        Maze maze = new Maze();
        maze.setLevel(1);
        maze.setMaxLevel(10);
        MockGameContext gameContext = new MockGameContext();
        gameContext.hero = hero;
        gameContext.executor = mock(ScheduledExecutorService.class);
        Random random = gameContext.random;
        RandomEventService service = spy(new RandomEventService(gameContext));
        int i = 100;
        while (i-- > 0){
            service.random();
        }
    }
}
