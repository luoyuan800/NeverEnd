package cn.luo.yuan.maze.model.gift;

import cn.luo.yuan.maze.service.InfoControlInterface;

/**
 * Created by luoyuan on 2017/3/30.
 */
public interface GiftHandler {
    void handler(InfoControlInterface control);
    void unHandler(InfoControlInterface control);
}
