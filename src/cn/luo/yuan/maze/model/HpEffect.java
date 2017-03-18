package cn.luo.yuan.maze.model;

import cn.luo.yuan.maze.R;
import cn.luo.yuan.maze.utils.Resource;

/**
 * Created by luoyuan on 2017/3/18.
 */
public class HpEffect extends Effect {
    private long hp;

    public long getHp() {
        return hp;
    }

    public void setHp(long hp) {
        this.hp = hp;
    }
    public String toString(){
        return Resource.getString(R.string.hp_effect) + hp;
    }
}
