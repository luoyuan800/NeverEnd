package cn.luo.yuan.maze.model;

/**
 * Created by gluo on 4/27/2017.
 */
public interface HarmAble {
    long getAtk();
    long getDef();
    long getHp();
    void setHp(long hp);
    default void setMaxHp(long hp){

    }
    long getMaxHp();

    Element getElement();
}
