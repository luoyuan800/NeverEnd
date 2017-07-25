package cn.luo.yuan.maze.skill;

import cn.luo.yuan.maze.model.Element;
import cn.luo.yuan.maze.model.HarmAble;
import cn.luo.yuan.maze.model.Hero;
import cn.luo.yuan.maze.model.Maze;
import cn.luo.yuan.maze.model.Race;
import cn.luo.yuan.maze.model.skill.AtkSkill;
import cn.luo.yuan.maze.model.skill.DefSkill;
import cn.luo.yuan.maze.model.skill.Skill;
import cn.luo.yuan.maze.model.skill.SkillAbleObject;
import cn.luo.yuan.maze.model.skill.SkillParameter;
import cn.luo.yuan.maze.model.skill.elementalist.ElementBomb;
import cn.luo.yuan.maze.model.skill.elementalist.ElementDefend;
import cn.luo.yuan.maze.model.skill.elementalist.Elementalist;
import cn.luo.yuan.maze.model.skill.evil.EvilTalent;
import cn.luo.yuan.maze.model.skill.evil.Reinforce;
import cn.luo.yuan.maze.model.skill.evil.Stealth;
import cn.luo.yuan.maze.model.skill.hero.Dodge;
import cn.luo.yuan.maze.model.skill.hero.FightBack;
import cn.luo.yuan.maze.model.skill.hero.HeroHit;
import cn.luo.yuan.maze.model.skill.result.DoNoThingResult;
import cn.luo.yuan.maze.model.skill.result.HarmResult;
import cn.luo.yuan.maze.model.skill.result.HasMessageResult;
import cn.luo.yuan.maze.model.skill.result.SkillResult;
import cn.luo.yuan.maze.persistence.DataManagerInterface;
import cn.luo.yuan.maze.service.BattleMessage;
import cn.luo.yuan.maze.service.MockBattleMessage;
import cn.luo.yuan.maze.service.MockGameContext;
import cn.luo.yuan.maze.utils.Random;
import org.jetbrains.annotations.NotNull;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

import java.util.concurrent.ScheduledThreadPoolExecutor;

import static org.mockito.Mockito.mock;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertTrue;

/**
 * Created by luoyuan on 2017/7/23.
 */
public class TestSkill {
    BattleMessage battleMessage = new MockBattleMessage();
    MockGameContext context = new MockGameContext();
    @BeforeTest
    public void init(){
        context.dataManager = mock(DataManagerInterface.class);
        context.executor = mock(ScheduledThreadPoolExecutor.class);
    }
    @Test
    public void testEvilTalent() {
        Hero hero = buildHero("QA");
        hero.setHp(hero.getHp() - 1);
        long hp = hero.getHp();
        Hero monster = buildHero("QA_2");
        EvilTalent evilTalent = new EvilTalent();
        evilTalent.setEnable(true);
        SkillResult hrm = testSkillInvoke(evilTalent, monster, hero);
        printlnMessage(hrm);
        assertTrue(hp < hero.getHp());
    }

    @Test
    public void testFightBack() {
        Hero hero = buildHero("QA");
        hero.setHp(hero.getHp() - 1);
        Hero monster = buildHero("QA_2");
        monster.setHp(monster.getHp() - 1);
        long hp = hero.getHp();
        FightBack skill = new FightBack();
        skill.setEnable(true);
        SkillResult hrm = testSkillInvoke(skill, monster, hero);
        printlnMessage(hrm);
        assertTrue(hp > hero.getHp());
        assertTrue(hrm instanceof HarmResult);
        assertTrue(((HarmResult)hrm).isBack());
        assertTrue(((HarmResult)hrm).getHarm() > 0);
    }

    @Test
    public void testDodge() {
        Hero hero = buildHero("QA");
        Hero monster = buildHero("QA_2");
        long hp = hero.getHp();
        Dodge skill = new Dodge();
        skill.setEnable(true);
        SkillResult hrm = testSkillInvoke(skill, monster, hero);
        printlnMessage(hrm);
        assertTrue(hp == hero.getHp());
    }

    @Test
    public void testHeroHit() {
        Hero hero = buildHero("QA");
        Hero monster = buildHero("QA_2");
        long hp = monster.getHp();
        HeroHit skill = new HeroHit();
        skill.setEnable(true);
        SkillResult hrm = testSkillInvoke(skill, hero, monster);
        printlnMessage(hrm);
        assertTrue(((HarmResult)hrm).getHarm() > 0);
        assertFalse(((HarmResult)hrm).isBack());
    }

    @Test
    public void testReinforce() {
        Hero hero = buildHero("QA");
        Hero monster = buildHero("QA_2");
        long hp = hero.getCurrentHp();
        Reinforce skill = new Reinforce();
        skill.setEnable(true);
        SkillResult hrm = testSkillInvoke(skill, hero, monster);
        printlnMessage(hrm);
        assertFalse(hero.getEffects().isEmpty());
        assertTrue(hero.getCurrentHp() > hp);
    }

    @Test
    public void testStealth() {
        Hero hero = buildHero("QA");
        Maze maze = new Maze();
        float mr = maze.getMeetRate();
        context.maze = maze;
        Stealth skill = new Stealth();
        SkillParameter parameter = getSkillParameter(hero);
        skill.enable(parameter);
        assertTrue(maze.getMeetRate() < mr);
    }

    @Test
    public void testElementalist() {
        Hero hero = buildHero("QA");
        float mr = hero.getElementRate();
        context.hero = hero;
        Elementalist skill = new Elementalist();
        SkillParameter parameter = getSkillParameter(hero);
        skill.enable(parameter);
        assertTrue(hero.getElementRate() > mr);
        skill.disable(parameter);
        assertTrue(hero.getElementRate() == mr);
    }

    @Test
    public void testElementBomb() {
        Hero hero = buildHero("QA");
        Hero monster = buildHero("QA_2");
        context.hero = hero;
        ElementBomb skill = new ElementBomb();
        SkillParameter parameter = getSkillParameter(hero,hero,monster, context.random,10);
        skill.enable(parameter);
        hero.setElement(Element.FIRE.getRestriction());
        monster.setElement(Element.FIRE);
        SkillResult rs = testSkillInvoke(skill,hero,monster);
        printlnMessage(rs);
        assertTrue(rs instanceof HarmResult);
        assertTrue(((HarmResult)rs).getHarm() >= monster.getCurrentHp());
    }
    @Test
    public void testElementDefend() {
        Hero hero = buildHero("QA");
        Hero monster = buildHero("QA_2");
        context.hero = hero;
        ElementDefend skill = new ElementDefend();
        SkillParameter parameter = getSkillParameter(hero,monster,hero, context.random,10);
        skill.enable(parameter);
        hero.setElement(Element.FIRE.getRestriction());
        monster.setElement(Element.FIRE);
        SkillResult rs = testSkillInvoke(skill,monster,hero);
        printlnMessage(rs);
        assertTrue(rs instanceof HarmResult);
        assertTrue(((HarmResult)rs).getHarm() == 0);

        hero.setElement(Element.FIRE);
        monster.setElement(Element.FIRE);
        rs = testSkillInvoke(skill,monster,hero);
        printlnMessage(rs);
        assertTrue(rs instanceof HarmResult);
        assertTrue(((HarmResult)rs).getHarm() > 0);
    }

    @NotNull
    private SkillParameter getSkillParameter(Hero hero) {
        SkillParameter parameter = new SkillParameter(hero);
        parameter.set(SkillParameter.CONTEXT, context);
        return parameter;
    }


    @NotNull
    private Hero buildHero(String name) {
        Hero hero = new Hero();
        hero.setName(name);
        hero.setAtk(100);
        hero.setDef(10);
        hero.setMaxHp(100);
        hero.setHp(hero.getMaxHp() - 1);
        hero.setRace(Race.Elyosr.ordinal());
        hero.setElement(Element.NONE);
        return hero;
    }

    private void printlnMessage(SkillResult hrm) {
        if (hrm instanceof HasMessageResult) {
            for (String s : hrm.getMessages()) {
                System.out.println(s);
            }
        }
    }

    private SkillResult testSkillInvoke(Skill skill, Hero atker, Hero defender) {
        if (skill instanceof AtkSkill) {
            return skill.invoke(getSkillParameter(atker, atker, defender, new Random(System.currentTimeMillis()), 10));
        } else if (skill instanceof DefSkill) {
            return skill.invoke(getSkillParameter(defender,atker,defender, new Random(System.currentTimeMillis()),10));
        } else {
            return new DoNoThingResult();
        }
    }

    private SkillParameter getSkillParameter(SkillAbleObject owner, HarmAble atker, HarmAble defender, Random random, long level) {
        SkillParameter atkPara = new SkillParameter(owner);
        atkPara.set(SkillParameter.ATKER, atker);
        atkPara.set(SkillParameter.RANDOM, context.getRandom());
        atkPara.set(SkillParameter.TARGET, atker == owner ? defender : atker);
        atkPara.set(SkillParameter.DEFENDER, defender);
        atkPara.set(SkillParameter.MINHARM, level);
        atkPara.set(SkillParameter.MESSAGE, battleMessage);
        atkPara.set(SkillParameter.CONTEXT, context);
        return atkPara;
    }
}
