package cn.luo.yuan.maze.model.gift;

import cn.luo.yuan.maze.service.InfoControlInterface;

/**
 * Created by luoyuan on 2017/3/30.
 */
public class DarkHeard implements GiftHandler {
    @Override
    public void handler(InfoControlInterface control) {
        control.getHero().setDefGrow(control.getHero().getDefGrow() * 2);
    }

    @Override
    public void unHandler(InfoControlInterface control) {
        control.getHero().setDefGrow(control.getHero().getDefGrow() / 2);
    }
}
