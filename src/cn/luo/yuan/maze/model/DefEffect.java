package cn.luo.yuan.maze.model;

import cn.luo.yuan.maze.R;
import cn.luo.yuan.maze.utils.Resource;

/**
 * Created by luoyuan on 2017/3/18.
 */
public class DefEffect extends Effect{
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
}
