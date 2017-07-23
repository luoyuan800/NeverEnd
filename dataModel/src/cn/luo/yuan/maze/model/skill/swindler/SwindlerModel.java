package cn.luo.yuan.maze.model.skill.swindler;

import cn.luo.yuan.maze.model.Data;
import cn.luo.yuan.maze.model.Hero;
import cn.luo.yuan.maze.model.skill.*;
import cn.luo.yuan.maze.model.skill.evil.EvilTalent;
import cn.luo.yuan.maze.service.InfoControlInterface;
import cn.luo.yuan.maze.utils.Field;

/**
 * Created by luoyuan on 2017/7/16.
 */
public class SwindlerModel extends SkillModel {
    private static final long serialVersionUID = Field.SERVER_VERSION;
    public SwindlerModel(Skill skill){
        super(skill);
    }
    public boolean canEnable(SkillParameter parameter){
        InfoControlInterface context = parameter.get("context");
        return (skill instanceof EvilTalent || isSkillEnable("Swindler", context)) && isEnablePointEnough(parameter);
    }

    public boolean canMount(SkillParameter parameter) {
        return skill.isEnable();
    }

    public void upgrade(SkillParameter parameter){
        SkillAbleObject hero = parameter.getOwner();
        if (hero instanceof Hero) {
            ((Hero) hero).setPoint(((Hero) hero).getPoint() - ((UpgradeAble)skill).getLevel() * Data.SKILL_ENABLE_COST);
        }
    }




}
