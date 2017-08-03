package cn.luo.yuan.maze.listener;

import cn.luo.yuan.maze.model.HarmAble;
import cn.luo.yuan.maze.service.InfoControlInterface;

/**
 * Created by luoyuan on 2017/8/3.
 */
public interface HarmListener extends Listener {
    void har(HarmAble atker, HarmAble harmer, InfoControlInterface context);
}
