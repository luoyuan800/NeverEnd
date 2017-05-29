package cn.luo.yuan.maze.model.skill.result;

/**
 * Created by gluo on 4/27/2017.
 */
public class EndBattleResult extends HasMessageResult {
    private boolean end;

    public boolean isEnd() {
        return end;
    }

    public void setEnd(boolean end) {
        this.end = end;
    }
}
