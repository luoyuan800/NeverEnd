package cn.luo.yuan.maze.service;

import cn.luo.yuan.maze.model.NameObject;

/**
 * Created by luoyuan on 2017/7/16.
 */
public interface BattleMessageInterface {
    void hit(NameObject hero);
    void dodge(NameObject hero, NameObject monster);
    void parry(NameObject hero);
    void harm(NameObject atker, NameObject defender, long harm);
}
