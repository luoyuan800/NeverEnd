package cn.luo.yuan.maze.model.effect;

import cn.luo.yuan.maze.R;
import cn.luo.yuan.maze.utils.Resource;
import cn.luo.yuan.maze.utils.StringUtils;

/**
 * Created by luoyuan on 2017/3/19.
 */
public class PetRateEffect extends cn.luo.yuan.maze.model.effect.original.PetRateEffect {
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
    public Float getValue() {
        return getPetRate();
    }

    public cn.luo.yuan.maze.model.effect.original.PetRateEffect covertToOriginal() {
        cn.luo.yuan.maze.model.effect.original.PetRateEffect effect = new cn.luo.yuan.maze.model.effect.original.PetRateEffect();
        effect.setPetRate(getPetRate());
        return effect;
    }
    public Effect clone(){
        return (Effect) super.clone();
    }
}
