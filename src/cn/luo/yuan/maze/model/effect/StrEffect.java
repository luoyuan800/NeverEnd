package cn.luo.yuan.maze.model.effect;

import cn.luo.yuan.maze.R;
import cn.luo.yuan.maze.utils.Resource;
import cn.luo.yuan.maze.utils.Field;

/**
 * Created by luoyuan on 2017/3/18.
 */
public class StrEffect extends cn.luo.yuan.maze.model.effect.original.StrEffect {
    private static final long serialVersionUID = Field.SERVER_VERSION;
    private long str;

    public long getStr() {
        return str;
    }

    public void setStr(long str) {
        this.str = str;
    }

    public String toString(){
        return Resource.getString(R.string.str_effect) + str;
    }

    @Override
    public void setValue(long value) {
        setStr(value);
    }

    @Override
    public Long getValue() {
        return getStr();
    }
}
