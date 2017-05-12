package cn.luo.yuan.maze.model.effect.original;

import cn.luo.yuan.maze.model.effect.LongValueEffect;
import cn.luo.yuan.maze.utils.Version;

/**
 * Created by luoyuan on 2017/3/18.
 */
public class StrEffect implements LongValueEffect {
    private static final long serialVersionUID = Version.SERVER_VERSION;
    private long str;

    public long getStr() {
        return str;
    }

    public void setStr(long str) {
        this.str = str;
    }

    @Override
    public long getValue() {
        return getStr();
    }

    @Override
    public void setValue(long value) {
        setStr(value);
    }
}
