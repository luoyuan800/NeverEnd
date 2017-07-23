package cn.luo.yuan.maze.model.skill.evil;

import cn.luo.yuan.maze.model.Data;
import cn.luo.yuan.maze.model.Hero;
import cn.luo.yuan.maze.model.skill.*;
import cn.luo.yuan.maze.service.InfoControlInterface;
import cn.luo.yuan.maze.utils.Field;

/**
 * Created by luoyuan on 2017/7/16.
 */
public class EvilModel extends SkillModel {
    private static final long serialVersionUID = Field.SERVER_VERSION;
    public EvilModel(Skill skill){
        super(skill);
    }
    public boolean canEnable(SkillParameter parameter){
        InfoControlInterface context = parameter.get("context");
        if(isSkillEnable("HeroHit", context) || isSkillEnable("Elementalist", context)){
            return false;
        }else {
            return (skill instanceof EvilTalent || isSkillEnable("EvilTalent", context)) && isEnablePointEnough(parameter);
        }
    }

    public boolean canMount(SkillParameter parameter) {
        InfoControlInterface context = parameter.get(SkillParameter.CONTEXT);
        return !isSkillEnable("HeroHit",context) && !isSkillEnable("Elementalist", context);
    }

    public void upgrade(SkillParameter parameter){
        SkillAbleObject hero = parameter.getOwner();
        if (hero instanceof Hero) {
            ((Hero) hero).setPoint(((Hero) hero).getPoint() - ((UpgradeAble)skill).getLevel() * Data.SKILL_ENABLE_COST);
        }
    }

    public boolean canUpgrade(SkillParameter parameter){
        return skill.isEnable() && isUpgradePointEnough(parameter);
    }




}
