package cn.luo.yuan.maze.model.effect.original;

import cn.luo.yuan.maze.model.Data;
import cn.luo.yuan.maze.model.NameObject;
import cn.luo.yuan.maze.model.effect.FloatValueEffect;

/**
 * Created by luoyuan on 2017/3/19.
 */
public class PetRateEffect implements FloatValueEffect, NameObject {
    private float meetRate;
    private boolean enable = false;

    public float getPetRate() {
        return meetRate;
    }

    public void setPetRate(float meetRate) {
        this.meetRate = meetRate;
    }

    @Override
    public float getValue() {
        return getPetRate();
    }

    @Override
    public void setValue(float value) {
        setPetRate(value);
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
