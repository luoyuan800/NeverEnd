package cn.luo.yuan.maze.model.effect.original;

import cn.luo.yuan.maze.model.effect.Effect;
import cn.luo.yuan.maze.model.effect.LongValueEffect;
import cn.luo.yuan.maze.utils.Field;

/**
 * Created by luoyuan on 2017/3/18.
 */
public class AtkEffect extends LongValueEffect {
    private String tag;
    private static final long serialVersionUID = Field.SERVER_VERSION;
    private long atk;
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
        return atk;
    }

    public void setAtk(long atk) {
        this.atk = atk;
    }

    @Override
    public Long getValue() {
        return getAtk();
    }

    @Override
    public void setValue(long value) {
        setAtk(value);
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
}
