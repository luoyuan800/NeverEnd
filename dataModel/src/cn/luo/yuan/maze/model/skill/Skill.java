package cn.luo.yuan.maze.model.skill;

import cn.luo.yuan.object.IDModel;
import cn.luo.yuan.maze.model.NameObject;
import cn.luo.yuan.maze.model.skill.result.SkillResult;
import cn.luo.yuan.maze.utils.Field;

import java.io.Serializable;

/**
 * Created by gluo on 4/27/2017.
 */
public interface Skill extends Serializable, NameObject, IDModel {
    long serialVersionUID = Field.SERVER_VERSION;

    SkillResult invoke(SkillParameter parameter);
    SkillResult perform(SkillParameter parameter);

    void enable(SkillParameter parameter);

    boolean canEnable(SkillParameter parameter);

    boolean isEnable();

    void disable();
    String getSkillName();
}
