package cn.luo.yuan.maze.model.skill;

/**
 * Created by gluo on 4/27/2017.
 */
public interface UpgradeAble {
    boolean canUpgrade(SkillParameter parameter);
    boolean upgrade(SkillParameter parameter);
    long getLevel();
}
