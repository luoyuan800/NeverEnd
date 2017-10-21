package cn.luo.yuan.maze.model.effect.original;

import cn.luo.yuan.maze.model.effect.Effect;
import cn.luo.yuan.maze.model.effect.FloatValueEffect;
import cn.luo.yuan.maze.utils.Field;

/**
 * Copyright @Luo
 * Created by Gavin Luo on 9/12/2017.
 */
public class PetABEEffect extends FloatValueEffect {
    private static final long serialVersionUID = Field.SERVER_VERSION;
    private String tag;
    private boolean enable = false;

    private boolean elementControl;

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
        this.elementControl = control;
    }

    @Override
    public boolean isElementControl() {
        return elementControl;
    }

    @Override
    public String toString() {
        return "增加宠物变异：" + getValue();
    }

    @Override
    public boolean isReject(Class<? extends Effect> effectClass) {
        return effectClass == PetRateEffect.class || effectClass == SkillRateEffect.class || effectClass == cn.luo.yuan.maze.model.effect.SkillRateEffect.class;
    }
}
