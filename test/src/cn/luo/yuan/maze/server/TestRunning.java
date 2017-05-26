package cn.luo.yuan.maze.server;

import cn.luo.yuan.maze.model.Element;
import cn.luo.yuan.maze.model.Hero;
import cn.luo.yuan.maze.model.Maze;
import cn.luo.yuan.maze.model.Monster;
import cn.luo.yuan.maze.model.Race;
import cn.luo.yuan.maze.model.names.FirstName;
import cn.luo.yuan.maze.model.names.SecondName;
import org.testng.annotations.Test;

import java.util.UUID;

import static org.mockito.Matchers.anyLong;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.when;

/**
 * Created by gluo on 5/26/2017.
 */
public class TestRunning {
    @Test
    public void testRunning() {
        cn.luo.yuan.maze.server.MonsterLoader loader = mock(cn.luo.yuan.maze.server.MonsterLoader.class);
        RunningKt.setMonsterLoader(loader);
        Monster monster = new Monster();
        monster.setElement(Element.FIRE);
        monster.setAtk(100);
        monster.setDef(100);
        monster.setRace(Race.Elyosr);
        monster.setFirstName(FirstName.angry);
        monster.setSex(1);
        monster.setSecondName(SecondName.blue);
        monster.setType("Test");
        when(loader.randomMonster(anyLong())).thenReturn(monster);
        Hero hero = new Hero();
        hero.setMaxHp(2000);
        hero.setHp(1000);
        hero.setAtk(10);
        hero.setDef(5);
        hero.setElement(Element.EARTH);
        hero.setName("p1");
        hero.setId(UUID.randomUUID().toString());
        Maze maze = new Maze();
        RunningKt.getHeroTable().saveHero(hero);
        RunningKt.getHeroTable().saveMaze(maze, hero.getId());
        hero.setName("p2");
        hero.setId(UUID.randomUUID().toString());
        RunningKt.getHeroTable().saveHero(hero);
        RunningKt.getHeroTable().saveMaze(maze, hero.getId());
        hero.setName("p3");
        hero.setId(UUID.randomUUID().toString());
        RunningKt.getHeroTable().saveHero(hero);
        RunningKt.getHeroTable().saveMaze(maze, hero.getId());
       /* new Thread(() -> {
            try {
                Thread.sleep(20000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            RunningKt.setRunning(false);
        }).start();*/
        RunningKt.main(new String[1]);

    }

}
