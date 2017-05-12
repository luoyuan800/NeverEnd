package cn.luo.yuan.maze.model.gift;

import cn.luo.yuan.maze.service.InfoControlInterface;

/**
 * Created by luoyuan on 2017/3/30.
 */
public class HeroHeart implements GiftHandler {
    @Override
    public void handler(InfoControlInterface control) {
        control.getHero().setAtkGrow(control.getHero().getAtkGrow() * 2);
    }

    @Override
    public void unHandler(InfoControlInterface control) {
        control.getHero().setAtkGrow(control.getHero().getAtkGrow() / 2);
    }
}
