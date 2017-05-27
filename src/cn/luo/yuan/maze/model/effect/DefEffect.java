package cn.luo.yuan.maze.model.effect;

import cn.luo.yuan.maze.R;
import cn.luo.yuan.maze.utils.Resource;
import cn.luo.yuan.maze.utils.Version;

/**
 * Created by luoyuan on 2017/3/18.
 */
public class DefEffect extends cn.luo.yuan.maze.model.effect.original.DefEffect {
    private static final long serialVersionUID = Version.SERVER_VERSION;
    private long def;

    public long getDef() {
        return def;
    }

    public void setDef(long def) {
        this.def = def;
    }

    public String toString(){
        return Resource.getString(R.string.def_effect) + def;
    }

    @Override
    public void setValue(long value) {
        setDef(value);
    }

    @Override
    public Long getValue() {
        return getDef();
    }
}
