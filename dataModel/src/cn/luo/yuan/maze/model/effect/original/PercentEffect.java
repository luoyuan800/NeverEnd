package cn.luo.yuan.maze.model.effect.original;

import cn.luo.yuan.maze.model.effect.Effect;
import cn.luo.yuan.maze.model.effect.FloatValueEffect;
import cn.luo.yuan.maze.model.effect.LongValueEffect;
import cn.luo.yuan.maze.utils.MathUtils;

/**
 * Copyright @Luo
 * Created by Gavin Luo on 7/21/2017.
 */
public abstract class PercentEffect extends FloatValueEffect {
    private boolean enable;
    private boolean elementControl;
    private float percent;
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

    public Float getValue() {
        return percent;
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
        this.percent = percent;
    }

    public long getAdditionValue(long value) {
        return (long) ((double) value * (percent / 100d));
    }

    public long getReduceValue(long value) {
        return MathUtils.getPercentAdditionReduceValue(value, percent);
    }

    @Override
    public void setValue(float value) {
        setPercent(value);
    }
}
