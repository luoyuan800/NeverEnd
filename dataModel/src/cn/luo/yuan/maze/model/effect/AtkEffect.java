package cn.luo.yuan.maze.model.effect;

import cn.luo.yuan.maze.utils.Field;

/**
 * Created by luoyuan on 2017/3/18.
 */
public class AtkEffect extends cn.luo.yuan.maze.model.effect.original.AtkEffect {
    private static final long serialVersionUID = Field.SERVER_VERSION;

    public long getAtk() {
        return getValue();
    }

    public void setAtk(long atk) {
        this.setValue(atk);
    }

    public cn.luo.yuan.maze.model.effect.original.AtkEffect covertToOriginal() {
        cn.luo.yuan.maze.model.effect.original.AtkEffect atkEffect = new cn.luo.yuan.maze.model.effect.original.AtkEffect();
        atkEffect.setAtk(getAtk());
        return atkEffect;
    }
}
