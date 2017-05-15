package cn.luo.yuan.maze.model.skill;

import cn.luo.yuan.maze.model.Hero;
import cn.luo.yuan.maze.service.EffectHandler;
import cn.luo.yuan.maze.utils.Random;

/**
 * Created by gluo on 4/27/2017.
 */
public abstract class DefSkill implements Skill, MountAble {
    private boolean mounted;
    private boolean enable;
    private float rate;

    public boolean isMounted(){
        return mounted;
    }
    public void mount(){
        this.mounted = true;
    }
    public void unMount(){
        this.mounted = false;
    }
    public void disable(){
        this.enable = false;
    }

    public boolean isEnable(){
        return enable;
    }
    public void setEnable(boolean enable){
        this.enable = enable;
    }

    public boolean invokeAble(SkillParameter parameter) {
        Random random = parameter.get("random");
        SkillAbleObject hero = parameter.getOwner();
        return random.nextInt(100) + random.nextFloat() < rate + (hero instanceof Hero ? EffectHandler.getEffectAdditionFloatValue(EffectHandler.SKILL_RATE, ((Hero)hero).getEffects()) : 0f);
    }

    public void setRate(float rate) {
        this.rate = rate;
    }
}
