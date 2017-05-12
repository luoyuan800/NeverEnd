package cn.luo.yuan.maze.model.effect.original;

import cn.luo.yuan.maze.model.effect.LongValueEffect;
import cn.luo.yuan.maze.utils.Version;

/**
 * Created by luoyuan on 2017/3/18.
 */
public class AtkEffect implements LongValueEffect {
    private static final long serialVersionUID = Version.SERVER_VERSION;
    private long atk;

    public long getAtk() {
        return atk;
    }

    public void setAtk(long atk) {
        this.atk = atk;
    }

    @Override
    public long getValue() {
        return getAtk();
    }

    @Override
    public void setValue(long value) {
        setAtk(value);
    }
}
