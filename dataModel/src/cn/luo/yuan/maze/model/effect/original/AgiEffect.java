package cn.luo.yuan.maze.model.effect.original;

import cn.luo.yuan.maze.model.Data;
import cn.luo.yuan.maze.model.NameObject;
import cn.luo.yuan.maze.model.effect.LongValueEffect;
import cn.luo.yuan.maze.utils.Version;

/**
 * Created by luoyuan on 2017/3/18.
 */
public class AgiEffect implements LongValueEffect, NameObject {
    private static final long serialVersionUID = Version.SERVER_VERSION;
    private long agi;
    private boolean enable = false;

    public long getAgi() {
        return agi;
    }

    public void setAgi(long agi) {
        this.agi = agi;
    }

    @Override
    public long getValue() {
        return getAgi();
    }

    @Override
    public void setValue(long value) {
        setAgi(value);
    }

    @Override
    public boolean isEnable() {
        return enable;
    }

    public void setEnable(boolean enable) {
        this.enable = enable;
    }

    @Override
    public String getName() {
        return getClass().getSimpleName();
    }

    @Override
    public String getDisplayName() {
        return "<font color='" + (isEnable()? Data.ENABLE_COLOR : Data.DISABLE_COLOR) + "'>" + toString() + "</font>";
    }
}
