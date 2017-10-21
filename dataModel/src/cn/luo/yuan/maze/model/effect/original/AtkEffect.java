package cn.luo.yuan.maze.model.effect.original;

import cn.luo.yuan.maze.model.effect.Effect;
import cn.luo.yuan.maze.model.effect.LongValueEffect;
import cn.luo.yuan.maze.utils.Field;
import cn.luo.yuan.maze.utils.StringUtils;

import java.util.Arrays;

/**
 * Created by luoyuan on 2017/3/18.
 */
public class AtkEffect extends LongValueEffect {
    private String tag;
    private static final long serialVersionUID = Field.SERVER_VERSION;
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
    public long getAtk() {
        return getValue();
    }

    public void setAtk(long atk) {
        setValue(atk);
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
        return "增加攻击：" + StringUtils.formatNumber(getValue());
    }

    @Override
    public boolean isReject(Class<? extends Effect> effectClass) {
        return Arrays.asList(cn.luo.yuan.maze.model.effect.original.AgiEffect.class, cn.luo.yuan.maze.model.effect.AgiEffect.class, cn.luo.yuan.maze.model.effect.HpEffect.class, cn.luo.yuan.maze.model.effect.original.HpEffect.class).contains(effectClass);
    }
}
