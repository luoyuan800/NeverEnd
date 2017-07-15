package cn.luo.yuan.maze.model.gift;

import cn.luo.yuan.maze.model.skill.MountAble;
import cn.luo.yuan.maze.model.skill.Skill;
import cn.luo.yuan.maze.service.InfoControlInterface;
import cn.luo.yuan.maze.service.SkillHelper;

import java.util.Arrays;

/**
 * Created by luoyuan on 2017/7/15.
 */
public class Warrior implements GiftHandler  {
    @Override
    public void handler(InfoControlInterface control) {
        control.getHero().setAtkGrow(control.getHero().getAtkGrow() * 2);
        control.getHero().setDefGrow(control.getHero().getDefGrow() * 2);
        control.getHero().setHpGrow(control.getHero().getHpGrow() * 2);
        Skill[] skills = control.getHero().getSkills();
        if(skills.length > 3){
            SkillHelper.unMountSkill((MountAble) skills[3], control.getHero());
            if(skills.length > 4){
                SkillHelper.unMountSkill((MountAble) skills[4], control.getHero());
            }
            if(skills.length > 5){
                SkillHelper.unMountSkill((MountAble) skills[5], control.getHero());
            }
        }
        control.getHero().setSkills(Arrays.copyOf(control.getHero().getSkills(), 3));
    }

    @Override
    public void unHandler(InfoControlInterface control) {
        control.getHero().setAtkGrow(control.getHero().getAtkGrow() / 2);
        control.getHero().setDefGrow(control.getHero().getDefGrow() / 2);
        control.getHero().setHpGrow(control.getHero().getHpGrow() / 2);
        SkillHelper.detectSkillCount(control.getHero());
    }
}
