package cn.luo.yuan.maze.model.effect.original;

import cn.luo.yuan.maze.model.effect.Effect;
import cn.luo.yuan.maze.model.effect.LongValueEffect;
import cn.luo.yuan.maze.utils.Field;

/**
 * Created by luoyuan on 2017/3/18.
 */
public class AtkEffect implements LongValueEffect {
    private static final long serialVersionUID = Field.SERVER_VERSION;
    private long atk;
    private boolean enable = false;

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
    public Effect clone(){
        try {
            return (Effect) super.clone();
        } catch (CloneNotSupportedException e) {
            return this;
        }
    }
}
