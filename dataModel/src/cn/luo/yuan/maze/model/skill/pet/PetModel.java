package cn.luo.yuan.maze.model.skill.pet;

import cn.luo.yuan.maze.model.Data;
import cn.luo.yuan.maze.model.Hero;
import cn.luo.yuan.maze.model.NeverEndConfig;
import cn.luo.yuan.maze.model.skill.*;
import cn.luo.yuan.maze.model.skill.evil.EvilTalent;
import cn.luo.yuan.maze.service.InfoControlInterface;
import cn.luo.yuan.maze.utils.Field;

/**
 * Created by luoyuan on 2017/7/16.
 */
public class PetModel extends SkillModel {
    private static final long serialVersionUID = Field.SERVER_VERSION;
    public PetModel(Skill skill){
        super(skill);
    }
    public boolean canEnable(SkillParameter parameter){
        InfoControlInterface context = parameter.get("context");
        NeverEndConfig config = context.getDataManager().loadConfig();
        return (config!=null && config.isPetGift()) && (skill instanceof PetMaster || isSkillEnable("PetMaster", context)) && isEnablePointEnough(parameter);
    }

    public boolean canMount(SkillParameter parameter) {
        return skill.isEnable();
    }


}
