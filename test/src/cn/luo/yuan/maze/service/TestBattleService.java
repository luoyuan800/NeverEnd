package cn.luo.yuan.maze.service;

import cn.luo.yuan.maze.model.Element;
import cn.luo.yuan.maze.model.Hero;
import cn.luo.yuan.maze.model.Monster;
import cn.luo.yuan.maze.model.NameObject;
import cn.luo.yuan.maze.utils.Random;
import org.testng.annotations.Test;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyLong;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
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
        BattleService battleService = new BattleService(hero,monster,random);
        battleService.setBattleMessage(battleMessage);
        battleService.battle();
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
        BattleService battleService = new BattleService(hero,monster,random);
        battleService.setBattleMessage(battleMessage);
        battleService.battle();
        verify(battleMessage,atLeastOnce()).harm(any(), any(), anyLong());
        assertTrue(hero.getHp() <= 0, "End battle!");
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
        BattleService battleService = new BattleService(hero,monster,random);
        battleService.setBattleMessage(battleMessage);
        battleService.battle();
        verify(battleMessage,atLeastOnce()).harm(any(), any(), anyLong());
        assertTrue(monster.getHp() <= 0, "End battle!");
    }
}
