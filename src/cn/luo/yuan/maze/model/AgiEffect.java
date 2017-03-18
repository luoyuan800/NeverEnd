package cn.luo.yuan.maze.model;

import cn.luo.yuan.maze.R;
import cn.luo.yuan.maze.utils.Resource;

/**
 * Created by luoyuan on 2017/3/18.
 */
public class AgiEffect extends Effect {
    private long agi;

    public long getAgi() {
        return agi;
    }

    public void setAgi(long agi) {
        this.agi = agi;
    }
    public String toString(){
        return Resource.getString(R.string.agi_effect) + agi;
    }
}
