package cn.luo.yuan.maze.model.effect.original;

import cn.luo.yuan.maze.model.effect.Effect;
import cn.luo.yuan.maze.model.effect.FloatValueEffect;
import cn.luo.yuan.maze.utils.EncodeFloat;
import cn.luo.yuan.maze.utils.Field;

/**
 * Created by luoyuan on 2017/6/29.
 */
public class EggRateEffect extends FloatValueEffect {
    private static final long serialVersionUID = Field.SERVER_VERSION;
    private String tag;
    private boolean enable = false;

    private boolean elementControl;

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
        this.elementControl = control;
    }

    public float getEggRate() {
        return getValue();
    }

    public void setEggRate(float eggRate) {
        this.setValue(eggRate);
    }

    public String toString() {
        return "增加生蛋率：" + getEggRate();
    }

    @Override
    public String getTag() {
        return tag;
    }

    @Override
    public void setTag(String tag) {
        this.tag = tag;
    }
}
