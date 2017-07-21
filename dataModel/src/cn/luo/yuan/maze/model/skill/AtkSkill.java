package cn.luo.yuan.maze.model.skill;

import cn.luo.yuan.maze.model.Hero;
import cn.luo.yuan.maze.service.EffectHandler;
import cn.luo.yuan.maze.utils.Random;

/**
 * Created by gluo on 4/27/2017.
 */
public abstract class AtkSkill implements Skill, MountAble {
    private boolean delete;
    @Override
    public boolean isDelete() {
        return delete;
    }
    public void markDelete(){
        delete = true;
    }
    private String id;
    private boolean mounted;
    private boolean enable;
    private float rate = 5f;
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

    public boolean invokeAble(SkillParameter parameter){
        Random random = parameter.get("random");
        SkillAbleObject hero = parameter.getOwner();
        return random.nextInt(100) + random.nextFloat() < rate + (hero instanceof Hero ? EffectHandler.getEffectAdditionFloatValue(EffectHandler.SKILL_RATE, ((Hero)hero).getEffects()) : 0f);
    }

    public float getRate() {
        return rate;
    }

    public void setRate(float rate) {
        this.rate = rate;
    }

    @Override
    public String getId() {
        return id;
    }

    @Override
    public void setId(String id) {
        this.id = id;
    }
}
