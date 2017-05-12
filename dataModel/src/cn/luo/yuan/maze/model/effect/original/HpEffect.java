package cn.luo.yuan.maze.model.effect.original;

import cn.luo.yuan.maze.model.effect.LongValueEffect;
import cn.luo.yuan.maze.utils.Version;

/**
 * Created by luoyuan on 2017/3/18.
 */
public class HpEffect implements LongValueEffect {
    private static final long serialVersionUID = Version.SERVER_VERSION;
    private long hp;

    public long getHp() {
        return hp;
    }

    public void setHp(long hp) {
        this.hp = hp;
    }

    @Override
    public long getValue() {
        return getHp();
    }

    @Override
    public void setValue(long value) {
        setHp(value);
    }
}
