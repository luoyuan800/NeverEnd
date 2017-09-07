package cn.luo.yuan.maze.model.effect.original;

import cn.luo.yuan.maze.model.effect.Effect;
import cn.luo.yuan.maze.model.effect.FloatValueEffect;
import cn.luo.yuan.maze.model.effect.LongValueEffect;
import cn.luo.yuan.maze.utils.EncodeFloat;
import cn.luo.yuan.maze.utils.MathUtils;

/**
 * Copyright @Luo
 * Created by Gavin Luo on 7/21/2017.
 */
public abstract class PercentEffect extends FloatValueEffect {
    private boolean enable;
    private boolean elementControl;
    private String tag;

    public String getTag() {
        return tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }

    @Override
    public boolean isEnable() {
        return enable;
    }

    @Override
    public void setEnable(boolean enable) {
        this.enable = enable;
    }


    @Override
    public boolean isElementControl() {
        return elementControl;
    }

    @Override
    public void setElementControl(boolean control) {
        elementControl = control;
    }

    public void setPercent(float percent) {
        this.setValue( percent);
    }

    public long getAdditionValue(long value) {
        return (long) ((double) value * (getValue() / 100d));
    }

    public long getReduceValue(long value) {
        return MathUtils.getPercentAdditionReduceValue(value, getValue());
    }

}
