package cn.luo.yuan.maze.model.effect.original;

import cn.luo.yuan.maze.model.effect.Effect;
import cn.luo.yuan.maze.model.effect.FloatValueEffect;
import cn.luo.yuan.maze.utils.EncodeFloat;
import cn.luo.yuan.maze.utils.StringUtils;

/**
 * Created by luoyuan on 2017/7/22.
 */
public class DogeRateEffect extends FloatValueEffect {
    private boolean enable;
    private String tag;
    private boolean element;

    @Override
    public boolean isEnable() {
        return enable;
    }

    @Override
    public void setTag(String tag) {
        this.tag = tag;
    }

    @Override
    public String getTag() {
        return tag;
    }

    @Override
    public void setEnable(boolean enable) {
        this.enable = enable;
    }

    @Override
    public void setElementControl(boolean control) {
        this.element = control;
    }

    @Override
    public boolean isElementControl() {
        return element;
    }

    @Override
    public String toString() {
        return "增加闪避: " + StringUtils.formatNumber(getValue());
    }
}
