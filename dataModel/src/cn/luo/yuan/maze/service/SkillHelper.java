package cn.luo.yuan.maze.service;

import cn.luo.yuan.maze.model.Data;
import cn.luo.yuan.maze.model.Hero;
import cn.luo.yuan.maze.model.skill.EmptySkill;
import cn.luo.yuan.maze.model.skill.MountAble;
import cn.luo.yuan.maze.model.skill.Skill;
import cn.luo.yuan.maze.model.skill.SkillParameter;
import cn.luo.yuan.maze.utils.Random;

import static cn.luo.yuan.maze.service.EffectHandler.ATK;
import static cn.luo.yuan.maze.service.EffectHandler.STR;
import static cn.luo.yuan.maze.service.EffectHandler.getEffectAdditionLongValue;

/**
 * Created by gluo on 4/27/2017.
 */
public class SkillHelper {
    public static int mountSkillIndex(Hero hero){
        int index = -1;

        for(int i =0; i< hero.getSkills().length; i++){
            if(hero.getSkills()[i] instanceof EmptySkill){
                index = i;
                break;
            }
        }
        return index;
    }

    public static boolean mountSkill(Skill skill, Hero hero){
        int index = mountSkillIndex(hero);
        if(index >= 0){
            return mountSkill(skill, hero, index);
        }
        return false;
    }

    public static boolean mountSkill(Skill skill, Hero hero, int index){
        Skill mountedSkill = hero.getSkills()[index];
        if(!(mountedSkill instanceof EmptySkill)){
            if(mountedSkill instanceof MountAble){
                unMountSkill((MountAble) mountedSkill, hero);
            }
        }
        hero.getSkills()[index] = skill;
        ((MountAble)skill).mount();
        return true;
    }

    public static void unMountSkill(MountAble skill, Hero hero){
        skill.unMount();
        for(int i = 0; i< hero.getSkills().length; i++){
            if(hero.getSkills()[i] == skill){
                hero.getSkills()[i] = EmptySkill.EMPTY_SKILL;
            }
        }
    }

    public static long getSkillBaseHarm(Hero hero, Random random){
            return random.nextLong(getEffectAdditionLongValue(ATK, hero.getEffects()) + getEffectAdditionLongValue(STR, hero.getEffects()) * hero.getAtkGrow()) + hero.getAtk();
    }

    public static void enableSkill(Skill skill, Hero hero, SkillParameter parameter) {
        if(parameter == null){
            parameter = new SkillParameter(hero);
        }
        skill.enable(parameter);
            hero.setPoint(hero.getPoint() - Data.SKILL_ENABLE_COST);
    }
}
