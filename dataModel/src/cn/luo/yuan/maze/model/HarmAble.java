package cn.luo.yuan.maze.model;

import cn.luo.yuan.maze.service.EffectHandler;

/**
 * Created by gluo on 4/27/2017.
 */
public interface HarmAble {
    long getAtk();
    long getDef();
    long getHp();
    void setHp(long hp);

    default long getCurrentHp() {
        return getHp();
    }

    default void setMaxHp(long hp){

    }
    long getMaxHp();

    Element getElement();
}
