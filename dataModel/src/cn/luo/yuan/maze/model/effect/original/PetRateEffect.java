package cn.luo.yuan.maze.model.effect.original;

import cn.luo.yuan.maze.model.effect.FloatValueEffect;

/**
 * Created by luoyuan on 2017/3/19.
 */
public class PetRateEffect implements FloatValueEffect {
    private float meetRate;
    private boolean enable = true;

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
}
