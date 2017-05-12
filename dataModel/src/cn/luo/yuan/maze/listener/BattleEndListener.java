package cn.luo.yuan.maze.listener;

import cn.luo.yuan.maze.model.HarmAble;
import cn.luo.yuan.maze.model.Hero;

/**
 * Created by gluo on 5/8/2017.
 */
public interface BattleEndListener extends Listener {
    void end(Hero battler, HarmAble target);
}
