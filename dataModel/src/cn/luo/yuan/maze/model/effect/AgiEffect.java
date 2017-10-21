package cn.luo.yuan.maze.model.effect;

import cn.luo.yuan.maze.model.effect.original.StrEffect;
import cn.luo.yuan.maze.utils.Field;

import java.util.Arrays;
import java.util.Collections;

/**
 * Created by luoyuan on 2017/3/18.
 */
public class AgiEffect extends cn.luo.yuan.maze.model.effect.original.AgiEffect {
    private static final long serialVersionUID = Field.SERVER_VERSION;

    public long getAgi() {
        return getValue();
    }

    public void setAgi(long agi) {
        setValue(agi);
    }

    public cn.luo.yuan.maze.model.effect.original.AgiEffect covertToOriginal() {
        cn.luo.yuan.maze.model.effect.original.AgiEffect agiEffect = new cn.luo.yuan.maze.model.effect.original.AgiEffect();
        agiEffect.setAgi(getAgi());
        return agiEffect;
    }

    public Effect clone() {
        return (Effect) super.clone();
    }

    @Override
    public boolean isReject(Class<? extends Effect> effectClass) {
        return effectClass == StrEffect.class || effectClass == cn.luo.yuan.maze.model.effect.StrEffect.class
        || effectClass == DefEffect.class || effectClass == cn.luo.yuan.maze.model.effect.original.DefEffect.class;
    }
}
