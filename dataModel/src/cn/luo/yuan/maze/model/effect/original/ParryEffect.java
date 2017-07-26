package cn.luo.yuan.maze.model.effect.original;

import cn.luo.yuan.maze.model.effect.Effect;
import cn.luo.yuan.maze.model.effect.FloatValueEffect;
import cn.luo.yuan.maze.utils.StringUtils;

/**
 * Created by luoyuan on 2017/7/22.
 */
public class ParryEffect extends FloatValueEffect {
    private boolean enable;
    private String tag;
    private boolean elementControl;
    private float parry;

    @Override
    public String toString() {
        return "增加格挡：" + StringUtils.formatNumber(parry);
    }

    @Override
    public boolean isEnable() {
        return enable;
    }

    @Override
    public void setEnable(boolean enable) {
        this.enable = enable;
    }

    public boolean isElementControl() {
        return elementControl;
    }

    public void setElementControl(boolean elementControl) {
        this.elementControl = elementControl;
    }

    @Override
    public String getTag() {
        return tag;
    }

    @Override
    public void setTag(String tag) {
        this.tag = tag;
    }

    @Override
    public void setValue(float value) {
        this.parry = value;
    }

    @Override
    public Float getValue() {
        return this.parry;
    }
}
