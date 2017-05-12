package cn.luo.yuan.maze.model.effect.original;

import cn.luo.yuan.maze.model.effect.LongValueEffect;
import cn.luo.yuan.maze.utils.Version;

/**
 * Created by luoyuan on 2017/3/18.
 */
public class DefEffect implements LongValueEffect {
    private static final long serialVersionUID = Version.SERVER_VERSION;
    private long def;

    public long getDef() {
        return def;
    }

    public void setDef(long def) {
        this.def = def;
    }

    @Override
    public long getValue() {
        return getDef();
    }

    @Override
    public void setValue(long value) {
        setDef(value);
    }
}
