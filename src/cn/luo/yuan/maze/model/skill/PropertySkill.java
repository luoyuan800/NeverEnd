package cn.luo.yuan.maze.model.skill;

/**
 * Created by gluo on 4/27/2017.
 */
public abstract class PropertySkill implements Skill {
    private boolean enable;
    public void disable(){
        this.enable = false;
    }

    abstract void disable(SkillParameter parameter);

    public boolean isEnable(){
        return enable;
    }
    public void setEnable(boolean enable){
        this.enable = enable;
    }
}
