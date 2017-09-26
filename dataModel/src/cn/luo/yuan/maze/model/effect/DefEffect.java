package cn.luo.yuan.maze.model.effect;

import cn.luo.yuan.maze.utils.Field;

/**
 * Created by luoyuan on 2017/3/18.
 */
public class DefEffect extends cn.luo.yuan.maze.model.effect.original.DefEffect {
    private static final long serialVersionUID = Field.SERVER_VERSION;

    public long getDef() {
        return getValue();
    }

    public void setDef(long def) {
        setValue(def);
    }

    public cn.luo.yuan.maze.model.effect.original.DefEffect covertToOriginal() {
        cn.luo.yuan.maze.model.effect.original.DefEffect effect = new cn.luo.yuan.maze.model.effect.original.DefEffect();
        effect.setDef(getDef());
        return effect;
    }
}
