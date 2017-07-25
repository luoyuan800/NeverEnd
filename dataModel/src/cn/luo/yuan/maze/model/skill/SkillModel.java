package cn.luo.yuan.maze.model.skill;

import cn.luo.yuan.maze.model.Data;
import cn.luo.yuan.maze.model.Hero;
import cn.luo.yuan.maze.persistence.DataManagerInterface;
import cn.luo.yuan.maze.service.InfoControlInterface;
import cn.luo.yuan.maze.utils.Field;

/**
 * Created by luoyuan on 2017/7/16.
 */
public class SkillModel {
    private static final long serialVersionUID = Field.SERVER_VERSION;
    protected Skill skill;

    public SkillModel(Skill skill) {
        this.skill = skill;
    }

    public boolean isSkillEnable(String name, InfoControlInterface context){
        DataManagerInterface dataManager = context.getDataManager();
        if(dataManager != null) {
            Skill skill = SkillFactory.geSkillByName(name, dataManager);
            return skill != null && skill.isEnable();
        }else{
            return false;
        }
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
