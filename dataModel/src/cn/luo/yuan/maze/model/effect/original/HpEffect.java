package cn.luo.yuan.maze.model.effect.original;

import cn.luo.yuan.maze.model.effect.Effect;
import cn.luo.yuan.maze.model.effect.LongValueEffect;
import cn.luo.yuan.maze.utils.Field;
import cn.luo.yuan.maze.utils.StringUtils;

/**
 * Created by luoyuan on 2017/3/18.
 */
public class HpEffect extends LongValueEffect {
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

    public long getHp() {
        return getValue();
    }

    public void setHp(long hp) {
        setValue(hp);
    }

    @Override
    public boolean isEnable() {
        return enable;
    }

    public void setEnable(boolean enable) {
        this.enable = enable;
    }


    @Override
    public boolean isReject(Class<? extends Effect> effectClass) {
        return effectClass == AtkEffect.class || effectClass == cn.luo.yuan.maze.model.effect.AtkEffect.class || effectClass == AtkPercentEffect.class;
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
        return "增加基础生命：" + StringUtils.formatNumber(getValue());
    }
}
