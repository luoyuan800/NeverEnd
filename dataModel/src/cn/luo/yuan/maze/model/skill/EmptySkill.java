package cn.luo.yuan.maze.model.skill;

import cn.luo.yuan.maze.model.skill.result.SkillResult;
import cn.luo.yuan.maze.utils.StringUtils;

/**
 * Created by gluo on 4/27/2017.
 */
public class EmptySkill implements Skill {
    private boolean delete;
    @Override
    public boolean isDelete() {
        return delete;
    }
    public void markDelete(){
        delete = true;
    }
    public static final Skill EMPTY_SKILL = new EmptySkill();
    @Override
    public SkillResult invoke(SkillParameter parameter) {
        return null;
    }

    @Override
    public void enable(SkillParameter parameter) {

    }

    @Override
    public boolean canEnable(SkillParameter parameter) {
        return false;
    }

    @Override
    public boolean isEnable() {
        return false;
    }

    @Override
    public void disable() {

    }

    @Override
    public String getSkillName() {
        return StringUtils.EMPTY_STRING;
    }

    @Override
    public String getName() {
        return null;
    }

    @Override
    public String getDisplayName() {
        return null;
    }

    @Override
    public String getId() {
        return "";
    }

    @Override
    public void setId(String id) {

    }
}
