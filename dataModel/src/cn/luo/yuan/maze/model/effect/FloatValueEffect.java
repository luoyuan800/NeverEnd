package cn.luo.yuan.maze.model.effect;

import cn.luo.yuan.encode.number.EncodeFloat;
import cn.luo.yuan.maze.utils.StringUtils;

/**
 * Created by luoyuan on 2017/3/19.
 */
public abstract class FloatValueEffect extends Effect {
    private EncodeFloat value = new EncodeFloat(0f,StringUtils.NUMBER_STRING_FORMATTER);

    public final Float getValue() {
        return this.value.getValue();
    }

    public final void setValue(float value) {
        this.value.setValue(value);
    }

    @Override
    public Effect clone() {
        FloatValueEffect e = (FloatValueEffect) super.clone();
        e.value = new EncodeFloat(getValue(), StringUtils.NUMBER_STRING_FORMATTER);
        return e;
    }
}
