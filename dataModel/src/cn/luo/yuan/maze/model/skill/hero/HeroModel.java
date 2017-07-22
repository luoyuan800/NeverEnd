package cn.luo.yuan.maze.model.skill.hero;

import cn.luo.yuan.maze.model.Data;
import cn.luo.yuan.maze.model.Hero;
import cn.luo.yuan.maze.model.skill.*;
import cn.luo.yuan.maze.service.InfoControlInterface;
import cn.luo.yuan.maze.utils.Field;

/**
 * Created by luoyuan on 2017/7/16.
 */
public class HeroModel extends SkillModel {
    private static final long serialVersionUID = Field.SERVER_VERSION;
    public HeroModel(Skill skill){
        super(skill);
    }
    public boolean canEnable(SkillParameter parameter){
        InfoControlInterface context = parameter.get("context");
        if(isSkillEnable("EvilTalent", context)|| isSkillEnable("Elementalist", context)){
            return false;
        }else {
            if(!(skill instanceof HeroHit)){
                return isSkillEnable("HeroHit", context) && isEnablePointEnough(parameter);
            }else {
                return isEnablePointEnough(parameter);
            }
        }
    }

    public boolean canMount(SkillParameter parameter) {
        InfoControlInterface context = parameter.get(SkillParameter.CONTEXT);
        return !isSkillEnable("EvilTalent",context) && !isSkillEnable("Elementalist", context);
    }

}
