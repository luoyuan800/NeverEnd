package cn.luo.yuan.maze.model.effect.original;

import cn.luo.yuan.maze.model.effect.Effect;
import cn.luo.yuan.maze.model.effect.LongValueEffect;
import cn.luo.yuan.maze.utils.StringUtils;

/**
 * Created by gluo on 7/3/2017.
 */
public class ClickMaterialEffect implements LongValueEffect {
    private long material;

    private boolean enable = false;

    private boolean elementControl;

    @Override
    public void setValue(long value) {
        this.material = value;
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
            e.printStackTrace();
        }
        return this;
    }

    @Override
    public void setEnable(boolean enable) {
        this.enable = enable;
    }

    @Override
    public Long getValue() {
        return material;
    }

    @Override
    public void setElementControl(boolean control) {
        this.elementControl = control;
    }

    @Override
    public boolean isElementControl() {
        return elementControl;
    }

    @Override
    public String toString(){
        return "点击锻造奖励：" + StringUtils.formatNumber(material);
    }
}
