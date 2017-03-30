package cn.luo.yuan.maze.model.gift;

import cn.luo.yuan.maze.service.InfoControl;

/**
 * Created by luoyuan on 2017/3/30.
 */
public class DarkHeard implements GiftHandler {
    @Override
    public void handler(InfoControl control) {
        control.getHero().setDefGrow(control.getHero().getDefGrow() * 2);
    }

    @Override
    public void unHandler(InfoControl control) {
        control.getHero().setDefGrow(control.getHero().getDefGrow() / 2);
    }
}
