package cn.luo.yuan.maze.model.effect;

import cn.luo.yuan.maze.R;
import cn.luo.yuan.maze.utils.Resource;
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
    public String toString(){
        return Resource.getString(R.string.hp_effect) + hp;
    }
}
