package cn.luo.yuan.maze.model.effect.original;

import cn.luo.yuan.maze.model.effect.FloatValueEffect;

/**
 * Created by gluo on 5/15/2017.
 */
public class SkillRateEffect implements FloatValueEffect{
    private float skillRate;
    private boolean enable = false;
    @Override
    public void setValue(float value) {
        setSkillRate(value);
    }

    @Override
    public Float getValue() {
        return getSkillRate();
    }

    public float getSkillRate() {
        return skillRate;
    }

    public void setSkillRate(float skillRate) {
        this.skillRate = skillRate;
    }

    @Override
    public boolean isEnable() {
        return enable;
    }

    public void setEnable(boolean enable) {
        this.enable = enable;
    }

}
