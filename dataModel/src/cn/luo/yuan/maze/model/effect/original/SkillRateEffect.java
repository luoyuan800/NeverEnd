package cn.luo.yuan.maze.model.effect.original;

import cn.luo.yuan.maze.model.effect.FloatValueEffect;

/**
 * Created by gluo on 5/15/2017.
 */
public class SkillRateEffect implements FloatValueEffect {
    private float skillRate;
    @Override
    public void setValue(float value) {
        setSkillRate(value);
    }

    @Override
    public float getValue() {
        return getSkillRate();
    }

    public float getSkillRate() {
        return skillRate;
    }

    public void setSkillRate(float skillRate) {
        this.skillRate = skillRate;
    }
}
