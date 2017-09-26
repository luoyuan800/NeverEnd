package cn.luo.yuan.maze.model.effect;

import cn.luo.yuan.maze.utils.Field;

/**
 * Created by luoyuan on 2017/3/18.
 */
public class HpEffect extends cn.luo.yuan.maze.model.effect.original.HpEffect {
    private static final long serialVersionUID = Field.SERVER_VERSION;

    public long getHp() {
        return getValue();
    }

    public void setHp(long hp) {
        this.setValue(hp);
    }


    public cn.luo.yuan.maze.model.effect.original.HpEffect covertToOriginal() {
        cn.luo.yuan.maze.model.effect.original.HpEffect effect = new cn.luo.yuan.maze.model.effect.original.HpEffect();
        effect.setHp(getHp());
        return effect;
    }
}
