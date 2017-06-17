package cn.luo.yuan.maze.model.effect.original;

import cn.luo.yuan.maze.model.effect.Effect;
import cn.luo.yuan.maze.model.effect.LongValueEffect;
import cn.luo.yuan.maze.utils.Field;

/**
 * Created by luoyuan on 2017/3/18.
 */
public class StrEffect implements LongValueEffect{
    private static final long serialVersionUID = Field.SERVER_VERSION;
    private long str;
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
    public long getStr() {
        return str;
    }

    public void setStr(long str) {
        this.str = str;
    }

    @Override
    public Long getValue() {
        return getStr();
    }

    @Override
    public void setValue(long value) {
        setStr(value);
    }

    @Override
    public boolean isEnable() {
        return enable;
    }

    public void setEnable(boolean enable) {
        this.enable = enable;
    }
    public Effect clone(){
        try {
            return (Effect) super.clone();
        } catch (CloneNotSupportedException e) {
            return this;
        }
    }
}
