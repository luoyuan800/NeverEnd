package cn.luo.yuan.maze.model.effect.original;

import cn.luo.yuan.maze.model.effect.FloatValueEffect;
import cn.luo.yuan.maze.utils.Field;
import cn.luo.yuan.maze.utils.StringUtils;

/**
 * Created by luoyuan on 2017/3/19.
 */
public class PetRateEffect extends FloatValueEffect {
    private static final long serialVersionUID = Field.SERVER_VERSION;
    private String tag;
    private boolean enable = false;

    private boolean elementControl;

    @Override
    public boolean isElementControl() {
        return elementControl;
    }

    @Override
    public void setElementControl(boolean elementControl) {
        this.elementControl = elementControl;
    }

    public float getPetRate() {
        return getValue();
    }

    public void setPetRate(float petRate) {
        setValue(petRate);
    }


    @Override
    public boolean isEnable() {
        return enable;
    }

    public void setEnable(boolean enable) {
        this.enable = enable;
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
    public String toString() {
        return "增加宠物捕获：" + StringUtils.formatPercentage(getValue());
    }
}
