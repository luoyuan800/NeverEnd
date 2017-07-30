package cn.luo.yuan.maze.model.effect.original;

import cn.luo.yuan.maze.model.effect.Effect;
import cn.luo.yuan.maze.model.effect.FloatValueEffect;
import cn.luo.yuan.maze.utils.Field;
import cn.luo.yuan.maze.utils.StringUtils;

/**
 * Created by gluo on 5/15/2017.
 */
public class SkillRateEffect extends FloatValueEffect {
    private static final long serialVersionUID = Field.SERVER_VERSION;
    private String tag;
    private float skillRate;
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

    @Override
    public Float getValue() {
        return getSkillRate();
    }

    @Override
    public void setValue(float value) {
        setSkillRate(value);
    }

    public float getSkillRate() {
        return skillRate;
    }

    public void setSkillRate(float skillRate) {
        this.skillRate = skillRate;
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
        return "增加技能释放：" + StringUtils.formatPercentage(getValue());
    }
}
