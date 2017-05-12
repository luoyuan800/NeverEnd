package cn.luo.yuan.maze.model.skill;

/**
 * Created by gluo on 4/27/2017.
 */
public interface MountAble {
    boolean isMounted();

     void mount();

     void unMount();

    boolean canMount(SkillParameter parameter);
}
