package cn.luo.yuan.maze.model.effect;

import cn.luo.yuan.maze.utils.EncodeFloat;

/**
 * Created by luoyuan on 2017/3/19.
 */
public abstract class FloatValueEffect extends Effect {
    private EncodeFloat value = new EncodeFloat(0f);

    public final Float getValue() {
        return this.value.getValue();
    }

    public final void setValue(float value) {
        this.value.setValue(value);
    }

    @Override
    public Effect clone() {
        FloatValueEffect e = (FloatValueEffect) super.clone();
        e.value = new EncodeFloat(getValue());
        return e;
    }
}
