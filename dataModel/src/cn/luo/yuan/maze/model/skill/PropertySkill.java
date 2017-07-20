package cn.luo.yuan.maze.model.skill;

import cn.luo.yuan.maze.model.skill.result.DonothingResult;
import cn.luo.yuan.maze.model.skill.result.SkillResult;
import cn.luo.yuan.maze.model.skill.result.SkipThisTurn;

/**
 * Created by gluo on 4/27/2017.
 */
public abstract class PropertySkill implements Skill {
    private boolean delete;
    private String id;

    @Override
    public boolean isDelete() {
        return delete;
    }
    public void markDelete(){
        delete = true;
    }
    private boolean enable;
    public void disable(){
        this.enable = false;
    }

    public abstract void disable(SkillParameter parameter);

    public boolean isEnable(){
        return enable;
    }
    public void setEnable(boolean enable){
        this.enable = enable;
    }

    @Override
    public String getId() {
        return id;
    }

    @Override
    public void setId(String id) {
        this.id = id;
    }

   public SkillResult invoke(SkillParameter parameter){
        //Do nothing
       return new DonothingResult();
    }
}
