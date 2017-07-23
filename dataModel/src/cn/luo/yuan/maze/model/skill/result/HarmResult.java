package cn.luo.yuan.maze.model.skill.result;

/**
 * Created by gluo on 4/27/2017.
 */
public class HarmResult extends HasMessageResult {
    private long harm;
    private boolean back = false;

    public long getHarm() {
        return harm;
    }

    public void setHarm(long harm) {
        this.harm = harm;
    }

    public boolean isBack() {
        return back;
    }

    public void setBack(boolean back) {
        this.back = back;
    }
}
