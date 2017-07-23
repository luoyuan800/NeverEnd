package cn.luo.yuan.maze.skill;

import cn.luo.yuan.maze.model.Element;
import cn.luo.yuan.maze.model.HarmAble;
import cn.luo.yuan.maze.model.Hero;
import cn.luo.yuan.maze.model.Race;
import cn.luo.yuan.maze.model.skill.*;
import cn.luo.yuan.maze.model.skill.evil.EvilTalent;
import cn.luo.yuan.maze.model.skill.hero.FightBack;
import cn.luo.yuan.maze.model.skill.result.DonothingResult;
import cn.luo.yuan.maze.model.skill.result.HasMessageResult;
import cn.luo.yuan.maze.model.skill.result.SkillResult;
import cn.luo.yuan.maze.utils.Random;
import org.jetbrains.annotations.NotNull;
import org.testng.annotations.Test;
import static org.testng.Assert.assertTrue;
/**
 * Created by luoyuan on 2017/7/23.
 */
public class TestSkill {
    @Test
    public void testEvilTalent(){
        Hero hero = buildHero("QA");
        hero.setHp(hero.getHp() -1);
        long hp = hero.getHp();
        Hero monster = buildHero("QA_2");
        EvilTalent evilTalent = new EvilTalent();
        evilTalent.setEnable(true);
        SkillResult hrm = testSkillInvoke(evilTalent, hero, monster);
        printlnMessage(hrm);
        assertTrue(hp < hero.getHp());
    }
    @Test
    public void testFightBack(){
        Hero hero = buildHero("QA");
        hero.setHp(hero.getHp() -1);
        Hero monster = buildHero("QA_2");
        monster.setHp(monster.getHp() -1);
        long hp = hero.getHp();
        FightBack skill = new FightBack();
        skill.setEnable(true);
        SkillResult hrm = testSkillInvoke(skill, hero, monster);
        printlnMessage(hrm);
        assertTrue(hp > hero.getHp());
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
        if(hrm instanceof HasMessageResult){
            for(String s : hrm.getMessages()){
                System.out.println(s);
            }
        }
    }

    private SkillResult testSkillInvoke(Skill skill, Hero hero, Hero monster) {
        if(skill instanceof AtkSkill || skill instanceof DefSkill) {
            return skill.invoke(getSkillParameter(hero, monster, new Random(System.currentTimeMillis()), 10));
        }else{
            return new DonothingResult();
        }
    }

    private SkillParameter getSkillParameter(SkillAbleObject atker, HarmAble target, Random random, long level) {
        SkillParameter atkPara = new SkillParameter(atker);
        atkPara.set(SkillParameter.RANDOM, random);
        atkPara.set(SkillParameter.TARGET, target);
        atkPara.set(SkillParameter.DEFENDER, target);
        atkPara.set(SkillParameter.MINHARM, level);
        atkPara.set(SkillParameter.ATKER, atker);
        /*atkPara.set(SkillParameter.MESSAGE, battleMessage);
        atkPara.set(SkillParameter.CONTEXT, runninfService.getContext());*/
        return atkPara;
    }
}
