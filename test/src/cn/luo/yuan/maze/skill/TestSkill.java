package cn.luo.yuan.maze.skill;

import cn.luo.yuan.maze.model.Element;
import cn.luo.yuan.maze.model.HarmAble;
import cn.luo.yuan.maze.model.Hero;
import cn.luo.yuan.maze.model.Race;
import cn.luo.yuan.maze.model.skill.SkillAbleObject;
import cn.luo.yuan.maze.model.skill.SkillParameter;
import cn.luo.yuan.maze.model.skill.evil.EvilTalent;
import cn.luo.yuan.maze.model.skill.result.HasMessageResult;
import cn.luo.yuan.maze.model.skill.result.SkillResult;
import cn.luo.yuan.maze.utils.Random;
import org.testng.annotations.Test;

/**
 * Created by luoyuan on 2017/7/23.
 */
public class TestSkill {
    @Test
    public void testSkill(){
        Hero hero = new Hero();
        hero.setName("QA");
        hero.setAtk(100);
        hero.setDef(10);
        hero.setMaxHp(100);
        hero.setHp(hero.getMaxHp());
        hero.setRace(Race.Elyosr.ordinal());
        hero.setElement(Element.NONE);
        Hero monster = new Hero();
        monster.setName("QA2");
        monster.setAtk(100);
        monster.setDef(10);
        monster.setMaxHp(100);
        monster.setHp(monster.getMaxHp());
        monster.setRace(Race.Elyosr.ordinal());
        monster.setElement(Element.NONE);
        EvilTalent evilTalent = new EvilTalent();
        evilTalent.setEnable(true);
        SkillResult hrm = evilTalent.invoke(getSkillParameter(hero, monster,new Random(System.currentTimeMillis()),10));
        if(hrm instanceof HasMessageResult){
            for(String s : hrm.getMessages()){
                System.out.println(s);
            }
        }
    }

    private SkillParameter getSkillParameter(SkillAbleObject atker, HarmAble target, Random random, long level) {
        SkillParameter atkPara = new SkillParameter(atker);
        atkPara.set(SkillParameter.RANDOM, random);
        atkPara.set(SkillParameter.TARGET, target);
        atkPara.set(SkillParameter.DEFENDER, target);
        atkPara.set(SkillParameter.MINHARM, level);
        /*atkPara.set(SkillParameter.MESSAGE, battleMessage);
        atkPara.set(SkillParameter.CONTEXT, runninfService.getContext());*/
        return atkPara;
    }
}
