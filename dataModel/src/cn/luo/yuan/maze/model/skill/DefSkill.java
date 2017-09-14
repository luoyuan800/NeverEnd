package cn.luo.yuan.maze.model.skill;

import cn.luo.yuan.maze.model.Data;
import cn.luo.yuan.maze.model.Hero;
import cn.luo.yuan.maze.model.skill.result.SkillResult;
import cn.luo.yuan.maze.service.EffectHandler;
import cn.luo.yuan.maze.utils.Random;

/**
 * Created by gluo on 4/27/2017.
 */
public abstract class DefSkill implements Skill, MountAble {
    private boolean mounted;
    private boolean enable;
    private float rate = 5;
    private String id;
    private boolean delete;
    private long useTime = 0L;
    @Override
    public boolean isDelete() {
        return delete;
    }
    public void markDelete(){
        delete = true;
    }
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

    @Override
    public SkillResult perform(SkillParameter parameter) {
        useTime ++;
        if(this instanceof UpgradeAble){
            if(useTime > ((UpgradeAble)this).getLevel() * Data.SKILL_ENABLE_COST * 10){
                ((UpgradeAble)this).upgrade(parameter);
            }
        }
        return invoke(parameter);
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
        float v = rate + (hero instanceof Hero ? EffectHandler.getEffectAdditionFloatValue(EffectHandler.SKILL_RATE, ((Hero) hero).getEffects()) : 0f);
        if(v > Data.RATE_MAX){
            v = Data.RATE_MAX;
        }
        return random.nextInt(100) + random.nextFloat() < v;
    }

    public void setRate(float rate) {
        this.rate = rate;
    }
    public float getRate() {
        return rate;
    }

    @Override
    public String getId() {
        return id;
    }

    @Override
    public void setId(String id) {
        this.id = id;
    }

    public long getUseTime() {
        return useTime;
    }

    public void setUseTime(long useTime) {
        this.useTime = useTime;
    }
}
