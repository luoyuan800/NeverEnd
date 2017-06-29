package cn.luo.yuan.maze.model.effect.original;

import cn.luo.yuan.maze.model.effect.Effect;
import cn.luo.yuan.maze.model.effect.FloatValueEffect;

/**
 * Created by luoyuan on 2017/6/29.
 */
public class EggRateEffect implements FloatValueEffect {
    private float eggRate;
    private boolean enable = false;

    private boolean elementControl;

    @Override
    public void setValue(float value) {
        setEggRate(value);
    }

    @Override
    public boolean isEnable() {
        return enable;
    }

    @Override
    public Effect clone() {
        try {
            return (Effect) super.clone();
        } catch (CloneNotSupportedException e) {
            return this;
        }
    }

    @Override
    public void setEnable(boolean enable) {
        this.enable = enable;
    }

    @Override
    public Float getValue() {
        return getEggRate();
    }

    @Override
    public void setElementControl(boolean control) {
        this.elementControl = control;
    }

    @Override
    public boolean isElementControl() {
        return elementControl;
    }

    public void setEggRate(float eggRate) {
        this.eggRate = eggRate;
    }

    public float getEggRate() {
        return eggRate;
    }
    public String toString(){
        return "增加生蛋率：" + eggRate;
    }

}
