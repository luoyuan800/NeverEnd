package cn.luo.yuan.maze.model.skill;

import cn.luo.yuan.maze.model.Data;
import cn.luo.yuan.maze.model.Hero;
import cn.luo.yuan.maze.model.skill.result.SkillResult;
import cn.luo.yuan.maze.service.EffectHandler;
import cn.luo.yuan.utils.Random;

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
    private long useTime = 0L;
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
        float v = rate + (hero instanceof Hero ? EffectHandler.getEffectAdditionFloatValue(EffectHandler.SKILL_RATE, ((Hero) hero).getEffects()) : 0f);
        if(v > Data.RATE_MAX){
            v = Data.RATE_MAX;
        }
        return random.nextInt(100) + random.nextFloat() < v;
    }

    public float getRate() {
        return rate;
    }

    public void setRate(float rate) {
        this.rate = rate;
    }

    @Override
    public SkillResult perform(SkillParameter parameter) {
        useTime ++;
        if(this instanceof UpgradeAble){
            if(useTime > ((UpgradeAble)this).getLevel() * Data.SKILL_ENABLE_COST * 5){
                ((UpgradeAble)this).upgrade(parameter);
            }
        }
        return invoke(parameter);
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
