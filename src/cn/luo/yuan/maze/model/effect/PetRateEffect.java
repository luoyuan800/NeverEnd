package cn.luo.yuan.maze.model.effect;

import cn.luo.yuan.maze.R;
import cn.luo.yuan.maze.utils.Resource;
import cn.luo.yuan.maze.utils.StringUtils;

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

    public String toString(){
        return Resource.getString(R.string.pet_rate_effect) + StringUtils.DecimalFormatRound(meetRate,2) + "%";
    }

    @Override
    public void setValue(float value) {
        setPetRate(value);
    }

    @Override
    public float getValue() {
        return getPetRate();
    }
}
