package cn.luo.yuan.maze.service;

import cn.luo.yuan.maze.model.Data;
import cn.luo.yuan.maze.model.Hero;
import cn.luo.yuan.maze.model.skill.*;
import cn.luo.yuan.maze.utils.Random;

import java.util.Arrays;

import static cn.luo.yuan.maze.service.EffectHandler.*;

/**
 * Created by gluo on 4/27/2017.
 */
public class SkillHelper {
    public static int mountSkillIndex(Hero hero) {
        int index = -1;

        for (int i = 0; i < hero.getSkills().length; i++) {
            if (hero.getSkills()[i] == null || hero.getSkills()[i] instanceof EmptySkill) {
                index = i;
                break;
            }
        }
        return index;
    }

    public static boolean mountSkill(Skill skill, Hero hero) {
        if(skill instanceof MountAble) {
            int index = mountSkillIndex(hero);
            if (index >= 0 && index < hero.getSkills().length) {
                return mountSkill(skill, hero, index);
            }
        }
        return false;
    }

    public static boolean mountSkill(Skill skill, Hero hero, int index) {
        Skill mountedSkill = hero.getSkills()[index];
        if (!(mountedSkill instanceof EmptySkill)) {
            if (mountedSkill instanceof MountAble) {
                unMountSkill((MountAble) mountedSkill, hero);
            }
        }
        hero.getSkills()[index] = skill;
        ((MountAble) skill).mount();
        return true;
    }

    public static void detectSkillCount(Hero hero){
        switch ((int)hero.getReincarnate()){
            case 2:
                hero.setSkills(Arrays.copyOf(hero.getSkills(),hero.getSkills().length + 1));
                break;
            case 4:
                hero.setSkills(Arrays.copyOf(hero.getSkills(),hero.getSkills().length + 1));
                break;
            case 8:
                hero.setSkills(Arrays.copyOf(hero.getSkills(),hero.getSkills().length + 1));
                break;
        }
    }

    public static void unMountSkill(MountAble skill, Hero hero) {
        if(skill!=null) {
            skill.unMount();
        }
        for (int i = 0; i < hero.getSkills().length; i++) {
            if (hero.getSkills()[i] == skill) {
                hero.getSkills()[i] = EmptySkill.EMPTY_SKILL;
            }
        }
    }

    public static long getSkillBaseHarm(Hero hero, Random random) {
        return random.nextLong(getEffectAdditionLongValue(ATK, hero.getEffects(), hero) + getEffectAdditionLongValue(STR, hero.getEffects(), hero) * hero.getAtkGrow()) + hero.getAtk();
    }

    public static void enableSkill(Skill skill, InfoControlInterface context, SkillParameter parameter) {
        if (parameter == null) {
            parameter = new SkillParameter(context.getHero());
            parameter.set(SkillParameter.CONTEXT, context);
        }
        skill.enable(parameter);
        context.getHero().setPoint(context.getHero().getPoint() - Data.SKILL_ENABLE_COST);
        context.getDataManager().saveSkill(skill);
    }

    public static boolean upgradeSkill(UpgradeAble skill, SkillParameter parameter, InfoControlInterface context) {
        if(skill.upgrade(parameter)){
            if(parameter.getOwner() instanceof Hero){
                ((Hero) parameter.getOwner()).setPoint(((Hero) parameter.getOwner()).getPoint() - skill.getLevel() * Data.SKILL_ENABLE_COST);
            }
            return true;
        }else{
            return false;
        }
    }
}
