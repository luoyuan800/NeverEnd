package cn.luo.yuan.maze.model.skill;

/**
 * Created by gluo on 4/27/2017.
 */
public abstract class AtkSkill implements Skill, MountAble {
    private boolean mounted;
    private boolean enable;
    public boolean isMounted(){
        return mounted;
    }
    public void mount(){
        this.mounted = true;
    }
    public void unMount(){
        this.mounted = false;
    }
    public boolean isEnable(){
        return enable;
    }
    public void disable(){
        this.enable = false;
    }
    public void setEnable(boolean enable){
        this.enable = enable;
    }

}
