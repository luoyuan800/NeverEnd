package cn.luo.yuan.maze.model.effect;

/**
 * Created by luoyuan on 2017/3/19.
 */
public class PetRateEffect extends cn.luo.yuan.maze.model.effect.original.PetRateEffect {

    public cn.luo.yuan.maze.model.effect.original.PetRateEffect covertToOriginal() {
        cn.luo.yuan.maze.model.effect.original.PetRateEffect effect = new cn.luo.yuan.maze.model.effect.original.PetRateEffect();
        effect.setPetRate(getPetRate());
        return effect;
    }
}
