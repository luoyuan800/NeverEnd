package cn.luo.yuan.maze.service;

import cn.luo.yuan.maze.listener.BattleEndListener;
import cn.luo.yuan.maze.model.Element;
import cn.luo.yuan.maze.model.Group;
import cn.luo.yuan.maze.model.HarmAble;
import cn.luo.yuan.maze.model.Hero;
import cn.luo.yuan.maze.model.Monster;
import cn.luo.yuan.maze.model.Race;
import cn.luo.yuan.maze.model.names.FirstName;
import cn.luo.yuan.maze.model.names.SecondName;
import cn.luo.yuan.maze.model.skill.hero.HeroHit;
import cn.luo.yuan.maze.utils.Random;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.testng.annotations.Test;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyLong;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.testng.Assert.assertTrue;

/**
 * Created by gluo on 5/22/2017.
 */
public class TestBattleService {
    @Test
    public void testBattle(){
        Hero hero = new Hero();
        hero.setMaxHp(2000);
        hero.setHp(1000);
        hero.setAtk(10);
        hero.setDef(5);
        hero.setElement(Element.EARTH);
        Monster monster = new Monster();
        monster.setHp(2000);
        monster.setMaxHp(2000);
        monster.setAtk(10);
        monster.setDef(5);
        monster.setElement(Element.FIRE);
        Random random = new Random(System.currentTimeMillis());
        BattleMessage battleMessage = mock(BattleMessage.class);
        BattleService battleService = new BattleService(hero,monster,random, null);
        battleService.setBattleMessage(battleMessage);
        battleService.battle(1);
        verify(battleMessage,atLeastOnce()).harm(any(), any(), anyLong());
        assertTrue(hero.getHp() <= 0 || monster.getHp() <= 0, "End battle!");
    }

    @Test
    public void testBattleMaybeFailed(){
        Hero hero = new Hero();
        hero.setMaxHp(100);
        hero.setHp(100);
        hero.setAtk(10);
        hero.setDef(5);
        hero.setElement(Element.WOOD);
        Monster monster = new Monster();
        monster.setHp(2000);
        monster.setMaxHp(2000);
        monster.setAtk(60);
        monster.setDef(10);
        monster.setElement(Element.FIRE);
        Random random = new Random(System.currentTimeMillis());
        BattleMessage battleMessage = mock(BattleMessage.class);
        BattleService battleService = new BattleService(hero,monster,random, null);
        battleService.setBattleMessage(battleMessage);
        battleService.battle(1);
        verify(battleMessage,atLeastOnce()).harm(any(), any(), anyLong());
        assertTrue(hero.getHp() <= 0, "End battle!");
    }

    @Test
    public void testSkillRelease(){
        Hero hero = new Hero();
        HeroHit heroHit = new HeroHit();
        heroHit.setEnable(true);
        heroHit.mount();
        heroHit.setRate(25.5f);
        hero.getSkills()[0] = heroHit;
        hero.setMaxHp(100000);
        hero.setHp(100000);
        hero.setAtk(10);
        hero.setDef(5);
        hero.setElement(Element.WOOD);
        Monster monster = new Monster();
        monster.setHp(20000);
        monster.setMaxHp(20000);
        monster.setAtk(60);
        monster.setDef(10);
        monster.setElement(Element.FIRE);
        Random random = new Random(System.currentTimeMillis());
        BattleMessage battleMessage = mock(BattleMessage.class);
        BattleService battleService = new BattleService(hero,monster,random, null);
        battleService.setBattleMessage(battleMessage);
        battleService.battle(1);
        //verify(battleMessage,atLeastOnce()).harm(any(), any(), anyLong());
        verify(battleMessage, atLeastOnce()).releaseSkill(any(), any());
    }

    @Test
    public void testBattleMaybeWin(){
        Hero hero = new Hero();
        hero.setMaxHp(1000);
        hero.setHp(1000);
        hero.setAtk(10);
        hero.setDef(5);
        hero.setElement(Element.WOOD);
        Monster monster = new Monster();
        monster.setHp(200);
        monster.setMaxHp(200);
        monster.setAtk(6);
        monster.setDef(1);
        monster.setElement(Element.FIRE);
        Random random = new Random(System.currentTimeMillis());
        BattleMessage battleMessage = mock(BattleMessage.class);
        BattleService battleService = new BattleService(hero,monster,random, null);
        battleService.setBattleMessage(battleMessage);
        battleService.battle(1);
        verify(battleMessage,atLeastOnce()).harm(any(), any(), anyLong());
        assertTrue(monster.getHp() <= 0, "End battle!");
    }

    @Test
    public void testGroupBattle(){
        Hero hero = new Hero();
        hero.setMaxHp(1000);
        hero.setHp(1000);
        hero.setAtk(10);
        hero.setDef(5);
        hero.setRace(Race.Elyosr.ordinal());
        hero.setElement(Element.WOOD);
        Hero hero1 = new Hero();
        hero1.setMaxHp(1000);
        hero1.setHp(1000);
        hero1.setAtk(10);
        hero1.setDef(5);
        hero1.setRace(Race.Eviler.ordinal());
        hero1.setElement(Element.WOOD);
        Group group = new Group();
        group.getHeroes().add(hero);
        group.getHeroes().add(hero1);
        Monster monster = new Monster();
        monster.setHp(200);
        monster.setMaxHp(200);
        monster.setAtk(6);
        monster.setDef(1);
        monster.setFirstName(FirstName.angry);
        monster.setSecondName(SecondName.blue);
        monster.setElement(Element.FIRE);
        monster.setRace(Race.Elyosr);
        monster.setElement(Element.FIRE);
        Random random = new Random(System.currentTimeMillis());
        BattleMessage battleMessage = mock(BattleMessage.class);
        BattleService battleService = new BattleService(hero1,monster,random, null);
        battleService.setBattleMessage(battleMessage);
        battleService.battle(1);
        verify(battleMessage,atLeastOnce()).harm(any(), any(), anyLong());
        //assertTrue(monster.getHp() <= 0, "End battle!");
    }

    @Test
    public void testBattleEndListener(){
        BattleEndListener endListener = new BattleEndListener() {
            @Override
            public void end(Hero battler, HarmAble target) {
                assertTrue(battler.getHp() <= 0 || target.getHp() <= 0, "Only 0 can end game");
            }

            @Override
            public String getKey() {
                return "key";
            }
        };
        ListenerService.registerListener(endListener);
        Hero hero = new Hero();
        hero.setMaxHp(100);
        hero.setHp(1000);
        hero.setAtk(10);
        hero.setDef(5);
        hero.setElement(Element.WOOD);
        Monster monster = new Monster();
        monster.setHp(200);
        monster.setMaxHp(200);
        monster.setAtk(6);
        monster.setDef(1);
        monster.setElement(Element.FIRE);
        Random random = new Random(System.currentTimeMillis());
        BattleMessage battleMessage = mock(BattleMessage.class);
        BattleService battleService = new BattleService(hero,monster,random, null);
        battleService.setBattleMessage(battleMessage);
        battleService.battle(1);
        verify(battleMessage,atLeastOnce()).harm(any(), any(), anyLong());
        assertTrue(monster.getHp() <= 0, "End battle!");
    }
}
