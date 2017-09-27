package cn.luo.yuan.maze.model;

import cn.luo.yuan.utils.Random;

/**
 * Created by gluo on 4/27/2017.
 */
public interface HarmAble {

    String getId();

    long getAtk();

    long getDef();

    long getHp();

    void setHp(long hp);

    float getElementRate();


    long getMaxHp();

    void setMaxHp(long hp);

    Element getElement();

    Race getRace();

    long getUpperDef();

    long getUpperHp();

    boolean isDodge(Random random);

    boolean isHit(Random random);

    boolean isParry(Random random);

    long getUpperAtk();

    long getCurrentHp();

}
