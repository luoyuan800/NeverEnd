package cn.luo.yuan.maze.model.skill;

import cn.luo.yuan.maze.model.Data;
import cn.luo.yuan.maze.model.Hero;
import cn.luo.yuan.maze.service.InfoControlInterface;

/**
 * Created by luoyuan on 2017/7/16.
 */
public class SkillModel {
    protected Skill skill;

    public SkillModel(Skill skill) {
        this.skill = skill;
    }

    public boolean isSkillEnable(String name, InfoControlInterface context){
        Skill skill = SkillFactory.geSkillByName("EvilTalent",context.getDataManager());
        return skill !=null && skill.isEnable();
    }

    public boolean isEnablePointEnough(SkillParameter parameter) {
        return parameter.getOwner() instanceof Hero && ((Hero) parameter.getOwner()).getPoint() > Data.SKILL_ENABLE_COST;
    }

    public String getSkillName() {
        return "<font color='" + (skill.isEnable() ? "#8fda5f0a" : "#453e22") + "'>" + skill.getName() + "</font>" + (skill instanceof MountAble ? (((MountAble) skill).isMounted() ? "√" : "") : "");
    }

    public boolean isUpgradePointEnough(SkillParameter parameter) {
        return ((Hero) parameter.getOwner()).getPoint() > (skill instanceof UpgradeAble ? ((UpgradeAble) skill).getLevel() : 1)* Data.SKILL_ENABLE_COST;
    }
}
