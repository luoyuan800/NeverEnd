package cn.luo.yuan.maze.model.effect;

import cn.luo.yuan.maze.R;
import cn.luo.yuan.maze.utils.Resource;
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

    public String toString(){
        return Resource.getString(R.string.atk_effect) + atk;
    }
}
