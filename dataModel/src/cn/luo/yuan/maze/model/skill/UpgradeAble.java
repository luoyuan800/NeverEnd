package cn.luo.yuan.maze.model.skill;

import cn.luo.yuan.maze.model.Data;
import cn.luo.yuan.maze.model.Hero;

/**
 * Created by gluo on 4/27/2017.
 */
public interface UpgradeAble {
    default boolean canUpgrade(SkillParameter parameter){
        return isPointEnough(parameter);
    }
    boolean upgrade(SkillParameter parameter);

    default boolean isPointEnough(SkillParameter parameter) {
        return ((Hero) parameter.getOwner()).getPoint() > getLevel() * Data.SKILL_ENABLE_COST;
    }

    long getLevel();
}
