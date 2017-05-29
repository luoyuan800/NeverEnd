package cn.luo.yuan.maze.model.skill.result;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by luoyuan on 2017/5/29.
 */
public class SkipThisTurn extends HasMessageResult {
    private boolean skip;

    public boolean isSkip() {
        return skip;
    }

    public void setSkip(boolean skip) {
        this.skip = skip;
    }

}
