package cn.luo.yuan.maze.model.effect.original;

import cn.luo.yuan.maze.model.Data;
import cn.luo.yuan.maze.model.NameObject;
import cn.luo.yuan.maze.model.effect.FloatValueEffect;

/**
 * Created by gluo on 5/15/2017.
 */
public class SkillRateEffect implements FloatValueEffect, NameObject {
    private float skillRate;
    private boolean enable = false;
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

    @Override
    public boolean isEnable() {
        return enable;
    }

    public void setEnable(boolean enable) {
        this.enable = enable;
    }
    @Override
    public String getName() {
        return getClass().getSimpleName();
    }

    @Override
    public String getDisplayName() {
        return "<font color='" + (isEnable()? Data.ENABLE_COLOR : Data.DISABLE_COLOR) + "'>" + toString() + "</font>";
    }
}
