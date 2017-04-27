package cn.luo.yuan.maze.model.skill;

/**
 * Created by gluo on 4/27/2017.
 */
public interface Skill {
    SkillResult invoke(SkillParameter parameter);
    void enable(SkillParameter parameter);
    boolean canEnable(SkillParameter parameter);
    boolean isEnable();
    void disable();
}
