package cn.luo.yuan.maze.model.skill;

import cn.luo.yuan.maze.model.Data;
import cn.luo.yuan.maze.model.Hero;
import cn.luo.yuan.maze.model.IDModel;
import cn.luo.yuan.maze.model.NameObject;
import cn.luo.yuan.maze.model.skill.result.SkillResult;
import cn.luo.yuan.maze.utils.Version;

import java.io.Serializable;

/**
 * Created by gluo on 4/27/2017.
 */
public interface Skill extends Serializable, NameObject, IDModel {
    long serialVersionUID = Version.SERVER_VERSION;

    SkillResult invoke(SkillParameter parameter);
    void enable(SkillParameter parameter);
    default boolean canEnable(SkillParameter parameter) {
        return isEnablePointEnough(parameter);
    }

    default boolean isEnablePointEnough(SkillParameter parameter) {
        return parameter.getOwner() instanceof Hero && ((Hero) parameter.getOwner()).getPoint() > Data.SKILL_ENABLE_COST;
    }

    boolean isEnable();
    void disable();
}
