package cn.luo.yuan.maze.model.gift;

import cn.luo.yuan.maze.service.InfoControl;

/**
 * Created by luoyuan on 2017/3/30.
 */
public interface GiftHandler {
    void handler(InfoControl control);
    void unHandler(InfoControl control);
}
