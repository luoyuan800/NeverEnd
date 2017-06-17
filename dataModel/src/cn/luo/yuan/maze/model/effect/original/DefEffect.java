package cn.luo.yuan.maze.model.effect.original;

import cn.luo.yuan.maze.model.effect.Effect;
import cn.luo.yuan.maze.model.effect.LongValueEffect;
import cn.luo.yuan.maze.utils.Field;

/**
 * Created by luoyuan on 2017/3/18.
 */
public class DefEffect implements LongValueEffect{
    private static final long serialVersionUID = Field.SERVER_VERSION;
    private long def;
    private boolean enable = false;

    public long getDef() {
        return def;
    }

    public void setDef(long def) {
        this.def = def;
    }

    @Override
    public Long getValue() {
        return getDef();
    }

    @Override
    public void setValue(long value) {
        setDef(value);
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
