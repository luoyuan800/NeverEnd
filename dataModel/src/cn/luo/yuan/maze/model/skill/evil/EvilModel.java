package cn.luo.yuan.maze.model.skill.evil;

import cn.luo.yuan.maze.model.skill.Skill;
import cn.luo.yuan.maze.model.skill.SkillFactory;
import cn.luo.yuan.maze.model.skill.SkillModel;
import cn.luo.yuan.maze.model.skill.SkillParameter;
import cn.luo.yuan.maze.service.InfoControlInterface;

/**
 * Created by luoyuan on 2017/7/16.
 */
public class EvilModel extends SkillModel {
    public EvilModel(Skill skill){
        super(skill);
    }
    public boolean canEnable(SkillParameter parameter){
        InfoControlInterface context = parameter.get("context");
        if(isSkillEnable("HeroHit", context)){
            return false;
        }else {
            return isEnablePointEnough(parameter);
        }
    }

    public boolean canMount(SkillParameter parameter) {
        InfoControlInterface context = parameter.get(SkillParameter.CONTEXT);
        return !isSkillEnable("HeroHit",context);
    }

}
