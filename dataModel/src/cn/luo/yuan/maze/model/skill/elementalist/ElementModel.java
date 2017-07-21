package cn.luo.yuan.maze.model.skill.elementalist;

import cn.luo.yuan.maze.model.Data;
import cn.luo.yuan.maze.model.Hero;
import cn.luo.yuan.maze.model.skill.*;
import cn.luo.yuan.maze.service.InfoControlInterface;

/**
 * Created by luoyuan on 2017/7/16.
 */
public class ElementModel extends SkillModel {
    public ElementModel(Skill skill){
        super(skill);
    }
    public boolean canEnable(SkillParameter parameter){
        InfoControlInterface context = parameter.get("context");
        if(isSkillEnable("HeroHit", context) || isSkillEnable("EvilTalent", context)){
            return false;
        }else {
            return (skill instanceof Elementalist || isSkillEnable("Elementalist", context)) && isEnablePointEnough(parameter);
        }
    }

    public boolean canMount(SkillParameter parameter) {
        InfoControlInterface context = parameter.get(SkillParameter.CONTEXT);
        return !isSkillEnable("HeroHit",context)&& !isSkillEnable("EvilTalent", context);
    }

    public void upgrade(SkillParameter parameter){
        SkillAbleObject hero = parameter.getOwner();
        if (hero instanceof Hero) {
            ((Hero) hero).setPoint(((Hero) hero).getPoint() - ((UpgradeAble)skill).getLevel() * Data.SKILL_ENABLE_COST);
        }
    }

    public boolean canUpgrade(SkillParameter parameter){
        return isUpgradePointEnough(parameter);
    }



}
