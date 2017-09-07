package cn.luo.yuan.maze.model.effect;

import cn.luo.yuan.maze.utils.EncodeLong;

/**
 * Created by luoyuan on 2017/3/19.
 */
public abstract class LongValueEffect extends Effect {
    private EncodeLong value = new EncodeLong(0);

    public final void setValue(long value){
        this.value.setValue(value);
    }
    public final Long getValue(){
        return this.value.getValue();
    }

    @Override
    public Effect clone() {
        LongValueEffect e = (LongValueEffect) super.clone();
        e.value = new EncodeLong(getValue());
        return e;
    }
}
