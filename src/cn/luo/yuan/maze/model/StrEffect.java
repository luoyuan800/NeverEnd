package cn.luo.yuan.maze.model;

import cn.luo.yuan.maze.R;
import cn.luo.yuan.maze.utils.Resource;

/**
 * Created by luoyuan on 2017/3/18.
 */
public class StrEffect extends Effect {
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
}
