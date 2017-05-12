package cn.luo.yuan.maze.model.effect.original;

import cn.luo.yuan.maze.model.effect.FloatValueEffect;

/**
 * Created by luoyuan on 2017/3/19.
 */
public class PetRateEffect implements FloatValueEffect {
    private float meetRate;

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
}
