package cn.luo.yuan.maze.model.effect;

import cn.luo.yuan.maze.utils.Field;

/**
 * Created by luoyuan on 2017/3/18.
 */
public class StrEffect extends cn.luo.yuan.maze.model.effect.original.StrEffect {
    private static final long serialVersionUID = Field.SERVER_VERSION;

    public long getStr() {
        return getValue();
    }

    public void setStr(long str) {
        this.setValue(str);
    }

    public cn.luo.yuan.maze.model.effect.original.StrEffect covertToOriginal() {
        cn.luo.yuan.maze.model.effect.original.StrEffect effect = new cn.luo.yuan.maze.model.effect.original.StrEffect();
        effect.setStr(getStr());
        return effect;
    }

}
