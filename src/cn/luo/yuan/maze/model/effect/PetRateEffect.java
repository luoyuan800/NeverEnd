package cn.luo.yuan.maze.model.effect;

import cn.luo.yuan.maze.R;
import cn.luo.yuan.maze.client.utils.Resource;
import cn.luo.yuan.maze.utils.StringUtils;

/**
 * Created by luoyuan on 2017/3/19.
 */
public class PetRateEffect extends cn.luo.yuan.maze.model.effect.original.PetRateEffect {

    public String toString(){
        return Resource.getString(R.string.pet_rate_effect) + StringUtils.DecimalFormatRound(getValue(),2) + "%";
    }

    public cn.luo.yuan.maze.model.effect.original.PetRateEffect covertToOriginal() {
        cn.luo.yuan.maze.model.effect.original.PetRateEffect effect = new cn.luo.yuan.maze.model.effect.original.PetRateEffect();
        effect.setPetRate(getPetRate());
        return effect;
    }
}
