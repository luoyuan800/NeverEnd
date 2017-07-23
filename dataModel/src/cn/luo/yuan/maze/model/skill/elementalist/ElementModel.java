package cn.luo.yuan.maze.model.skill.elementalist;

import cn.luo.yuan.maze.model.Data;
import cn.luo.yuan.maze.model.Hero;
import cn.luo.yuan.maze.model.skill.*;
import cn.luo.yuan.maze.service.InfoControlInterface;
import cn.luo.yuan.maze.utils.Field;

/**
 * Created by luoyuan on 2017/7/16.
 */
public class ElementModel extends SkillModel {
        private static final long serialVersionUID = Field.SERVER_VERSION;
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

    public boolean canUpgrade(SkillParameter parameter){
        return skill.isEnable() && isUpgradePointEnough(parameter);
    }



}
