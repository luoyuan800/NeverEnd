package cn.luo.yuan.maze.server;

import cn.luo.yuan.maze.model.Element;
import cn.luo.yuan.maze.model.Hero;
import cn.luo.yuan.maze.model.Maze;
import cn.luo.yuan.maze.model.Monster;
import cn.luo.yuan.maze.model.Pet;
import cn.luo.yuan.maze.model.Race;
import cn.luo.yuan.maze.model.ServerData;
import cn.luo.yuan.maze.model.names.FirstName;
import cn.luo.yuan.maze.model.names.SecondName;
import cn.luo.yuan.maze.server.persistence.HeroTable;
import org.testng.annotations.Test;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Map;
import java.util.UUID;

import static org.mockito.Matchers.anyLong;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.when;

/**
 * Created by gluo on 5/26/2017.
 */
public class TestMainProcess {
    @Test
    public void testBuildRange() throws IOException, ClassNotFoundException {
        MainProcess process = new MainProcess("data");
        Hero hero = new Hero();
        hero.setName("骑士☆某鸟");
        hero.setMaxHp(2000);
        hero.setHp(1000);
        hero.setAtk(10000);
        hero.setDef(5);
        hero.setElement(Element.EARTH);
        hero.setRace(Race.Elyosr.ordinal());
        hero.setId("test");
        Maze maze = new Maze();
        Pet pet = new Pet();
        pet.setType("test");
        pet.setFirstName(FirstName.angry);
        pet.setSecondName(SecondName.blue);
        pet.setHp(100);
        pet.setMaxHp(100);
        pet.setAtk(100);
        pet.setDef(100);
        pet.setIndex(100);
        pet.setId(hero.getId());
        pet.setOwnerName("100");
        pet.setOwnerId("101");
        hero.getPets().add(pet);
        ServerData upload = new ServerData();
        upload.setHero(hero);
        upload.setMaze(maze);
        upload.setPets(new ArrayList<>(hero.getPets()));
        try {
            process.submitHero(upload);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        System.out.println(process.buildHeroRange());
    }

}
